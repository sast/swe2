package de.webshop.lagerverwaltung.service;

import static javax.ejb.TransactionAttributeType.MANDATORY;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;

import de.webshop.util.AbstractDao;

@Stateless
@TransactionAttribute(MANDATORY)
public class LagerverwaltungDao extends AbstractDao implements Serializable {

	private static final long serialVersionUID = 3045269799721379212L;
	
	
}
