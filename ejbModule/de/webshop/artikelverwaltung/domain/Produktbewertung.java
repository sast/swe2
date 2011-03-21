package de.webshop.artikelverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.util.DomainBean;
import de.webshop.util.IdGroup;
import de.webshop.util.XmlDateAdapter;

/**
 * Bewertung eines Artikel
 * Produktbewertungen bekommen fest einen User und einem Artikel zugewiesen
 *
 */

@Entity
@Table(name = "produktbewertung")
@NamedQueries({
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN, 
			query = "SELECT pb " 
					+ "FROM Produktbewertung pb"),
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_ARTIKEL_ID, 
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb " 
						+ "WHERE pb.artikel.idArtikel = :" + Produktbewertung.PARAM_ARTIKEL_ID),
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID,
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb " 
						+ "WHERE pb.benutzer.idBenutzer = :" + Produktbewertung.PARAM_BENUTZER_ID),
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_BENUTZER_ID_AND_ARTIKEL_ID,
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb " 
						+ "WHERE pb.benutzer.idBenutzer = :" + Produktbewertung.PARAM_BENUTZER_ID
						+ " AND pb.artikel.idArtikel = :" + Produktbewertung.PARAM_ARTIKEL_ID),						
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_BEWERTUNG,
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb " 
						+ "WHERE pb.bewertung = :" + Produktbewertung.PARAM_BEWERTUNG),
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR_EXISTS,
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb "  
						+ "WHERE pb.kommentar IS NOT NULL"),
	@NamedQuery(name = Produktbewertung.FIND_BEWERTUNGEN_BY_KOMMENTAR,
				query = "SELECT pb " 
						+ "FROM Produktbewertung pb " 
						+ "WHERE upper(pb.kommentar) LIKE upper(:" + Produktbewertung.PARAM_KOMMENTAR + ")")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Produktbewertung implements Serializable, DomainBean {

	private static final long serialVersionUID = 7895013467572655899L;
	
	private static final String PREFIX = "Produktbewertung.";
	
	public static final String FIND_BEWERTUNGEN = PREFIX + "findBewertungen";
	public static final String FIND_BEWERTUNGEN_BY_ARTIKEL_ID = PREFIX + "findBewertungenByArtikelId"; 
	public static final String FIND_BEWERTUNGEN_BY_BENUTZER_ID = PREFIX + "findBewertungenByBenutzerId";
	public static final String FIND_BEWERTUNGEN_BY_BENUTZER_ID_AND_ARTIKEL_ID = PREFIX + "findBewertungenByBenutzerIdAndArtikelId";
	public static final String FIND_BEWERTUNGEN_BY_BEWERTUNG = PREFIX + "findBewertungenByBewertung";
	public static final String FIND_BEWERTUNGEN_BY_KOMMENTAR_EXISTS = PREFIX + "findBewertungenByKommentarVorhanden";
	public static final String FIND_BEWERTUNGEN_BY_KOMMENTAR = PREFIX + "findBewertungenByKommentar";
	
	public static final String PARAM_ARTIKEL_ID = "artikelId";
	public static final String PARAM_BENUTZER_ID = "kundenId";
	public static final String PARAM_BEWERTUNG = "bewertung";
	public static final String PARAM_KOMMENTAR = "kommentar";
	
	@Id
	@GeneratedValue(generator = "produktbewertung_seq")
	@SequenceGenerator(name = "produktbewertung_seq", sequenceName = "produktbewertung_idproduktbewertung_seq", allocationSize = 1)
	@Min(value = MIN_ID, message = "{artikelverwaltung.produktbewertung.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idProduktbewertung;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_idartikel", nullable = false, insertable = true, updatable = false)
	//@Column(name = "artikel_idartikel", nullable = false)
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel")
	private URI artikelUri;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = true, updatable = false)
	//@Column(name = "benutzer_idbenutzer", nullable = false)
	@XmlTransient
	private AbstractBenutzer benutzer;
	
	@Transient
	@XmlElement(name = "benutzer")
	private URI benutzerUri;
	
	@Column(name = "kommentar", length = 512, nullable = true)
	@Size(min = 1, max = 512, message = "{artikelverwaltung.produktbewertung.kommentar.length}")
	//@Pattern(regexp = "[A-Z0-9\u00C4\u00D6\u00DCa-z0-9\u00E4\u00F6\u00FC\u00DF]+", message="{artikelverwaltung.produktbewertung.kommentar.regexp.text}") 
	private String kommentar;
	
	@Column(name = "bewertung", nullable = false)
	@NotNull(message = "{artikelverwaltung.produktbewertung.bewertung.notNull}")
	@Min(1)
	@Max(5)
	@XmlAttribute(name = "bewertung", required = true)
	private Integer bewertung;
	
	@Temporal(TIMESTAMP)
	@Column(name = "erstellungsdatum", insertable = false)
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	private Date erstellungsdatum;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((idProduktbewertung == null) ? 0 : idProduktbewertung
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
		Produktbewertung other = (Produktbewertung) obj;
		if (idProduktbewertung == null) {
			if (other.idProduktbewertung != null) {
				return false;
			}
		} 
		else if (!idProduktbewertung.equals(other.idProduktbewertung)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Produktbewertung [idProduktbewertung=" + idProduktbewertung
				+ ", artikelIdArtikel=" + artikel
				+ ", benutzerIdBenutzer=" + benutzer + ", kommentar="
				+ kommentar + ", bewertung=" + bewertung
				+ ", erstellungsdatum=" + erstellungsdatum + ", getClass()="
				+ getClass() + "]";
	}
	
	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public AbstractBenutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(AbstractBenutzer benutzer) {
		this.benutzer = benutzer;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}

	public Date getErstellungsdatum() {
		return erstellungsdatum;
	}

	public void setErstellungsdatum(Date erstellungsdatum) {
		this.erstellungsdatum = erstellungsdatum;
	}

	public Long getIdProduktbewertung() {
		return idProduktbewertung;
	}
	
	public Integer getBewertung() {
		return bewertung;
	}

	public void setBewertung(Integer bewertung) {
		this.bewertung = bewertung;
	}
	
	public URI getArtikelUri() {
		return artikelUri;
	}

	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}

	public URI getBenutzerUri() {
		return benutzerUri;
	}

	public void setBenutzerUri(URI benutzerUri) {
		this.benutzerUri = benutzerUri;
	}

	@Override
	public Long getId() {
		return idProduktbewertung;
	}
}
