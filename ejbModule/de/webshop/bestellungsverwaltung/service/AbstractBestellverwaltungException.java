package de.webshop.bestellungsverwaltung.service;

import java.io.Serializable;

import de.webshop.util.AbstractWebshopException;


public class AbstractBestellverwaltungException extends AbstractWebshopException implements Serializable {

	private static final long serialVersionUID = -4106004195998647534L;
	public AbstractBestellverwaltungException(String msg) {
		super(msg);
	}

}
