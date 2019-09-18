package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.config.CacheConfiguration;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.repository.TimeSlotTypeRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.cache.CacheService;
import org.crossfit.app.web.exception.BadRequestException;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/api")
public class TimeSlotTypeResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotTypeResource.class);

    @Inject
    private TimeSlotTypeRepository timeSlotTypeRepository;
    @Inject
    private TimeSlotRepository timeSlotRepository;

	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private CacheService cacheService;
    /**
     * GET  /timeSlotTypes -> get all the timeSlotTypes.
     */
    @RequestMapping(value = "/timeSlotTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TimeSlotType>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        return new ResponseEntity<>(doFindAll(), HttpStatus.OK);
    }

	protected List<TimeSlotType> doFindAll() {
		return timeSlotTypeRepository.findAllByBox(boxService.findCurrentCrossFitBox());
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
		return timeSlotTypeRepository.findById(id).get();
	}

	
	
	 /**
     * POST  /timeSlotTypes -> Create a new timeSlotType.
     */
    @RequestMapping(value = "/timeSlotTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlotType> create(@Valid @RequestBody TimeSlotType timeSlotType) throws URISyntaxException {
        log.debug("REST request to save TimeSlotType : {}", timeSlotType);
        if (timeSlotType.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new TimeSlotType cannot already have an ID").body(null);
        }
		
        TimeSlotType result;
		try {
			result = doSave(timeSlotType);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        
        return ResponseEntity.created(new URI("/api/TimeSlotType/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("timeSlotType", result.getId().toString()))
                .body(result);
    }

	protected TimeSlotType doSave(TimeSlotType timeSlotTypes) throws BadRequestException {
		timeSlotTypes.setBox(boxService.findCurrentCrossFitBox());
		TimeSlotType result = timeSlotTypeRepository.save(timeSlotTypes);
		cacheService.clearCache(CacheConfiguration.PUBLIC_TIMESLOT_CACHE_NAME);
		return result;
	}

    /**
     * PUT  /timeSlotTypes -> Updates an existing timeSlotType.
     */
    @RequestMapping(value = "/timeSlotTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlotType> update(@Valid @RequestBody TimeSlotType timeSlotType) throws URISyntaxException {
        log.debug("REST request to update TimeSlotType : {}", timeSlotType);
        if (timeSlotType.getId() == null) {
            return create(timeSlotType);
        }
        TimeSlotType result;
		try {
			result = doSave(timeSlotType);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("timeSlotType", timeSlotType.getId().toString()))
                .body(result);
    }
    

	/**
	 * DELETE /timeSlotTypes/:id -> delete the "id" timeSlotType.
	 */
	@RequestMapping(value = "/timeSlotTypes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete TimeSlotTypes : {}", id);
		
		
		TimeSlotType timeSlotTypeToDel = doGet(id);
		if (timeSlotTypeToDel.getBox().equals(boxService.findCurrentCrossFitBox())){
			List<TimeSlot> slots = timeSlotRepository.findAllByTimeSlotType(timeSlotTypeToDel);
			if (slots.isEmpty()){
				timeSlotTypeRepository.deleteById(id);
				cacheService.clearCache(CacheConfiguration.PUBLIC_TIMESLOT_CACHE_NAME);
			}
			else{
	    		throw new CustomParameterizedException("Il existe des créneaux utilisant ce type");
			}
		}
		else{
			ResponseEntity.status(HttpStatus.FORBIDDEN);
		}
		
		
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timeSlotType", id.toString()))
				.build();
	}

}
