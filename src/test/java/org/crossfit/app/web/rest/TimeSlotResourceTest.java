package org.crossfit.app.web.rest;

import org.crossfit.app.Application;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.repository.TimeSlotRepository;

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
import org.joda.time.LocalTime;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.crossfit.app.domain.enumeration.Level;

/**
 * Test class for the TimeSlotResource REST controller.
 *
 * @see TimeSlotResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TimeSlotResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final Integer DEFAULT_DAY_OF_WEEK = 1;
    private static final Integer UPDATED_DAY_OF_WEEK = 2;
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final LocalTime DEFAULT_START_TIME = new LocalTime(0L, DateTimeZone.UTC);
    private static final LocalTime UPDATED_START_TIME = new LocalTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_START_TIME_STR = dateTimeFormatter.print(DEFAULT_START_TIME);

    private static final LocalTime DEFAULT_END_TIME = new LocalTime(0L, DateTimeZone.UTC);
    private static final LocalTime UPDATED_END_TIME = new LocalTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_END_TIME_STR = dateTimeFormatter.print(DEFAULT_END_TIME);

    private static final Integer DEFAULT_MAX_ATTENDEES = 0;
    private static final Integer UPDATED_MAX_ATTENDEES = 1;

    private static final Level DEFAULT_REQUIRED_LEVEL = Level.FOUNDATION;
    private static final Level UPDATED_REQUIRED_LEVEL = Level.NOVICE;

    @Inject
    private TimeSlotRepository timeSlotRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restTimeSlotMockMvc;

    private TimeSlot timeSlot;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimeSlotResource timeSlotResource = new TimeSlotResource();
        ReflectionTestUtils.setField(timeSlotResource, "timeSlotRepository", timeSlotRepository);
        this.restTimeSlotMockMvc = MockMvcBuilders.standaloneSetup(timeSlotResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        timeSlot = new TimeSlot();
        timeSlot.setDayOfWeek(DEFAULT_DAY_OF_WEEK);
        timeSlot.setName(DEFAULT_NAME);
        timeSlot.setStartTime(DEFAULT_START_TIME);
        timeSlot.setEndTime(DEFAULT_END_TIME);
        timeSlot.setMaxAttendees(DEFAULT_MAX_ATTENDEES);
        timeSlot.setRequiredLevel(DEFAULT_REQUIRED_LEVEL);
    }

    @Test
    @Transactional
    public void createTimeSlot() throws Exception {
        int databaseSizeBeforeCreate = timeSlotRepository.findAll().size();

        // Create the TimeSlot

        restTimeSlotMockMvc.perform(post("/api/timeSlots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeSlot)))
                .andExpect(status().isCreated());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeCreate + 1);
        TimeSlot testTimeSlot = timeSlots.get(timeSlots.size() - 1);
        assertThat(testTimeSlot.getDayOfWeek()).isEqualTo(DEFAULT_DAY_OF_WEEK);
        assertThat(testTimeSlot.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTimeSlot.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTimeSlot.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testTimeSlot.getMaxAttendees()).isEqualTo(DEFAULT_MAX_ATTENDEES);
        assertThat(testTimeSlot.getRequiredLevel()).isEqualTo(DEFAULT_REQUIRED_LEVEL);
    }

    @Test
    @Transactional
    public void checkDayOfWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotRepository.findAll().size();
        // set the field null
        timeSlot.setDayOfWeek(null);

        // Create the TimeSlot, which fails.

        restTimeSlotMockMvc.perform(post("/api/timeSlots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeSlot)))
                .andExpect(status().isBadRequest());

        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRequiredLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotRepository.findAll().size();
        // set the field null
        timeSlot.setRequiredLevel(null);

        // Create the TimeSlot, which fails.

        restTimeSlotMockMvc.perform(post("/api/timeSlots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeSlot)))
                .andExpect(status().isBadRequest());

        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTimeSlots() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get all the timeSlots
        restTimeSlotMockMvc.perform(get("/api/timeSlots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(timeSlot.getId().intValue())))
                .andExpect(jsonPath("$.[*].dayOfWeek").value(hasItem(DEFAULT_DAY_OF_WEEK)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME_STR)))
                .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME_STR)))
                .andExpect(jsonPath("$.[*].maxAttendees").value(hasItem(DEFAULT_MAX_ATTENDEES)))
                .andExpect(jsonPath("$.[*].requiredLevel").value(hasItem(DEFAULT_REQUIRED_LEVEL.toString())));
    }

    @Test
    @Transactional
    public void getTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/timeSlots/{id}", timeSlot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(timeSlot.getId().intValue()))
            .andExpect(jsonPath("$.dayOfWeek").value(DEFAULT_DAY_OF_WEEK))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME_STR))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME_STR))
            .andExpect(jsonPath("$.maxAttendees").value(DEFAULT_MAX_ATTENDEES))
            .andExpect(jsonPath("$.requiredLevel").value(DEFAULT_REQUIRED_LEVEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTimeSlot() throws Exception {
        // Get the timeSlot
        restTimeSlotMockMvc.perform(get("/api/timeSlots/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

		int databaseSizeBeforeUpdate = timeSlotRepository.findAll().size();

        // Update the timeSlot
        timeSlot.setDayOfWeek(UPDATED_DAY_OF_WEEK);
        timeSlot.setName(UPDATED_NAME);
        timeSlot.setStartTime(UPDATED_START_TIME);
        timeSlot.setEndTime(UPDATED_END_TIME);
        timeSlot.setMaxAttendees(UPDATED_MAX_ATTENDEES);
        timeSlot.setRequiredLevel(UPDATED_REQUIRED_LEVEL);
        

        restTimeSlotMockMvc.perform(put("/api/timeSlots")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(timeSlot)))
                .andExpect(status().isOk());

        // Validate the TimeSlot in the database
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeUpdate);
        TimeSlot testTimeSlot = timeSlots.get(timeSlots.size() - 1);
        assertThat(testTimeSlot.getDayOfWeek()).isEqualTo(UPDATED_DAY_OF_WEEK);
        assertThat(testTimeSlot.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTimeSlot.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTimeSlot.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testTimeSlot.getMaxAttendees()).isEqualTo(UPDATED_MAX_ATTENDEES);
        assertThat(testTimeSlot.getRequiredLevel()).isEqualTo(UPDATED_REQUIRED_LEVEL);
    }

    @Test
    @Transactional
    public void deleteTimeSlot() throws Exception {
        // Initialize the database
        timeSlotRepository.saveAndFlush(timeSlot);

		int databaseSizeBeforeDelete = timeSlotRepository.findAll().size();

        // Get the timeSlot
        restTimeSlotMockMvc.perform(delete("/api/timeSlots/{id}", timeSlot.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        assertThat(timeSlots).hasSize(databaseSizeBeforeDelete - 1);
    }
}
