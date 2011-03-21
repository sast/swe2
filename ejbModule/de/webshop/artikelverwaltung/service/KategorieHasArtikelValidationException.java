package de.webshop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;


@ApplicationException(rollback = true)
public class KategorieHasArtikelValidationException extends AbstractArtikelverwaltungException {

	private static final long serialVersionUID = -6453347286342688114L;

	private KategorieHasArtikel kha;
	private final Collection<ConstraintViolation<KategorieHasArtikel>> violations;
	
	public KategorieHasArtikelValidationException(KategorieHasArtikel kha, Collection<ConstraintViolation<KategorieHasArtikel>> violations) {
		super("Ungueltige RolleHasBenutzer: " + kha + ", Violations: " + violations);
		this.kha = kha;
		this.violations = violations; //weil violation FINAL ist, muss dieses Attribut initialisiert werden!
	}

	public KategorieHasArtikel getKategorieHasArtikel() {
		return kha;
	}

	public Collection<ConstraintViolation<KategorieHasArtikel>> getViolations() {
		return violations;
	}
	
}
