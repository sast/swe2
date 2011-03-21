package de.webshop.benutzerverwaltung.rest;

import static de.webshop.artikelverwaltung.service.Artikelverwaltung.NO_ARTIKEL_FOUND_WITH_ID;
import static de.webshop.benutzerverwaltung.service.Benutzerverwaltung.NO_USERS_FOUND;
import static de.webshop.benutzerverwaltung.service.Benutzerverwaltung.NO_USER_FOUND_WITH_EMAIL;
import static de.webshop.benutzerverwaltung.service.Benutzerverwaltung.NO_USER_FOUND_WITH_ID;
import static de.webshop.bestellungsverwaltung.service.Bestellverwaltung.NO_BESTELLUNGEN_FOUND;
import static de.webshop.bestellungsverwaltung.service.Bestellverwaltung.NO_BESTELLUNGEN_FOUND_TO_USER_ID;
import static de.webshop.util.GenericMethod.genericUpdateUri;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.artikelverwaltung.rest.ArtikelverwaltungResourceEJB;
import de.webshop.artikelverwaltung.service.Artikelverwaltung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidProduktbewertungIdException;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.domain.Rolle;
import de.webshop.benutzerverwaltung.domain.Rolle.RolleTyp;
import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;
import de.webshop.benutzerverwaltung.service.BenutzerDuplikatException;
import de.webshop.benutzerverwaltung.service.BenutzerValidationException;
import de.webshop.benutzerverwaltung.service.Benutzerverwaltung;
import de.webshop.benutzerverwaltung.service.InvalidBenutzerIdException;
import de.webshop.benutzerverwaltung.service.InvalidEmailException;
import de.webshop.benutzerverwaltung.service.InvalidNachnameException;
import de.webshop.benutzerverwaltung.service.InvalidPlzException;
import de.webshop.benutzerverwaltung.service.InvalidRolleException;
import de.webshop.benutzerverwaltung.service.InvalidRolleHasBenutzerIdException;
import de.webshop.benutzerverwaltung.service.ProduktbewertungDuplikatException;
import de.webshop.benutzerverwaltung.service.ProduktbewertungValidationException;
import de.webshop.benutzerverwaltung.service.RolleHasBenutzerDuplikatException;
import de.webshop.benutzerverwaltung.service.RolleHasBenutzerValidationException;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.bestellungsverwaltung.rest.BestellungList;
import de.webshop.bestellungsverwaltung.rest.BestellverwaltungResourceEJB;
import de.webshop.bestellungsverwaltung.service.Bestellverwaltung;
import de.webshop.util.GenericMethodException;
import de.webshop.util.InvalidUriExceptionRest;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class BenutzerverwaltungResourceEJB implements
		BenutzerverwaltungResource, Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BenutzerverwaltungResourceEJB.class);
	private static final long serialVersionUID = -3103480702396427340L;
	
	private static final String BENUTZER_URI_NOT_SET = "Benutzer-URI ist nicht gesetzt!";
	private static final String ARTIKEL_URI_NOT_SET = "Artikel-URI ist nicht gesetzt!";

	private enum UriAttribute { BESTELLUNGEN, PRODUKTBEWERTUNGEN, ROLLEHASBENUTZER, ALLE };
	
	@EJB
	private Benutzerverwaltung bv;
	
	@EJB
	private Bestellverwaltung bestellverwaltung;
	
	@EJB
	private Artikelverwaltung artikelverwaltung;
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	public static UriBuilder getUriBuilderRolleHasBenutzer(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderRolleHasBenutzer: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(BenutzerverwaltungResource.class)
        .path(BenutzerverwaltungResource.class, "findRolleHasBenutzerById");
		
		LOGGER.debug("ENDE getUriBuilderRolleHasBenutzer: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static UriBuilder getUriBuilderProduktbewertung(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderProduktbewertung: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(BenutzerverwaltungResource.class)
        .path(BenutzerverwaltungResource.class, "findProduktbewertungById");
		
		LOGGER.debug("ENDE getUriBuilderProduktbewertung: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static UriBuilder getUriBuilderAbstractBenutzer(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderAbstractBenutzer: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(BenutzerverwaltungResource.class)
        .path(BenutzerverwaltungResource.class, "findBenutzerById");
		
		LOGGER.debug("ENDE getUriBuilderAbstractBenutzer: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static void updateUriProduktbewertung(Produktbewertung produktbewertung, UriInfo uriInfo) throws GenericMethodException {
		LOGGER.debug("BEGINN updateUriProduktbewertung: produktbewertung:{}, uriInfo:{}", new Object[]{produktbewertung, uriInfo});
		
		produktbewertung.setArtikelUri(genericUpdateUri(ArtikelverwaltungResourceEJB.class, Artikel.class, produktbewertung.getArtikel(), uriInfo));
		produktbewertung.setBenutzerUri(genericUpdateUri(BenutzerverwaltungResourceEJB.class, AbstractBenutzer.class, produktbewertung.getBenutzer(), uriInfo));
		
		LOGGER.debug("ENDE updateUriProduktbewertung: produktbewertung:{}, uriInfo:{}", new Object[]{produktbewertung, uriInfo});
	}
	
	public static void updateUriBenutzer(AbstractBenutzer benutzer, UriInfo uriInfo, UriAttribute[] uriAttributes) {
		LOGGER.debug("BEGINN updateBenutzerURI: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
		
		if (uriAttributes == null || uriAttributes.length == 0) {
			return;
		}
		
		for (UriAttribute uriAttr : uriAttributes) {
			try {
				switch (uriAttr) {
				case BESTELLUNGEN:
					updateUriBenutzerBestellungen(benutzer, uriInfo);
					break;
					
				case PRODUKTBEWERTUNGEN:
					updateUriBenutzerProduktbewertungen(benutzer, uriInfo);
					break;
					
				case ROLLEHASBENUTZER:
					updateUriBenutzerRolleHasBenutzer(benutzer, uriInfo);
					break;
	
				default:
					updateUriBenutzerAlles(benutzer, uriInfo);
					break;
				}
			}
			catch (GenericMethodException e) {
				LOGGER.debug("## GenericMethodException in updateUriBenutzer(): {}", e.getMessage());
			}
		}
		
		LOGGER.debug("ENDE updateBenutzerURI: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
	}
	
	public static void updateUriBenutzerBestellungen(AbstractBenutzer benutzer, UriInfo uriInfo) throws GenericMethodException {
		LOGGER.debug("BEGINN updateUriBenutzerBestellungen: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
		
		try {
			benutzer.setBestellungenURIs(genericUpdateUri(BestellverwaltungResourceEJB.class, Bestellung.class, benutzer.getBestellungen(), uriInfo));
		} 
		catch (SecurityException e) {
			LOGGER.debug("## genericUpdateUri: {}", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			LOGGER.debug("## genericUpdateUri: {}", e.getMessage());
		}
		
		LOGGER.debug("ENDE updateUriBenutzerBestellungen: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
	}
	
	public static void updateUriBenutzerProduktbewertungen(AbstractBenutzer benutzer, UriInfo uriInfo) throws GenericMethodException {
		LOGGER.debug("BEGINN updateUriBenutzerProduktbewertungen: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
		
		try {
			benutzer.setProduktbewertungenURIs(genericUpdateUri(BenutzerverwaltungResourceEJB.class, Produktbewertung.class, benutzer.getProduktbewertungen(), uriInfo));
		} 
		catch (SecurityException e) {
			LOGGER.debug("## genericUpdateUri: {}", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			LOGGER.debug("## genericUpdateUri: {}", e.getMessage());
		}
		
		LOGGER.debug("ENDE updateUriBenutzerProduktbewertungen: benutzer:{}, uriInfo:{}", new Object[]{benutzer, uriInfo});
	}
	
	public static void updateUriBenutzerRolleHasBenutzer(AbstractBenutzer benutzer, UriInfo uriInfo) {
	
	}
	
	public static void updateUriBenutzerAlles(AbstractBenutzer benutzer, UriInfo uriInfo) throws GenericMethodException {
		updateUriBenutzerBestellungen(benutzer, uriInfo);
		updateUriBenutzerProduktbewertungen(benutzer, uriInfo);
		updateUriBenutzerRolleHasBenutzer(benutzer, uriInfo);
	}

	@Override
	public AbstractBenutzer findBenutzerById(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidBenutzerIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		AbstractBenutzer benutzer;
		try {
			benutzer = bv.findBenutzerByID(id, locale);
		}
		catch (NotFoundException e) {
			final String msg = NO_USER_FOUND_WITH_ID + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		updateUriBenutzer(benutzer, uriInfo, new UriAttribute[]{UriAttribute.ALLE});
		
		return benutzer;
	}
	
	@Override
	public BenutzerList findBenutzerN(String nachname, HttpHeaders headers,	UriInfo uriInfo) 
			throws InvalidNachnameException, NotFoundExceptionRest, GenericMethodException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		List<AbstractBenutzer> benutzers;
		try {
			if ("".equals(nachname)) {
				benutzers = bv.findBenutzerN();
			}
			else {
				benutzers = bv.findBenutzerNByNachname(nachname, locale, false);
			}
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USERS_FOUND, e);
		}
		
		for (AbstractBenutzer benutzer : benutzers) {
			updateUriBenutzerAlles(benutzer, uriInfo);
		}
		
		final BenutzerList benutzerList = new BenutzerList(benutzers);
		return benutzerList;
	}

	@Override
	public BestellungList findBestellungenByBenutzerNachname(String nachname, HttpHeaders headers, UriInfo uriInfo) 
			throws InvalidNachnameException, NotFoundExceptionRest {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		List<Bestellung> bestellungen;
		try {
			if ("".equals(nachname)) {
				bestellungen = bestellverwaltung.findBestellungen();
			}
			else {
				bestellungen = bestellverwaltung.findBestellungenByBenutzerName(nachname, locale);
			}
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_BESTELLUNGEN_FOUND, e);
		}
		
		for (Bestellung bestellung : bestellungen) {
			BestellverwaltungResourceEJB.updateUrlBestellung(bestellung, uriInfo);
		}
		
		BestellungList bestellungList = new BestellungList(bestellungen);
		
		return bestellungList;
	}

	@Override
	public BestellungList findBestellungenByBenutzerId(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidBenutzerIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		List<Bestellung> bestellungen;
		try {
			bestellungen = bestellverwaltung.findBestellungenByBenutzerId(id, locale);
		}
		catch (NotFoundException e) {
			final String msg = NO_BESTELLUNGEN_FOUND_TO_USER_ID + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		for (Bestellung bestellung : bestellungen) {
			BestellverwaltungResourceEJB.updateUrlBestellung(bestellung, uriInfo);
		}
		
		BestellungList bestellungList = new BestellungList(bestellungen);
		
		return bestellungList;
	}

	/**
	 * aktuelle Version der Methode sucht lediglich in der Rechnungsadresse nach der uebergebenen Postleitzahl
	 */
	@Override
	public BenutzerList findBenutzerNByPLZ(String plz, HttpHeaders headers,
			UriInfo uriInfo) throws NotFoundExceptionRest, InvalidPlzException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final List<AbstractBenutzer> benutzers;
		try {
			benutzers = bv.findBenutzerNByPLZ(plz, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USERS_FOUND, e);
		}
		
		for (AbstractBenutzer benutzer : benutzers) {
			updateUriBenutzer(benutzer, uriInfo, new UriAttribute[]{UriAttribute.ALLE});
		}
		BenutzerList benutzerList = new BenutzerList(benutzers);
		
		return benutzerList;
	}

	@Override
	public BenutzerList findBenutzerNByRolleTyp(RolleTyp rolleTyp, HttpHeaders headers,	UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidRolleException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		 
		final List<AbstractBenutzer> benutzers;
		try {
			benutzers = bv.findBenutzerNByRolle(rolleTyp, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USERS_FOUND, e);
		}
		
		for (AbstractBenutzer benutzer : benutzers) {
			updateUriBenutzer(benutzer, uriInfo, new UriAttribute[]{UriAttribute.ROLLEHASBENUTZER});
		}
		
		for (AbstractBenutzer benutzer : benutzers) {
			updateUriBenutzerRolleHasBenutzer(benutzer, uriInfo);
		}
		
		final BenutzerList benutzerList = new BenutzerList(benutzers);
		return benutzerList;
	}

	@Override
	public AbstractBenutzer findBenutzerByEmail(String email, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidEmailException, GenericMethodException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		AbstractBenutzer benutzer;
		try {
			benutzer = bv.findBenutzerByEmail(email, locale);
		} 
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USER_FOUND_WITH_EMAIL + email, e);
		}
		
		updateUriBenutzerAlles(benutzer, uriInfo);
		
		return benutzer;
	}

	@Override
	public Produktbewertung findProduktbewertungById(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidProduktbewertungIdException, GenericMethodException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

		Produktbewertung produktbewertung;
		try {
			produktbewertung = bv.findProduktbewertungById(id, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(Benutzerverwaltung.NO_PRODUKTBEWERTUNG_FOUND_WITH_ID + id, e);
		}
		
		updateUriProduktbewertung(produktbewertung, uriInfo);
		
		return produktbewertung;
	}

	@Override
	public RolleHasBenutzer findRolleHasBenutzerById(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidRolleHasBenutzerIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

		RolleHasBenutzer rolleHasBenutzer;
		try {
			rolleHasBenutzer = bv.findRolleHasBenutzerById(id, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(Benutzerverwaltung.NO_PRODUKTBEWERTUNG_FOUND_WITH_ID + id, e);
		}
		
		return rolleHasBenutzer;
	}
	
	@Override
	public RolleHasBenutzer findRolleHasBenutzerByBenutzerIdAndRolletyp(Long idBenutzer, Long idRolle, HttpHeaders headers, UriInfo uriInfo)
			throws NotFoundExceptionRest, InvalidBenutzerIdException, InvalidRolleException {
		// TODO bei Bedarf implementieren
		return null;
	}

	@Override
	public ProduktbewertungList findProduktbewertungByBenutzerIdAndArtikelId(long idBenutzer, long idArtikel, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidBenutzerIdException, InvalidArtikelIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

		List<Produktbewertung> produktbewertungen;
		try {
			//Parameter-Default-Wert ist -1
			if (idBenutzer == -1 && idArtikel == -1) {
				produktbewertungen = bv.findProduktbewertungen();
			}
			else if (idArtikel == -1) {
				produktbewertungen = bv.findProduktbewertungenByBenutzerId(new Long(idBenutzer), locale);
			}
			else if (idBenutzer == -1) {
				produktbewertungen = bv.findProduktbewertungenByArtikelId(new Long(idArtikel), locale);
			}
			else {
				produktbewertungen = new ArrayList<Produktbewertung>();
				
				Arrays.asList(new Produktbewertung[]{bv.findProduktbewertungByBenutzerIdAndArtikelId(new Long(idBenutzer), new Long(idArtikel), locale)});
			}
		} 
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(Benutzerverwaltung.NO_PRODUKTBEWERTUNG_FOUND, e);
		}
		
		ProduktbewertungList produktbewertungList = new ProduktbewertungList(produktbewertungen);
		
		return produktbewertungList;
	}

	@Override
	public Response createBenutzer(AbstractBenutzer benutzer, HttpHeaders headers, UriInfo uriInfo)
			throws BenutzerValidationException, BenutzerDuplikatException {
		LOGGER.debug("BEGINN createBenutzer: benutzer={}", benutzer);
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		benutzer.getRechnungsadresse().setBenutzer(benutzer);
		if (benutzer.getLieferadresse() != null) {
			benutzer.getLieferadresse().setBenutzer(benutzer);
		}
		benutzer = bv.createBenutzer(benutzer, locale, false);
		
		final UriBuilder uriBuilder = getUriBuilderAbstractBenutzer(uriInfo);
		final URI location = uriBuilder.build(benutzer.getIdBenutzer());
		final Response response = Response.created(location).build();
		
		LOGGER.debug("ENDE createBenutzer: benutzer={}", benutzer);
		return response;
	}

	@Override
	public AbstractBenutzer updateBenutzer(AbstractBenutzer benutzer, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, BenutzerValidationException, BenutzerDuplikatException, InvalidBenutzerIdException {
		LOGGER.debug("BEGINN updateBenutzer: benutzer={}", benutzer);
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		AbstractBenutzer origBenutzer;
		try {
			origBenutzer = bv.findBenutzerByID(benutzer.getIdBenutzer(), locale);
		} 
		catch (NotFoundException e) {
			final String msg = NO_USER_FOUND_WITH_ID + benutzer.getIdBenutzer();
			throw new NotFoundExceptionRest(msg, e);
		}
		
		if (benutzer.getRechnungsadresse() != null && benutzer.getRechnungsadresse().getBenutzer() == null) {
			benutzer.getRechnungsadresse().setBenutzer(origBenutzer);
		}
		if (benutzer.getLieferadresse() != null && benutzer.getLieferadresse().getBenutzer() == null) {
			benutzer.getLieferadresse().setBenutzer(origBenutzer);
		}
		
		origBenutzer.setValues(benutzer);
		try {
			benutzer = bv.updateBenutzer(origBenutzer, locale, false);
		}
		catch (NotFoundException e) {
			final String msg = NO_USER_FOUND_WITH_ID + benutzer.getIdBenutzer();
			throw new NotFoundExceptionRest(msg, e);
		}
		
		updateUriBenutzer(benutzer, uriInfo, new UriAttribute[]{UriAttribute.ALLE});
		LOGGER.debug("ENDE updateBenutzer: benutzer={}", benutzer);
		return benutzer;
	}

	@Override
	public Response deleteBenutzer(Long id, HttpHeaders headers, UriInfo uriInfo)
			throws NotFoundExceptionRest, BenutzerValidationException, BenutzerDuplikatException, InvalidBenutzerIdException {
		LOGGER.debug("BEGINN deleteBenutzer: id={}", id);
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		try {
			AbstractBenutzer benutzer;
			benutzer = bv.findBenutzerByID(id, locale);
			bv.deleteBenutzer(benutzer);
		}
		catch (NotFoundException e) {
			final String msg = NO_USER_FOUND_WITH_ID + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		final Response response = Response.noContent().build();
		LOGGER.debug("ENDE deleteBenutzer: id={}", id);
		return response;
	}

	@Override
	public Response createProduktbewertung(Produktbewertung produktbewertung, HttpHeaders headers, UriInfo uriInfo)
			throws InvalidBenutzerIdException, InvalidArtikelIdException, ProduktbewertungValidationException, ProduktbewertungDuplikatException, 
					InvalidUriExceptionRest, NotFoundExceptionRest {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		if (produktbewertung.getBenutzerUri() == null) {
			throw new InvalidUriExceptionRest(BENUTZER_URI_NOT_SET);
		}
		else if (produktbewertung.getArtikelUri() == null) {
			throw new InvalidUriExceptionRest(ARTIKEL_URI_NOT_SET);
		}
		
		String path = produktbewertung.getBenutzerUri().toString();
		int startPos = path.lastIndexOf("/") + 1;
		Long idBenutzer = new Long(produktbewertung.getBenutzerUri().toString().substring(startPos));
		AbstractBenutzer benutzer;
		try {
			benutzer = bv.findBenutzerByID(idBenutzer, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USER_FOUND_WITH_ID + idBenutzer, e);
		}
		
		path = produktbewertung.getArtikelUri().toString();
		startPos = path.lastIndexOf("/") + 1;
		Long idArtikel = new Long(path.substring(startPos));
		Artikel artikel;
		try {
			artikel = artikelverwaltung.findArtikelById(idArtikel, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_ARTIKEL_FOUND_WITH_ID + idArtikel, e);
		}
		
		produktbewertung = bv.createProduktbewertung(produktbewertung, benutzer, artikel, locale, false);
		
		final UriBuilder uriBuilder = getUriBuilderProduktbewertung(uriInfo);
		final URI location = uriBuilder.build(produktbewertung.getIdProduktbewertung());
		final Response response = Response.created(location).build();
		
		return response;
	}

	@Override
	public Response deleteProduktbewertung(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidProduktbewertungIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		try {
			Produktbewertung produktbewertung = bv.findProduktbewertungById(id, locale);
			bv.deleteProduktbewertung(produktbewertung, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(Benutzerverwaltung.NO_PRODUKTBEWERTUNG_FOUND_WITH_ID + id, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}

	@Override
	public Response createRolleHasBenutzer(Rolle rolle, Long idBenutzer, HttpHeaders headers, UriInfo uriInfo)
			throws InvalidBenutzerIdException, RolleHasBenutzerValidationException,	RolleHasBenutzerDuplikatException, NotFoundExceptionRest {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		AbstractBenutzer benutzer;
		try {
			benutzer = bv.findBenutzerByID(idBenutzer, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(NO_USER_FOUND_WITH_ID + idBenutzer, e);
		}
		
		RolleHasBenutzer rolleHasBenutzer = bv.createRolleHasBenutzer(benutzer, rolle, locale, false);
		
		final UriBuilder uriBuilder = getUriBuilderRolleHasBenutzer(uriInfo);
		final URI location = uriBuilder.build(rolleHasBenutzer.getIdRolleHasBenutzer());
		final Response response = Response.created(location).build();
		
		return response;
	}

	@Override
	public Response deleteRolleHasBenutzer(Long id, HttpHeaders headers, UriInfo uriInfo) 
			throws NotFoundExceptionRest, InvalidRolleHasBenutzerIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		try {
			RolleHasBenutzer rolleHasBenutzer = bv.findRolleHasBenutzerById(id, locale);
			bv.deleteRolleHasBenutzer(rolleHasBenutzer, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest(Benutzerverwaltung.NO_PRODUKTBEWERTUNG_FOUND_WITH_ID + id, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}
}
