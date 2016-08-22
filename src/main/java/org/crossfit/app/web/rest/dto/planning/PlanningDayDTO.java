package org.crossfit.app.web.rest.dto.planning;

import java.util.List;

import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class PlanningDayDTO{

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
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