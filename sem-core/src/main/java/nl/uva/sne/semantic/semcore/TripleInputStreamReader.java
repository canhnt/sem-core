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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLParser;


/**
 * The Class TripleFileReader.
 */
public class TripleInputStreamReader {
	
	private final RDFParser parser;
	private final ModelDictionary dictionary;
	
	/**
	 * Instantiates a new triple file reader.
	 *
	 * @param dictionary the dictionary
	 */
	public TripleInputStreamReader(ModelDictionary dictionary) {
		this.parser = new RDFXMLParser();
		this.dictionary = dictionary;
	}
	
	
	/**
	 * Read owl.
	 *
	 * @param in the in
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<TopOntologyConcept> readOWL(InputStream in, String baseNamespace) throws Exception {
		
		StatementCollector myRDFHandler = new StatementCollector();
		
		parser.setRDFHandler(myRDFHandler);
		parser.setVerifyData(true);
		parser.setStopAtFirstError(false);
		
		parser.parse(in, baseNamespace);
		
		Collection<Statement> statements = myRDFHandler.getStatements();
		
		List<TopOntologyConcept> concepts = new ArrayList<TopOntologyConcept>(TripleToJavaConvertor.triplesToJava(statements, dictionary));
		
		// link the concepts
		
		
		return concepts;
		
	}
	
		
	
	
}
