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

public class EnumContainerConcept extends TopConcept {

	private People person;
	
	public EnumContainerConcept(String id) {
		super(id);
	}
	
	public EnumContainerConcept(String id, People person) {
		super(id);
		this.person = person;
	}
	
	public People getPerson() {
		return this.person;
	}
	
	public void setPerson(People person) {
		this.person = person;
	}

}
