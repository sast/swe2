package de.webshop.artikelverwaltung.rest;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Kategorie;
import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;
import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.artikelverwaltung.service.ArtikelDuplikatException;
import de.webshop.artikelverwaltung.service.ArtikelValidationException;
import de.webshop.artikelverwaltung.service.Artikelverwaltung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidKategorieHasArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidKategorieIdException;
import de.webshop.artikelverwaltung.service.KategorieDeleteSubKategorieException;
import de.webshop.artikelverwaltung.service.KategorieHasArtikelDuplikatException;
import de.webshop.artikelverwaltung.service.KategorieHasArtikelValidationException;
import de.webshop.artikelverwaltung.service.KategorieValidationException;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.lagerverwaltung.rest.LagerverwaltungResource;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;
import static de.webshop.util.Constants.KEINE_ID;
import static de.webshop.benutzerverwaltung.rest.BenutzerverwaltungResourceEJB.getUriBuilderProduktbewertung;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ArtikelverwaltungResourceEJB implements
		ArtikelverwaltungResource, Serializable {

	private static final long serialVersionUID = 8599112236555743335L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtikelverwaltungResourceEJB.class);

	@EJB
	private Artikelverwaltung av;
	
	@SuppressWarnings("unused")
	@PersistenceContext
	private EntityManager em;
	
	
	@Override
	public Artikel findArtikel(Long id, HttpHeaders headers, UriInfo uriInfo) throws NotFoundExceptionRest, InvalidArtikelIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Artikel artikel;
		try {
			artikel = av.findArtikelById(id, locale);  
		}
		catch (NotFoundException e) {
			final String msg = "Kein Artikel gefunden mit der ID: " + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		updateUrlArtikel(artikel, uriInfo);
		
		return artikel;
	}
	
	

	@Override
	public Kategorie findKategorie(Long id, HttpHeaders headers, UriInfo uriInfo) throws NotFoundExceptionRest, InvalidKategorieIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Kategorie kategorie;
		try {
			kategorie = av.findKategorieById(id, locale); 
		}
		catch (NotFoundException e) {
			final String msg = "Kein Kategorie gefunden mit der ID: " + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		updateUrlKategorie(kategorie, uriInfo);
		
		return kategorie;
	}
	
	public static void updateUrlArtikel(Artikel artikel, UriInfo uriInfo) {
		
		final List<Lager> lagerartikel = artikel.getArtikelVarianten();
		if (lagerartikel != null && !lagerartikel.isEmpty()) {
			final UriBuilder ub = getUriBuilderLager(uriInfo);
			final List<URI> lagerartikelURIs = new ArrayList<URI>();
			for (Lager la : lagerartikel) {
				final URI artikelKey = ub.build(la.getIdlager());
				lagerartikelURIs.add(artikelKey);
			} 
			artikel.setArtikelVariantenUri(lagerartikelURIs);
		}
		
		final List<Produktbewertung> produktbewertung = artikel.getProduktBewertungen();
		if (produktbewertung != null && !produktbewertung.isEmpty()) {
			final UriBuilder ub = getUriBuilderProduktbewertung(uriInfo);
			final List<URI> produktbewertungURIs = new ArrayList<URI>();
			for (Produktbewertung pb : produktbewertung) {
				final URI bewertungKey = ub.build(pb.getIdProduktbewertung());
				produktbewertungURIs.add(bewertungKey);
			}
			artikel.setProduktBewertungUri(produktbewertungURIs);
		}
		
		final List<KategorieHasArtikel> kategorie = artikel.getKategorien();
		if (kategorie != null && !kategorie.isEmpty()) {
			final UriBuilder ub = getUriBuilderKategorie(uriInfo);
			final List<URI> kategorieURIs = new ArrayList<URI>();
			for (KategorieHasArtikel k : kategorie) {
				final URI kategorieKey = ub.build(k.getIdkategoriehasartikel());
				kategorieURIs.add(kategorieKey);
			}
			artikel.setKategorieUri(kategorieURIs);
		}
	}
	
	private void updateUrlKategorie(Kategorie kategorie, UriInfo uriInfo) {
		
		final List<Kategorie> subKategorie = kategorie.getSubKategorien();
		if (subKategorie != null && !subKategorie.isEmpty()) {
			final UriBuilder ub = getUriBuilderKategorie(uriInfo);
			final List<URI> kategorieURIs = new ArrayList<URI>();
			for (Kategorie k : subKategorie) {
				final URI kategorieKey = ub.build(k.getIdKategorie());
				kategorieURIs.add(kategorieKey);
			}
			kategorie.setSubKategorieUri(kategorieURIs);
		}
	}

	public static UriBuilder getUriBuilderLager(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderArtikel: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(LagerverwaltungResource.class)
        .path(LagerverwaltungResource.class, "findLagerById");
		
		LOGGER.debug("ENDE getUriBuilderArtikel: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static UriBuilder getUriBuilderArtikel(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderArtikel: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(ArtikelverwaltungResource.class)
        .path(ArtikelverwaltungResource.class, "findArtikel");
		
		LOGGER.debug("ENDE getUriBuilderArtikel: uriInfo={}", uriInfo);
		return ub;
	}
	
	public static UriBuilder getUriBuilderKategorie(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriBuilderKategorie: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(ArtikelverwaltungResource.class)
        .path(ArtikelverwaltungResource.class, "findKategorie");
		
		LOGGER.debug("ENDE getUriBuilderKategorie: uriInfo={}", uriInfo);
		return ub;
	}



	@Override
	public Response createArtikel(Artikel artikel, HttpHeaders headers,
			UriInfo uriInfo) throws ArtikelValidationException, ArtikelDuplikatException, NotFoundException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		artikel.setIdArtikel(KEINE_ID);
		artikel = av.createArtikel(artikel, locale, false);
		
		final UriBuilder uriBuilderArtikel = getUriBuilderArtikel(uriInfo);
		final URI artikelURI = uriBuilderArtikel.build(artikel.getIdArtikel());
		final Response response = Response.created(artikelURI).build();
		return response;
	}


	
	@Override
	public Artikel updateArtikel(Artikel artikel, HttpHeaders headers,
			UriInfo uriInfo) throws NotFoundExceptionRest, InvalidArtikelIdException, ArtikelValidationException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Artikel origArtikel;
		try {
			origArtikel = av.findArtikelById(Long.valueOf(artikel.getIdArtikel()), locale);  
			
		}
		catch (NotFoundException e) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getIdArtikel();
			throw new NotFoundExceptionRest(msg, e);
		}
		LOGGER.trace("{}", origArtikel);
		
		origArtikel.setValues(artikel);
		
		LOGGER.trace("{}", origArtikel);
		
		artikel = av.updateArtikel(origArtikel, locale, false);
		
		updateUrlArtikel(artikel, uriInfo);
		
		return artikel;
	}



	@Override
	public ArtikelList findArtikelB(String bezeichnung, HttpHeaders headers,
			UriInfo uriInfo) throws NotFoundExceptionRest {
		
		final ArtikelList artikelList;
		try {
			artikelList = new ArtikelList(av.findArtikelByBezeichnung(bezeichnung));  
		}
		catch (NotFoundException e) {
			final String msg = "Keine Artikel gefunden!";
			throw new NotFoundExceptionRest(msg, e);
		}
		
		return artikelList;
	}

	@Override
	public Response createKategorie(Kategorie kategorie, HttpHeaders headers,
			UriInfo uriInfo) throws NotFoundException, KategorieValidationException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		kategorie.setIdKategorie(KEINE_ID);
		kategorie = av.createKategorie(kategorie, locale, false);
		
		
		final UriBuilder uriBuilderKategorie = getUriBuilderKategorie(uriInfo);
		final URI kategorieURI = uriBuilderKategorie.build(kategorie.getIdKategorie());
		final Response response = Response.created(kategorieURI).build();
		return response;
	}

	@Override
	public Kategorie updateKategorie(Kategorie kategorie, HttpHeaders headers,
			UriInfo uriInfo) throws InvalidKategorieIdException, NotFoundExceptionRest, KategorieValidationException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Kategorie origKategorie;
		try {
			origKategorie = av.findKategorieById(Long.valueOf(kategorie.getIdKategorie()), locale);  
			
		}
		catch (NotFoundException e) {
			final String msg = "Keine Kategorie gefunden mit der ID " + kategorie.getIdKategorie();
			throw new NotFoundExceptionRest(msg, e);
		}
		LOGGER.trace("{}", origKategorie);
		
		origKategorie.setValues(kategorie);
		
		LOGGER.trace("{}", origKategorie);
		
		kategorie = av.updateKategorie(origKategorie, locale, false); 
		
		updateUrlKategorie(kategorie, uriInfo);
		
		return kategorie;
	}


	//TODO ueberpruefen ob Kategorie auf Subkategorien verweist
	@Override
	public Response deleteKategorie(Long id, HttpHeaders headers,
			UriInfo uriInfo) throws InvalidKategorieIdException, NotFoundExceptionRest, KategorieDeleteSubKategorieException {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Kategorie kategorie;
		try {
			kategorie = av.findKategorieById(id, locale);
		}
		catch (NotFoundException e) {
			final String msg = "Keine Kategorie gefunden mit der ID " + id;
			throw new NotFoundExceptionRest(msg, e);
		}
		
		try {
			av.deleteKategorie(kategorie);
		} 
		catch (NotFoundException e) {
			final String msg = "Keine Kategorie gefunden mit der ID " + kategorie.getIdKategorie();
			throw new NotFoundExceptionRest(msg, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}
	
	@Override
	public Response createKategorieHasArtikel(Kategorie kategorie, Long idArtikel, HttpHeaders headers, UriInfo uriInfo) throws InvalidArtikelIdException, NotFoundExceptionRest, KategorieHasArtikelValidationException, KategorieHasArtikelDuplikatException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		Artikel artikel;
		try {
			artikel = av.findArtikelById(idArtikel, locale);
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest("NO_KATEGORIEHASARTIKEL_FOUND_WITH_ID" + idArtikel, e);
		}
		
		KategorieHasArtikel kategorieHasArtikel = av.createKategorieHasArtikel(kategorie, artikel, locale, false);
		
		final UriBuilder uriBuilder = getUriBuilderKategorieHasArtikel(uriInfo);
		final URI location = uriBuilder.build(kategorieHasArtikel.getIdkategoriehasartikel());
		final Response response = Response.created(location).build();
		
		return response;
	}



	private UriBuilder getUriBuilderKategorieHasArtikel(UriInfo uriInfo) {
		LOGGER.debug("BEGINN getUriKategorieHasArtikel: uriInfo={}", uriInfo);
		
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
        .path(ArtikelverwaltungResource.class)
        .path(ArtikelverwaltungResource.class, "findKategorieHasArtikel");
		
		LOGGER.debug("ENDE getUriBuilderKategorieHasArtikel: uriInfo={}", uriInfo);
		return ub;
	}



	@Override
	public KategorieHasArtikel findKategorieHasArtikel(Long id, HttpHeaders headers,
			UriInfo uriInfo) throws NotFoundExceptionRest,
			InvalidKategorieIdException, InvalidKategorieHasArtikelIdException {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);

		KategorieHasArtikel kategorieHasArtikel;
		try {
			kategorieHasArtikel = av.findKategorieHasArtikelById(id, locale); 
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest("NO_KATEGORIEHASARTIKEL_FOUND_WITH_ID" + id, e);
		}
		
		return kategorieHasArtikel;
	}



	@Override
	public Response deleteKategorieHasArtikel(Long id, HttpHeaders headers,
			UriInfo uriInfo) throws InvalidKategorieHasArtikelIdException, NotFoundExceptionRest  {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		try {
			KategorieHasArtikel kategorieHasArtikel = av.findKategorieHasArtikelById(id, locale);
			av.deleteKategorieHasArtikel(kategorieHasArtikel, locale); 
		}
		catch (NotFoundException e) {
			throw new NotFoundExceptionRest("NO_KATEGORIEHASARTIKEL_FOUND_WITH_ID" + id, e);
		}
		
		final Response response = Response.noContent().build();
		return response;
	}
}
