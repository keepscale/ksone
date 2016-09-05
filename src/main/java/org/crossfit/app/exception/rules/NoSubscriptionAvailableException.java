package org.crossfit.app.exception.rules;

import java.util.List;

public class NoSubscriptionAvailableException extends Exception {

	private final List<SubscriptionException> exceptions;

	public NoSubscriptionAvailableException(
			List<SubscriptionException> exceptions) {
		super();
		this.exceptions = exceptions;
	}

	public List<SubscriptionException> getExceptions() {
		return exceptions;
	}

	
}
