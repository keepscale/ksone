package org.crossfit.app.exception.booking;

import org.crossfit.app.domain.MembershipRules;

public class UnableToDeleteBooking extends Exception {

	private final MembershipRules rules;

	public UnableToDeleteBooking(MembershipRules membershipRules) {
		super("Impossible de supprimer de la reservation");
		this.rules = membershipRules;
	}

	public MembershipRules getRules() {
		return rules;
	}

}
