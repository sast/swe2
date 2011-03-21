package de.webshop.bestellungsverwaltung.service;

import java.util.Date;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BestellungDuplikatException extends AbstractBestellverwaltungException {
	
	private static final long serialVersionUID = -5680762232249015095L;

	private final Long benutzerId;
	private final Date erstellungsdatum;
	
	public BestellungDuplikatException(Long benutzerId, Date erstellungsdatum) {
		
		super("Bestellung von Kunde \"" + benutzerId + ", " + erstellungsdatum + "\" existiert bereits");
		this.benutzerId = benutzerId;
		this.erstellungsdatum = erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

	public Long getKundeId() {
		return benutzerId;
	}

	public Date getErzeugt() {
		return erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

}
