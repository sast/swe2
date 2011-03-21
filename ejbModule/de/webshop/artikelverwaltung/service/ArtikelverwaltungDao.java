package de.webshop.artikelverwaltung.service;

import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.util.AbstractDao;

@Stateless
@TransactionAttribute(MANDATORY)
public class ArtikelverwaltungDao extends AbstractDao implements Serializable {

	private static final long serialVersionUID = -7141899216513042744L;
	
	@PersistenceContext
	private EntityManager em;
	
	public Artikel reloadArtikel(Artikel artikel) {
		if (artikel == null) {
			return null;
		}
		
		if (em.contains(artikel)) {
			return artikel;
		}
		
		final Long id = artikel.getIdArtikel();
		artikel = em.find(Artikel.class, id);
		return artikel;
	}
}
