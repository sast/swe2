package de.webshop.artikelverwaltung.service;

import java.util.Date;

import javax.ejb.ApplicationException;

import de.webshop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class ArtikelDuplikatException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = -4666892546119815862L;
	private final Long id;
	private final String bezeichnung;
	private final Date erstellungsdatum;	
	
	public ArtikelDuplikatException(Artikel artikel) {
		super("Artikel \"" + artikel.getIdArtikel() + ", " + artikel.getBezeichnung() + ", " + artikel.getErstellungsdatum() + "\" existiert bereits");
		this.id = artikel.getIdArtikel();
		this.bezeichnung = artikel.getBezeichnung();
		this.erstellungsdatum = artikel.getErstellungsdatum();
		
	}

	public Long getId() {
		return id;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public Date getErstellungsdatum() {
		return erstellungsdatum;
	}
}
