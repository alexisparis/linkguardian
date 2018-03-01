package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.service.BookmarkBatchService;
import org.blackdog.linkguardian.web.rest.errors.BadRequestAlertException;
import org.blackdog.linkguardian.web.rest.util.HeaderUtil;
import org.blackdog.linkguardian.web.rest.util.PaginationUtil;
import org.blackdog.linkguardian.service.dto.BookmarkBatchCriteria;
import org.blackdog.linkguardian.service.BookmarkBatchQueryService;
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
 * REST controller for managing BookmarkBatch.
 */
@RestController
@RequestMapping("/api")
public class BookmarkBatchResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchResource.class);

    private static final String ENTITY_NAME = "bookmarkBatch";

    private final BookmarkBatchService bookmarkBatchService;

    private final BookmarkBatchQueryService bookmarkBatchQueryService;

    public BookmarkBatchResource(BookmarkBatchService bookmarkBatchService, BookmarkBatchQueryService bookmarkBatchQueryService) {
        this.bookmarkBatchService = bookmarkBatchService;
        this.bookmarkBatchQueryService = bookmarkBatchQueryService;
    }

    /**
     * POST  /bookmark-batches : Create a new bookmarkBatch.
     *
     * @param bookmarkBatch the bookmarkBatch to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookmarkBatch, or with status 400 (Bad Request) if the bookmarkBatch has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bookmark-batches")
    @Timed
    public ResponseEntity<BookmarkBatch> createBookmarkBatch(@Valid @RequestBody BookmarkBatch bookmarkBatch) throws URISyntaxException {
        log.debug("REST request to save BookmarkBatch : {}", bookmarkBatch);
        if (bookmarkBatch.getId() != null) {
            throw new BadRequestAlertException("A new bookmarkBatch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookmarkBatch result = bookmarkBatchService.save(bookmarkBatch);
        return ResponseEntity.created(new URI("/api/bookmark-batches/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmark-batches : Updates an existing bookmarkBatch.
     *
     * @param bookmarkBatch the bookmarkBatch to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmarkBatch,
     * or with status 400 (Bad Request) if the bookmarkBatch is not valid,
     * or with status 500 (Internal Server Error) if the bookmarkBatch couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bookmark-batches")
    @Timed
    public ResponseEntity<BookmarkBatch> updateBookmarkBatch(@Valid @RequestBody BookmarkBatch bookmarkBatch) throws URISyntaxException {
        log.debug("REST request to update BookmarkBatch : {}", bookmarkBatch);
        if (bookmarkBatch.getId() == null) {
            return createBookmarkBatch(bookmarkBatch);
        }
        BookmarkBatch result = bookmarkBatchService.save(bookmarkBatch);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookmarkBatch.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookmark-batches : get all the bookmarkBatches.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarkBatches in body
     */
    @GetMapping("/bookmark-batches")
    @Timed
    public ResponseEntity<List<BookmarkBatch>> getAllBookmarkBatches(BookmarkBatchCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BookmarkBatches by criteria: {}", criteria);
        Page<BookmarkBatch> page = bookmarkBatchQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookmark-batches");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bookmark-batches/:id : get the "id" bookmarkBatch.
     *
     * @param id the id of the bookmarkBatch to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookmarkBatch, or with status 404 (Not Found)
     */
    @GetMapping("/bookmark-batches/{id}")
    @Timed
    public ResponseEntity<BookmarkBatch> getBookmarkBatch(@PathVariable Long id) {
        log.debug("REST request to get BookmarkBatch : {}", id);
        BookmarkBatch bookmarkBatch = bookmarkBatchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(bookmarkBatch));
    }

    /**
     * DELETE  /bookmark-batches/:id : delete the "id" bookmarkBatch.
     *
     * @param id the id of the bookmarkBatch to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bookmark-batches/{id}")
    @Timed
    public ResponseEntity<Void> deleteBookmarkBatch(@PathVariable Long id) {
        log.debug("REST request to delete BookmarkBatch : {}", id);
        bookmarkBatchService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/bookmark-batches?query=:query : search for the bookmarkBatch corresponding
     * to the query.
     *
     * @param query the query of the bookmarkBatch search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bookmark-batches")
    @Timed
    public ResponseEntity<List<BookmarkBatch>> searchBookmarkBatches(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of BookmarkBatches for query {}", query);
        Page<BookmarkBatch> page = bookmarkBatchService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bookmark-batches");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
