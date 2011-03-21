package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Artikel;


@ApplicationException(rollback = true)
public class InvalidArtikelIdException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = -6976815054824801880L;
	private final Long id;
	private final Collection<ConstraintViolation<Artikel>> violations;
	
	public InvalidArtikelIdException(Long id, Collection<ConstraintViolation<Artikel>> violations) {
		super("Ungueltige Artikel-ID: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
