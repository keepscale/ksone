package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.function.Function;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.Subscription;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.TimeSlotType;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BookingDTO implements Serializable {

	public static Function<Booking, BookingDTO> adminMapper = b->{
		Member member = b.getSubscription().getMember();
		String title = member.getFirstName() + " " + member.getLastName() + 
				" ("+b.getSubscription().getMembership().getName()+")";
		BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), title);
		return dto;
	};

	public static Function<Booking, BookingDTO> publicMapper = b->{
		Member member = b.getSubscription().getMember();
		String title = member.getFirstName();
		BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), title);
		return dto;
	};
	
	private Long id;

	@NotNull
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	
	private String title;

	private Long timeslotId;
	
	private Long subscriptionId;
	
	private DateTime createdAt;
	
	public BookingDTO() {
		super();
	}

	public BookingDTO(Long id, LocalDate date, String title) {
		super();
		this.id = id;
		this.date = date;
		this.title = title;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Long getTimeslotId() {
		return timeslotId;
	}

	public void setTimeslotId(Long timeslotId) {
		this.timeslotId = timeslotId;
	}
	

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "BookingDTO [id=" + id + ", date=" + date + ", title=" + title + ", timeslotId=" + timeslotId
				+ ", subscriptionId=" + subscriptionId + "]";
	}
	
}
