package de.webshop.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class GenericMethod implements Serializable {
	

	private static final long serialVersionUID = -5474555642063340278L;
	private static final String GET_URI_BUILDER = "getUriBuilder";
	
	/** Diese generische Methode ermoeglicht es von jeder Resource-Klasse die jeweilige Domain-Klassen spezifische getUriBuilder-Methode aufzurufen und auf Basis dessen eine URI-Liste zusammen zu bauen. 
	 * 
	 * @param <T> - Parameter der das Interface DomainBean erweitert / implementiert.
	 * @param clazzOfMethod - ResourceEJB-Klasse in der die Methode getUriBuilderXYZ(UriInfo) hinterlegt ist.
	 * @param clazz - Domain-Klasse, die die getUriBuilder-Methode genau spezifiziert, z.B. Benutzer.class  -->  getUriBuilderBenutzer(uriInfo) 
	 * @param list - Liste mit Elementen vom Typ {clazz}, deren Id an die URL angefuegt wird.
	 * @param uriInfo -
	 * @return List<URI>: eine Liste mit URI-Objekten, die die URL fuer den Web Service auf ein z.B. Bestellungsobjekt beinhaltet. 
	 * @throws GenericMethodException
	 */
	public static <T extends DomainBean> List<URI> genericUpdateUri(Class<?> clazzOfMethod, Class<T> clazz, List<T> list, UriInfo uriInfo) throws GenericMethodException {
		List<URI> lstUris = null;
		if (list != null && !list.isEmpty()) {
			try {
				final Method method = clazzOfMethod.getMethod(GET_URI_BUILDER + clazz.getSimpleName(), UriInfo.class);
				final UriBuilder uriBuilder = (UriBuilder) method.invoke(null, uriInfo);
				lstUris = new ArrayList<URI>(list.size());
				for (T element : list) {
					final URI uri = uriBuilder.build(element.getId());
					lstUris.add(uri);
				}
			}
			catch (Exception e) {
				throw new GenericMethodException(e.getMessage(), e);
			}
		}
		
		return lstUris;
	}
	
	/** Diese generische Methode ermoeglicht es von jeder Resource-Klasse die jeweilige Domain-Klassen spezifische getUriBuilder-Methode aufzurufen und auf Basis dessen eine URI zusammen zu bauen. 
	 * 
	 * @param <T> - Parameter der das Interface DomainBean erweitert / implementiert.
	 * @param clazzOfMethod - ResourceEJB-Klasse in der die Methode getUriBuilderXYZ(UriInfo) hinterlegt ist.
	 * @param clazz - Domain-Klasse, die die getUriBuilder-Methode genau spezifiziert, z.B. Benutzer.class  -->  getUriBuilderBenutzer(uriInfo) 
	 * @param obj - Objekt vom Typ {clazz}, dessen Id an die URL angefuegt wird.
	 * @param uriInfo -
	 * @return URI: ein URI-Objekt, das die URL fuer den Web Service auf ein z.B. Benutzerobjekt beinhaltet. 
	 * @throws GenericMethodException
	 */
	public static <T extends DomainBean> URI genericUpdateUri(Class<?> clazzOfMethod, Class<T> clazz, T obj, UriInfo uriInfo) throws GenericMethodException {
		URI uri = null;
		if (obj != null) {
			try {
				final Method method = clazzOfMethod.getMethod(GET_URI_BUILDER + clazz.getSimpleName(), UriInfo.class);
				final UriBuilder uriBuilder = (UriBuilder) method.invoke(null, uriInfo);
				uri = uriBuilder.build(obj.getId());
			}
			catch (Exception e) {
				throw new GenericMethodException(e.getMessage(), e);
			}
		}
		
		return uri;
	}
}
