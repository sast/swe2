package de.webshop.bestellungsverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.util.IdGroup;

@Entity
@Table(name = "bestellposition")
@XmlRootElement
@XmlAccessorType(FIELD)
public class Bestellposition implements Serializable {

	private static final long serialVersionUID = 6833564933951361241L;

	@Id
	@GeneratedValue(generator = "bestellposition_seq")
	@SequenceGenerator(name = "bestellposition_seq", sequenceName = "bestellposition_idbestellposition_seq", allocationSize = 1)
	@Column(name = "idbestellposition", unique = true, nullable = false)
	@Min(value = MIN_ID, message = "{bestellungsverwaltung.bestellposition.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idbestellposition;
	
	@ManyToOne
    @JoinColumn(name = "lager_idlager", nullable = false)
    @XmlTransient
	private Lager lagerArtikel;
	
	@Transient
	@XmlElement(name = "lagerArtikel")
	private URI lagerArtikelUri;
	
	//TODO: POSTGRES: Tabelle bestellposition --> Spalte position darf auch den Wert NULL beinhalten! (ist spaeter wieder zurueckzunehmen) --> siehe create_table.sql
	//nicht verwendet, damit in Bestellung nach position sortiert werden kann, da ansonsten Exception auftritt
	/*
	@Transient
	@Column(name = "position")
	@XmlTransient
	private Long position;*/
	
	@Column(name = "menge")
	@NotNull(message = "{bestellungsverwaltung.bestellposition.menge.notNull}")
	//@Size(min = 1, max = 4, message="{bestellungsverwaltung.bestellposition.menge.length}")
	//@Pattern(regexp = "[0-9]", message="{bestellungsverwaltung.bestellposition.menge.regexp.zahl}")
	@XmlElement(required = true)
	private Integer menge;

	
	@Temporal(TIMESTAMP)
	@Column(name = "erstellungsdatum", nullable = false, insertable = false)
	private Date erstellungsdatum;
	
	@Temporal(TIMESTAMP)
	@Column(name = "aenderungsdatum", insertable = false)
	private Date aenderungsdatum;

	public Bestellposition() {
		super();
	}
	
	@SuppressWarnings("unused")
	@PreUpdate
	private void aenderungsdatum() {
		aenderungsdatum = new Date();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idbestellposition == null) ? 0 : idbestellposition
						.hashCode());
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
		Bestellposition other = (Bestellposition) obj;
		if (idbestellposition == null) {
			if (other.idbestellposition != null) {
				return false;
			}
		} 
		else if (!idbestellposition.equals(other.idbestellposition)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Bestellposition [idbestellposition=" + idbestellposition
				+ ", lagerArtikel=" + lagerArtikel //+ ", position=" + position
				+ ", menge=" + menge + ", erstellungsdatum=" + erstellungsdatum
				+ ", aenderungsdatum=" + aenderungsdatum + ", getClass()=" + getClass() + "]";
	}

	public Lager getLagerArtikel() {
		return lagerArtikel;
	}

	public void setLagerArtikel(Lager lagerArtikel) {
		this.lagerArtikel = lagerArtikel;
	}
/*
	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}
*/
	public Integer getMenge() {
		return menge;
	}

	public void setMenge(Integer menge) {
		this.menge = menge;
	}

	public Date getAenderungsdatum() {
		return aenderungsdatum == null ? null : (Date) aenderungsdatum.clone();
	}

	public void setAenderungsdatum(Date aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum == null ? null : (Date) aenderungsdatum.clone();
	}

	public Long getIdbestellposition() {
		return idbestellposition;
	}

	public void setIdbestellposition(Long idbestellposition) {
		this.idbestellposition = idbestellposition;
	}
	
	public Date getErstellungsdatum() {
		return erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

	public void setErstellungsdatum(Date erstellungsdatum) {
		this.erstellungsdatum = erstellungsdatum;
	}
	
	public void setLagerArtikelUri(URI lagerArtikelUri) {
		this.lagerArtikelUri = lagerArtikelUri;
	}

	public URI getLagerArtikelUri() {
		return lagerArtikelUri;
	}
}
