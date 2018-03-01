package org.blackdog.linkguardian.service;

import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.TagRepository;
import org.blackdog.linkguardian.repository.search.TagSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Tag.
 */
@Service
@Transactional
public class TagService {

    private final Logger log = LoggerFactory.getLogger(TagService.class);

    private final TagRepository tagRepository;

    private final TagSearchRepository tagSearchRepository;

    public TagService(TagRepository tagRepository, TagSearchRepository tagSearchRepository) {
        this.tagRepository = tagRepository;
        this.tagSearchRepository = tagSearchRepository;
    }

    /**
     * Save a tag.
     *
     * @param tag the entity to save
     * @return the persisted entity
     */
    public Tag save(Tag tag) {
        log.debug("Request to save Tag : {}", tag);
        Tag result = tagRepository.save(tag);
        tagSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the tags.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Tag> findAll(Pageable pageable) {
        log.debug("Request to get all Tags");
        return tagRepository.findAll(pageable);
    }

    /**
     * Get one tag by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Tag findOne(Long id) {
        log.debug("Request to get Tag : {}", id);
        return tagRepository.findOne(id);
    }

    /**
     * Delete the tag by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Tag : {}", id);
        tagRepository.delete(id);
        tagSearchRepository.delete(id);
    }

    /**
     * Search for the tag corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Tag> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Tags for query {}", query);
        Page<Tag> result = tagSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
