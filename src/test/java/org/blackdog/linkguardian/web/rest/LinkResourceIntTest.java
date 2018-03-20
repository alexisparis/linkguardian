package org.blackdog.linkguardian.web.rest;

import javax.inject.Inject;
import org.blackdog.linkguardian.LinkguardianApp;

import org.blackdog.linkguardian.domain.Link;
import org.blackdog.linkguardian.domain.User;
import org.blackdog.linkguardian.domain.Tag;
import org.blackdog.linkguardian.repository.LinkRepository;
import org.blackdog.linkguardian.service.LinkBuilder;
import org.blackdog.linkguardian.service.LinkService;
import org.blackdog.linkguardian.repository.search.LinkSearchRepository;
import org.blackdog.linkguardian.service.UserService;
import org.blackdog.linkguardian.service.util.TagsNormalizer;
import org.blackdog.linkguardian.web.rest.errors.ExceptionTranslator;
import org.blackdog.linkguardian.service.LinkQueryService;

import org.blackdog.linkguardian.web.util.SpringSecurityUtils;
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
 * Test class for the LinkResource REST controller.
 *
 * @see LinkResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkguardianApp.class)
public class LinkResourceIntTest {

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DOMAIN = "AAAAAAAAAA";
    private static final String UPDATED_DOMAIN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_LOCKED = false;
    private static final Boolean UPDATED_LOCKED = true;

    private static final Integer DEFAULT_NOTE = 1;
    private static final Integer UPDATED_NOTE = 2;

    private static final Boolean DEFAULT_READ = false;
    private static final Boolean UPDATED_READ = true;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_URL = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_URL = "BBBBBBBBBB";


    @Inject
    private LinkRepository linkRepository;

    @Autowired
    private LinkService linkService;

    @Inject
    private LinkBuilder linkBuilder;

    @Inject
    private UserService userService;

    @Inject
    private TagsNormalizer tagsNormalizer;

    @Autowired
    private SpringSecurityUtils springSecurityUtils;

    @Autowired
    private LinkSearchRepository linkSearchRepository;

    @Autowired
    private LinkQueryService linkQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLinkMockMvc;

    private Link link;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LinkResource linkResource = new LinkResource(
            linkRepository, linkService, linkBuilder, userService, tagsNormalizer, springSecurityUtils, linkQueryService);

        this.restLinkMockMvc = MockMvcBuilders.standaloneSetup(linkResource)
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
    public static Link createEntity(EntityManager em) {
        Link link = new Link()
            .creation_date(DEFAULT_CREATION_DATE)
            .description(DEFAULT_DESCRIPTION)
            .domain(DEFAULT_DOMAIN)
            .locked(DEFAULT_LOCKED)
            .note(DEFAULT_NOTE)
            .read(DEFAULT_READ)
            .title(DEFAULT_TITLE)
            .url(DEFAULT_URL)
            .originalUrl(DEFAULT_ORIGINAL_URL);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        link.setUser(user);
        return link;
    }

    @Before
    public void initTest() {
        linkSearchRepository.deleteAll();
        link = createEntity(em);
    }

    @Test
    @Transactional
    public void createLink() throws Exception {
        int databaseSizeBeforeCreate = linkRepository.findAll().size();

        // Create the Link
        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isCreated());

        // Validate the Link in the database
        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeCreate + 1);
        Link testLink = linkList.get(linkList.size() - 1);
        assertThat(testLink.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testLink.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testLink.getDomain()).isEqualTo(DEFAULT_DOMAIN);
        assertThat(testLink.isLocked()).isEqualTo(DEFAULT_LOCKED);
        assertThat(testLink.getNote()).isEqualTo(DEFAULT_NOTE);
        assertThat(testLink.isRead()).isEqualTo(DEFAULT_READ);
        assertThat(testLink.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLink.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testLink.getOriginalUrl()).isEqualTo(DEFAULT_ORIGINAL_URL);

        // Validate the Link in Elasticsearch
        Link linkEs = linkSearchRepository.findOne(testLink.getId());
        assertThat(testLink.getCreationDate()).isEqualTo(testLink.getCreationDate());
        assertThat(linkEs).isEqualToIgnoringGivenFields(testLink, "creation_date");
    }

    @Test
    @Transactional
    public void createLinkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = linkRepository.findAll().size();

        // Create the Link with an existing ID
        link.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isBadRequest());

        // Validate the Link in the database
        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreation_dateIsRequired() throws Exception {
        int databaseSizeBeforeTest = linkRepository.findAll().size();
        // set the field null
        link.setCreationDate(null);

        // Create the Link, which fails.

        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isBadRequest());

        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDomainIsRequired() throws Exception {
        int databaseSizeBeforeTest = linkRepository.findAll().size();
        // set the field null
        link.setDomain(null);

        // Create the Link, which fails.

        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isBadRequest());

        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = linkRepository.findAll().size();
        // set the field null
        link.setUrl(null);

        // Create the Link, which fails.

        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isBadRequest());

        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOriginal_urlIsRequired() throws Exception {
        int databaseSizeBeforeTest = linkRepository.findAll().size();
        // set the field null
        link.setOriginalUrl(null);

        // Create the Link, which fails.

        restLinkMockMvc.perform(post("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isBadRequest());

        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLinks() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList
        restLinkMockMvc.perform(get("/api/links?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(link.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ.booleanValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].original_url").value(hasItem(DEFAULT_ORIGINAL_URL.toString())));
    }

    @Test
    @Transactional
    public void getLink() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get the link
        restLinkMockMvc.perform(get("/api/links/{id}", link.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(link.getId().intValue()))
            .andExpect(jsonPath("$.creation_date").value(sameInstant(DEFAULT_CREATION_DATE)))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.domain").value(DEFAULT_DOMAIN.toString()))
            .andExpect(jsonPath("$.locked").value(DEFAULT_LOCKED.booleanValue()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.read").value(DEFAULT_READ.booleanValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.original_url").value(DEFAULT_ORIGINAL_URL.toString()));
    }

    @Test
    @Transactional
    public void getAllLinksByCreation_dateIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where creation_date equals to DEFAULT_CREATION_DATE
        defaultLinkShouldBeFound("creation_date.equals=" + DEFAULT_CREATION_DATE);

        // Get all the linkList where creation_date equals to UPDATED_CREATION_DATE
        defaultLinkShouldNotBeFound("creation_date.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllLinksByCreation_dateIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where creation_date in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultLinkShouldBeFound("creation_date.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the linkList where creation_date equals to UPDATED_CREATION_DATE
        defaultLinkShouldNotBeFound("creation_date.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllLinksByCreation_dateIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where creation_date is not null
        defaultLinkShouldBeFound("creation_date.specified=true");

        // Get all the linkList where creation_date is null
        defaultLinkShouldNotBeFound("creation_date.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByCreation_dateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where creation_date greater than or equals to DEFAULT_CREATION_DATE
        defaultLinkShouldBeFound("creation_date.greaterOrEqualThan=" + DEFAULT_CREATION_DATE);

        // Get all the linkList where creation_date greater than or equals to UPDATED_CREATION_DATE
        defaultLinkShouldNotBeFound("creation_date.greaterOrEqualThan=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllLinksByCreation_dateIsLessThanSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where creation_date less than or equals to DEFAULT_CREATION_DATE
        defaultLinkShouldNotBeFound("creation_date.lessThan=" + DEFAULT_CREATION_DATE);

        // Get all the linkList where creation_date less than or equals to UPDATED_CREATION_DATE
        defaultLinkShouldBeFound("creation_date.lessThan=" + UPDATED_CREATION_DATE);
    }


    @Test
    @Transactional
    public void getAllLinksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where description equals to DEFAULT_DESCRIPTION
        defaultLinkShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the linkList where description equals to UPDATED_DESCRIPTION
        defaultLinkShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLinksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultLinkShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the linkList where description equals to UPDATED_DESCRIPTION
        defaultLinkShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllLinksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where description is not null
        defaultLinkShouldBeFound("description.specified=true");

        // Get all the linkList where description is null
        defaultLinkShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByDomainIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where domain equals to DEFAULT_DOMAIN
        defaultLinkShouldBeFound("domain.equals=" + DEFAULT_DOMAIN);

        // Get all the linkList where domain equals to UPDATED_DOMAIN
        defaultLinkShouldNotBeFound("domain.equals=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    public void getAllLinksByDomainIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where domain in DEFAULT_DOMAIN or UPDATED_DOMAIN
        defaultLinkShouldBeFound("domain.in=" + DEFAULT_DOMAIN + "," + UPDATED_DOMAIN);

        // Get all the linkList where domain equals to UPDATED_DOMAIN
        defaultLinkShouldNotBeFound("domain.in=" + UPDATED_DOMAIN);
    }

    @Test
    @Transactional
    public void getAllLinksByDomainIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where domain is not null
        defaultLinkShouldBeFound("domain.specified=true");

        // Get all the linkList where domain is null
        defaultLinkShouldNotBeFound("domain.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByLockedIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where locked equals to DEFAULT_LOCKED
        defaultLinkShouldBeFound("locked.equals=" + DEFAULT_LOCKED);

        // Get all the linkList where locked equals to UPDATED_LOCKED
        defaultLinkShouldNotBeFound("locked.equals=" + UPDATED_LOCKED);
    }

    @Test
    @Transactional
    public void getAllLinksByLockedIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where locked in DEFAULT_LOCKED or UPDATED_LOCKED
        defaultLinkShouldBeFound("locked.in=" + DEFAULT_LOCKED + "," + UPDATED_LOCKED);

        // Get all the linkList where locked equals to UPDATED_LOCKED
        defaultLinkShouldNotBeFound("locked.in=" + UPDATED_LOCKED);
    }

    @Test
    @Transactional
    public void getAllLinksByLockedIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where locked is not null
        defaultLinkShouldBeFound("locked.specified=true");

        // Get all the linkList where locked is null
        defaultLinkShouldNotBeFound("locked.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where note equals to DEFAULT_NOTE
        defaultLinkShouldBeFound("note.equals=" + DEFAULT_NOTE);

        // Get all the linkList where note equals to UPDATED_NOTE
        defaultLinkShouldNotBeFound("note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void getAllLinksByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where note in DEFAULT_NOTE or UPDATED_NOTE
        defaultLinkShouldBeFound("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE);

        // Get all the linkList where note equals to UPDATED_NOTE
        defaultLinkShouldNotBeFound("note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void getAllLinksByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where note is not null
        defaultLinkShouldBeFound("note.specified=true");

        // Get all the linkList where note is null
        defaultLinkShouldNotBeFound("note.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByNoteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where note greater than or equals to DEFAULT_NOTE
        defaultLinkShouldBeFound("note.greaterOrEqualThan=" + DEFAULT_NOTE);

        // Get all the linkList where note greater than or equals to UPDATED_NOTE
        defaultLinkShouldNotBeFound("note.greaterOrEqualThan=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    public void getAllLinksByNoteIsLessThanSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where note less than or equals to DEFAULT_NOTE
        defaultLinkShouldNotBeFound("note.lessThan=" + DEFAULT_NOTE);

        // Get all the linkList where note less than or equals to UPDATED_NOTE
        defaultLinkShouldBeFound("note.lessThan=" + UPDATED_NOTE);
    }


    @Test
    @Transactional
    public void getAllLinksByReadIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where read equals to DEFAULT_READ
        defaultLinkShouldBeFound("read.equals=" + DEFAULT_READ);

        // Get all the linkList where read equals to UPDATED_READ
        defaultLinkShouldNotBeFound("read.equals=" + UPDATED_READ);
    }

    @Test
    @Transactional
    public void getAllLinksByReadIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where read in DEFAULT_READ or UPDATED_READ
        defaultLinkShouldBeFound("read.in=" + DEFAULT_READ + "," + UPDATED_READ);

        // Get all the linkList where read equals to UPDATED_READ
        defaultLinkShouldNotBeFound("read.in=" + UPDATED_READ);
    }

    @Test
    @Transactional
    public void getAllLinksByReadIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where read is not null
        defaultLinkShouldBeFound("read.specified=true");

        // Get all the linkList where read is null
        defaultLinkShouldNotBeFound("read.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where title equals to DEFAULT_TITLE
        defaultLinkShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the linkList where title equals to UPDATED_TITLE
        defaultLinkShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLinksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultLinkShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the linkList where title equals to UPDATED_TITLE
        defaultLinkShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllLinksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where title is not null
        defaultLinkShouldBeFound("title.specified=true");

        // Get all the linkList where title is null
        defaultLinkShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where url equals to DEFAULT_URL
        defaultLinkShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the linkList where url equals to UPDATED_URL
        defaultLinkShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllLinksByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where url in DEFAULT_URL or UPDATED_URL
        defaultLinkShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the linkList where url equals to UPDATED_URL
        defaultLinkShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllLinksByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where url is not null
        defaultLinkShouldBeFound("url.specified=true");

        // Get all the linkList where url is null
        defaultLinkShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByOriginal_urlIsEqualToSomething() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where originalUrl equals to DEFAULT_ORIGINAL_URL
        defaultLinkShouldBeFound("original_url.equals=" + DEFAULT_ORIGINAL_URL);

        // Get all the linkList where originalUrl equals to UPDATED_ORIGINAL_URL
        defaultLinkShouldNotBeFound("original_url.equals=" + UPDATED_ORIGINAL_URL);
    }

    @Test
    @Transactional
    public void getAllLinksByOriginal_urlIsInShouldWork() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where originalUrl in DEFAULT_ORIGINAL_URL or UPDATED_ORIGINAL_URL
        defaultLinkShouldBeFound("original_url.in=" + DEFAULT_ORIGINAL_URL + "," + UPDATED_ORIGINAL_URL);

        // Get all the linkList where originalUrl equals to UPDATED_ORIGINAL_URL
        defaultLinkShouldNotBeFound("original_url.in=" + UPDATED_ORIGINAL_URL);
    }

    @Test
    @Transactional
    public void getAllLinksByOriginal_urlIsNullOrNotNull() throws Exception {
        // Initialize the database
        linkRepository.saveAndFlush(link);

        // Get all the linkList where originalUrl is not null
        defaultLinkShouldBeFound("original_url.specified=true");

        // Get all the linkList where originalUrl is null
        defaultLinkShouldNotBeFound("original_url.specified=false");
    }

    @Test
    @Transactional
    public void getAllLinksByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        link.setUser(user);
        linkRepository.saveAndFlush(link);
        Long userId = user.getId();

        // Get all the linkList where user equals to userId
        defaultLinkShouldBeFound("userId.equals=" + userId);

        // Get all the linkList where user equals to userId + 1
        defaultLinkShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllLinksByTagsIsEqualToSomething() throws Exception {
        // Initialize the database
        Tag tags = TagResourceIntTest.createEntity(em);
        em.persist(tags);
        em.flush();
        link.getTags().add(tags);
        linkRepository.saveAndFlush(link);
        Long tagsId = tags.getId();

        // Get all the linkList where tags equals to tagsId
        defaultLinkShouldBeFound("tagsId.equals=" + tagsId);

        // Get all the linkList where tags equals to tagsId + 1
        defaultLinkShouldNotBeFound("tagsId.equals=" + (tagsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultLinkShouldBeFound(String filter) throws Exception {
        restLinkMockMvc.perform(get("/api/links?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(link.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ.booleanValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].original_url").value(hasItem(DEFAULT_ORIGINAL_URL.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultLinkShouldNotBeFound(String filter) throws Exception {
        restLinkMockMvc.perform(get("/api/links?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingLink() throws Exception {
        // Get the link
        restLinkMockMvc.perform(get("/api/links/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLink() throws Exception {
        // Initialize the database
        linkService.save(link);

        int databaseSizeBeforeUpdate = linkRepository.findAll().size();

        // Update the link
        Link updatedLink = linkRepository.findOne(link.getId());
        // Disconnect from session so that the updates on updatedLink are not directly saved in db
        em.detach(updatedLink);
        updatedLink
            .creation_date(UPDATED_CREATION_DATE)
            .description(UPDATED_DESCRIPTION)
            .domain(UPDATED_DOMAIN)
            .locked(UPDATED_LOCKED)
            .note(UPDATED_NOTE)
            .read(UPDATED_READ)
            .title(UPDATED_TITLE)
            .url(UPDATED_URL)
            .originalUrl(UPDATED_ORIGINAL_URL);

        restLinkMockMvc.perform(put("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLink)))
            .andExpect(status().isOk());

        // Validate the Link in the database
        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeUpdate);
        Link testLink = linkList.get(linkList.size() - 1);
        assertThat(testLink.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testLink.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testLink.getDomain()).isEqualTo(UPDATED_DOMAIN);
        assertThat(testLink.isLocked()).isEqualTo(UPDATED_LOCKED);
        assertThat(testLink.getNote()).isEqualTo(UPDATED_NOTE);
        assertThat(testLink.isRead()).isEqualTo(UPDATED_READ);
        assertThat(testLink.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLink.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testLink.getOriginalUrl()).isEqualTo(UPDATED_ORIGINAL_URL);

        // Validate the Link in Elasticsearch
        Link linkEs = linkSearchRepository.findOne(testLink.getId());
        assertThat(testLink.getCreationDate()).isEqualTo(testLink.getCreationDate());
        assertThat(linkEs).isEqualToIgnoringGivenFields(testLink, "creation_date");
    }

    @Test
    @Transactional
    public void updateNonExistingLink() throws Exception {
        int databaseSizeBeforeUpdate = linkRepository.findAll().size();

        // Create the Link

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLinkMockMvc.perform(put("/api/links")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(link)))
            .andExpect(status().isCreated());

        // Validate the Link in the database
        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLink() throws Exception {
        // Initialize the database
        linkService.save(link);

        int databaseSizeBeforeDelete = linkRepository.findAll().size();

        // Get the link
        restLinkMockMvc.perform(delete("/api/links/{id}", link.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean linkExistsInEs = linkSearchRepository.exists(link.getId());
        assertThat(linkExistsInEs).isFalse();

        // Validate the database is empty
        List<Link> linkList = linkRepository.findAll();
        assertThat(linkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLink() throws Exception {
        // Initialize the database
        linkService.save(link);

        // Search the link
        restLinkMockMvc.perform(get("/api/_search/links?query=id:" + link.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(link.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation_date").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].domain").value(hasItem(DEFAULT_DOMAIN.toString())))
            .andExpect(jsonPath("$.[*].locked").value(hasItem(DEFAULT_LOCKED.booleanValue())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ.booleanValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].original_url").value(hasItem(DEFAULT_ORIGINAL_URL.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Link.class);
        Link link1 = new Link();
        link1.setId(1L);
        Link link2 = new Link();
        link2.setId(link1.getId());
        assertThat(link1).isEqualTo(link2);
        link2.setId(2L);
        assertThat(link1).isNotEqualTo(link2);
        link1.setId(null);
        assertThat(link1).isNotEqualTo(link2);
    }
}
