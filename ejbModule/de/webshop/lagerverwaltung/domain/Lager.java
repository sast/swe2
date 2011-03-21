package de.webshop.lagerverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.EnumType.STRING;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.webshop.artikelverwaltung.domain.Artikel;
import de.webshop.util.IdGroup;

@Entity
@Table(name = "lager")
@NamedQueries({
	@NamedQuery(name = Lager.FIND_AUSVERKAUFTE_ARTIKEL,
			query = "SELECT l " 
					+ "FROM Lager l " 
					+ "WHERE l.bestandIst = '0'"),

	@NamedQuery(name = Lager.FIND_LAGER_BY_ID,
			query = "SELECT l "
					+ "FROM Lager l "
					+ "WHERE l.idlager = :" + Lager.PARAM_ID),
	@NamedQuery(name = Lager.FIND_LAGER_BY_ARTIKEL_ID,
			query = "SELECT l "
					+ "FROM Lager l "
					+ "LEFT JOIN FETCH l.artikel a " 
					+ "WHERE l.artikel.idArtikel = :" + Lager.PARAM_ARTIKEL_ID),
	@NamedQuery(name = Lager.FIND_LAGER_BY_ID_FETCH_ARTIKEL,
			query = "SELECT l "
					+ "FROM Lager l "
					+ "LEFT JOIN FETCH l.artikel a "
					+ "WHERE l.idlager = :" + Lager.PARAM_ID)

})
@XmlRootElement
@XmlAccessorType(FIELD)
public class Lager implements Serializable {

	private static final long serialVersionUID = 6893416542840461535L;

	public static final String PREFIX = "Lager.";
	public static final String FIND_AUSVERKAUFTE_ARTIKEL = PREFIX + "findAusverkaufteArtikel";
	public static final String FIND_LAGER_BY_ID = PREFIX + "findLagerById";
	public static final String FIND_LAGER_BY_ARTIKEL_ID = PREFIX + "findLagerByArtikelId";
	public static final String FIND_LAGER_BY_ID_FETCH_ARTIKEL = PREFIX + "findLagerByIdFetchArtikel";	
	public static final String PARAM_ID = "idlager";
	public static final String PARAM_ARTIKEL_ID = "artikel_idartikel";
	
	@Id
	@GeneratedValue(generator = "lager_seq")
	@SequenceGenerator(name = "lager_seq", sequenceName = "lager_idlager_seq", allocationSize = 1)
	@Column(name = "idlager")
	@Min(value = MIN_ID, message = "{lagerverwaltung.lager.id.min}", groups = IdGroup.class)
	private Long idlager;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "artikel_idartikel", nullable = false, insertable = true, updatable = true)
    @XmlTransient
    //@Valid
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikelUri")
	private URI artikelUri;
	
	@Column(name = "groesse")
	@Size(min = 1, max = 10, message = "{lagerverwaltung.lager.groesse.length}")
	@Pattern(regexp = "[A-Z0-9\u00C4\u00D6\u00DCa-z0-9\u00E4\u00F6\u00FC\u00DF]+", message = "{lagerverwaltung.lager.groesse.regexp.text}") 
	private String groesse;
	
	//TODO: Farbe und Groesse dynamisch halten, sprich modifizierbar!!
	//oder ggf. alle moeglichen Wert vorgeben, zB. per Tabelle?? oder @Ressource... 
	@XmlEnum
	public enum Farbe {
		WEISS,
		BLAU,
		ROT
	}
	
	@Column(name = "farbe")
	@Enumerated(STRING)
	@Valid
	private Farbe farbe;
	
	@Column(name = "bestand_ist")
	private Integer bestandIst;
	
	@Column(name = "bestand_min")
	private Integer bestandMin;

	public Lager() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idlager == null) ? 0 : idlager.hashCode());
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
		Lager other = (Lager) obj;
		if (idlager == null) {
			if (other.idlager != null) {
				return false;
			}
		} 
		else if (!idlager.equals(other.idlager)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Lager [idlager=" + idlager + ", artikel=" + artikel
				+ ", groesse=" + groesse + ", farbe=" + farbe
				+ ", bestand_ist=" + bestandIst + ", bestand_min="
				+ bestandMin + ", getClass()=" + getClass() + "]";
	}

	public Long getIdlager() {
		return idlager;
	}

	public void setIdlager(Long idlager) {
		this.idlager = idlager;
	}

	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public String getGroesse() {
		return groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public Farbe getFarbe() {
		return farbe;
	}

	public void setFarbe(Farbe farbe) {
		this.farbe = farbe;
	}

	public Integer getBestandIst() {
		return bestandIst;
	}

	public void setBestandIst(Integer bestandIst) {
		this.bestandIst = bestandIst;
	}

	public Integer getBestandMin() {
		return bestandMin;
	}

	public void setBestandMin(Integer bestandMin) {
		this.bestandMin = bestandMin;
	}
	
	public URI getArtikelUri() {
		return artikelUri;
	}
	
	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	public void setValues(Lager l) {
		bestandIst = l.bestandIst;
		bestandMin = l.bestandMin;
		farbe = l.farbe;
		groesse = l.groesse;	
	}
}
