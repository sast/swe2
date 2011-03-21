package de.webshop.benutzerverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BenutzerList {

	@XmlElementRef
	private List<AbstractBenutzer> benutzerList;
	
	public BenutzerList() {
		super();
	}
	
	public BenutzerList(List<AbstractBenutzer> benutzerList) {
		this.benutzerList = benutzerList;
	}

	public List<AbstractBenutzer> getBenutzerList() {
		return benutzerList;
	}

	public void setBenutzerList(List<AbstractBenutzer> benutzerList) {
		this.benutzerList = benutzerList;
	}
}
