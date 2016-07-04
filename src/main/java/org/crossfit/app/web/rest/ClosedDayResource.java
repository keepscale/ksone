package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing ClosedDay.
 */
@RestController
@RequestMapping("/api")
public class ClosedDayResource {

    private final Logger log = LoggerFactory.getLogger(ClosedDayResource.class);

    @Inject
    private ClosedDayRepository closedDayRepository;

    /**
     * POST  /closedDays -> Create a new closedDay.
     */
    @RequestMapping(value = "/closedDays",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClosedDay> create(@Valid @RequestBody ClosedDay closedDay) throws URISyntaxException {
        log.debug("REST request to save ClosedDay : {}", closedDay);
        if (closedDay.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new closedDay cannot already have an ID").body(null);
        }
        ClosedDay result = doSave(closedDay);
        return ResponseEntity.created(new URI("/api/closedDays/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("closedDay", result.getId().toString()))
                .body(result);
    }

	protected ClosedDay doSave(ClosedDay closedDay) {
		ClosedDay result = closedDayRepository.save(closedDay);
		return result;
	}

    /**
     * PUT  /closedDays -> Updates an existing closedDay.
     */
    @RequestMapping(value = "/closedDays",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClosedDay> update(@Valid @RequestBody ClosedDay closedDay) throws URISyntaxException {
        log.debug("REST request to update ClosedDay : {}", closedDay);
        if (closedDay.getId() == null) {
            return create(closedDay);
        }
        ClosedDay result = doSave(closedDay);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("closedDay", closedDay.getId().toString()))
                .body(result);
    }

    /**
     * GET  /closedDays -> get all the closedDays.
     */
    @RequestMapping(value = "/closedDays",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ClosedDay> getAll() {
        log.debug("REST request to get all ClosedDays");
        return doFindAll();
    }

	protected List<ClosedDay> doFindAll() {
		return closedDayRepository.findAll();
	}

    /**
     * GET  /closedDays/:id -> get the "id" closedDay.
     */
    @RequestMapping(value = "/closedDays/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ClosedDay> get(@PathVariable Long id) {
        log.debug("REST request to get ClosedDay : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(closedDay -> new ResponseEntity<>(
                closedDay,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected ClosedDay doGet(Long id) {
		return closedDayRepository.findOne(id);
	}

    /**
     * DELETE  /closedDays/:id -> delete the "id" closedDay.
     */
    @RequestMapping(value = "/closedDays/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete ClosedDay : {}", id);
        doDelete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("closedDay", id.toString())).build();
    }

	protected void doDelete(Long id) {
		closedDayRepository.delete(id);
	}
}
