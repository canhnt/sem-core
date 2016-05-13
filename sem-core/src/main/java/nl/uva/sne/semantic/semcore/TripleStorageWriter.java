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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;


/**
 * The Class TripleStorageWriter.
 */
public class TripleStorageWriter {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TripleStorageWriter.class);
	
	private final Repository repository;
	private final RepositoryConnection connection;
	private final ValueFactory valueFactory;
	private final ModelDictionary modelDictionary;
	
	/**
	 * Instantiates a new triple storage writer.
	 *
	 * @param repository the repository
	 * @param connection the connection
	 * @param valueFactory the value factory
	 * @param modelDictionary the model dictionary
	 */
	public TripleStorageWriter(Repository repository, RepositoryConnection connection, ValueFactory valueFactory, ModelDictionary modelDictionary) {
		this.repository = repository;
		this.connection = connection;
		this.valueFactory = valueFactory;
		this.modelDictionary = modelDictionary;
	}

	/**
	 * Clear.
	 *
	 * @throws RepositoryException the repository exception
	 */
	public void clear() throws RepositoryException {
		connection.clear();
	}
	
	/**
	 * Initialized.
	 *
	 * @return true, if successful
	 */
	public boolean initialized() {
		return (repository != null && connection != null && valueFactory != null);
	}
	
	/**
	 * Close.
	 *
	 * @throws RepositoryException the repository exception
	 */
	public void close() throws RepositoryException {
		if (connection != null) {
			if (connection.isOpen()) {
				connection.close();
			}
		}
		if (repository != null) {
			repository.shutDown();
		}
	}

	/**
	 * Store concept.
	 *
	 * @param concept the concept
	 * @param crawl the crawl
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws ConversionException the conversion exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws RepositoryException the repository exception
	 */
	public void storeConcept(TopOntologyConcept concept, boolean crawl) throws IllegalArgumentException, ConversionException, IllegalAccessException, InvocationTargetException, RepositoryException {
		List<Statement> statements = JavaToTripleConvertor.encodeToTriple(concept, modelDictionary, true);

		List<Resource> oldSubjects = new ArrayList<Resource>();
		for (Statement statement : statements) {
			if (!oldSubjects.contains(statement.getSubject())) {
				oldSubjects.add(statement.getSubject());
			}
		}

		List<String> found = new ArrayList<String>();
		found.add(concept.getId());
		List<TopOntologyConcept> relatedConcepts = JavaToTripleConvertor.findRelatedConcepts(concept, found, crawl);
		for (TopOntologyConcept related : relatedConcepts) {
			statements.addAll(JavaToTripleConvertor.encodeToTriple(related, modelDictionary, crawl));
		}
		List<Resource> oldRelatedSubjects = new ArrayList<Resource>();
		if (crawl) {
			for (Statement statement : statements) {
				if (!oldRelatedSubjects.contains(statement.getSubject())) {
					oldRelatedSubjects.add(statement.getSubject());
				}
			}
		}
		
		// before adding these statements, remove any old statements with the same subjectURI
		for (Resource subject : oldSubjects) {
			log.debug("removing all references from subject in DB:" + subject.stringValue());
			connection.remove(subject, null, null, new Resource[0]);
		}
		for (Resource subject : oldRelatedSubjects) {
			log.debug("removing all references from subject in DB:" + subject.stringValue());
			connection.remove(subject, null, null, new Resource[0]);
		}
		
		
		connection.add(statements);
	}

	/**
	 * Delete concept.
	 *
	 * @param concept the concept
	 * @param crawl the crawl
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws ConversionException the conversion exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws RepositoryException the repository exception
	 */
	public void deleteConcept(TopOntologyConcept concept, boolean crawl) throws IllegalArgumentException, ConversionException, IllegalAccessException, InvocationTargetException, RepositoryException {
		List<Statement> triples = JavaToTripleConvertor.encodeToTriple(concept, modelDictionary, true);
		for (Statement statement : triples) {
			log.debug("removing from DB: " + statement.toString());
		}
		
		connection.remove(triples);				
		// remove all references to this object
		List<Statement> references = connection.getStatements(null, null, new URIImpl(concept.getId()), true).asList();
		for (Statement statement : references) {
			log.debug("removing from DB: " + statement.toString());
		}
		connection.remove(references);
		
		if (crawl) {
			// Step 1: find the subgraph of triples that should also be deleted
			List<String> candidateIds = new ArrayList<String>();
			candidateIds.add(concept.getId());
			List<TopOntologyConcept> candidateConcepts = JavaToTripleConvertor.findRelatedConcepts(concept, candidateIds, crawl);
			candidateIds.remove(concept.getId());
			
			// Step 2: check all candidate id's for references from outside the list of candidates
			boolean check = true;
			while (check) {
				List<String> noRemove = new ArrayList<String>();
				for (String candidateId : candidateIds) {
					List<Statement> referencingTriples = connection.getStatements(null, null, new URIImpl(candidateId), true).asList();
					for (Statement referencingTriple : referencingTriples) {
						String subjectId = referencingTriple.getSubject().stringValue();
						String objectId = referencingTriple.getObject().stringValue();
						if (!candidateIds.contains(subjectId)) {
							noRemove.add(objectId);
							break; // go to the next candidate
						}
					}
				}
				candidateIds.removeAll(noRemove);
				check = !noRemove.isEmpty();
			}
			
			// Step 3: remove all obsolete candidate concepts
			List<TopOntologyConcept> obsoleteCandidates = new ArrayList<TopOntologyConcept>();
			for (TopOntologyConcept candidateConcept : candidateConcepts) {
				if (!candidateIds.contains(candidateConcept.getId())) {
					obsoleteCandidates.add(candidateConcept);
				}
			}
			candidateConcepts.removeAll(obsoleteCandidates);
			
			// Step 4: find the related triples of candiate concepts and delete them
			List<Statement> relatedTriples = new ArrayList<Statement>();
			for (TopOntologyConcept candidate : candidateConcepts) {
				relatedTriples.addAll(JavaToTripleConvertor.encodeToTriple(candidate, modelDictionary, true));
			}
			for (Statement statement : relatedTriples) {
				log.debug("removing from DB: " + statement.toString());
			}
			connection.remove(relatedTriples);
		}
	}
	
}
