package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/api")
public class BookingResource {

    private final Logger log = LoggerFactory.getLogger(BookingResource.class);

    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private MemberRepository memberRepository;

    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/bookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Booking>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Booking> page = bookingRepository.findAllByMember(SecurityUtils.getCurrentMember(), PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookings", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * DELETE  /bookings/:id -> delete the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Booking : {}", id);
		Booking booking = bookingRepository.findOne(id);
		

    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
    		if(booking == null || !booking.getOwner().equals( SecurityUtils.getCurrentMember())){
    			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createAlert("Vous n'êtes pas le propiétaire de cette réservation", "")).body(null);
    		}
    	}
    	
		
        bookingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("booking", id.toString())).build();
    }
    
    

    /**
     * PUT  /bookings/:id/validate -> validate the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}/validate",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> validate(@PathVariable Long id) {
        log.debug("REST request to validate Booking : {}", id);

    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createAlert("Vous n'avez pas les droits necessaires", "")).body(null);
    	}
    	
		Booking booking = bookingRepository.findOne(id);
		booking.setStatus(BookingStatus.VALIDATED);    	
		
        bookingRepository.save(booking);

		return ResponseEntity.ok().build();
    }
    
    /**
     * POST /timeSlots/:id/booking -> save the booking for timeslot
     */

	@RequestMapping(value = "/bookings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Booking> create(@Valid @RequestBody BookingDTO bookingdto) throws URISyntaxException {

		log.debug("REST request to save Booking : {}", bookingdto);
		
    	TimeSlot timeSlot = timeSlotRepository.findOne(bookingdto.getTimeslotId());
    	Member owner = memberRepository.findOne(bookingdto.getOwner().getId());
    	
    	// Si le timeSlot n'existe pas ou si il n'appartient pas à la box
    	if(timeSlot == null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("TimeSlot introuvable", "")).body(null);
        } else if(!timeSlot.getBox().equals(boxService.findCurrentCrossFitBox())){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Le timeSlot n'appartient pas à la box", "")).body(null);
        }
    	
    	// On ajoute l'heure à la date
    	DateTime startAt = bookingdto.getDate().toDateTime(timeSlot.getStartTime(), DateTimeZone.UTC);
    	DateTime endAt = bookingdto.getDate().toDateTime(timeSlot.getEndTime(), DateTimeZone.UTC);
    	
    	
    	Member currentMember = SecurityUtils.getCurrentMember();
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	
    	boolean isSuperUser = SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN);
		if (!isSuperUser && !currentMember.equals(owner)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            		.headers(HeaderUtil.createAlert("Vous n'avez pas le droit de reserver pour quelqu'un d'autre", String.valueOf(timeSlot.getId())))
            		.body(null);
    	}
    	
    	// Si il y a déjà une réservation pour ce créneau
		List<Booking> currentBookings = bookingRepository.findAllBetween(boxService.findCurrentCrossFitBox(), startAt, endAt);
    	if(currentBookings.stream().anyMatch(b->b.getOwner().equals(owner))){
    		return ResponseEntity.badRequest()
            		.headers(HeaderUtil.createAlert("Une réservation existe déjà pour ce créneau", String.valueOf(timeSlot.getId())))
            		.body(null);
    	}
    	
    	if (!isSuperUser && currentBookings.size() >= timeSlot.getMaxAttendees()){
    		return ResponseEntity.badRequest()
            		.headers(HeaderUtil.createAlert("Il n'y a plus de place disponible pour ce créneau", String.valueOf(timeSlot.getId())))
            		.body(null);
    	}
    	
    	if (!isSuperUser && !canUserBook(owner, timeSlot)){
    		return ResponseEntity.badRequest()
            		.headers(HeaderUtil.createAlert("Votre abonnement ne vous permet pas de réserver ce créneau", String.valueOf(timeSlot.getId())))
            		.body(null);
    	}
    	
    	Booking b  = new Booking();
        
    	b.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        b.setCreatedDate(DateTime.now());
        b.setOwner(owner);
        b.setBox(currentCrossFitBox);
        b.setTimeSlotType(timeSlot.getTimeSlotType());
        b.setStartAt(startAt);
        b.setEndAt(endAt);
    	b.setStatus(BookingStatus.VALIDATED);
    	
        Booking result = bookingRepository.save(b);
        
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("booking", result.getId().toString())).body(result);

    }


	private boolean canUserBook(Member owner, TimeSlot timeSlot) {
		//Verifier qu'au moins une de ses souscription lui permet de reserver ce slot
		return true;
	}
    
}
