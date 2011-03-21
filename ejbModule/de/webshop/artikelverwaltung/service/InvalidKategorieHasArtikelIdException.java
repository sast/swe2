package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;

@ApplicationException(rollback = true)
public class InvalidKategorieHasArtikelIdException extends Exception {

	private static final long serialVersionUID = -580464312065136469L;
	private final Long id;
	private final Collection<ConstraintViolation<KategorieHasArtikel>> violations;
	
	public InvalidKategorieHasArtikelIdException(Long id, Collection<ConstraintViolation<KategorieHasArtikel>> violations) {
		super("Ungueltige KategorieHasArtikel-ID: " + id + ", Violations: " + violations);
		this.id = id;
		this.violations = violations;
	}
	
	public Long getId() {
		return id;
	}
	
	public Collection<ConstraintViolation<KategorieHasArtikel>> getViolations() {
		return violations;
	}
}
