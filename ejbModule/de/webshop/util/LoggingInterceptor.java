package de.webshop.util;

import java.util.Collection;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interceptor zum Tracing von public-Methoden der Session Beans und MessageDriven Beans (MDB).
 * Sowohl der Methodenaufruf als auch der Rueckgabewert (nicht: Exception) werden mit
 * Level DEBUG protokolliert.
 */
public class LoggingInterceptor {
	private static final String BEGIN = "BEGINN ";
	private static final String END = "ENDE ";
	
	private static final String COUNT = "Anzahl = ";
	private static final int MAX_ELEM = 4;  // bei Collections wird ab 5 Elementen nur die Anzahl ausgegeben

	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {
		
		final Object bean = ctx.getTarget();
		final Class<?> clazz = bean.getClass();
		final Logger logger = LoggerFactory.getLogger(clazz);

		if (!logger.isDebugEnabled()) {
			return ctx.proceed();
		}

		final String methodName = ctx.getMethod().getName();
		final Object[] params = ctx.getParameters();

		// Methodenaufruf protokollieren
		final StringBuilder sb = new StringBuilder(BEGIN + methodName);
		if (params != null) {
			final int anzahlParams = params.length;
			sb.append(": ");
			for (int i = 0; i < anzahlParams; i++) {
				if (params[i] == null) {
					sb.append("null");
				}
				else {
					final Class<?> paramClass = params[i].getClass();
					if (paramClass == null) {
						// primitiver Wert, z.B. int
						sb.append(params[i]);
					}
					else {
						final String paramStr = toString(params[i]);
						sb.append(paramStr);
					}
				}
				sb.append(", ");
			}
			final int laenge = sb.length();
			sb.delete(laenge - 2, laenge - 1);
		}
		logger.debug(sb.toString());
		
		Object result = null;
//		try {
			// Eigentlicher Methodenaufruf
			result = ctx.proceed();
			
		// Keine Protokollierung der geworfenen Exception:
		// 1) Stacktrace wuerde abgeschnitten werden
		// 2) Exception wird an der Ursprungsstelle bereits protokolliert.
		//    Wenn der LoggingInterceptor in ejb-jar.xml abgeklemmt wird,
		//    muss naemlich immer noch eine Protokollierung stattfinden.

//		}
//		catch (Exception e) {
//			// Methode hat eine Exception geworfen
//			log.error(methodName + ": " + e.getMessage());
//			throw e;
//		}

		if (result == null) {
			// Methode vom Typ void oder Rueckgabewert null
			logger.debug(END + methodName);
		}
		else {
			final String resultStr = toString(result);
			logger.debug(END + methodName + ": " + resultStr);
		}
		
		return result;
	}
	
	/**
	 * Collection oder Array oder Objekt in einen String konvertieren
	 */
	private static String toString(Object obj) {
		if (obj instanceof Collection<?>) {
			// Collection: Elemente bei kleiner Anzahl ausgeben; sonst nur die Anzahl
			final Collection<?> coll = (Collection<?>) obj;
			final int anzahl = coll.size();
			if (anzahl > MAX_ELEM) {
				return COUNT + coll.size();
			}

			return coll.toString();
		}
		
		if (obj.getClass().isArray()) {
			// Array in String konvertieren: Element fuer Element
			final String str = arrayToString(obj);
			return str;
		}

		// Objekt, aber keine Collection und kein Array
		return obj.toString();
	}
	
	/**
	 * Array in einen String konvertieren
	 */
	private static String arrayToString(Object obj) {
		final Class<?> componentClass = obj.getClass().getComponentType();

		if (!componentClass.isPrimitive()) {
			// Array von Objekten
			final Object[] arr = (Object[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				if (arr[i] == null) {
					sbEnd.append("null");
				}
				else {
					sbEnd.append(arr[i]);
				}
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			sbEnd.delete(laenge - 2, laenge - 1);
			sbEnd.append("]");
			return sbEnd.toString();
		}
		
		// Array von primitiven Werten: byte, short, int, long, ..., float, double, boolean, char
		
		if ("short".equals(componentClass.getName())
				|| "int".equals(componentClass.getName())
				|| "long".equals(componentClass.getName())) {
			final long[] arr = (long[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			sbEnd.delete(laenge - 2, laenge - 1);
			sbEnd.append("]");
			return sbEnd.toString();
		}
		
		if ("byte".equals(componentClass.getName())) {
			return "<byte-array>";
		}

		if ("float".equals(componentClass.getName())
			     || "double".equals(componentClass.getName())) {
			final double[] arr = (double[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			sbEnd.delete(laenge - 2, laenge - 1);
			sbEnd.append("]");
			return sbEnd.toString();
		}

		if ("char".equals(componentClass.getName())) {
			final char[] arr = (char[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			sbEnd.delete(laenge - 2, laenge - 1);
			sbEnd.append("]");
			return sbEnd.toString();
		}

		if ("boolean".equals(componentClass.getName())) {
			final boolean[] arr = (boolean[]) obj;
			if (arr.length > MAX_ELEM) {
				return COUNT + arr.length;
			}

			final StringBuilder sbEnd = new StringBuilder("[");
			final int anzahl = arr.length;
			for (int i = 0; i < anzahl; i++) {
				sbEnd.append(arr[i]);
				sbEnd.append(", ");
			}
			final int laenge = sbEnd.length();
			sbEnd.delete(laenge - 2, laenge - 1);
			sbEnd.append("]");
			return sbEnd.toString();
		}

		throw new IllegalStateException();
	}
}
