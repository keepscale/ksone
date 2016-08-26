package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Subscription;

public class SubscriptionException extends Exception {

	private final Subscription subscription;

	public SubscriptionException(Subscription subscription) {
		super();
		this.subscription = subscription;
	}

	public Subscription getSubscription() {
		return subscription;
	}	
}
