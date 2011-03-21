package de.webshop.benutzerverwaltung.service;

import javax.ejb.ApplicationException;

import de.webshop.benutzerverwaltung.domain.Rolle.RolleTyp;
import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein RolleHasBenutzer angelegt werden soll, aber eine Rolle einem Benutzer bereits zugewiesen ist.
 */
@ApplicationException(rollback = true)
public class RolleHasBenutzerDuplikatException extends AbstractBenutzerverwaltungException {
	
	private static final long serialVersionUID = -7265501841036732328L;
	private final Long idBenutzer;
	private final RolleTyp rolle;

	public RolleHasBenutzerDuplikatException(RolleHasBenutzer rhb) {
		super("RolleHasBenutzer fuer Benutzer-Id(" + ") und Rolle(" + rhb.getRolle().getRolle() + ") existiert bereits");
		this.idBenutzer = 0L; //rhb.getBenutzer().getIdBenutzer();
		this.rolle = rhb.getRolle().getRolle();
	}

	public Long getIdBenutzer() {
		return idBenutzer;
	}

	public RolleTyp getRolle() {
		return rolle;
	}
}
