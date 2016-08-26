package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.BookingRepository;
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
import org.joda.time.Period;
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
    private BookingRepository bookingRepository;
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
		timeSlot.setBox(boxService.findCurrentCrossFitBox());

		if (timeSlot.getRecurrent() == TimeSlotRecurrent.DATE){
			timeSlot.setDayOfWeek(null);
		}
		else if (timeSlot.getRecurrent() == TimeSlotRecurrent.DAY_OF_WEEK){
			timeSlot.setDate(null);
		}		
		if (timeSlot.getDayOfWeek() == null && timeSlot.getDate() == null){
			throw new BadRequestException("A new timeslot must have a date or a day of week");
		}
		
		LocalTime startAt = timeSlot.getStartTime();
		LocalTime endAt = timeSlot.getEndTime();
		if (startAt.isAfter(endAt)){
			timeSlot.setEndTime(startAt.plus(Period.fieldDifference(endAt, startAt)));
		}
		
		TimeSlot result = timeSlotRepository.save(timeSlot);
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
		return timeSlotRepository.findOne(id);
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
		TimeSlot timeSlot = timeSlotRepository.findOne(id);
		if (timeSlot.getBox().equals(boxService.findCurrentCrossFitBox())) {
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
    	

    	DateTime startAt = timeService.parseDateAsUTC("yyyy-MM-dd", startStr);
    	DateTime endAt = timeService.parseDateAsUTC("yyyy-MM-dd", endStr);
    	
    	if (startAt == null || endAt == null){
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	
    	
    	List<EventSourceDTO> eventSources =  
    			timeSlotService.findAllTimeSlotInstance(startAt, endAt).stream() //Les timeslot instance
			.collect(
				Collectors.groupingBy(TimeSlotInstanceDTO::getTimeSlotType)) //Groupé par level
			
			.entrySet().stream() //pour chaque level
			
			.map(entry -> {
	        	TimeSlotType slotType = entry.getKey();
	        	List<TimeSlotInstanceDTO> slots = entry.getValue();
	        	
				List<EventDTO> events = slots.stream() //On créé la liste d'evenement
	    			.map(slotInstance ->{
						return new EventDTO(slotInstance);
	    			}).collect(Collectors.toList());
				
				EventSourceDTO evt = new EventSourceDTO(); //On met cette liste d'évènement dans EventSource
	        	evt.setEditable(true);
				evt.setEvents(events);
				evt.setColor(slotType.getColor());
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
}
