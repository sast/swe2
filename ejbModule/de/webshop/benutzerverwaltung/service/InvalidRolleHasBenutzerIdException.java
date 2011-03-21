package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;

@ApplicationException(rollback = true)
public class InvalidRolleHasBenutzerIdException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = 6861642746887057720L;
	private final Long id;
	private final Collection<ConstraintViolation<RolleHasBenutzer>> violations;
	
	public InvalidRolleHasBenutzerIdException(Long id, Collection<ConstraintViolation<RolleHasBenutzer>> violations) {
		super("Ungueltige RolleHasBenutzer-Id: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<RolleHasBenutzer>> getViolations() {
		return violations;
	}
}
