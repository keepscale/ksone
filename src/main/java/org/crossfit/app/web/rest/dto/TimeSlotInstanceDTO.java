package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

	private List<BookingDTO> bookings = new ArrayList<>();
	
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
		return date.withTime(slot.getStartTime().getHourOfDay(), slot.getStartTime().getMinuteOfHour(), 0, 0);
	}

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	public DateTime getEnd() {
		DateTime returnDate = date.withTime(slot.getEndTime().getHourOfDay(), slot.getEndTime().getMinuteOfHour(), 0, 0);
		if (returnDate.isBefore(getStart())){
			returnDate = returnDate.plusDays(1);
		}
		return returnDate;
	}

	public Integer getMaxAttendees() {
		return slot.getMaxAttendees();
	}

	public TimeSlotType getTimeSlotType() {
		return slot.getTimeSlotType();
	}

	public List<BookingDTO> getBookings() {
		return bookings;
	}


	public void setBookings(List<BookingDTO> bookings) {
		this.bookings = bookings;
		this.totalBooking = this.bookings.size();
	}
	
/*
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings.stream().map(b->{
			Subscription s = new Subscription();
			s.setId(b.getSubscription().getId());
			s.setMember(b.getSubscription().getMember());
			Membership membership = new Membership();
			membership.setName(b.getSubscription().getMembership().getName());
			s.setMembership(membership);
			BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), b.getStatus(), slot, s);
			return dto;
		}).collect(Collectors.toList());
		
		this.totalBooking = this.bookings.size();
	}
	*/
	
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
