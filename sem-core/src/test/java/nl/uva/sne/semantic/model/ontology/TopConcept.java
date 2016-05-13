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

import nl.uva.sne.semantic.semcore.TopOntologyConcept;

public abstract class TopConcept implements TopOntologyConcept {

	private String id;
	
	public TopConcept(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

}
