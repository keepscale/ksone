package org.crossfit.app.web.rest.manage;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.enumeration.Level;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.crossfit.app.web.rest.dto.planning.PlanningDTO;
import org.crossfit.app.web.rest.dto.planning.PlanningDayDTO;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/manage")
public class CrossFitBoxBookingResource {

    private final Logger log = LoggerFactory.getLogger(CrossFitBoxBookingResource.class);

    @Inject
    private BookingRepository bookingRepository;

    @Inject
    private TimeService timeService;

    @Inject
    private TimeSlotService timeSlotService;
    
    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private ClosedDayRepository closedDayRepository;

    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/planning",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PlanningDTO> get(@RequestParam(value = "page" , required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "per_page", required = false, defaultValue = "7") Integer limit)
        throws URISyntaxException {
        
    	DateTime start = timeService.now().plusDays((offset < 0 ? 0 : offset) * limit);
    	DateTime end = start.plusDays(limit <= 0 ? 1 : limit);
    	
    	if (Days.daysBetween(start, end).getDays() > 7){
    		log.warn("Le nombre de jour recherche est trop important: " + Days.daysBetween(start, end).getDays());
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

    	List<Booking> bookings = bookingRepository.findAll(boxService.findCurrentCrossFitBox(), start, end);
    	List<TimeSlotInstanceDTO> slotInstances = timeSlotService.findAllTimeSlotInstance(start, end);
    	
    	List<PlanningDayDTO> days = 
			slotInstances.stream().map(slot ->{
	    		slot.setBookings(
	    				bookings.stream()
	    				.filter(b -> {return slot.contains(b.getStartAt(), b.getEndAt());})
	    	    		.sorted( (b1, b2) -> { return b1.getCreatedDate().compareTo(b2.getCreatedDate());} )
	    				.collect(Collectors.toList()));
	    		return slot;
	    	})
    		.sorted( (s1, s2) -> { return s1.getStart().compareTo(s2.getStart());} )
    		.collect(Collectors.groupingBy(TimeSlotInstanceDTO::getDate))
    		.entrySet().stream()
    		.map(entry -> {
    			return new PlanningDayDTO(entry.getKey(), entry.getValue());
    		})
    		.sorted( (d1, d2) -> { return d1.getDate().compareTo(d2.getDate());} )
    		.collect(Collectors.toList());
    	
    	
    	
    	return new ResponseEntity<>(new PlanningDTO(days) , HttpStatus.OK);
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

}
