package de.webshop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.artikelverwaltung.domain.Kategorie;
import de.webshop.artikelverwaltung.domain.KategorieHasArtikel;
import de.webshop.artikelverwaltung.service.ArtikelDuplikatException;
import de.webshop.artikelverwaltung.service.ArtikelValidationException;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidKategorieHasArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidKategorieIdException;
import de.webshop.artikelverwaltung.service.KategorieDeleteSubKategorieException;
import de.webshop.artikelverwaltung.service.KategorieHasArtikelDuplikatException;
import de.webshop.artikelverwaltung.service.KategorieHasArtikelValidationException;
import de.webshop.artikelverwaltung.service.KategorieValidationException;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;

@Path("/artikelverwaltung")
@Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })  //http://download.oracle.com/javaee/6/api/index.html?javax/ws/rs/package-summary.html
@Consumes
public interface ArtikelverwaltungResource {

	@GET
	@Path("/artikel")
	ArtikelList findArtikelB(@QueryParam("bezeichnung") @DefaultValue("") String nachname, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest;
	
	@GET
	@Path("/artikel/{id:[0-9]+}")
	Artikel findArtikel(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidArtikelIdException;
	
	@GET
	@Path("/kategorie/{id:[0-9]+}")
	Kategorie findKategorie(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidKategorieIdException;
	
	@GET
	@Path("/kategoriehasartikel/{id:[0-9]+}")
	KategorieHasArtikel findKategorieHasArtikel(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidKategorieIdException, InvalidKategorieHasArtikelIdException;
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Path("/artikel")
	@Produces
	Response createArtikel(Artikel artikel, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws ArtikelValidationException, ArtikelDuplikatException, NotFoundException;
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Path("/artikel")
	Artikel updateArtikel(Artikel artikel, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidArtikelIdException, ArtikelValidationException;
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Path("/artikel")
	@Produces
	Response createKategorie(Kategorie kategorie, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundException, KategorieValidationException;
	
	@PUT
	@Consumes({APPLICATION_XML, TEXT_XML })
	@Path("/kategorie")
	Kategorie updateKategorie(Kategorie kategorie, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidKategorieIdException, NotFoundExceptionRest, KategorieValidationException;
	
	@DELETE
	@Path("/kategorie/{id:[0-9]+}")
	Response deleteKategorie(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidKategorieIdException, NotFoundExceptionRest, KategorieDeleteSubKategorieException;

	@POST
	@Consumes({APPLICATION_XML, TEXT_XML })
	@Path("/artikel/{id:[0-9]+}/kategorie")
	Response createKategorieHasArtikel(Kategorie kategorie, @PathParam("id") Long idArtikel,
			HttpHeaders headers, UriInfo uriInfo) throws InvalidArtikelIdException, NotFoundExceptionRest, KategorieHasArtikelValidationException, KategorieHasArtikelDuplikatException;

	
	@DELETE
	@Produces
	@Path("/kategoriehasartikel/{id:[0-9]+}")
	Response deleteKategorieHasArtikel(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidKategorieHasArtikelIdException;
}
