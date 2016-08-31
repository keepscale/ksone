package org.crossfit.app.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotExclusion;
import org.crossfit.app.domain.enumeration.TimeSlotRecurrent;
import org.crossfit.app.repository.ClosedDayRepository;
import org.crossfit.app.repository.TimeSlotExclusionRepository;
import org.crossfit.app.repository.TimeSlotRepository;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TimeSlotService {

	private final Logger log = LoggerFactory.getLogger(TimeSlotService.class);

    @Inject
    private CrossFitBoxSerivce boxService;


    @Inject
    private TimeSlotRepository timeSlotRepository;
    
	/**
	 * Renvoie toutes les créneau horaire entre start et end, en tenant compte des jours de fermeture
	 * @param start
	 * @param end
	 * @param closedDays 
	 * @param timeSlotExclusions 
	 * @return
	 */
	public List<TimeSlotInstanceDTO> findAllTimeSlotInstance(DateTime start, DateTime end, List<ClosedDay> closedDays, Collection<TimeSlotExclusion> timeSlotExclusions){

		List<TimeSlotInstanceDTO> timeSlotInstances = new ArrayList<>();
		
		if (end.isBefore(start)){
			return timeSlotInstances;
		}
		
		List<TimeSlot> allSlots = timeSlotRepository.findAll();
		Map<TimeSlot, List<TimeSlotExclusion>> timeSlotExclusionsByTimeSlot = timeSlotExclusions
				.stream()
				.collect(Collectors.groupingBy(TimeSlotExclusion::getTimeSlot));
		
		while(!start.isAfter(end)){
			final DateTime startF = start;
			List<TimeSlotInstanceDTO> slotInstanceOfDay = allSlots.stream()
				.filter( isSlotInDay(startF))
				.filter( isSlotVisibleAt(startF))
				.filter( isSlotNotInAnTimeSlotExclusion(startF, timeSlotExclusionsByTimeSlot))
				.map(slot -> {return new TimeSlotInstanceDTO(startF, slot);})
				.collect(Collectors.toList());
			
			timeSlotInstances.addAll(slotInstanceOfDay);
			
			start = start.plusDays(1);
		}
		start = null;
		//Attention la variable start a changé ici !!! elle n'est plus réutilisable
    	
    	List<TimeSlotInstanceDTO> timeSlotInstanceWithoutClosedDay = timeSlotInstances.stream()
    			.filter(slotInstance -> slotNotInAnCloseDay(slotInstance, closedDays))
				.collect(Collectors.toList());
    	
    	return timeSlotInstanceWithoutClosedDay;
	}


	protected Predicate<? super TimeSlot> isSlotVisibleAt(final DateTime startF) {
		return slot -> { return 
				(slot.getVisibleAfter() == null || startF.toLocalDate().isAfter(slot.getVisibleAfter()) )
				&& 
				(slot.getVisibleBefore() == null || startF.toLocalDate().isBefore(slot.getVisibleBefore()));};
	}
	
	protected Predicate<? super TimeSlot> isSlotInDay(final DateTime startF) {
		return slot -> { return 
				slot.getRecurrent() == TimeSlotRecurrent.DAY_OF_WEEK ? 
				slot.getDayOfWeek() == startF.getDayOfWeek() : slot.getDate().toLocalDate().equals(startF.toLocalDate()); };
	}
	

	protected Predicate<? super TimeSlot> isSlotNotInAnTimeSlotExclusion(final DateTime startF, Map<TimeSlot, List<TimeSlotExclusion>> timeSlotExclusions) {
		return slot -> { return  !
				timeSlotExclusions.get(slot).stream()
				.anyMatch( e -> e.getDate().isEqual(startF.toLocalDate()));
		};
	}
	
	private boolean slotNotInAnCloseDay(TimeSlotInstanceDTO slot, List<ClosedDay> closedDays) {
		Optional<ClosedDay> closedDayContainingSlot = closedDays.stream()
				.filter( closeDay -> closeDay.contain(slot.getStart()) || 
						( closeDay.contain(slot.getEnd()) && !closeDay.getStartAt().isEqual(slot.getEnd())) ).findFirst();
		return ! closedDayContainingSlot.isPresent();
	}
	
}
