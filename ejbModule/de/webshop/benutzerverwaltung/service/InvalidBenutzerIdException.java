package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;


@ApplicationException(rollback = true)
public class InvalidBenutzerIdException extends AbstractBenutzerverwaltungException {
	
	private static final long serialVersionUID = -7665299011157840029L;
	private final Long id;
	private final Collection<ConstraintViolation<AbstractBenutzer>> violations;
	
	public InvalidBenutzerIdException(Long id, Collection<ConstraintViolation<AbstractBenutzer>> violations) {
		super("Ungueltige Benutzer-Id: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<AbstractBenutzer>> getViolations() {
		return violations;
	}
}
