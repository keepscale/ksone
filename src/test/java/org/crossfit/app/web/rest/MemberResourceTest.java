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
import org.crossfit.app.domain.Member;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.web.rest.api.MemberResource;
import org.joda.time.LocalDate;
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
 * Test class for the MemberResource REST controller.
 *
 * @see MemberResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MemberResourceTest {

    private static final String DEFAULT_TELEPHON_NUMBER = "SAMPLE_TEXT";
    private static final String UPDATED_TELEPHON_NUMBER = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_SICK_NOTE_END_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_SICK_NOTE_END_DATE = new LocalDate();

    private static final LocalDate DEFAULT_MEMBERSHIP_START_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_MEMBERSHIP_START_DATE = new LocalDate();

    private static final LocalDate DEFAULT_MEMBERSHIP_END_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_MEMBERSHIP_END_DATE = new LocalDate();

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restMemberMockMvc;

    private Member member;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MemberResource memberResource = new MemberResource();
        ReflectionTestUtils.setField(memberResource, "memberRepository", memberRepository);
        this.restMemberMockMvc = MockMvcBuilders.standaloneSetup(memberResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        member = new Member();
        member.setTelephonNumber(DEFAULT_TELEPHON_NUMBER);
    }

    @Test
    @Transactional
    public void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // Create the Member

        restMemberMockMvc.perform(post("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isCreated());

        // Validate the Member in the database
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = members.get(members.size() - 1);
        assertThat(testMember.getTelephonNumber()).isEqualTo(DEFAULT_TELEPHON_NUMBER);
    }

    @Test
    @Transactional
    public void checkMembershipStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null

        // Create the Member, which fails.

        restMemberMockMvc.perform(post("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isBadRequest());

        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null

        // Create the Member, which fails.

        restMemberMockMvc.perform(post("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isBadRequest());

        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMembers() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the members
        restMemberMockMvc.perform(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())))
                .andExpect(jsonPath("$.[*].telephonNumber").value(hasItem(DEFAULT_TELEPHON_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].sickNoteEndDate").value(hasItem(DEFAULT_SICK_NOTE_END_DATE.toString())))
                .andExpect(jsonPath("$.[*].membershipStartDate").value(hasItem(DEFAULT_MEMBERSHIP_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].membershipEndDate").value(hasItem(DEFAULT_MEMBERSHIP_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void getMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", member.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(member.getId().intValue()))
            .andExpect(jsonPath("$.telephonNumber").value(DEFAULT_TELEPHON_NUMBER.toString()))
            .andExpect(jsonPath("$.sickNoteEndDate").value(DEFAULT_SICK_NOTE_END_DATE.toString()))
            .andExpect(jsonPath("$.membershipStartDate").value(DEFAULT_MEMBERSHIP_START_DATE.toString()))
            .andExpect(jsonPath("$.membershipEndDate").value(DEFAULT_MEMBERSHIP_END_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMember() throws Exception {
        // Get the member
        restMemberMockMvc.perform(get("/api/members/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

		int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member
        member.setTelephonNumber(UPDATED_TELEPHON_NUMBER);
        

        restMemberMockMvc.perform(put("/api/members")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(member)))
                .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeUpdate);
        Member testMember = members.get(members.size() - 1);
        assertThat(testMember.getTelephonNumber()).isEqualTo(UPDATED_TELEPHON_NUMBER);
    }

    @Test
    @Transactional
    public void deleteMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

		int databaseSizeBeforeDelete = memberRepository.findAll().size();

        // Get the member
        restMemberMockMvc.perform(delete("/api/members/{id}", member.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(databaseSizeBeforeDelete - 1);
    }
}
