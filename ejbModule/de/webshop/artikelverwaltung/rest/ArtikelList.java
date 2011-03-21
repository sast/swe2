package de.webshop.artikelverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.artikelverwaltung.domain.Artikel;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArtikelList {

	@XmlElementRef
	private List<Artikel> artikelList;
		
	public ArtikelList() {
		super();
	}
	
	public ArtikelList(List<Artikel> artikelList) {
		this.artikelList = artikelList;
	}

	public void setArtikel(List<Artikel> artikelList) {
		this.artikelList = artikelList;
	}

	public List<Artikel> getArtikel() {
		return artikelList;
	}
}
