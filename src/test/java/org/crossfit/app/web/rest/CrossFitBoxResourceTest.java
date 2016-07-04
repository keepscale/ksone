package org.crossfit.app.web.rest;

import org.crossfit.app.Application;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.repository.CrossFitBoxRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CrossFitBoxResource REST controller.
 *
 * @see CrossFitBoxResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CrossFitBoxResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_WEBSITE = "SAMPLE_TEXT";
    private static final String UPDATED_WEBSITE = "UPDATED_TEXT";
    private static final String DEFAULT_ADMINWEBSITE = "SAMPLE_TEXT";
    private static final String UPDATED_ADMINWEBSITE = "UPDATED_TEXT";
    private static final String DEFAULT_BOOKINGWEBSITE = "SAMPLE_TEXT";
    private static final String UPDATED_BOOKINGWEBSITE = "UPDATED_TEXT";
    private static final String DEFAULT_ROOTWEBSITE = "SAMPLE_TEXT";
    private static final String UPDATED_ROOTWEBSITE = "UPDATED_TEXT";

    @Inject
    private CrossFitBoxRepository crossFitBoxRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restCrossFitBoxMockMvc;

    private CrossFitBox crossFitBox;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CrossFitBoxResource crossFitBoxResource = new CrossFitBoxResource();
        ReflectionTestUtils.setField(crossFitBoxResource, "crossFitBoxRepository", crossFitBoxRepository);
        this.restCrossFitBoxMockMvc = MockMvcBuilders.standaloneSetup(crossFitBoxResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        crossFitBox = new CrossFitBox();
        crossFitBox.setName(DEFAULT_NAME);
        crossFitBox.setWebsite(DEFAULT_WEBSITE);
        crossFitBox.setAdminwebsite(DEFAULT_ADMINWEBSITE);
        crossFitBox.setBookingwebsite(DEFAULT_BOOKINGWEBSITE);
        crossFitBox.setRootwebsite(DEFAULT_ROOTWEBSITE);
    }

    @Test
    @Transactional
    public void createCrossFitBox() throws Exception {
        int databaseSizeBeforeCreate = crossFitBoxRepository.findAll().size();

        // Create the CrossFitBox

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isCreated());

        // Validate the CrossFitBox in the database
        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeCreate + 1);
        CrossFitBox testCrossFitBox = crossFitBoxs.get(crossFitBoxs.size() - 1);
        assertThat(testCrossFitBox.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCrossFitBox.getWebsite()).isEqualTo(DEFAULT_WEBSITE);
        assertThat(testCrossFitBox.getAdminwebsite()).isEqualTo(DEFAULT_ADMINWEBSITE);
        assertThat(testCrossFitBox.getBookingwebsite()).isEqualTo(DEFAULT_BOOKINGWEBSITE);
        assertThat(testCrossFitBox.getRootwebsite()).isEqualTo(DEFAULT_ROOTWEBSITE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = crossFitBoxRepository.findAll().size();
        // set the field null
        crossFitBox.setName(null);

        // Create the CrossFitBox, which fails.

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isBadRequest());

        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkWebsiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = crossFitBoxRepository.findAll().size();
        // set the field null
        crossFitBox.setWebsite(null);

        // Create the CrossFitBox, which fails.

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isBadRequest());

        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAdminwebsiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = crossFitBoxRepository.findAll().size();
        // set the field null
        crossFitBox.setAdminwebsite(null);

        // Create the CrossFitBox, which fails.

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isBadRequest());

        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBookingwebsiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = crossFitBoxRepository.findAll().size();
        // set the field null
        crossFitBox.setBookingwebsite(null);

        // Create the CrossFitBox, which fails.

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isBadRequest());

        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRootwebsiteIsRequired() throws Exception {
        int databaseSizeBeforeTest = crossFitBoxRepository.findAll().size();
        // set the field null
        crossFitBox.setRootwebsite(null);

        // Create the CrossFitBox, which fails.

        restCrossFitBoxMockMvc.perform(post("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isBadRequest());

        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCrossFitBoxs() throws Exception {
        // Initialize the database
        crossFitBoxRepository.saveAndFlush(crossFitBox);

        // Get all the crossFitBoxs
        restCrossFitBoxMockMvc.perform(get("/api/crossFitBoxs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(crossFitBox.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].website").value(hasItem(DEFAULT_WEBSITE.toString())))
                .andExpect(jsonPath("$.[*].adminwebsite").value(hasItem(DEFAULT_ADMINWEBSITE.toString())))
                .andExpect(jsonPath("$.[*].bookingwebsite").value(hasItem(DEFAULT_BOOKINGWEBSITE.toString())))
                .andExpect(jsonPath("$.[*].rootwebsite").value(hasItem(DEFAULT_ROOTWEBSITE.toString())));
    }

    @Test
    @Transactional
    public void getCrossFitBox() throws Exception {
        // Initialize the database
        crossFitBoxRepository.saveAndFlush(crossFitBox);

        // Get the crossFitBox
        restCrossFitBoxMockMvc.perform(get("/api/crossFitBoxs/{id}", crossFitBox.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(crossFitBox.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.website").value(DEFAULT_WEBSITE.toString()))
            .andExpect(jsonPath("$.adminwebsite").value(DEFAULT_ADMINWEBSITE.toString()))
            .andExpect(jsonPath("$.bookingwebsite").value(DEFAULT_BOOKINGWEBSITE.toString()))
            .andExpect(jsonPath("$.rootwebsite").value(DEFAULT_ROOTWEBSITE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCrossFitBox() throws Exception {
        // Get the crossFitBox
        restCrossFitBoxMockMvc.perform(get("/api/crossFitBoxs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCrossFitBox() throws Exception {
        // Initialize the database
        crossFitBoxRepository.saveAndFlush(crossFitBox);

		int databaseSizeBeforeUpdate = crossFitBoxRepository.findAll().size();

        // Update the crossFitBox
        crossFitBox.setName(UPDATED_NAME);
        crossFitBox.setWebsite(UPDATED_WEBSITE);
        crossFitBox.setAdminwebsite(UPDATED_ADMINWEBSITE);
        crossFitBox.setBookingwebsite(UPDATED_BOOKINGWEBSITE);
        crossFitBox.setRootwebsite(UPDATED_ROOTWEBSITE);
        

        restCrossFitBoxMockMvc.perform(put("/api/crossFitBoxs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(crossFitBox)))
                .andExpect(status().isOk());

        // Validate the CrossFitBox in the database
        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeUpdate);
        CrossFitBox testCrossFitBox = crossFitBoxs.get(crossFitBoxs.size() - 1);
        assertThat(testCrossFitBox.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCrossFitBox.getWebsite()).isEqualTo(UPDATED_WEBSITE);
        assertThat(testCrossFitBox.getAdminwebsite()).isEqualTo(UPDATED_ADMINWEBSITE);
        assertThat(testCrossFitBox.getBookingwebsite()).isEqualTo(UPDATED_BOOKINGWEBSITE);
        assertThat(testCrossFitBox.getRootwebsite()).isEqualTo(UPDATED_ROOTWEBSITE);
    }

    @Test
    @Transactional
    public void deleteCrossFitBox() throws Exception {
        // Initialize the database
        crossFitBoxRepository.saveAndFlush(crossFitBox);

		int databaseSizeBeforeDelete = crossFitBoxRepository.findAll().size();

        // Get the crossFitBox
        restCrossFitBoxMockMvc.perform(delete("/api/crossFitBoxs/{id}", crossFitBox.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<CrossFitBox> crossFitBoxs = crossFitBoxRepository.findAll();
        assertThat(crossFitBoxs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
