package org.crossfit.app.web.rest.use;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.enumeration.Level;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.BookingResource;
import org.crossfit.app.web.rest.dto.CurrentTimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.crossfit.app.web.rest.dto.planning.CurrentPlanningDTO;
import org.crossfit.app.web.rest.dto.planning.CurrentPlanningDayDTO;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.Days;
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

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/use")
public class CrossFitBoxCurrentBookingResource extends BookingResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxCurrentBookingResource.class);

    @Inject
    private BookingRepository bookingRepository;
    
    @Inject
    private TimeSlotRepository timeSlotRepository;

    @Inject
    private TimeService timeService;

    @Inject
    private TimeSlotService timeSlotService;
    
    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private MemberRepository memberRepository;
    
    
    protected Member doGetCurrentMember() {
		return memberRepository.findOneByLogin(SecurityUtils.getCurrentLogin(), boxService.findCurrentCrossFitBox());
	}
    
    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/planning",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CurrentPlanningDTO> getPlanning(@RequestParam(value = "page" , required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "per_page", required = false, defaultValue = "7") Integer limit)
        throws URISyntaxException {
        
    	DateTime start = timeService.now().plusDays((offset < 0 ? 0 : offset) * limit);
    	DateTime end = start.plusDays(limit <= 0 ? 1 : limit);
    	
    	if (Days.daysBetween(start, end).getDays() > 7){
    		log.warn("Le nombre de jour recherche est trop important: " + Days.daysBetween(start, end).getDays());
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

    	List<Booking> bookings = bookingRepository.findAllByMemberForPlanning(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), start, end);
    	List<CurrentTimeSlotInstanceDTO> slotInstances = timeSlotService.findAllTimeSlotInstance(start, end, doGetCurrentMember());
    	
    	List<CurrentPlanningDayDTO> days = 
			slotInstances.stream().map(slot ->{
	    		slot.setBookings(
	    				bookings.stream()
	    				.filter(b -> {return slot.contains(b.getStartAt(), b.getEndAt());})
	    	    		.sorted( (b1, b2) -> { return b1.getCreatedDate().compareTo(b2.getCreatedDate());} )
	    				.collect(Collectors.toList()));
	    		slot.setMemberBookings(bookingRepository.findAllByMemberAndDate(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), slot.getStart(), slot.getEnd()));
	    		return slot;
	    	})
			.filter(s -> {return !s.getValidatedBookings().isEmpty() || !s.getWaitingBookings().isEmpty();})
    		.sorted( (s1, s2) -> { return s1.getStart().compareTo(s2.getStart());} )
    		.collect(Collectors.groupingBy(CurrentTimeSlotInstanceDTO::getDate))
    		.entrySet().stream()
    		.map(entry -> {
    			return new CurrentPlanningDayDTO(entry.getKey(), entry.getValue());
    		})
    		.sorted( (d1, d2) -> { return d1.getDate().compareTo(d2.getDate());} )
    		.collect(Collectors.toList());
    	
    	
    	
    	return new ResponseEntity<CurrentPlanningDTO>(new CurrentPlanningDTO(days) , HttpStatus.OK);
    }

    
    /**
     * GET  /event -> get all event (timeslot & closedday.
     */
    @RequestMapping(value = "/event",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EventSourceDTO>> getAll(
    		@RequestParam(value = "start", required = false) String startStr,
    		@RequestParam(value = "end", required = false) String endStr) {
    	

    	DateTime startAt = timeService.parseDateAsUTC("yyyy-MM-dd", startStr);
    	DateTime endAt = timeService.parseDateAsUTC("yyyy-MM-dd", endStr);
    	
    	if (startAt == null || endAt == null){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	
    	
    	List<EventSourceDTO> eventSources =  
    			timeSlotService.findAllTimeSlotInstance(startAt, endAt).stream() //Les timeslot instance
			.collect(
				Collectors.groupingBy(TimeSlotInstanceDTO::getRequiredLevel)) //Groupé par level
			
			.entrySet().stream() //pour chaque level
			
			.map(entry -> {
	        	List<EventDTO> events = entry.getValue().stream() //On créé la liste d'evenement
	    			.map(slotInstance ->{
						return new EventDTO(slotInstance);
	    			}).collect(Collectors.toList());
				
				EventSourceDTO evt = new EventSourceDTO(); //On met cette liste d'évènement dans EventSource
	        	evt.setEditable(true);
				evt.setEvents(events);
	        	evt.setColor(getColor(entry.getKey()));
	        	return evt;
			})
			.collect(Collectors.toList()); 
    	
    	//Pareil pour les jours fériés
    	List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(boxService.findCurrentCrossFitBox(), startAt, endAt);
		List<EventDTO> closedDaysAsDTO = closedDays.stream().map(closeDay -> {
			return new EventDTO(closeDay);

		}).collect(Collectors.toList());
		EventSourceDTO evt = new EventSourceDTO();
    	evt.setEditable(false);
    	evt.setEvents(closedDaysAsDTO);
    	evt.setColor("#A0A0A0");
    	eventSources.add(evt);
    	
    	return new ResponseEntity<List<EventSourceDTO>>(eventSources, HttpStatus.OK);
    }

    
	private static String getColor(Level level) {
		String color = "blue";
		switch (level) {
			case FOUNDATION:
				color = "#0000FF";
				break;
			case NOVICE:
				color = "#0174DF";
				break;
			case MIDDLE:
				color = "#DF7401";
				break;
			case SKILLED:
				color = "#FF4000";
				break;

		}
		return color;
	}
	
	/**
     * DELETE  /bookings/:id -> delete the "id" booking.
     */
    @RequestMapping(value = "/bookings/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.debug("REST request to delete Booking : {}", id);
		
		Booking booking = bookingRepository.findOne(id);
		if(booking == null || !booking.getOwner().equals(doGetCurrentMember())){
			return ResponseEntity.badRequest().header("Failure", "Vous n'êtes pas le propiétaire de cette réservation").body(null);
		}
		
		return super.delete(id);
	}
	
	/**
     * POST /timeSlots/:id/booking -> save the booking for timeslot
     */
    @RequestMapping(value = "/timeSlots/{id}/booking",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Booking> createBooking(@PathVariable Long id, @RequestBody(required = true) String date) throws URISyntaxException {
    	
    	TimeSlot timeSlot = timeSlotRepository.findOne(id);
    	
    	// Si le timeSlot n'existe pas ou si il n'appartient pas à la box
    	if(timeSlot == null){
            return ResponseEntity.badRequest().header("Failure", "TimeSlot introuvable").body(null);
        } else if(!timeSlot.getBox().equals(boxService.findCurrentCrossFitBox())){
            return ResponseEntity.badRequest().header("Failure", "Le timeSlot n'appartient pas à la box").body(null);
        }
    	
    	DateTime dateDispo = timeService.parseDateAsUTC("yyyy-MM-dd", date);
    	
    	// On ajoute l'heure à la date
    	DateTime startAt = dateDispo.withTime(timeSlot.getStartTime().getHourOfDay(), timeSlot.getStartTime().getMinuteOfHour(), 0, 0);
    	DateTime endAt = dateDispo.withTime(timeSlot.getEndTime().getHourOfDay(), timeSlot.getEndTime().getMinuteOfHour(), 0, 0);
    	
    	// Si il y a déjà une réservation pour ce créneau
    	List<Booking> bookings = bookingRepository.findAllByMemberAndDate(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), startAt, endAt);
    	if(bookings == null || !bookings.isEmpty()){
    		return ResponseEntity.badRequest().header("Failure", "Une réservation existe déjà pour ce créneau").body(null);
    	}
    	Booking booking  = new Booking();
        
    	booking.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        booking.setCreatedDate(DateTime.now());
        booking.setOwner(doGetCurrentMember());
        booking.setBox(boxService.findCurrentCrossFitBox());
        booking.setStartAt(startAt);
        booking.setEndAt(endAt);
    	
        if(isAvailable(booking)){
        	booking.setStatus(BookingStatus.VALIDATED);
        }else{
        	booking.setStatus(BookingStatus.ON_WAINTING_LIST);
        }
        
        Booking result = bookingRepository.save(booking);
        return new ResponseEntity<Booking>(result, HttpStatus.OK);
    }
    
    protected boolean isAvailable(Booking booking){
		List<Booking> memberBookings = bookingRepository.findAll(boxService.findCurrentCrossFitBox(), booking.getStartAt(), booking.getEndAt());
		List<TimeSlotInstanceDTO> timeSlots = timeSlotService.findAllTimeSlotInstance(booking.getStartAt(), booking.getEndAt());
		if(!timeSlots.isEmpty()){
			return (timeSlots.get(0).getMaxAttendees() - memberBookings.size())>0;
		}
		
		return false;
	}

    @Override
    public ResponseEntity<List<Booking>> getAll(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
        Page<Booking> page = bookingRepository.findAllByMember(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), PaginationUtil.generatePageRequest(offset, limit));
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/use/bookings", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
