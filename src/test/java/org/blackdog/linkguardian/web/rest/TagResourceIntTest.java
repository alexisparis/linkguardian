package org.blackdog.linkguardian.web.rest;

import javax.inject.Inject;
import org.blackdog.linkguardian.LinkguardianApp;

import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.TagRepository;
import org.blackdog.linkguardian.repository.UserRepository;
import org.blackdog.linkguardian.service.TagService;
import org.blackdog.linkguardian.repository.search.TagSearchRepository;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.blackdog.linkguardian.web.rest.errors.ExceptionTranslator;
import org.blackdog.linkguardian.service.dto.TagCriteria;
import org.blackdog.linkguardian.service.TagQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.blackdog.linkguardian.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TagResource REST controller.
 *
 * @see TagResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkguardianApp.class)
public class TagResourceIntTest {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    @Inject
    private TagsNormalizer tagsNormalizer;

    @Inject
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagSearchRepository tagSearchRepository;

    @Autowired
    private TagQueryService tagQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTagMockMvc;

    private Tag tag;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TagResource tagResource = new TagResource(tagRepository, tagsNormalizer, userRepository, tagService, tagQueryService);

        this.restTagMockMvc = MockMvcBuilders.standaloneSetup(tagResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tag createEntity(EntityManager em) {
        Tag tag = new Tag()
            .label(DEFAULT_LABEL);
        return tag;
    }

    @Before
    public void initTest() {
        tagSearchRepository.deleteAll();
        tag = createEntity(em);
    }

    @Test
    @Transactional
    public void createTag() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag
        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tag)))
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate + 1);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getLabel()).isEqualTo(DEFAULT_LABEL);

        // Validate the Tag in Elasticsearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToIgnoringGivenFields(testTag);
    }

    @Test
    @Transactional
    public void createTagWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tagRepository.findAll().size();

        // Create the Tag with an existing ID
        tag.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tag)))
            .andExpect(status().isBadRequest());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = tagRepository.findAll().size();
        // set the field null
        tag.setLabel(null);

        // Create the Tag, which fails.

        restTagMockMvc.perform(post("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tag)))
            .andExpect(status().isBadRequest());

        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTags() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tagList
        restTagMockMvc.perform(get("/api/tags?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void getTag() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tag.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()));
    }

    @Test
    @Transactional
    public void getAllTagsByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tagList where label equals to DEFAULT_LABEL
        defaultTagShouldBeFound("label.equals=" + DEFAULT_LABEL);

        // Get all the tagList where label equals to UPDATED_LABEL
        defaultTagShouldNotBeFound("label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllTagsByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tagList where label in DEFAULT_LABEL or UPDATED_LABEL
        defaultTagShouldBeFound("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL);

        // Get all the tagList where label equals to UPDATED_LABEL
        defaultTagShouldNotBeFound("label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    public void getAllTagsByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        tagRepository.saveAndFlush(tag);

        // Get all the tagList where label is not null
        defaultTagShouldBeFound("label.specified=true");

        // Get all the tagList where label is null
        defaultTagShouldNotBeFound("label.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTagShouldBeFound(String filter) throws Exception {
        restTagMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTagShouldNotBeFound(String filter) throws Exception {
        restTagMockMvc.perform(get("/api/tags?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingTag() throws Exception {
        // Get the tag
        restTagMockMvc.perform(get("/api/tags/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTag() throws Exception {
        // Initialize the database
        tagService.save(tag);

        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Update the tag
        Tag updatedTag = tagRepository.findOne(tag.getId());
        // Disconnect from session so that the updates on updatedTag are not directly saved in db
        em.detach(updatedTag);
        updatedTag
            .label(UPDATED_LABEL);

        restTagMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTag)))
            .andExpect(status().isOk());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate);
        Tag testTag = tagList.get(tagList.size() - 1);
        assertThat(testTag.getLabel()).isEqualTo(UPDATED_LABEL);

        // Validate the Tag in Elasticsearch
        Tag tagEs = tagSearchRepository.findOne(testTag.getId());
        assertThat(tagEs).isEqualToIgnoringGivenFields(testTag);
    }

    @Test
    @Transactional
    public void updateNonExistingTag() throws Exception {
        int databaseSizeBeforeUpdate = tagRepository.findAll().size();

        // Create the Tag

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTagMockMvc.perform(put("/api/tags")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tag)))
            .andExpect(status().isCreated());

        // Validate the Tag in the database
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTag() throws Exception {
        // Initialize the database
        tagService.save(tag);

        int databaseSizeBeforeDelete = tagRepository.findAll().size();

        // Get the tag
        restTagMockMvc.perform(delete("/api/tags/{id}", tag.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tagExistsInEs = tagSearchRepository.exists(tag.getId());
        assertThat(tagExistsInEs).isFalse();

        // Validate the database is empty
        List<Tag> tagList = tagRepository.findAll();
        assertThat(tagList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTag() throws Exception {
        // Initialize the database
        tagService.save(tag);

        // Search the tag
        restTagMockMvc.perform(get("/api/_search/tags?query=id:" + tag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tag.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);
        tag2.setId(2L);
        assertThat(tag1).isNotEqualTo(tag2);
        tag1.setId(null);
        assertThat(tag1).isNotEqualTo(tag2);
    }
}
