package de.webshop.lagerverwaltung.service;

import javax.ejb.ApplicationException;

import de.webshop.lagerverwaltung.domain.Lager;

@ApplicationException(rollback = true)
public class LagerArtikelDuplikatException extends AbstractLagerverwaltungException {

	private static final long serialVersionUID = 9070642128921550866L;
	private final Long id;
	private final String farbe;
	private final String groesse;

	public LagerArtikelDuplikatException(Lager artikelLager) {
		super("Lagerartikel \"" + artikelLager.getIdlager() + ", " + artikelLager.getFarbe() + ", " + artikelLager.getGroesse() + "\" existiert bereits");
		this.id = artikelLager.getIdlager();
		this.farbe = artikelLager.getFarbe().toString();
		this.groesse = artikelLager.getGroesse();
	}

	public Long getId() {
		return id;
	}

	public String getFarbe() {
		return farbe;
	}

	public String getGroesse() {
		return groesse;
	}

}
