package de.webshop.util;

import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@TransactionAttribute(MANDATORY)
public abstract class AbstractDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2929877565285887967L;
	
	@PersistenceContext
	protected transient EntityManager em;
	
	/*
	public <T> void validate(Class<T> clazz, Class) throws Exception {
				
	}
	*/
	
	/**
	 */
	public <T> T find(Class<T> clazz, Object id) throws NotFoundException {
		final T result = em.find(clazz, id);
		if (result == null) {
			throw new NotFoundException("Kein Treffer fuer die ID: " + id, clazz);
		}
		return result;
	}
	
	/**
	 */
	public <T> List<T> find(String namedQuery, Class<T> clazz) throws NotFoundException {
		final List<T> result = em.createNamedQuery(namedQuery, clazz)
		                         .getResultList();
		if (result.isEmpty()) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz);
		}
		return result;
	}
	
	/**
	 */
	public <T> List<T> find(String namedQuery, Map<String, Object> parameters, Class<T> clazz) throws NotFoundException {
		final TypedQuery<T> query = em.createNamedQuery(namedQuery, clazz);

		final Set<Entry<String, Object>> paramSet = parameters.entrySet();  // Map in Set konvertieren fuer for-Schleife
		for (Entry<String, Object> entry : paramSet) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		
		final List<T> result = query.getResultList();
		if (result.isEmpty()) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz);
		}
		return result;
	}
	
	/**
	 */
	public <T> List<T> find(String namedQuery, int resultLimit, Class<T> clazz) throws NotFoundException {
		final List<T> result = em.createNamedQuery(namedQuery, clazz)
		                         .setMaxResults(resultLimit)
	                             .getResultList();
		if (result.isEmpty()) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz);
		}
		return result;
	}
	
	/**
	 */
	public <T> List<T> find(String namedQuery, Map<String, Object> parameters, int resultLimit, Class<T> clazz) throws NotFoundException {
		final TypedQuery<T> query = em.createNamedQuery(namedQuery, clazz);
		query.setMaxResults(resultLimit);

		final Set<Entry<String, Object>> paramSet = parameters.entrySet();  // Map in Set konvertieren fuer for-Schleife
		for (Entry<String, Object> entry : paramSet) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		
		final List<T> result = query.getResultList();
		if (result.isEmpty()) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz);
		}
		return result;
	}

	
	/**
	 */
	public <T> T findSingle(String namedQuery, Class<T> clazz) throws NotFoundException {
		try {
			return em.createNamedQuery(namedQuery, clazz)
			         .getSingleResult();
		}
		catch (NoResultException e) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz, e);
		}
	}
	
	/**
	 */
	public <T> T findSingle(String namedQuery, Map<String, Object> parameters, Class<T> clazz) throws NotFoundException {
		final TypedQuery<T> query = em.createNamedQuery(namedQuery, clazz);

		final Set<Entry<String, Object>> paramSet = parameters.entrySet();  // Map in Set konvertieren fuer for-Schleife
		for (Entry<String, Object> entry : paramSet) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		
		try {
			return query.getSingleResult();
		}
		catch (NoResultException e) {
			throw new NotFoundException("Kein Treffer fuer die Query: " + namedQuery, clazz, e);
		}
	}
	
	
	/**
	 */
	public List<?> findUsingSQL(String sql, String resultSetMapping) {
		return em.createNativeQuery(sql, resultSetMapping)
		         .getResultList();
	}

	/**
	 */
	public <T> T create(T obj) {
		em.persist(obj);
		return obj;
	}
	
	/**
	 */
	public <T> T update(T obj) {
		return em.merge(obj);
	}
	
	/**
	 */
	public void delete(Object obj, Object id) {
		if (!em.contains(obj)) {
			obj = em.find(obj.getClass(), id);
		}
		em.remove(obj);
	}
	
	/**
	 */
	public void delete(Class<?> clazz, Object id) {
		final Object obj = em.find(clazz, id);
		em.remove(obj);
	}
	
	/**
	 * Beispiel:
	 * public List<Bestellung> findBestellungen(Long kundeId, Bestellung.class) {
	 *    return dao.find(Bestellung.FIND_BY_KUNDEID,
	 *                    with(Bestellung.PARAM_KUNDEID, kundeId).parameters(),
	 *                    Bestellung.class);
	 */
	public static final class QueryParameter {
		private final Map<String, Object> params;
			
		private QueryParameter(String name, Object value) {
			params = new HashMap<String, Object>();
			params.put(name, value);
		}
		
		public static QueryParameter with(String name, Object value) {
			return new QueryParameter(name, value);
		}
	
		public QueryParameter and(String name, Object value) {
			params.put(name, value);
			return this;
		}
		
		public Map<String, Object> parameters() {
			return params;
		}
	}


}
