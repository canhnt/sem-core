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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.sne.semantic.semcore.TopOntologyConcept;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * The Class ModelDictionary.
 */
public abstract class SimpleDictionary implements ModelDictionary {

	protected final Map<URI, Class<? extends TopOntologyConcept>> classDictionary = new HashMap<URI, Class<? extends TopOntologyConcept>>();
	
	protected final Map<URI, Class<? extends Enum<?>>> enumDictionary = new HashMap<URI, Class<? extends Enum<?>>>();
	
	protected final Map<Method, URI> predicateDictionary = new HashMap<Method, URI>();
	
	protected String namespace;
	
	
	public TopOntologyConcept createConcept(URI classType, Value conceptId) throws DictionaryException	{
		Class<? extends TopOntologyConcept> ontologyClass = getClassType(classType);

		try {
			Constructor<? extends TopOntologyConcept> constructor = ontologyClass.getConstructor(String.class);
			if (constructor != null) {
				return constructor.newInstance(conceptId.stringValue());
			} else {
				throw new DictionaryException("No valid constructor could be found for "+ontologyClass.getName());
			}
		} catch (Exception e) {
			throw new DictionaryException(e.getMessage());
		}
		
	}
	
	@Override
	public Class<? extends TopOntologyConcept> getClassType(URI classType) throws DictionaryException {
		Class<? extends TopOntologyConcept> ontologyClass = classDictionary.get(classType);
		if (ontologyClass==null) {
			throw new DictionaryException("Dictionary does not contain entry for: "+classType.stringValue());
		}
		return ontologyClass;
		
	}

	@Override
	public List<Class<? extends TopOntologyConcept>> getClassConcepts() {
		return new ArrayList<Class<? extends TopOntologyConcept>>(classDictionary.values());
	}

	@Override
	public String getNameSpace(Class<?> classType) throws DictionaryException {
		if (classDictionary.containsValue(classType)) {
			return namespace;
		} else if (enumDictionary.containsValue(classType)) {
			return namespace;
		} else {
			return null;
		}
	}

	
	@Override
	public SimpleDictionary getSimpleDictionary(String namespace) {
		if (this.namespace.equals(namespace)) {
			return this;
		} else {
			return null;
		}
	}

	
	public String getNameSpace() {
		return namespace;
	}
	
	
}
