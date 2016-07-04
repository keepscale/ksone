package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.repository.CrossFitBoxRepository;
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
 * REST controller for managing CrossFitBox.
 */
@RestController
@RequestMapping("/api")
public class CrossFitBoxResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxResource.class);

    @Inject
    private CrossFitBoxRepository crossFitBoxRepository;

    /**
     * POST  /crossFitBoxs -> Create a new crossFitBox.
     */
    @RequestMapping(value = "/crossFitBoxs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrossFitBox> create(@Valid @RequestBody CrossFitBox crossFitBox) throws URISyntaxException {
        log.debug("REST request to save CrossFitBox : {}", crossFitBox);
        if (crossFitBox.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new crossFitBox cannot already have an ID").body(null);
        }
        CrossFitBox result = crossFitBoxRepository.save(crossFitBox);
        return ResponseEntity.created(new URI("/api/crossFitBoxs/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("crossFitBox", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /crossFitBoxs -> Updates an existing crossFitBox.
     */
    @RequestMapping(value = "/crossFitBoxs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrossFitBox> update(@Valid @RequestBody CrossFitBox crossFitBox) throws URISyntaxException {
        log.debug("REST request to update CrossFitBox : {}", crossFitBox);
        if (crossFitBox.getId() == null) {
            return create(crossFitBox);
        }
        CrossFitBox result = crossFitBoxRepository.save(crossFitBox);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("crossFitBox", crossFitBox.getId().toString()))
                .body(result);
    }

    /**
     * GET  /crossFitBoxs -> get all the crossFitBoxs.
     */
    @RequestMapping(value = "/crossFitBoxs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CrossFitBox>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<CrossFitBox> page = crossFitBoxRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/crossFitBoxs", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /crossFitBoxs/:id -> get the "id" crossFitBox.
     */
    @RequestMapping(value = "/crossFitBoxs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CrossFitBox> get(@PathVariable Long id) {
        log.debug("REST request to get CrossFitBox : {}", id);
        return Optional.ofNullable(crossFitBoxRepository.findOneWithEagerRelationships(id))
            .map(crossFitBox -> new ResponseEntity<>(
                crossFitBox,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /crossFitBoxs/:id -> delete the "id" crossFitBox.
     */
    @RequestMapping(value = "/crossFitBoxs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete CrossFitBox : {}", id);
        crossFitBoxRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("crossFitBox", id.toString())).build();
    }
}
