package org.blackdog.linkguardian.web.rest;

import org.blackdog.linkguardian.LinkguardianApp;

import org.blackdog.linkguardian.domain.BookmarkBatchItem;
import org.blackdog.linkguardian.repository.BookmarkBatchItemRepository;
import org.blackdog.linkguardian.service.BookmarkBatchItemService;
import org.blackdog.linkguardian.repository.search.BookmarkBatchItemSearchRepository;
import org.blackdog.linkguardian.web.rest.errors.ExceptionTranslator;
import org.blackdog.linkguardian.service.BookmarkBatchItemQueryService;

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

import org.blackdog.linkguardian.domain.enumeration.BookmarkBatchItemStatus;
/**
 * Test class for the BookmarkBatchItemResource REST controller.
 *
 * @see BookmarkBatchItemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkguardianApp.class)
@Ignore
public class BookmarkBatchItemResourceIntTest {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LINK_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LINK_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BookmarkBatchItemStatus DEFAULT_STATUS = BookmarkBatchItemStatus.CREATED;
    private static final BookmarkBatchItemStatus UPDATED_STATUS = BookmarkBatchItemStatus.NOT_IMPORTED;

    private static final String DEFAULT_ERROR_MSG_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MSG_CODE = "BBBBBBBBBB";

    @Autowired
    private BookmarkBatchItemRepository bookmarkBatchItemRepository;

    @Autowired
    private BookmarkBatchItemService bookmarkBatchItemService;

    @Autowired
    private BookmarkBatchItemSearchRepository bookmarkBatchItemSearchRepository;

    @Autowired
    private BookmarkBatchItemQueryService bookmarkBatchItemQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBookmarkBatchItemMockMvc;

    private BookmarkBatchItem bookmarkBatchItem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BookmarkBatchItemResource bookmarkBatchItemResource = new BookmarkBatchItemResource(bookmarkBatchItemService, bookmarkBatchItemQueryService);
        this.restBookmarkBatchItemMockMvc = MockMvcBuilders.standaloneSetup(bookmarkBatchItemResource)
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
    public static BookmarkBatchItem createEntity(EntityManager em) {
        BookmarkBatchItem bookmarkBatchItem = new BookmarkBatchItem()
            .url(DEFAULT_URL)
            .tags(DEFAULT_TAGS)
            .link_creation_date(DEFAULT_LINK_CREATION_DATE)
            .status(DEFAULT_STATUS)
            .error_msg_code(DEFAULT_ERROR_MSG_CODE);
        return bookmarkBatchItem;
    }

    @Before
    public void initTest() {
        bookmarkBatchItemSearchRepository.deleteAll();
        bookmarkBatchItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createBookmarkBatchItem() throws Exception {
        int databaseSizeBeforeCreate = bookmarkBatchItemRepository.findAll().size();

        // Create the BookmarkBatchItem
        restBookmarkBatchItemMockMvc.perform(post("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatchItem)))
            .andExpect(status().isCreated());

        // Validate the BookmarkBatchItem in the database
        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeCreate + 1);
        BookmarkBatchItem testBookmarkBatchItem = bookmarkBatchItemList.get(bookmarkBatchItemList.size() - 1);
        assertThat(testBookmarkBatchItem.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testBookmarkBatchItem.getTags()).isEqualTo(DEFAULT_TAGS);
        assertThat(testBookmarkBatchItem.getLinkCreationDate()).isEqualTo(DEFAULT_LINK_CREATION_DATE);
        assertThat(testBookmarkBatchItem.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBookmarkBatchItem.getErrorMsgCode()).isEqualTo(DEFAULT_ERROR_MSG_CODE);

        // Validate the BookmarkBatchItem in Elasticsearch
        BookmarkBatchItem bookmarkBatchItemEs = bookmarkBatchItemSearchRepository.findOne(testBookmarkBatchItem.getId());
        assertThat(testBookmarkBatchItem.getLinkCreationDate()).isEqualTo(testBookmarkBatchItem.getLinkCreationDate());
        assertThat(bookmarkBatchItemEs).isEqualToIgnoringGivenFields(testBookmarkBatchItem, "link_creation_date");
    }

    @Test
    @Transactional
    public void createBookmarkBatchItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bookmarkBatchItemRepository.findAll().size();

        // Create the BookmarkBatchItem with an existing ID
        bookmarkBatchItem.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookmarkBatchItemMockMvc.perform(post("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatchItem)))
            .andExpect(status().isBadRequest());

        // Validate the BookmarkBatchItem in the database
        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookmarkBatchItemRepository.findAll().size();
        // set the field null
        bookmarkBatchItem.setUrl(null);

        // Create the BookmarkBatchItem, which fails.

        restBookmarkBatchItemMockMvc.perform(post("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatchItem)))
            .andExpect(status().isBadRequest());

        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookmarkBatchItemRepository.findAll().size();
        // set the field null
        bookmarkBatchItem.setStatus(null);

        // Create the BookmarkBatchItem, which fails.

        restBookmarkBatchItemMockMvc.perform(post("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatchItem)))
            .andExpect(status().isBadRequest());

        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItems() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList
        restBookmarkBatchItemMockMvc.perform(get("/api/bookmark-batch-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatchItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
            .andExpect(jsonPath("$.[*].link_creation_date").value(hasItem(sameInstant(DEFAULT_LINK_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].error_msg_code").value(hasItem(DEFAULT_ERROR_MSG_CODE.toString())));
    }

    @Test
    @Transactional
    public void getBookmarkBatchItem() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get the bookmarkBatchItem
        restBookmarkBatchItemMockMvc.perform(get("/api/bookmark-batch-items/{id}", bookmarkBatchItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bookmarkBatchItem.getId().intValue()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS.toString()))
            .andExpect(jsonPath("$.link_creation_date").value(sameInstant(DEFAULT_LINK_CREATION_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.error_msg_code").value(DEFAULT_ERROR_MSG_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where url equals to DEFAULT_URL
        defaultBookmarkBatchItemShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the bookmarkBatchItemList where url equals to UPDATED_URL
        defaultBookmarkBatchItemShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where url in DEFAULT_URL or UPDATED_URL
        defaultBookmarkBatchItemShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the bookmarkBatchItemList where url equals to UPDATED_URL
        defaultBookmarkBatchItemShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where url is not null
        defaultBookmarkBatchItemShouldBeFound("url.specified=true");

        // Get all the bookmarkBatchItemList where url is null
        defaultBookmarkBatchItemShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where tags equals to DEFAULT_TAGS
        defaultBookmarkBatchItemShouldBeFound("tags.equals=" + DEFAULT_TAGS);

        // Get all the bookmarkBatchItemList where tags equals to UPDATED_TAGS
        defaultBookmarkBatchItemShouldNotBeFound("tags.equals=" + UPDATED_TAGS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByTagsIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where tags in DEFAULT_TAGS or UPDATED_TAGS
        defaultBookmarkBatchItemShouldBeFound("tags.in=" + DEFAULT_TAGS + "," + UPDATED_TAGS);

        // Get all the bookmarkBatchItemList where tags equals to UPDATED_TAGS
        defaultBookmarkBatchItemShouldNotBeFound("tags.in=" + UPDATED_TAGS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByTagsIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where tags is not null
        defaultBookmarkBatchItemShouldBeFound("tags.specified=true");

        // Get all the bookmarkBatchItemList where tags is null
        defaultBookmarkBatchItemShouldNotBeFound("tags.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByLink_creation_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where link_creation_date equals to DEFAULT_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldBeFound("link_creation_date.equals=" + DEFAULT_LINK_CREATION_DATE);

        // Get all the bookmarkBatchItemList where link_creation_date equals to UPDATED_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldNotBeFound("link_creation_date.equals=" + UPDATED_LINK_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByLink_creation_dateIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where link_creation_date in DEFAULT_LINK_CREATION_DATE or UPDATED_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldBeFound("link_creation_date.in=" + DEFAULT_LINK_CREATION_DATE + "," + UPDATED_LINK_CREATION_DATE);

        // Get all the bookmarkBatchItemList where link_creation_date equals to UPDATED_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldNotBeFound("link_creation_date.in=" + UPDATED_LINK_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByLink_creation_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where link_creation_date is not null
        defaultBookmarkBatchItemShouldBeFound("link_creation_date.specified=true");

        // Get all the bookmarkBatchItemList where link_creation_date is null
        defaultBookmarkBatchItemShouldNotBeFound("link_creation_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByLink_creation_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where link_creation_date greater than or equals to DEFAULT_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldBeFound("link_creation_date.greaterOrEqualThan=" + DEFAULT_LINK_CREATION_DATE);

        // Get all the bookmarkBatchItemList where link_creation_date greater than or equals to UPDATED_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldNotBeFound("link_creation_date.greaterOrEqualThan=" + UPDATED_LINK_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByLink_creation_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where link_creation_date less than or equals to DEFAULT_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldNotBeFound("link_creation_date.lessThan=" + DEFAULT_LINK_CREATION_DATE);

        // Get all the bookmarkBatchItemList where link_creation_date less than or equals to UPDATED_LINK_CREATION_DATE
        defaultBookmarkBatchItemShouldBeFound("link_creation_date.lessThan=" + UPDATED_LINK_CREATION_DATE);
    }


    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where status equals to DEFAULT_STATUS
        defaultBookmarkBatchItemShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the bookmarkBatchItemList where status equals to UPDATED_STATUS
        defaultBookmarkBatchItemShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultBookmarkBatchItemShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the bookmarkBatchItemList where status equals to UPDATED_STATUS
        defaultBookmarkBatchItemShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where status is not null
        defaultBookmarkBatchItemShouldBeFound("status.specified=true");

        // Get all the bookmarkBatchItemList where status is null
        defaultBookmarkBatchItemShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByError_msg_codeIsEqualToSomething() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where error_msg_code equals to DEFAULT_ERROR_MSG_CODE
        defaultBookmarkBatchItemShouldBeFound("error_msg_code.equals=" + DEFAULT_ERROR_MSG_CODE);

        // Get all the bookmarkBatchItemList where error_msg_code equals to UPDATED_ERROR_MSG_CODE
        defaultBookmarkBatchItemShouldNotBeFound("error_msg_code.equals=" + UPDATED_ERROR_MSG_CODE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByError_msg_codeIsInShouldWork() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where error_msg_code in DEFAULT_ERROR_MSG_CODE or UPDATED_ERROR_MSG_CODE
        defaultBookmarkBatchItemShouldBeFound("error_msg_code.in=" + DEFAULT_ERROR_MSG_CODE + "," + UPDATED_ERROR_MSG_CODE);

        // Get all the bookmarkBatchItemList where error_msg_code equals to UPDATED_ERROR_MSG_CODE
        defaultBookmarkBatchItemShouldNotBeFound("error_msg_code.in=" + UPDATED_ERROR_MSG_CODE);
    }

    @Test
    @Transactional
    public void getAllBookmarkBatchItemsByError_msg_codeIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookmarkBatchItemRepository.saveAndFlush(bookmarkBatchItem);

        // Get all the bookmarkBatchItemList where error_msg_code is not null
        defaultBookmarkBatchItemShouldBeFound("error_msg_code.specified=true");

        // Get all the bookmarkBatchItemList where error_msg_code is null
        defaultBookmarkBatchItemShouldNotBeFound("error_msg_code.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultBookmarkBatchItemShouldBeFound(String filter) throws Exception {
        restBookmarkBatchItemMockMvc.perform(get("/api/bookmark-batch-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatchItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
            .andExpect(jsonPath("$.[*].link_creation_date").value(hasItem(sameInstant(DEFAULT_LINK_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].error_msg_code").value(hasItem(DEFAULT_ERROR_MSG_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultBookmarkBatchItemShouldNotBeFound(String filter) throws Exception {
        restBookmarkBatchItemMockMvc.perform(get("/api/bookmark-batch-items?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingBookmarkBatchItem() throws Exception {
        // Get the bookmarkBatchItem
        restBookmarkBatchItemMockMvc.perform(get("/api/bookmark-batch-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBookmarkBatchItem() throws Exception {
        // Initialize the database
        bookmarkBatchItemService.save(bookmarkBatchItem);

        int databaseSizeBeforeUpdate = bookmarkBatchItemRepository.findAll().size();

        // Update the bookmarkBatchItem
        BookmarkBatchItem updatedBookmarkBatchItem = bookmarkBatchItemRepository.findOne(bookmarkBatchItem.getId());
        // Disconnect from session so that the updates on updatedBookmarkBatchItem are not directly saved in db
        em.detach(updatedBookmarkBatchItem);
        updatedBookmarkBatchItem
            .url(UPDATED_URL)
            .tags(UPDATED_TAGS)
            .link_creation_date(UPDATED_LINK_CREATION_DATE)
            .status(UPDATED_STATUS)
            .error_msg_code(UPDATED_ERROR_MSG_CODE);

        restBookmarkBatchItemMockMvc.perform(put("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBookmarkBatchItem)))
            .andExpect(status().isOk());

        // Validate the BookmarkBatchItem in the database
        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeUpdate);
        BookmarkBatchItem testBookmarkBatchItem = bookmarkBatchItemList.get(bookmarkBatchItemList.size() - 1);
        assertThat(testBookmarkBatchItem.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testBookmarkBatchItem.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testBookmarkBatchItem.getLinkCreationDate()).isEqualTo(UPDATED_LINK_CREATION_DATE);
        assertThat(testBookmarkBatchItem.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBookmarkBatchItem.getErrorMsgCode()).isEqualTo(UPDATED_ERROR_MSG_CODE);

        // Validate the BookmarkBatchItem in Elasticsearch
        BookmarkBatchItem bookmarkBatchItemEs = bookmarkBatchItemSearchRepository.findOne(testBookmarkBatchItem.getId());
        assertThat(testBookmarkBatchItem.getLinkCreationDate()).isEqualTo(testBookmarkBatchItem.getLinkCreationDate());
        assertThat(bookmarkBatchItemEs).isEqualToIgnoringGivenFields(testBookmarkBatchItem, "link_creation_date");
    }

    @Test
    @Transactional
    public void updateNonExistingBookmarkBatchItem() throws Exception {
        int databaseSizeBeforeUpdate = bookmarkBatchItemRepository.findAll().size();

        // Create the BookmarkBatchItem

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBookmarkBatchItemMockMvc.perform(put("/api/bookmark-batch-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bookmarkBatchItem)))
            .andExpect(status().isCreated());

        // Validate the BookmarkBatchItem in the database
        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBookmarkBatchItem() throws Exception {
        // Initialize the database
        bookmarkBatchItemService.save(bookmarkBatchItem);

        int databaseSizeBeforeDelete = bookmarkBatchItemRepository.findAll().size();

        // Get the bookmarkBatchItem
        restBookmarkBatchItemMockMvc.perform(delete("/api/bookmark-batch-items/{id}", bookmarkBatchItem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean bookmarkBatchItemExistsInEs = bookmarkBatchItemSearchRepository.exists(bookmarkBatchItem.getId());
        assertThat(bookmarkBatchItemExistsInEs).isFalse();

        // Validate the database is empty
        List<BookmarkBatchItem> bookmarkBatchItemList = bookmarkBatchItemRepository.findAll();
        assertThat(bookmarkBatchItemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBookmarkBatchItem() throws Exception {
        // Initialize the database
        bookmarkBatchItemService.save(bookmarkBatchItem);

        // Search the bookmarkBatchItem
        restBookmarkBatchItemMockMvc.perform(get("/api/_search/bookmark-batch-items?query=id:" + bookmarkBatchItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookmarkBatchItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())))
            .andExpect(jsonPath("$.[*].link_creation_date").value(hasItem(sameInstant(DEFAULT_LINK_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].error_msg_code").value(hasItem(DEFAULT_ERROR_MSG_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookmarkBatchItem.class);
        BookmarkBatchItem bookmarkBatchItem1 = new BookmarkBatchItem();
        bookmarkBatchItem1.setId(1L);
        BookmarkBatchItem bookmarkBatchItem2 = new BookmarkBatchItem();
        bookmarkBatchItem2.setId(bookmarkBatchItem1.getId());
        assertThat(bookmarkBatchItem1).isEqualTo(bookmarkBatchItem2);
        bookmarkBatchItem2.setId(2L);
        assertThat(bookmarkBatchItem1).isNotEqualTo(bookmarkBatchItem2);
        bookmarkBatchItem1.setId(null);
        assertThat(bookmarkBatchItem1).isNotEqualTo(bookmarkBatchItem2);
    }
}
