package de.webshop.artikelverwaltung.service;

import de.webshop.artikelverwaltung.domain.Kategorie;

public class KategorieDeleteSubKategorieException extends Exception {

	private static final long serialVersionUID = -7552631989267672425L;
	private final Long kategorieId;
	private final int anzahlSubkategorien;

	public KategorieDeleteSubKategorieException(Kategorie kategorie) {
		super("Kunde mit ID=" + kategorie.getIdKategorie() + " kann nicht geloescht werden: " + kategorie.getSubKategorien().size() + " Bestellung(en)");
		this.kategorieId = kategorie.getIdKategorie();
		this.anzahlSubkategorien = kategorie.getSubKategorien().size();
	}

	public Long getKategorieId() {
		return kategorieId;
	}

	public int getAnzahlSubkategorien() {
		return anzahlSubkategorien;
	}
}
