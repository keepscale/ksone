package org.crossfit.app.exception.booking;

import org.crossfit.app.domain.Booking;
import org.crossfit.app.domain.Member;

public class NotBookingOwnerException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final Booking booking;
	private final Member currentMember;

	public NotBookingOwnerException(Booking booking, Member currentMember) {
		super("Le membre "+currentMember+" n'est pas le proprietaire de la reservation " + booking);
		this.booking = booking;
		this.currentMember = currentMember;
	}

}
