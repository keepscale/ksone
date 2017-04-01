package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.resources.Resource;
import org.crossfit.app.domain.resources.ResourceBooking;
import org.crossfit.app.repository.resource.ResourceBookingRepository;
import org.crossfit.app.repository.resource.ResourceRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
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
public class ResourceResource {

    private final Logger log = LoggerFactory.getLogger(ResourceResource.class);

    @Inject
    private ResourceRepository resourceRepository;
    @Inject
    private ResourceBookingRepository resourceBookingRepository;

	@Inject
	private CrossFitBoxSerivce boxService;
	
    /**
     * GET  /timeSlotTypes -> get all the timeSlotTypes.
     */
    @RequestMapping(value = "/resources",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Resource>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        return new ResponseEntity<>(doFindAll(), HttpStatus.OK);
    }

	protected List<Resource> doFindAll() {
		return resourceRepository.findAllByBox(boxService.findCurrentCrossFitBox());
	}

    /**
     * GET  /timeSlotTypes/:id -> get the "id" timeSlotTypes.
     */
    @RequestMapping(value = "/resources/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> get(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(timeSlot -> new ResponseEntity<>(
                timeSlot,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected Resource doGet(Long id) {
		return resourceRepository.findOne(id);
	}

	
	
	 /**
     * POST  /timeSlotTypes -> Create a new resource.
     */
    @RequestMapping(value = "/resources",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> create(@Valid @RequestBody Resource resource) throws URISyntaxException {
        log.debug("REST request to save Resource : {}", resource);
        if (resource.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new Resource cannot already have an ID").body(null);
        }
		
        Resource result;
		try {
			result = doSave(resource);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        
        return ResponseEntity.created(new URI("/api/Resource/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("resource", result.getId().toString()))
                .body(result);
    }

	protected Resource doSave(Resource resource) throws BadRequestException {
		resource.setBox(boxService.findCurrentCrossFitBox());
		Resource result = resourceRepository.save(resource);
		return result;
	}

    /**
     * PUT  /timeSlotTypes -> Updates an existing resource.
     */
    @RequestMapping(value = "/resources",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> update(@Valid @RequestBody Resource resource) throws URISyntaxException {
        log.debug("REST request to update Resource : {}", resource);
        if (resource.getId() == null) {
            return create(resource);
        }
        Resource result;
		try {
			result = doSave(resource);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("resource", resource.getId().toString()))
                .body(result);
    }
    
	/**
	 * DELETE /timeSlotTypes/:id -> delete the "id" timeSlotType.
	 */
	@RequestMapping(value = "/resources/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Resource : {}", id);
		
		
		Resource resourceToDel = doGet(id);
		if (resourceToDel.getBox().equals(boxService.findCurrentCrossFitBox())){
			List<ResourceBooking> resourceBookins = resourceBookingRepository.findAllByResource(resourceToDel);
			if (resourceBookins.isEmpty()){
				resourceRepository.delete(id);
			}
			else{
	    		throw new CustomParameterizedException("Il existe des réservation utilisant cette resource " + resourceToDel.getName());
			}
		}
		else{
			ResponseEntity.status(HttpStatus.FORBIDDEN);
		}
		
		
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timeSlotType", id.toString()))
				.build();
	}

}
