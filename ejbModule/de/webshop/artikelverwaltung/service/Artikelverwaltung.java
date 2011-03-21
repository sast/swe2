package de.webshop.artikelverwaltung.service;

import static de.webshop.util.AbstractDao.QueryParameter.with;
import static de.webshop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.ejb3.annotation.IgnoreDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Kategorie;
import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;
import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.benutzerverwaltung.service.Benutzerverwaltung;
import de.webshop.benutzerverwaltung.service.InvalidBenutzerIdException;
import de.webshop.util.IdGroup;
import de.webshop.util.NotFoundException;
import de.webshop.util.ValidationService;

@Stateless
public class Artikelverwaltung implements Serializable {

	private static final long serialVersionUID = 1549626865845818966L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Artikelverwaltung.class);
	//private static final boolean TRACE = LOGGER.isTraceEnabled();
	
	public static final String NO_ARTIKEL_FOUND_WITH_ID = "Keinen Artikel gefunden mit der ID: ";
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ArtikelverwaltungDao dao;
	
	@IgnoreDependency
	@EJB
	private Benutzerverwaltung bv;
	
	@EJB
	private ValidationService validationService;
	
	public Artikel reloadArtikel(Artikel artikel) {
		return dao.reloadArtikel(artikel);
	}
	
	public Artikel findArtikelById(Long id, Locale locale) throws NotFoundException, InvalidArtikelIdException {
		validateArtikelId(id, locale);
		final Artikel artikel = dao.find(Artikel.class, id);
		return artikel;
	}
	
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) throws NotFoundException {
		List<Artikel> artikel;
		
		if (bezeichnung == null || bezeichnung.isEmpty()) {
			artikel = dao.find(Artikel.FIND_ARTIKEL, Artikel.class);
			return artikel;
		}
		artikel = dao.find(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG,
							with(Artikel.PARAM_BEZEICHNUNG, "%" + bezeichnung + "%").parameters(),
							Artikel.class);
		
		return artikel;
	}
	
	public List<Artikel> findArtikelByKategorieId(Long id, Locale locale) throws NotFoundException, InvalidKategorieIdException {
		validateKategorieId(id, locale);
		
		final List<Artikel> artikel = dao.find(Artikel.FIND_ARTIKEL_BY_KATEGORIE_ID_FETCH_KATEGORIE, 
												with(Artikel.PARAM_KATEGORIE_ID, id).parameters(),
												Artikel.class);
		return artikel;
	}
	
	public List<Artikel> findArtikelByKategorie(String bezeichnung, Locale locale) throws NotFoundException, InvalidKategorieBezeichnungException {
		validateKategorieBezeichnung(bezeichnung, locale);
		final List<Artikel> artikel = dao.find(Artikel.FIND_ARTIKEL_BY_KATEGORIE_FETCH_KATEGORIE, 
												with(Artikel.PARAM_KATEGORIE, bezeichnung).parameters(),
												Artikel.class);
		return artikel;
	}
	
	
	public Kategorie findKategorieById(Long id, Locale locale) throws InvalidKategorieIdException, NotFoundException {
		validateKategorieId(id, locale);
		final Kategorie kategorie = dao.find(Kategorie.class, id);
		return kategorie;
	}
	
	public Kategorie createKategorie(Kategorie kategorie, Locale locale, boolean uiValidated) throws KategorieValidationException {
		
		if (kategorie == null) {
			return kategorie;
		}
		
		validateKategorie(kategorie, locale, uiValidated, Default.class);
		
		kategorie.setIdKategorie(KEINE_ID);
		
		dao.create(kategorie);
		
		return kategorie;
	}
	
	private void validateKategorie(Kategorie kategorie, Locale locale,
			boolean uiValidated, Class<Default> group) throws KategorieValidationException {
		
		LOGGER.debug("BEGIN validateKategorie: kategorie={}, locale={}, uiValidated={}", new Object[] {kategorie, locale, uiValidated});
		
		if (uiValidated) {
			LOGGER.debug("END validateKategorie");
			return;
		}
		
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Kategorie>> violations = validator.validate(kategorie, group);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debug("END validateKategorie: {}", violations);
			throw new KategorieValidationException(kategorie, violations);
		}
		
		LOGGER.debug("END validateArtikel");
		
	}

	public KategorieHasArtikel createKategorieHasArtikel(Kategorie kategorie, Artikel artikel, Locale locale, boolean uiValidated) throws KategorieHasArtikelValidationException, KategorieHasArtikelDuplikatException {
		artikel = dao.reloadArtikel(artikel);
		
		KategorieHasArtikel kha = new KategorieHasArtikel();
		kha.setKategorie(kategorie);
		
		if (!uiValidated) {
			final Validator validator = validationService.getValidator(locale);
			final Set<ConstraintViolation<KategorieHasArtikel>> violations = validator.validate(kha);
		
			if (!violations.isEmpty()) {
				throw new KategorieHasArtikelValidationException(kha, violations);
			}
		}
		
		if (artikel.getKategorien().contains(kha)) {
			throw new KategorieHasArtikelDuplikatException(kha);
		}
		
		artikel.addKategorieHasArtikel(kha);
		kha = dao.create(kha);
		
		return kha;
	}
	
	private void validateKategorieBezeichnung(String bezeichnung, Locale locale) throws InvalidKategorieBezeichnungException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kategorie>> violations = validator.validateValue(Kategorie.class, "bezeichnung", bezeichnung);	
		
		if (!violations.isEmpty()) {
			throw new InvalidKategorieBezeichnungException(bezeichnung, violations);
		}
	}

	public void validateArtikelId(Long id, Locale locale) throws InvalidArtikelIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class, "idArtikel", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidArtikelIdException(id, violations);
		}
	}
	
	private void validateArtikel(Artikel artikel, Locale locale, boolean uiValidated, Class<?> group) throws ArtikelValidationException {
		LOGGER.debug("BEGIN validateArtikel: artikel={}, locale={}, uiValidated={}", new Object[] {artikel, locale, uiValidated});
		
		if (uiValidated) {
			LOGGER.debug("END validateArtikel");
			return;
		}
		
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, group);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debug("END validateArtikel: {}", violations);
			throw new ArtikelValidationException(artikel, violations);
		}
		
		LOGGER.debug("END validateArtikel");
	}
	
	
	public Artikel createArtikel(Artikel artikel, Locale locale, boolean uiValidated) throws NotFoundException, ArtikelValidationException, ArtikelDuplikatException {
		
		if (artikel == null) {
			return artikel;
		}
		
		//TODO: nochmal ueberdenken!  Parameter Default.class nicht an dieser Stelle!! --> wird in der validate-Methode fest definiert!!
		validateArtikel(artikel, locale, uiValidated, Default.class);
		
		Artikel artikelVorhanden = null;
		try {
			artikelVorhanden = dao.findSingle(Artikel.FIND_ARTIKEL_BY_ID, 
									with(Artikel.PARAM_ID, artikel.getIdArtikel()).parameters(), 
									Artikel.class);
		} 
		catch (NotFoundException e) {
			//ist OK, falls Benutzer noch nicht existiert!
		}
		
		if (artikelVorhanden != null) {
			LOGGER.debug("Artikel existiert bereits!");
			throw new ArtikelDuplikatException(artikel);
		}
		LOGGER.debug("Artikel existiert nicht");
		
		artikel.setIdArtikel(KEINE_ID);
		dao.create(artikel);
		
		return artikel;
	}
	
	public Artikel updateArtikel(Artikel artikel, Locale locale, boolean uiValidated) throws ArtikelValidationException {
		
		if (artikel == null) {
			return artikel;
		}
		validateArtikel(artikel, locale, uiValidated, Default.class);
		
		dao.update(artikel);
		return artikel;
	}
	
	public Kategorie updateKategorie(Kategorie kategorie, Locale locale, boolean uiValidated) throws KategorieValidationException  {
		
		if (kategorie == null) {
			return kategorie;
		}
		validateKategorie(kategorie, locale, uiValidated, Default.class);
	
		dao.update(kategorie);
		return kategorie;
	}
	
	private void validateKategorieId(Long id, Locale locale) throws InvalidKategorieIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Kategorie>> violations = validator.validateValue(Kategorie.class, "idKategorie", id, IdGroup.class);
		
		if (!violations.isEmpty()) {
			throw new InvalidKategorieIdException(id, violations);
		}
	}
		
	private void validateProduktbewertungId(Long id, Locale locale) throws InvalidProduktbewertungIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Produktbewertung>> violations = validator.validateValue(Produktbewertung.class, "idProduktbewertung", id, IdGroup.class);
		
		if (!violations.isEmpty()) {
			throw new InvalidProduktbewertungIdException(id, violations);
		}
	}
	
	public Produktbewertung findProduktbewertungById(Long id, Locale locale) throws NotFoundException, InvalidProduktbewertungIdException {
		validateProduktbewertungId(id, locale);
		final Produktbewertung bewertung = dao.find(Produktbewertung.class, id);
		return bewertung;
	}
	
	public List<Produktbewertung> findProduktbewertungByBenutzerId(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		bv.validateBenutzerId(id, locale);
		final List<Produktbewertung> bewertung = dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID, 
															with(Produktbewertung.PARAM_BENUTZER_ID, id).parameters(),
															Produktbewertung.class);
		return bewertung;
		
	}
	
	public List<Produktbewertung> findProduktbewertungByArtikelId(Long id, Locale locale) throws NotFoundException, InvalidArtikelIdException {
		validateArtikelId(id, locale);
		final List<Produktbewertung> bewertung = dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_ARTIKEL_ID,
															with(Produktbewertung.PARAM_ARTIKEL_ID, id).parameters(),
															Produktbewertung.class);
		
		return bewertung;
	}
	
	//TODO Nochmal anschauen wegen datentyp bewertung 
	public List<Produktbewertung> findProduktbewertungByBewertung(Integer bewert, Locale locale) throws NotFoundException {
		
		final List<Produktbewertung> bewertung = dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_BEWERTUNG,
															with(Produktbewertung.PARAM_BEWERTUNG, bewert).parameters(),
															Produktbewertung.class);
		
		return bewertung;
	}
	
	
	public List<Produktbewertung> findProduktbewertungByKommentarExists(Locale locale) throws NotFoundException {
		
		final List<Produktbewertung> bewertung = dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR_EXISTS, Produktbewertung.class);
		
		return bewertung;
	}
	
	public List<Kategorie> findSubKategorieByMainKategorieId(Long id, Locale locale) throws NotFoundException, InvalidKategorieIdException {
		validateKategorieId(id, locale);
		final List<Kategorie> kategorie = dao.find(Kategorie.FIND_SUB_KATEGORIEN_BY_MAIN_KATEGORIE_ID,
													with(Kategorie.PARAM_ID_KATEGORIE, id).parameters(),
													Kategorie.class);
		return kategorie;
	}
	
	public void deleteKategorie(Kategorie kategorie) throws KategorieDeleteSubKategorieException, NotFoundException  {
		if (kategorie == null) {
			return;
		}
		if (!kategorie.getSubKategorien().isEmpty()) {
			throw new KategorieDeleteSubKategorieException(kategorie);
		}
		
		kategorie = dao.find(Kategorie.class, kategorie.getIdKategorie());
		dao.delete(kategorie, kategorie.getIdKategorie());
	}

	public KategorieHasArtikel findKategorieHasArtikelById(Long id,
			Locale locale) throws NotFoundException, InvalidKategorieHasArtikelIdException {
		validateKategorieHasArtikelId(id, locale);
		final KategorieHasArtikel kategorieHasArtikel = dao.find(KategorieHasArtikel.class, id);
		return kategorieHasArtikel;
	}

	private void validateKategorieHasArtikelId(Long id, Locale locale) throws InvalidKategorieHasArtikelIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<KategorieHasArtikel>> violations = validator.validateValue(KategorieHasArtikel.class, "idkategoriehasartikel", id, IdGroup.class);
		
		if (!violations.isEmpty()) {
			throw new InvalidKategorieHasArtikelIdException(id, violations);
		}
	}

	public void deleteKategorieHasArtikel(
			KategorieHasArtikel kategorieHasArtikel, Locale locale) throws NotFoundException {
		if (kategorieHasArtikel == null) {
			return;
		}
		try {
			kategorieHasArtikel = findKategorieHasArtikelById(kategorieHasArtikel.getIdkategoriehasartikel(), locale); 
		}
		catch (InvalidKategorieHasArtikelIdException e) {
			return;
		}
		
		dao.delete(kategorieHasArtikel, kategorieHasArtikel.getIdkategoriehasartikel());	
	}
}
