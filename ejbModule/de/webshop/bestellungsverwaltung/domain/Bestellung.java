package de.webshop.bestellungsverwaltung.domain;

import static de.webshop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.webshop.benutzerverwaltung.domain.AbstractBenutzer;
import de.webshop.util.DomainBean;
import de.webshop.util.IdGroup;
import de.webshop.util.PreExistingGroup;

/**
 * Bestellungen haben eine eindeutige ID
 * Haben einen User zugewiesen und beinhalten Bestellpositionen.
 */

@Entity
@Table(name = "bestellung")
@NamedQueries({
	@NamedQuery(name = Bestellung.FIND_BESTELLUNGEN,
				query = "SELECT b " 
						+ "FROM Bestellung b"),
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_ID,
                query = "SELECT b"
			            + " FROM Bestellung b"
						+ " WHERE b.benutzer.idBenutzer = :" + Bestellung.PARAM_BENUTZER_ID),
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_BENUTZER_NAME,
				query = "SELECT b"
						+ " FROM Bestellung b"
						+ " WHERE upper(b.benutzer.nachname) LIKE upper(:" + Bestellung.PARAM_BENUTZER_NAME + ")"),
    @NamedQuery(name = Bestellung.FIND_BESTELLUNG_N_BY_STATUS,
    			query = "SELECT b"
    					+ " FROM Bestellung b"
    					+ " where b.bestellstatus = :" + Bestellung.PARAM_STATUS),
    @NamedQuery(name = Bestellung.FIND_BESTELLUNG_BY_ID,
 				query = "SELECT b"
 						+ " FROM Bestellung b"
 						+ " WHERE b.idbestellung = :" + Bestellung.PARAM_ID),
 	@NamedQuery(name = Bestellung.FIND_BESTELLUNG_N_BY_ERSTELLUNGSDATUM,
		 		query = "SELECT b"
		 				+ " FROM Bestellung b"
		 				+ " WHERE b.erstellungsdatum = :" + Bestellung.PARAM_DATUM)			
})
@XmlRootElement
@XmlAccessorType(FIELD)
public class Bestellung implements Serializable, DomainBean {

	private static final long serialVersionUID = -864725307697950718L;

	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN = PREFIX + "findBestellungen";
	public static final String FIND_BESTELLUNGEN_BY_BENUTZER_NAME = PREFIX + "findBestellungenByBenutzerNachname";
	public static final String FIND_BESTELLUNGEN_BY_BENUTZER_ID = PREFIX + "findBestellungenByBenutzer";
	public static final String FIND_BESTELLUNG_N_BY_STATUS = PREFIX + "findBestellungByNStatus";
	public static final String FIND_BESTELLUNG_BY_ID = PREFIX + "findBestellungById";
	public static final String FIND_BESTELLUNG_N_BY_ERSTELLUNGSDATUM = PREFIX + "findBestellungByNErstellungsdatum";
	public static final String FIND_BESTELLPOSITIONEN_BY_ID = PREFIX + "findBestellpositionenById";
	
	public static final String PARAM_BENUTZER_ID = "benutzer_idbenutzer";
	public static final String PARAM_ID = "idbestellung";
	public static final String PARAM_STATUS = "bestellstatus";
	public static final String PARAM_DATUM = "erstellungsdatum";
	public static final String PARAM_BENUTZER_NAME = "benutzer_nachname";
	
	@Id
	@GeneratedValue(generator = "bestellung_seq")
	@SequenceGenerator(name = "bestellung_seq", sequenceName = "bestellung_idbestellung_seq", allocationSize = 1)
	@Column(name = "idbestellung")
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	@XmlAttribute(name = "id", required = true)
	private Long idbestellung;
	
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "benutzer_idbenutzer", nullable = false, insertable = true, updatable = false)
	@NotNull(message = "{bestellverwaltung.bestellung.benutzer.notNull}", groups = PreExistingGroup.class)
	@XmlTransient
	private AbstractBenutzer benutzer;
	
	@Transient
	@XmlElement(name = "benutzer")
	private URI benutzerUri;
	
	//@Transient
	/**
	 * gerichtet Beziehung zur Bestellposition
	 */
	@OneToMany(fetch = EAGER, cascade = { PERSIST, MERGE, REMOVE })
	@JoinColumn(name = "bestellung_idbestellung", nullable = false, insertable = false, updatable = false)
//FIXME: verursacht einen Fehler: Liste der Bestellpos enthaelt zu Beginn ein Element mit dem "Wert" null (und somit auch ein Element mehr als eigentlich in der DB hinterlegt!) 
	//@OrderColumn(name = "position", nullable = false)
	@XmlElementWrapper(name = "bestellpositionen")
	@XmlElement(name = "bestellposition")
	private List<Bestellposition> bestellpositionen;
	
	@XmlEnum
	public enum Bestellstatus { WARENKORB, BESTELLT, IN_BERARBEITUNG, VERSENDET, STORNIERT };
	
	@Column(name = "bestellstatus")
	@Enumerated(STRING)
	@Valid
	private Bestellstatus bestellstatus;
	
	@XmlEnum
	public enum Zahlungsart { BAR, VORKASSE, NACHNAHME, UEBERWEISUNG, RECHNUNG };
	
	@Column(name = "zahlungsart")
	@Enumerated(STRING)
	@Valid
	private Zahlungsart zahlungsart;
	
	@Column(name = "gesamtpreis")
	@DecimalMin("0.0")
	@XmlElement(required = true)
	private double gesamtpreis;

	@Temporal(TIMESTAMP)
	@Column(name = "erstellungsdatum", nullable = false, insertable = false, updatable = false)
	private Date erstellungsdatum;
	
	@Temporal(TIMESTAMP)
	@Column(name = "aenderungsdatum", insertable = false, updatable = true)
	//@Past
	private Date aenderungsdatum;
	
	public void setValues(Bestellung b) {
	//	benutzer = b.benutzer;
		bestellpositionen = b.bestellpositionen;
		aenderungsdatum = b.aenderungsdatum;
		bestellstatus = b.bestellstatus;
		erstellungsdatum = b.erstellungsdatum;
		gesamtpreis = b.gesamtpreis;
		zahlungsart = b.zahlungsart;
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
		result = prime * result
				+ ((idbestellung == null) ? 0 : idbestellung.hashCode());
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
		Bestellung other = (Bestellung) obj;
		
		if (benutzer == null) {
			if (other.benutzer != null) {
				return false;
			}
		}
		else if (!benutzer.equals(other.benutzer)) {
			return false;
		}
		
		if (erstellungsdatum == null) {
			if (other.erstellungsdatum != null) {
				return false;
			}
		}
		else if (!erstellungsdatum.equals(other.erstellungsdatum)) {
			return false;
		}
		
		
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [idbestellung=" + idbestellung + ", kunde=" + benutzer
				+ ", bestellpositionen=" + bestellpositionen
				+ ", bestellstatus=" + bestellstatus + ", zahlungsart="
				+ zahlungsart + ", gesamtpreis=" + gesamtpreis
				+ ", erstellungsdatum=" + erstellungsdatum
				+ ", aenderungsdatum=" + aenderungsdatum + ", getClass()=" + getClass() + "]";
	}
	
	public List<Bestellposition> getBestellpositionen() {
		if (bestellpositionen == null) {
			return null;
		}
		
		return Collections.unmodifiableList(bestellpositionen);
	}

	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}
		
		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}

	public void addBestellposition(Bestellposition bestellposition) {
		if (bestellpositionen == null) {
			bestellpositionen = new ArrayList<Bestellposition>();
		}
		this.bestellpositionen.add(bestellposition);
	}
	
	public Bestellstatus getBestellstatus() {
		return bestellstatus;
	}

	public void setBestellstatus(Bestellstatus bestellstatus) {
		this.bestellstatus = bestellstatus;
	}

	public Zahlungsart getZahlungsart() {
		return zahlungsart;
	}

	public void setZahlungsart(Zahlungsart zahlungsart) {
		this.zahlungsart = zahlungsart;
	}

	public double getGesamtpreis() {
		return gesamtpreis;
	}

	public void setGesamtpreis(double gesamtpreis) {
		this.gesamtpreis = gesamtpreis;
	}

	public Date getAenderungsdatum() {
		return aenderungsdatum == null ? null : (Date) aenderungsdatum.clone();
	}

	public void setAenderungsdatum(Date aenderungsdatum) {
		this.aenderungsdatum = aenderungsdatum == null ? null : (Date) aenderungsdatum.clone();
	}

	public Long getIdbestellung() {
		return idbestellung;
	}

	public void setIdbestellung(Long idbestellung) {
		this.idbestellung = idbestellung;
	}
	
	public AbstractBenutzer getBenutzer() {
		return benutzer;
	}

	public void setBenutzer(AbstractBenutzer benutzer) {
		this.benutzer = benutzer;
	}

	public Date getErstellungsdatum() {
		return erstellungsdatum == null ? null : (Date) erstellungsdatum.clone();
	}

	public void setErstellungsdatum(Date erstellungsdatum) {
		this.erstellungsdatum = erstellungsdatum;
	}
	
	public URI getBenutzerUri() {
		return benutzerUri;
	}
	
	public void setBenutzerUri(URI benutzerUri) {
		this.benutzerUri = benutzerUri;
	}

	@Override
	public Long getId() {
		return idbestellung;
	}
}
