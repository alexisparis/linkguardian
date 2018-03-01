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

import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.*; // for static metamodels
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.repository.search.BookmarkBatchSearchRepository;
import org.blackdog.linkguardian.service.dto.BookmarkBatchCriteria;

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;

/**
 * Service for executing complex queries for BookmarkBatch entities in the database.
 * The main input is a {@link BookmarkBatchCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BookmarkBatch} or a {@link Page} of {@link BookmarkBatch} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookmarkBatchQueryService extends QueryService<BookmarkBatch> {

    private final Logger log = LoggerFactory.getLogger(BookmarkBatchQueryService.class);


    private final BookmarkBatchRepository bookmarkBatchRepository;

    private final BookmarkBatchSearchRepository bookmarkBatchSearchRepository;

    public BookmarkBatchQueryService(BookmarkBatchRepository bookmarkBatchRepository, BookmarkBatchSearchRepository bookmarkBatchSearchRepository) {
        this.bookmarkBatchRepository = bookmarkBatchRepository;
        this.bookmarkBatchSearchRepository = bookmarkBatchSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BookmarkBatch} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BookmarkBatch> findByCriteria(BookmarkBatchCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<BookmarkBatch> specification = createSpecification(criteria);
        return bookmarkBatchRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link BookmarkBatch} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookmarkBatch> findByCriteria(BookmarkBatchCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<BookmarkBatch> specification = createSpecification(criteria);
        return bookmarkBatchRepository.findAll(specification, page);
    }

    /**
     * Function to convert BookmarkBatchCriteria to a {@link Specifications}
     */
    private Specifications<BookmarkBatch> createSpecification(BookmarkBatchCriteria criteria) {
        Specifications<BookmarkBatch> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), BookmarkBatch_.id));
            }
            if (criteria.getCreation_date() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreation_date(), BookmarkBatch_.creationDate));
            }
            if (criteria.getStatus_date() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStatus_date(), BookmarkBatch_.statusDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), BookmarkBatch_.status));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), BookmarkBatch_.user, User_.id));
            }
            if (criteria.getItemsId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getItemsId(), BookmarkBatch_.items, BookmarkBatchItem_.id));
            }
        }
        return specification;
    }

}
