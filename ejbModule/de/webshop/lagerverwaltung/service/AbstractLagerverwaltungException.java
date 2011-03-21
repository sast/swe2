package de.webshop.lagerverwaltung.service;

import de.webshop.util.AbstractWebshopException;

public class AbstractLagerverwaltungException extends AbstractWebshopException {

	private static final long serialVersionUID = -5934406447088828731L;
	
	public AbstractLagerverwaltungException(String msg) {
		super(msg);
	}
}
