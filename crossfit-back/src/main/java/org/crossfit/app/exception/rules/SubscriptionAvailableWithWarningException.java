package org.crossfit.app.exception.rules;

import org.crossfit.app.domain.Subscription;
import org.joda.time.LocalDate;

public class SubscriptionAvailableWithWarningException extends Exception {
	private static final long serialVersionUID = 1L;

	private final Subscription subscription;
	private final LocalDate dateFinValiditeCertif;

	public SubscriptionAvailableWithWarningException(Subscription subscription, LocalDate dateFinValiditeCertif) {
		super("Trop de souscription disponible");
		this.subscription = subscription;
		this.dateFinValiditeCertif = dateFinValiditeCertif;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public LocalDate getDateFinValiditeCertif() {
		return dateFinValiditeCertif;
	}
	
}
