package org.crossfit.app.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.CardEventRepository;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.TimeSlotExclusionRepository;
import org.crossfit.app.repository.TimeSlotNotificationRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO.EventSourceType;
import org.crossfit.app.web.rest.dto.planning.PlanningDTO;
import org.crossfit.app.web.rest.dto.planning.PlanningDayDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
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
    private CardEventRepository cardEventRepository;

    @Inject
    private TimeService timeService;

    @Inject
    private TimeSlotService timeSlotService;

    @Inject
    private TimeSlotNotificationRepository timeSlotNotificationRepository;
    
    @Inject
    private CrossFitBoxSerivce boxService;

    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private TimeSlotExclusionRepository timeSlotExclusionRepository;


    @RequestMapping(value = "/private/lastbookingtoday",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingDTO>> getLastBookingOfToday(
    		@RequestParam(value = "page" , required = false, defaultValue = "0") Integer index,
            @RequestParam(value = "per_page", required = false, defaultValue = "7") Integer nbDaysToDisplay) throws URISyntaxException {

        log.debug("REST request to GET LastBookingOfToday");
        
    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	
    	DateTime now = timeService.nowAsDateTime(box);
    	DateTime endOfNow = now.withHourOfDay(0).plusDays(1);
    	
    	List<BookingDTO> bookings = 
    			bookingRepository.findAllAt(box, now, endOfNow)
			    	.stream()
			    	//.filter(b->b.getCreatedDate().isAfter(now))
			    	.sorted(Comparator.comparing(Booking::getCreatedDate).reversed())
			    	.map(BookingDTO.adminMapper)
			    	.collect(Collectors.toList());

    	return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
    

    /**
     * GET  /bookings -> get all the bookings.
     */
    @RequestMapping(value = "/private/planning",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanningDTO> get(@RequestParam(value = "page" , required = false, defaultValue = "0") Integer index,
                                  @RequestParam(value = "per_page", required = false, defaultValue = "7") Integer nbDaysToDisplay)
        throws URISyntaxException {

        log.debug("REST request to GET planning");
        
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
        
    
    	List<PlanningDayDTO> days = new ArrayList<PlanningDayDTO>();
    	int i = 0;
    	DateTime start, end = null;
    	do{

        	DateTime now = timeService.nowAsDateTime(currentCrossFitBox).withTimeAtStartOfDay();
    		start = now.plusDays(index * nbDaysToDisplay);
        	end = start.plusDays(nbDaysToDisplay <= 0 ? 1 : nbDaysToDisplay).minusMillis(1);
        	
        	if (Days.daysBetween(start, end).getDays() > 14){
        		log.warn("Le nombre de jour recherche est trop important: " + Days.daysBetween(start, end).getDays());
        		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        	}


        	List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(currentCrossFitBox, start, end);
    		List<TimeSlotExclusion> timeSlotExclusions = timeSlotExclusionRepository.findAllBetween(start.toLocalDate(), end.toLocalDate());

    		
    		List<Booking> bookings = new ArrayList<>(
        			bookingRepository.findAllStartBetween(currentCrossFitBox, start, end));
    		log.debug("{} resas entre {} et {}", bookings.size(), start, end);
    		
        	List<CardEvent> cardEvents = new ArrayList<>(
        			cardEventRepository.findAllBetweenBookingStartDate(currentCrossFitBox, start, end));
    		log.debug("{} cardevents entre {} et {}", cardEvents.size(), start, end);
        	
			List<TimeSlotNotification> allNotifications = timeSlotNotificationRepository.findAllAfter(start.toLocalDate());
			
			Stream<TimeSlotInstanceDTO> slotInstancesStream = timeSlotService.findAllTimeSlotInstance(
        			start, end, closedDays, timeSlotExclusions, bookings, allNotifications, cardEvents, BookingDTO.adminMapper, timeService.getDateTimeZone(currentCrossFitBox));
        	
        	days = slotInstancesStream
        		.collect(Collectors.groupingBy(TimeSlotInstanceDTO::getDate))
        		.entrySet().stream()
        		.map(entry -> {
        			return new PlanningDayDTO(entry.getKey(), entry.getValue());
        		})
        		.collect(Collectors.toList());
        	
        	index++;
        	i++;
    	}
    	while (days.isEmpty() && i <= 7); //Sert à rien de boucler plus de 7 fois...
    	
    	index--; //On a ete une fois trop loin
    	
    	i = days.size();
    	LocalDate startAt = start.withZoneRetainFields(DateTimeZone.UTC).toLocalDate();
    	while (days.size() < nbDaysToDisplay){
    		boolean containDate = false;
    		for (PlanningDayDTO day : days) {
    			if (day.getDate().equals(startAt)){
    				containDate = true;
    				break;
    			}
			}
    		
    		if (!containDate){
    			days.add(new PlanningDayDTO(startAt, new ArrayList<>()));
    		}
    		startAt = startAt.plusDays(1);
    		i++;
    		if (i > 14){ //On se protège, pas plus de 14 !
    			break;
    		}
    	}
    	
    	List<PlanningDayDTO> sortedDay = days.stream()
        		.sorted( (d1, d2) -> { return d1.getDate().compareTo(d2.getDate());} )
        		.collect(Collectors.toList());
    	
    	return new ResponseEntity<>(new PlanningDTO(index, sortedDay) , HttpStatus.OK);
    }

    @RequestMapping(value = "/protected/planning",
    		params = {"start","view"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventSourceDTO>> planningProtected(
    		@RequestParam(value = "start" , required = true) String startStr,
    		@RequestParam(value = "view" , required = true, defaultValue = "week") String viewStr){

        log.debug("REST request to GET planning");
        
    	CrossFitBox currentCrossFitBox = boxService.findCurrentCrossFitBox();
    	DateTime now = timeService.nowAsDateTime(currentCrossFitBox);
    	DateTime startAt = timeService.parseDate("yyyy-MM-dd", startStr, currentCrossFitBox);
    	DateTime endAt = "day".equals(viewStr) ? startAt.plusDays(1) : "week".equals(viewStr) ? startAt.plusDays(7) : null;
    	
    	if (startAt == null || endAt == null){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

		List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(currentCrossFitBox, startAt, endAt);
		List<TimeSlotExclusion> timeSlotExclusions = timeSlotExclusionRepository.findAllBetween(startAt.toLocalDate(), endAt.toLocalDate());
    	
		log.debug("Recherche des resas entre {} et {}", startAt, endAt);
		
		List<Booking> bookings = new ArrayList<>(
    			bookingRepository.findAllStartBetween(currentCrossFitBox, startAt, endAt));
		
		Map<Long, Booking> bookingsById = bookings.stream().collect(Collectors.toMap(Booking::getId, Function.identity()));
		
		
    	List<EventSourceDTO> eventSources = timeSlotService.findAllTimeSlotInstance(
    			startAt, endAt, closedDays, timeSlotExclusions, bookings, new ArrayList<>(), new ArrayList<>(), BookingDTO.publicMapper, timeService.getDateTimeZone(currentCrossFitBox))
    	.collect(Collectors.groupingBy(slotInstance ->{
    		Integer max = slotInstance.getMaxAttendees();
    		Integer count = slotInstance.getTotalBooking();
    		boolean booked = slotInstance.getBookings().stream().anyMatch(bDto->{
    			return bookingsById.get(bDto.getId()).getSubscription().getMember().equals(SecurityUtils.getCurrentMember());
    		});
    		
    		boolean past = slotInstance.getStart().isBefore(now);
    		int percentFree = 100-(Integer.divideUnsigned(100, max)) * count;
    		return booked ? TimeSlotStatus.BOOKED 
    				: past ? TimeSlotStatus.NO_ABLE 
    						: count >= max ? TimeSlotStatus.FULL 
    								: (percentFree <= 25 || (max - count) == 1) ? TimeSlotStatus.ALMOST_FULL 
    										: TimeSlotStatus.FREE;
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

    				
					return new EventDTO( status == TimeSlotStatus.NO_ABLE ? null : slotInstance.getId(), title, slotInstance.getTimeSlotType().getName(), slotInstance.getStart(), slotInstance.getEnd());
    			}).collect(Collectors.toList());
			
			EventSourceType type = EventSourceType.BOOKABLE;
			switch (status) {
			case NO_ABLE:
				type = EventSourceType.PAST;
				break;
			case BOOKED:
				type = EventSourceType.BOOKED;
				break;
			case FULL:
				type = EventSourceType.FULL;
				break;
			default:
				break;
			}
			EventSourceDTO evt = new EventSourceDTO(type ); //On met cette liste d'évènement dans EventSource
        	evt.setEditable(status == TimeSlotStatus.NO_ABLE ? false : true);
			evt.setEvents(events);
			evt.setColor(status.color);
        	return evt;
		})
    	.collect(Collectors.toList()); 

    	EventSourceDTO evt = timeSlotService.buildEventSourceForExclusion(timeSlotExclusions);
    	eventSources.add(evt);
    	EventSourceDTO evt2 = timeSlotService.buildEventSourceForClosedDay(closedDays);
    	eventSources.add(evt2);
    	
    	
    	return new ResponseEntity<List<EventSourceDTO>>(eventSources, HttpStatus.OK);
    		
	}
    

	enum TimeSlotStatus{
		NO_ABLE("#A0A0A0"),
		BOOKED("#337ab7"),
		FULL("#d9534f"),
		ALMOST_FULL("#f0ad4e"),
		FREE("#5cb85c");
		
		String color;
		
		TimeSlotStatus(String color){
			this.color = color;
		}
		
	}
	
}
