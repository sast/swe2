package de.webshop.lagerverwaltung.service;

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
import de.webshop.artikelverwaltung.service.ArtikelValidationException;
import de.webshop.artikelverwaltung.service.Artikelverwaltung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.bestellungsverwaltung.service.Bestellverwaltung;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.util.IdGroup;
import de.webshop.util.NotFoundException;
import de.webshop.util.ValidationService;

@Stateless
public class Lagerverwaltung implements Serializable {

	private static final long serialVersionUID = 2445057857922720583L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Bestellverwaltung.class);

	@SuppressWarnings("unused")
	@PersistenceContext
	private transient EntityManager em;
	
	@EJB
	private ValidationService validationService;
	
	@EJB
	private LagerverwaltungDao dao;
	
	@IgnoreDependency
	@EJB
	private Artikelverwaltung av;
	
	
	private void validateLagerId(Long idLager, Locale locale) throws InvalidLagerIdException {
		final Validator validator = validationService.getValidator(locale);
		final Set<ConstraintViolation<Lager>> violations = validator.validateValue(Lager.class, "idlager", idLager, IdGroup.class);	
		
		if (!violations.isEmpty()) {
			throw new InvalidLagerIdException(idLager, violations);
		}
	}
	
	public Lager findLagerById(Long idLager, Locale locale) throws InvalidLagerIdException, NotFoundException {
		
		this.validateLagerId(idLager, locale);
		/*final Lager lager = dao.find(Lager.class, idLager);*/
		final List<Lager> lagerN = dao.find(Lager.FIND_LAGER_BY_ID_FETCH_ARTIKEL,
				with(Lager.PARAM_ID, idLager).parameters(),
				Lager.class);
		if (lagerN.isEmpty()) {
			throw new NotFoundException("Kein Lager mit der Id " + idLager, Lager.class);		
		}
		return lagerN.get(0);
	}
	
	public List<Lager> findLagerByArtikelId(Long idArtikel, Locale locale)throws NotFoundException, InvalidArtikelIdException {
		av.validateArtikelId(idArtikel, locale);
		final List<Lager> lagerN = dao.find(Lager.FIND_LAGER_BY_ARTIKEL_ID,
				with(Lager.PARAM_ARTIKEL_ID, idArtikel).parameters(),
                Lager.class);
		
		return lagerN;
	}
	
	public Lager updateLagerartikel(Lager lagerArtikel, Locale locale, boolean uiValidated) throws LagerartikelValidationException {
		if (lagerArtikel == null) {
			return null;
		}
		validateLagerartikel(lagerArtikel, locale, uiValidated, Default.class);
		
		dao.update(lagerArtikel);
		return lagerArtikel;	
	}
	
	public Lager createLagerArtikel(Lager lagerArtikel, Artikel artikel, Locale locale, boolean uiValidated) throws LagerArtikelDuplikatException, LagerartikelValidationException, InvalidArtikelIdException, ArtikelValidationException {
		
		if (lagerArtikel == null || artikel == null) {
			return null;
		}
		
		validateLagerartikel(lagerArtikel, locale, uiValidated, Default.class);
		
		Lager lagerartikelVorhanden = null;
		try {
			lagerartikelVorhanden = dao.findSingle(Lager.FIND_LAGER_BY_ID, 
									with(Lager.PARAM_ID, lagerArtikel.getIdlager()).parameters(), 
									Lager.class);
		} 
		catch (NotFoundException e) {
			//ist OK, falls Benutzer noch nicht existiert!
		}
		
		if (lagerartikelVorhanden != null) {
			LOGGER.debug("Lagerartikel existiert bereits!");
			throw new LagerArtikelDuplikatException(lagerArtikel);
		}
		LOGGER.debug("Lagerartikel existiert nicht");
		
		av.validateArtikelId(artikel.getIdArtikel(), locale);
		artikel = av.reloadArtikel(artikel);
		
		
		artikel.addLagerartikel(lagerArtikel);
		lagerArtikel.setArtikel(artikel);
		av.updateArtikel(artikel, locale, false);
		 
		lagerArtikel.setIdlager(KEINE_ID);
		dao.create(lagerArtikel);
		
		return lagerArtikel;
	}
	
	private void validateLagerartikel(Lager lagerArtikel, Locale locale, boolean uiValidated, Class<?> group) throws LagerartikelValidationException {
		LOGGER.debug("BEGIN validateLagerartikel: artikel={}, locale={}, uiValidated={}", new Object[] {lagerArtikel, locale, uiValidated});
		
		if (uiValidated) {
			LOGGER.debug("END validateLagerartikel");
			return;
		}
		
		final Validator validator = validationService.getValidator(locale);
		
		final Set<ConstraintViolation<Lager>> violations = validator.validate(lagerArtikel, group);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.debug("END validateLagerartikel: {}", violations);
			throw new LagerartikelValidationException(lagerArtikel, violations);
		}
		
		LOGGER.debug("END validateLagerartikel");
	}
	
	//TODO delete funktioniert nicht, wenn Artikel in einer Bestellung verwendet wurde, sollte noch abgefangen werden!
	public void deleteLagerartikel(Lager lagerArtikel, Locale locale) throws NotFoundException, InvalidLagerIdException {
		if (lagerArtikel == null) {
			return;
		}
		try {
			lagerArtikel = findLagerById(lagerArtikel.getIdlager(), locale);  
		}
		catch (InvalidLagerIdException e) {
			return;
		}
		
		dao.delete(lagerArtikel, lagerArtikel.getIdlager());	
	}
}
