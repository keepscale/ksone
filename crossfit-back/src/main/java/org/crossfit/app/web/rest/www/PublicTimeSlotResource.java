package org.crossfit.app.web.rest.www;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.config.CacheConfiguration;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/public")
public class PublicTimeSlotResource {

    private final Logger log = LoggerFactory.getLogger(PublicTimeSlotResource.class);

    @Inject
    private TimeSlotService timeSlotService;

	@Inject
	private CrossFitBoxSerivce boxService;

    @Inject
    private TimeService timeService;
	
	/**
     * GET  /event -> get all event (timeslot & closedday.
     */
    @RequestMapping(value = "/timeslots.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)	
    @Cacheable(CacheConfiguration.PUBLIC_TIMESLOT_CACHE_NAME)
    public ResponseEntity<AgendaWebDTO> getTimeSlotsByDayByHour() {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();

    	DateTime startAt = timeService.nowAsDateTime(box).withDayOfWeek(DateTimeConstants.MONDAY);
    	DateTime endAt = startAt.plusDays(6);
    	
    	final DateTimeFormatter dtfJour = DateTimeFormat.forPattern("EEEE");
    	final DateTimeFormatter dtfHeure = DateTimeFormat.forPattern("HH:mm");

    	List<TimeSlotInstanceDTO> events = timeSlotService.findAllTimeSlotInstance(startAt, endAt, Collections.emptyList(), Collections.emptyList(), timeService.getDateTimeZone(box))
    			.collect(Collectors.toList());
    	
    	
				
    	    	
    	AgendaWebDTO agenda = new AgendaWebDTO();
    	agenda.events = events.parallelStream().collect(
								Collectors.groupingBy(e->dtfJour.print(e.getStart()), LinkedHashMap::new, 
								Collectors.groupingBy(e->dtfHeure.print(e.getStart()), LinkedHashMap::new, 
										Collectors.mapping(ts->new EventWebDTO(ts.getTimeSlotType().getName(), ts.getStart(), ts.getEnd()), Collectors.toList()))));
    	agenda.days = events.stream().sorted(Comparator.comparing(TimeSlotInstanceDTO::getStart))
    			.map(e->dtfJour.print(e.getStart())).collect(Collectors.toCollection(LinkedHashSet::new));
    	agenda.times = events.stream().sorted(Comparator.comparing(ts->ts.getStart().toLocalTime()))
    			.map(e->dtfHeure.print(e.getStart())).collect(Collectors.toCollection(LinkedHashSet::new));
    	
    	agenda.definitions = events.stream().map(TimeSlotInstanceDTO::getTimeSlotType).distinct()
    			.collect(Collectors.toMap(TimeSlotType::getName, TimeSlotType::getDescription));
     	
    	return new ResponseEntity<AgendaWebDTO>(agenda, HttpStatus.OK);
    }
    
    
    static class AgendaWebDTO{
    	Set<String> days;
    	Set<String> times;
    	Map<String, String> definitions;
    	Map<String, Map<String, List<EventWebDTO>>> events;

		public Map<String, Map<String, List<EventWebDTO>>> getEvents() {
			return events;
		}
		public Set<String> getDays(){
			return days;
		}   
		public Set<String> getTimes(){
			return times;
		}
		public Map<String, String> getDefinitions() {
			return definitions;
		}
		
    	
    }

    static class EventWebDTO{
    	String name;
    	private DateTime start;
    	private DateTime end;

		public EventWebDTO(String name, DateTime start, DateTime end) {
			this.name = name;
			this.start = start;
			this.end = end;
		}
		

		public LocalTime getStart() {
			return start.toLocalTime();
		}
		public LocalTime getEnd() {
			return end.toLocalTime();
		}


		public String getName() {
			return name;
		}
    }
}
