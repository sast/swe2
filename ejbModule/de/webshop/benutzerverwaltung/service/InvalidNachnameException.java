package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;


@ApplicationException(rollback = true)
public class InvalidNachnameException extends AbstractBenutzerverwaltungException {
	private static final long serialVersionUID = -5197024567869060635L;
	private final String nachname;
	private final Collection<ConstraintViolation<AbstractBenutzer>> violations;

	public InvalidNachnameException(String nachname, Collection<ConstraintViolation<AbstractBenutzer>> violations) {
		super("Ungueltiger Nachname: " + nachname + ", Violations: " + violations);
		this.nachname = nachname;
		this.violations = violations;
	}
	
	public String getNachname() {
		return nachname;
	}
	
	public Collection<ConstraintViolation<AbstractBenutzer>> getViolations() {
		return violations;
	}
}
