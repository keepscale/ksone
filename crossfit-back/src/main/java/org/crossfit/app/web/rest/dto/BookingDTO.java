package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.CardEvent;
import org.crossfit.app.domain.Member;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.crossfit.app.domain.util.CustomLocalDateSerializer;
import org.crossfit.app.domain.util.ISO8601LocalDateDeserializer;
import org.crossfit.app.domain.workouts.Wod;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BookingDTO implements Serializable {


	public static Function<Booking, BookingDTO> cardMapper = b->{
		Member member = b.getSubscription().getMember();
		String title = member.getFirstName() + " " + member.getLastName() + 
				" ("+b.getSubscription().getMembership().getName()+")";
		BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), title);
		dto.setCreatedAt(b.getCreatedDate());
		dto.setStartAt(b.getStartAt());
		return dto;
	};
	
	public static Function<Booking, BookingDTO> adminMapper = b->{
		Member member = b.getSubscription().getMember();
		String title = member.getFirstName() + " " + member.getLastName() + 
				" ("+b.getSubscription().getMembership().getName()+")";
		BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), title);
		dto.setCheckInDate(b.getCardEvent().map(CardEvent::getCheckingDate).orElseGet(()->{return null;}));
		dto.setCardUuid(member.getCardUuid());
		dto.setMemberId(member.getId());
		return dto;
	};

	public static Function<Booking, BookingDTO> memberEditMapper = b->{
		BookingDTO dto = adminMapper.apply(b);
		dto.setStartAt(b.getStartAt());
		return dto;
	};

	public static Function<Booking, BookingDTO> myBooking = b->{
//		String membershipName = b.getSubscription().getMembership().getN	ame();
		String timeslotName = b.getTimeSlotType().getName();
		String title = "'" + timeslotName+"'";
		BookingDTO dto = new BookingDTO(b.getId(), b.getStartAt().toLocalDate(), title);
		dto.setCreatedAt(b.getCreatedDate());
		dto.setStartAt(b.getStartAt());
		
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

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime startAt;
	
	private String title;

	private Long timeslotId;
	
	private Long subscriptionId;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime createdAt;

	private Long memberId;
	
	private String cardUuid;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime checkInDate;
		
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
	
	

	public DateTime getStartAt() {
		return startAt;
	}

	public void setStartAt(DateTime startAt) {
		this.startAt = startAt;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getCardUuid() {
		return cardUuid;
	}

	public void setCardUuid(String cardUuid) {
		this.cardUuid = cardUuid;
	}

	public DateTime getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(DateTime checkInDate) {
		this.checkInDate = checkInDate;
	}

	@Override
	public String toString() {
		return "BookingDTO [id=" + id + ", date=" + date + ", title=" + title + ", timeslotId=" + timeslotId
				+ ", subscriptionId=" + subscriptionId + "]";
	}
	
}
