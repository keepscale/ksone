package org.crossfit.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.TimeSlotExclusionRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.repository.TimeSlotTypeRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.cache.CacheService;
import org.crossfit.app.web.rest.api.TimeSlotResource;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for the TimeSlotResource REST controller.
 *
 * @see TimeSlotResource
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
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


    @Inject
    private CrossFitBoxSerivce boxService;
    @Inject
    private CacheService cacheService;
    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private TimeService timeService;
    @Inject
    private TimeSlotExclusionRepository timeSlotExclusionRepository;
    @Inject
    private TimeSlotTypeRepository timeSlotTypeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restTimeSlotMockMvc;

    private TimeSlot timeSlot;

    @Autowired
    private WebApplicationContext wac;
   
    
    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TimeSlotResource timeSlotResource = new TimeSlotResource();
        ReflectionTestUtils.setField(timeSlotResource, "timeService", timeService);
        ReflectionTestUtils.setField(timeSlotResource, "boxService", boxService);
        ReflectionTestUtils.setField(timeSlotResource, "cacheService", cacheService);
        ReflectionTestUtils.setField(timeSlotResource, "timeSlotExclusionRepository", timeSlotExclusionRepository);
        ReflectionTestUtils.setField(timeSlotResource, "timeSlotRepository", timeSlotRepository);
        
        this.restTimeSlotMockMvc = MockMvcBuilders.standaloneSetup(timeSlotResource).setMessageConverters(jacksonMessageConverter).build();
        

        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(wac);
        ServletContextEvent event = new ServletContextEvent(sc);
        listener.contextInitialized(event);
    }

    @Before
    public void initTest() {
        timeSlot = new TimeSlot();
        timeSlot.setRecurrent(TimeSlotRecurrent.DAY_OF_WEEK);
        timeSlot.setDayOfWeek(DEFAULT_DAY_OF_WEEK);
        timeSlot.setName(DEFAULT_NAME);
        timeSlot.setStartTime(DEFAULT_START_TIME);
        timeSlot.setEndTime(DEFAULT_END_TIME);
        timeSlot.setMaxAttendees(DEFAULT_MAX_ATTENDEES);
        timeSlot.setTimeSlotType(timeSlotTypeRepository.findById(1L).get());
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
    }

    @Test
    @Transactional
    public void checkDayOfWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotRepository.findAll().size();
        // set the field null
        timeSlot.setDayOfWeek(null);
        timeSlot.setDate(new DateTime());
        timeSlot.setRecurrent(TimeSlotRecurrent.DAY_OF_WEEK);

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
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotRepository.findAll().size();
        // set the field null
        timeSlot.setDayOfWeek(1);
        timeSlot.setDate(null);
        timeSlot.setRecurrent(TimeSlotRecurrent.DATE);

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

        // Get all the timeSlots
        restTimeSlotMockMvc.perform(get("/api/timeSlots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

}
