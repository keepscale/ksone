package org.crossfit.app.web.rest.dto.planning;

import java.util.List;
import java.util.Map;

import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class IntervalDTO {
	

	private final DateTime start;
    private final DateTime end;
    private final List<TimeSlotInstanceDTO> slots;
    
	public IntervalDTO(DateTime start, DateTime end, List<TimeSlotInstanceDTO> slots) {
		super();
		this.start = start;
		this.end = end;
		this.slots = slots;
	}

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	public DateTime getStart() {
		return start;
	}

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	public DateTime getEnd() {
		return end;
	}

	public List<TimeSlotInstanceDTO> getSlots() {
		return slots;
	}

	
    
    
}
