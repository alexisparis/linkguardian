package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.service.ToxicLinkService;
import org.blackdog.linkguardian.web.rest.errors.BadRequestAlertException;
import org.blackdog.linkguardian.web.rest.util.HeaderUtil;
import org.blackdog.linkguardian.web.rest.util.PaginationUtil;
import org.blackdog.linkguardian.service.dto.ToxicLinkCriteria;
import org.blackdog.linkguardian.service.ToxicLinkQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ToxicLink.
 */
@RestController
@RequestMapping("/api")
public class ToxicLinkResource {

    private final Logger log = LoggerFactory.getLogger(ToxicLinkResource.class);

    private static final String ENTITY_NAME = "toxicLink";

    private final ToxicLinkService toxicLinkService;

    private final ToxicLinkQueryService toxicLinkQueryService;

    public ToxicLinkResource(ToxicLinkService toxicLinkService, ToxicLinkQueryService toxicLinkQueryService) {
        this.toxicLinkService = toxicLinkService;
        this.toxicLinkQueryService = toxicLinkQueryService;
    }

    /**
     * POST  /toxic-links : Create a new toxicLink.
     *
     * @param toxicLink the toxicLink to create
     * @return the ResponseEntity with status 201 (Created) and with body the new toxicLink, or with status 400 (Bad Request) if the toxicLink has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/toxic-links")
    @Timed
    public ResponseEntity<ToxicLink> createToxicLink(@Valid @RequestBody ToxicLink toxicLink) throws URISyntaxException {
        log.debug("REST request to save ToxicLink : {}", toxicLink);
        if (toxicLink.getId() != null) {
            throw new BadRequestAlertException("A new toxicLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ToxicLink result = toxicLinkService.save(toxicLink);
        return ResponseEntity.created(new URI("/api/toxic-links/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /toxic-links : Updates an existing toxicLink.
     *
     * @param toxicLink the toxicLink to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated toxicLink,
     * or with status 400 (Bad Request) if the toxicLink is not valid,
     * or with status 500 (Internal Server Error) if the toxicLink couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/toxic-links")
    @Timed
    public ResponseEntity<ToxicLink> updateToxicLink(@Valid @RequestBody ToxicLink toxicLink) throws URISyntaxException {
        log.debug("REST request to update ToxicLink : {}", toxicLink);
        if (toxicLink.getId() == null) {
            return createToxicLink(toxicLink);
        }
        ToxicLink result = toxicLinkService.save(toxicLink);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, toxicLink.getId().toString()))
            .body(result);
    }

    /**
     * GET  /toxic-links : get all the toxicLinks.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of toxicLinks in body
     */
    @GetMapping("/toxic-links")
    @Timed
    public ResponseEntity<List<ToxicLink>> getAllToxicLinks(ToxicLinkCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ToxicLinks by criteria: {}", criteria);
        Page<ToxicLink> page = toxicLinkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/toxic-links");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /toxic-links/:id : get the "id" toxicLink.
     *
     * @param id the id of the toxicLink to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the toxicLink, or with status 404 (Not Found)
     */
    @GetMapping("/toxic-links/{id}")
    @Timed
    public ResponseEntity<ToxicLink> getToxicLink(@PathVariable Long id) {
        log.debug("REST request to get ToxicLink : {}", id);
        ToxicLink toxicLink = toxicLinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(toxicLink));
    }

    /**
     * DELETE  /toxic-links/:id : delete the "id" toxicLink.
     *
     * @param id the id of the toxicLink to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/toxic-links/{id}")
    @Timed
    public ResponseEntity<Void> deleteToxicLink(@PathVariable Long id) {
        log.debug("REST request to delete ToxicLink : {}", id);
        toxicLinkService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/toxic-links?query=:query : search for the toxicLink corresponding
     * to the query.
     *
     * @param query the query of the toxicLink search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/toxic-links")
    @Timed
    public ResponseEntity<List<ToxicLink>> searchToxicLinks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of ToxicLinks for query {}", query);
        Page<ToxicLink> page = toxicLinkService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/toxic-links");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
