package org.crossfit.app.exception;

public class NameAlreadyUseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NameAlreadyUseException(String name) {
		super(name + " existe déjà.");
	}

}
