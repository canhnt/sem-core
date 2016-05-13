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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import nl.uva.sne.semantic.model.map.MAP;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryException;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;
import nl.uva.sne.semantic.semcore.dictionary.SimpleDictionary;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;


/**
 * The Class TripleStorageReader.
 */
public class TripleStorageReader {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TripleStorageReader.class);
	
	private final Repository repository;
	private final RepositoryConnection connection;
	private final ValueFactory valueFactory;
	private final ModelDictionary dictionary;
	
	/**
	 * Instantiates a new triple storage reader.
	 *
	 * @param repository the repository
	 * @param connection the connection
	 * @param valueFactory the value factory
	 * @param dictionary the dictionary
	 */
	public TripleStorageReader(Repository repository, RepositoryConnection connection, ValueFactory valueFactory, ModelDictionary dictionary) {
		this.repository = repository;
		this.connection = connection;
		this.valueFactory = valueFactory;
		this.dictionary = dictionary;
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
	 * Retrieve concept.
	 *
	 * @param conceptURI the concept uri
	 * @param crawl the crawl
	 * @return the top ontology concept
	 * @throws RepositoryException the repository exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws DictionaryException the dictionary exception
	 * @throws ConversionException the conversion exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws TripleReaderException the triple reader exception
	 * @throws NoSuchFieldException 
	 */
	public TopOntologyConcept retrieveConcept(URI conceptURI, boolean crawl) throws RepositoryException, IllegalArgumentException, SecurityException, DictionaryException, ConversionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DatatypeConfigurationException, URISyntaxException, TripleReaderException, NoSuchFieldException {
		
		List<Statement> triples = connection.getStatements(conceptURI, null, null, true).asList();
		
		List<Statement> relatedTriples = findRelatedTriples(triples, new ArrayList<String>(), crawl);
		triples.addAll(relatedTriples);
		
		
		Collection<TopOntologyConcept> concepts = TripleToJavaConvertor.triplesToJava(triples, dictionary);
		
		Iterator<TopOntologyConcept> conceptIterator = concepts.iterator();
		while (conceptIterator.hasNext()) {
			TopOntologyConcept concept = conceptIterator.next();
			if (concept.getId().equals(conceptURI.stringValue())) {
				return concept;
			}
		}
		throw new TripleReaderException("No concept for " + conceptURI.stringValue() + " could be found.");
	}

	/**
	 * Retrieve concept type.
	 *
	 * @param type the type
	 * @param crawl the crawl
	 * @return the list
	 * @throws RepositoryException the repository exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws DictionaryException the dictionary exception
	 * @throws ConversionException the conversion exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws NoSuchFieldException 
	 */
	public List<TopOntologyConcept> retrieveConceptType(Class<? extends TopOntologyConcept> type, boolean crawl) throws RepositoryException, IllegalArgumentException, SecurityException, DictionaryException, ConversionException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DatatypeConfigurationException, URISyntaxException, NoSuchFieldException {
		String nameSpace = dictionary.getNameSpace(type);
		URI subjectURI = new URIImpl(nameSpace + type.getSimpleName().substring(dictionary.getModelNameOffset(nameSpace)));
		List<Statement> typeTriples = connection.getStatements(null, RDF.TYPE, subjectURI, true).asList();
		List<Statement> triples = new ArrayList<Statement>();
		for (Statement triple : typeTriples) {
			triples.addAll(connection.getStatements(triple.getSubject(), null, null, true).asList());
		}
		
		List<Statement> relatedTriples = findRelatedTriples(triples, new ArrayList<String>(), crawl);
		triples.addAll(relatedTriples);
		
		Collection<TopOntologyConcept> concepts = TripleToJavaConvertor.triplesToJava(triples, dictionary);
		List<TopOntologyConcept> result = new ArrayList<TopOntologyConcept>();
		Iterator<TopOntologyConcept> conceptIterator = concepts.iterator();
		while (conceptIterator.hasNext()) {
			TopOntologyConcept concept = conceptIterator.next();
			if (type.isAssignableFrom(concept.getClass())) {
				result.add(concept);
			}
		}
		
		return result;
	}
	
	private List<Statement> findRelatedTriples(List<Statement> triples, List<String> found, boolean crawl) throws RepositoryException, DictionaryException {
		// add the triples to the list of found
		for (Statement triple : triples) {
			if (triple.getPredicate().equals(RDF.TYPE)) {
				if (triple.getObject() instanceof URI) {
					URI objectURI = (URI) triple.getObject();
					SimpleDictionary dict = dictionary.getSimpleDictionary(objectURI.getNamespace());
					if (dict != null) {
						if (!found.contains(triple.getSubject().stringValue())) {
							found.add(triple.getSubject().stringValue());
						}
					}
				}
			}
		}
		List<Statement> relatedTriples = new ArrayList<Statement>();
		// then start exploring the triples for new related triples
		for (Statement triple : triples) {
			SimpleDictionary dict = dictionary.getSimpleDictionary(triple.getPredicate().getNamespace());
			if (dict != null ||
				triple.getPredicate().getNamespace().equals(MAP.NAMESPACE)) {
				if (triple.getObject() instanceof URI) {
					URI objectURI = (URI) triple.getObject();
					if (!found.contains(objectURI.stringValue())) {
						List<Statement> subjectTriples = connection.getStatements(objectURI, null, null, true).asList();
						
						if (crawl) {
							relatedTriples.addAll(subjectTriples);
							relatedTriples.addAll(findRelatedTriples(subjectTriples, found, crawl));
						} else {
							// only get RDF.TYPE
							for (Statement subjectTriple : subjectTriples) {
								if (subjectTriple.getPredicate().equals(RDF.TYPE)) {
									relatedTriples.add(subjectTriple);
								}
							}
						}
						
					}
				} 
			}
		}
	
		return relatedTriples;
	}

	public boolean isEmpty() throws RepositoryException {
		return connection.isEmpty();
	}

	public void exportDB(RDFHandler handler) throws RepositoryException, RDFHandlerException {
		connection.export(handler);
	}
}
