package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractAdresse;


@ApplicationException(rollback = true)
public class InvalidAdresseIdException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = 2983498879634020399L;
	private final Long id;
	private final Collection<ConstraintViolation<AbstractAdresse>> violations;
	
	public InvalidAdresseIdException(Long id, Collection<ConstraintViolation<AbstractAdresse>> violations) {
		super("Ungueltige Adresse-Id: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<AbstractAdresse>> getViolations() {
		return violations;
	}
}
