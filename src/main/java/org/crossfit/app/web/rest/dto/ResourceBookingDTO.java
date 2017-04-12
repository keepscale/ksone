package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResourceBookingDTO implements Serializable {

	private Long id;

	@NotNull
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime startHour;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime endHour;

	private Long resourceId;

	private Long memberId;

	public ResourceBookingDTO() {
		super();
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

	public DateTime getStartHour() {
		return startHour;
	}

	public void setStartHour(DateTime startHour) {
		this.startHour = startHour;
	}

	public DateTime getEndHour() {
		return endHour;
	}

	public void setEndHour(DateTime endHour) {
		this.endHour = endHour;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	@Override
	public String toString() {
		return "ResourceBookingDTO [id=" + id + ", date=" + date + ", startHour=" + startHour + ", endHour=" + endHour
				+ ", resourceId=" + resourceId + ", memberId=" + memberId + "]";
	}

}
