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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.sne.semantic.model.BookDictionary;
import nl.uva.sne.semantic.model.TestDictionary;
import nl.uva.sne.semantic.model.ontology.MapContainerConcept;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import junit.framework.TestCase;

public class MapTest extends TestCase {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MapTest.class);
	static {
		BasicConfigurator.configure();
	}
	
	private static final String NS = "http://testconcept.sne.uva.nl#";
	
	private TripleStorageWriter storageWriter;
	private TripleStorageReader storageReader;
	
	
	private static Map<Integer, String> buildSimpleMap() {
		Map<Integer, String> result = new HashMap<Integer, String>();
		
		result.put(1, "one");
		result.put(2, "two");
		result.put(3, "three");
		
		return result;
	}
	
	private static Map<String, List<String>> buildSimpleListMap() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		
		result.put("Key1", Arrays.asList("one","two","three"));
		result.put("Key2", Arrays.asList("four","five","six"));
		result.put("Key3", new ArrayList<String>());
		
		return result;
	}
	
	@Before
	public void setUp() {
		log.debug("setting up empty test storage.");
		try {
			Repository repository = new SailRepository(new MemoryStore());
			repository.initialize();
			RepositoryConnection connection = repository.getConnection();
			ValueFactory valueFactory = connection.getValueFactory();
			ModelDictionary dictionary = new TestDictionary();
			
			storageReader = new TripleStorageReader(repository, connection, valueFactory, dictionary);
			storageWriter = new TripleStorageWriter(repository, connection, valueFactory, dictionary);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDeepStorage() {
		try {
			MapContainerConcept mapContainer = new MapContainerConcept(NS + "MyMapContainer");
			Map<Integer, String> simpleMap = buildSimpleMap();
			mapContainer.setSimpleMap(simpleMap);
			Map<String, List<String>> simpleListMap = buildSimpleListMap();
			mapContainer.setSimpleListMap(simpleListMap);
			
			
			storageWriter.storeConcept(mapContainer, true);
			TopOntologyConcept result = storageReader.retrieveConcept(new URIImpl(mapContainer.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof MapContainerConcept);
			MapContainerConcept resultContainer = (MapContainerConcept) result;
			
			assertNotNull(resultContainer.getSimpleMap());
			log.debug(resultContainer.getSimpleMap().toString());
			assertTrue(simpleMap.equals(resultContainer.getSimpleMap()));
			
			assertNotNull(resultContainer.getSimpleListMap());
			log.debug(resultContainer.getSimpleListMap().toString());
			assertTrue(simpleListMap.equals(resultContainer.getSimpleListMap()));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	
}
