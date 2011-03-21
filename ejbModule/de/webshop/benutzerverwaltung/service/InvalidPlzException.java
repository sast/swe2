package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractAdresse;


@ApplicationException(rollback = true)
public class InvalidPlzException extends AbstractBenutzerverwaltungException {
	private static final long serialVersionUID = -5675412375055036543L;
	private final String plz;
	private final Collection<ConstraintViolation<AbstractAdresse>> violations;
	
	public InvalidPlzException(String plz, Collection<ConstraintViolation<AbstractAdresse>> violations) {
		super("Ungueltige PLZ: " + plz + ", Violations: " + violations);
		this.plz = plz;
		this.violations = violations;
	}
	
	public String getPlz() {
		return plz;
	}
	
	public Collection<ConstraintViolation<AbstractAdresse>> getViolations() {
		return violations;
	}
}
