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
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.model.ontology.XMLContainerConcept;
import nl.uva.sne.semantic.semcore.TopOntologyConcept;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryException;
import nl.uva.sne.semantic.semcore.dictionary.SimpleDictionary;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

public class BookDictionary extends SimpleDictionary {

	public BookDictionary() {
		namespace = "http://sne.uva.nl/minibookontology.owl#";
		
		enumDictionary.put(new URIImpl(namespace + People.class.getSimpleName()), People.class);
		
		classDictionary.put(new URIImpl(namespace + BookCase.class.getSimpleName()), BookCase.class);
		classDictionary.put(new URIImpl(namespace + Book.class.getSimpleName()), Book.class);
		classDictionary.put(new URIImpl(namespace + BookAuthor.class.getSimpleName()), BookAuthor.class);
		
	}


	@Override
	public int getModelNameOffset(String nameSpace) throws DictionaryException {
		return 0;
	}

}
