package de.webshop.util;

public class NotFoundExceptionRest extends AbstractRestException {

	private static final long serialVersionUID = 2736390817996568741L;

	public NotFoundExceptionRest(String msg, NotFoundException e) {
		super(msg, e);
	}
}
