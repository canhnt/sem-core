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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.rdfxml.RDFXMLWriterFactory;

/**
 * The Class TripleFileWriter.
 */
public class TripleOutputStreamWriter {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TripleOutputStreamWriter.class);
	
	private final ModelDictionary modelDictionary;
	
	/**
	 * Instantiates a new triple file writer.
	 *
	 * @param modelDictionary the model dictionary
	 */
	public TripleOutputStreamWriter(ModelDictionary modelDictionary) {
		this.modelDictionary = modelDictionary;
		log.debug("created default triple writer");
	}
	
	/**
	 * Write owl.
	 *
	 * @param concept the concept
	 * @param crawl the crawl
	 * @param out the out
	 * @throws Exception the exception
	 */
	public void writeOWL(TopOntologyConcept concept, boolean crawl, OutputStream out) throws Exception {
		RDFWriter rdfWriter = new RDFXMLWriterFactory().getWriter(out);
		
		List<Statement> statements = JavaToTripleConvertor.encodeToTriple(concept, modelDictionary, true);
		//List<Statement> statements = new ArrayList<Statement>();
		List<String> found = new ArrayList<String>();
		found.add(concept.getId());
		List<TopOntologyConcept> relatedConcepts = JavaToTripleConvertor.findRelatedConcepts(concept, found, crawl);
		for (TopOntologyConcept related : relatedConcepts) {
			statements.addAll(JavaToTripleConvertor.encodeToTriple(related, modelDictionary, crawl));
		}
		
		rdfWriter.startRDF();
		for (Statement statement : statements) {
			rdfWriter.handleStatement(statement);
		}
		rdfWriter.endRDF();
		
		out.close();
	}
	
	/**
	 * Write owl.
	 *
	 * @param concepts the concepts
	 * @param crawl the crawl
	 * @param out the out
	 * @throws Exception the exception
	 */
	public void writeOWL(List<TopOntologyConcept> concepts, boolean crawl, OutputStream out) throws Exception {
		RDFWriter rdfWriter = new RDFXMLWriterFactory().getWriter(out);
			
		
		List<Statement> statements = new ArrayList<Statement>();
		for (TopOntologyConcept concept : concepts) {
			statements.addAll(JavaToTripleConvertor.encodeToTriple(concept, modelDictionary, true));
		}
			
		List<String> found = new ArrayList<String>();
		for (TopOntologyConcept concept : concepts) {
			found.add(concept.getId());
		}
		List<TopOntologyConcept> relatedConcepts = new ArrayList<TopOntologyConcept>();
		for (TopOntologyConcept concept : concepts) {
			relatedConcepts.addAll(JavaToTripleConvertor.findRelatedConcepts(concept, found, crawl));
		}	
		for (TopOntologyConcept related : relatedConcepts) {
			statements.addAll(JavaToTripleConvertor.encodeToTriple(related, modelDictionary, crawl));
		}
		
		rdfWriter.startRDF();
		for (Statement statement : statements) {
			rdfWriter.handleStatement(statement);
		}
		rdfWriter.endRDF();
		
		out.close();
	}
	
}
