package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.validation.Valid;
import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.TagRepository;
import org.blackdog.linkguardian.repository.UserRepository;
import org.blackdog.linkguardian.service.TagQueryService;
import org.blackdog.linkguardian.service.TagService;
import org.blackdog.linkguardian.service.dto.TagCriteria;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.blackdog.linkguardian.web.rest.errors.BadRequestAlertException;
import org.blackdog.linkguardian.web.rest.util.HeaderUtil;
import org.blackdog.linkguardian.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Tag.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private static final String ENTITY_NAME = "tag";

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

//    @Inject
    private TagRepository tagRepository;

//    @Inject
    private TagsNormalizer tagsNormalizer;

//    @Inject
    private UserRepository userRepository;

//    @Inject
    private TagService tagService;

//    @Inject
    private TagQueryService tagQueryService;

    public TagResource(TagRepository tagRepository, TagsNormalizer tagsNormalizer,
        UserRepository userRepository, TagService tagService, TagQueryService tagQueryService) {
        this.tagRepository = tagRepository;
        this.tagsNormalizer = tagsNormalizer;
        this.userRepository = userRepository;
        this.tagService = tagService;
        this.tagQueryService = tagQueryService;
    }

    /**
     * POST  /tags : Create a new tag.
     *
     * @param tag the tag to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tag, or with status 400 (Bad Request) if the tag has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tags")
    @Timed
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to save Tag : {}", tag);
        if (tag.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tag result = tagService.save(tag);
        return ResponseEntity.created(new URI("/api/tags/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tags : Updates an existing tag.
     *
     * @param tag the tag to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tag,
     * or with status 400 (Bad Request) if the tag is not valid,
     * or with status 500 (Internal Server Error) if the tag couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tags")
    @Timed
    public ResponseEntity<Tag> updateTag(@Valid @RequestBody Tag tag) throws URISyntaxException {
        log.debug("REST request to update Tag : {}", tag);
        if (tag.getId() == null) {
            return createTag(tag);
        }
        Tag result = tagService.save(tag);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tag.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tags : get all the tags.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tags in body
     */
    @GetMapping("/tags")
    @Timed
    public ResponseEntity<List<Tag>> getAllTags(TagCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tags by criteria: {}", criteria);
        Page<Tag> page = tagQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tags");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tags/:id : get the "id" tag.
     *
     * @param id the id of the tag to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tag, or with status 404 (Not Found)
     */
    @GetMapping("/tags/{id}")
    @Timed
    public ResponseEntity<Tag> getTag(@PathVariable Long id) {
        log.debug("REST request to get Tag : {}", id);
        Tag tag = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tag));
    }

    /**
     * DELETE  /tags/:id : delete the "id" tag.
     *
     * @param id the id of the tag to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tags/{id}")
    @Timed
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        log.debug("REST request to delete Tag : {}", id);
        tagService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tags?query=:query : search for the tag corresponding
     * to the query.
     *
     * @param query the query of the tag search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/tags")
    @Timed
    public ResponseEntity<List<Tag>> searchTags(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Tags for query {}", query);
        Page<Tag> page = tagService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tags");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     */
    @RequestMapping(value = "/search/tags",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<String> tags(String filter) {
        log.info("calling tags with " + filter);

        Set<String> normalized = this.tagsNormalizer.split(filter, " ", true);

        String filtered = null;

        if ( normalized != null && ! normalized.isEmpty() ) {
            filtered = normalized.iterator().next();
        }

        log.info("calling getTags with " + filtered);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username

        List<String> results = tagRepository.getTags(userRepository.findOneByLogin(name).get().getId(), filtered);
        results.add(0, "");

        return results;
    }

}
