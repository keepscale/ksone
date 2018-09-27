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
     * PUT  /boxs -> Updates an existing crossFitBox.
     */
    @RequestMapping(value = "/boxs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CrossFitBox> update(@Valid @RequestBody CrossFitBox crossFitBox) throws URISyntaxException {
        log.debug("REST request to update CrossFitBox : {}", crossFitBox);
        if (crossFitBox.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        CrossFitBox result = crossFitBoxRepository.save(crossFitBox);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("crossFitBox", crossFitBox.getId().toString()))
                .body(result);
    }
    
    /**
     * GET  /boxs/current -> get the "id" crossFitBox.
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
