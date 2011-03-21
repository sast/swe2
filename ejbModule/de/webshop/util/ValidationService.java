package de.webshop.util;


import static javax.ejb.LockType.READ;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/*
 * Bei Verwendung von JSF werden die Attribute bereits in der
 * Praesentationsschicht validiert.
 */
@Singleton
@Lock(READ)
//@Startup
public class ValidationService implements Serializable {
	private static final long serialVersionUID = -2597507987127159758L;
	
	private final List<Locale> locales = Arrays.asList(Locale.ENGLISH, Locale.GERMAN);
	private final HashMap<Locale, Validator> validators = new HashMap<Locale, Validator>();
	
	// https://jira.jboss.org/browse/JBAS-8474
	//@Resource
	//private ValidatorFactory validatorFactory;
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		
		for (Locale locale : locales) {
			final MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(validatorFactory.getMessageInterpolator(),
                                                                                           locale);
			Validator validator = validatorFactory.usingContext()
                                                  .messageInterpolator(interpolator)
                                                  .getValidator();
			validators.put(locale, validator);
		}
	}
	
	/*
	 * JSF liefert Locale durch FacesContext.getCurrentInstance().getViewRoot().getLocale();
	 * JAX-RS liefert List<Locale> durch HttpHeaders.getAcceptableLanguages() mit absteigenden Prioritaeten.
	 */
	public Validator getValidator(Locale locale) {
		if (locale == null) {
			locale = locales.get(0);   // Default-Sprache
		}
		
		Validator validator = validators.get(locale);
		if (validator != null) {
			return validator;
		}
			
		if (!locale.getCountry().isEmpty()) {
			// z.B. de_DE
			locale = new Locale(locale.getLanguage());  // z.B. de_DE -> de
			validator = validators.get(locale);
			if (validator != null) {
				return validator;
			}
		}

		return validators.get(locales.get(0));   // Default-Wert
	}
	
	/**
	 * http://hibernate.org/~emmanuel/validation
	 * 
	 * ResourceBundle.getBundle(String, Locale, ClassLoader)
	 * ResourceBundleMessageInterpolator.loadBundle(ClassLoader, Locale, String) line: 206	
	 * ResourceBundleMessageInterpolator.getFileBasedResourceBundle(Locale) line: 177	
	 * ResourceBundleMessageInterpolator.findUserResourceBundle(Locale) line: 284	
	 * ResourceBundleMessageInterpolator.interpolateMessage(String, Map<String,Object>, Locale) line: 123	
	 * ResourceBundleMessageInterpolator.interpolate(String, MessageInterpolator$Context, Locale) line: 101	
	 * ValidationUtil$LocaleSpecificMessageInterpolator.interpolate(String, MessageInterpolator$Context) line: 100	
	 */
	public static class LocaleSpecificMessageInterpolator implements MessageInterpolator {
		private final MessageInterpolator interpolator;
		private final Locale locale;

		public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator, Locale locale) {
			this.locale = locale;
			this.interpolator = interpolator;
		}

		@Override
		public String interpolate(String message, Context context) {
			final String resultMessage = interpolator.interpolate(message, context, locale);
			return resultMessage;
		}

		@Override
		public String interpolate(String message, Context context, Locale loc) {
			return interpolator.interpolate(message, context, loc);
		}
	}
}
