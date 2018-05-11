package org.crossfit.app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.BookingEventType;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Entity
@Table(name = "BOOKING_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BookingEvent{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "event_date", nullable = false)
	private DateTime eventDate;
    
    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private BookingEventType eventType;

    @Column(name = "user_login", length = 100, nullable = false)
	private String userLogin;
    
    @Column(name = "user_display_name", length = 200, nullable = false)
    private String userDisplayName;

    @ManyToOne(optional=true, cascade = {}, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    private Member user;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "booking_start_date", nullable = false)
    private DateTime bookingStartDate;

    @Column(name = "booking_owner_login", length = 100, nullable = false)
	private String bookingOwnerLogin;

    @Column(name = "booking_owner_display_name", length = 100, nullable = false)
	private String bookingOwnerDisplayName;

    @Column(name = "booking_time_slot_type_name", length = 200, nullable = false)
	private String bookingTimeSlotTypeName;
    
    @ManyToOne(optional=true, cascade = {}, fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
	private Booking booking;
    

    @ManyToOne(optional=false, cascade = {})
	private CrossFitBox box;

    
    public static BookingEvent createdBooking(DateTime eventDate, Member user, Booking booking, CrossFitBox box) {
    	return new BookingEvent(eventDate, BookingEventType.CREATED, user, booking, box);
    }
    
    public static BookingEvent deletedBooking(DateTime eventDate, Member user, Booking booking, CrossFitBox box) {
    	return new BookingEvent(eventDate, BookingEventType.DELETED, user, booking, box);
    }
    
	public BookingEvent() {
		super();
	}

	private BookingEvent(DateTime eventDate, BookingEventType eventType, Member user, Booking booking, CrossFitBox box) {
		super();
		this.eventDate = eventDate;
		this.eventType = eventType;
		this.setUser(user);
		this.setBooking(booking);
		this.box = box;
	}



	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	public Member getUser() {
		return user;
	}


	public void setUser(Member user) {
		this.user = user;
		this.userLogin = user.getLogin();
		this.userDisplayName = user.getFirstName() + " " + user.getLastName();
	}


	public String getUserLogin() {
		return userLogin;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public Booking getBooking() {
		return booking;
	}


	public void setBooking(Booking booking) {
		this.booking = booking;
		this.bookingStartDate = booking.getStartAt();
		this.bookingTimeSlotTypeName = booking.getTimeSlotType().getName();
		Member owner = booking.getSubscription().getMember();
		this.bookingOwnerLogin = owner.getLogin();
		this.bookingOwnerDisplayName = owner.getFirstName() + " " + owner.getLastName();
	}


	public CrossFitBox getBox() {
		return box;
	}


	public void setBox(CrossFitBox box) {
		this.box = box;
	}


	public DateTime getBookingStartDate() {
		return bookingStartDate;
	}

	public String getBookingOwnerLogin() {
		return bookingOwnerLogin;
	}

	public String getBookingOwnerDisplayName() {
		return bookingOwnerDisplayName;
	}

	public String getBookingTimeSlotTypeName() {
		return bookingTimeSlotTypeName;
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

	@Override
	public String toString() {
		return "BookingEvent [id=" + id + ", eventDate=" + eventDate + ", eventType=" + eventType + ", userLogin="
				+ userLogin + ", bookingStartDate=" + bookingStartDate + "]";
	}
    
}
