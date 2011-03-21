package de.webshop.bestellungsverwaltung.service;

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.bestellungsverwaltung.domain.Bestellung;

@ApplicationException(rollback = true)
public class InvalidBestellungIdException extends AbstractBestellverwaltungException implements Serializable {

	private static final long serialVersionUID = 1021815289867180024L;
	
	private final Long benutzerId;
	private final Collection<ConstraintViolation<Bestellung>> violations;
	
	public InvalidBestellungIdException(Long id, Collection<ConstraintViolation<Bestellung>> violations) {
		super("Ungueltige ID: " + id + ", Violations: " + violations);
		this.benutzerId = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return benutzerId;
	}
	
	public Collection<ConstraintViolation<Bestellung>> getViolations() {
		return violations;
	}	
}
