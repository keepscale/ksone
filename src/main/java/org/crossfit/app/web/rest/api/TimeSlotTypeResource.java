package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.repository.TimeSlotTypeRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/api")
public class TimeSlotTypeResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotTypeResource.class);

    @Inject
    private TimeSlotTypeRepository timeSlotTypeRepository;

	@Inject
	private CrossFitBoxSerivce boxService;
    /**
     * GET  /timeSlotTypes -> get all the timeSlotTypes.
     */
    @RequestMapping(value = "/timeSlotTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TimeSlotType>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<TimeSlotType> page = doFindAll(offset, limit);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeSlotTypes", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

	protected Page<TimeSlotType> doFindAll(Integer offset, Integer limit) {
		Page<TimeSlotType> page = timeSlotTypeRepository.findAllByBox(boxService.findCurrentCrossFitBox(), PaginationUtil.generatePageRequest(offset, limit));
		return page;
	}

    /**
     * GET  /timeSlotTypes/:id -> get the "id" timeSlotTypes.
     */
    @RequestMapping(value = "/timeSlotTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlotType> get(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(timeSlot -> new ResponseEntity<>(
                timeSlot,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected TimeSlotType doGet(Long id) {
		return timeSlotTypeRepository.findOne(id);
	}

}
