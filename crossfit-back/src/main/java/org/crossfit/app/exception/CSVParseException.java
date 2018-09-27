package org.crossfit.app.exception;

public class CSVParseException extends Exception {

	public CSVParseException(String message) {
		super(message);
	}
	public CSVParseException(String message, Throwable e) {
		super(message, e);
	}

}
