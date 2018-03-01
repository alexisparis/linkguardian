package org.blackdog.linkguardian.service;

import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.repository.search.ToxicLinkSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ToxicLink.
 */
@Service
@Transactional
public class ToxicLinkService {

    private final Logger log = LoggerFactory.getLogger(ToxicLinkService.class);

    private final ToxicLinkRepository toxicLinkRepository;

    private final ToxicLinkSearchRepository toxicLinkSearchRepository;

    public ToxicLinkService(ToxicLinkRepository toxicLinkRepository, ToxicLinkSearchRepository toxicLinkSearchRepository) {
        this.toxicLinkRepository = toxicLinkRepository;
        this.toxicLinkSearchRepository = toxicLinkSearchRepository;
    }

    /**
     * Save a toxicLink.
     *
     * @param toxicLink the entity to save
     * @return the persisted entity
     */
    public ToxicLink save(ToxicLink toxicLink) {
        log.debug("Request to save ToxicLink : {}", toxicLink);
        ToxicLink result = toxicLinkRepository.save(toxicLink);
        toxicLinkSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the toxicLinks.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ToxicLink> findAll(Pageable pageable) {
        log.debug("Request to get all ToxicLinks");
        return toxicLinkRepository.findAll(pageable);
    }

    /**
     * Get one toxicLink by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ToxicLink findOne(Long id) {
        log.debug("Request to get ToxicLink : {}", id);
        return toxicLinkRepository.findOne(id);
    }

    /**
     * Delete the toxicLink by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ToxicLink : {}", id);
        toxicLinkRepository.delete(id);
        toxicLinkSearchRepository.delete(id);
    }

    /**
     * Search for the toxicLink corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ToxicLink> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ToxicLinks for query {}", query);
        Page<ToxicLink> result = toxicLinkSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
