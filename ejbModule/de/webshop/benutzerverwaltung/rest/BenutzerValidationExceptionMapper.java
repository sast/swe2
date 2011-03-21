package de.webshop.benutzerverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.benutzerverwaltung.service.BenutzerValidationException;


@Provider
public class BenutzerValidationExceptionMapper implements ExceptionMapper<BenutzerValidationException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BenutzerValidationExceptionMapper.class);

	@Override
	public Response toResponse(BenutzerValidationException e) {
		LOGGER.debug("BEGINN toResponse: {}", e.getViolations());
		
		final Collection<ConstraintViolation<AbstractBenutzer>> violations = e.getViolations();
		final StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<AbstractBenutzer> v : violations) {
			sb.append(v.getMessage());
			sb.append(" ");
		}
		
		final String responseStr = sb.toString();
		final Response response = Response.status(NOT_FOUND)
		                                  .type(TEXT_PLAIN)
		                                  .entity(responseStr)
		                                  .build();
		
		LOGGER.debug("ENDE toResponse: {}", responseStr);
		return response;
	}

}
