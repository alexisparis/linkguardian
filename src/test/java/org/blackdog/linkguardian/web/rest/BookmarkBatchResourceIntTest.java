package org.blackdog.linkguardian.web.rest;

import org.blackdog.linkguardian.LinkguardianApp;

import org.blackdog.linkguardian.domain.BookmarkBatch;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.repository.BookmarkBatchRepository;
import org.blackdog.linkguardian.service.BookmarkBatchService;
import org.blackdog.linkguardian.repository.search.BookmarkBatchSearchRepository;
import org.blackdog.linkguardian.web.rest.errors.ExceptionTranslator;
import org.blackdog.linkguardian.service.BookmarkBatchQueryService;

import org.junit.Before;
import org.junit.Ignore;
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

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchStatus;
/**
 * Test class for the BookmarkBatchResource REST controller.
 *
 * @see BookmarkBatchResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkguardianApp.class)
@Ignore
public class BookmarkBatchResourceIntTest {

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_STATUS_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_STATUS_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BookmarkBatchStatus DEFAULT_STATUS = BookmarkBatchStatus.NOT_IMPORTED;
    private static final BookmarkBatchStatus UPDATED_STATUS = BookmarkBatchStatus.IMPORTED;

    @Autowired
    private BookmarkBatchRepository bookmarkBatchRepository;

    @Autowired
    private BookmarkBatchService bookmarkBatchService;

    @Autowired
    private BookmarkBatchSearchRepository bookmarkBatchSearchRepository;

    @Autowired
    private BookmarkBatchQueryService bookmarkBatchQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBookmarkBatchMockMvc;

    private BookmarkBatch bookmarkBatch;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookmarkBatchResource bookmarkBatchResource = new BookmarkBatchResource(bookmarkBatchService, bookmarkBatchQueryService);
        this.restBookmarkBatchMockMvc = MockMvcBuilders.standaloneSetup(bookmarkBatchResource)
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
    public static BookmarkBatch createEntity(EntityManager em) {
        BookmarkBatch bookmarkBatch = new BookmarkBatch()
            .creation_date(DEFAULT_CREATION_DATE)
            .status_date(DEFAULT_STATUS_DATE)
            .status(DEFAULT_STATUS);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        bookmarkBatch.setUser(user);
        return bookmarkBatch;
    }

    @Before
    public void initTest() {
        bookmarkBatchSearchRepository.deleteAll();
        bookmarkBatch = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmarkBatch() throws Exception {
        int databaseSizeBeforeCreate = bookmarkBatchRepository.findAll().size();

        // Create the BookmarkBatch
        restBookmarkBatchMockMvc.perform(post("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatch)))
            .andExpect(status().isCreated());

        // Validate the BookmarkBatch in the database
        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeCreate + 1);
        BookmarkBatch testBookmarkBatch = bookmarkBatchList.get(bookmarkBatchList.size() - 1);
        assertThat(testBookmarkBatch.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testBookmarkBatch.getStatusDate()).isEqualTo(DEFAULT_STATUS_DATE);
        assertThat(testBookmarkBatch.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the BookmarkBatch in Elasticsearch
        BookmarkBatch bookmarkBatchEs = bookmarkBatchSearchRepository.findOne(testBookmarkBatch.getId());
        assertThat(testBookmarkBatch.getCreationDate()).isEqualTo(testBookmarkBatch.getCreationDate());
        assertThat(testBookmarkBatch.getStatusDate()).isEqualTo(testBookmarkBatch.getStatusDate());
        assertThat(bookmarkBatchEs).isEqualToIgnoringGivenFields(testBookmarkBatch, "creation_date", "status_date");
    }

    @Test
    @Transactional
    public void createBookmarkBatchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookmarkBatchRepository.findAll().size();

        // Create the BookmarkBatch with an existing ID
        bookmarkBatch.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookmarkBatchMockMvc.perform(post("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatch)))
            .andExpect(status().isBadRequest());

        // Validate the BookmarkBatch in the database
        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreation_dateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookmarkBatchRepository.findAll().size();
        // set the field null
        bookmarkBatch.setCreationDate(null);

        // Create the BookmarkBatch, which fails.

        restBookmarkBatchMockMvc.perform(post("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatch)))
            .andExpect(status().isBadRequest());

        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookmarkBatchRepository.findAll().size();
        // set the field null
        bookmarkBatch.setStatus(null);

        // Create the BookmarkBatch, which fails.

        restBookmarkBatchMockMvc.perform(post("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatch)))
            .andExpect(status().isBadRequest());

        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatches() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList
        restBookmarkBatchMockMvc.perform(get("/api/bookmark-batches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status_date").value(hasItem(sameInstant(DEFAULT_STATUS_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getBookmarkBatch() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get the bookmarkBatch
        restBookmarkBatchMockMvc.perform(get("/api/bookmark-batches/{id}", bookmarkBatch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bookmarkBatch.getId().intValue()))
            .andExpect(jsonPath("$.creation_date").value(sameInstant(DEFAULT_CREATION_DATE)))
            .andExpect(jsonPath("$.status_date").value(sameInstant(DEFAULT_STATUS_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByCreation_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where creation_date equals to DEFAULT_CREATION_DATE
        defaultBookmarkBatchShouldBeFound("creation_date.equals=" + DEFAULT_CREATION_DATE);

        // Get all the bookmarkBatchList where creation_date equals to UPDATED_CREATION_DATE
        defaultBookmarkBatchShouldNotBeFound("creation_date.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByCreation_dateIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where creation_date in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultBookmarkBatchShouldBeFound("creation_date.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the bookmarkBatchList where creation_date equals to UPDATED_CREATION_DATE
        defaultBookmarkBatchShouldNotBeFound("creation_date.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByCreation_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where creation_date is not null
        defaultBookmarkBatchShouldBeFound("creation_date.specified=true");

        // Get all the bookmarkBatchList where creation_date is null
        defaultBookmarkBatchShouldNotBeFound("creation_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByCreation_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where creation_date greater than or equals to DEFAULT_CREATION_DATE
        defaultBookmarkBatchShouldBeFound("creation_date.greaterOrEqualThan=" + DEFAULT_CREATION_DATE);

        // Get all the bookmarkBatchList where creation_date greater than or equals to UPDATED_CREATION_DATE
        defaultBookmarkBatchShouldNotBeFound("creation_date.greaterOrEqualThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByCreation_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where creation_date less than or equals to DEFAULT_CREATION_DATE
        defaultBookmarkBatchShouldNotBeFound("creation_date.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the bookmarkBatchList where creation_date less than or equals to UPDATED_CREATION_DATE
        defaultBookmarkBatchShouldBeFound("creation_date.lessThan=" + UPDATED_CREATION_DATE);
    }


    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatus_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status_date equals to DEFAULT_STATUS_DATE
        defaultBookmarkBatchShouldBeFound("status_date.equals=" + DEFAULT_STATUS_DATE);

        // Get all the bookmarkBatchList where status_date equals to UPDATED_STATUS_DATE
        defaultBookmarkBatchShouldNotBeFound("status_date.equals=" + UPDATED_STATUS_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatus_dateIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status_date in DEFAULT_STATUS_DATE or UPDATED_STATUS_DATE
        defaultBookmarkBatchShouldBeFound("status_date.in=" + DEFAULT_STATUS_DATE + "," + UPDATED_STATUS_DATE);

        // Get all the bookmarkBatchList where status_date equals to UPDATED_STATUS_DATE
        defaultBookmarkBatchShouldNotBeFound("status_date.in=" + UPDATED_STATUS_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatus_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status_date is not null
        defaultBookmarkBatchShouldBeFound("status_date.specified=true");

        // Get all the bookmarkBatchList where status_date is null
        defaultBookmarkBatchShouldNotBeFound("status_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatus_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status_date greater than or equals to DEFAULT_STATUS_DATE
        defaultBookmarkBatchShouldBeFound("status_date.greaterOrEqualThan=" + DEFAULT_STATUS_DATE);

        // Get all the bookmarkBatchList where status_date greater than or equals to UPDATED_STATUS_DATE
        defaultBookmarkBatchShouldNotBeFound("status_date.greaterOrEqualThan=" + UPDATED_STATUS_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatus_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status_date less than or equals to DEFAULT_STATUS_DATE
        defaultBookmarkBatchShouldNotBeFound("status_date.lessThan=" + DEFAULT_STATUS_DATE);

        // Get all the bookmarkBatchList where status_date less than or equals to UPDATED_STATUS_DATE
        defaultBookmarkBatchShouldBeFound("status_date.lessThan=" + UPDATED_STATUS_DATE);
    }


    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status equals to DEFAULT_STATUS
        defaultBookmarkBatchShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the bookmarkBatchList where status equals to UPDATED_STATUS
        defaultBookmarkBatchShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBookmarkBatchShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the bookmarkBatchList where status equals to UPDATED_STATUS
        defaultBookmarkBatchShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);

        // Get all the bookmarkBatchList where status is not null
        defaultBookmarkBatchShouldBeFound("status.specified=true");

        // Get all the bookmarkBatchList where status is null
        defaultBookmarkBatchShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        bookmarkBatch.setUser(user);
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);
        Long userId = user.getId();

        // Get all the bookmarkBatchList where user equals to userId
        defaultBookmarkBatchShouldBeFound("userId.equals=" + userId);

        // Get all the bookmarkBatchList where user equals to userId + 1
        defaultBookmarkBatchShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllBookmarkBatchesByItemsIsEqualToSomething() throws Exception {
        // Initialize the database
        BookmarkBatchItem items = BookmarkBatchItemResourceIntTest.createEntity(em);
        em.persist(items);
        em.flush();
        bookmarkBatch.addItems(items);
        bookmarkBatchRepository.saveAndFlush(bookmarkBatch);
        Long itemsId = items.getId();

        // Get all the bookmarkBatchList where items equals to itemsId
        defaultBookmarkBatchShouldBeFound("itemsId.equals=" + itemsId);

        // Get all the bookmarkBatchList where items equals to itemsId + 1
        defaultBookmarkBatchShouldNotBeFound("itemsId.equals=" + (itemsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBookmarkBatchShouldBeFound(String filter) throws Exception {
        restBookmarkBatchMockMvc.perform(get("/api/bookmark-batches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status_date").value(hasItem(sameInstant(DEFAULT_STATUS_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBookmarkBatchShouldNotBeFound(String filter) throws Exception {
        restBookmarkBatchMockMvc.perform(get("/api/bookmark-batches?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingBookmarkBatch() throws Exception {
        // Get the bookmarkBatch
        restBookmarkBatchMockMvc.perform(get("/api/bookmark-batches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBookmarkBatch() throws Exception {
        // Initialize the database
        bookmarkBatchService.save(bookmarkBatch);

        int databaseSizeBeforeUpdate = bookmarkBatchRepository.findAll().size();

        // Update the bookmarkBatch
        BookmarkBatch updatedBookmarkBatch = bookmarkBatchRepository.findOne(bookmarkBatch.getId());
        // Disconnect from session so that the updates on updatedBookmarkBatch are not directly saved in db
        em.detach(updatedBookmarkBatch);
        updatedBookmarkBatch
            .creation_date(UPDATED_CREATION_DATE)
            .status_date(UPDATED_STATUS_DATE)
            .status(UPDATED_STATUS);

        restBookmarkBatchMockMvc.perform(put("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBookmarkBatch)))
            .andExpect(status().isOk());

        // Validate the BookmarkBatch in the database
        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeUpdate);
        BookmarkBatch testBookmarkBatch = bookmarkBatchList.get(bookmarkBatchList.size() - 1);
        assertThat(testBookmarkBatch.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testBookmarkBatch.getStatusDate()).isEqualTo(UPDATED_STATUS_DATE);
        assertThat(testBookmarkBatch.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the BookmarkBatch in Elasticsearch
        BookmarkBatch bookmarkBatchEs = bookmarkBatchSearchRepository.findOne(testBookmarkBatch.getId());
        assertThat(testBookmarkBatch.getCreationDate()).isEqualTo(testBookmarkBatch.getCreationDate());
        assertThat(testBookmarkBatch.getStatusDate()).isEqualTo(testBookmarkBatch.getStatusDate());
        assertThat(bookmarkBatchEs).isEqualToIgnoringGivenFields(testBookmarkBatch, "creation_date", "status_date");
    }

    @Test
    @Transactional
    public void updateNonExistingBookmarkBatch() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkBatchRepository.findAll().size();

        // Create the BookmarkBatch

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBookmarkBatchMockMvc.perform(put("/api/bookmark-batches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatch)))
            .andExpect(status().isCreated());

        // Validate the BookmarkBatch in the database
        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBookmarkBatch() throws Exception {
        // Initialize the database
        bookmarkBatchService.save(bookmarkBatch);

        int databaseSizeBeforeDelete = bookmarkBatchRepository.findAll().size();

        // Get the bookmarkBatch
        restBookmarkBatchMockMvc.perform(delete("/api/bookmark-batches/{id}", bookmarkBatch.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean bookmarkBatchExistsInEs = bookmarkBatchSearchRepository.exists(bookmarkBatch.getId());
        assertThat(bookmarkBatchExistsInEs).isFalse();

        // Validate the database is empty
        List<BookmarkBatch> bookmarkBatchList = bookmarkBatchRepository.findAll();
        assertThat(bookmarkBatchList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBookmarkBatch() throws Exception {
        // Initialize the database
        bookmarkBatchService.save(bookmarkBatch);

        // Search the bookmarkBatch
        restBookmarkBatchMockMvc.perform(get("/api/_search/bookmark-batches?query=id:" + bookmarkBatch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatch.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status_date").value(hasItem(sameInstant(DEFAULT_STATUS_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookmarkBatch.class);
        BookmarkBatch bookmarkBatch1 = new BookmarkBatch();
        bookmarkBatch1.setId(1L);
        BookmarkBatch bookmarkBatch2 = new BookmarkBatch();
        bookmarkBatch2.setId(bookmarkBatch1.getId());
        assertThat(bookmarkBatch1).isEqualTo(bookmarkBatch2);
        bookmarkBatch2.setId(2L);
        assertThat(bookmarkBatch1).isNotEqualTo(bookmarkBatch2);
        bookmarkBatch1.setId(null);
        assertThat(bookmarkBatch1).isNotEqualTo(bookmarkBatch2);
    }
}
