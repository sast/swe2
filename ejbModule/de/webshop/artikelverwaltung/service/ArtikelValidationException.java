package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class ArtikelValidationException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = 676784593806016245L;
	private final Collection<ConstraintViolation<Artikel>> violations;
	private final Artikel artikel;
	
	public ArtikelValidationException(Artikel artikel, Collection<ConstraintViolation<Artikel>> violations) {	
		super("Ungueltiger Artikel: " + artikel + ", Violations: " + violations);
		this.artikel = artikel;
		this.violations = violations; 
	}
	
	public Artikel getArtikel() {
		return artikel;
	}
	
	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
	}
}
