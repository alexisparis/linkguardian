package org.blackdog.linkguardian.service;


import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.domain.*; // for static metamodels
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.repository.search.BookmarkBatchItemSearchRepository;
import org.blackdog.linkguardian.service.dto.BookmarkBatchItemCriteria;

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;

/**
 * Service for executing complex queries for BookmarkBatchItem entities in the database.
 * The main input is a {@link BookmarkBatchItemCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BookmarkBatchItem} or a {@link Page} of {@link BookmarkBatchItem} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookmarkBatchItemQueryService extends QueryService<BookmarkBatchItem> {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchItemQueryService.class);


    private final BookmarkBatchItemRepository bookmarkBatchItemRepository;

    private final BookmarkBatchItemSearchRepository bookmarkBatchItemSearchRepository;

    public BookmarkBatchItemQueryService(BookmarkBatchItemRepository bookmarkBatchItemRepository, BookmarkBatchItemSearchRepository bookmarkBatchItemSearchRepository) {
        this.bookmarkBatchItemRepository = bookmarkBatchItemRepository;
        this.bookmarkBatchItemSearchRepository = bookmarkBatchItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BookmarkBatchItem} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BookmarkBatchItem> findByCriteria(BookmarkBatchItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<BookmarkBatchItem> specification = createSpecification(criteria);
        return bookmarkBatchItemRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link BookmarkBatchItem} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatchItem> findByCriteria(BookmarkBatchItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<BookmarkBatchItem> specification = createSpecification(criteria);
        return bookmarkBatchItemRepository.findAll(specification, page);
    }

    /**
     * Function to convert BookmarkBatchItemCriteria to a {@link Specifications}
     */
    private Specifications<BookmarkBatchItem> createSpecification(BookmarkBatchItemCriteria criteria) {
        Specifications<BookmarkBatchItem> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), BookmarkBatchItem_.id));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), BookmarkBatchItem_.url));
            }
            if (criteria.getTags() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTags(), BookmarkBatchItem_.tags));
            }
            if (criteria.getLink_creation_date() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLink_creation_date(), BookmarkBatchItem_.linkCreationDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), BookmarkBatchItem_.status));
            }
            if (criteria.getError_msg_code() != null) {
                specification = specification.and(buildStringSpecification(criteria.getError_msg_code(), BookmarkBatchItem_.errorMsgCode));
            }
        }
        return specification;
    }

}
