package org.crossfit.app.exception;

public class CrossFitBoxConfiguration extends RuntimeException {

	private static final long serialVersionUID = 1L;

	
	public CrossFitBoxConfiguration(String message, Throwable cause) {
		super(message, cause);
	}

	public CrossFitBoxConfiguration(String message) {
		super(message);
	}
	
	
}
