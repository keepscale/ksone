package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Subscription;

public class SubscriptionDateExpiredException extends SubscriptionException {

	public SubscriptionDateExpiredException(Subscription subscription) {
		super(subscription, "La souscription n'est plus valable pour aujourd'hui");
	}

}
