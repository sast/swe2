package de.webshop.benutzerverwaltung.service;

import static de.webshop.util.AbstractDao.QueryParameter.with;
import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.util.AbstractDao;
import de.webshop.util.NotFoundException;

@Stateless
@TransactionAttribute(MANDATORY)
public class BenutzerverwaltungDAO extends AbstractDao implements Serializable {

	private static final long serialVersionUID = -2424244400838077802L;
	
	@PersistenceContext
	private EntityManager em;
	
	public AbstractBenutzer findBenutzerByIDFetchAll(Long id) throws NotFoundException {
		
		AbstractBenutzer benutzer = findSingle(AbstractBenutzer.FIND_BENUTZER_BY_ID, with(AbstractBenutzer.PARAM_ID, id).parameters(), AbstractBenutzer.class);
		
		benutzer.getBestellungen().size();
		benutzer.getProduktbewertungen().size();
		benutzer.getRollehasbenutzer().size();
		
		return benutzer;
	}
	
	public AbstractBenutzer reloadBenutzer(AbstractBenutzer benutzer) {
		if (benutzer == null) {
			return null;
		}
		
		if (em.contains(benutzer)) {
			return benutzer;
		}
		
		final Long id = benutzer.getIdBenutzer();
		benutzer = em.find(AbstractBenutzer.class, id);
		return benutzer;
	}
}
