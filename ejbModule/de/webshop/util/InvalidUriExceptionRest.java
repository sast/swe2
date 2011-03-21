package de.webshop.util;

public class InvalidUriExceptionRest extends AbstractRestException {

	private static final long serialVersionUID = -2227282691499871012L;

	public InvalidUriExceptionRest(String msg) {
		super(msg);
	}
	
	public InvalidUriExceptionRest(String msg, NotFoundException e) {
		super(msg, e);
	}
}
