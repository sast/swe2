package de.webshop.bestellungsverwaltung.service;

import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.util.AbstractDao;

@Stateless
@TransactionAttribute(MANDATORY)
public class BestellverwaltungDao extends AbstractDao implements Serializable {

	private static final long serialVersionUID = 1338556089076605004L;
	
	public List<Bestellung> reloadBestellungenMitPositionen(List<Bestellung> bestellungen) {
		
		if (bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}
		
		final List<Long> idsBestellungen = new ArrayList<Long>();
		for (Bestellung b : bestellungen) {
			idsBestellungen.add(b.getIdbestellung());
		}
		
		return bestellungen;
	}
	
	public Bestellung reloadBestellung(Bestellung bestellung) {
		if (bestellung == null) {
			return null;
		}
		
		if (em.contains(bestellung)) {
			return bestellung;
		}
		
		final Long id = bestellung.getIdbestellung();
		bestellung = em.find(Bestellung.class, id);
		return bestellung;
	}
}
