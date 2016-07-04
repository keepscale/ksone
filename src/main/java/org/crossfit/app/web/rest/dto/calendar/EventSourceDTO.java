package org.crossfit.app.web.rest.dto.calendar;

import java.util.List;

import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EventSourceDTO {

	private List<EventDTO> events;

	private String color;
	boolean editable = true;

	public List<EventDTO> getEvents() {
		return events;
	}

	public void setEvents(List<EventDTO> events) {
		this.events = events;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
