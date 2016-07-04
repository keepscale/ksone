package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

import org.crossfit.app.web.rest.dto.CurrentTimeSlotInstanceDTO;
import org.joda.time.LocalDate;

public class CurrentPlanningDayDTO{
	
	private final LocalDate date;
	private final List<CurrentTimeSlotInstanceDTO> slots;
	
	public CurrentPlanningDayDTO(LocalDate date, List<CurrentTimeSlotInstanceDTO> slots) {
		this.date = date;
		this.slots = slots;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<CurrentTimeSlotInstanceDTO> getSlots() {
		return slots;
	}
	
}