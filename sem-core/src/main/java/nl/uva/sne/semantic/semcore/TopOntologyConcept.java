/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.semcore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface TopOntologyConcept.
 */
public interface TopOntologyConcept {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Predicate {
		  String function();
		  String predicateName();
	}
	
	String getId();
	
}
