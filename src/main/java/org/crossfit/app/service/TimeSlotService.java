package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.CrossFitBox;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.web.rest.dto.BookingDTO;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.crossfit.app.web.rest.dto.calendar.EventDTO;
import org.crossfit.app.web.rest.dto.calendar.EventSourceDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeSlotService {

	private final Logger log = LoggerFactory.getLogger(TimeSlotService.class);

    @Inject
    private TimeService timeService;
    
    @Inject
    private CrossFitBoxSerivce boxService;


    @Inject
    private TimeSlotRepository timeSlotRepository;
    

	public Stream<TimeSlotInstanceDTO> findAllTimeSlotInstance(
			DateTime start, DateTime end, 
			List<ClosedDay> closedDays, Collection<TimeSlotExclusion> timeSlotExclusions, DateTimeZone timeZone){
		return findAllTimeSlotInstance(start, end, closedDays, timeSlotExclusions, new ArrayList<>(), b->{return null;}, timeZone);
	}
    
	/**
	 * Renvoie toutes les créneau horaire entre start et end, en tenant compte des jours de fermeture
	 * @param start
	 * @param end
	 * @param closedDays 
	 * @param timeSlotExclusions 
	 * @param bookingMapper 
	 * @param timeZone 
	 * @return
	 */
	public Stream<TimeSlotInstanceDTO> findAllTimeSlotInstance(
			DateTime start, DateTime end, 
			List<ClosedDay> closedDays, Collection<TimeSlotExclusion> timeSlotExclusions,
			List<Booking> bookings, Function<Booking, BookingDTO> bookingMapper, DateTimeZone timeZone){

		List<TimeSlotInstanceDTO> timeSlotInstances = new ArrayList<>();
		
		if (end.isBefore(start)){
			return timeSlotInstances.stream();
		}
		
		List<TimeSlot> allSlots = timeSlotRepository.findAll();
		Map<TimeSlot, List<TimeSlotExclusion>> timeSlotExclusionsByTimeSlot = timeSlotExclusions
				.stream()
				.collect(Collectors.groupingBy(TimeSlotExclusion::getTimeSlot));
		
		while(!start.isAfter(end)){
			final DateTime startF = start;
			List<TimeSlotInstanceDTO> slotInstanceOfDay = allSlots.stream()
				.filter( isSlotInDay(startF, timeZone))
				.filter( isSlotVisibleAt(startF))
				.filter( isSlotNotInAnTimeSlotExclusion(startF, timeSlotExclusionsByTimeSlot))
				.map(slot -> {return new TimeSlotInstanceDTO(startF, slot);})
				.collect(Collectors.toList());
			
			timeSlotInstances.addAll(slotInstanceOfDay);
			
			start = start.plusDays(1);
		}
		start = null;
		//Attention la variable start a changé ici !!! elle n'est plus réutilisable
    	
    	Stream<TimeSlotInstanceDTO> timeSlotInstanceWithoutClosedDay = timeSlotInstances.stream()
    			.filter(slotInstance -> slotNotInAnCloseDay(slotInstance, closedDays))
    			.map(slot ->{
    	    		slot.setBookings(
    	    				bookings.stream()
    	    				.filter(b -> {return 
    	    					slot.getTimeSlotType().getId().equals(b.getTimeSlotType().getId())
    	    						&& slot.getStart().compareTo(b.getStartAt()) == 0
    	    							&& slot.getEnd().compareTo(b.getEndAt())  == 0;
    	    				})
    	    	    		.sorted( (b1, b2) -> { return b1.getCreatedDate().compareTo(b2.getCreatedDate());} )
    	    	    		.map(bookingMapper)
    	    				.collect(Collectors.toList()));
    	    		return slot;
    	    	})
        		.sorted( (s1, s2) -> { return s1.getStart().compareTo(s2.getStart());} );
    	
    	return timeSlotInstanceWithoutClosedDay;
	}
	

	public EventSourceDTO buildEventSourceForClosedDay(List<ClosedDay> closedDays) {

    	CrossFitBox box = boxService.findCurrentCrossFitBox();
    	
		List<EventDTO> closedDaysAsDTO = closedDays.stream().map(closeDay -> {
			return new EventDTO(closeDay.getName(), closeDay.getStartAt().withZone(timeService.getDateTimeZone(box)), closeDay.getEndAt().withZone(timeService.getDateTimeZone(box)));

		}).collect(Collectors.toList());
		EventSourceDTO evtCloseDay = new EventSourceDTO();
    	evtCloseDay.setEditable(false);
    	evtCloseDay.setEvents(closedDaysAsDTO);
    	evtCloseDay.setColor("#A0A0A0");
		return evtCloseDay;
	}

	public EventSourceDTO buildEventSourceForExclusion(List<TimeSlotExclusion> timeSlotExclusions) {

		List<EventDTO> timeSlotExclusionsAsDTO = timeSlotExclusions.stream().map(timeSlotExclusion -> {

			TimeSlot timeSlot = timeSlotExclusion.getTimeSlot();
			
			String title = 
					(StringUtils.isBlank(timeSlot.getName()) ? timeSlot.getTimeSlotType().getName() : timeSlot.getName() )
							
					+ " ("+ timeSlot.getMaxAttendees() + ")";
			
			return new EventDTO(title, timeSlotExclusion.getDate().toDateTime(timeSlot.getStartTime()), timeSlotExclusion.getDate().toDateTime(timeSlot.getEndTime()));

		}).collect(Collectors.toList());
		EventSourceDTO evt = new EventSourceDTO();
    	evt.setEditable(false);
    	evt.setEvents(timeSlotExclusionsAsDTO);
    	evt.setColor("#A0A0A0");
		return evt;
	}


	protected Predicate<? super TimeSlot> isSlotVisibleAt(final DateTime startF) {
		return slot -> { return 
				(slot.getVisibleAfter() == null || startF.toLocalDate().compareTo(slot.getVisibleAfter()) >= 0 )
				&& 
				(slot.getVisibleBefore() == null || startF.toLocalDate().compareTo(slot.getVisibleBefore()) <= 0);};
	}
	
	protected Predicate<? super TimeSlot> isSlotInDay(final DateTime startF, DateTimeZone zone) {
		return slot -> { 
			if (slot.getRecurrent() == TimeSlotRecurrent.DAY_OF_WEEK)
				return slot.getDayOfWeek() == startF.getDayOfWeek();
			else{

				LocalDate localDate = slot.getDate().withZone(zone).toLocalDate();
				return localDate.equals(startF.toLocalDate());
			}
		};
	}
	

	protected Predicate<? super TimeSlot> isSlotNotInAnTimeSlotExclusion(final DateTime startF, Map<TimeSlot, List<TimeSlotExclusion>> timeSlotExclusions) {
		return slot -> { 
			
			List<TimeSlotExclusion> list = timeSlotExclusions.get(slot);
			if (list == null) return true;
			return ! list.stream().anyMatch( e -> e.getDate().isEqual(startF.toLocalDate()));
		};
	}
	
	private boolean slotNotInAnCloseDay(TimeSlotInstanceDTO slot, List<ClosedDay> closedDays) {
		Optional<ClosedDay> closedDayContainingSlot = closedDays.stream()
				.filter( closeDay -> closeDay.contain(slot.getStart()) || 
						( closeDay.contain(slot.getEnd()) && !closeDay.getStartAt().isEqual(slot.getEnd())) ).findFirst();
		return ! closedDayContainingSlot.isPresent();
	}
	
}
