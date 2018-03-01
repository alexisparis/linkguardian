package org.blackdog.linkguardian.web.rest;

import org.blackdog.linkguardian.LinkguardianApp;

import org.blackdog.linkguardian.domain.ToxicLink;
import org.blackdog.linkguardian.repository.ToxicLinkRepository;
import org.blackdog.linkguardian.service.ToxicLinkService;
import org.blackdog.linkguardian.repository.search.ToxicLinkSearchRepository;
import org.blackdog.linkguardian.web.rest.errors.ExceptionTranslator;
import org.blackdog.linkguardian.service.ToxicLinkQueryService;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static org.blackdog.linkguardian.web.rest.TestUtil.sameInstant;
import static org.blackdog.linkguardian.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ToxicLinkResource REST controller.
 *
 * @see ToxicLinkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkguardianApp.class)
public class ToxicLinkResourceIntTest {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_ERROR = "AAAAAAAAAA";
    private static final String UPDATED_ERROR = "BBBBBBBBBB";

    @Autowired
    private ToxicLinkRepository toxicLinkRepository;

    @Autowired
    private ToxicLinkService toxicLinkService;

    @Autowired
    private ToxicLinkSearchRepository toxicLinkSearchRepository;

    @Autowired
    private ToxicLinkQueryService toxicLinkQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restToxicLinkMockMvc;

    private ToxicLink toxicLink;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ToxicLinkResource toxicLinkResource = new ToxicLinkResource(toxicLinkService, toxicLinkQueryService);
        this.restToxicLinkMockMvc = MockMvcBuilders.standaloneSetup(toxicLinkResource)
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
    public static ToxicLink createEntity(EntityManager em) {
        ToxicLink toxicLink = new ToxicLink()
            .email(DEFAULT_EMAIL)
            .url(DEFAULT_URL)
            .creation_date(DEFAULT_CREATION_DATE)
            .error(DEFAULT_ERROR);
        return toxicLink;
    }

    @Before
    public void initTest() {
        toxicLinkSearchRepository.deleteAll();
        toxicLink = createEntity(em);
    }

    @Test
    @Transactional
    public void createToxicLink() throws Exception {
        int databaseSizeBeforeCreate = toxicLinkRepository.findAll().size();

        // Create the ToxicLink
        restToxicLinkMockMvc.perform(post("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isCreated());

        // Validate the ToxicLink in the database
        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeCreate + 1);
        ToxicLink testToxicLink = toxicLinkList.get(toxicLinkList.size() - 1);
        assertThat(testToxicLink.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testToxicLink.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testToxicLink.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testToxicLink.getError()).isEqualTo(DEFAULT_ERROR);

        // Validate the ToxicLink in Elasticsearch
        ToxicLink toxicLinkEs = toxicLinkSearchRepository.findOne(testToxicLink.getId());
        assertThat(testToxicLink.getCreationDate()).isEqualTo(testToxicLink.getCreationDate());
        assertThat(toxicLinkEs).isEqualToIgnoringGivenFields(testToxicLink, "creation_date");
    }

    @Test
    @Transactional
    public void createToxicLinkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toxicLinkRepository.findAll().size();

        // Create the ToxicLink with an existing ID
        toxicLink.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToxicLinkMockMvc.perform(post("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isBadRequest());

        // Validate the ToxicLink in the database
        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = toxicLinkRepository.findAll().size();
        // set the field null
        toxicLink.setEmail(null);

        // Create the ToxicLink, which fails.

        restToxicLinkMockMvc.perform(post("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isBadRequest());

        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = toxicLinkRepository.findAll().size();
        // set the field null
        toxicLink.setUrl(null);

        // Create the ToxicLink, which fails.

        restToxicLinkMockMvc.perform(post("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isBadRequest());

        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreation_dateIsRequired() throws Exception {
        int databaseSizeBeforeTest = toxicLinkRepository.findAll().size();
        // set the field null
        toxicLink.setCreationDate(null);

        // Create the ToxicLink, which fails.

        restToxicLinkMockMvc.perform(post("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isBadRequest());

        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllToxicLinks() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList
        restToxicLinkMockMvc.perform(get("/api/toxic-links?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toxicLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].error").value(hasItem(DEFAULT_ERROR.toString())));
    }

    @Test
    @Transactional
    public void getToxicLink() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get the toxicLink
        restToxicLinkMockMvc.perform(get("/api/toxic-links/{id}", toxicLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(toxicLink.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.creation_date").value(sameInstant(DEFAULT_CREATION_DATE)))
            .andExpect(jsonPath("$.error").value(DEFAULT_ERROR.toString()));
    }

    @Test
    @Transactional
    public void getAllToxicLinksByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where email equals to DEFAULT_EMAIL
        defaultToxicLinkShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the toxicLinkList where email equals to UPDATED_EMAIL
        defaultToxicLinkShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultToxicLinkShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the toxicLinkList where email equals to UPDATED_EMAIL
        defaultToxicLinkShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where email is not null
        defaultToxicLinkShouldBeFound("email.specified=true");

        // Get all the toxicLinkList where email is null
        defaultToxicLinkShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    public void getAllToxicLinksByCreation_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where creation_date equals to DEFAULT_CREATION_DATE
        defaultToxicLinkShouldBeFound("creation_date.equals=" + DEFAULT_CREATION_DATE);

        // Get all the toxicLinkList where creation_date equals to UPDATED_CREATION_DATE
        defaultToxicLinkShouldNotBeFound("creation_date.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByCreation_dateIsInShouldWork() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where creation_date in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultToxicLinkShouldBeFound("creation_date.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the toxicLinkList where creation_date equals to UPDATED_CREATION_DATE
        defaultToxicLinkShouldNotBeFound("creation_date.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByCreation_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where creation_date is not null
        defaultToxicLinkShouldBeFound("creation_date.specified=true");

        // Get all the toxicLinkList where creation_date is null
        defaultToxicLinkShouldNotBeFound("creation_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllToxicLinksByCreation_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where creation_date greater than or equals to DEFAULT_CREATION_DATE
        defaultToxicLinkShouldBeFound("creation_date.greaterOrEqualThan=" + DEFAULT_CREATION_DATE);

        // Get all the toxicLinkList where creation_date greater than or equals to UPDATED_CREATION_DATE
        defaultToxicLinkShouldNotBeFound("creation_date.greaterOrEqualThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByCreation_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where creation_date less than or equals to DEFAULT_CREATION_DATE
        defaultToxicLinkShouldNotBeFound("creation_date.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the toxicLinkList where creation_date less than or equals to UPDATED_CREATION_DATE
        defaultToxicLinkShouldBeFound("creation_date.lessThan=" + UPDATED_CREATION_DATE);
    }


    @Test
    @Transactional
    public void getAllToxicLinksByErrorIsEqualToSomething() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where error equals to DEFAULT_ERROR
        defaultToxicLinkShouldBeFound("error.equals=" + DEFAULT_ERROR);

        // Get all the toxicLinkList where error equals to UPDATED_ERROR
        defaultToxicLinkShouldNotBeFound("error.equals=" + UPDATED_ERROR);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByErrorIsInShouldWork() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where error in DEFAULT_ERROR or UPDATED_ERROR
        defaultToxicLinkShouldBeFound("error.in=" + DEFAULT_ERROR + "," + UPDATED_ERROR);

        // Get all the toxicLinkList where error equals to UPDATED_ERROR
        defaultToxicLinkShouldNotBeFound("error.in=" + UPDATED_ERROR);
    }

    @Test
    @Transactional
    public void getAllToxicLinksByErrorIsNullOrNotNull() throws Exception {
        // Initialize the database
        toxicLinkRepository.saveAndFlush(toxicLink);

        // Get all the toxicLinkList where error is not null
        defaultToxicLinkShouldBeFound("error.specified=true");

        // Get all the toxicLinkList where error is null
        defaultToxicLinkShouldNotBeFound("error.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultToxicLinkShouldBeFound(String filter) throws Exception {
        restToxicLinkMockMvc.perform(get("/api/toxic-links?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toxicLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].error").value(hasItem(DEFAULT_ERROR.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultToxicLinkShouldNotBeFound(String filter) throws Exception {
        restToxicLinkMockMvc.perform(get("/api/toxic-links?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingToxicLink() throws Exception {
        // Get the toxicLink
        restToxicLinkMockMvc.perform(get("/api/toxic-links/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToxicLink() throws Exception {
        // Initialize the database
        toxicLinkService.save(toxicLink);

        int databaseSizeBeforeUpdate = toxicLinkRepository.findAll().size();

        // Update the toxicLink
        ToxicLink updatedToxicLink = toxicLinkRepository.findOne(toxicLink.getId());
        // Disconnect from session so that the updates on updatedToxicLink are not directly saved in db
        em.detach(updatedToxicLink);
        updatedToxicLink
            .email(UPDATED_EMAIL)
            .url(UPDATED_URL)
            .creation_date(UPDATED_CREATION_DATE)
            .error(UPDATED_ERROR);

        restToxicLinkMockMvc.perform(put("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedToxicLink)))
            .andExpect(status().isOk());

        // Validate the ToxicLink in the database
        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeUpdate);
        ToxicLink testToxicLink = toxicLinkList.get(toxicLinkList.size() - 1);
        assertThat(testToxicLink.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testToxicLink.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testToxicLink.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testToxicLink.getError()).isEqualTo(UPDATED_ERROR);

        // Validate the ToxicLink in Elasticsearch
        ToxicLink toxicLinkEs = toxicLinkSearchRepository.findOne(testToxicLink.getId());
        assertThat(testToxicLink.getCreationDate()).isEqualTo(testToxicLink.getCreationDate());
        assertThat(toxicLinkEs).isEqualToIgnoringGivenFields(testToxicLink, "creation_date");
    }

    @Test
    @Transactional
    public void updateNonExistingToxicLink() throws Exception {
        int databaseSizeBeforeUpdate = toxicLinkRepository.findAll().size();

        // Create the ToxicLink

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restToxicLinkMockMvc.perform(put("/api/toxic-links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toxicLink)))
            .andExpect(status().isCreated());

        // Validate the ToxicLink in the database
        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteToxicLink() throws Exception {
        // Initialize the database
        toxicLinkService.save(toxicLink);

        int databaseSizeBeforeDelete = toxicLinkRepository.findAll().size();

        // Get the toxicLink
        restToxicLinkMockMvc.perform(delete("/api/toxic-links/{id}", toxicLink.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean toxicLinkExistsInEs = toxicLinkSearchRepository.exists(toxicLink.getId());
        assertThat(toxicLinkExistsInEs).isFalse();

        // Validate the database is empty
        List<ToxicLink> toxicLinkList = toxicLinkRepository.findAll();
        assertThat(toxicLinkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchToxicLink() throws Exception {
        // Initialize the database
        toxicLinkService.save(toxicLink);

        // Search the toxicLink
        restToxicLinkMockMvc.perform(get("/api/_search/toxic-links?query=id:" + toxicLink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toxicLink.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].error").value(hasItem(DEFAULT_ERROR.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToxicLink.class);
        ToxicLink toxicLink1 = new ToxicLink();
        toxicLink1.setId(1L);
        ToxicLink toxicLink2 = new ToxicLink();
        toxicLink2.setId(toxicLink1.getId());
        assertThat(toxicLink1).isEqualTo(toxicLink2);
        toxicLink2.setId(2L);
        assertThat(toxicLink1).isNotEqualTo(toxicLink2);
        toxicLink1.setId(null);
        assertThat(toxicLink1).isNotEqualTo(toxicLink2);
    }
}
