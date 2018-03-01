package org.blackdog.linkguardian.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.service.BookmarkBatchItemService;
import org.blackdog.linkguardian.web.rest.errors.BadRequestAlertException;
import org.blackdog.linkguardian.web.rest.util.HeaderUtil;
import org.blackdog.linkguardian.web.rest.util.PaginationUtil;
import org.blackdog.linkguardian.service.dto.BookmarkBatchItemCriteria;
import org.blackdog.linkguardian.service.BookmarkBatchItemQueryService;
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
 * REST controller for managing BookmarkBatchItem.
 */
@RestController
@RequestMapping("/api")
public class BookmarkBatchItemResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchItemResource.class);

    private static final String ENTITY_NAME = "bookmarkBatchItem";

    private final BookmarkBatchItemService bookmarkBatchItemService;

    private final BookmarkBatchItemQueryService bookmarkBatchItemQueryService;

    public BookmarkBatchItemResource(BookmarkBatchItemService bookmarkBatchItemService, BookmarkBatchItemQueryService bookmarkBatchItemQueryService) {
        this.bookmarkBatchItemService = bookmarkBatchItemService;
        this.bookmarkBatchItemQueryService = bookmarkBatchItemQueryService;
    }

    /**
     * POST  /bookmark-batch-items : Create a new bookmarkBatchItem.
     *
     * @param bookmarkBatchItem the bookmarkBatchItem to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookmarkBatchItem, or with status 400 (Bad Request) if the bookmarkBatchItem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bookmark-batch-items")
    @Timed
    public ResponseEntity<BookmarkBatchItem> createBookmarkBatchItem(@Valid @RequestBody BookmarkBatchItem bookmarkBatchItem) throws URISyntaxException {
        log.debug("REST request to save BookmarkBatchItem : {}", bookmarkBatchItem);
        if (bookmarkBatchItem.getId() != null) {
            throw new BadRequestAlertException("A new bookmarkBatchItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookmarkBatchItem result = bookmarkBatchItemService.save(bookmarkBatchItem);
        return ResponseEntity.created(new URI("/api/bookmark-batch-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmark-batch-items : Updates an existing bookmarkBatchItem.
     *
     * @param bookmarkBatchItem the bookmarkBatchItem to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmarkBatchItem,
     * or with status 400 (Bad Request) if the bookmarkBatchItem is not valid,
     * or with status 500 (Internal Server Error) if the bookmarkBatchItem couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bookmark-batch-items")
    @Timed
    public ResponseEntity<BookmarkBatchItem> updateBookmarkBatchItem(@Valid @RequestBody BookmarkBatchItem bookmarkBatchItem) throws URISyntaxException {
        log.debug("REST request to update BookmarkBatchItem : {}", bookmarkBatchItem);
        if (bookmarkBatchItem.getId() == null) {
            return createBookmarkBatchItem(bookmarkBatchItem);
        }
        BookmarkBatchItem result = bookmarkBatchItemService.save(bookmarkBatchItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookmarkBatchItem.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookmark-batch-items : get all the bookmarkBatchItems.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarkBatchItems in body
     */
    @GetMapping("/bookmark-batch-items")
    @Timed
    public ResponseEntity<List<BookmarkBatchItem>> getAllBookmarkBatchItems(BookmarkBatchItemCriteria criteria, Pageable pageable) {
        log.debug("REST request to get BookmarkBatchItems by criteria: {}", criteria);
        Page<BookmarkBatchItem> page = bookmarkBatchItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookmark-batch-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bookmark-batch-items/:id : get the "id" bookmarkBatchItem.
     *
     * @param id the id of the bookmarkBatchItem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookmarkBatchItem, or with status 404 (Not Found)
     */
    @GetMapping("/bookmark-batch-items/{id}")
    @Timed
    public ResponseEntity<BookmarkBatchItem> getBookmarkBatchItem(@PathVariable Long id) {
        log.debug("REST request to get BookmarkBatchItem : {}", id);
        BookmarkBatchItem bookmarkBatchItem = bookmarkBatchItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(bookmarkBatchItem));
    }

    /**
     * DELETE  /bookmark-batch-items/:id : delete the "id" bookmarkBatchItem.
     *
     * @param id the id of the bookmarkBatchItem to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bookmark-batch-items/{id}")
    @Timed
    public ResponseEntity<Void> deleteBookmarkBatchItem(@PathVariable Long id) {
        log.debug("REST request to delete BookmarkBatchItem : {}", id);
        bookmarkBatchItemService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/bookmark-batch-items?query=:query : search for the bookmarkBatchItem corresponding
     * to the query.
     *
     * @param query the query of the bookmarkBatchItem search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bookmark-batch-items")
    @Timed
    public ResponseEntity<List<BookmarkBatchItem>> searchBookmarkBatchItems(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of BookmarkBatchItems for query {}", query);
        Page<BookmarkBatchItem> page = bookmarkBatchItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bookmark-batch-items");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
