package org.crossfit.app.web.rest;

import org.crossfit.app.Application;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FileDocumentResource REST controller.
 *
 * @see FileDocumentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FileDocumentResourceTest {

    private static final String DEFAULT_UUID = "SAMPLE_TEXT";
    private static final String UPDATED_UUID = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(5000000, "1");

    @Inject
    private FileDocumentRepository fileDocumentRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restFileDocumentMockMvc;

    private FileDocument fileDocument;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FileDocumentResource fileDocumentResource = new FileDocumentResource();
        ReflectionTestUtils.setField(fileDocumentResource, "fileDocumentRepository", fileDocumentRepository);
        this.restFileDocumentMockMvc = MockMvcBuilders.standaloneSetup(fileDocumentResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        fileDocument = new FileDocument();
        fileDocument.setUuid(DEFAULT_UUID);
        fileDocument.setName(DEFAULT_NAME);
        fileDocument.setContent(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createFileDocument() throws Exception {
        int databaseSizeBeforeCreate = fileDocumentRepository.findAll().size();

        // Create the FileDocument

        restFileDocumentMockMvc.perform(post("/api/fileDocuments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDocument)))
                .andExpect(status().isCreated());

        // Validate the FileDocument in the database
        List<FileDocument> fileDocuments = fileDocumentRepository.findAll();
        assertThat(fileDocuments).hasSize(databaseSizeBeforeCreate + 1);
        FileDocument testFileDocument = fileDocuments.get(fileDocuments.size() - 1);
        assertThat(testFileDocument.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testFileDocument.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFileDocument.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileDocumentRepository.findAll().size();
        // set the field null
        fileDocument.setName(null);

        // Create the FileDocument, which fails.

        restFileDocumentMockMvc.perform(post("/api/fileDocuments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDocument)))
                .andExpect(status().isBadRequest());

        List<FileDocument> fileDocuments = fileDocumentRepository.findAll();
        assertThat(fileDocuments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = fileDocumentRepository.findAll().size();
        // set the field null
        fileDocument.setContent(null);

        // Create the FileDocument, which fails.

        restFileDocumentMockMvc.perform(post("/api/fileDocuments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDocument)))
                .andExpect(status().isBadRequest());

        List<FileDocument> fileDocuments = fileDocumentRepository.findAll();
        assertThat(fileDocuments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFileDocuments() throws Exception {
        // Initialize the database
        fileDocumentRepository.saveAndFlush(fileDocument);

        // Get all the fileDocuments
        restFileDocumentMockMvc.perform(get("/api/fileDocuments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(fileDocument.getId().intValue())))
                .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }

    @Test
    @Transactional
    public void getFileDocument() throws Exception {
        // Initialize the database
        fileDocumentRepository.saveAndFlush(fileDocument);

        // Get the fileDocument
        restFileDocumentMockMvc.perform(get("/api/fileDocuments/{id}", fileDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(fileDocument.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    public void getNonExistingFileDocument() throws Exception {
        // Get the fileDocument
        restFileDocumentMockMvc.perform(get("/api/fileDocuments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFileDocument() throws Exception {
        // Initialize the database
        fileDocumentRepository.saveAndFlush(fileDocument);

		int databaseSizeBeforeUpdate = fileDocumentRepository.findAll().size();

        // Update the fileDocument
        fileDocument.setUuid(UPDATED_UUID);
        fileDocument.setName(UPDATED_NAME);
        fileDocument.setContent(UPDATED_CONTENT);
        

        restFileDocumentMockMvc.perform(put("/api/fileDocuments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(fileDocument)))
                .andExpect(status().isOk());

        // Validate the FileDocument in the database
        List<FileDocument> fileDocuments = fileDocumentRepository.findAll();
        assertThat(fileDocuments).hasSize(databaseSizeBeforeUpdate);
        FileDocument testFileDocument = fileDocuments.get(fileDocuments.size() - 1);
        assertThat(testFileDocument.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testFileDocument.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFileDocument.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void deleteFileDocument() throws Exception {
        // Initialize the database
        fileDocumentRepository.saveAndFlush(fileDocument);

		int databaseSizeBeforeDelete = fileDocumentRepository.findAll().size();

        // Get the fileDocument
        restFileDocumentMockMvc.perform(delete("/api/fileDocuments/{id}", fileDocument.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<FileDocument> fileDocuments = fileDocumentRepository.findAll();
        assertThat(fileDocuments).hasSize(databaseSizeBeforeDelete - 1);
    }
}
