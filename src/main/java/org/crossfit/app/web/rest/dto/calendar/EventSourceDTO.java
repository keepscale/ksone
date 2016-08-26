package org.crossfit.app.web.rest.dto.calendar;

import java.util.List;

public class EventSourceDTO {

	private List<EventDTO> events;


	private String color;
	private String backgroundColor;
	private String borderColor;
	private String textColor;
	
	private String className = "";

	private boolean editable = true;
	private boolean startEditable = true;
	private boolean durationEditable = true;
	private Rendering rendering;
	
	private boolean overlap = false;

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

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isStartEditable() {
		return startEditable;
	}

	public void setStartEditable(boolean startEditable) {
		this.startEditable = startEditable;
	}

	public boolean isDurationEditable() {
		return durationEditable;
	}

	public void setDurationEditable(boolean durationEditable) {
		this.durationEditable = durationEditable;
	}


	public String getRendering() {
		return rendering == null ? null : rendering.value;
	}

	public void setRendering(Rendering rendering) {
		this.rendering = rendering;
	}

	public boolean isOverlap() {
		return overlap;
	}

	public void setOverlap(boolean overlap) {
		this.overlap = overlap;
	}

	public enum Rendering {
		background("background"), inversebackground("inverse-background");
		
	
		final String value;
		Rendering(String value){
			this.value = value;
		}
	}

}
