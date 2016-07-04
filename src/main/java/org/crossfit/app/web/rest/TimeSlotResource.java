package org.crossfit.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.web.exception.BadRequestException;
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
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/api")
public class TimeSlotResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotResource.class);

    @Inject
    private TimeSlotRepository timeSlotRepository;

    /**
     * POST  /timeSlots -> Create a new timeSlot.
     */
    @RequestMapping(value = "/timeSlots",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> create(@Valid @RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to save TimeSlot : {}", timeSlot);
        if (timeSlot.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new timeSlot cannot already have an ID").body(null);
        }
		
        TimeSlot result;
		try {
			result = doSave(timeSlot);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        
        return ResponseEntity.created(new URI("/api/timeSlots/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("timeSlot", result.getId().toString()))
                .body(result);
    }

	protected TimeSlot doSave(TimeSlot timeSlot) throws BadRequestException {

		if (timeSlot.getRecurrent() == TimeSlotRecurrent.DATE){
			timeSlot.setDayOfWeek(null);
		}
		else if (timeSlot.getRecurrent() == TimeSlotRecurrent.DAY_OF_WEEK){
			timeSlot.setDate(null);
		}		
		if (timeSlot.getDayOfWeek() == null && timeSlot.getDate() == null){
			throw new BadRequestException("A new timeslot must have a date or a day of week");
		}
		
		TimeSlot result = timeSlotRepository.save(timeSlot);
		return result;
	}

    /**
     * PUT  /timeSlots -> Updates an existing timeSlot.
     */
    @RequestMapping(value = "/timeSlots",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> update(@Valid @RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to update TimeSlot : {}", timeSlot);
        if (timeSlot.getId() == null) {
            return create(timeSlot);
        }
        TimeSlot result;
		try {
			result = doSave(timeSlot);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("timeSlot", timeSlot.getId().toString()))
                .body(result);
    }

    /**
     * GET  /timeSlots -> get all the timeSlots.
     */
    @RequestMapping(value = "/timeSlots",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TimeSlot>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<TimeSlot> page = doFindAll(offset, limit);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeSlots", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

	protected Page<TimeSlot> doFindAll(Integer offset, Integer limit) {
		Page<TimeSlot> page = timeSlotRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		return page;
	}

    /**
     * GET  /timeSlots/:id -> get the "id" timeSlot.
     */
    @RequestMapping(value = "/timeSlots/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TimeSlot> get(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(timeSlot -> new ResponseEntity<>(
                timeSlot,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected TimeSlot doGet(Long id) {
		return timeSlotRepository.findOne(id);
	}

    /**
     * DELETE  /timeSlots/:id -> delete the "id" timeSlot.
     */
    @RequestMapping(value = "/timeSlots/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete TimeSlot : {}", id);
        doDelete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timeSlot", id.toString())).build();
    }

	protected void doDelete(Long id) {
		timeSlotRepository.delete(id);
	}
}
