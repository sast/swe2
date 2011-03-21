package de.webshop.benutzerverwaltung.domain;

import static de.webshop.benutzerverwaltung.domain.AbstractAdresse.LIEFERADRESSE;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Klasse Lieferadresse
 * Erbt von der Klasse AbstractAdresse und ergaenzt diese um einen Namen und Vornamen
 *
 */
@Entity 
@DiscriminatorValue(LIEFERADRESSE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Lieferadresse extends AbstractAdresse {

	private static final long serialVersionUID = 272988068062142637L;

	@Column(name = "name", length = 256, nullable = true)
	@NotNull(message = "{benutzerverwaltung.lieferadresse.name.notNull}")
	@Size(min = 2, max = 256, message = "{benutzerverwaltung.lieferadresse.name.length}")
	@Pattern(regexp = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+", message = "{benutzerverwaltung.lieferadresse.name.regexp.text}")
	private String name;
	
	@Column(name = "vorname", length = 256, nullable = true)
	@NotNull(message = "{benutzerverwaltung.lieferadresse.vorname.notNull}")
	@Size(min = 2, max = 256, message = "{benutzerverwaltung.lieferadresse.vorname.length}")
	@Pattern(regexp = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+", message = "{benutzerverwaltung.lieferadresse.vorname.regexp.text}") 
	private String vorname;

	/*public Lieferadresse(Long idadresse, String plz, String ort,
			String strasse, String hausnummer, String name, String vorname) {
		super(idadresse, plz, ort, strasse, hausnummer);
		this.name = name;
		this.vorname = vorname;
	}*/

	public void setValues(Lieferadresse lieferadresse) {
		if (lieferadresse != null) {
			super.setValues(lieferadresse);
			name = lieferadresse.name;
			vorname = lieferadresse.vorname;
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + ", name=" + name + ", vorname=" + vorname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
}
