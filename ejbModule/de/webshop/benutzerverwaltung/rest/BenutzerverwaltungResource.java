package de.webshop.benutzerverwaltung.rest;

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

import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.artikelverwaltung.service.InvalidArtikelIdException;
import de.webshop.artikelverwaltung.service.InvalidProduktbewertungIdException;
import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.domain.Rolle;
import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;
import de.webshop.benutzerverwaltung.domain.Rolle.RolleTyp;
import de.webshop.benutzerverwaltung.service.BenutzerDuplikatException;
import de.webshop.benutzerverwaltung.service.BenutzerValidationException;
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
import de.webshop.bestellungsverwaltung.rest.BestellungList;
import de.webshop.util.GenericMethodException;
import de.webshop.util.InvalidUriExceptionRest;
import de.webshop.util.NotFoundException;
import de.webshop.util.NotFoundExceptionRest;

//was passiert, wenn das '/' weggelassen wird?? --> Test ergab, dass '/' auch weggelassen werden kann
//TODO: deleteLieferadresse() implementieren!

@Path("/benutzerverwaltung")
@Produces({APPLICATION_XML, APPLICATION_JSON })  //http://download.oracle.com/javaee/6/api/index.html?javax/ws/rs/package-summary.html
@Consumes
public interface BenutzerverwaltungResource {

//#### GET - BenutzerList
	@GET
	@Path("/benutzer")
	BenutzerList findBenutzerN(@QueryParam("nachname") @DefaultValue("") String nachname, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidNachnameException, NotFoundExceptionRest, GenericMethodException;
	
	@GET
	@Path("/adresse/{plz:[0-9][0-9][0-9][0-9][0-9]}/benutzer")
	BenutzerList findBenutzerNByPLZ(@PathParam("plz") String plz, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidPlzException;
	
	//wandelt den Pfadparameter z.B. KUNDE in einen Enum-Wert um
	@GET
	@Path("/rolle/{typ:[A-Z]+}/benutzer")
	BenutzerList findBenutzerNByRolleTyp(@PathParam("typ") RolleTyp typ, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidRolleException;
//#### GET - BenutzerList - END
	
//#### GET - Benutzer	
	@GET
	@Path("/benutzer/{id:[0-9]+}")
	AbstractBenutzer findBenutzerById(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidBenutzerIdException;
	
	//Hinweis:
	//\u002D -->  '-'
	//\u002E -->  '.'
	//\u002B -->  '+'
	
	//TODO: vllt. besser als EMAIL als QueryParam realisieren!
	@GET
	@Path("/benutzer/{email:[A-Za-z0-9\u002E\u005F\u0025\u002B\u002D]+@[A-Za-z0-9\u002E\u002D]+\u002E[A-Za-z]{2,4}}")
	AbstractBenutzer findBenutzerByEmail(@PathParam("email") String email, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidEmailException, GenericMethodException;
//#### GET - Benutzer - END
	
	@GET
	@Path("/benutzer/{id:[0-9]+}/bestellungen")
	BestellungList findBestellungenByBenutzerId(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidBenutzerIdException;
	
	@GET
	@Path("/benutzer/bestellungen")
	BestellungList findBestellungenByBenutzerNachname(@QueryParam("nachname") @DefaultValue("") String nachname, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidNachnameException, NotFoundExceptionRest;
//#### GET - BestellungList - END
	
	@GET
	@Path("/produktbewertung/{id:[0-9]+}")
	Produktbewertung findProduktbewertungById(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidProduktbewertungIdException, GenericMethodException;
	
	@GET
	@Path("/produktbewertungen")
	ProduktbewertungList findProduktbewertungByBenutzerIdAndArtikelId(@QueryParam("idbenutzer") @DefaultValue("-1") long idBenutzer, @QueryParam("idartikel") @DefaultValue("-1") long idArtikel, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidBenutzerIdException, InvalidArtikelIdException;
	
	@GET
	@Path("/rollehasbenutzer/{id:[0-9]+}")
	RolleHasBenutzer findRolleHasBenutzerById(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidRolleHasBenutzerIdException;

	@GET
	@Path("/rollehasbenutzer")
	RolleHasBenutzer findRolleHasBenutzerByBenutzerIdAndRolletyp(@QueryParam("idbenutzer") @DefaultValue("") Long idBenutzer, @QueryParam("rolletyp") @DefaultValue("") Long idRolle, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidBenutzerIdException, InvalidRolleException;
	
//#### CREATE | UPDATE | DELETE - Benutzer
	@Path("/benutzer")
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createBenutzer(AbstractBenutzer benutzer, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws BenutzerValidationException, BenutzerDuplikatException, InvalidEmailException;
	
	@Path("/benutzer")
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	AbstractBenutzer updateBenutzer(AbstractBenutzer benutzer, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, BenutzerValidationException, BenutzerDuplikatException, InvalidBenutzerIdException;
	
	@Path("/benutzer/{id:[0-9]+}")
	@DELETE
	@Produces
	Response deleteBenutzer(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, BenutzerValidationException, BenutzerDuplikatException, NotFoundException, InvalidBenutzerIdException;
//#### CREATE | UPDATE | DELETE - Benutzer - END

//#### CREATE | DELETE - Produktbewertung
	//@Path("/benutzer/{bid:[0-9]+}/artikel/{aid:[0-9]+}/produktbewertung")
	@Path("/produktbewertung")
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createProduktbewertung(Produktbewertung produktbewertung, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidBenutzerIdException, InvalidArtikelIdException, ProduktbewertungValidationException, ProduktbewertungDuplikatException, InvalidUriExceptionRest, NotFoundExceptionRest;

	@Path("/produktbewertung/{id:[0-9]+}")
	@DELETE
	@Produces
	Response deleteProduktbewertung(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidProduktbewertungIdException;
//#### CREATE | DELETE - Produktbewertung - END
	
//#### CREATE | DELETE - RolleHasBenutzer
	//TODO: ggf. anpassen an @Path("/benutzer/{id:[0-9]+}/rolle/{typ:[A-Z]+}") --> erspart Uebergabe der XML-Struktur!
	@Path("/benutzer/{id:[0-9]+}/rolle")
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	Response createRolleHasBenutzer(Rolle rolle, @PathParam("id") Long idBenutzer, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws InvalidBenutzerIdException, RolleHasBenutzerValidationException, RolleHasBenutzerDuplikatException, NotFoundExceptionRest;

	//TODO: ggf das hier verwenden: @Path("/benutzer/{idbenutzer:[0-9]+}/rolle/{idrolle:[0-9]+}")
	@Path("/rollehasbenutzer/{id:[0-9]+}")
	@DELETE
	@Produces
	Response deleteRolleHasBenutzer(@PathParam("id") Long id, @Context HttpHeaders headers, @Context UriInfo uriInfo) throws NotFoundExceptionRest, InvalidRolleHasBenutzerIdException;
//#### CREATE | DELETE - RolleHasBenutzer - END
}
