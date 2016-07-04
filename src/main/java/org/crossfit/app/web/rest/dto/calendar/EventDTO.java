package org.crossfit.app.web.rest.dto.calendar;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.ClosedDay;
import org.crossfit.app.web.rest.dto.TimeSlotInstanceDTO;
import org.joda.time.DateTime;

public class EventDTO {

	private final Long id;
	private final String title;
	private final DateTime start;
	private final DateTime end;

	public EventDTO(TimeSlotInstanceDTO slotInstance) {
		super();
		this.id = slotInstance.getId();
		this.start = slotInstance.getStart();
		this.end = slotInstance.getEnd();
		this.title = 
				(StringUtils.isBlank(slotInstance.getName()) ? slotInstance.getRequiredLevel() : slotInstance.getName() )
						
				+ " ("+ slotInstance.getValidatedBookings().size() + "/" + slotInstance.getMaxAttendees() + ")";
	}
	
	public EventDTO(ClosedDay closedDay) {
		super();
		this.id = null;
		this.start = closedDay.getStartAt();
		this.end = closedDay.getEndAt();
		this.title = closedDay.getName();
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public DateTime getStart() {
		return start;
	}

	public DateTime getEnd() {
		return end;
	}

}
