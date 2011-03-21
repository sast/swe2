package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Kategorie;

@ApplicationException(rollback = true)
public class InvalidKategorieBezeichnungException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = -8623896967054997322L;
	private final String bezeichnung;
	private final Collection<ConstraintViolation<Kategorie>> violations;
	
	public InvalidKategorieBezeichnungException(String bezeichnung, Collection<ConstraintViolation<Kategorie>> violations) {
		super("Ungueltige Kategorie-Bezeichnung: " + bezeichnung + ", Violations: " + violations);
		this.bezeichnung = bezeichnung;
		this.violations = violations;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	public Collection<ConstraintViolation<Kategorie>> getViolations() {
		return violations;
	}
	
}
