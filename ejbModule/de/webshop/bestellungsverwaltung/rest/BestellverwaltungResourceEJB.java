package de.webshop.bestellungsverwaltung.rest;

import static de.webshop.artikelverwaltung.rest.ArtikelverwaltungResourceEJB.getUriBuilderArtikel;
import static de.webshop.benutzerverwaltung.rest.BenutzerverwaltungResourceEJB.getUriBuilderAbstractBenutzer;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.service.Benutzerverwaltung;
import de.webshop.benutzerverwaltung.service.InvalidBenutzerIdException;
import de.webshop.bestellungsverwaltung.domain.Bestellposition;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.bestellungsverwaltung.domain.Bestellung.Bestellstatus;
import de.webshop.bestellungsverwaltung.service.BestellungDeleteException;
import de.webshop.bestellungsverwaltung.service.BestellungDuplikatException;
import de.webshop.bestellungsverwaltung.service.BestellungValidationException;
import de.webshop.bestellungsverwaltung.service.Bestellverwaltung;
import de.webshop.bestellungsverwaltung.service.InvalidBestellungIdException;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.lagerverwaltung.service.InvalidLagerIdException;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;
import de.webshop.lagerverwaltung.service.Lagerverwaltung;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class BestellverwaltungResourceEJB implements 
		BestellverwaltungResource, Serializable {

	private static final long serialVersionUID = -8495931518755966982L;
	private static final Logger LOGGER = LoggerFactory.getLogger(BestellverwaltungResourceEJB.class);
	
	@EJB
	private Bestellverwaltung bv;
	
	@EJB
	private Benutzerverwaltung benv;
	
	@EJB
	private Lagerverwaltung lv;
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	//Funktioniert
	@Override
	public Bestellung findBestellung(Long id, HttpHeaders headers, UriInfo uriInfo) 
		throws NotFoundExceptionRest, InvalidBestellungIdException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Bestellung bestellung;
		try {
			bestellung = bv.findBestellungById(id, locale);
		}
		catch (NotFoundException e) {

			final String msg = "Keine Bestellung gefunden mit der ID " + id;
			throw new NotFoundExceptionRest(msg, e);
		}

		// URLs innerhalb der gefundenen Bestellung anpassen
		updateUrlBestellung(bestellung, uriInfo);

		return bestellung;
	}
	
	//Funktioniert
	@Override
	public BestellungList findBestellungenStatus(Bestellstatus status, HttpHeaders headers, UriInfo uriInfo) 
		throws NotFoundExceptionRest {

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		List<Bestellung> bestellungen;
		
		try {
			bestellungen = bv.findBestellungByBestellstatus(status, locale);
		}
		catch (NotFoundException e) {
			final String msg = "Keine Bestellungen gefunden mit dem Status " + status;
			throw new NotFoundExceptionRest(msg, e);
		}
		for (Bestellung bestellung : bestellungen) {
			updateUrlBestellung(bestellung, uriInfo);
		}
		
		BestellungList bestellList = new BestellungList(bestellungen);
		
		return bestellList;
	}
	//Funktioniert
	@Override
	public Response createBestellung(Bestellung bestellung,	HttpHeaders headers, UriInfo uriInfo)
		throws BestellungValidationException, BestellungDuplikatException, NotFoundExceptionRest, InvalidBenutzerIdException, InvalidLagerIdException {
		
		// Schluessel des Kunden extrahieren
		final String benutzerUriStr = bestellung.getBenutzerUri().toString();
		int startPos = benutzerUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = benutzerUriStr.substring(startPos);
		Long benutzerId = null;
		try {
			benutzerId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			throw new WebApplicationException(e, NOT_FOUND);
		}
		
		// Kunde mit den vorhandenen ("alten") Bestellungen ermitteln
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		AbstractBenutzer benutzer;
		try {
			benutzer = benv.findBenutzerByID(benutzerId, locale);
		}
		catch (NotFoundException e) {

			final String msg = "Kein Benutzer gefunden mit der ID " + benutzerId;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		
		List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
		List<Long> lagerIds = new ArrayList<Long>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {
			final String lagerUriStr = bp.getLagerArtikelUri().toString();
			startPos = lagerUriStr.lastIndexOf('/') + 1;
			final String lagerIdStr = lagerUriStr.substring(startPos);
			Long lagerId = null;
			try {
				lagerId = Long.valueOf(lagerIdStr);
			}
			catch (NumberFormatException e) {
				throw new WebApplicationException(e, NOT_FOUND);
			}
			
			lagerIds.add(lagerId);
		}
		
		List<Lager> gefundeneLager = new ArrayList<Lager>();
		try {
			for (Long l : lagerIds) {
				Lager lager = lv.findLagerById(l, locale);
				gefundeneLager.add(lager);
			}
		}
		catch (NotFoundException e) {

			final String msg = "Keine Artikel gefunden mit den IDs " + lagerIds;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		
		int i = 0;
		final List<Bestellposition> neueBestellpositionen = new ArrayList<Bestellposition>(bestellpositionen.size());
		for (Bestellposition bp : bestellpositionen) {

			final long lagerId = lagerIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Lager lager : gefundeneLager) {
				if (lager.getIdlager().longValue() == lagerId) {
					// Der Artikel wurde gefunden
					bp.setLagerArtikel(lager);
					neueBestellpositionen.add(bp);
					break;					
				}
			}
		}
		bestellung.setBestellpositionen(neueBestellpositionen);
		
		try {
			bestellung = bv.createBestellung(bestellung, benutzer, locale, false);
		}
		catch (NotFoundException e) {
			// Kann nicht passieren, da Kunde und Artikel bereits geladen wurden
			throw new IllegalStateException("Benutzer " + benutzer + " oder Artikel nicht mehr gefunden", e);
		}
		final UriBuilder uriBuilderBestellungen = getUriBuilderBestellung(uriInfo);
		final URI bestellungUri = uriBuilderBestellungen.build(bestellung.getId());
		final Response response = Response.created(bestellungUri).build();
		LOGGER.trace(bestellungUri.toString());
		
		return response;
	}
	
	@Override
	public Bestellung updateBestellung(Bestellung bestellung, HttpHeaders headers, UriInfo uriInfo)	
		throws BestellungValidationException, NotFoundExceptionRest, InvalidBestellungIdException, LagerartikelValidationException, InvalidLagerIdException, NotFoundException {

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Bestellung originalBestellung;
		try {
			originalBestellung = bv.findBestellungById(bestellung.getIdbestellung(), locale);
		}
		catch (NotFoundException e) {
			final String msg = "Keine Bestellung gefunden mit der ID " + bestellung.getIdbestellung();
			throw new NotFoundExceptionRest(msg, e);
		}
		LOGGER.trace("{}", originalBestellung);
		
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			
			final String lagerUriStr = bp.getLagerArtikelUri().toString();
			
			int startPos = lagerUriStr.lastIndexOf('/') + 1;
			final String lagerIdStr = lagerUriStr.substring(startPos);
			
			Long lagerId = null;
			
			try {
				lagerId = Long.valueOf(lagerIdStr);
			}
			catch (NumberFormatException e) {
				throw new WebApplicationException(e, NOT_FOUND);
			}
			Lager lager = null;
			try {
				lager = lv.findLagerById(lagerId, locale);
			}
			catch (NotFoundException e) {
				final String msg = "Keine Artikel gefunden mit den IDs " + lager.getIdlager();
				throw new NotFoundExceptionRest(msg, e);
			}
			
			bp.setLagerArtikel(lager);
		}
		
		originalBestellung.setValues(bestellung);
		
		LOGGER.trace("{}", originalBestellung);
		// Update durchfuehren

		bestellung = bv.updateBestellung(originalBestellung, locale, false);


		updateUrlBestellung(bestellung, uriInfo);
		
		return bestellung;
		
	}
	
	//funktioniert
	@Override
	public Response deleteBestellung(Long id, HttpHeaders headers, UriInfo uriInfo) 
		throws NotFoundExceptionRest, BestellungValidationException, BestellungDeleteException, LagerartikelValidationException, InvalidBestellungIdException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Bestellung bestellung;
		
		try {
			bestellung = bv.findBestellungById(id, locale);
		}
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel gefunden mit der ID " + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		try {
			bv.deleteBestellung(bestellung, locale, false);
		} 
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel gefunden mit der ID " + bestellung.getIdbestellung();
			throw new NotFoundExceptionRest(msg, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}
	
	
	public static UriBuilder getUriBuilderBestellung(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderBestellung: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(BestellverwaltungResource.class)
        .path(BestellverwaltungResource.class, "findBestellung");
		
		LOGGER.debug("ENDE getUriBuilderBestellung: uriInfo={}", uriInfo);
		return ub;
	}
	
	
	
	public static void updateUrlBestellung(Bestellung bestellung, UriInfo uriInfo) {
		LOGGER.debug("BEGINN updateUrlBestellung: bestellung={}", bestellung);
		
		// URL fuer Kunde setzen
		final AbstractBenutzer benutzer = bestellung.getBenutzer();
		if (benutzer != null) {
			final UriBuilder ub = getUriBuilderAbstractBenutzer(uriInfo);
			final URI benutzerUri = ub.build(bestellung.getBenutzer().getIdBenutzer());
			bestellung.setBenutzerUri(benutzerUri);
		}
		
		// URLs fuer Artikel in den Bestellpositionen setzen
		final List<Bestellposition> bestellpositionen = bestellung.getBestellpositionen();
		if (bestellpositionen != null && !bestellpositionen.isEmpty()) {
			final UriBuilder ub = getUriBuilderArtikel(uriInfo);
			for (Bestellposition bp : bestellpositionen) {
				final URI lagerId = ub.build(bp.getLagerArtikel().getIdlager());
				bp.setLagerArtikelUri(lagerId);
			}
		}
	}
	
}
