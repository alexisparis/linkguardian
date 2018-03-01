package org.blackdog.linkguardian.service;

import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.repository.search.BookmarkBatchItemSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing BookmarkBatchItem.
 */
@Service
@Transactional
public class BookmarkBatchItemService {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchItemService.class);

    private final BookmarkBatchItemRepository bookmarkBatchItemRepository;

    private final BookmarkBatchItemSearchRepository bookmarkBatchItemSearchRepository;

    public BookmarkBatchItemService(BookmarkBatchItemRepository bookmarkBatchItemRepository, BookmarkBatchItemSearchRepository bookmarkBatchItemSearchRepository) {
        this.bookmarkBatchItemRepository = bookmarkBatchItemRepository;
        this.bookmarkBatchItemSearchRepository = bookmarkBatchItemSearchRepository;
    }

    /**
     * Save a bookmarkBatchItem.
     *
     * @param bookmarkBatchItem the entity to save
     * @return the persisted entity
     */
    public BookmarkBatchItem save(BookmarkBatchItem bookmarkBatchItem) {
        log.debug("Request to save BookmarkBatchItem : {}", bookmarkBatchItem);
        BookmarkBatchItem result = bookmarkBatchItemRepository.save(bookmarkBatchItem);
        bookmarkBatchItemSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the bookmarkBatchItems.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatchItem> findAll(Pageable pageable) {
        log.debug("Request to get all BookmarkBatchItems");
        return bookmarkBatchItemRepository.findAll(pageable);
    }

    /**
     * Get one bookmarkBatchItem by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public BookmarkBatchItem findOne(Long id) {
        log.debug("Request to get BookmarkBatchItem : {}", id);
        return bookmarkBatchItemRepository.findOne(id);
    }

    /**
     * Delete the bookmarkBatchItem by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete BookmarkBatchItem : {}", id);
        bookmarkBatchItemRepository.delete(id);
        bookmarkBatchItemSearchRepository.delete(id);
    }

    /**
     * Search for the bookmarkBatchItem corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatchItem> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BookmarkBatchItems for query {}", query);
        Page<BookmarkBatchItem> result = bookmarkBatchItemSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
