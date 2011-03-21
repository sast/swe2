package de.webshop.benutzerverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import de.webshop.util.IdGroup;

/**
 * Klasse AbstractAdresse Adresse 
 * Beinhaltet Adressdaten eines User
 */
@Entity
@Table(name = "adresse")
@NamedQueries({
    @NamedQuery(name = AbstractAdresse.FIND_ADRESSE,
    			query = "SELECT a " 
    					+ "FROM AbstractAdresse a"),
    @NamedQuery(name = AbstractAdresse.FIND_ADRESSE_BY_ID,
				query = "SELECT a " 
    					+ "FROM AbstractAdresse a " 
    					+ "WHERE a.idadresse = :" + AbstractAdresse.PARAM_ID),
    @NamedQuery(name = AbstractAdresse.FIND_ADRESSE_BY_USER_ID,
    			query = "SELECT a " 
    					+ "FROM AbstractAdresse a " 
    					+ "WHERE a.benutzer.idBenutzer = :" + AbstractAdresse.PARAM_USER_ID)
})

@Inheritance
@DiscriminatorColumn(name = "art", length = 1)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Rechnungsadresse.class, Lieferadresse.class })
public abstract class AbstractAdresse implements Serializable {

	private static final long serialVersionUID = -3852718359561394373L;

	// Const fuer named querry
	public static final String PREFIX = "Adresse.";
	public static final String FIND_ADRESSE = "findAdresse";
	public static final String FIND_ADRESSE_BY_ID = PREFIX + "findAdresseByID";
	public static final String FIND_ADRESSE_BY_USER_ID = PREFIX + "findAdresseByUserID";
	
	public static final String PARAM_ID = "idadresse";
	public static final String PARAM_USER_ID = "benutzer_idbenutzer";
	
	
	public static final String RECHNUNGSADRESSE = "R";
	public static final String LIEFERADRESSE = "L";

	@Id
	@GeneratedValue(generator = "adresse_seq")
	@SequenceGenerator(name = "adresse_seq", sequenceName = "adresse_idadresse_seq", allocationSize = 1)
	@Column(name = "idadresse", unique = true, nullable = false)
	@Min(value = MIN_ID, message = "{benutzerverwaltung.adresse.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idadresse;
	
	@Column(name = "postleitzahl", length = 5, nullable = false)
	@NotNull(message = "{benutzerverwaltung.adresse.plz.notNull}")
	@Digits(integer = 5, fraction = 0, message = "{benutzerverwaltung.adresse.plz.length}")
	@XmlElement(required = true)
	private String plz;
	//TODO bean validation pruefen, ob message-Verweis bei Digits-Validation OK ? 
	
	@Column(name = "ort", length = 128, nullable = false) 
	@NotNull(message = "{benutzerverwaltung.adresse.ort.notNull}")
	@Size(min = 2, max = 128, message = "{benutzerverwaltung.adresse.ort.length}")
	@Pattern(regexp = "[A-Z\u00C4\u00D6\u00DC][A-Za-z\u00E4\u00F6\u00FC\u00DF]+", message = "{benutzerverwaltung.adresse.ort.regexp.text}")
	@XmlElement(required = true)
	private String ort;

	/*
	 * \u0020 --> <SPACE>
	 * \u002E --> .
	 * \u002D --> -
	 */
	@Column(name = "strasse", length = 128, nullable = false)  
	@NotNull(message = "{benutzerverwaltung.adresse.strasse.notNull}")
	@Size(min = 2, max = 128, message = "{benutzerverwaltung.adresse.strasse.length}")
	@Pattern(regexp = "[A-Z\u00C4\u00D6\u00DC][A-Za-z\u00E4\u00F6\u00FC\u00DF\u0020\u002E\u002D]+", message = "{benutzerverwaltung.adresse.strasse.regexp.text}")
	@XmlElement(required = true)
	private String strasse;
	
	@Column(name = "hausnummer", length = 5, nullable = false)
	@NotNull(message = "{benutzerverwaltung.adresse.hausnummer.notNull}")
	@Size(min = 1, max = 5, message = "{benutzerverwaltung.adresse.hausnummer.length}")
	@Pattern(regexp = "[0-9][a-z0-9]*", message = "{benutzerverwaltung.adresse.hausnummer.regexp.text}")
	@XmlElement(required = true)
	private String hausnummer;

	@OneToOne
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false)
	@XmlTransient
	private AbstractBenutzer benutzer;
	
	@Transient
	@XmlElement(name = "benutzer", required = true)
	private URI benutzerURI;
	
	/*public AbstractAdresse(Long idadresse, String plz, String ort,
			String strasse, String hausnummer) {
		super();
		this.idadresse = idadresse;
		this.plz = plz;
		this.ort = ort;
		this.strasse = strasse;
		this.hausnummer = hausnummer;
	}*/
	
//### URI - Access ###	
	public URI getBenutzerURI() {
		return benutzerURI;
	}

	public void setBenutzerURI(URI benutzerURI) {
		this.benutzerURI = benutzerURI;
	}
//### URI - Access - END ###

	public void setValues(AbstractAdresse adresse) {
		if (adresse != null) {
			hausnummer = adresse.hausnummer;
			ort = adresse.ort;
			plz = adresse.plz;
			strasse = adresse.strasse;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idadresse == null) ? 0 : idadresse.hashCode());
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
		AbstractAdresse other = (AbstractAdresse) obj;
		if (idadresse == null) {
			if (other.idadresse != null) {
				return false;
			}
		} 
		else if (!idadresse.equals(other.idadresse)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [idadresse=" + idadresse + ", plz=" + plz
				+ ", ort=" + ort + ", strasse=" + strasse + ", hausnummer="
				+ hausnummer + ", getClass()=" + getClass() + "]";
	}
	
	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public Long getIdadresse() {
		return idadresse;
	}

	public void setIdadresse(Long idadresse) {
		this.idadresse = idadresse;
	}

	public AbstractBenutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(AbstractBenutzer benutzer) {
		this.benutzer = benutzer;
	}

}
