package de.webshop.benutzerverwaltung.domain;

import static de.webshop.benutzerverwaltung.domain.AbstractAdresse.RECHNUNGSADRESSE;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Klasse Rechnungsadresse 
 * Erbt von AbstractAdresse	
 */

@Entity 
@DiscriminatorValue(RECHNUNGSADRESSE)
@XmlRootElement
public class Rechnungsadresse extends AbstractAdresse {

		private static final long serialVersionUID = -8858943442973543278L;
}
