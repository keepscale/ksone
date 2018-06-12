package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.repository.CrossFitBoxRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing CrossFitBox.
 */
@RestController
@RequestMapping("/api")
public class CrossFitBoxResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxResource.class);

    @Inject
    private CrossFitBoxRepository crossFitBoxRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    
    /**
     * POST  /boxs -> Create a new crossFitBox.
     */
    @RequestMapping(value = "/boxs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CrossFitBox> create(@Valid @RequestBody CrossFitBox crossFitBox) throws URISyntaxException {
        log.debug("REST request to save CrossFitBox : {}", crossFitBox);
        if (crossFitBox.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new crossFitBox cannot already have an ID").body(null);
        }
        CrossFitBox result = crossFitBoxRepository.save(crossFitBox);
        return ResponseEntity.created(new URI("/api/boxs/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("crossFitBox", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /boxs -> Updates an existing crossFitBox.
     */
    @RequestMapping(value = "/boxs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
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
     * GET  /boxs -> get all the crossFitBoxs.
     */
    @RequestMapping(value = "/boxs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CrossFitBox>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<CrossFitBox> page = crossFitBoxRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/boxs", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /boxs/:id -> get the "id" crossFitBox.
     */
    @RequestMapping(value = "/boxs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CrossFitBox> get(@PathVariable Long id) {
        log.debug("REST request to get CrossFitBox : {}", id);
        return Optional.ofNullable(crossFitBoxRepository.findById(id).get())
            .map(crossFitBox -> new ResponseEntity<>(
                crossFitBox,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /boxs/:id -> delete the "id" crossFitBox.
     */
    @RequestMapping(value = "/boxs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete CrossFitBox : {}", id);
        crossFitBoxRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("crossFitBox", id.toString())).build();
    }
    
    /**
     * GET  /boxs/:id -> get the "id" crossFitBox.
     */
    @RequestMapping(value = "/boxs/current",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CrossFitBox> current() {
        log.debug("REST request to get Current CrossFitBox");
        return Optional.ofNullable(boxService.findCurrentCrossFitBox())
            .map(crossFitBox -> new ResponseEntity<>(
                crossFitBox,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
