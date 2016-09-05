package org.crossfit.app.exception.rules;

import java.util.List;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.MembershipRules;
import org.crossfit.app.domain.Subscription;

public class SubscriptionMembershipRulesException extends SubscriptionException {
	
	public enum MembershipRulesExceptionType {
		CountPreviousBooking, NbHoursAtLeastToBook, NbMaxBooking, NbMaxDayBooking
		
	}


	private final MembershipRulesExceptionType type;
	private final Booking booking;
	private final List<MembershipRules> breakingRules;
	
	
	public SubscriptionMembershipRulesException(MembershipRulesExceptionType type, Subscription subscription, Booking booking,
			List<MembershipRules> breakingRules) {
		super(subscription, "La réservation viole une ou plusieurs regles " + type);
		this.type = type;
		this.booking = booking;
		this.breakingRules = breakingRules;
	}


	public MembershipRulesExceptionType getType() {
		return type;
	}


	public Booking getBooking() {
		return booking;
	}


	public List<MembershipRules> getBreakingRules() {
		return breakingRules;
	}
	
}
