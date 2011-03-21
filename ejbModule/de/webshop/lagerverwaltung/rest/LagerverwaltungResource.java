package de.webshop.lagerverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.webshop.artikelverwaltung.service.ArtikelValidationException;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.lagerverwaltung.service.InvalidLagerIdException;
import de.webshop.lagerverwaltung.service.LagerArtikelDuplikatException;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;
import de.webshop.util.NotFoundExceptionRest;

@Path("/lagerverwaltung")
@Produces({APPLICATION_XML, APPLICATION_JSON })
@Consumes
public interface LagerverwaltungResource {

	@GET
	@Path("/lager/{id:[0-9]+}")
	Lager findLagerById(@PathParam("id") Long idLager, @Context HttpHeaders headers, @Context UriInfo uriInfo) 
		throws NotFoundExceptionRest, InvalidLagerIdException;
	
	@GET
	@Path("/lager/artikel/{id:[0-9]+}")
	LagerList findLagerByArtikelId(@PathParam("id") Long idArtikel, @Context HttpHeaders headers, @Context UriInfo uriInfo) 
		throws NotFoundExceptionRest, InvalidArtikelIdException;
	
	@PUT
	@Path("/lager")
	@Consumes({APPLICATION_XML })
	Lager updateLagerArtikel(Lager lagerArtikel, @Context HttpHeaders headers, @Context UriInfo uriInfo)
		throws LagerartikelValidationException, NotFoundExceptionRest, InvalidLagerIdException;
	
	@POST
	@Path("/lager")
	@Consumes({APPLICATION_XML })
	@Produces
	Response createLagerArtikel(Lager lagerArtikel, @Context HttpHeaders headers, @Context UriInfo uriInfo)
		throws LagerArtikelDuplikatException, LagerartikelValidationException, InvalidArtikelIdException, NotFoundExceptionRest, ArtikelValidationException;
	
	@DELETE
	@Path("/lager/{id:[0-9]+}")
	@Produces
	Response deleteLagerArtikel(@PathParam("id") Long idLager, @Context HttpHeaders headers)
		throws NotFoundExceptionRest, InvalidLagerIdException;
	
	
}
