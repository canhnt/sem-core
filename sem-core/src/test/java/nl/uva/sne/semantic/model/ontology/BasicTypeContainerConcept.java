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


public class BasicTypeContainerConcept extends TopConcept {

	private String myString;
	private Integer myInteger;
	private Double myDouble;
	private Boolean myBoolean;
	
	public BasicTypeContainerConcept(String id) {
		super(id);
	}

	public BasicTypeContainerConcept(String id, String myString, Integer myInteger, Double myDouble, Boolean myBoolean) {
		super(id);
		this.myString = myString;
		this.myInteger = myInteger;
		this.myDouble = myDouble;
		this.myBoolean = myBoolean;
	}
	
	public String getString() {
		return this.myString;
	}
	
	public void setString(String myString) {
		this.myString = myString;
	}
	
	public Integer getInteger() {
		return this.myInteger;
	}
	
	public void setInteger(Integer myInteger) {
		this.myInteger = myInteger;
	}
	
	public Double getDouble() {
		return this.myDouble;
	}
	
	public void setDouble(Double myDouble) {
		this.myDouble = myDouble;
	}
	
	public Boolean getBoolean() {
		return this.myBoolean;
	}
	
	public void setBoolean(Boolean myBoolean) {
		this.myBoolean = myBoolean;
	}
}
