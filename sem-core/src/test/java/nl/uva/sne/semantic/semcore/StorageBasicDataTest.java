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

import java.util.Arrays;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import junit.framework.TestCase;

import nl.uva.sne.semantic.model.BookDictionary;
import nl.uva.sne.semantic.model.TestDictionary;
import nl.uva.sne.semantic.model.ontology.BasicTypeContainerConcept;
import nl.uva.sne.semantic.model.ontology.EnumContainerConcept;
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.model.ontology.XMLContainerConcept;
import nl.uva.sne.semantic.semcore.compare.Compare;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryRepository;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class StorageBasicDataTest extends TestCase {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StorageBasicDataTest.class);
	
	private static final String NS = "http://test1.sne.uva.nl#";
	
	private TripleStorageReader reader;
	private TripleStorageWriter writer;
	
	@Before
	public void setUp() {
		log.debug("setting up empty test storage.");
		try {
			Repository repository = new SailRepository(new MemoryStore());
			repository.initialize();
			RepositoryConnection connection = repository.getConnection();
			ValueFactory valueFactory = connection.getValueFactory();
			ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
			
			reader = new TripleStorageReader(repository, connection, valueFactory, dictionary);
			writer = new TripleStorageWriter(repository, connection, valueFactory, dictionary);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@After
	public void tearDown() {
		try {
			reader.close();
			writer.close(); 
			log.debug("test storage removed successfully.");
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testStoreNullID() {
		try {
			log.debug("testing null id");
			BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(null);
			writer.storeConcept(basicConcept, true);
			fail();
		} catch (Exception e) {
			// success
		}
	}
	
	@Test
	public void testStoreMalformedID() {
		try {
			log.debug("testing malformed id");
			BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept("randomString");
			writer.storeConcept(basicConcept, true);
			fail();
		} catch (Exception e) {
			// success
		} 
	}
	
	
	@Test
	public void testStoreRetrieveProperURNID() {
		try {
			log.debug("testing proper URN id");
			BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept("urn:sne.uva.nl:test-id");
			writer.storeConcept(basicConcept, true);
			
			URI conceptURI = new URIImpl(basicConcept.getId());
			TopOntologyConcept resultConcept = reader.retrieveConcept(conceptURI, true);
			assertTrue(resultConcept != null);
			assertTrue(resultConcept instanceof BasicTypeContainerConcept); 
			
			BasicTypeContainerConcept basicConceptCopy = (BasicTypeContainerConcept) resultConcept;
			assertTrue(Compare.shallowEquals(basicConcept, basicConceptCopy));
			assertTrue(Compare.deepEquals(basicConcept, basicConceptCopy));
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}

	@Test
	public void testStoreProperURLID() {
		try {
			log.debug("testing proper URL id");
			BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(NS+"BasicConcept");
			writer.storeConcept(basicConcept, true);
			
			URI conceptURI = new URIImpl(basicConcept.getId());
			TopOntologyConcept resultConcept = reader.retrieveConcept(conceptURI, true);
			assertTrue(resultConcept != null);
			assertTrue(resultConcept instanceof BasicTypeContainerConcept); 
			
			BasicTypeContainerConcept basicConceptCopy = (BasicTypeContainerConcept) resultConcept;
			assertTrue(Compare.shallowEquals(basicConcept, basicConceptCopy));
			assertTrue(Compare.deepEquals(basicConcept, basicConceptCopy));
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} 
	}
	
	
	@Test
	public void testStoreRetrieveBasicDataTypeProperties() {
		try {
			log.debug("testing store and retrieve on String, Integer, Double, Boolean properties");
			BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(NS+"BasicConcept");
			basicConcept.setString("testString");
			basicConcept.setInteger(12345);
			basicConcept.setDouble(1.12345);
			basicConcept.setBoolean(true);

			writer.storeConcept(basicConcept, true);
			
			URI conceptURI = new URIImpl(basicConcept.getId());
			TopOntologyConcept resultConcept = reader.retrieveConcept(conceptURI, true);
			assertTrue(resultConcept != null);
			assertTrue(resultConcept instanceof BasicTypeContainerConcept); 
			
			BasicTypeContainerConcept basicConceptCopy = (BasicTypeContainerConcept) resultConcept;
			assertTrue(Compare.shallowEquals(basicConcept, basicConceptCopy));
			assertTrue(Compare.deepEquals(basicConcept, basicConceptCopy));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testStoreRetrieveEnumTypeProperties() {
		try {
			EnumContainerConcept enumConcept = new EnumContainerConcept(NS+"EnumConcept");
			enumConcept.setPerson(People.MATTIJS);
			
			writer.storeConcept(enumConcept, true);
			
			URI conceptURI = new URIImpl(enumConcept.getId());
			TopOntologyConcept resultConcept = reader.retrieveConcept(conceptURI, true);
			assertTrue(resultConcept != null);
			assertTrue(resultConcept instanceof EnumContainerConcept); 
			
			EnumContainerConcept enumConceptCopy = (EnumContainerConcept) resultConcept;
			assertTrue(Compare.shallowEquals(enumConcept, enumConceptCopy));
			assertTrue(Compare.deepEquals(enumConcept, enumConceptCopy));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testStoreRetrieveXMLDataTypeProperties() {
		try {
			log.debug("testing store and retrieve on Duration and XMLGregorianCalendar properties");
			XMLContainerConcept xmlConcept = new XMLContainerConcept(NS+"XMLConcept");
			DatatypeFactory df = DatatypeFactory.newInstance();
			xmlConcept.setXMLGregorianCalendar(df.newXMLGregorianCalendarDate(
					2011,								
					DatatypeConstants.AUGUST, 
					13,
					DatatypeConstants.FIELD_UNDEFINED));
			xmlConcept.setDuration(df.newDuration(true, 0, 5, 0 ,0 ,0 ,0));

			writer.storeConcept(xmlConcept, true);
			
			URI conceptURI = new URIImpl(xmlConcept.getId());
			TopOntologyConcept resultConcept = reader.retrieveConcept(conceptURI, true);
			assertTrue(resultConcept != null);
			assertTrue(resultConcept instanceof XMLContainerConcept); 
			
			XMLContainerConcept xmlConceptCopy = (XMLContainerConcept) resultConcept;
			assertTrue(Compare.shallowEquals(xmlConcept, xmlConceptCopy));
			assertTrue(Compare.deepEquals(xmlConcept, xmlConceptCopy));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	
}
