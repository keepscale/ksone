package org.crossfit.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.crossfit.app.Application;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.web.rest.api.MembershipTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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


    @Inject
    private MembershipRepository membershipTypeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restMembershipTypeMockMvc;

    private Membership membershipType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MembershipTypeResource membershipTypeResource = new MembershipTypeResource();
        ReflectionTestUtils.setField(membershipTypeResource, "membershipTypeRepository", membershipTypeRepository);
        this.restMembershipTypeMockMvc = MockMvcBuilders.standaloneSetup(membershipTypeResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        membershipType = new Membership();
        membershipType.setName(DEFAULT_NAME);
        membershipType.setPrice(DEFAULT_PRICE);
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
        List<Membership> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeCreate + 1);
        Membership testMembershipType = membershipTypes.get(membershipTypes.size() - 1);
        assertThat(testMembershipType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMembershipType.getPrice()).isEqualTo(DEFAULT_PRICE);
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

        List<Membership> membershipTypes = membershipTypeRepository.findAll();
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

        List<Membership> membershipTypes = membershipTypeRepository.findAll();
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
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.toString())));
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
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.toString()));
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
        

        restMembershipTypeMockMvc.perform(put("/api/membershipTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(membershipType)))
                .andExpect(status().isOk());

        // Validate the MembershipType in the database
        List<Membership> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeUpdate);
        Membership testMembershipType = membershipTypes.get(membershipTypes.size() - 1);
        assertThat(testMembershipType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMembershipType.getPrice()).isEqualTo(UPDATED_PRICE);
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
        List<Membership> membershipTypes = membershipTypeRepository.findAll();
        assertThat(membershipTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
