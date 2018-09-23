package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.workouts.Wod;
import org.crossfit.app.domain.workouts.WodResult;
import org.crossfit.app.exception.booking.NotBookingOwnerException;
import org.crossfit.app.exception.booking.UnableToDeleteBooking;
import org.crossfit.app.exception.rules.ManySubscriptionsAvailableException;
import org.crossfit.app.exception.rules.NoSubscriptionAvailableException;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.CardEventRepository;
import org.crossfit.app.repository.SubscriptionRepository;
import org.crossfit.app.repository.TimeSlotNotificationRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.AuthoritiesConstants;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.BookingService;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.WodService;
import org.crossfit.app.service.util.BookingRulesChecker;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.BookingEventDTO;
import org.crossfit.app.web.rest.dto.BookingStatusDTO;
import org.crossfit.app.web.rest.errors.CustomParameterizedException;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.bus.Event;
import reactor.bus.EventBus;

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
    private BookingService bookingService;
    
    @Inject
    private WodService wodService;

	@Inject
	private CardEventRepository cardEventRepository;

    @Inject
    private CrossFitBoxSerivce boxService;
    
    @Inject
    private TimeService timeService;

    @Inject
    private TimeSlotRepository timeSlotRepository;
    
    @Inject
	private SubscriptionRepository subscriptionRepository;

    @Inject
	private TimeSlotNotificationRepository notificationRepository;

    @Inject
	private EventBus eventBus;
    @Autowired
    private SimpMessagingTemplate template;
    
    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/bookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingDTO>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
    	
    	Page<Booking> page = null; 
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	
		page = bookingRepository.findAllByMemberAfter(SecurityUtils.getCurrentMember(), timeService.nowAsDateTime(currentCrossFitBox), PaginationUtil.generatePageRequest(offset, limit));

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookings", offset, limit);
        return new ResponseEntity<>(page.getContent().stream().map(BookingDTO.myBooking()).collect(Collectors.toList()), headers, HttpStatus.OK);
    }
    

    /**
     * GET  /bookings -> get all past the bookings.
     */
    @RequestMapping(value = "/bookings/pastbookings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingDTO>> getAllPast(
    		@RequestParam(value = "page" , required = false) Integer offset) throws URISyntaxException {
    	
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	DateTime end = timeService.nowAsDateTime(box).minusMillis(1);
		DateTime start = end.minusMonths(offset);
		Set<Booking> result = bookingRepository.findAllStartBetween(box, SecurityUtils.getCurrentMember(), start, end);
//		Set<Wod> wods = wodService.findWodsBetween(start.toLocalDate(), end.toLocalDate());
//		Set<WodResult> wodResults = wodService.findMyResultsBetween(start.toLocalDate(), end.toLocalDate());

        Comparator<? super BookingDTO> comparator = (b1,b2) ->{
        	return b2.getStartAt().compareTo(b1.getStartAt());
        };
		return new ResponseEntity<>(result.stream().map(BookingDTO.myBooking()).sorted(comparator).collect(Collectors.toList()), HttpStatus.OK);
    }


	private TimeSlot findTimeSlot(Long timeSlotId) {
		TimeSlot selectedTimeSlot = timeSlotRepository.findById(timeSlotId).get();
		CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	
    	
    	// Si le timeSlot n'existe pas ou si il n'appartient pas à la box
    	if(selectedTimeSlot == null){
    		throw new CustomParameterizedException("TimeSlot introuvable", "");
        } else if(!selectedTimeSlot.getBox().equals(currentCrossFitBox)){
    		throw new CustomParameterizedException("Le timeSlot n'appartient pas à la box", "");
        }
		return selectedTimeSlot;
	}
	
	private Stream<Booking> findBookingsFor(Date date, DateTimeZone dateTimeZone, TimeSlot selectedTimeSlot) {

    	
    	LocalDate localDate = new LocalDate(date, dateTimeZone);
    	// On ajoute l'heure à la date
    	DateTime startAt = localDate.toDateTime(selectedTimeSlot.getStartTime(), dateTimeZone);
    	DateTime endAt = localDate.toDateTime(selectedTimeSlot.getEndTime(), dateTimeZone);

    	log.debug("Recherche des resa pour au {} -> {} de type {}", startAt, endAt, selectedTimeSlot.getTimeSlotType());
    	
    	Stream<Booking> bookings = bookingRepository.findAllAt(boxService.findCurrentCrossFitBox(), startAt, endAt).stream()
				.filter( b -> b.getTimeSlotType().equals(selectedTimeSlot.getTimeSlotType()));
		return bookings;
	}

	
    @RequestMapping(value = "/bookings/{date}/{timeSlotId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getAllBookingForTimeSlot(
    		@PathVariable @DateTimeFormat(iso=ISO.DATE) Date date,
    		@PathVariable Long timeSlotId) throws URISyntaxException {
		
    	CrossFitBox box = boxService.findCurrentCrossFitBox();

    	if (!box.isSocialEnabled()){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
		
    	TimeSlot selectedTimeSlot = findTimeSlot(timeSlotId);
    	Stream<Booking> bookings = findBookingsFor(date, timeService.getDateTimeZone(box), selectedTimeSlot);
    	
		List<String> bookingNames = bookings
				.map(b->{
					String nick = b.getSubscription().getMember().getNickName();
					return StringUtils.isEmpty(nick) ? "Anonyme" : nick;
				})
				.collect(Collectors.toList());
		
		
        return new ResponseEntity<>(bookingNames, HttpStatus.OK);
    }


    
    @RequestMapping(value = "/bookings/{date}/{timeSlotId}/status",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingStatusDTO> getBookingStatusForTimeSlot(
    		@PathVariable @DateTimeFormat(iso=ISO.DATE) Date date,
    		@PathVariable Long timeSlotId) throws URISyntaxException {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	DateTimeZone dateTimeZone = timeService.getDateTimeZone(box);
    	
    	TimeSlot selectedTimeSlot = findTimeSlot(timeSlotId);
		List<Booking> bookings = findBookingsFor(date, dateTimeZone, selectedTimeSlot).collect(Collectors.toList());


    	Optional<TimeSlotNotification> notif = notificationRepository.findOneByDateAndTimeSlotAndMember(
    			new LocalDate(date), selectedTimeSlot, SecurityUtils.getCurrentMember());

    	LocalDate localDate = new LocalDate(date, dateTimeZone);
    	DateTime startAt = localDate.toDateTime(selectedTimeSlot.getStartTime(), dateTimeZone);
    	
    	boolean canSubscribeNotif = true;
    	if (notif.isPresent()){
    		canSubscribeNotif = false;
    	}
    	else if (startAt.isBefore(timeService.nowAsDateTime(box))){
    		canSubscribeNotif = false;
    	}
    	else if (bookings.stream().anyMatch(b->{return b.getSubscription().getMember().equals(SecurityUtils.getCurrentMember());})){
    		canSubscribeNotif = false;
    	}
    	
        return new ResponseEntity<BookingStatusDTO>(
        		new BookingStatusDTO(selectedTimeSlot.getMaxAttendees(), bookings.size(), notif.isPresent(), canSubscribeNotif), HttpStatus.OK);
    }

    @RequestMapping(value = "/bookings/{date}/{timeSlotId}/subscribe",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingStatusDTO> subscribeNotificationForTimeSlot(
    		@PathVariable @DateTimeFormat(iso=ISO.DATE) Date date,
    		@PathVariable Long timeSlotId) throws URISyntaxException {
    	
    	TimeSlot selectedTimeSlot = findTimeSlot(timeSlotId);

    	Optional<TimeSlotNotification> notif = notificationRepository.findOneByDateAndTimeSlotAndMember(
    			new LocalDate(date), selectedTimeSlot, SecurityUtils.getCurrentMember());
    	
    	if (!notif.isPresent()){
    		TimeSlotNotification notification = new TimeSlotNotification();
    		notification.setDate(new LocalDate(date));
    		notification.setMember(SecurityUtils.getCurrentMember());
    		notification.setTimeSlot(selectedTimeSlot);
    		
			notificationRepository.save(notification);
    	}
			
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * GET  /bookings/:id -> get the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Booking> get(@PathVariable Long id) {
        log.debug("REST request to get Booking : {}", id);
		Booking booking = bookingRepository.findById(id).get();
		
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
    public ResponseEntity<Void> delete(
    		@PathVariable Long id) {
    	
        log.debug("REST request to delete Booking : {}", id);
        
        try {
			bookingService.deleteBooking(id);
		} catch (NotBookingOwnerException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createAlert("Vous n'êtes pas le propiétaire de cette réservation", "")).body(null);
		} catch (UnableToDeleteBooking e) {
	        throw new CustomParameterizedException("La réservation ne peut être annulée que "+e.getRules().getNbHoursAtLeastToCancel()+" heures avant le début de la séance. Veuillez prendre contact avec le coach.");

		}
        
        
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("booking", id.toString())).build();
    }

    /**
     * PUT  /bookings/:id/checkin -> Make a card event for the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}/checkin",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkin(@PathVariable Long id) {

    	if (!SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createAlert("Vous n'avez pas les droits necessaires", "")).body(null);
    	}
    	
		Booking booking = bookingRepository.findById(id).get();
		Member m = booking.getSubscription().getMember();
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
		DateTime nowAsDateTime = timeService.nowAsDateTime(box);
		
		CardEvent cardEvent = new CardEvent(nowAsDateTime, nowAsDateTime, m.getCardUuid(), box, m, booking);

		cardEventRepository.save(cardEvent);
    	
		return ResponseEntity.ok().build();
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
    	
		Booking booking = bookingRepository.findById(id).get();
		booking.setStatus(BookingStatus.VALIDATED);    	
		
        bookingRepository.save(booking);

		return ResponseEntity.ok().build();
    }
    
    /**
     * POST /bookings -> save the booking for timeslot
     * @throws NoSubscriptionAvailableException 
     * @throws ManySubscriptionsAvailableException 
     */

	@RequestMapping(value = "/bookings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookingDTO> create(
			@RequestParam(value = "prepare" , required = false, defaultValue = "false") boolean prepare,
			@Valid @RequestBody BookingDTO bookingdto) throws URISyntaxException, NoSubscriptionAvailableException, ManySubscriptionsAvailableException {

		log.debug("REST request to save Booking : {}", bookingdto);
		
    	TimeSlot selectedTimeSlot = timeSlotRepository.findById(bookingdto.getTimeslotId()).get();
    	
    	// Si owner est null alors on prend l'utilisateur courant
    	Subscription selectedSubscription = bookingdto.getSubscriptionId() == null ? null : subscriptionRepository.findById(bookingdto.getSubscriptionId()).get();
    	Member selectedMember = selectedSubscription == null ? SecurityUtils.getCurrentMember() : selectedSubscription.getMember();
    	
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	
    	DateTime now = timeService.nowAsDateTime(currentCrossFitBox);

    	
    	// Si le timeSlot n'existe pas ou si il n'appartient pas à la box
    	if(selectedTimeSlot == null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("TimeSlot introuvable", "")).body(null);
        } else if(!selectedTimeSlot.getBox().equals(boxService.findCurrentCrossFitBox())){
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Le timeSlot n'appartient pas à la box", "")).body(null);
        }
    	
    	// On ajoute l'heure à la date
    	DateTime startAt = bookingdto.getDate().toDateTime(selectedTimeSlot.getStartTime(), timeService.getDateTimeZone(currentCrossFitBox));
    	DateTime endAt = bookingdto.getDate().toDateTime(selectedTimeSlot.getEndTime(), timeService.getDateTimeZone(currentCrossFitBox));

    	if (selectedTimeSlot.getVisibleAfter() != null && startAt.toLocalDate().isBefore(selectedTimeSlot.getVisibleAfter())){
    		 return ResponseEntity.status(HttpStatus.FORBIDDEN)
             		.headers(HeaderUtil.createAlert("Le timeslot n'est valable qu'à partir du " + selectedTimeSlot.getVisibleAfter(), String.valueOf(selectedTimeSlot.getId())))
             		.body(null);
    	}

    	if (selectedTimeSlot.getVisibleBefore() != null && startAt.toLocalDate().isAfter(selectedTimeSlot.getVisibleBefore())){
    		 return ResponseEntity.status(HttpStatus.FORBIDDEN)
             		.headers(HeaderUtil.createAlert("Le timeslot n'est valable qu'avant le " + selectedTimeSlot.getVisibleBefore(), String.valueOf(selectedTimeSlot.getId())))
             		.body(null);
    	}
    	
    	boolean isSuperUser = SecurityUtils.isUserInAnyRole(AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN);
		if (!isSuperUser && !SecurityUtils.getCurrentMember().equals(selectedMember)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            		.headers(HeaderUtil.createAlert("Vous n'avez pas le droit de reserver pour quelqu'un d'autre", String.valueOf(selectedTimeSlot.getId())))
            		.body(null);
    	}
    	
    	// Si il y a déjà une réservation entre les date de ce créneau
		List<Booking> currentBookingsBetweenStartAndEnd = new ArrayList<>(
				bookingRepository.findAllAt(boxService.findCurrentCrossFitBox(), startAt, endAt));
		
		Optional<Booking> alreadyBooked = currentBookingsBetweenStartAndEnd.stream()
				.filter(b-> b.getSubscription().getMember().equals(selectedMember)).findAny();
		
    	if(alreadyBooked.isPresent()){
    		//La résa concerne l'utilisateur connecté ?
    		if (prepare && SecurityUtils.getCurrentMember().equals(selectedMember)){
    			Booking b = alreadyBooked.get();
    			
    			BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), null);
    			dto.setSubscriptionId(b.getSubscription().getId());
    			dto.setCreatedAt(b.getCreatedDate());
    			
    			//La résa est pour le même type de séance, alors oui, c'est la même
    			if (b.getTimeSlotType().equals(selectedTimeSlot.getTimeSlotType())){
	    	        if (b.getStartAt().isBefore(now)){
		    	        throw new CustomParameterizedException("Vous ne pouvez pas modifier une réservation passée");
	    	        }
	    	        else{
	    				return ResponseEntity.badRequest().body(dto);
	    	        }
    				
    			}
    			//Sinon, c'est qu'il y a une résa à la même heure, mais pour une autre type de séance
    			else{
	    	        String name = b.getTimeSlotType().getName();    	    		
	    	        throw new CustomParameterizedException("Vous avez déjà réservé pour le créneau " + name + " à ce même horaire");
    			}
    		}
    		//La résa est faite par un admin
    		else
    			throw new CustomParameterizedException("Une réservation existe déjà pour cet horaire et ce membre");
    	}
    	
    	List<Booking> currentBookingsForTimeSlot = currentBookingsBetweenStartAndEnd.stream()
    		.filter(b->{
    			 return
    				b.getTimeSlotType().equals(selectedTimeSlot.getTimeSlotType()) &&
					b.getStartAt().getMillis() == startAt.getMillis() && 
					b.getEndAt().getMillis() == endAt.getMillis();
    		}).collect(Collectors.toList());
    	
    	if (!isSuperUser && currentBookingsForTimeSlot.size() >= selectedTimeSlot.getMaxAttendees()){
    		//TODO: Throw BookingFullException => faire un translator, et gerer l'exception cote angular pour proposer d'etre notifier
    		throw new CustomParameterizedException("Il n'y a plus de place disponible pour ce créneau");
    	}
    	
    	Booking b  = new Booking();        
    	b.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        b.setCreatedDate(now);
        
        BookingRulesChecker rules = new BookingRulesChecker(now,
        		bookingRepository.findAllByMember(selectedMember), 
        		subscriptionRepository.findAllByMember(selectedMember));

        Subscription possibleSubscription = null;        
        try {
        	
        	possibleSubscription = rules.findSubscription(selectedMember, selectedTimeSlot.getTimeSlotType(), startAt, currentBookingsForTimeSlot.size());
        	
        	//Si on est pas admin, qu'on souhaite forcer une souscription, qui n'est pas possible => erreur
        	//(Si on est admin, on laisse passer)
        	if (!isSuperUser && selectedSubscription != null && !possibleSubscription.equals(selectedSubscription)){
        		throw new CustomParameterizedException("Vous ne pouvez pas réserver avec cet abonnement ("+selectedSubscription.getMembership().getName()+")");
        	}
			
        	selectedSubscription = possibleSubscription;
        	
		} catch (ManySubscriptionsAvailableException e) {
			if (selectedSubscription == null){
				throw e;
			}
			//Plusieurs souscription possibles et celle souhaite n'est pas dans la liste => erreur
			if (!e.getSubscriptions().contains(selectedSubscription)){
				if (!isSuperUser)
					throw e;
			}	

		} catch (NoSubscriptionAvailableException e) {
			//Pas de souscription disponible
			//on est pas admin ? => erreur
			if (!isSuperUser)
				throw e;
			//Sinon, il faut forcer une souscription
			else if (selectedSubscription == null)
				throw e;
		}
        
        
		b.setSubscription(selectedSubscription);
        b.setBox(currentCrossFitBox);
        b.setTimeSlotType(selectedTimeSlot.getTimeSlotType());
        b.setStartAt(startAt);
        b.setEndAt(endAt);
    	b.setStatus(BookingStatus.VALIDATED);


		log.debug("Booking a sauvegarder ou a preparer: {}", b);
    	if(!prepare){
	        Booking result = bookingRepository.save(b);
	        BookingEvent bookingEvent = BookingEvent.createdBooking(now, SecurityUtils.getCurrentMember(), result, currentCrossFitBox);
			eventBus.notify("bookings", Event.wrap(bookingEvent));
            template.convertAndSend("/topic/bookings", BookingEventDTO.mapper.apply(bookingEvent));
    		log.debug("Booking sauvegarde: {}", b);
	        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("booking", result.getId().toString())).body(null);
    	}
    	else{
    		bookingdto.setSubscriptionId(selectedSubscription.getId());
    		return ResponseEntity.ok().body(bookingdto);
    	}
    }

}
