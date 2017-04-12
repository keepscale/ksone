package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.resources.Resource;
import org.crossfit.app.domain.resources.ResourceBooking;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.resource.ResourceBookingRepository;
import org.crossfit.app.repository.resource.ResourceRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.web.rest.dto.ResourceBookingDTO;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class ResourceBookingResource {

    private final Logger log = LoggerFactory.getLogger(ResourceBookingResource.class);

    @Inject
    private ResourceRepository resourceRepository;

    @Inject
    private ResourceBookingRepository resourceBookingRepository;
    @Inject
    private MemberRepository memberRepository;

	@Inject
	private CrossFitBoxSerivce boxService;
	@Inject
	private TimeService timeService;
	
	

    /**
     * POST /bookings -> save the booking for timeslot
     * @throws NoSubscriptionAvailableException 
     * @throws ManySubscriptionsAvailableException 
     */

	@RequestMapping(value = "/resource-bookings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResourceBookingDTO> create(
			@RequestParam(value = "prepare" , required = false, defaultValue = "false") boolean prepare,
			@Valid @RequestBody ResourceBookingDTO resourceBookingDTO) throws URISyntaxException, NoSubscriptionAvailableException, ManySubscriptionsAvailableException {

		log.debug("REST request to save ResourceBooking : {}", resourceBookingDTO);

    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	
    	Resource selectedResource = resourceRepository.findOne(resourceBookingDTO.getResourceId(), currentCrossFitBox);
    	
    	Member selectedMember = resourceBookingDTO.getMemberId() == null ? SecurityUtils.getCurrentMember() : memberRepository.findOne(resourceBookingDTO.getMemberId());
    	
    	    	
    	// Si le timeSlot n'existe pas ou si il n'appartient pas à la box
    	if(selectedResource == null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Resource introuvable", "")).body(null);
        } else if(!selectedResource.getBox().equals(currentCrossFitBox)){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("La resource n'appartient pas à la box", "")).body(null);
        }
    	
    	boolean isSuperUser = SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN);
		if (!isSuperUser && !SecurityUtils.getCurrentMember().equals(selectedMember)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            		.headers(HeaderUtil.createAlert("Vous n'avez pas le droit de reserver pour quelqu'un d'autre", String.valueOf(selectedResource.getId())))
            		.body(null);
    	}
		

    	if (selectedResource.getRules().stream().noneMatch(rules->{return rules.getMember().equals(selectedMember);})){
    		 return ResponseEntity.status(HttpStatus.FORBIDDEN)
              		.headers(HeaderUtil.createAlert("L'utilisateur " + selectedMember.getId() + " ne fait pas parti des locataires possibles.", String.valueOf(selectedResource.getId())))
              		.body(null);
    	}    	

    	// On ajoute l'heure à la date
    	DateTime startAt = resourceBookingDTO.getDate().toDateTime(resourceBookingDTO.getStartHour().toLocalTime(), timeService.getDateTimeZone(currentCrossFitBox));
    	DateTime endAt = resourceBookingDTO.getDate().toDateTime(resourceBookingDTO.getEndHour().toLocalTime(), timeService.getDateTimeZone(currentCrossFitBox));
    	
    	
    	// Si il y a déjà une réservation entre les date de ce créneau
		List<ResourceBooking> bookingsBetweenStartAndEnd = new ArrayList<>(
				resourceBookingRepository.findAllBetweenExcluded(selectedResource, startAt, endAt));
		
		if (!bookingsBetweenStartAndEnd.isEmpty()){
	        throw new CustomParameterizedException("Il y a déjà des reservations sur cette plage horaire");
		}

		ResourceBooking b = new ResourceBooking();       
        b.setStartAt(startAt);
        b.setEndAt(endAt);
        b.setMember(selectedMember);
        b.setResource(selectedResource);
        		
		ResourceBooking result = resourceBookingRepository.save(b);
		log.debug("Booking sauvegarde: {}", b);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("resourceBooking", result.getId().toString())).body(null);
    }

}
