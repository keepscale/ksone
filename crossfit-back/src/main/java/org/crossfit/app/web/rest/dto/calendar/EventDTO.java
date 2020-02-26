package org.crossfit.app.web.rest.dto.calendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class EventDTO {

	private final Long id;
	private String type;
	private final String title;
	private final DateTime start;
	private final DateTime end;

	private final String name;
	private final String icon;
	private final Integer totalAttendees;
	private final Integer maxAttendees;
	
	

	private String color;
	/*
	private String backgroundColor;
	private String borderColor;
	private String textColor;
	
	private String className = "";

	private boolean editable = true;
	private boolean startEditable = true;
	private boolean durationEditable = true;
	private Rendering rendering;

	*/

	public EventDTO(String title, String type, DateTime start, DateTime end, String name, String icon, Integer totalAttendees, Integer maxAttendees) {
		this(null, title, type, start, end, name, icon, totalAttendees, maxAttendees);
	}	

	public EventDTO(Long id, String title,  String type, DateTime start, DateTime end, String name, String icon, Integer totalAttendees, Integer maxAttendees) {
		super();
		this.id = id;
		this.title = title;
		this.type = type;
		this.start = start.withZoneRetainFields(DateTimeZone.UTC);
		this.end = end.withZoneRetainFields(DateTimeZone.UTC);
		this.name = name;
		this.icon = icon;
		this.totalAttendees = totalAttendees;
		this.maxAttendees = maxAttendees;
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public Integer getTotalAttendees() {
		return totalAttendees;
	}

	public Integer getMaxAttendees() {
		return maxAttendees;
	}
	
	
	/*
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
	
	*/

}
