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

import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.*; // for static metamodels
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.repository.search.LinkSearchRepository;
import org.blackdog.linkguardian.service.dto.LinkCriteria;


/**
 * Service for executing complex queries for Link entities in the database.
 * The main input is a {@link LinkCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Link} or a {@link Page} of {@link Link} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LinkQueryService extends QueryService<Link> {

    private final Logger log = LoggerFactory.getLogger(LinkQueryService.class);


    private final LinkRepository linkRepository;

    private final LinkSearchRepository linkSearchRepository;

    public LinkQueryService(LinkRepository linkRepository, LinkSearchRepository linkSearchRepository) {
        this.linkRepository = linkRepository;
        this.linkSearchRepository = linkSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Link} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Link> findByCriteria(LinkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Link> specification = createSpecification(criteria);
        return linkRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Link} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Link> findByCriteria(LinkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Link> specification = createSpecification(criteria);
        return linkRepository.findAll(specification, page);
    }

    /**
     * Function to convert LinkCriteria to a {@link Specifications}
     */
    private Specifications<Link> createSpecification(LinkCriteria criteria) {
        Specifications<Link> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Link_.id));
            }
            if (criteria.getCreation_date() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreation_date(), Link_.creationDate));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Link_.description));
            }
            if (criteria.getDomain() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDomain(), Link_.domain));
            }
            if (criteria.getLocked() != null) {
                specification = specification.and(buildSpecification(criteria.getLocked(), Link_.locked));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNote(), Link_.note));
            }
            if (criteria.getRead() != null) {
                specification = specification.and(buildSpecification(criteria.getRead(), Link_.read));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Link_.title));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Link_.url));
            }
            if (criteria.getOriginal_url() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOriginal_url(), Link_.originalUrl));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), Link_.user, User_.id));
            }
            if (criteria.getTagsId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getTagsId(), Link_.tags, Tag_.id));
            }
        }
        return specification;
    }

}
