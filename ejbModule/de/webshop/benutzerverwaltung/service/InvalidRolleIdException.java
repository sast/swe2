package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.Rolle;

@ApplicationException(rollback = true)
public class InvalidRolleIdException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = -2384136092621616057L;
	private final Long id;
	private final Collection<ConstraintViolation<Rolle>> violations;
	
	public InvalidRolleIdException(Long id, Collection<ConstraintViolation<Rolle>> violations) {
		super("Ungueltige Rolle-Id: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<Rolle>> getViolations() {
		return violations;
	}
}
