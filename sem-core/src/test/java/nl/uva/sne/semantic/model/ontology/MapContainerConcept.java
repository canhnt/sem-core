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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapContainerConcept extends TopConcept {

	private Map<Integer, String> simpleMap;
	private Map<String, List<String>> simpleListMap;
	private Map<String, List<MapContainerConcept>> nestedMap;
	private Map<BasicTypeContainerConcept, String> complexKeyMap;
	
	public MapContainerConcept(String id) {
		super(id);
		simpleMap = new HashMap<Integer, String>();
		simpleListMap = new HashMap<String, List<String>>();
		nestedMap = new HashMap<String, List<MapContainerConcept>>();
		complexKeyMap = new HashMap<BasicTypeContainerConcept, String>();
	}
	
	public Map<Integer, String> getSimpleMap() {
		return this.simpleMap;
	}
	
	public void setSimpleMap(Map<Integer, String> simpleMap) {
		this.simpleMap = simpleMap;
	}
	
	public Map<String, List<String>> getSimpleListMap() {
		return this.simpleListMap;
	}
	
	public void setSimpleListMap(Map<String, List<String>> simpleListMap) {
		this.simpleListMap = simpleListMap;
	}
	
	public Map<String, List<MapContainerConcept>> getNestedMap() {
		return this.nestedMap;
	}
	
	public void setNestedMap(Map<String, List<MapContainerConcept>> nestedMap) {
		this.nestedMap = nestedMap;
	}
	
	public Map<BasicTypeContainerConcept, String> getComplexKeyMap() {
		return this.complexKeyMap;
	}
	
	public void setComplexKeyMap(Map<BasicTypeContainerConcept, String> complexKeyMap) {
		this.complexKeyMap = complexKeyMap;
	}

}
