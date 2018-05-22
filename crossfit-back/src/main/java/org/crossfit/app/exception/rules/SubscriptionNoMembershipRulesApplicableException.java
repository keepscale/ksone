package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Subscription;

public class SubscriptionNoMembershipRulesApplicableException extends SubscriptionException {

	private final Booking booking;
	
	public SubscriptionNoMembershipRulesApplicableException(Subscription subscription, Booking booking) {
		super(subscription, "Aucune règle ne permet de reserver un creneau de type " + booking.getTimeSlotType().getName());
		this.booking = booking;
	}

	public Booking getBooking() {
		return booking;
	}

	
}
