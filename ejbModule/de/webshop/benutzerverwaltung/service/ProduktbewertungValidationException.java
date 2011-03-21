package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.Produktbewertung;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte einer Produktbewertung nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class ProduktbewertungValidationException extends AbstractBenutzerverwaltungException {
	//das Erben eines nicht Default-Konstruktors besagt lediglich, dass auch ein expliziter Konstruktur in der erbenden Klasse implementiert werden muss!

	private static final long serialVersionUID = 4436329702290602500L;
	private Produktbewertung produktbewertung;
	private final Collection<ConstraintViolation<Produktbewertung>> violations;
	
	public ProduktbewertungValidationException(Produktbewertung produktbewertung, Collection<ConstraintViolation<Produktbewertung>> violations) {
		super("Ungueltige Produktbewertung: " + produktbewertung + ", Violations: " + violations);
		this.produktbewertung = produktbewertung;
		this.violations = violations; //weil violation FINAL ist, muss dieses Attribut initialisiert werden!
	}

	public Produktbewertung getProduktbewertung() {
		return produktbewertung;
	}

	public Collection<ConstraintViolation<Produktbewertung>> getViolations() {
		return violations;
	}
}
