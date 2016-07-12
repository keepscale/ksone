package org.crossfit.app.web.rest.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.BookingRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.security.SecurityUtils;
import org.crossfit.app.service.CrossFitBoxSerivce;
import org.crossfit.app.service.TimeService;
import org.crossfit.app.service.TimeSlotService;
import org.crossfit.app.web.exception.BadRequestException;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.util.HeaderUtil;
import org.crossfit.app.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
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
     * POST /timeSlots/:id/booking -> save the booking for timeslot
     */
    @RequestMapping(value = "/timeSlots/{id}/booking",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
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
    	List<Booking> bookings = bookingRepository.findAllByMemberAndDate(SecurityUtils.getCurrentMember(), startAt, endAt);
    	if(bookings == null || !bookings.isEmpty()){
    		return ResponseEntity.badRequest().header("Failure", "Une réservation existe déjà pour ce créneau").body(null);
    	}
    	Booking booking  = new Booking();
        
    	booking.setCreatedBy(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        booking.setCreatedDate(DateTime.now());
        booking.setOwner(SecurityUtils.getCurrentMember());
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
}
