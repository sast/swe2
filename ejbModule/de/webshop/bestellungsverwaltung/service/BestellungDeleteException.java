package de.webshop.bestellungsverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BestellungDeleteException extends AbstractBestellverwaltungException {

	private static final long serialVersionUID = -3621863052066563201L;
	
	private final Long bestellId;
	
	public BestellungDeleteException(Long bestellId) {
		super("Bestellung von Kunde " + bestellId + " konnte nicht geloescht werden");
		
		this.bestellId = bestellId;
	}
	
	public Long getBestellId() {
		return bestellId;
	}
	
}
