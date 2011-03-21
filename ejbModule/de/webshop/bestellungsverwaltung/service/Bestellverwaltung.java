package de.webshop.bestellungsverwaltung.service;

import static de.webshop.util.AbstractDao.QueryParameter.with;
import static de.webshop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.Date;
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

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.service.Benutzerverwaltung;
import de.webshop.benutzerverwaltung.service.InvalidBenutzerIdException;
import de.webshop.bestellungsverwaltung.domain.Bestellposition;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.bestellungsverwaltung.domain.Bestellung.Bestellstatus;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;
import de.webshop.lagerverwaltung.service.Lagerverwaltung;
import de.webshop.util.IdGroup;
import de.webshop.util.NotFoundException;
import de.webshop.util.PreExistingGroup;
import de.webshop.util.ValidationService;


//TODO: Mailversand bei createBestellung

@Stateless
public class Bestellverwaltung implements Serializable {

	private static final long serialVersionUID = 13899435297886246L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Bestellverwaltung.class);
	private static final boolean TRACE = LOGGER.isTraceEnabled();
	
	public static final String NO_BESTELLUNGEN_FOUND = "Keine Bestellungen gefunden!";
	public static final String NO_BESTELLUNGEN_FOUND_TO_USER_ID = "Keine Bestellungen zum Benutzer-ID: ";
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private transient EntityManager em;
	
	@EJB
	private BestellverwaltungDao dao;
	
	@IgnoreDependency
	@EJB
	private Benutzerverwaltung bv;
	
	@IgnoreDependency
	@EJB 
	private Lagerverwaltung lv;

	@EJB
	private ValidationService validationService;

	// ####  validate-methods  ####	
	
	private void validateId(Long idbestellung, Locale locale) throws InvalidBestellungIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Bestellung>> violations = validator.validateValue(Bestellung.class, "idbestellung", idbestellung, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidBestellungIdException(idbestellung, violations);
		}
	}
	
	private void validateBestellung(Bestellung bestellung, Locale locale, boolean uiValidated, Class<?> group) throws BestellungValidationException {
		LOGGER.debug("BEGIN validateBestellung: bestellung={}, locale={}, uiValidated={}", new Object[] {bestellung, locale, uiValidated});
		
		if (uiValidated) {
			LOGGER.debug("END validateBestellung");
			return;
		}
		
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Bestellung>> violations = validator.validate(bestellung, group);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debug("END validateBestellung: {}", violations);
			throw new BestellungValidationException(bestellung, violations);
		}
		
		LOGGER.debug("END validateBestellung");
	}
	
	// ####  validate-methods -- END ####	
	
	// ####  query-methods ####
	
	public List<Bestellung> findBestellungen() throws NotFoundException {
		return dao.find(Bestellung.FIND_BESTELLUNGEN, Bestellung.class);
	}
	
	public Bestellung findBestellungById(Long idbestellung, Locale locale) throws InvalidBestellungIdException, NotFoundException {
		
		this.validateId(idbestellung, locale);
		final Bestellung bestellung = dao.find(Bestellung.class, idbestellung);
		return bestellung;
	}
	
	public List<Bestellung> findBestellungByBestellstatus(Bestellung.Bestellstatus status, Locale locale)throws NotFoundException {
		final List<Bestellung> bestellungen = dao.find(Bestellung.FIND_BESTELLUNG_N_BY_STATUS,
				with(Bestellung.PARAM_STATUS, status).parameters(),
                Bestellung.class);
		return bestellungen;
	}
	
	public List<Bestellung> findBestellungenByBenutzerId(Long idBenutzer, Locale locale) throws NotFoundException, InvalidBenutzerIdException {
		bv.validateBenutzerId(idBenutzer, locale);
		final List<Bestellung> bestellungen = dao.find(Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_ID,
				with(Bestellung.PARAM_BENUTZER_ID, idBenutzer).parameters(),
                Bestellung.class);
		return bestellungen;
	}
	
	public List<Bestellung> findBestellungenByBenutzerName(String nachname, Locale locale) throws NotFoundException {
		final List<Bestellung> bestellungen = dao.find(Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_NAME,
				with(Bestellung.PARAM_BENUTZER_NAME, nachname).parameters(),
                Bestellung.class);
		return bestellungen;
	}
	
	public List<Bestellung> findBestellungenByErstellungsDatum(Date datum, Locale locale) throws NotFoundException {
		
		final List<Bestellung> bestellungen = dao.find(Bestellung.FIND_BESTELLUNG_N_BY_ERSTELLUNGSDATUM,
				with(Bestellung.PARAM_DATUM, datum).parameters(),
                Bestellung.class);
		return bestellungen;
	}
	
	// ####  query-methods -- END ####	
	
	public Bestellung createBestellung(Bestellung bestellung, AbstractBenutzer benutzer, Locale locale, boolean uiValidated)throws BestellungValidationException, BestellungDuplikatException, NotFoundException, InvalidBenutzerIdException {
		
		if (bestellung == null) {
			return null;
		}
		
		if (TRACE) {
			for (Bestellposition bp : bestellung.getBestellpositionen()) {
				LOGGER.trace("Bestellposition: " + bp);				
			}
		}
		
		validateBestellung(bestellung, locale, uiValidated, Default.class);
		
		benutzer = bv.findBenutzerByIDFetchBestellungen(benutzer.getIdBenutzer(), locale);
		final List<Bestellung> bestellungen = benutzer.getBestellungen();
		
		if (bestellungen != null) {
			for (Bestellung b : bestellungen) {
				/* TODO FRAGE: braucht man das weil referenz kann ja eigentlich nie gleich sein .. wenn haut equals es doch sowieso raus
				if (b == bestellung) {   
					continue;
				}
				*/
				if (!bestellung.equals(b)) {
					continue;
				}
				
				final Date erstellungsdatum = bestellung.getErstellungsdatum();
				throw new BestellungDuplikatException(benutzer.getIdBenutzer(), erstellungsdatum);
			}
		}
		
		benutzer.addBestellung(bestellung);
		bestellung.setBenutzer(benutzer);
		
		validateBestellung(bestellung, locale, false, PreExistingGroup.class);
		
		bestellung.setIdbestellung(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setIdbestellposition(KEINE_ID);
		}
		bestellung.setGesamtpreis(gesamtpreisberechnen(bestellung));
		dao.create(bestellung);
		
		return bestellung;
	}
	
	public Bestellung updateBestellung(Bestellung bestellung, Locale locale, boolean uiValidated)throws BestellungValidationException {
		if (bestellung == null) {
			return null;
		}
		
		bestellung.setGesamtpreis(this.gesamtpreisberechnen(bestellung));
		
		validateBestellung(bestellung, locale, uiValidated, Default.class);		
		
		bestellung = dao.update(bestellung);

		//laedt bestellung direkt aus EM! und liefert nur die bereits 3 zuvor existierenden!
		//bestellung = dao.reloadBestellung(bestellung);
		
		//bestellung = dao.find(Bestellung.class, bestellung.getIdbestellung());
		
		return bestellung;
	}
	
	private double gesamtpreisberechnen(Bestellung bestellung) {
		double gesamtPreis = 0.0;
		if (bestellung.getBestellpositionen() == null) {
			return 0.0;
		}
		if (bestellung.getBestellpositionen().isEmpty()) {
			return 0.0;
		}
			
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			gesamtPreis += bp.getLagerArtikel().getArtikel().getPreis() * bp.getMenge();
		}
		
		return gesamtPreis;
	}
	
	public void deleteBestellung(Bestellung bestellung, Locale locale, boolean uiValidated) throws NotFoundException, BestellungValidationException, BestellungDeleteException, LagerartikelValidationException {
		if (bestellung == null) {
			return;
		}
		try {
			bestellung = findBestellungById(bestellung.getIdbestellung(), null);
		}
		catch (InvalidBestellungIdException e) {
			return;
		}

		if (bestellung.getBestellstatus() != Bestellstatus.WARENKORB) {
			bestellung.setBestellstatus(Bestellstatus.STORNIERT);
			return;
		}
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.getLagerArtikel().setBestandIst(bp.getLagerArtikel().getBestandIst() + bp.getMenge());
			lv.updateLagerartikel(bp.getLagerArtikel(), locale, uiValidated);
		}
		dao.delete(bestellung, bestellung.getIdbestellung());
	}
	
	public void deleteBestellposition(Bestellposition bp, Locale locale, boolean uiValidated) throws LagerartikelValidationException {
		if (bp == null) {
			return;
		}
		dao.delete(bp, bp.getIdbestellposition());
		bp.getLagerArtikel().setBestandIst(bp.getLagerArtikel().getBestandIst() + bp.getMenge());
		lv.updateLagerartikel(bp.getLagerArtikel(), locale, uiValidated);
	}
	
	public Bestellposition findBestellpositionById(Long idBp) throws NotFoundException {
		
		return dao.find(Bestellposition.class, idBp);
	}
	
}
