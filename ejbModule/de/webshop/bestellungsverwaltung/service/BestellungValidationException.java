package de.webshop.bestellungsverwaltung.service;

import java.util.Collection;
import java.util.Date;

import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.bestellungsverwaltung.domain.Bestellung;

public class BestellungValidationException extends AbstractBestellverwaltungException {

	private static final long serialVersionUID = 2070655474755098393L;
	
	private final Date erstellungsdatum;
	private final Long benutzerId;
	private final Collection<ConstraintViolation<Bestellung>> violations;
	
	public BestellungValidationException(Bestellung bestellung, Collection<ConstraintViolation<Bestellung>> violations) {
		super(violations.toString());
		
		if (bestellung == null) {
			this.erstellungsdatum = null;
			this.benutzerId = null;
		}
		else {
			this.erstellungsdatum = bestellung.getErstellungsdatum();
			final AbstractBenutzer benutzer = bestellung.getBenutzer();
			this.benutzerId = benutzer == null ? null : benutzer.getIdBenutzer();
		}
		
		this.violations = violations;
	}
	
	public Date getErzeugt() {
		return erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}
	
	public Long getKundeId() {
		return benutzerId;
	}
	
	public Collection<ConstraintViolation<Bestellung>> getViolations() {
		return violations;
	}
}
