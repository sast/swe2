package de.webshop.util;

public class GenericMethodException extends Exception {

	private static final long serialVersionUID = 7197206633053714947L;
	
	public GenericMethodException(String msg) {
		super(msg);
	}
	
	public GenericMethodException(String msg, Exception e) {
		super(msg);
		setStackTrace(e.getStackTrace());
	}
}
