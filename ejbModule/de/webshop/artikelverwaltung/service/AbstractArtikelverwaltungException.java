package de.webshop.artikelverwaltung.service;

import de.webshop.util.AbstractWebshopException;

public abstract class AbstractArtikelverwaltungException extends AbstractWebshopException {

	private static final long serialVersionUID = 1011665387009101580L;

	public AbstractArtikelverwaltungException(String msg) {
		super(msg);
	}
}
