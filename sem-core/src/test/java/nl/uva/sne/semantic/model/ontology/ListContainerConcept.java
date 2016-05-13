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

import java.util.ArrayList;
import java.util.List;

public class ListContainerConcept extends TopConcept {

	private List<String> list;
	
	public ListContainerConcept(String id) {
		super(id);
		list = new ArrayList<String>();
	}
	
	public List<String> getList() {
		return this.list;
	}
	
	public void setList(List<String> list) {
		this.list = list;
	}

}
