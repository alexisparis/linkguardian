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

import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.domain.*; // for static metamodels
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.repository.search.ToxicLinkSearchRepository;
import org.blackdog.linkguardian.service.dto.ToxicLinkCriteria;


/**
 * Service for executing complex queries for ToxicLink entities in the database.
 * The main input is a {@link ToxicLinkCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ToxicLink} or a {@link Page} of {@link ToxicLink} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ToxicLinkQueryService extends QueryService<ToxicLink> {

    private final Logger log = LoggerFactory.getLogger(ToxicLinkQueryService.class);


    private final ToxicLinkRepository toxicLinkRepository;

    private final ToxicLinkSearchRepository toxicLinkSearchRepository;

    public ToxicLinkQueryService(ToxicLinkRepository toxicLinkRepository, ToxicLinkSearchRepository toxicLinkSearchRepository) {
        this.toxicLinkRepository = toxicLinkRepository;
        this.toxicLinkSearchRepository = toxicLinkSearchRepository;
    }

    /**
     * Return a {@link List} of {@link ToxicLink} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ToxicLink> findByCriteria(ToxicLinkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<ToxicLink> specification = createSpecification(criteria);
        return toxicLinkRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link ToxicLink} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ToxicLink> findByCriteria(ToxicLinkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<ToxicLink> specification = createSpecification(criteria);
        return toxicLinkRepository.findAll(specification, page);
    }

    /**
     * Function to convert ToxicLinkCriteria to a {@link Specifications}
     */
    private Specifications<ToxicLink> createSpecification(ToxicLinkCriteria criteria) {
        Specifications<ToxicLink> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), ToxicLink_.id));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), ToxicLink_.email));
            }
            if (criteria.getCreation_date() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreation_date(), ToxicLink_.creationDate));
            }
            if (criteria.getError() != null) {
                specification = specification.and(buildStringSpecification(criteria.getError(), ToxicLink_.error));
            }
        }
        return specification;
    }

}
