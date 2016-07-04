package org.crossfit.app.web.rest.use;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.TimeSlotStatus;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.MemberRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.TimeSlotResource;
import org.crossfit.app.web.rest.dto.CurrentTimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.crossfit.app.web.rest.manage.CrossFitBoxBookingResource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing imeSlot.
 */
@RestController
@RequestMapping("/use")
public class CrossFitBoxCurrentTimeSlotResource extends TimeSlotResource {

	private final Logger log = LoggerFactory.getLogger(CrossFitBoxBookingResource.class);

    @Inject
    private CrossFitBoxSerivce boxService;
    
    @Inject
    private TimeService timeService;

    @Inject
    private TimeSlotRepository timeSlotRepository;
    
    @Inject
    private TimeSlotService timeSlotService;
    
    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private BookingRepository bookingRepository;
    
    @Inject
    private MemberRepository memberRepository;
    
    /**
     * GET  /timeSlotsAsEvent -> get all the timeSlots.
     */
    @RequestMapping(value = "/timeSlotsAsEvent",
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
    	
    	List<Booking> bookings = bookingRepository.findAll(boxService.findCurrentCrossFitBox(), startAt, endAt);
    	
    	List<EventSourceDTO> eventSources =  
    			timeSlotService.findAllTimeSlotInstance(startAt, endAt, doGetCurrentMember()).stream() //Les timeslot instance
			.map(event -> {
				event.setBookings(
    				bookings.stream()
    				.filter(b -> {return event.contains(b.getStartAt(), b.getEndAt());})
    	    		.sorted( (b1, b2) -> { return b1.getCreatedDate().compareTo(b2.getCreatedDate());} )
    				.collect(Collectors.toList()));
				event.setMemberBookings(bookingRepository.findAllByMemberAndDate(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), event.getStart(), event.getEnd()));
				return event;
			})
    		.collect(
				Collectors.groupingBy(CurrentTimeSlotInstanceDTO::getAvailability)) //Groupé par disponibilité
			
			.entrySet().stream() //pour chaque disponibilité
			
			.map(entry -> {
	        	List<EventDTO> events = entry.getValue().stream() //On créé la liste d'evenement
	    			.map(slotInstance ->{
						return new EventDTO(slotInstance);
	    			}).collect(Collectors.toList());
				
				EventSourceDTO evt = new EventSourceDTO(); //On met cette liste d'évènement dans EventSource
	        	evt.setEditable(false);
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

	private static String getColor(TimeSlotStatus level) {
		String color = "black";
		switch (level) {
			case NO_ABLE:
				color = "#000000";
				break;
			case WAITING:
				color = "#5bc0de";
				break;
			case BOOKED:
				color = "#337ab7";
				break;
			case FULL:
				color = "#d9534f";
				break;
			case ALMOST_FULL:
				color = "#f0ad4e";
				break;
			case FREE:
				color = "#5cb85c";
				break;

		}
		return color;
	}

	
	/**
     * GET  /timeSlots/:id/availability -> get all availability info for timeslot.
     */
    @RequestMapping(value = "/timeSlots/{id}/availability",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CurrentTimeSlotInstanceDTO> getAvailability(@PathVariable Long id,
    		@RequestParam(value = "date", required = true) String date) {
    	
    	TimeSlot timeSlot = timeSlotRepository.findOne(id);

    	DateTime dateDispo = timeService.parseDateAsUTC("yyyy-MM-dd", date);
    	
    	CurrentTimeSlotInstanceDTO dispo = new CurrentTimeSlotInstanceDTO(dateDispo, timeSlot, doGetCurrentMember().getLevel());
    	
    	List<Booking> bookings = bookingRepository.findAll(boxService.findCurrentCrossFitBox(), dispo.getStart(), dispo.getEnd());
    	List<Booking> memberBookings = bookingRepository.findAllByMemberAndDate(boxService.findCurrentCrossFitBox(), doGetCurrentMember(), dispo.getStart(), dispo.getEnd());
    	
    	dispo.setBookings(bookings);				// Réservation de tout les membres
    	dispo.setMemberBookings(memberBookings);	// Réservation du membre courant
    	
    	return new ResponseEntity<CurrentTimeSlotInstanceDTO>(dispo, HttpStatus.OK);

    }
    
    protected Member doGetCurrentMember() {
		return memberRepository.findOneByLogin(SecurityUtils.getCurrentLogin(), boxService.findCurrentCrossFitBox());
	}

    
    
    
    
}
