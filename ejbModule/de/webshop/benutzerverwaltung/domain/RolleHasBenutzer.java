package de.webshop.benutzerverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.util.IdGroup;

/**
 * Klasse RolleHasBenutzer
 * Stellt die Beziehung zwischen Benutzern und ihren zugewiesenen Rollen dar
 */

@Entity
@Table(name = "rolle_has_benutzer")
@XmlRootElement
@XmlAccessorType(FIELD)
public class RolleHasBenutzer implements Serializable {

	private static final long serialVersionUID = -5070950962769498582L;
	
	//TODO: Validation Messages fuer Constraints anlegen!
	
	@Id
	@GeneratedValue(generator = "rollehasbenutzer_seq")
	@SequenceGenerator(name = "rollehasbenutzer_seq", sequenceName = "rolle_has_benutzer_idrollehasbenutzer_seq", allocationSize = 1)
	@Column(name = "idrollehasbenutzer")
	@Min(value = MIN_ID, message = "{benutzerverwaltung.rollehasbenutzer.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idRolleHasBenutzer;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "rolle_idrolle", nullable = false, insertable = true, updatable = false)
	@NotNull
	private Rolle rolle;
	
	//TODO: den Benutzer wieder in RolleHasBenutzer aufnehmen, damit Objekte der Klasse aussagekraeftiger (--> WebService) sind!
	/*@Transient
	@ManyToOne(optional = false)
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	@NotNull
	private AbstractBenutzer benutzer;*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rolle == null) ? 0 : rolle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RolleHasBenutzer other = (RolleHasBenutzer) obj;
		if (rolle == null) {
			if (other.rolle != null) {
				return false;
			}
		} 
		else if (!rolle.equals(other.rolle)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RolleHasBenutzer [idRolleHasBenutzer=" + idRolleHasBenutzer
				+ ", getClass()=" + getClass() + "]";
	}
	
	public Rolle getRolle() {
		return rolle;
	}

	public void setRolle(Rolle rolle) {
		this.rolle = rolle;
	}

/*	public AbstractBenutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(AbstractBenutzer benutzer) {
		this.benutzer = benutzer;
	}
*/
	public void setIdRolleHasBenutzer(Long idRolleHasBenutzer) {
		this.idRolleHasBenutzer = idRolleHasBenutzer;
	}

	public Long getIdRolleHasBenutzer() {
		return idRolleHasBenutzer;
	}

}
