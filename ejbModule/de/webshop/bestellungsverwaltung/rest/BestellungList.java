package de.webshop.bestellungsverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.bestellungsverwaltung.domain.Bestellung;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BestellungList {

	@XmlElementRef
	private List<Bestellung> bestellungList;

	public BestellungList() {
		super();
	}

	public BestellungList(List<Bestellung> bestellungList) {
		this.bestellungList = bestellungList;
	}

	public List<Bestellung> getBestellungList() {
		return bestellungList;
	}

	public void setBestellungList(List<Bestellung> bestellungList) {
		this.bestellungList = bestellungList;
	}
}
