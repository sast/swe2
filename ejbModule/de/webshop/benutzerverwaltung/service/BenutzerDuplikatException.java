package de.webshop.benutzerverwaltung.service;

import javax.ejb.ApplicationException;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;

/**
 * Exception, die ausgel&ouml;st wird, wenn ein Kunde angelegt werden soll, aber bereits ein Kunde
 * mit gleichen Daten existiert
 */
@ApplicationException(rollback = true)
public class BenutzerDuplikatException extends AbstractBenutzerverwaltungException {
	private static final long serialVersionUID = 4867667611097919943L;
	private final String nachname;
	private final String vorname;
	private final String email;

	public BenutzerDuplikatException(AbstractBenutzer benutzer) {
		super("Benutzer \"" + benutzer.getNachname() + ", " + benutzer.getVorname() + ", " + benutzer.getEmail() + "\" existiert bereits");
		this.nachname = benutzer.getNachname();
		this.vorname = benutzer.getVorname();
		this.email = benutzer.getEmail();
	}

	public String getNachname() {
		return nachname;
	}
	
	public String getVorname() {
		return vorname;
	}

	public String getEmail() {
		return email;
	}
}
