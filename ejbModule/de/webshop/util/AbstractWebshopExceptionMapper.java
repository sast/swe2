package de.webshop.util;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class AbstractWebshopExceptionMapper implements ExceptionMapper<AbstractWebshopException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWebshopExceptionMapper.class);

	@Override
	public Response toResponse(AbstractWebshopException e) {
		LOGGER.debug("BEGINN toResponse: {}", e.getMessage());
		
		final String msg = e.getMessage();
		final Response response = Response.status(NOT_FOUND)
		                                  .type(TEXT_PLAIN)
		                                  .entity(msg)
		                                  .build();
		
		LOGGER.debug("ENDE toResponse");
		return response;
	}

}
