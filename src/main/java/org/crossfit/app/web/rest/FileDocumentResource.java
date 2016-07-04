package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing FileDocument.
 */
@RestController
@RequestMapping("/api")
public class FileDocumentResource {

    private final Logger log = LoggerFactory.getLogger(FileDocumentResource.class);

    @Inject
    private FileDocumentRepository fileDocumentRepository;

    /**
     * POST  /fileDocuments -> Create a new fileDocument.
     */
    @RequestMapping(value = "/fileDocuments",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileDocument> create(@Valid @RequestBody FileDocument fileDocument) throws URISyntaxException {
        log.debug("REST request to save FileDocument : {}", fileDocument);
        if (fileDocument.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new fileDocument cannot already have an ID").body(null);
        }
        FileDocument result = fileDocumentRepository.save(fileDocument);
        return ResponseEntity.created(new URI("/api/fileDocuments/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("fileDocument", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /fileDocuments -> Updates an existing fileDocument.
     */
    @RequestMapping(value = "/fileDocuments",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileDocument> update(@Valid @RequestBody FileDocument fileDocument) throws URISyntaxException {
        log.debug("REST request to update FileDocument : {}", fileDocument);
        if (fileDocument.getId() == null) {
            return create(fileDocument);
        }
        FileDocument result = fileDocumentRepository.save(fileDocument);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("fileDocument", fileDocument.getId().toString()))
                .body(result);
    }

    /**
     * GET  /fileDocuments -> get all the fileDocuments.
     */
    @RequestMapping(value = "/fileDocuments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<FileDocument>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<FileDocument> page = fileDocumentRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/fileDocuments", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /fileDocuments/:id -> get the "id" fileDocument.
     */
    @RequestMapping(value = "/fileDocuments/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FileDocument> get(@PathVariable Long id) {
        log.debug("REST request to get FileDocument : {}", id);
        return Optional.ofNullable(fileDocumentRepository.findOne(id))
            .map(fileDocument -> new ResponseEntity<>(
                fileDocument,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /fileDocuments/:id -> delete the "id" fileDocument.
     */
    @RequestMapping(value = "/fileDocuments/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete FileDocument : {}", id);
        fileDocumentRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("fileDocument", id.toString())).build();
    }
}
