package org.crossfit.app.web.rest.dto;

import java.io.Serializable;
import java.util.function.Function;

import org.crossfit.app.domain.BookingEvent;
import org.crossfit.app.domain.enumeration.BookingEventType;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BookingEventDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static Function<BookingEvent, BookingEventDTO> mapper = b->{
		BookingEventDTO dto = new BookingEventDTO(b.getId(), b.getEventDate(), b.getEventType(), b.getUserLogin(), b.getUserDisplayName(), 
				b.getBookingStartDate(), b.getBookingOwnerLogin(), b.getBookingOwnerDisplayName(), b.getBookingTimeSlotTypeName());
		return dto;
	};
	
	private Long id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime eventDate;
	
	private BookingEventType  eventType;    
    private String userLogin; 
    private String userDisplayName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime bookingStartDate;
	private String bookingOwnerLogin;
	private String bookingOwnerDisplayName;
	private String bookingTimeSlotTypeName;
    
	
	public BookingEventDTO() {
		super();
	}
	

	public BookingEventDTO(Long id, DateTime eventDate, BookingEventType eventType, String userLogin, String userDisplayName,
			DateTime bookingStartDate, String bookingOwnerLogin, String bookingOwnerDisplayName, String bookingTimeSlotTypeName) {
		super();
		this.id = id;
		this.eventDate = eventDate;
		this.eventType = eventType;
		this.userLogin = userLogin;
		this.userDisplayName = userDisplayName;
		this.bookingStartDate = bookingStartDate;
		this.bookingOwnerLogin = bookingOwnerLogin;
		this.bookingOwnerDisplayName = bookingOwnerDisplayName;
		this.bookingTimeSlotTypeName = bookingTimeSlotTypeName;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public DateTime getEventDate() {
		return eventDate;
	}


	public void setEventDate(DateTime eventDate) {
		this.eventDate = eventDate;
	}


	public BookingEventType getEventType() {
		return eventType;
	}


	public void setEventType(BookingEventType eventType) {
		this.eventType = eventType;
	}


	public String getUserLogin() {
		return userLogin;
	}


	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}


	public String getUserDisplayName() {
		return userDisplayName;
	}


	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}


	public DateTime getBookingStartDate() {
		return bookingStartDate;
	}


	public void setBookingStartDate(DateTime bookingStartDate) {
		this.bookingStartDate = bookingStartDate;
	}


	public String getBookingOwnerLogin() {
		return bookingOwnerLogin;
	}


	public void setBookingOwnerLogin(String bookingOwnerLogin) {
		this.bookingOwnerLogin = bookingOwnerLogin;
	}


	public String getBookingOwnerDisplayName() {
		return bookingOwnerDisplayName;
	}


	public void setBookingOwnerDisplayName(String bookingOwnerDisplayName) {
		this.bookingOwnerDisplayName = bookingOwnerDisplayName;
	}


	public String getBookingTimeSlotTypeName() {
		return bookingTimeSlotTypeName;
	}


	public void setBookingTimeSlotTypeName(String bookingTimeSlotTypeName) {
		this.bookingTimeSlotTypeName = bookingTimeSlotTypeName;
	}


	@Override
	public String toString() {
		return "BookingEventDTO [id=" + id + ", eventDate=" + eventDate + ", eventType=" + eventType + ", userLogin="
				+ userLogin + ", bookingStartDate=" + bookingStartDate + ", bookingOwnerLogin=" + bookingOwnerLogin
				+ ", bookingTimeSlotTypeName=" + bookingTimeSlotTypeName + "]";
	}
	
}
