package org.crossfit.app.web.rest.dto;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BookingDTO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;

	private BookingStatus status;
	
	private TimeSlot timeSlot;

	private Subscription subscription;

	
	
	public BookingDTO(Long id, LocalDate date, BookingStatus status,
			TimeSlot timeSlot, Subscription subscription) {
		super();
		this.id = id;
		this.date = date;
		this.status = status;
		this.timeSlot = timeSlot;
		this.subscription = subscription;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "BookingDTO [id=" + id + ", date=" + date + ", timeSlot="
				+ (timeSlot) + ", subscription=" + (subscription) + "]";
	}
}
