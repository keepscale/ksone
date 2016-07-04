package org.crossfit.app.web.rest.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.TimeSlot;
import org.crossfit.app.domain.enumeration.BookingStatus;
import org.crossfit.app.domain.enumeration.Level;
import org.crossfit.app.domain.enumeration.TimeSlotStatus;
import org.joda.time.DateTime;

/**
 * C'est un créneau horaire, à une date donnée... une instance de time slot quoi
 * !
 * 
 * @author lgangloff
 *
 */
public class CurrentTimeSlotInstanceDTO extends TimeSlotInstanceDTO {

	private List<Booking> memberBookings = new ArrayList<>();

	private Level memberLevel;

	public CurrentTimeSlotInstanceDTO(DateTime date, TimeSlot slot, Level level) {
		super(date, slot);
		this.memberLevel = level;
	}


	public int getNbValidatedBookings() {
		return getValidatedBookings().size();
	}
	public int getNbWaitingBookings() {
		return getWaitingBookings().size();
	}
	
	public boolean isValidatedBooking() {
		return !memberBookings.stream().filter(b->{return b.getStatus() == BookingStatus.VALIDATED;}).collect(Collectors.toList()).isEmpty();
	}
	public boolean isWaitingBooking() {
		return !memberBookings.stream().filter(b->{return b.getStatus() == BookingStatus.ON_WAINTING_LIST;}).collect(Collectors.toList()).isEmpty();
	}
	
	public void setMemberBookings(List<Booking> bookings) {
		this.memberBookings = new ArrayList<>(bookings);
	}
	
	public Booking getMemberBooking(){
		if(!this.memberBookings.isEmpty()){
			return this.memberBookings.get(0);
		}
		return null;
	}
	
	public TimeSlotStatus getAvailability(){
		// Si le membre a déjà reservé
		if(isValidatedBooking()){
				return TimeSlotStatus.BOOKED;
		}else if(isWaitingBooking()){
				return TimeSlotStatus.WAITING;
		}else if(!isLevelAuthorized()){ // Si le membre n'a pas le droit
			return TimeSlotStatus.NO_ABLE;
		}// retourne l'état de la dispo
		else if(getPercentFree()>25){
			return TimeSlotStatus.FREE;
		}else{
			if(getPercentFree() == 0){
				return TimeSlotStatus.FULL;
			}else{
				return TimeSlotStatus.ALMOST_FULL;
			}	
		}
	}
	private int getPercentFree(){
		return 100-(Integer.divideUnsigned(100, this.getMaxAttendees())) * this.getValidatedBookings().size();
	}
	
	public boolean isLevelAuthorized(){
		return memberLevel.compareTo(getRequiredLevel()) >= 0;
	}


}
