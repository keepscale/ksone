package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.TimeSlotExclusionRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.exception.BadRequestException;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing TimeSlot.
 */
@RestController
@RequestMapping("/api")
public class TimeSlotResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotResource.class);

    @Inject
    private TimeSlotRepository timeSlotRepository;
    @Inject
    private TimeSlotService timeSlotService;

	@Inject
	private CrossFitBoxSerivce boxService;

    @Inject
    private TimeService timeService;

    @Inject
    private ClosedDayRepository closedDayRepository;
    
    @Inject
    private TimeSlotExclusionRepository timeSlotExclusionRepository;
    /**
     * POST  /timeSlots -> Create a new timeSlot.
     */
    @RequestMapping(value = "/timeSlots",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlot> create(@Valid @RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to save TimeSlot : {}", timeSlot);
        if (timeSlot.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new timeSlot cannot already have an ID").body(null);
        }
		
        TimeSlot result;
		try {
			result = doSave(timeSlot);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        
        return ResponseEntity.created(new URI("/api/timeSlots/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("timeSlot", result.getId().toString()))
                .body(result);
    }

	protected TimeSlot doSave(TimeSlot timeSlot) throws BadRequestException {
		CrossFitBox box = boxService.findCurrentCrossFitBox();
		timeSlot.setBox(box);

		if (timeSlot.getRecurrent() == TimeSlotRecurrent.DATE){
			timeSlot.setDayOfWeek(null);
			timeSlot.getExclusions().clear();
			timeSlot.setVisibleAfter(null);
			timeSlot.setVisibleBefore(null);
			DateTime date = timeSlot.getDate() == null ? null : timeSlot.getDate().toDateTime(timeService.getDateTimeZone(box)).withTime(timeSlot.getStartTime());
			timeSlot.setDate(date);
		}
		else if (timeSlot.getRecurrent() == TimeSlotRecurrent.DAY_OF_WEEK){
			timeSlot.setDate(null);
			timeSlot.getExclusions().removeIf(ex->ex.getDate() == null || ex.getDate().getDayOfWeek() != timeSlot.getDayOfWeek());
		}		
		if (timeSlot.getDayOfWeek() == null && timeSlot.getDate() == null){
			throw new BadRequestException("A new timeslot must have a date or a day of week");
		}
		
		LocalTime startAt = timeSlot.getStartTime();
		LocalTime endAt = timeSlot.getEndTime();
		if (startAt.isAfter(endAt)){
			int difference = Minutes.minutesBetween(startAt, endAt).getMinutes();
			difference = difference < 0 ? -1 * difference : difference;
			timeSlot.setEndTime(startAt.plusMinutes(difference));
		}
		

		log.debug("Sauvegarde du timeslot: {}", timeSlot);
		TimeSlot result = timeSlotRepository.save(timeSlot);

		timeSlotExclusionRepository.deleteAll(timeSlotExclusionRepository.findAllByTimeSlot(timeSlot));
						
		for (TimeSlotExclusion timeSlotExclusion : timeSlot.getExclusions()) {
			timeSlotExclusion.setTimeSlot(result);		
		}
		HashSet<TimeSlotExclusion> exptectedExclusions = new HashSet<TimeSlotExclusion>(timeSlot.getExclusions());
		
		for (TimeSlotExclusion timeSlotExclusion : exptectedExclusions) {
			timeSlotExclusionRepository.save(timeSlotExclusion);
		}

		log.debug("Timeslot sauvegardé: {}", result);
		
		return result;
	}

    /**
     * PUT  /timeSlots -> Updates an existing timeSlot.
     */
    @RequestMapping(value = "/timeSlots",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlot> update(@Valid @RequestBody TimeSlot timeSlot) throws URISyntaxException {
        log.debug("REST request to update TimeSlot : {}", timeSlot);
        if (timeSlot.getId() == null) {
            return create(timeSlot);
        }
        TimeSlot result;
		try {
			result = doSave(timeSlot);
		} catch (BadRequestException e) {
			 return ResponseEntity.badRequest().header("Failure", e.getMessage()).body(null);
		}
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("timeSlot", timeSlot.getId().toString()))
                .body(result);
    }

    /**
     * GET  /timeSlots -> get all the timeSlots.
     */
    @RequestMapping(value = "/timeSlots",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TimeSlot>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<TimeSlot> page = doFindAll(offset, limit);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timeSlots", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

	protected Page<TimeSlot> doFindAll(Integer offset, Integer limit) {
		// TODO: Filtrer par box
		Page<TimeSlot> page = timeSlotRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		return page;
	}

    /**
     * GET  /timeSlots/:id -> get the "id" timeSlot.
     */
    @RequestMapping(value = "/timeSlots/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeSlot> get(@PathVariable Long id) {
        log.debug("REST request to get TimeSlot : {}", id);
        return Optional.ofNullable(doGet(id))
            .map(timeSlot -> new ResponseEntity<>(
                timeSlot,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	protected TimeSlot doGet(Long id) {
		// TODO: Filtrer par box
		TimeSlot t = timeSlotRepository.findById(id).map(ts->{
			ts.setExclusions(timeSlotExclusionRepository.findAllByTimeSlot(ts));
			return ts;
		}).orElse(null);
		return t;
	}

    /**
     * DELETE  /timeSlots/:id -> delete the "id" timeSlot.
     */
    @RequestMapping(value = "/timeSlots/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete TimeSlot : {}", id);
        doDelete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("timeSlot", id.toString())).build();
    }

	protected void doDelete(Long id) {
		TimeSlot timeSlot = timeSlotRepository.findById(id).get();
		if (timeSlot.getBox().equals(boxService.findCurrentCrossFitBox())) {
			timeSlotExclusionRepository.deleteAll(timeSlotExclusionRepository.findAllByTimeSlot(timeSlot));
			timeSlotRepository.delete(timeSlot);
		}
	}
	
	
	
	/**
     * GET  /event -> get all event (timeslot & closedday.
     */
    @RequestMapping(value = "/timeSlots",
    		params = {"view=event"},
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventSourceDTO>> getAll(
    		@RequestParam(value = "start", required = false) String startStr,
    		@RequestParam(value = "end", required = false) String endStr) {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();

    	DateTime startAt = timeService.parseDate("yyyy-MM-dd", startStr, box);
    	DateTime endAt = timeService.parseDate("yyyy-MM-dd", endStr, box);
    	
    	if (startAt == null || endAt == null){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}

		List<ClosedDay> closedDays = closedDayRepository.findAllByBoxAndBetween(box, startAt, endAt);
		List<TimeSlotExclusion> timeSlotExclusions = timeSlotExclusionRepository.findAllBetween(startAt.toLocalDate(), endAt.toLocalDate());
    	
    	List<EventSourceDTO> eventSources =  
    			timeSlotService.findAllTimeSlotInstance(startAt, endAt, closedDays, timeSlotExclusions, timeService.getDateTimeZone(box)) //Les timeslot instance
			.collect(
				Collectors.groupingBy(TimeSlotInstanceDTO::getTimeSlotType)) //Groupé par level
			
			.entrySet().stream() //pour chaque level
			
			.map(entry -> {
	        	TimeSlotType slotType = entry.getKey();
	        	List<TimeSlotInstanceDTO> slots = entry.getValue();
	        	
				List<EventDTO> events = slots.stream() //On créé la liste d'evenement
	    			.map(slotInstance ->{
	    				String title = 
	    						(StringUtils.isBlank(slotInstance.getName()) ? slotInstance.getTimeSlotType().getName() : slotInstance.getName() )
	    								
	    						+ " ("+ slotInstance.getMaxAttendees() + ")";

	    				
						return new EventDTO( slotInstance.getId(), title, slotInstance.getTimeSlotType().getName(), slotInstance.getStart(), slotInstance.getEnd());
	    			}).collect(Collectors.toList());
				
				EventSourceDTO evt = new EventSourceDTO(); //On met cette liste d'évènement dans EventSource
	        	evt.setEditable(true);
				evt.setEvents(events);
				evt.setColor(slotType.getColor());
	        	return evt;
			})
			.collect(Collectors.toList()); 
    	
		//Pareil pour les jours d'exclusions
    	EventSourceDTO evt = timeSlotService.buildEventSourceForExclusion(timeSlotExclusions);
    	eventSources.add(evt);
    	
    	
		//Pareil pour les jours ferie
    	EventSourceDTO evtCloseDay = timeSlotService.buildEventSourceForClosedDay(closedDays);
    	eventSources.add(evtCloseDay);
    	
    	return new ResponseEntity<List<EventSourceDTO>>(eventSources, HttpStatus.OK);
    }

}
