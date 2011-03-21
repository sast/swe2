package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines Benutzers nicht korrekt sind
 */
@ApplicationException(rollback = true)
public class BenutzerValidationException extends AbstractBenutzerverwaltungException {
	//das Erben eines nicht Default-Konstruktors besagt lediglich, dass auch ein expliziter Konstruktur in der erbenden Klasse implementiert werden muss!

	private static final long serialVersionUID = 4404748650474900570L;

	private AbstractBenutzer benutzer;
	private final Collection<ConstraintViolation<AbstractBenutzer>> violations;
	
	public BenutzerValidationException(AbstractBenutzer benutzer, Collection<ConstraintViolation<AbstractBenutzer>> violations) {
		super("Ungueltiger Benutzer: " + benutzer + ", Violations: " + violations);
		this.benutzer = benutzer;
		this.violations = violations; //weil violation FINAL ist, muss dieses Attribut initialisiert werden!
	}

	public AbstractBenutzer getBenutzer() {
		return benutzer;
	}

	public Collection<ConstraintViolation<AbstractBenutzer>> getViolations() {
		return violations;
	}
}
