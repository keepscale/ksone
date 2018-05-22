package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Subscription;

public class SubscriptionDateExpiredForBookingException extends
		SubscriptionException {

	private final Booking booking;
	
	public SubscriptionDateExpiredForBookingException(Subscription subscription, Booking booking) {
		super(subscription, "La réservation est pour le "+booking.getStartAt()+" alors que la souscription est valable jusqu'au " + subscription.getSubscriptionEndDate());
		this.booking = booking;
	}

	public Booking getBooking() {
		return booking;
	}
	
	

}
