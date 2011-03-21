package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;

/**
 * Exception, die ausgel&ouml;st wird, wenn die Attributwerte eines RolleHasBenutzer-Objekts nicht korrekt sind.
 */
@ApplicationException(rollback = true)
public class RolleHasBenutzerValidationException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = 1123947990685556379L;
	private RolleHasBenutzer rhb;
	private final Collection<ConstraintViolation<RolleHasBenutzer>> violations;
	
	public RolleHasBenutzerValidationException(RolleHasBenutzer rhb, Collection<ConstraintViolation<RolleHasBenutzer>> violations) {
		super("Ungueltige RolleHasBenutzer: " + rhb + ", Violations: " + violations);
		this.rhb = rhb;
		this.violations = violations; //weil violation FINAL ist, muss dieses Attribut initialisiert werden!
	}

	public RolleHasBenutzer getRolleHasBenutzer() {
		return rhb;
	}

	public Collection<ConstraintViolation<RolleHasBenutzer>> getViolations() {
		return violations;
	}
}
