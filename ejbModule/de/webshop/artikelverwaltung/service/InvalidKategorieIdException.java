package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Kategorie;


@ApplicationException(rollback = true)
public class InvalidKategorieIdException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = -8623896967054997322L;
	private final Long id;
	private final Collection<ConstraintViolation<Kategorie>> violations;
	
	public InvalidKategorieIdException(Long id, Collection<ConstraintViolation<Kategorie>> violations) {
		super("Ungueltige Kategorie-ID: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<Kategorie>> getViolations() {
		return violations;
	}
}
