package de.webshop.util;

public class AbstractRestException extends Exception {

	private static final long serialVersionUID = 983529209127067577L;

	public AbstractRestException(String msg) {
		super(msg);
	}

	public AbstractRestException(String msg, Throwable t) {
		super(msg, t);
	}
}
