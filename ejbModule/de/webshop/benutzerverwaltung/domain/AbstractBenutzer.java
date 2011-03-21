package de.webshop.benutzerverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webshop.artikelverwaltung.domain.Produktbewertung;
import de.webshop.bestellungsverwaltung.domain.Bestellung;
import de.webshop.util.Constants;
import de.webshop.util.DomainBean;
import de.webshop.util.IdGroup;
import de.webshop.util.XmlDateAdapter;

/** Klasse AbstractBenutzer<br/> 
 * 	stellt einen User in der Anwendung dar. <br/>
 * 	Verfuegt ueber eine eindeutig identifizierende ID.<br/>
 *  Jeder Benutzer hat sowohl beschreibende Attribute (Name, Nachmane, Email)
 *  als auch eine Rechnungs/Lieferadresse und Listen von Bestellungen und Produktbewertungen  
**/
@Entity
@Table(name = "benutzer")
@NamedQueries({
    @NamedQuery(name = AbstractBenutzer.FIND_BENUTZER,
    			query = "SELECT b " 
    					+ "FROM AbstractBenutzer b"),
    @NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_BY_ID,
				query = "SELECT b " 
    					+ "FROM AbstractBenutzer b " 
    					+ "WHERE idBenutzer = :" + AbstractBenutzer.PARAM_ID),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_N_BY_ROLLE,
			    query = "SELECT DISTINCT b " 
			    		+ "FROM AbstractBenutzer b " 
			    		+ "JOIN b.rollehasbenutzer r " 
			    		+ "WHERE r.rolle.rolleTyp = :" + AbstractBenutzer.PARAM_ROLLE),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME,
		    	query = "SELECT b " 
		    			+ "FROM AbstractBenutzer b " 
		    			+ "WHERE upper(b.nachname) LIKE upper(:" + AbstractBenutzer.PARAM_NACHNAME + ")"),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_BY_EMAIL,
	    		query = "SELECT b " 
	    				+ "FROM AbstractBenutzer b " 
	    				+ "WHERE upper(b.email) LIKE upper(:" + AbstractBenutzer.PARAM_EMAIL + ")"),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_N_BY_PLZ,
	    		query = "SELECT b " 
	    				+ "FROM AbstractBenutzer b " 
	    				+ "WHERE b.rechnungsadresse.plz = :" + AbstractBenutzer.PARAM_PLZ),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_ROLLEN,
	    		query = "SELECT DISTINCT b " 
	    				+ "FROM AbstractBenutzer b " 
	    				+ "LEFT JOIN FETCH b.rollehasbenutzer rhb " 
	    				+ "WHERE b.idBenutzer = :" + AbstractBenutzer.PARAM_ID),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_BESTELLUNGEN,
	    		query = "SELECT DISTINCT b "
	    				+ "FROM AbstractBenutzer b " 
	    				+ "LEFT JOIN FETCH b.bestellungen best " 
	    				+ "WHERE b.idBenutzer = :" + AbstractBenutzer.PARAM_ID),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_N_BY_NACHNAME_FETCH_BESTELLUNGEN,
				query = "SELECT DISTINCT b " 
						+ "FROM AbstractBenutzer b " 
						+ "LEFT JOIN FETCH b.bestellungen best " 
						+ "WHERE upper(b.nachname) LIKE upper(:" + AbstractBenutzer.PARAM_NACHNAME + ")"),
	@NamedQuery(name = AbstractBenutzer.FIND_BENUTZER_BY_ID_FETCH_PRODUKTBEWERTUNGEN,
				query = "SELECT DISTINCT b " 
						+ "FROM AbstractBenutzer b " 
						+ "LEFT JOIN FETCH b.produktbewertungen p " 
						+ "WHERE b.idBenutzer = :" + AbstractBenutzer.PARAM_ID)
})
@XmlRootElement(name = "benutzer") //TODO: solange die Klasse noch AbstractBenutzer heisst!
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractBenutzer implements java.io.Serializable, DomainBean {

	public interface PasswordGroup { }
	
	private static final long serialVersionUID = 3414980961784624339L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBenutzer.class);
	
//	public static final String CLASSNAME = AbstractBenutzer.class.getSimpleName();
	
	public static final String PREFIX = "Benuter.";
	
	public static final String FIND_BENUTZER = "findBenutzer";
	public static final String FIND_BENUTZER_BY_ID = PREFIX + "findBenutzerByID";
	public static final String FIND_BENUTZER_N_BY_NACHNAME = PREFIX + "findBenutzerByNachname";
	public static final String FIND_BENUTZER_BY_EMAIL = PREFIX + "findBenutzerByEmail";
	public static final String FIND_BENUTZER_N_BY_PLZ = PREFIX + "findBenutzerByPLZ";
	//TODO eventl. NamedQuery einbauen?
	public static final String FIND_BENUTZER_N_BY_PLZ_RANGE = PREFIX + "findBenutzerByPLZRange";
	public static final String FIND_BENUTZER_N_BY_ROLLE = PREFIX + "findBenutzerByRolle";
	
	public static final String FIND_BENUTZER_N_BY_NACHNAME_FETCH_BESTELLUNGEN = PREFIX + "findBenutzerByNachnameFetchBestellungen";
	public static final String FIND_BENUTZER_BY_ID_FETCH_ROLLEN = PREFIX + "findBenutzerByIDFetchRollen";
	public static final String FIND_BENUTZER_BY_ID_FETCH_BESTELLUNGEN = PREFIX + "findBenutzerByIDFetchBestellungen";
	public static final String FIND_BENUTZER_BY_ID_FETCH_PRODUKTBEWERTUNGEN = PREFIX + "findBenutzerByIDFetchProduktbewertungen";
	public static final String FIND_BENUTZER_BY_ID_FETCH_ALL = PREFIX + "findBenutzerByIDFetchAll";
	
	//TODO: wert der konstanten besondere bedeutung oder koennte auch x-beliebig sein???
	public static final String PARAM_ID = "idbenutzer";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_PLZ = "plz";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_ROLLE = "rolle";
	
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int VORNAME_LENGTH_MAX = 256;
	public static final int NACHNAME_LENGTH_MAX = 256;
 
	/**
	 *  Jeder Benutzer hat einen eindeutige ID
	 */
	@Id
	@GeneratedValue(generator = "benutzer_seq")
	@SequenceGenerator(name = "benutzer_seq", sequenceName = "benutzer_idbenutzer_seq", allocationSize = 1)
	@Column(name = "idbenutzer", unique = true, nullable = false)
	@Min(value = MIN_ID, message = "{benutzerverwaltung.benutzer.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idBenutzer = Constants.KEINE_ID;
	
	/**
	 * Hinweis:
	 * die sezifische Klasse ist zu verwenden, nicht AbstractAdresse, da sonst folgende Exception komme:
	 * org.hibernate.HibernateException: More than one row with the given identifier was found: 1, for class: de.webshop.benutzerverwaltung.AbstractAdresse
	 */
	
	@OneToOne(mappedBy = "benutzer", cascade = { PERSIST, MERGE, REMOVE })
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	@Valid
	@NotNull
	private Rechnungsadresse rechnungsadresse;
	
	@OneToOne(mappedBy = "benutzer", cascade = { PERSIST, MERGE, REMOVE })
	//@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	@Valid
	private Lieferadresse lieferadresse;

	/**
	 * Liste von Bestellungen des Users
	 */
	@OneToMany(mappedBy = "benutzer", cascade = { REMOVE })
	//--> wenn @JoinColumn anstatt "mappedBy" dann muss auf der Gegenseite (Bestellung) das Gleiche stehen: @JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	//    dadurch wird innerhalb createBestellung() beim Benutzer das Hinzufuegen (benutzer.addBestellung(bestellung);) der Bestellung ueberfluessig  
	//@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	@Transient
	@XmlElementWrapper(name = "bestellungen")
	@XmlElement(name = "bestellung")
	private List<URI> bestellungenURIs;
	
	@OneToMany(cascade = { REMOVE })
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private Set<RolleHasBenutzer> rollehasbenutzer;
	
	/**
	 *  Benutzer verfuegen ueber Rollen zur Verwaltung der Zugriffsrechte
	 */
	@Transient
	@XmlElementWrapper(name = "rollehasbenutzers")
	@XmlElement(name = "rollehasbenutzer")
	private Set<URI> rollehasbenutzerURIs;
	
	/**
	 * Beinhaltet alle Produktbewertungen die ein User abgegeben hat
	 */
	@OneToMany(mappedBy = "benutzer", cascade = { REMOVE })
	//@JoinColumn(name="benutzer_idbenutzer", nullable=false)
	//@OrderColumn(name="erstellungsdatum")
	@XmlTransient
	private List<Produktbewertung> produktbewertungen;
	
	@Transient
	@XmlElementWrapper(name = "produktbewertungen")
	@XmlElement(name = "produktbewertung")
	private List<URI> produktbewertungenURIs;
	
	@Column(name = "email", length = EMAIL_LENGTH_MAX, unique = true, nullable = false)
	@NotNull(message = "{benutzerverwaltung.benutzer.email.notNull}")
	@Email
	@XmlElement(required = true)
	private String email;
	
	@Column(name  = "vorname", length = VORNAME_LENGTH_MAX, nullable = false)
	@NotNull(message = "{benutzerverwaltung.benutzer.vorname.notNull}")
	@Size(min = 2, max = 256, message = "{benutzerverwaltung.benutzer.vorname.length}")
	@Pattern(regexp = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF\u0020\u002E]+", message = "{benutzerverwaltung.benutzer.vorname.regexp.text}")
	@XmlElement(required = true)
	private String vorname;
	
	@Column(name = "nachname", length = NACHNAME_LENGTH_MAX, nullable = false)
	@NotNull(message = "{benutzerverwaltung.benutzer.nachname.notNull}")
	@Size(min = 2, max = 256, message = "{benutzerverwaltung.benutzer.nachname.length}")
	@Pattern(regexp = "[A-Z\u00E4\u00F6\u00FC\u00DF][A-Za-z\u00E4\u00F6\u00FC\u00DF]+", message = "{benutzerverwaltung.benutzer.nachname.regexp.text}")
	@XmlElement(required = true)
	private String nachname;

	/**
	 * 12 Stellige Nummer
	 */
	@Column(name = "kontonummer")
	@Digits(integer = 12, fraction = 0, message = "{benutzerverwaltung.benutzer.kontonummer.length}") 
	//TODO Im moment wird in den ValidationMessages "hart" die 12 gesetzt. Kann man auf die Laenge hier auch wie auf "max" zugreifen?
	private String kontonummer;
	
	/**
	 * 12 Stellige Nummer
	 */
	@Column(name = "bankleitzahl")
	@Digits(integer = 12, fraction = 0, message = "{benutzerverwaltung.benutzer.bankleitzahl.length}") // Siehe Kommentar bei Kontonummer
	private String bankleitzahl;
	
	@Column(name = "passwort", nullable = false)
	@NotNull(message = "{benutzerverwaltung.benutzer.passwort.notNull}")
	private String passwort;
	
	@Transient
	private String passwortWdh;
	
	@Column(name = "newsletter")
	private boolean newsletter = false;
	
	@Column(name = "erstellungsdatum", nullable = false, insertable = false)
	@Temporal(TIMESTAMP)
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	private Date erstellungsdatum;
	
	@Column(name = "aenderungsdatum", insertable = false, updatable = true)
	@Temporal(TIMESTAMP)
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	private Date aenderungsdatum;

	
//### Lifecycle-Methods ###
	@PostLoad
	protected void postLoad() {
		LOGGER.debug("BEGINN Benutzer.postLoad: " +  this);
		passwortWdh = passwort;
		LOGGER.debug("ENDE Benutzer.postLoad: " +  this);
	}
	
	@PreUpdate
	protected void preUpdate() {
		LOGGER.debug("BEGINN Benutzer.preUpdate: " +  this);
		this.aenderungsdatum = new Date();
		LOGGER.debug("ENDE Benutzer.preUpdate: " +  this);
	}
//### Lifecycle-Methods - END ###
	
	
//### Collection Access ###
	/**
	 * Gibt alle Bestellungen eines User zurueck
	 * @return bestellungen
	 */
	public List<Bestellung> getBestellungen() {
		if (bestellungen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellungen);
	}
	
	/**
	 * Erzeugt eine neue Liste Bestellungen
	 * @param bestellungen
	 */
	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}
		
		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}
	
	/**
	 * Fuegt zu den Bestellungen eines Users eine Weitere hinzu
	 * @param bestellungen
	 */
	public void addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<Bestellung>();
		}
		bestellungen.add(bestellung);
	}
	
	/**
	 * Gibt die Produktbewertungen aus
	 * @return Produktbewertung
	 */
	public List<Produktbewertung> getProduktbewertungen() {
		if (produktbewertungen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(produktbewertungen);
	}

	public void setProduktbewertungen(List<Produktbewertung> produktbewertungen) {
		if (this.produktbewertungen == null) {
			this.produktbewertungen = produktbewertungen;
			return;
		}
		
		this.produktbewertungen.clear();
		if (produktbewertungen != null) {
			this.produktbewertungen.addAll(produktbewertungen);
		}
	}
	
	public void addProduktbewertung(Produktbewertung produktBewertung) {
		if (produktbewertungen == null) {
			produktbewertungen = new ArrayList<Produktbewertung>();
		}
		produktbewertungen.add(produktBewertung);
	}
	
	
	/**
	 * Gibt die Rollen eines User zurueck
	 * @return rollehasbenutzer
	 */
	public Set<RolleHasBenutzer> getRollehasbenutzer() {
		if (rollehasbenutzer == null) {
			return null;
		}
		
		return Collections.unmodifiableSet(rollehasbenutzer);
	}

	public void setRollehasbenutzer(Set<RolleHasBenutzer> rollehasbenutzer) {
		if (this.rollehasbenutzer == null) {
			this.rollehasbenutzer = rollehasbenutzer;
			return;
		}
		
		this.rollehasbenutzer.clear();
		if (rollehasbenutzer != null) {
			this.rollehasbenutzer.addAll(rollehasbenutzer);
		}
	}
	
	/**
	 * F&uuml;gt eine Rolle hinzu
	 * @param rolleHasBenutzer
	 */
	public void addRollehasbenutzer(RolleHasBenutzer rolleHasBenutzer) {
		if (rollehasbenutzer == null) {
			rollehasbenutzer = new HashSet<RolleHasBenutzer>();
		}
		rollehasbenutzer.add(rolleHasBenutzer);
	}
	
	public RolleHasBenutzer removeRollehasbenutzer(RolleHasBenutzer rolleHasBenutzer) {
		if (rollehasbenutzer == null) {
			return null;
		}
		
		rollehasbenutzer.remove(rolleHasBenutzer);
		return rolleHasBenutzer;
	}
//### Collection Access - END ###

//### URI - Access ###
	public List<URI> getBestellungenURIs() {
		return bestellungenURIs;
	}

	public void setBestellungenURIs(List<URI> bestellungenURIs) {
		this.bestellungenURIs = bestellungenURIs;
	}

	public Set<URI> getRollehasbenutzerURIs() {
		return rollehasbenutzerURIs;
	}

	public void setRollehasbenutzerURIs(Set<URI> rollehasbenutzerURIs) {
		this.rollehasbenutzerURIs = rollehasbenutzerURIs;
	}

	public List<URI> getProduktbewertungenURIs() {
		return produktbewertungenURIs;
	}

	public void setProduktbewertungenURIs(List<URI> produktbewertungenURIs) {
		this.produktbewertungenURIs = produktbewertungenURIs;
	}
//### URI - Access - END ###

//### Validation-Methods ###
	@AssertTrue(groups = PasswordGroup.class, message = "{benutzerverwaltung.benutzer.passwort.notEqual}")
	public boolean isPasswordEqual() {
		if (passwort == null) {
			return passwortWdh == null;
		}
		return passwort.equals(passwortWdh);
	}
//### Validation-Methods - END ###
	
	/**
	 * KEIN setValue fuer Produktbewertungen, Bestellungen und RolleHasBenutzer
	 */
	public void setValues(AbstractBenutzer benutzer) {
		nachname = benutzer.getNachname();
		vorname = benutzer.getVorname();
		email = benutzer.getEmail();
		kontonummer = benutzer.getKontonummer();
		bankleitzahl = benutzer.getBankleitzahl();
		newsletter = benutzer.isNewsletter();
		passwort = benutzer.getPasswort();
		passwortWdh = benutzer.getPasswortWdh();
		
		if (lieferadresse == null) {
			//setze Lieferadresse, sofern an dem Benutzer noch keine gesetzt ist
			lieferadresse = benutzer.getLieferadresse();	
		}
		else {
			//andernfalls aendere nur die Werte!
			lieferadresse.setValues(benutzer.getLieferadresse());
		}
		
		rechnungsadresse.setValues(benutzer.getRechnungsadresse());
		
		/* da kein MERGE bringt auch das neusetzen nix!!
		produktbewertungen;
		bestellungen;
		rollehasbenutzer;*/
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		AbstractBenutzer other = (AbstractBenutzer) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} 
		else if (!email.equals(other.email)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "AbstractBenutzer [idbenutzer=" + idBenutzer + ", email="
				+ email + ", vorname=" + vorname + ", nachname=" + nachname
				+ ", kontonummer=" + kontonummer + ", bankleitzahl="
				+ bankleitzahl + ", passwort=" + passwort + ", newsletter="
				+ newsletter + ", erstellungsdatum=" + erstellungsdatum
				+ ", aenderungsdatum=" + aenderungsdatum + ", rechnungsadresse=" + rechnungsadresse
				+ ", lieferadresse=" + lieferadresse + ", getClass()=" + getClass() + "]";
	}

	public Long getIdBenutzer() {
		return idBenutzer;
	}

	/**
	 * 
	 * @param idBenutzer bei CREATE nicht setzen!
	 */
	public void setIdBenutzer(Long idBenutzer) {
		this.idBenutzer = idBenutzer;
	}

	public Rechnungsadresse getRechnungsadresse() {
		return rechnungsadresse;
	}

	/**
	 * 
	 * @param rechnungsadresse bei CREATE: <b>required</b>
	 */
	public void setRechnungsadresse(Rechnungsadresse rechnungsadresse) {
		this.rechnungsadresse = rechnungsadresse;
	}

	public Lieferadresse getLieferadresse() {
		return lieferadresse;
	}

	public void setLieferadresse(Lieferadresse lieferadresse) {
		this.lieferadresse = lieferadresse;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email bei CREATE: <b>required</b>
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public String getVorname() {
		return vorname;
	}

	/**
	 * 
	 * @param vorname bei CREATE: <b>required</b>
	 */
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	/**
	 * 
	 * @param nachname bei CREATE: <b>required</b>
	 */
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(String kontonummer) {
		this.kontonummer = kontonummer;
	}

	public String getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(String bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public String getPasswort() {
		return passwort;
	}

	/**
	 * 
	 * @param passwort bei CREATE: <b>required</b>
	 */
	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}

	public String getPasswortWdh() {
		return passwortWdh;
	}

	/**
	 * 
	 * @param passwortWdh bei CREATE: <b>required</b>
	 */
	public void setPasswortWdh(String passwortWdh) {
		this.passwortWdh = passwortWdh;
	}

	public boolean isNewsletter() {
		return newsletter;
	}

	public void setNewsletter(boolean newsletter) {
		this.newsletter = newsletter;
	}

	public Date getErstellungsdatum() {
		return erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

	public void setErstellungsdatum(Date erstellungsdatum) {
		this.erstellungsdatum = erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

	public Date getAenderungsdatum() {
		return aenderungsdatum == null ? null : (Date) aenderungsdatum;
	}

	public void setAenderungsdatum(Date aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum == null ? null : (Date) aenderungsdatum.clone();
	}

	@Override
	public Long getId() {
		return idBenutzer;
	}
}
