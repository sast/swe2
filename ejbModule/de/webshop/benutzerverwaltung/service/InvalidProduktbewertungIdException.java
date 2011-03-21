package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Produktbewertung;


@ApplicationException(rollback = true)
public class InvalidProduktbewertungIdException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = -8973126758409655506L;
	private final Long id;
	private final Collection<ConstraintViolation<Produktbewertung>> violations;
	
	public InvalidProduktbewertungIdException(Long id, Collection<ConstraintViolation<Produktbewertung>> violations) {
		super("Ungueltige Produktbewertung-Id: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<Produktbewertung>> getViolations() {
		return violations;
	}
}
