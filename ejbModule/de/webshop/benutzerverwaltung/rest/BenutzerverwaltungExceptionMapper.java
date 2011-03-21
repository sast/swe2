package de.webshop.benutzerverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.benutzerverwaltung.service.AbstractBenutzerverwaltungException;


@Provider
public class BenutzerverwaltungExceptionMapper implements ExceptionMapper<AbstractBenutzerverwaltungException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BenutzerverwaltungExceptionMapper.class);

	@Override
	public Response toResponse(AbstractBenutzerverwaltungException e) {
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
