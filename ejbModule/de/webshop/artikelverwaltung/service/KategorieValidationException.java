package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Kategorie;

public class KategorieValidationException extends Exception {

	private static final long serialVersionUID = -6423174514970057857L;
	private final Collection<ConstraintViolation<Kategorie>> violations;
	private final Kategorie kategorie;
	
	public KategorieValidationException(Kategorie kategorie, Collection<ConstraintViolation<Kategorie>> violations) {
		
		super("Ungueltige Kategorie: " + kategorie + ", Violations: " + violations);
		this.kategorie = kategorie;
		this.violations = violations; 
	}
	
	public Kategorie getKategorie() {
		return kategorie;
	}
	
	public Collection<ConstraintViolation<Kategorie>> getViolations() {
		return violations;
	}
}
