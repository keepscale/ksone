package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Subscription;

public class SubscriptionDateNotYetAvaiblableException extends
		SubscriptionException {

	private final Booking booking;
	
	public SubscriptionDateNotYetAvaiblableException(Subscription subscription, Booking booking) {
		super(subscription, "La réservation est pour le "+booking.getStartAt()+" alors que la souscription demarre a partir du " + subscription.getSubscriptionStartDate());
		this.booking = booking;
	}

	public Booking getBooking() {
		return booking;
	}
	
	

}
