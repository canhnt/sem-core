/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.semcore.dictionary;

import java.lang.reflect.Method;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import nl.uva.sne.semantic.semcore.TopOntologyConcept;

/**
 * The Class ModelDictionary.
 */
public interface ModelDictionary {

	
	/**
	 * Find concept.
	 *
	 * @param classType the class type
	 * @param conceptId the concept id
	 * @return the top ontology concept
	 * @throws DictionaryException the dictionary exception
	 */
	TopOntologyConcept createConcept(URI classType, Value conceptId) throws DictionaryException;	
	
	/**
	 * Gets the class type.
	 *
	 * @param classType the class type
	 * @return the class type
	 * @throws DictionaryException the dictionary exception
	 */
	Class<? extends TopOntologyConcept> getClassType(URI classType) throws DictionaryException;

	/**
	 * Gets the class concepts.
	 *
	 * @return the class concepts
	 */
	List<Class<? extends TopOntologyConcept>> getClassConcepts();
	
	/**
	 * Gets the model name offset.
	 *
	 * @return the model name offset
	 */
	int getModelNameOffset(String nameSpace) throws DictionaryException;

	/**
	 * Finds the namespace for the given class
	 * @param classType
	 * @return
	 */
	String getNameSpace(Class<?> classType) throws DictionaryException;

	SimpleDictionary getSimpleDictionary(String nameSpace) throws DictionaryException;

}
