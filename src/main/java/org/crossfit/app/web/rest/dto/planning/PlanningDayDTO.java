package org.crossfit.app.web.rest.dto.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PlanningDayDTO{

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private final LocalDate date;
	private final List<TimeSlotInstanceDTO> slots;
	private final List<IntervalDTO> slotsByInterval;
	
	public PlanningDayDTO(LocalDate date, List<TimeSlotInstanceDTO> slots) {
		super();
		this.date = date;
		this.slots = slots;
		this.slotsByInterval = new ArrayList<IntervalDTO>();
		
		List<Interval> intervals = slots.stream()
				.map(instance -> { 
					return new Interval(instance.getStart(), instance.getEnd());
				})
				.collect(Collectors.toSet())
				.stream()
				.sorted((i1,i2)->i1.getStart().compareTo(i2.getStart()))
				.collect(Collectors.toList());

		for (Interval interval : intervals) {
			List<TimeSlotInstanceDTO> slotsOfInterval = slots.stream()
					.filter(slot -> interval.contains(slot.getStart()))
					.sorted((s1,s2) -> s1.getTimeSlotType().getName().compareTo(s2.getTimeSlotType().getName()))
					.collect(Collectors.toList());
			
			slots.removeAll(slotsOfInterval);
			
			if (slotsOfInterval.isEmpty())
				continue;
			
			slotsByInterval.add(new IntervalDTO(interval.getStart(), interval.getEnd(), slotsOfInterval));
		}
	}

	public LocalDate getDate() {
		return date;
	}

	public List<TimeSlotInstanceDTO> getSlots() {
		return slots;
	}

	public List<IntervalDTO> getSlotsByInterval() {
		return slotsByInterval;
	}

	
	
}