package org.crossfit.app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crossfit.app.domain.enumeration.CardEventStatus;
import org.crossfit.app.domain.util.CustomDateTimeDeserializer;
import org.crossfit.app.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@Entity
@Table(name = "CARD_EVENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CardEvent{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "effective_date", nullable = false)
	private DateTime effectiveDate;
    

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "checking_date", nullable = false) 
	private DateTime checkingDate;
    
    
    @NotNull
    @Column(name = "card_uuid", length = 255, nullable = false)
	private String carduuid;

    @NotNull        
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardEventStatus status;

    @Column(name = "member_login", length = 100, nullable = true)
	private String memberLogin;

    @ManyToOne(optional=true, cascade = {})
    private Member member;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "booking_start_date", nullable = true)
    private DateTime bookingStartDate;
    
    @ManyToOne(optional=true, cascade = {})
	private Booking booking;
    

    @ManyToOne(optional=false, cascade = {})
	private CrossFitBox box;

    
	public CardEvent() {
		super();
	}

	public CardEvent(DateTime effectiveDate, DateTime checkingDate,
			String carduuid, CrossFitBox box) {
		super();
		this.effectiveDate = effectiveDate;
		this.checkingDate = checkingDate;
		this.carduuid = carduuid;
		this.box = box;
		this.status = CardEventStatus.NO_MEMBER;
	}

	public CardEvent(DateTime effectiveDate, DateTime checkingDate,
			String carduuid, CrossFitBox box, Member member) {
		this(effectiveDate, checkingDate, carduuid, box);
		this.setMember(member);
		this.status = CardEventStatus.NO_BOOKING;
	}

	public CardEvent(DateTime effectiveDate, DateTime checkingDate,
			String carduuid, CrossFitBox box, Member member,
			Booking booking) {
		this(effectiveDate, checkingDate, carduuid, box, member);
		this.setBooking(booking);
		this.status = CardEventStatus.OK;
	}




	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public DateTime getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public DateTime getCheckingDate() {
		return checkingDate;
	}


	public void setCheckingDate(DateTime checkingDate) {
		this.checkingDate = checkingDate;
	}


	public String getCarduuid() {
		return carduuid;
	}


	public void setCarduuid(String carduuid) {
		this.carduuid = carduuid;
	}


	public Member getMember() {
		return member;
	}


	public void setMember(Member member) {
		this.member = member;
		this.memberLogin = member.getLogin();
	}


	public Booking getBooking() {
		return booking;
	}


	public void setBooking(Booking booking) {
		this.booking = booking;
		this.bookingStartDate = booking.getStartAt();
	}


	public CrossFitBox getBox() {
		return box;
	}


	public void setBox(CrossFitBox box) {
		this.box = box;
	}


	public String getMemberLogin() {
		return memberLogin;
	}


	public DateTime getBookingStartDate() {
		return bookingStartDate;
	}

	@Override
	public String toString() {
		return "CardEvent [id=" + id + ", effectiveDate=" + effectiveDate
				+ ", checkingDate=" + checkingDate + ", carduuid=" + carduuid
				+ ", status=" + status + ", memberLogin=" + memberLogin
				+ ", bookingStartDate=" + bookingStartDate + "]";
	}	
    
}
