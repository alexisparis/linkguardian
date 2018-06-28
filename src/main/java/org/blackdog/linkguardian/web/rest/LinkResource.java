package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.api.client.repackaged.com.google.common.base.Objects;
import io.github.jhipster.web.util.ResponseUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.validation.Valid;
import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.domain.transfer.CountPerTag;
import org.blackdog.linkguardian.domain.transfer.ReadStatus;
import org.blackdog.linkguardian.domain.transfer.SortDirection;
import org.blackdog.linkguardian.domain.transfer.SortType;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.repository.exception.TooMuchTagException;
import org.blackdog.linkguardian.service.LinkBuilder;
import org.blackdog.linkguardian.service.LinkQueryService;
import org.blackdog.linkguardian.service.LinkResponse;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.service.LinkTarget;
import org.blackdog.linkguardian.service.UserService;
import org.blackdog.linkguardian.service.dto.LinkCriteria;
import org.blackdog.linkguardian.service.exception.TagTooLongException;
import org.blackdog.linkguardian.service.template.LinkTargetProcessorTemplateMethod;
import org.blackdog.linkguardian.service.template.impl.HttpLinkTargetProcessorTemplateMethod;
import org.blackdog.linkguardian.service.template.impl.ManualLinkProcessorTemplateMethod;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.blackdog.linkguardian.web.rest.errors.BadRequestAlertException;
import org.blackdog.linkguardian.web.rest.util.HeaderUtil;
import org.blackdog.linkguardian.web.rest.util.PaginationUtil;
import org.blackdog.linkguardian.web.util.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Link.
 */
@RestController
@RequestMapping("/api")
public class LinkResource {

    private final Logger log = LoggerFactory.getLogger(LinkResource.class);

    private static final String ENTITY_NAME = "link";

//    @Inject
    private LinkRepository linkRepository;

//    @Autowired
    private LinkService linkService;

//    @Inject
    private LinkBuilder linkBuilder;

//    @Inject
    private UserService userService;

//    @Inject
    private TagsNormalizer tagsNormalizer;

//    @Autowired
    private SpringSecurityUtils springSecurityUtils;

//    @Inject
    private LinkQueryService linkQueryService;

    public LinkResource(LinkRepository linkRepository, LinkService linkService,
        LinkBuilder linkBuilder, UserService userService, TagsNormalizer tagsNormalizer,
        SpringSecurityUtils springSecurityUtils, LinkQueryService linkQueryService) {
        this.linkRepository = linkRepository;
        this.linkService = linkService;
        this.linkBuilder = linkBuilder;
        this.userService = userService;
        this.tagsNormalizer = tagsNormalizer;
        this.springSecurityUtils = springSecurityUtils;
        this.linkQueryService = linkQueryService;
    }

    /**
     * POST  /links : Create a new link.
     *
     * @param link the link to create
     * @return the ResponseEntity with status 201 (Created) and with body the new link, or with status 400 (Bad Request) if the link has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/links")
    @Timed
    public ResponseEntity<Link> createLink(@Valid @RequestBody Link link) throws URISyntaxException {
        log.debug("REST request to save Link : {}", link);
        if (link.getId() != null) {
            throw new BadRequestAlertException("A new link cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Link result = linkService.save(link);
        return ResponseEntity.created(new URI("/api/links/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /links : Updates an existing link.
     *
     * @param link the link to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated link,
     * or with status 400 (Bad Request) if the link is not valid,
     * or with status 500 (Internal Server Error) if the link couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/links")
    @Timed
    public ResponseEntity<Link> updateLink(@Valid @RequestBody Link link) throws URISyntaxException {
        log.debug("REST request to update Link : {}", link);
        if (link.getId() == null) {
            return createLink(link);
        }
        Link result = linkService.save(link);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, link.getId().toString()))
            .body(result);
    }

    /**
     * GET  /links : get all the links.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of links in body
     */
    @GetMapping("/links")
    @Timed
    public ResponseEntity<List<Link>> getAllLinks(LinkCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Links by criteria: {}", criteria);
        Page<Link> page = linkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/links");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /links/:id : get the "id" link.
     *
     * @param id the id of the link to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the link, or with status 404 (Not Found)
     */
    @GetMapping("/links/{id}")
    @Timed
    public ResponseEntity<Link> getLink(@PathVariable Long id) {
        log.debug("REST request to get Link : {}", id);
        Link link = linkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(link));
    }

    /**
     * DELETE  /links/:id : delete the "id" link.
     *
     * @param id the id of the link to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/links/{id}")
    @Timed
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        log.debug("REST request to delete Link : {}", id);
        linkService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/links?query=:query : search for the link corresponding
     * to the query.
     *
     * @param query the query of the link search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/links")
    @Timed
    public ResponseEntity<List<Link>> searchLinks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Links for query {}", query);
        Page<Link> page = linkService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/links");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @ExceptionHandler(TooMuchTagException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAppException(TooMuchTagException ex) {
        log.error("handle " + TooMuchTagException.class, ex);
        return ex.getMessage();
    }

    @RequestMapping(value = "/my_links",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed
    public ResponseEntity<?> getMyLinks(/*@PathVariable String tag, */
        String token,
        String read_status,
        String sortBy,
        String sortType,
        Pageable pageable)
        throws URISyntaxException, TooMuchTagException {

        log.info("CALL getMyLinks(tag:" + token + ", read_status:" + read_status + ", sortBy:" + sortBy + ", sortDirection:" + sortType + ")");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        log.debug("REST request to get my links with " + name);

        Page<Link> page = linkRepository.customFindLinksOfUser(name, token,
            ReadStatus.valueOf(read_status),
            SortType.valueOf(sortBy),
            SortDirection.valueOf(sortType),
            pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/my_links");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * add a new link
     * @return
     */
    @RequestMapping(value = "/my_links",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> addUrl(String newurl, String tag)
    {
        log.info("calling addUrl with url : " + newurl + " tags : " + tag);
        User user = springSecurityUtils.getUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // refuse to process request if too many toxic link submitted
        if (this.userService.isLockedTemporary(user)) {
            return RestUtils.standardTemporaryBlockedResponse();
        }

        if ( newurl == null || newurl.trim().length() == 0 )
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else
        {
            LinkTarget target = this.linkService.determineTarget(newurl);

            Set<String> tags = this.tagsNormalizer.split(tag, " ", true);
            return new HttpLinkTargetProcessorTemplateMethod(this.linkService).
                process(
                    LinkTargetProcessorTemplateMethod.CallContext.newInstance(target, user, newurl, tags));
        }
    }

    /**
     * add a new link manually
     * @return
     */
    @RequestMapping(value = "/my_links/manual",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> manuallyAddUrl(String newurl, String description, String tag)
    {
        log.info("calling manuallyAddUrl with url : " + newurl + " tags : " + tag + " description : " + description);
        User user = springSecurityUtils.getUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // refuse to process request if too many toxic link submitted
        if (this.userService.isLockedTemporary(user)) {
            return RestUtils.standardTemporaryBlockedResponse();
        }

        if ( newurl == null || newurl.trim().length() == 0 )
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else
        {
            return this.linkService.manuallyAddUrl(user, newurl, description, tag);
        }
    }

    /**
     * delete a link
     * @param id
     * @return
     */
    @RequestMapping(value = "/my_links",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> delete(Long id) {

        log.info("calling delete link with id : " + id);

        Link link = this.linkRepository.findOne(id);

        if (link != null)
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                this.linkRepository.delete(link);
                return new ResponseEntity<LinkResponse>(LinkResponse.empty(), HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<LinkResponse>(LinkResponse.of("forbidden"), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<LinkResponse>(LinkResponse.of("not_found"), HttpStatus.NOT_FOUND);
        }
    }


    String formatUrl(String url)
    {
        String result = url;
        if ( result != null )
        {
            if( url.length() > 50 )
            {
                result = url.substring(0, 50) + "...";
            }
        }

        return result;
    }

    /**
     * add a new tag to an existing link
     * @param id
     * @param tag
     * @return
     */
    @RequestMapping(value = "/my_links/tag",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> addTag(Long id, String tag)
    {
        log.info("calling addTag for link " + id + " with tag " + tag);

        Link link = this.linkRepository.findOne(id);
        if (link != null && tag != null)
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                String normalizedTag = this.tagsNormalizer.normalize(tag);

                log.info("tag to add " + tag + " become normalized as " + normalizedTag);

                if ( normalizedTag == null || normalizedTag.isEmpty() )
                {
                    return new ResponseEntity<>(LinkResponse.of("invalidTag", link), HttpStatus.BAD_REQUEST);
                }
                else
                {
                    // remove from the set tags that already exist
                    Optional<Tag> firstTag = link.getTags().stream()
                        .filter(tag1 -> Objects.equal(normalizedTag, tag1.getLabel())).findFirst();
                    log.info("existing tags for this link : " + link.getTags());
                    if (firstTag.isPresent()) {
                        return new ResponseEntity<>(LinkResponse.of("tagsExist", link), HttpStatus.BAD_REQUEST);
                    } else {
                        try {
                            this.linkBuilder.addTags(link, normalizedTag);
                        } catch (TagTooLongException e) {
                            return new ResponseEntity<>(LinkResponse.of("tagTooLong", link), HttpStatus.BAD_REQUEST);
                        }

                        this.linkRepository.saveAndFlush(link);

                        return new ResponseEntity<>(LinkResponse.of("tagAdded", link), HttpStatus.OK);
                    }
                }
            }
            else
            {
                return new ResponseEntity<>(LinkResponse.of("forbidden"), HttpStatus.FORBIDDEN);
            }
        }
        else
        {
            return new ResponseEntity<>(LinkResponse.of("error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete a tag from a link
     * @param id
     * @param tag
     * @return
     */
    @RequestMapping(value = "/my_links/tag",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> deleteTag(Long id, String tag)
    {
        log.info("calling deleteTag for link " + id + " for tag " + tag);

        Link link = this.linkRepository.findOne(id);
        if (link != null && tag != null)
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                Optional<Tag> tagToDelete = link.getTags().stream().filter(new Predicate<Tag>() {
                    @Override
                    public boolean test(Tag _tag) {
                        return tag.equals(_tag.getLabel());
                    }
                }).findFirst();

                if ( tagToDelete.isPresent() ) {
                    link.getTags().remove(tagToDelete.get());
                    this.linkRepository.saveAndFlush(link);
                    return new ResponseEntity<>(LinkResponse.of(link), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(LinkResponse.of("tagDoesNotExist", link), HttpStatus.BAD_REQUEST);
                }
            }
            else
            {
                return new ResponseEntity<>(LinkResponse.of("forbidden"), HttpStatus.FORBIDDEN);
            }
        } else {
            if ( link == null ) {
                return new ResponseEntity<>(LinkResponse.of("tagDoesNotExist"), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * update the note empty the link
     * @param id the id empty the link
     * @param score
     * @return
     */
    @RequestMapping(value = "/my_links/note",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> updateNote(Long id, Integer score)
    {
        log.info("calling update note to " + score);

        Link link = this.linkRepository.findOne(id);

        if (link == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                try
                {   Integer _score = score;
                    if (_score == null)
                    {
                        _score = 0;
                    }

                    _score = Math.max(0, Math.min(_score, 5));

                    link.setNote(_score);
                    this.linkRepository.saveAndFlush(link);
                    return new ResponseEntity<>(LinkResponse.of(link), HttpStatus.OK);
                }
                catch (Exception e)
                {
                    log.error("error while trying to update the note", e);
                    return new ResponseEntity<>(LinkResponse.of(link), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else
            {
                return new ResponseEntity<>(LinkResponse.of("forbidden"), HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * change the read attribute empty a link
     * @param id
     * @param value
     */
    public ResponseEntity<LinkResponse> changeReadAttribute(Long id, boolean value)
    {
        log.info("calling changeReadAttribute for link : " + id + " read : " +value);

        Link link = this.linkRepository.findOne(id);

        if ( link == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                link.setRead(value);
                this.linkRepository.saveAndFlush(link);
                return new ResponseEntity<>(LinkResponse.of("success", link), HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>(LinkResponse.of("forbidden"), HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * mark a link as read
     * @param id
     */
    @RequestMapping(value = "/my_links/read",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> markAsRead(Long id)
    {
        return changeReadAttribute(id, true);
    }

    /**
     * mark a link as unread
     * @param id
     */
    @RequestMapping(value = "/my_links/unread",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LinkResponse> markAsUnread(Long id)
    {
        return changeReadAttribute(id, false);
    }

    public ResponseEntity<LinkResponse> importLink(Long id)
    {
        log.info("calling importLink with id : " + id);

        Link link = this.linkRepository.findOne(id);
        if ( link != null )
        {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String name = auth.getName(); //get logged in username
            if ( link.getUser().getLogin().equals(name) )
            {
                return new ResponseEntity<>(LinkResponse.of(link, "linkExistsForConnectedUser"), HttpStatus.BAD_REQUEST);
            }
            else
            {
                Link linksByUserAndUrl =
                    this.linkRepository.findLinksByUserAndUrl(name, link.getOriginalUrl());
                if ( linksByUserAndUrl != null )
                {
                    return new ResponseEntity<>(
                        LinkResponse.of(linksByUserAndUrl, "linkExistsForConnectedUser"), HttpStatus.BAD_REQUEST);
                }
                else
                {
                    // clone link
                    Link result = linkBuilder.clone(link);
                    result.setCreationDate(ZonedDateTime.now());
                    result.setLocked(Boolean.FALSE);
                    result.setRead(Boolean.FALSE);
                    result.setUser(this.userService.getUserWithLogin(name));

                    this.linkRepository.saveAndFlush(result);

                    return new ResponseEntity<>(LinkResponse.of("linkImported", result), HttpStatus.OK);
                }
            }
        }
        else
        {
            return new ResponseEntity<>(LinkResponse.of("linkDoesNotExist"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * get count of links per tags
     * @param id
     */
    @RequestMapping(value = "/my_links/count_per_tags",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CountPerTag>> countPerTags(Long id)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        return new ResponseEntity<List<CountPerTag>>(this.linkRepository.getCountPerTags(name), HttpStatus.OK);
    }

}
