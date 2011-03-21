package de.webshop.artikelverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

//TODO findKategorieByArtikel
@Entity
@Table(name = "kategorie")
@NamedQueries({
	@NamedQuery(name = Kategorie.FIND_FIRST_KATEGORIE,
				query = "SELECT k " 
						+ "FROM Kategorie k " 
						+ "WHERE k.mainKategorie IS NULL"),
	@NamedQuery(name = Kategorie.FIND_KATEGORIE_BY_ID,
				query = "SELECT k "
						+ "FROM Kategorie k "
						+ "WHERE k.idKategorie = :" + Kategorie.PARAM_ID),
	@NamedQuery(name = Kategorie.FIND_MAIN_KATEGORIE_BY_SUB_KATEGORIE_ID,
				query = "SELECT k.mainKategorie " 
						+ "FROM Kategorie k " 
						+ "WHERE k.idKategorie = :" + Kategorie.PARAM_ID_KATEGORIE),
	@NamedQuery(name = Kategorie.FIND_SUB_KATEGORIEN_BY_MAIN_KATEGORIE_ID,
				query = "SELECT k.subKategorien " 
						+ "FROM Kategorie k " 
						+ "WHERE k.idKategorie = :" + Kategorie.PARAM_ID_KATEGORIE)
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Kategorie implements Serializable {

	private static final long serialVersionUID = 141063907221215618L;
	
	public static final String PREFIX = "Kategorie.";
	public static final String FIND_FIRST_KATEGORIE = PREFIX + "findFirstKategorie";
	public static final String FIND_KATEGORIE_BY_ID = PREFIX + "findKategorieByID";
	public static final String FIND_MAIN_KATEGORIE_BY_SUB_KATEGORIE_ID = PREFIX + "findMainKategorieBySubKatgeorieID";
	public static final String FIND_SUB_KATEGORIEN_BY_MAIN_KATEGORIE_ID = PREFIX + "findSubKategorienByMainKatgeorieID";
	
	public static final String PARAM_ID_KATEGORIE = "idkatgeorie";
	public static final String PARAM_ID = "idKategorie";
	
	@Id
	@GeneratedValue(generator = "kategorie_seq")
	@SequenceGenerator(name = "kategorie_seq", sequenceName = "kategorie_idkategorie_seq", allocationSize = 1)
	@Column(name = "idkategorie")
	@XmlAttribute(name = "id", required = true)
	private Long idKategorie;
	
	@OneToMany(mappedBy = "mainKategorie")
	@XmlTransient
	private List<Kategorie> subKategorien;
	
	@Transient
	@XmlElementWrapper(name = "subkategorien")
	@XmlElement(name = "subkategorie")
	private List<URI> subKategorieUri;
	
	@ManyToOne
	@JoinColumn(name = "kategorie_idkategorie", nullable = true)
	//@XmlTransient
	private Kategorie mainKategorie;
	/*
	@Transient
	@XmlElement(name = "mainkategorie")
	private URI mainKategorieUri;
	*/
	/*@Transient
	@OneToMany
	@JoinColumn(name="kategorie_idkategorie", nullable = true)
	private List<Kategorie> subKategorien;*/
	
	@Column(name = "bezeichnung", length = 256, nullable = false)
	@NotNull(message = "{artikelverwaltung.kategorie.bezeichnung.notNull}")
	@Size(min = 1, max = 256, message = "{artikelverwaltung.kategorie.bezeichnung.length}")
	@Pattern(regexp = "[A-Z0-9\u00C4\u00D6\u00DC][a-z0-9\u00E4\u00F6\u00FC\u00DF]+", message = "{artikelverwaltung.kategorie.bezeichnung.regexp.text}") 
	private String bezeichnung;
	
	public List<URI> getSubKategorieUri() {
		return subKategorieUri;
	}

	public void setSubKategorieUri(List<URI> subKategorieUri) {
		this.subKategorieUri = subKategorieUri;
	}
/*
	public URI getMainKategorieUri() {
		return mainKategorieUri;
	}

	public void setMainKategorieUri(URI mainKategorieUri) {
		this.mainKategorieUri = mainKategorieUri;
	}*/
	
	public void setValues(Kategorie kategorie) {
		bezeichnung = kategorie.bezeichnung;
		mainKategorie = kategorie.mainKategorie;
		subKategorien = kategorie.subKategorien;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idKategorie == null) ? 0 : idKategorie.hashCode());
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
		Kategorie other = (Kategorie) obj;
		if (idKategorie == null) {
			if (other.idKategorie != null) {
				return false;
			}
		} 
		else if (!idKategorie.equals(other.idKategorie)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Kategorie [idKategorie=" + idKategorie
				+ ", bezeichnung=" + bezeichnung 
				+ ", kategorieIdKategorie=" + mainKategorie
				+ ", getClass()=" + getClass()
				+ "]";
	}
//Getter & Setter
	public Kategorie getKategorieIdKategorie() {
		return mainKategorie;
	}

	public void setKategorieIdKategorie(Kategorie kategorieIdKategorie) {
		this.mainKategorie = kategorieIdKategorie;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public Long getIdKategorie() {
		return idKategorie;
	}

	public void setIdKategorie(Long idKategorie) {
		this.idKategorie = idKategorie;
	}

	public List<Kategorie> getSubKategorien() {
		return subKategorien;
	}

	public void setSubKategorien(List<Kategorie> subKategorien) {
		this.subKategorien = subKategorien;
	}
	
}
