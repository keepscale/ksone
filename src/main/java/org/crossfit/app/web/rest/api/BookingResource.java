package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Membership;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.MembershipRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.util.BookingRulesChecker;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
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
    @Inject
    private MembershipRepository membershipRepository;
    @Inject
	private SubscriptionRepository subscriptionRepository;


    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/bookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Booking>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
    	
    	Page<Booking> page = null; 
    	
    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
    		page = bookingRepository.findAllByMember(SecurityUtils.getCurrentMember(), PaginationUtil.generatePageRequest(offset, limit));
    	}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookings", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * GET  /bookings/:id -> get the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Booking> get(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
		Booking booking = bookingRepository.findOne(id);
		
		if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
    		if(booking == null || !booking.getSubscription().getMember().equals( SecurityUtils.getCurrentMember())){
    			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createAlert("Vous n'êtes pas le propiétaire de cette réservation", "")).body(null);
    		}
    	}
    	
    	return Optional.ofNullable(booking)
                .map(book -> new ResponseEntity<>(
                    book,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
    		if(booking == null || !booking.getSubscription().getMember().equals( SecurityUtils.getCurrentMember())){
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
     * @throws NoSubscriptionAvailableException 
     */

	@RequestMapping(value = "/bookings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Booking> create(@Valid @RequestBody BookingDTO bookingdto) throws URISyntaxException, NoSubscriptionAvailableException {

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
		List<Booking> currentBookings = new ArrayList<>(
				bookingRepository.findAllBetween(boxService.findCurrentCrossFitBox(), startAt, endAt));
		Optional<Booking> alreadyBooked = currentBookings.stream().filter(b->b.getSubscription().getMember().equals(owner)).findAny();
    	if(alreadyBooked.isPresent()){
    		throw new CustomParameterizedException("Une réservation existe déjà pour ce créneau et ce membre");
    	}
    	
    	if (!isSuperUser && currentBookings.size() >= timeSlot.getMaxAttendees()){
    		throw new CustomParameterizedException("Il n'y a plus de place disponible pour ce créneau");
    	}
    	
    	if (!isSuperUser /*&& !canUserBook(owner, timeSlot)*/){
    		throw new CustomParameterizedException("Votre abonnement ne vous permet pas de réserver ce créneau");
    	}
    	
    	Booking b  = new Booking();
        
    	b.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        b.setCreatedDate(DateTime.now());
        BookingRulesChecker rules = new BookingRulesChecker(
        		bookingRepository.findAllByMember(owner), 
        		subscriptionRepository.findAllByMember(owner));

        Subscription subscription = null;
        
        try {
        	
        	subscription = rules.findSubscription(owner, timeSlot.getTimeSlotType(), startAt);
			
		} catch (ManySubscriptionsAvailableException e) {
			subscription = e.getSubscriptions().get(0);
		} catch (NoSubscriptionAvailableException e) {
			if (isSuperUser){
				List<Membership> findAllByDefault = membershipRepository.findAllByDefault(currentCrossFitBox);
				for (Membership membership : findAllByDefault) {
					Subscription s = new Subscription();
					s.setMember(owner);
					s.setMembership(membership);
					s.setSubscriptionStartDate(startAt.toLocalDate());
					s.setSubscriptionEndDate(startAt.toLocalDate().plusYears(1));
					subscription = subscriptionRepository.save(s);
				}
			}
			else{
				throw e;
			}
		}
        
		b.setSubscription(subscription);
        b.setBox(currentCrossFitBox);
        b.setTimeSlotType(timeSlot.getTimeSlotType());
        b.setStartAt(startAt);
        b.setEndAt(endAt);
    	b.setStatus(BookingStatus.VALIDATED);
    	
        Booking result = bookingRepository.save(b);
        
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("booking", result.getId().toString())).body(result);

    }

}
