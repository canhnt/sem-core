/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.model;

import org.openrdf.model.impl.URIImpl;

import nl.uva.sne.semantic.model.ontology.BasicTypeContainerConcept;
import nl.uva.sne.semantic.model.ontology.Book;
import nl.uva.sne.semantic.model.ontology.BookAuthor;
import nl.uva.sne.semantic.model.ontology.BookCase;
import nl.uva.sne.semantic.model.ontology.EnumContainerConcept;
import nl.uva.sne.semantic.model.ontology.ListContainerConcept;
import nl.uva.sne.semantic.model.ontology.MapContainerConcept;
import nl.uva.sne.semantic.model.ontology.XMLContainerConcept;
import nl.uva.sne.semantic.semcore.TopOntologyConcept;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryException;
import nl.uva.sne.semantic.semcore.dictionary.SimpleDictionary;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

public class TestDictionary extends SimpleDictionary {

	public TestDictionary() {
		namespace = "http://sne.uva.nl/testontology.owl#";
		
		classDictionary.put(new URIImpl(namespace + BasicTypeContainerConcept.class.getSimpleName()), BasicTypeContainerConcept.class);
		classDictionary.put(new URIImpl(namespace + XMLContainerConcept.class.getSimpleName()), XMLContainerConcept.class);
		classDictionary.put(new URIImpl(namespace + EnumContainerConcept.class.getSimpleName()), EnumContainerConcept.class);
		classDictionary.put(new URIImpl(namespace + ListContainerConcept.class.getSimpleName()), ListContainerConcept.class);
		classDictionary.put(new URIImpl(namespace + MapContainerConcept.class.getSimpleName()), MapContainerConcept.class);
	}
	

	@Override
	public int getModelNameOffset(String nameSpace) throws DictionaryException {
		return 0;
	}
	
}
