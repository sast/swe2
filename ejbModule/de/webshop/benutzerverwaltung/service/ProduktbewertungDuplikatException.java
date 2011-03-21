package de.webshop.benutzerverwaltung.service;

import javax.ejb.ApplicationException;

import de.webshop.artikelverwaltung.domain.Produktbewertung;


/**
 * Exception, die ausgel&ouml;st wird, wenn eine Produktbewertung angelegt werden soll, aber ein Benutzer bereits einen Artikel bewertet hat.
 */
@ApplicationException(rollback = true)
public class ProduktbewertungDuplikatException extends AbstractBenutzerverwaltungException {
	
	private static final long serialVersionUID = -6581576854029746217L;
	private Produktbewertung produktbewertung;

	public ProduktbewertungDuplikatException(Produktbewertung produktbewertung) {
		super("Produktbewertung von Benutzer [" + produktbewertung.getBenutzer() + "] fuer Artikel [" + produktbewertung.getArtikel() + "] existiert bereits");
		this.produktbewertung = produktbewertung;
	}

	public Produktbewertung getProduktbewertung() {
		return produktbewertung;
	}
}
