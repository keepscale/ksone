package org.crossfit.app.exception;

import org.crossfit.app.domain.Signable;

public class AlreadySignedException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private Signable signable;

	public AlreadySignedException(Signable signable) {
		this.signable = signable;
	}

}
