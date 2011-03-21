package de.webshop.lagerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.lagerverwaltung.domain.Lager;

@ApplicationException(rollback = true)
public class LagerartikelValidationException extends AbstractLagerverwaltungException {

	private static final long serialVersionUID = 2525748785497843582L;
	private final Collection<ConstraintViolation<Lager>> violations;
	private final Lager lagerArtikel;
	
	public LagerartikelValidationException(Lager lagerArtikel, Collection<ConstraintViolation<Lager>> violations) {
		super("Ungueltiger Lagerartikel: " + lagerArtikel + ", Violations: " + violations);
		this.lagerArtikel = lagerArtikel;
		this.violations = violations; 
	}

	public Collection<ConstraintViolation<Lager>> getViolations() {
		return violations;
	}

	public Lager getLagerArtikel() {
		return lagerArtikel;
	}
}
