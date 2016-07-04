package org.crossfit.app.web.rest;

import org.crossfit.app.Application;
import org.crossfit.app.domain.MembershipType;
import org.crossfit.app.repository.MembershipTypeRepository;

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
 * Test class for the MembershipTypeResource REST controller.
 *
 * @see MembershipTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MembershipTypeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_PRICE = "SAMPLE_TEXT";
    private static final String UPDATED_PRICE = "UPDATED_TEXT";

    private static final Boolean DEFAULT_OPEN_ACCESS = false;
    private static final Boolean UPDATED_OPEN_ACCESS = true;

    private static final Integer DEFAULT_NUMBER_OF_SESSION = 100;
    private static final Integer UPDATED_NUMBER_OF_SESSION = 99;

    private static final Integer DEFAULT_NUMBER_OF_SESSION_PER_WEEK = 20;
    private static final Integer UPDATED_NUMBER_OF_SESSION_PER_WEEK = 19;

    @Inject
    private MembershipTypeRepository membershipTypeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restMembershipTypeMockMvc;

    private MembershipType membershipType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MembershipTypeResource membershipTypeResource = new MembershipTypeResource();
        ReflectionTestUtils.setField(membershipTypeResource, "membershipTypeRepository", membershipTypeRepository);
        this.restMembershipTypeMockMvc = MockMvcBuilders.standaloneSetup(membershipTypeResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        membershipType = new MembershipType();
        membershipType.setName(DEFAULT_NAME);
        membershipType.setPrice(DEFAULT_PRICE);
        membershipType.setOpenAccess(DEFAULT_OPEN_ACCESS);
        membershipType.setNumberOfSession(DEFAULT_NUMBER_OF_SESSION);
        membershipType.setNumberOfSessionPerWeek(DEFAULT_NUMBER_OF_SESSION_PER_WEEK);
    }

    @Test
    @Transactional
    public void createMembershipType() throws Exception {
        int databaseSizeBeforeCreate = membershipTypeRepository.findAll().size();

        // Create the MembershipType

        restMembershipTypeMockMvc.perform(post("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isCreated());

        // Validate the MembershipType in the database
        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeCreate + 1);
        MembershipType testMembershipType = membershipTypes.get(membershipTypes.size() - 1);
        assertThat(testMembershipType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMembershipType.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testMembershipType.getOpenAccess()).isEqualTo(DEFAULT_OPEN_ACCESS);
        assertThat(testMembershipType.getNumberOfSession()).isEqualTo(DEFAULT_NUMBER_OF_SESSION);
        assertThat(testMembershipType.getNumberOfSessionPerWeek()).isEqualTo(DEFAULT_NUMBER_OF_SESSION_PER_WEEK);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = membershipTypeRepository.findAll().size();
        // set the field null
        membershipType.setName(null);

        // Create the MembershipType, which fails.

        restMembershipTypeMockMvc.perform(post("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isBadRequest());

        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = membershipTypeRepository.findAll().size();
        // set the field null
        membershipType.setPrice(null);

        // Create the MembershipType, which fails.

        restMembershipTypeMockMvc.perform(post("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isBadRequest());

        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNumberOfSessionPerWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = membershipTypeRepository.findAll().size();
        // set the field null
        membershipType.setNumberOfSessionPerWeek(null);

        // Create the MembershipType, which fails.

        restMembershipTypeMockMvc.perform(post("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isBadRequest());

        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMembershipTypes() throws Exception {
        // Initialize the database
        membershipTypeRepository.saveAndFlush(membershipType);

        // Get all the membershipTypes
        restMembershipTypeMockMvc.perform(get("/api/membershipTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(membershipType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toString())))
                .andExpect(jsonPath("$.[*].openAccess").value(hasItem(DEFAULT_OPEN_ACCESS.booleanValue())))
                .andExpect(jsonPath("$.[*].numberOfSession").value(hasItem(DEFAULT_NUMBER_OF_SESSION)))
                .andExpect(jsonPath("$.[*].numberOfSessionPerWeek").value(hasItem(DEFAULT_NUMBER_OF_SESSION_PER_WEEK)));
    }

    @Test
    @Transactional
    public void getMembershipType() throws Exception {
        // Initialize the database
        membershipTypeRepository.saveAndFlush(membershipType);

        // Get the membershipType
        restMembershipTypeMockMvc.perform(get("/api/membershipTypes/{id}", membershipType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(membershipType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.toString()))
            .andExpect(jsonPath("$.openAccess").value(DEFAULT_OPEN_ACCESS.booleanValue()))
            .andExpect(jsonPath("$.numberOfSession").value(DEFAULT_NUMBER_OF_SESSION))
            .andExpect(jsonPath("$.numberOfSessionPerWeek").value(DEFAULT_NUMBER_OF_SESSION_PER_WEEK));
    }

    @Test
    @Transactional
    public void getNonExistingMembershipType() throws Exception {
        // Get the membershipType
        restMembershipTypeMockMvc.perform(get("/api/membershipTypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMembershipType() throws Exception {
        // Initialize the database
        membershipTypeRepository.saveAndFlush(membershipType);

		int databaseSizeBeforeUpdate = membershipTypeRepository.findAll().size();

        // Update the membershipType
        membershipType.setName(UPDATED_NAME);
        membershipType.setPrice(UPDATED_PRICE);
        membershipType.setOpenAccess(UPDATED_OPEN_ACCESS);
        membershipType.setNumberOfSession(UPDATED_NUMBER_OF_SESSION);
        membershipType.setNumberOfSessionPerWeek(UPDATED_NUMBER_OF_SESSION_PER_WEEK);
        

        restMembershipTypeMockMvc.perform(put("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isOk());

        // Validate the MembershipType in the database
        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeUpdate);
        MembershipType testMembershipType = membershipTypes.get(membershipTypes.size() - 1);
        assertThat(testMembershipType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMembershipType.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testMembershipType.getOpenAccess()).isEqualTo(UPDATED_OPEN_ACCESS);
        assertThat(testMembershipType.getNumberOfSession()).isEqualTo(UPDATED_NUMBER_OF_SESSION);
        assertThat(testMembershipType.getNumberOfSessionPerWeek()).isEqualTo(UPDATED_NUMBER_OF_SESSION_PER_WEEK);
    }

    @Test
    @Transactional
    public void deleteMembershipType() throws Exception {
        // Initialize the database
        membershipTypeRepository.saveAndFlush(membershipType);

		int databaseSizeBeforeDelete = membershipTypeRepository.findAll().size();

        // Get the membershipType
        restMembershipTypeMockMvc.perform(delete("/api/membershipTypes/{id}", membershipType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<MembershipType> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
