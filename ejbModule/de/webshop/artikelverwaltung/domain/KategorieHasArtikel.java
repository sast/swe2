package de.webshop.artikelverwaltung.domain;


import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "kategorie_has_artikel")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KategorieHasArtikel implements Serializable {

	private static final long serialVersionUID = 6200434552504766187L;

	@Id
	@GeneratedValue(generator = "kategoriehasartikel_seq")
	@SequenceGenerator(name = "kategoriehasartikel_seq", sequenceName = "kategorie_has_artikel_idkategoriehasartikel_seq", allocationSize = 1)
	@XmlAttribute(name = "id", required = true)
	private Long idkategoriehasartikel;
	
	//TODO: pruefen, ob Artikel-Beziehung korrekt gesetzt!
	//@Transient
	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_idartikel", nullable = false, insertable = false, updatable = false)
	private Artikel artikel;
	
	//@Transient
	@ManyToOne(optional = false)
	@JoinColumn(name = "kategorie_idkategorie", nullable = false, insertable = true, updatable = false)
	@NotNull
	@Valid
	private Kategorie kategorie;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idkategoriehasartikel == null) ? 0 : idkategoriehasartikel
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
		KategorieHasArtikel other = (KategorieHasArtikel) obj;
		if (idkategoriehasartikel == null) {
			if (other.idkategoriehasartikel != null) {
				return false;
			}
		} 
		else if (!idkategoriehasartikel.equals(other.idkategoriehasartikel)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "KategorieHasArtikel [idkategoriehasartikel="
				+ idkategoriehasartikel + ", getClass()=" + getClass() + "]";
	}
//Getter & Setter
	
	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	
	public Kategorie getKategorie() {
		return kategorie;
	}

	public void setKategorie(Kategorie kategorie) {
		this.kategorie = kategorie;
	}

	public void setIdkategoriehasartikel(Long idkategoriehasartikel) {
		this.idkategoriehasartikel = idkategoriehasartikel;
	}

	public Long getIdkategoriehasartikel() {
		return idkategoriehasartikel;
	}
	
}
