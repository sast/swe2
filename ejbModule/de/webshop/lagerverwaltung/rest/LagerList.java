package de.webshop.lagerverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.lagerverwaltung.domain.Lager;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LagerList {

	@XmlElementRef
	private List<Lager> lagerList;
	
	public LagerList() {
		super();
	}
	
	public LagerList(List<Lager> lagerList) {
		this.lagerList = lagerList;
	}
	
	public List<Lager> getLagerList() {
		return this.lagerList;
	}
	
	public void setLagerList(List<Lager> lagerList) {
		this.lagerList = lagerList;
	}
}
