package org.blackdog.linkguardian.web.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.BookmarkedUrl;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.service.BookmarkImportService;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.UserService;
import org.blackdog.linkguardian.web.rest.vm.BookmarkBatchResponse;
import org.blackdog.linkguardian.web.util.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing Bookmarks.
 */
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkImportResource {


    private final Logger log = LoggerFactory.getLogger(BookmarkImportResource.class);

    @Inject
    private BookmarkBatchRepository bookmarkBatchRepository;

    @Inject
    private BookmarkImportService bookmarkService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private UserService userService;

    @Value("${service.bookmark.parse.url}")
    private String bookmarkParsingServiceUrl;

    @Autowired
    private SpringSecurityUtils springSecurityUtils;

    private RestTemplate restTemplate;

    @PostConstruct
    public void postBuild() {
        this.restTemplate = new RestTemplate();
    }

    @PostMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookmarkBatchResponse> submitBookmarks(@RequestBody List<BookmarkedUrl> bookmarks) {

        User user = springSecurityUtils.getUser();

        // refuse to process request if too many toxic link submitted
        if (this.userService.isLockedTemporary(user)) {
            return RestUtils.standardTemporaryBlockedResponse();
        }

        List<BookmarkedUrl> bookmarksTmp = bookmarks;

        if (bookmarksTmp != null && !bookmarksTmp.isEmpty()) {
            bookmarksTmp =
                bookmarksTmp.stream().filter(bookmarkedUrl -> bookmarkedUrl.getUrl() != null).collect(Collectors.toList());
        }

        if (bookmarksTmp != null && !bookmarksTmp.isEmpty()) {

            BookmarkBatch batch = new BookmarkBatch();
            batch.setCreationDate(ZonedDateTime.now());
            batch.setStatus(BookmarkBatchStatus.NOT_IMPORTED);
            batch.setStatusDate(batch.getCreationDate());
            batch.setUser(user);

            // insert items
            bookmarksTmp.forEach(bookmarkedUrl -> {
                BookmarkBatchItem item = bookmarkService.convert(bookmarkedUrl);
                if (item != null) {
                    batch.getItems().add(item);
                    item.setBookmarkBatch(batch);
                }
            });

            bookmarkBatchRepository.save(batch);

            return new ResponseEntity<>(BookmarkBatchResponse.of(batch), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(BookmarkBatchResponse.of("invalidValue"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    /**
     * provide a file containing links (either a valid html file or a bookmarks file)
     * @param file
     */
    @PostMapping(value = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookmarkedUrl>> uploadLinksFile(@RequestParam("file") MultipartFile file)
    {
        User user = springSecurityUtils.getUser();

        // refuse to process request if too many toxic link submitted
        if (this.userService.isLockedTemporary(user)) {
            return RestUtils.standardTemporaryBlockedResponse();
        }

        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();

        OutputStream out = null;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("bookmarks", ".html");
            out = new FileOutputStream(tempFile);
            FileCopyUtils.copy(file.getInputStream(), out);
            multipartMap.add("file", new FileSystemResource(tempFile));
        } catch(IOException e) {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("multipart", "form-data"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multipartMap, headers);

        ResponseEntity<List<BookmarkedUrl>> result = null;

        try {
            result = restTemplate.exchange(this.bookmarkParsingServiceUrl, HttpMethod.POST, request,
                new ParameterizedTypeReference<List<BookmarkedUrl>>() {
                });
        } catch(HttpStatusCodeException | ResourceAccessException e) {
            return new ResponseEntity<List<BookmarkedUrl>>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if(result != null) {
            log.info("bookmark parsing service called and returned http status " + result.getStatusCode());
        }

        if (result.getBody() != null) {
            result.getBody().forEach(bookmarkedUrl -> {
                bookmarkService.purgePath(bookmarkedUrl);
            });
        }

        // delete temp file
        if (tempFile != null) {
            tempFile.deleteOnExit();
        }

        return new ResponseEntity<List<BookmarkedUrl>>(result.getBody(), HttpStatus.OK);

        //        List<BookmarkedUrl> results = new ArrayList<>();
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "Montréal, Québec - Tendance météo 14 jours - MétéoMédia",
        //                "http://www.meteomedia.com/tendance-meteo-14-jours/canada/quebec/montreal")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "cameras MTL",
        //                "http://www.quebec511.info/fr/cameras/montreal/")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "cameras QBC",
        //                "http://www.quebec511.info/fr/cameras/quebec/index.aspx")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "Caméras / Montréal",
        //                "http://meteocentre.com/webcam/cam_mtl.html")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "CAD/EUR",
        //                "http://www.xe.com/fr/currencycharts/?from=CAD&to=EUR&view=1M")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "MTL - Conditions des sites hivernaux",
        //                "http://ville.montreal.qc.ca/portal/page?_pageid=5798,94909605&_dad=portal&_schema=PORTAL")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "MTL - Conditions des sites hivernaux",
        //                "http://ville.montreal.qc.ca/portal/page?_pageid=5798,94909605&_dad=portal&_schema=PORTAL")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "NIGHTLIFE.CA",
        //                "http://www.nightlife.ca/")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "MTL Patinoires",
        //                "http://ville.montreal.qc.ca/portal/page?_pageid=5798,94909650&_dad=portal&_schema=PORTAL")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "Patiner Montréal – Patinoires extérieures à Montréal",
        //                "http://patinermontreal.ca/f/paysagee/patin-libre")
        //        );
        //        results.add(
        //            new BookmarkedUrl("//Bookmarks Bar/quebec", "Patiner Montréal – Patinoires extérieures à Montréal",
        //                "http://patinermontreal.ca/f/paysagee/patin-libre")
        //        );
        //
        //        for(int i = 0; i < 100; i++) {
        //            results.add(
        //                new BookmarkedUrl("//Bookmarks Bar/quebec/" + i, "test" + i,
        //                    "http://patinermontreal.ca/f/paysagee/patin-libre" + i)
        //            );
        //        }
        //
        //        results.forEach(bookmarkedUrl -> {
        //            bookmarkService.initializeTags(bookmarkedUrl);
        //        });
        //        return new ResponseEntity<List<BookmarkedUrl>>(results, HttpStatus.OK);
    }
}
