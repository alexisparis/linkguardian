package org.blackdog.linkguardian.service;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Set;
import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.transfer.BookmarkedUrl;
import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.repository.search.BookmarkBatchSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing BookmarkBatch.
 */
@Service
@Transactional
public class BookmarkBatchService {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchService.class);

    private final BookmarkBatchRepository bookmarkBatchRepository;

    private final BookmarkBatchSearchRepository bookmarkBatchSearchRepository;

    public BookmarkBatchService(BookmarkBatchRepository bookmarkBatchRepository, BookmarkBatchSearchRepository bookmarkBatchSearchRepository) {
        this.bookmarkBatchRepository = bookmarkBatchRepository;
        this.bookmarkBatchSearchRepository = bookmarkBatchSearchRepository;
    }

    /**
     * Save a bookmarkBatch.
     *
     * @param bookmarkBatch the entity to save
     * @return the persisted entity
     */
    public BookmarkBatch save(BookmarkBatch bookmarkBatch) {
        log.debug("Request to save BookmarkBatch : {}", bookmarkBatch);
        BookmarkBatch result = bookmarkBatchRepository.save(bookmarkBatch);
        bookmarkBatchSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the bookmarkBatches.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatch> findAll(Pageable pageable) {
        log.debug("Request to get all BookmarkBatches");
        return bookmarkBatchRepository.findAll(pageable);
    }

    /**
     * Get one bookmarkBatch by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public BookmarkBatch findOne(Long id) {
        log.debug("Request to get BookmarkBatch : {}", id);
        return bookmarkBatchRepository.findOne(id);
    }

    /**
     * Delete the bookmarkBatch by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete BookmarkBatch : {}", id);
        bookmarkBatchRepository.delete(id);
        bookmarkBatchSearchRepository.delete(id);
    }

    /**
     * Search for the bookmarkBatch corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatch> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BookmarkBatches for query {}", query);
        Page<BookmarkBatch> result = bookmarkBatchSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }

    public void purgePath(BookmarkedUrl bookmark) {
        if (bookmark != null) {
            String[] path = bookmark.getPath();
            if (path != null) {
                Set<String> pathSet = new HashSet<>();
                for(int i = 0; i < path.length; i++) {
                    pathSet.add(path[i]);
                }

                pathSet.remove("Bookmarks Bar");

                bookmark.setPath(pathSet.toArray(new String[pathSet.size()]));
            }
        }
    }

    /**
     * convert a {@link BookmarkedUrl} into a {@link BookmarkBatchItem}
     * @param url a {@link BookmarkedUrl}
     * @return a {@link BookmarkBatchItem}
     */
    public BookmarkBatchItem convert(BookmarkedUrl url) {
        BookmarkBatchItem item = null;

        if (url != null) {
            item = new BookmarkBatchItem();

            item.setUrl(url.getUrl());
            item.setStatus(BookmarkBatchItemStatus.NOT_IMPORTED);
            String[] tags = url.getPath();
            if (tags != null && tags.length > 0) {

                StringBuilder builder = new StringBuilder();

                for(int i = 0; i < tags.length; i++) {
                    String currentTag = tags[i];
                    if (currentTag != null) {
                        currentTag = currentTag.trim();
                    }
                    currentTag = Strings.emptyToNull(currentTag);
                    if (currentTag != null) {
                        if (builder.length() > 0) {
                            builder.append(",");
                        }
                        builder.append(currentTag);
                    }
                }

                item.setTags(builder.toString());
            }
        }

        return item;
    }
}
