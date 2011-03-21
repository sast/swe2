package de.webshop.benutzerverwaltung.service;

import de.webshop.util.AbstractWebshopException;

public abstract class AbstractBenutzerverwaltungException extends AbstractWebshopException {
	
	private static final long serialVersionUID = 4110293768141667315L;

	public AbstractBenutzerverwaltungException(String msg) {
		super(msg);
	}
}
