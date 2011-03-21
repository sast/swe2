package de.webshop.lagerverwaltung.rest;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.Serializable;
import java.net.URI;
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

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.rest.ArtikelverwaltungResource;
import de.webshop.artikelverwaltung.service.ArtikelValidationException;
import de.webshop.artikelverwaltung.service.Artikelverwaltung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.lagerverwaltung.service.InvalidLagerIdException;
import de.webshop.lagerverwaltung.service.LagerArtikelDuplikatException;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;
import de.webshop.lagerverwaltung.service.Lagerverwaltung;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;
import static de.webshop.util.Constants.KEINE_ID;


@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class LagerverwaltungResourceEJB implements LagerverwaltungResource, Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(LagerverwaltungResourceEJB.class);
	private static final long serialVersionUID = 1833154187559780265L;

	@SuppressWarnings("unused")
	@PersistenceContext
	private transient EntityManager em;
	
	@EJB
	private Lagerverwaltung lv;
	
	@EJB
	private Artikelverwaltung av;
	
	@Override
	public Lager findLagerById(Long idLager, HttpHeaders headers, UriInfo uriInfo) 
		throws NotFoundExceptionRest, InvalidLagerIdException {
	
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
	
		Lager lager;
		try  {
			lager = lv.findLagerById(idLager, locale);
		}
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel mit der ID " + idLager + " gefunden";
			throw new NotFoundExceptionRest(msg, e);
		}
		updateUriLager(lager, uriInfo);
		
		return lager;	
	}
	
	@Override
	public LagerList findLagerByArtikelId(Long idArtikel, HttpHeaders headers, UriInfo uriInfo)
		throws NotFoundExceptionRest, InvalidArtikelIdException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		List<Lager> lager;
		try {
			lager = lv.findLagerByArtikelId(idArtikel, locale);
		}
		catch (NotFoundException e) {
			final String msg = "Kein Artikel mit der ID " + idArtikel + " gefunden";
			throw new NotFoundExceptionRest(msg, e);
		}
		
		for (Lager lag : lager) {
			updateUriLager(lag, uriInfo);
		}
				
		LagerList lagerList = new LagerList(lager);
						
		return lagerList;
	}
	
	@Override
	public Response createLagerArtikel(Lager lagerArtikel, HttpHeaders headers, UriInfo uriInfo)
		throws LagerArtikelDuplikatException, LagerartikelValidationException, InvalidArtikelIdException, NotFoundExceptionRest, ArtikelValidationException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		final String artikelUriString = lagerArtikel.getArtikelUri().toString();
		int startPos = artikelUriString.lastIndexOf('/') + 1;
		final String artikelIdString = artikelUriString.substring(startPos);
		
		Long artikelId = null;
		try {
			artikelId = Long.valueOf(artikelIdString);
		} 
		catch (NumberFormatException e) {
			throw new WebApplicationException(e, NOT_FOUND);
		}
		Artikel artikel;
		try {
			artikel = av.findArtikelById(artikelId, locale);
		} 
		catch (NotFoundException e) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikelId;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		lagerArtikel.setIdlager(KEINE_ID);
		lagerArtikel = lv.createLagerArtikel(lagerArtikel, artikel, locale, false);
		LOGGER.trace("{}", lagerArtikel);
		
		final UriBuilder uriBuilderLager = uriInfo.getBaseUriBuilder();
		final URI lagerUri = uriBuilderLager.build(lagerArtikel.getIdlager());
		final Response response = Response.created(lagerUri).build();
		
		return response;
	}
	
	@Override
	public Lager updateLagerArtikel(Lager lagerArtikel, HttpHeaders headers, UriInfo uriInfo)
		throws NotFoundExceptionRest, LagerartikelValidationException, InvalidLagerIdException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Lager origLager;
		try {
			origLager = lv.findLagerById(Long.valueOf(lagerArtikel.getIdlager()), locale);
		}
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel gefunden mit der ID " + lagerArtikel.getIdlager();
			throw new NotFoundExceptionRest(msg, e);
		}
		LOGGER.trace("{}", origLager);
			
		origLager.setValues(lagerArtikel);
		LOGGER.trace("{}", origLager);
		
		try {
			lagerArtikel = lv.updateLagerartikel(origLager, locale, false);
		}
		catch (LagerartikelValidationException e) {
		}
		updateUriLager(lagerArtikel, uriInfo);
		
		return lagerArtikel;
	}
	
	@Override
	public Response deleteLagerArtikel(Long idLager, HttpHeaders headers)
		throws NotFoundExceptionRest, InvalidLagerIdException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Lager lager;
		try {
			lager = lv.findLagerById(idLager, locale);
		} 
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel gefunden mit der ID " + idLager;
			throw new NotFoundExceptionRest(msg, e);
		}
		try {
			lv.deleteLagerartikel(lager, locale);
		} 
		catch (NotFoundException e) {
			final String msg = "Kein Lagerartikel gefunden mit der ID " + lager.getIdlager();
			throw new NotFoundExceptionRest(msg, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}
	
	public static UriBuilder getUriBuilderArtikel(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderArtikel: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(ArtikelverwaltungResource.class)
        .path(ArtikelverwaltungResource.class, "findArtikel");
		
		LOGGER.debug("ENDE getUriBuilderArtikel: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static void updateUriLager(Lager lager, UriInfo uriInfo) {
		LOGGER.debug("BEGINN updateUriLager: lager={}", lager);
		
		final Artikel artikel = lager.getArtikel();
		if (artikel != null) {
			final UriBuilder ub = getUriBuilderArtikel(uriInfo);
			final URI artikelUri = ub.build(lager.getArtikel().getIdArtikel());
			lager.setArtikelUri(artikelUri);
		}
		
	}
}
