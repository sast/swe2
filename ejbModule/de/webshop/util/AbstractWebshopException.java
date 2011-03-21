package de.webshop.util;

public abstract class AbstractWebshopException extends Exception {

	private static final long serialVersionUID = 8047058498305214547L;

	public AbstractWebshopException(String msg) {
		super(msg);
	}

	public AbstractWebshopException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
