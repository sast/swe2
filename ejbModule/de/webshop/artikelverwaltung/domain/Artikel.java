package de.webshop.artikelverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.webshop.lagerverwaltung.domain.Lager;
import de.webshop.util.DomainBean;
import de.webshop.util.IdGroup;

/**
 * Artikel
 * verfuegt ueber beschreibung, preis, eindeutige ID
 * gehoert kathegorien an
 * verfuegt ueber bewertungen
 */

@Entity
@Table(name = "artikel")
@NamedQueries({
    @NamedQuery(name = Artikel.FIND_ARTIKEL,
    			query = "SELECT a " 
    					+ "FROM Artikel a"),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_ID,
				query = "SELECT a " 
						+ "FROM Artikel a " 
    					+ "WHERE a.idArtikel = :" + Artikel.PARAM_ID),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG,
    			query = "SELECT a " 
    					+ "FROM Artikel a " 
    					+ "WHERE a.bezeichnung like :" + Artikel.PARAM_BEZEICHNUNG),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_KATEGORIE,
    			query = "SELECT a " 
    					+ "FROM Artikel a " 
    					+ "JOIN a.kategorien kha " 
    					+ "WHERE upper(kha.kategorie.bezeichnung) = upper(:" + Artikel.PARAM_KATEGORIE + ")"),
    @NamedQuery(name = Artikel.FIND_ANZAHL_ARTIKEL_AUF_LAGER_BY_ID,
    			query = "SELECT l.bestandIst " 
    					+ "FROM Lager l " 
    					+ "WHERE l.artikel.idArtikel = :" + Artikel.PARAM_ID + " and l.farbe = :" + Artikel.PARAM_FARBE + " and l.groesse = :" + Artikel.PARAM_GROESSE),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_KATEGORIE_ID,
    			query = "SELECT a "
    					+ "FROM Artikel a "
    					+ "JOIN a.kategorien kha "
    					+ "WHERE kha.kategorie.idKategorie = :" + Artikel.PARAM_KATEGORIE_ID + ")"),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_KATEGORIE_ID_FETCH_KATEGORIE,
    			query = "SELECT a "
    					+ "FROM Artikel a "
    					+ "JOIN FETCH a.kategorien kha "
    					+ "WHERE kha.kategorie.idKategorie = :" + Artikel.PARAM_KATEGORIE_ID + ")"),
    @NamedQuery(name = Artikel.FIND_ARTIKEL_BY_KATEGORIE_FETCH_KATEGORIE,
       			query = "SELECT a " 
      					+ "FROM Artikel a " 
       					+ "JOIN FETCH a.kategorien kha " 
       					+ "WHERE upper(kha.kategorie.bezeichnung) = upper(:" + Artikel.PARAM_KATEGORIE + ")")    
})

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Artikel implements Serializable, DomainBean {

	private static final long serialVersionUID = 8396220033143311521L;

	public static final String PREFIX = "Artikel.";
	public static final String FIND_ARTIKEL = "findArtikel";
	public static final String FIND_ARTIKEL_BY_ID = PREFIX + "findArtikelByID";
	public static final String FIND_ARTIKEL_BY_BEZEICHNUNG = PREFIX + "findArtikelByBezeichnung";
	public static final String FIND_ARTIKEL_BY_KATEGORIE = PREFIX + "findArtikelByKategorie";
	public static final String FIND_ANZAHL_ARTIKEL_AUF_LAGER_BY_ID = PREFIX + "findAnzahlArtikelAufLager";
	public static final String FIND_ARTIKEL_BY_KATEGORIE_ID = PREFIX + "findArtikelByKategorieID";
	public static final String FIND_ARTIKEL_BY_KATEGORIE_ID_FETCH_KATEGORIE = PREFIX + "findArtikelByKategorieIDFetchKategorie";
	public static final String FIND_ARTIKEL_BY_KATEGORIE_FETCH_KATEGORIE = PREFIX + "findArtikelByKategorieFetchKategorie";
	
	public static final String PARAM_ID = "idArtikel";
	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	public static final String PARAM_KATEGORIE = "kategorie";
	public static final String PARAM_FARBE = "farbe";
	public static final String PARAM_GROESSE = "groesse";
	public static final String PARAM_KATEGORIE_ID = "idKategorie";
	
	@Id
	@GeneratedValue(generator = "artikel_seq")
	@SequenceGenerator(name = "artikel_seq", sequenceName = "artikel_idartikel_seq", allocationSize = 1)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idArtikel;
	
	@OneToMany(mappedBy = "artikel", cascade = {PERSIST })
	@XmlTransient
	private List<Lager> artikelVarianten;
	
	@Transient
	@XmlElementWrapper(name = "artikelvarianten")
	@XmlElement(name = "artikelvariante")
	private List<URI> artikelVariantenUri;
	
	@OneToMany(cascade = {PERSIST })
	@JoinColumn(name = "artikel_idartikel", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private List<KategorieHasArtikel> kategorien;
	
	@Transient
	@XmlElementWrapper(name = "kategorien")
	@XmlElement(name = "kategorie")
	private List<URI> kategorieUri;
	
	//TODO: OrderColumn auch bei Datum moeglich??
	@OneToMany(mappedBy = "artikel")
	//@JoinColumn(name="artikel_idartikel")
	//@OrderColumn(name="erstellungsdatum")
	@XmlTransient
	private List<Produktbewertung> produktBewertungen;
	
	@Transient
	@XmlElementWrapper(name = "produktbewertungen")
	@XmlElement(name = "produktbewertung")
	private List<URI> produktBewertungUri;
	
	@Column(name = "bezeichnung", length = 256, nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.bezeichnung.notNull}")
	@Size(min = 1, max = 256, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	//TODO: regexp ueberdenken!
	//@Pattern(regexp = "[A-Za-z0-9\u00E4\u00F6\u00FC\u00DF]+", message = "{artikelverwaltung.artikel.bezeichnung.regexp.text}") 
	private String bezeichnung;
	
	@Column(name = "preis", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.preis.notNull}")
	@DecimalMin("0.0")
	private Double preis;
	
	@Column(name = "erstellungsdatum", nullable = false, insertable = false, updatable = false)
	@Past
	@XmlTransient
	private Date erstellungsdatum;
	
	@Column(name = "aenderungsdatum", nullable = true)
	@XmlTransient
	private Date aenderungsdatum;
	
	@Column(name = "imsortiment", nullable = false)
	private boolean imsortiment;
	
	@SuppressWarnings("unused")
	@PreUpdate
	private void aenderungsdatum() {
		aenderungsdatum = new Date();
	}
	

	public List<URI> getArtikelVariantenUri() {
		return artikelVariantenUri;
	}

	public void setArtikelVariantenUri(List<URI> artikelVariantenUri) {
		this.artikelVariantenUri = artikelVariantenUri;
	}

	public List<URI> getProduktBewertungUri() {
		return produktBewertungUri;
	}

	public void setProduktBewertungUri(List<URI> produktBewertungUri) {
		this.produktBewertungUri = produktBewertungUri;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idArtikel == null) ? 0 : idArtikel.hashCode());
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
		Artikel other = (Artikel) obj;
		if (idArtikel == null) {
			if (other.idArtikel != null) {
				return false;
			}
		} 
		else if (!idArtikel.equals(other.idArtikel)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [idArtikel=" + idArtikel + ", bezeichnung="
				+ bezeichnung + ", preis=" + preis + ", erstellungsdatum="
				+ erstellungsdatum + ", aenderungsdatum=" + aenderungsdatum
				+ ", imsortiment=" + imsortiment + ", getClass()=" + getClass() + "]";
	}
	
//Getter & Setter
	@Override
	public Long getId() {
		return idArtikel;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public Double getPreis() {
		return preis;
	}

	public void setPreis(Double preis) {
		this.preis = preis;
	}

	public Date getErstellungsdatum() {
		return erstellungsdatum;
	}

	public void setErstellungsdatum(Date erstellungsdatum) {
		this.erstellungsdatum = erstellungsdatum;
	}

	public Date getAenderungsdatum() {
		return aenderungsdatum;
	}

	public void setAenderungsdatum(Date aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum;
	}

	public Boolean getImsortiment() {
		return imsortiment;
	}

	public void setImsortiment(Boolean imsortiment) {
		this.imsortiment = imsortiment;
	}

	public Long getIdArtikel() {
		return idArtikel;
	}
	public void setIdArtikel(Long idArtikel) {
		this.idArtikel = idArtikel;
	}

	public List<Lager> getArtikelVarianten() {
		return artikelVarianten;
	}

	public void setArtikelVarianten(List<Lager> artikelVarianten) {
		this.artikelVarianten = artikelVarianten;
	}

	public List<Produktbewertung> getProduktBewertungen() {
		return produktBewertungen;
	}

	public void setProduktBewertungen(List<Produktbewertung> produktBewertungen) {
		this.produktBewertungen = produktBewertungen;
	}
	
	public List<KategorieHasArtikel> getKategorien() {
		return kategorien;
	}

	public void setKategorien(List<KategorieHasArtikel> kategorien) {
		this.kategorien = kategorien;
	}
	
	public List<URI> getKategorieUri() {
		return kategorieUri;
	}

	public void setKategorieUri(List<URI> kategorieUri) {
		this.kategorieUri = kategorieUri;
	}
// ADDS
	public void addKategorieHasArtikel(KategorieHasArtikel kha) {
		if (this.kategorien == null) {
			kategorien = new ArrayList<KategorieHasArtikel>();
		}
		kategorien.add(kha);
	}
	
	public void addProduktbewertung(Produktbewertung produktBewertung) {
		if (produktBewertungen == null) {
			produktBewertungen = new ArrayList<Produktbewertung>();
		}
		produktBewertungen.add(produktBewertung);
	}
	
	public void addLagerartikel(Lager lagerArtikel) {
		if (artikelVarianten == null) {
			artikelVarianten = new ArrayList<Lager>();
		}
		artikelVarianten.add(lagerArtikel);
	}

	public void setValues(Artikel artikel) {
		bezeichnung = artikel.bezeichnung;
		artikelVarianten = artikel.artikelVarianten;
		produktBewertungen = artikel.produktBewertungen;
		kategorien = artikel.kategorien;
		preis = artikel.preis;
		erstellungsdatum = artikel.erstellungsdatum;
		aenderungsdatum = artikel.aenderungsdatum;
		imsortiment = artikel.imsortiment;
		
	}

	
}
