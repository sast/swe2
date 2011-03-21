package de.webshop.bestellungsverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

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

import de.webshop.benutzerverwaltung.service.InvalidBenutzerIdException;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.bestellungsverwaltung.domain.Bestellung.Bestellstatus;
import de.webshop.bestellungsverwaltung.service.BestellungDeleteException;
import de.webshop.bestellungsverwaltung.service.BestellungDuplikatException;
import de.webshop.bestellungsverwaltung.service.BestellungValidationException;
import de.webshop.bestellungsverwaltung.service.InvalidBestellungIdException;
import de.webshop.lagerverwaltung.service.InvalidLagerIdException;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;

@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Path("/bestellverwaltung")
@Consumes
public interface BestellverwaltungResource {

	@GET
	@Path("/bestellung/{id:[0-9]+}")
	Bestellung findBestellung(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidBestellungIdException;
	
	@GET
	@Path("/bestellungen/{status:[A-Z]+}")
	BestellungList findBestellungenStatus(@PathParam("status") Bestellstatus status, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest;
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Path("/bestellung")
	@Produces
	Response createBestellung(Bestellung bestellung, @Context HttpHeaders headers, @Context UriInfo uriInfo)throws BestellungValidationException, BestellungDuplikatException, NotFoundExceptionRest, InvalidBenutzerIdException, InvalidLagerIdException;
		
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Path("/bestellung")
	Bestellung updateBestellung(Bestellung bestellung, @Context HttpHeaders headers, @Context UriInfo uriInfo)throws BestellungValidationException, NotFoundExceptionRest, InvalidBestellungIdException, LagerartikelValidationException, InvalidLagerIdException, NotFoundException;
	
	@DELETE
	@Path("/bestellung/{id:[0-9]+}")
	Response deleteBestellung(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo)throws NotFoundExceptionRest, BestellungValidationException, BestellungDeleteException, LagerartikelValidationException, InvalidBestellungIdException;
}
