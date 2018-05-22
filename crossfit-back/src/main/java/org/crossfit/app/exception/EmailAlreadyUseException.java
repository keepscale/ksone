package org.crossfit.app.exception;

public class EmailAlreadyUseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailAlreadyUseException(String email) {
		super(email + " est deja utilisé.");
	}

}
