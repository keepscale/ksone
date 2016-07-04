package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.LocalDate;

public class PlanningDayDTO{
	
	private final LocalDate date;
	private final List<TimeSlotInstanceDTO> slots;
	
	public PlanningDayDTO(LocalDate date, List<TimeSlotInstanceDTO> slots) {
		super();
		this.date = date;
		this.slots = slots;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<TimeSlotInstanceDTO> getSlots() {
		return slots;
	}
	
}