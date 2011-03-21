package de.webshop.benutzerverwaltung.service;

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
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.ejb3.annotation.IgnoreDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.artikelverwaltung.service.Artikelverwaltung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidProduktbewertungIdException;
import de.webshop.benutzerverwaltung.domain.AbstractAdresse;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.domain.Lieferadresse;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer.PasswordGroup;
import de.webshop.benutzerverwaltung.domain.Rolle;
import de.webshop.benutzerverwaltung.domain.Rolle.RolleTyp;
import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;
import de.webshop.util.IdGroup;
import de.webshop.util.NotFoundException;
import de.webshop.util.ValidationService;

//HINWEIS: '%' wird von aussen bereits im Wert des Methodenparameter uebergeben!
//TODO: Mailversand beim createBenutzer

@Stateless
public class Benutzerverwaltung implements Serializable {

	private static final long serialVersionUID = -8701804652965660766L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Benutzerverwaltung.class);
	
	public static final String NO_USER_FOUND_WITH_ID = "Keinen Benutzer gefunden mit der ID: ";
	public static final String NO_USERS_FOUND = "Keine Benutzer gefunden!";
	public static final String NO_USER_FOUND_WITH_EMAIL = "Keinen Benutzer gefunden mit der Email: ";
	
	public static final String NO_PRODUKTBEWERTUNG_FOUND_WITH_ID = "Keine Produktbewertung gefunden mit der ID: ";
	public static final String NO_PRODUKTBEWERTUNG_FOUND = "Keine Produktbewertung(en) gefunden!";
	
	//@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private BenutzerverwaltungDAO dao;
	
	@IgnoreDependency
	@EJB
	private Artikelverwaltung av;
	
	@EJB
	private ValidationService validationService;

	//"create" erfolgt durch setzen der Lieferadresse am Benutzer und dessen update
	public void deleteLieferadresse(Lieferadresse lieferadresse) throws NotFoundException {
		if (lieferadresse == null) {
			return;
		}
		
		lieferadresse = dao.find(Lieferadresse.class, lieferadresse.getIdadresse());
		
		dao.delete(lieferadresse, lieferadresse.getIdadresse());
	}
	
	public void deleteProduktbewertung(Produktbewertung produktbewertung, Locale locale) throws NotFoundException {
		if (produktbewertung == null) {
			return;
		}
		
		//dao.find(Produktbewertung.class, produktbewertung.getIdProduktbewertung());
		try {
			produktbewertung = findProduktbewertungById(produktbewertung.getIdProduktbewertung(), locale);
		}
		catch (InvalidProduktbewertungIdException e) {
			return;
		}
		
		dao.delete(produktbewertung, produktbewertung.getIdProduktbewertung());
	}
	
	public Produktbewertung createProduktbewertung(Produktbewertung produktbewertung, AbstractBenutzer benutzer, Artikel artikel, Locale locale, boolean uiValidated) throws ProduktbewertungValidationException, ProduktbewertungDuplikatException {
		if (produktbewertung == null) {
			return null;
		}
		
		if (!uiValidated) {
			final Validator validator = validationService.getValidator(locale);
			final Set<ConstraintViolation<Produktbewertung>> violations = validator.validate(produktbewertung, Default.class);
		
			if (!violations.isEmpty()) {
				throw new ProduktbewertungValidationException(produktbewertung, violations);
			}
		}
		
		Produktbewertung produktbewertungVorhanden = null;
		try {
			produktbewertungVorhanden = dao.findSingle(Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID_AND_ARTIKEL_ID, with(Produktbewertung.PARAM_BENUTZER_ID, benutzer.getIdBenutzer()).and(Produktbewertung.PARAM_ARTIKEL_ID, artikel.getIdArtikel()).parameters(), Produktbewertung.class);
		} 
		catch (NotFoundException e) {
		
		}
		
		if (produktbewertungVorhanden != null) {
			//uebergibt produktbewertungVorhanden, da in diesem Objekt (produktbewertung hat keine Referenz) der Verweis auf Benutzer & Artikel existiert
			throw new ProduktbewertungDuplikatException(produktbewertungVorhanden);
		}

		//bei WebServices pruefen, ob benutzer / artikel nach CREATE automatisch vom EM neu gezogen /aktualisiert werden --> falls nicht, waere Verwendung/Nutzen eindeutig!!
		benutzer = dao.reloadBenutzer(benutzer);
		artikel = av.reloadArtikel(artikel);
		
		produktbewertung.setBenutzer(benutzer);
		produktbewertung.setArtikel(artikel);
		
		benutzer.addProduktbewertung(produktbewertung);
		artikel.addProduktbewertung(produktbewertung);
		
		dao.create(produktbewertung);
		
		return produktbewertung;
	}
	
	public void deleteRolleHasBenutzer(AbstractBenutzer benutzer, Rolle rolle) throws NotFoundException {
		if (benutzer == null || rolle == null) {
			return;
		}
		
		benutzer = dao.reloadBenutzer(benutzer);
		
		//RolleHasBenutzer rolleHasBenutzer = null;
		for (RolleHasBenutzer rhb : benutzer.getRollehasbenutzer()) {
			if (rhb.getRolle().equals(rolle)) {
				benutzer.removeRollehasbenutzer(rhb); //--> nicht wirklich notwendig, aber aus Gruenden der Konsistenz ist der Befehl auszufuehren!
				dao.delete(rhb, rhb.getIdRolleHasBenutzer());
				return;
			}
		}
		
		throw new NotFoundException("Rolle ist dem Benutzer nicht zugewiesen.", RolleHasBenutzer.class);
	}
	
	//FIXME obere Methode fuer spaetere Verwendung sinnvoller
	public void deleteRolleHasBenutzer(RolleHasBenutzer rhb, Locale locale) throws NotFoundException {
		if (rhb == null) {
			return;
		}
		
		try {
			rhb = findRolleHasBenutzerById(rhb.getIdRolleHasBenutzer(), locale);
		}
		catch (InvalidRolleHasBenutzerIdException e) {
			return;
		}
		
		dao.delete(rhb, rhb.getIdRolleHasBenutzer());
	}
	
	public RolleHasBenutzer createRolleHasBenutzer(AbstractBenutzer benutzer, Rolle rolle, Locale locale, boolean uiValidated) throws RolleHasBenutzerValidationException, RolleHasBenutzerDuplikatException {
		benutzer = dao.reloadBenutzer(benutzer);
		
		RolleHasBenutzer rhb = new RolleHasBenutzer();
		rhb.setRolle(rolle);
		
		if (!uiValidated) {
			final Validator validator = validationService.getValidator(locale);
			final Set<ConstraintViolation<RolleHasBenutzer>> violations = validator.validate(rhb);
		
			if (!violations.isEmpty()) {
				throw new RolleHasBenutzerValidationException(rhb, violations);
			}
		}
		
		//pruefe, ob Rolle dem Benutzer bereits zugewiesen ist!
		if (benutzer.getRollehasbenutzer().contains(rhb)) {
			throw new RolleHasBenutzerDuplikatException(rhb);
		}
		
		benutzer.addRollehasbenutzer(rhb);
		
		rhb = dao.create(rhb);
		
		return rhb;
	}
	
// ####  CREATE / UPDATE / DELETE - Benutzer  ####
	public AbstractBenutzer createBenutzer(AbstractBenutzer benutzer, Locale locale, boolean uiValidated) throws BenutzerValidationException, BenutzerDuplikatException {
		if (benutzer == null) {
			return benutzer;
		}
		
		//Benutzer validieren und ggf. Exception werfen!
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<AbstractBenutzer>> violations = 
			uiValidated
			? validator.validate(benutzer, PasswordGroup.class)
			: validator.validate(benutzer, Default.class, PasswordGroup.class);
		
		if (!violations.isEmpty()) {
			throw new BenutzerValidationException(benutzer, violations);
		}

		AbstractBenutzer benutzerVorhanden = null;
		try {
			//wuerde eine erneute Valdierung der Email mit sich ziehen: findBenutzerByEmail(benutzer.getEmail(), locale);
			//deshalb besser:
			benutzerVorhanden = dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_EMAIL, 
									with(AbstractBenutzer.PARAM_EMAIL, benutzer.getEmail()).parameters(), 
									AbstractBenutzer.class);
		} 
		catch (NotFoundException e) {
			//ist OK, falls Benutzer noch nicht existiert!
		}
		
		if (benutzerVorhanden != null) {
			LOGGER.debug("Benutzer existiert bereits!");
			throw new BenutzerDuplikatException(benutzer);
		}
		LOGGER.debug("Benutzer existiert nicht");
		
		benutzer.setIdBenutzer(KEINE_ID);
		dao.create(benutzer);
		
		return benutzer;
	}
	
	public AbstractBenutzer updateBenutzer(AbstractBenutzer benutzer, Locale locale, boolean uiValidated) throws BenutzerValidationException, NotFoundException, BenutzerDuplikatException {
		if (benutzer == null) {
			return null;
		}
		
		//Benutzer validieren und ggf. Exception werfen!
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<AbstractBenutzer>> violations = 
			uiValidated
			? validator.validate(benutzer, PasswordGroup.class, IdGroup.class)
			: validator.validate(benutzer, Default.class, PasswordGroup.class, IdGroup.class);
		
		if (!violations.isEmpty()) {
			throw new BenutzerValidationException(benutzer, violations);
		}
		
		AbstractBenutzer vorhandenerBenutzer = dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_EMAIL, 
													with(AbstractBenutzer.PARAM_EMAIL, benutzer.getEmail()).parameters(), 
													AbstractBenutzer.class);

		if (vorhandenerBenutzer != null && !vorhandenerBenutzer.getIdBenutzer().equals(benutzer.getIdBenutzer())) {
			LOGGER.debug("Benutzer mit gleicher Email:{}, aber unterschiedlicher ID:{}!", benutzer.getEmail(), benutzer.getIdBenutzer());
			throw new BenutzerDuplikatException(benutzer);
		}
		LOGGER.debug("Kein Benutzerduplikat!");
		
		dao.update(benutzer);
		
		return benutzer;
	}
	
	public void deleteBenutzer(AbstractBenutzer benutzer) throws NotFoundException {
		if (benutzer == null) {
			return;
		}
		
		try {
			benutzer = findBenutzerByIDFetchBestellungen(benutzer.getIdBenutzer(), null);
		} 
		catch (InvalidBenutzerIdException e) {
			return;
		}
		
		//TODO pruefe auf noch offene Bestellungen, da Benutzer in diesem Falle nicht geloescht werden darf!
		
		dao.delete(benutzer, benutzer.getIdBenutzer());
	}
	
// ####  CREATE / UPDATE / DELETE - Benutzer - END  ####

// ####  query-methods  ####
	public Lieferadresse findLieferadresseById(Long idlieferadresse, Locale locale) throws NotFoundException, InvalidAdresseIdException {
		validateAdresseId(idlieferadresse, locale);
		return dao.find(Lieferadresse.class, idlieferadresse);
	}
	
	public RolleHasBenutzer findRolleHasBenutzerById(Long idRolleHasBenutzer, Locale locale) throws NotFoundException, InvalidRolleHasBenutzerIdException {
		validateRolleHasBenutzerId(idRolleHasBenutzer, locale);
		return dao.find(RolleHasBenutzer.class, idRolleHasBenutzer);
	}
	
	public Rolle findRolleById(Long idRolle, Locale locale) throws NotFoundException, InvalidRolleIdException {
		validateRolleId(idRolle, locale);
		return dao.find(Rolle.class, idRolle);
	}
	
	public Produktbewertung findProduktbewertungById(Long idProduktbewertung, Locale locale) throws NotFoundException, InvalidProduktbewertungIdException {
		validateProduktbewertungId(idProduktbewertung, locale);
		return dao.find(Produktbewertung.class, idProduktbewertung);
	}
	
	public List<Produktbewertung> findProduktbewertungen() throws NotFoundException {
		return dao.find(Produktbewertung.FIND_BEWERTUNGEN, Produktbewertung.class);
	}
	
	public List<Produktbewertung> findProduktbewertungenByArtikelId(Long idArtikel, Locale locale) throws NotFoundException, InvalidArtikelIdException {
		av.validateArtikelId(idArtikel, locale);
				
		return dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_ARTIKEL_ID, with(Produktbewertung.PARAM_ARTIKEL_ID, idArtikel).parameters(), Produktbewertung.class);
	}
	
	public List<Produktbewertung> findProduktbewertungenByBenutzerId(Long idBenutzer, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(idBenutzer, locale);
		
		return dao.find(Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID, with(Produktbewertung.PARAM_BENUTZER_ID, idBenutzer).parameters(), Produktbewertung.class);
	}
	
	public Produktbewertung findProduktbewertungByBenutzerIdAndArtikelId(Long idBenutzer, Long idArtikel, Locale locale) throws NotFoundException, InvalidBenutzerIdException, InvalidArtikelIdException {
		validateBenutzerId(idBenutzer, locale);
		av.validateArtikelId(idArtikel, locale);
		
		return dao.findSingle(Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID_AND_ARTIKEL_ID, with(Produktbewertung.PARAM_BENUTZER_ID, idBenutzer).and(Produktbewertung.PARAM_ARTIKEL_ID, idArtikel).parameters(), Produktbewertung.class);
	}
	
	//TODO: nur zu Testzwecken!
	public List<AbstractBenutzer> findBenutzerNFetchBestellungen() {
		
		String hql = "SELECT b FROM AbstractBenutzer b LEFT JOIN FETCH b.bestellungen";
		
		final TypedQuery<AbstractBenutzer> query = em.createQuery(hql, AbstractBenutzer.class);
		final List<AbstractBenutzer> result = query.getResultList();
	
		return result;
	}
	
	public List<AbstractBenutzer> findBenutzerN() throws NotFoundException {
		return dao.find(AbstractBenutzer.FIND_BENUTZER, AbstractBenutzer.class);
	}
	
	//TODO: ID in Id umbenennen!!
	public AbstractBenutzer findBenutzerByID(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(id, locale);
		
		return dao.find(AbstractBenutzer.class, id);
	}

	public AbstractBenutzer findBenutzerByEmail(String email, Locale locale) throws NotFoundException, InvalidEmailException {
		validateEmail(email, locale);
		
		return dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_EMAIL, with(AbstractBenutzer.PARAM_EMAIL, email).parameters(), AbstractBenutzer.class);
	}

	/**
	 * 
	 * @param plz - PLZ der Rechnungsadresse, d.h. eine eventl. existierende Lieferadresse bleibt aussen vor.
	 * @param locale
	 * @return
	 * @throws NotFoundException
	 * @throws InvalidPlzException
	 */
	public List<AbstractBenutzer> findBenutzerNByPLZ(String plz, Locale locale) throws NotFoundException, InvalidPlzException {
		validatePLZ(plz, locale);
		
		return dao.find(AbstractBenutzer.FIND_BENUTZER_N_BY_PLZ, with(AbstractBenutzer.PARAM_PLZ, plz).parameters(), AbstractBenutzer.class);
	}

	/**
	 * Diese Methode gibt alle Benutzer zurueck, die einen bestimmten Nachnamen besitzen.
	 * @param nachname Nachname oder Teilstring eines Benutzernachnamens
	 * @return List<AbstractBenutzer> mit Benutzerobjekten
	 * @throws NotFoundException 
	 */
	public List<AbstractBenutzer> findBenutzerNByNachname(String nachname, Locale locale, boolean validate) throws NotFoundException, InvalidNachnameException {
		//Problem, validate schraenkt die Suchmoeglichkeit ein, da man z.B. nicht alle Benutzer deren Nachname mit 'S' beginnt suchen kann, bzw. immer explizit nen Grossbuchstaben angeben muss!
		//Loesung: boolean zur Steuerung der Validierung
		if (validate) {
			validateNachname(nachname, locale);
		}
		
		return  dao.find(AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME, with(AbstractBenutzer.PARAM_NACHNAME, nachname).parameters(), AbstractBenutzer.class);
	}

	public List<AbstractBenutzer> findBenutzerNByRolle(RolleTyp rolleTyp, Locale locale) throws NotFoundException, InvalidRolleException {
		validateRolle(rolleTyp, locale);
		
		return dao.find(AbstractBenutzer.FIND_BENUTZER_N_BY_ROLLE, with(AbstractBenutzer.PARAM_ROLLE, rolleTyp).parameters(), AbstractBenutzer.class);
	}

	public List<AbstractBenutzer> findBenutzerNByNachnameFetchBestellungen(String nachname, Locale locale, boolean validate) throws NotFoundException, InvalidNachnameException {
		if (validate) {
			validateNachname(nachname, locale);
		}
		
		return dao.find(AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME_FETCH_BESTELLUNGEN, with(AbstractBenutzer.PARAM_NACHNAME, nachname).parameters(), AbstractBenutzer.class);
	}
	
	public AbstractBenutzer findBenutzerByIDFetchRollen(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(id, locale);
		
		return dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_ROLLEN, with(AbstractBenutzer.PARAM_ID, id).parameters(), AbstractBenutzer.class);
	}
	
	public AbstractBenutzer findBenutzerByIDFetchBestellungen(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(id, locale);
		
		return dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_BESTELLUNGEN, with(AbstractBenutzer.PARAM_ID, id).parameters(), AbstractBenutzer.class);
	}
	
	public AbstractBenutzer findBenutzerByIDFetchProduktbewertungen(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(id, locale);
		
		return dao.findSingle(AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_PRODUKTBEWERTUNGEN, with(AbstractBenutzer.PARAM_ID, id).parameters(), AbstractBenutzer.class);
	}
	
	public AbstractBenutzer findBenutzerByIDFetchAll(Long id, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		validateBenutzerId(id, locale);
		
		return dao.findBenutzerByIDFetchAll(id);
	}
// ####  query-methods - END  ####
	
// ####  validate-methods  ####
	public void validateAdresseId(Long id, Locale locale) throws InvalidAdresseIdException {
		LOGGER.debug("BEGINN validateAdresseId: id={}", id);
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<AbstractAdresse>> violations = validator.validateValue(AbstractAdresse.class, "idadresse", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidAdresseIdException(id, violations);
		}
		LOGGER.debug("ENDE validateAdresseId");
	}
	
	public void validateRolleId(Long id, Locale locale) throws InvalidRolleIdException {
		LOGGER.debug("BEGINN validateRolleId: id={}", id);
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Rolle>> violations = validator.validateValue(Rolle.class, "idrolle", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidRolleIdException(id, violations);
		}
		LOGGER.debug("ENDE validateRolleId");
	}
	
	public void validateRolleHasBenutzerId(Long id, Locale locale) throws InvalidRolleHasBenutzerIdException {
		LOGGER.debug("BEGINN validateRolleHasBenutzerId: id={}", id);
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<RolleHasBenutzer>> violations = validator.validateValue(RolleHasBenutzer.class, "idRolleHasBenutzer", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidRolleHasBenutzerIdException(id, violations);
		}
		LOGGER.debug("ENDE validateRolleHasBenutzerId");
	}
	
	public void validateProduktbewertungId(Long id, Locale locale) throws InvalidProduktbewertungIdException {
		LOGGER.debug("BEGINN validateProduktbewertungId: id={}", id);
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Produktbewertung>> violations = validator.validateValue(Produktbewertung.class, "idProduktbewertung", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidProduktbewertungIdException(id, violations);
		}
		LOGGER.debug("ENDE validateProduktbewertungId");
	}
	
	public void validateBenutzerId(Long id, Locale locale) throws InvalidBenutzerIdException {
		LOGGER.debug("BEGINN validateBenutzerId: id={}", id);
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<AbstractBenutzer>> violations = validator.validateValue(AbstractBenutzer.class, "idBenutzer", id, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidBenutzerIdException(id, violations);
		}
		LOGGER.debug("ENDE validateBenutzerId");
	}
	
	private void validateEmail(String email, Locale locale) throws InvalidEmailException {
		LOGGER.debug("BEGINN validateEmail: email={}", email);
		final Validator validator = validationService.getValidator(locale);
		Set<ConstraintViolation<AbstractBenutzer>> violations = validator.validateValue(AbstractBenutzer.class, "email", email);	
		
		if (!violations.isEmpty()) {
			throw new InvalidEmailException(email, violations);
		}
		LOGGER.debug("ENDE validateEmail");
	}
	
	private void validatePLZ(String plz, Locale locale) throws InvalidPlzException {
		LOGGER.debug("BEGINN validatePLZ: plz={}", plz);
		final Validator validator = validationService.getValidator(locale);
		Set<ConstraintViolation<AbstractAdresse>> violations = validator.validateValue(AbstractAdresse.class, "plz", plz);	
		
		if (!violations.isEmpty()) {
			throw new InvalidPlzException(plz, violations);
		}
		LOGGER.debug("ENDE validatePLZ");
	}
	
	private void validateNachname(String nachname, Locale locale) throws InvalidNachnameException {
		LOGGER.debug("BEGINN validateNachname: nachname={}", nachname);
		final Validator validator = validationService.getValidator(locale);
		Set<ConstraintViolation<AbstractBenutzer>> violations = validator.validateValue(AbstractBenutzer.class, "nachname", nachname);	
		
		if (!violations.isEmpty()) {
			throw new InvalidNachnameException(nachname, violations);
		}
		LOGGER.debug("ENDE validateNachname");
	}
	
	private void validateRolle(RolleTyp rolleTyp, Locale locale) throws InvalidRolleException {
		LOGGER.debug("BEGINN validateRolle: rolle={}", rolleTyp);
		final Validator validator = validationService.getValidator(locale);
		Set<ConstraintViolation<Rolle>> violations = validator.validateValue(Rolle.class, "rolleTyp", rolleTyp);	
		
		if (!violations.isEmpty()) {
			throw new InvalidRolleException(rolleTyp, violations);
		}
		LOGGER.debug("ENDE validateRolle");
	}
// ####  validate-methods - END ####
}
