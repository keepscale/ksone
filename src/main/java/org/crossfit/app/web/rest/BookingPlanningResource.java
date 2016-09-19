package org.crossfit.app.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.TimeSlotExclusionRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.dto.BookingDTO;
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

/**
 * REST controller for managing Booking.
 */
@RestController
public class BookingPlanningResource {

    private final Logger log = LoggerFactory.getLogger(BookingPlanningResource.class);

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
    
    @Inject
    private TimeSlotExclusionRepository timeSlotExclusionRepository;

    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/private/planning",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanningDTO> get(@RequestParam(value = "page" , required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(value = "per_page", required = false, defaultValue = "7") Integer limit)
        throws URISyntaxException {
    	
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
        
    	DateTime start = timeService.nowAsDateTime(currentCrossFitBox).plusDays((offset < 0 ? 0 : offset) * limit);
    	DateTime end = start.plusDays(limit <= 0 ? 1 : limit);
    	
    	if (Days.daysBetween(start, end).getDays() > 14){
    		log.warn("Le nombre de jour recherche est trop important: " + Days.daysBetween(start, end).getDays());
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}


    	List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(boxService.findCurrentCrossFitBox(), start, end);
		List<TimeSlotExclusion> timeSlotExclusions = timeSlotExclusionRepository.findAllBetween(start.toLocalDate(), end.toLocalDate());

		log.debug("Recherche des resas entre {} et {}", start, end);
		
		List<Booking> bookings = new ArrayList<>(
    			bookingRepository.findAllStartBetween(currentCrossFitBox, start, end));
		
    	Stream<TimeSlotInstanceDTO> slotInstancesStream = timeSlotService.findAllTimeSlotInstance(
    			start, end, closedDays, timeSlotExclusions, bookings, BookingDTO.adminMapper);
    	
    	List<PlanningDayDTO> days = slotInstancesStream
    		.collect(Collectors.groupingBy(TimeSlotInstanceDTO::getDate))
    		.entrySet().stream()
    		.map(entry -> {
    			return new PlanningDayDTO(entry.getKey(), entry.getValue());
    		})
    		.sorted( (d1, d2) -> { return d1.getDate().compareTo(d2.getDate());} )
    		.collect(Collectors.toList());
    	
    	
    	
    	return new ResponseEntity<>(new PlanningDTO(days) , HttpStatus.OK);
    }

    @RequestMapping(value = "/protected/planning",
    		params = {"start","view"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventSourceDTO>> planningProtected(
    		@RequestParam(value = "start" , required = true) String startStr,
    		@RequestParam(value = "view" , required = true, defaultValue = "week") String viewStr){
 
    	DateTime startAt = timeService.parseDateAsUTC("yyyy-MM-dd", startStr);
    	DateTime endAt = "day".equals(viewStr) ? startAt.plusDays(1) : "week".equals(viewStr) ? startAt.plusDays(7) : null;
    	
    	if (startAt == null || endAt == null){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
		List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(currentCrossFitBox, startAt, endAt);
		List<TimeSlotExclusion> timeSlotExclusions = timeSlotExclusionRepository.findAllBetween(startAt.toLocalDate(), endAt.toLocalDate());
    	
		log.debug("Recherche des resas entre {} et {}", startAt, endAt);
		
		List<Booking> bookings = new ArrayList<>(
    			bookingRepository.findAllStartBetween(currentCrossFitBox, startAt, endAt));
		
		Map<Long, Booking> bookingsById = bookings.stream().collect(Collectors.toMap(Booking::getId, Function.identity()));
		
		
    	List<EventSourceDTO> eventSources = timeSlotService.findAllTimeSlotInstance(
    			startAt, endAt, closedDays, timeSlotExclusions, bookings, BookingDTO.publicMapper)
    	.collect(Collectors.groupingBy(slotInstance ->{
    		Integer max = slotInstance.getMaxAttendees();
    		Integer count = slotInstance.getTotalBooking();
    		boolean booked = slotInstance.getBookings().stream().anyMatch(bDto->{
    			return bookingsById.get(bDto.getId()).getSubscription().getMember().equals(SecurityUtils.getCurrentMember());
    		});

    		return booked ? TimeSlotStatus.BOOKED : count >= max ? TimeSlotStatus.FULL : TimeSlotStatus.FREE;
    	}))
		.entrySet().stream() //pour chaque level
    	.map(entry -> {
    		TimeSlotStatus status = entry.getKey();
        	List<TimeSlotInstanceDTO> slots = entry.getValue();
        	
			List<EventDTO> events = slots.stream() //On créé la liste d'evenement
    			.map(slotInstance ->{
    				String title = 
    						(StringUtils.isBlank(slotInstance.getName()) ? slotInstance.getTimeSlotType().getName() : slotInstance.getName() )
    								
    						+ " ("+ slotInstance.getTotalBooking() + "/" + slotInstance.getMaxAttendees() + ")";

    				
					return new EventDTO( slotInstance.getId(), title, slotInstance.getStart(), slotInstance.getEnd());
    			}).collect(Collectors.toList());
			
			EventSourceDTO evt = new EventSourceDTO(); //On met cette liste d'évènement dans EventSource
        	evt.setEditable(true);
			evt.setEvents(events);
			evt.setColor(status.color);
        	return evt;
		})
    	.collect(Collectors.toList()); 
    	
    	return new ResponseEntity<List<EventSourceDTO>>(eventSources, HttpStatus.OK);
    		
	}
    

	enum TimeSlotStatus{
		BOOKED("#337ab7"),
		FULL("#d9534f"),
		FREE("#5cb85c");
		
		String color;
		
		TimeSlotStatus(String color){
			this.color = color;
		}
		
	}
	
}
