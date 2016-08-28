package org.crossfit.app.exception.rules;

import java.util.List;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;

public class MembershipRulesException extends SubscriptionException {
	private final Booking booking;
	private final List<MembershipRules> breakingRules;
	
	
	public MembershipRulesException(Subscription subscription, Booking booking,
			List<MembershipRules> breakingRules) {
		super(subscription, "La réservation viole une ou plusieurs regles");
		this.booking = booking;
		this.breakingRules = breakingRules;
	}
}
