package org.crossfit.app.exception.rules;

import java.util.List;

import org.crossfit.app.domain.Subscription;

public class ManySubscriptionsAvailableException extends Exception {

	private final List<Subscription> subscriptions;

	public ManySubscriptionsAvailableException(List<Subscription> subscriptions) {
		super("Trop de souscription disponible");
		this.subscriptions = subscriptions;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}
}
