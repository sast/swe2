package de.webshop.artikelverwaltung.service;

import javax.ejb.ApplicationException;

import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;


@ApplicationException(rollback = true)
public class KategorieHasArtikelDuplikatException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = 7726598203885669493L;
	private final Long idArtikel;
	private final String kategorieBezeichnung;
	
	public KategorieHasArtikelDuplikatException(KategorieHasArtikel kha) {
		super("KategorieHasArtikel fuer Artikel-Id(" + ") und Kategorie(" + kha.getKategorie().getBezeichnung() + ") existiert bereits");
		this.idArtikel = 0L;
		this.kategorieBezeichnung = kha.getKategorie().getBezeichnung();
	}

	public Long getIdArtikel() {
		return idArtikel;
	}
	
	public String getKategorieBezeichnung() {
		return kategorieBezeichnung;
	}
}
