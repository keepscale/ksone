package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * C'est un créneau horaire, à une date donnée... une instance de time slot quoi
 * !
 * 
 * @author lgangloff
 *
 */
public class TimeSlotInstanceDTO {

	private DateTime date;

	private TimeSlot slot;

	private List<Booking> bookings = new ArrayList<>();
	
	private TimeSlotInstanceStatus timeSlotStatus;
	
	private int totalBooking;
	
	public TimeSlotInstanceDTO(DateTime date, TimeSlot slot) {
		super();
		this.date = date;
		this.slot = slot;
	}

	public Long getId() {
		return slot.getId();
	}

	public String getName() {
		return slot.getName();
	}

    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	public LocalDate getDate() {
		return date.toLocalDate();
	}

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	public DateTime getStart() {
		return date.withTime(slot.getStartTime().getHourOfDay(), slot.getStartTime().getMinuteOfHour(), 0, 0).toDateTime(DateTimeZone.UTC);
	}

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	public DateTime getEnd() {
		return date.withTime(slot.getEndTime().getHourOfDay(), slot.getEndTime().getMinuteOfHour(), 0, 0).toDateTime(DateTimeZone.UTC);
	}

	public Integer getMaxAttendees() {
		return slot.getMaxAttendees();
	}

	public TimeSlotType getTimeSlotType() {
		return slot.getTimeSlotType();
	}

	public List<Booking> getValidatedBookings() {
		return bookings.stream().filter(b->{return b.getStatus() == BookingStatus.VALIDATED;}).collect(Collectors.toList());
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = new ArrayList<>(bookings);
		this.totalBooking = this.bookings.size();
	}
	
	
	public TimeSlotInstanceStatus getTimeSlotStatus() {
		return timeSlotStatus;
	}

	public void setTimeSlotStatus(TimeSlotInstanceStatus timeSlotStatus) {
		this.timeSlotStatus = timeSlotStatus;
	}

	public int getTotalBooking() {
		return totalBooking;
	}

	public void setTotalBooking(int totalBooking) {
		this.totalBooking = totalBooking;
	}

	public boolean isPast(){
		return getStart().isBeforeNow();
	}

	@Override
	public String toString() {
		return "[" + getStart() + "->" + getEnd() + "]";
	}


	public boolean equalsDate(DateTime start, DateTime end) {
		return this.getStart().equals(start) && this.getEnd().equals(end);
	}
}
