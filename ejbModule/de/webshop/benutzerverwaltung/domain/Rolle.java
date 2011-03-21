package de.webshop.benutzerverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.EnumType.STRING;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.webshop.util.IdGroup;

/**
 * Klasse Rolle
 * Werden Usern zur Zugriffsverwaltung zugewiesen
 */

@Entity
@Table(name = "rolle")
@NamedQueries({
    @NamedQuery(name = Rolle.FIND_ROLLE_N,
    			query = "SELECT r "  
    					+ "FROM Rolle r"),
	@NamedQuery(name = Rolle.FIND_ROLLE_BY_ID,
    			query = "SELECT r " 
       					+ "FROM Rolle r " 
    					+ "WHERE r.idrolle = :" + Rolle.PARAM_ID)
    })
@XmlRootElement
@XmlAccessorType(FIELD)
public class Rolle implements Serializable {

	private static final long serialVersionUID = 6020250387602280583L;
	
	public static final String PREFIX = "Rolle.";
	public static final String FIND_ROLLE_N = "findRolle";
	public static final String FIND_ROLLE_BY_ID = PREFIX + "findRolleByID";
	
	public static final String PARAM_ID = "idRolle";
	
	@Id
	@GeneratedValue(generator = "rolle_seq")
	@SequenceGenerator(name = "rolle_seq", sequenceName = "rolle_idrolle_seq", allocationSize = 1)
	@Min(value = MIN_ID, message = "{benutzerverwaltung.rolle.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idrolle;
	
	public enum RolleTyp { KUNDE, MITARBEITER, ADMINISTRATOR };
	
	@Column(name = "rolle", length = 64, nullable = false)
	@Enumerated(STRING)
	@Valid
	private RolleTyp rolleTyp;
	
	@Transient
	@OneToMany
	/**
	 * FIXME: warum muss nullable = false sein, damit der Fehler:
	 * org.hibernate.MappingException: Duplicate property mapping of _rollehasbenutzerBackref found in de.webshop.benutzerverwaltung.RolleHasBenutzer
	 * nicht mehr erscheint?
	 */
	@JoinColumn(name = "rolle_idrolle", nullable = false, insertable = false, updatable = false)
	private Set<RolleHasBenutzer> rollehasbenutzer;

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idrolle == null) ? 0 : idrolle.hashCode());
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
		Rolle other = (Rolle) obj;
		if (idrolle == null) {
			if (other.idrolle != null) {
				return false;
			}
		} 
		else if (!idrolle.equals(other.idrolle)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rolle [idrolle=" + idrolle + ", rolle=" + rolleTyp
				+ ", getClass()=" + getClass() + "]";
	}
	
	public Long getIdrolle() {
		return idrolle;
	}
	
	public void setIdrolle(Long idrolle) {
		this.idrolle = idrolle;
	}

	public RolleTyp getRolle() {
		return rolleTyp;
	}

	public void setRolle(RolleTyp rolle) {
		this.rolleTyp = rolle;
	}

	public void setRollehasbenutzer(Set<RolleHasBenutzer> rollehasbenutzer) {
		this.rollehasbenutzer = rollehasbenutzer;
	}

	public Set<RolleHasBenutzer> getRollehasbenutzer() {
		return rollehasbenutzer;
	}
}
