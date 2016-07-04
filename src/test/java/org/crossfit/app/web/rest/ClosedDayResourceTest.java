package org.crossfit.app.web.rest;

import org.crossfit.app.Application;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.repository.ClosedDayRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ClosedDayResource REST controller.
 *
 * @see ClosedDayResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClosedDayResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final DateTime DEFAULT_START_AT = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_START_AT = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_START_AT_STR = dateTimeFormatter.print(DEFAULT_START_AT);

    private static final DateTime DEFAULT_END_AT = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_END_AT = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_END_AT_STR = dateTimeFormatter.print(DEFAULT_END_AT);

    @Inject
    private ClosedDayRepository closedDayRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restClosedDayMockMvc;

    private ClosedDay closedDay;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClosedDayResource closedDayResource = new ClosedDayResource();
        ReflectionTestUtils.setField(closedDayResource, "closedDayRepository", closedDayRepository);
        this.restClosedDayMockMvc = MockMvcBuilders.standaloneSetup(closedDayResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        closedDay = new ClosedDay();
        closedDay.setName(DEFAULT_NAME);
        closedDay.setStartAt(DEFAULT_START_AT);
        closedDay.setEndAt(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    public void createClosedDay() throws Exception {
        int databaseSizeBeforeCreate = closedDayRepository.findAll().size();

        // Create the ClosedDay

        restClosedDayMockMvc.perform(post("/api/closedDays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(closedDay)))
                .andExpect(status().isCreated());

        // Validate the ClosedDay in the database
        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeCreate + 1);
        ClosedDay testClosedDay = closedDays.get(closedDays.size() - 1);
        assertThat(testClosedDay.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClosedDay.getStartAt().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_START_AT);
        assertThat(testClosedDay.getEndAt().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = closedDayRepository.findAll().size();
        // set the field null
        closedDay.setName(null);

        // Create the ClosedDay, which fails.

        restClosedDayMockMvc.perform(post("/api/closedDays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(closedDay)))
                .andExpect(status().isBadRequest());

        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = closedDayRepository.findAll().size();
        // set the field null
        closedDay.setStartAt(null);

        // Create the ClosedDay, which fails.

        restClosedDayMockMvc.perform(post("/api/closedDays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(closedDay)))
                .andExpect(status().isBadRequest());

        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = closedDayRepository.findAll().size();
        // set the field null
        closedDay.setEndAt(null);

        // Create the ClosedDay, which fails.

        restClosedDayMockMvc.perform(post("/api/closedDays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(closedDay)))
                .andExpect(status().isBadRequest());

        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClosedDays() throws Exception {
        // Initialize the database
        closedDayRepository.saveAndFlush(closedDay);

        // Get all the closedDays
        restClosedDayMockMvc.perform(get("/api/closedDays"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(closedDay.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT_STR)))
                .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT_STR)));
    }

    @Test
    @Transactional
    public void getClosedDay() throws Exception {
        // Initialize the database
        closedDayRepository.saveAndFlush(closedDay);

        // Get the closedDay
        restClosedDayMockMvc.perform(get("/api/closedDays/{id}", closedDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(closedDay.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.startAt").value(DEFAULT_START_AT_STR))
            .andExpect(jsonPath("$.endAt").value(DEFAULT_END_AT_STR));
    }

    @Test
    @Transactional
    public void getNonExistingClosedDay() throws Exception {
        // Get the closedDay
        restClosedDayMockMvc.perform(get("/api/closedDays/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClosedDay() throws Exception {
        // Initialize the database
        closedDayRepository.saveAndFlush(closedDay);

		int databaseSizeBeforeUpdate = closedDayRepository.findAll().size();

        // Update the closedDay
        closedDay.setName(UPDATED_NAME);
        closedDay.setStartAt(UPDATED_START_AT);
        closedDay.setEndAt(UPDATED_END_AT);
        

        restClosedDayMockMvc.perform(put("/api/closedDays")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(closedDay)))
                .andExpect(status().isOk());

        // Validate the ClosedDay in the database
        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeUpdate);
        ClosedDay testClosedDay = closedDays.get(closedDays.size() - 1);
        assertThat(testClosedDay.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClosedDay.getStartAt().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_START_AT);
        assertThat(testClosedDay.getEndAt().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    public void deleteClosedDay() throws Exception {
        // Initialize the database
        closedDayRepository.saveAndFlush(closedDay);

		int databaseSizeBeforeDelete = closedDayRepository.findAll().size();

        // Get the closedDay
        restClosedDayMockMvc.perform(delete("/api/closedDays/{id}", closedDay.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ClosedDay> closedDays = closedDayRepository.findAll();
        assertThat(closedDays).hasSize(databaseSizeBeforeDelete - 1);
    }
}
