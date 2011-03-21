package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;


@ApplicationException(rollback = true)
public class InvalidEmailException extends AbstractBenutzerverwaltungException {
	private static final long serialVersionUID = 1566746881789307348L;
	private final String email;
	private final Collection<ConstraintViolation<AbstractBenutzer>> violations;
	
	public InvalidEmailException(String email, Collection<ConstraintViolation<AbstractBenutzer>> violations) {
		super("Ungueltige Email: " + email + ", Violations: " + violations);
		this.email = email;
		this.violations = violations;
	}
	
	public String getEmail() {
		return email;
	}
	
	public Collection<ConstraintViolation<AbstractBenutzer>> getViolations() {
		return violations;
	}
}
