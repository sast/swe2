package de.webshop.lagerverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.Collection;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.lagerverwaltung.service.LagerartikelValidationException;

@Provider
public class LagerartikelValidationExceptionMapper implements ExceptionMapper<LagerartikelValidationException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LagerartikelValidationExceptionMapper.class);

	@Override
	public Response toResponse(LagerartikelValidationException e) {
		LOGGER.debug("BEGINN toResponse: {}", e.getViolations());
		
		final Collection<ConstraintViolation<Lager>> violations = e.getViolations();
		final StringBuilder sb = new StringBuilder();
		for (ConstraintViolation<Lager> v : violations) {
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

