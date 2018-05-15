package org.crossfit.app.web.rest.api;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.domain.TimeSlotNotification;
import org.crossfit.app.repository.BookingEventRepository;
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
import org.crossfit.app.web.rest.dto.BookingEventDTO;
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

import com.gs.collections.impl.factory.Lists;

/**
 * REST controller for managing Booking.
 */
@RestController
@RequestMapping("/api/events")
public class BookingEventResource {

    private final Logger log = LoggerFactory.getLogger(BookingEventResource.class);

    @Inject
    private BookingEventRepository bookingEventRepository;
    

    @Inject
    private TimeService timeService;
    
    @Inject
    private CrossFitBoxSerivce boxService;



    @RequestMapping(value = "/bookings/today",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingEventDTO>> getLastBookingEventOfToday(
    		@RequestParam(value = "page" , required = false, defaultValue = "0") Integer index,
            @RequestParam(value = "per_page", required = false, defaultValue = "10") Integer nbElements) throws URISyntaxException {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	
    	DateTime now = timeService.nowAsDateTime(box);
		DateTime nowMinusOneHours = now.minusHours(1);
    	DateTime endOfNow = now.withHourOfDay(0).plusDays(1);
    	
    	List<BookingEventDTO> bookingEvents = 
    			bookingEventRepository.findAllByBookingStartBetween(box, nowMinusOneHours, endOfNow)
			    	.stream()
			    	.filter(event->event.getEventDate().isAfter(now.withHourOfDay(0))) //On ne veut que les évènements créés après 0h du jour courant
			    	.sorted(Comparator.comparing(BookingEvent::getEventDate).reversed())
			    	.map(BookingEventDTO.mapper)
			    	.collect(Collectors.toList());

    	log.debug("Recherche des derniers evenements de résa pour un créneau entre {} et {} => {} evenements.", nowMinusOneHours, endOfNow, bookingEvents.size()); 
    	
    	int fromIndex = index * nbElements;
		int toIndex = (index+1) * nbElements;
		toIndex = toIndex > bookingEvents.size() ? bookingEvents.size() : toIndex;		
		
		List<BookingEventDTO> subList = fromIndex >= bookingEvents.size() ? 
				Collections.emptyList() 
				: bookingEvents.subList(fromIndex, toIndex);
		return new ResponseEntity<>(
				subList, HttpStatus.OK);
    }
	
}
