package org.crossfit.app.exception;

import org.crossfit.app.domain.Subscription;

public class SubscriptionAlreadySignException extends Exception {
	
	private Subscription subscription;

	public SubscriptionAlreadySignException(Subscription subscription) {
		this.subscription = subscription;
	}

}
