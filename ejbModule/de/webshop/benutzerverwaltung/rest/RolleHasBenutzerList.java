package de.webshop.benutzerverwaltung.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.benutzerverwaltung.domain.RolleHasBenutzer;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RolleHasBenutzerList {

	@XmlElementRef
	private List<RolleHasBenutzer> rolleHasBenutzerList;
	
	public RolleHasBenutzerList() {
		super();
	}
	
	public RolleHasBenutzerList(List<RolleHasBenutzer> rolleHasBenutzerList) {
		this.rolleHasBenutzerList = rolleHasBenutzerList;
	}

	public List<RolleHasBenutzer> getRolleHasBenutzerList() {
		return rolleHasBenutzerList;
	}

	public void setRolleHasBenutzerList(List<RolleHasBenutzer> rolleHasBenutzerList) {
		this.rolleHasBenutzerList = rolleHasBenutzerList;
	}
}
