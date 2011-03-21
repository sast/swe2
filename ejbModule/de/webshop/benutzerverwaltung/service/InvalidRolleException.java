package de.webshop.benutzerverwaltung.service;

import java.util.Collection;

import javax.validation.ConstraintViolation;

import de.webshop.benutzerverwaltung.domain.Rolle;
import de.webshop.benutzerverwaltung.domain.Rolle.RolleTyp;

public class InvalidRolleException extends AbstractBenutzerverwaltungException {

	private static final long serialVersionUID = -1734502907985351373L;
	private final RolleTyp rolleTyp;
	private final Collection<ConstraintViolation<Rolle>> violations;
	
	public InvalidRolleException(RolleTyp rolleTyp, Collection<ConstraintViolation<Rolle>> violations) {
		super("Ungueltige Rolle: " + rolleTyp + ", Violations: " + violations);
		this.rolleTyp = rolleTyp;
		this.violations = violations;
	}
	
	public RolleTyp getRolleTyp() {
		return rolleTyp;
	}
	
	public Collection<ConstraintViolation<Rolle>> getViolations() {
		return violations;
	}
}
