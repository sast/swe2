package de.webshop.lagerverwaltung.service;

import java.io.Serializable;
import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.bestellungsverwaltung.service.AbstractBestellverwaltungException;
import de.webshop.lagerverwaltung.domain.Lager;

@ApplicationException(rollback = true)
public class InvalidLagerIdException extends AbstractBestellverwaltungException implements Serializable {

	private static final long serialVersionUID = -6078045006551663312L;

	private final Long idlager;
	private final Collection<ConstraintViolation<Lager>> violations;
	
	public InvalidLagerIdException(Long idlager, Collection<ConstraintViolation<Lager>> violations) {
		super("Ungueltige Lager-ID: " + idlager + ", Violations: " + violations);
		this.idlager = idlager;
		this.violations = violations;
	}
	
	public Long getId() {
		return idlager;
	}
	
	public Collection<ConstraintViolation<Lager>> getViolations() {
		return violations;
	}	
}
