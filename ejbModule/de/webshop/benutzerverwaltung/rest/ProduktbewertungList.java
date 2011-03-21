package de.webshop.benutzerverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.artikelverwaltung.domain.Produktbewertung;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProduktbewertungList {

	@XmlElementRef
	private List<Produktbewertung> produktbewertungList;
	
	public ProduktbewertungList() {
		super();
	}
	
	public ProduktbewertungList(List<Produktbewertung> produktbewertungList) {
		this.produktbewertungList = produktbewertungList;
	}

	public List<Produktbewertung> getProduktbewertungList() {
		return produktbewertungList;
	}

	public void setProduktbewertungList(List<Produktbewertung> produktbewertungList) {
		this.produktbewertungList = produktbewertungList;
	}
}
