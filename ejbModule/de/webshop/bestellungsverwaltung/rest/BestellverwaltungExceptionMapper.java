package de.webshop.bestellungsverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.bestellungsverwaltung.service.AbstractBestellverwaltungException;

@Provider
public class BestellverwaltungExceptionMapper implements ExceptionMapper<AbstractBestellverwaltungException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BestellverwaltungExceptionMapper.class);

	@Override
	public Response toResponse(AbstractBestellverwaltungException e) {
		LOGGER.debug("BEGINN toResponse: {}", e.getMessage());
		
		final String msg = e.getMessage();
		final Response response = Response.status(CONFLICT)
		                                  .type(TEXT_PLAIN)
		                                  .entity(msg)
		                                  .build();
		
		LOGGER.debug("ENDE toResponse");
		return response;
	}

}
