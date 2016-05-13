/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.model.ontology;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


public class XMLContainerConcept extends TopConcept {

	private Duration myDuration;
	private XMLGregorianCalendar myCalendar;
	
	public XMLContainerConcept(String id) {
		super(id);
	}

	public XMLContainerConcept(String id, Duration myDuration, XMLGregorianCalendar myCalendar) {
		super(id);
		this.myDuration = myDuration;
		this.myCalendar = myCalendar;
	}
	
	public Duration getDuration() {
		return this.myDuration;
	}
	
	public void setDuration(Duration myDuration) {
		this.myDuration = myDuration;
	}
	
	public XMLGregorianCalendar getXMLGregorianCalendar() {
		return this.myCalendar;
	}
	
	public void setXMLGregorianCalendar(XMLGregorianCalendar myCalendar) {
		this.myCalendar = myCalendar;
	}
	
}
