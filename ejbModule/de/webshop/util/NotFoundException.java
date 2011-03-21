package de.webshop.util;

public class NotFoundException extends AbstractWebshopException {

	private static final long serialVersionUID = -2806953314051248334L;

	private final Class<?> clazz;
	
	public NotFoundException(String msg, Class<?> clazz) {
		super(msg);
		this.clazz = clazz;
	}
	
	public NotFoundException(String msg, Class<?> clazz, Throwable t) {
		super(msg, t);
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
}
