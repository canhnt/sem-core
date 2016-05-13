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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import nl.uva.sne.semantic.model.BookDictionary;
import nl.uva.sne.semantic.model.TestDictionary;
import nl.uva.sne.semantic.model.ontology.BasicTypeContainerConcept;
import nl.uva.sne.semantic.model.ontology.EnumContainerConcept;
import nl.uva.sne.semantic.model.ontology.ListContainerConcept;
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.model.ontology.XMLContainerConcept;
import nl.uva.sne.semantic.semcore.TopOntologyConcept;
import nl.uva.sne.semantic.semcore.TripleInputStreamReader;
import nl.uva.sne.semantic.semcore.TripleOutputStreamWriter;
import nl.uva.sne.semantic.semcore.compare.Compare;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryRepository;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ConvertBasicDataTest extends TestCase {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConvertBasicDataTest.class);
	
	private static final String NS = "http://test1.sne.uva.nl#";
	
	private static final String TEST_OUTPUT = "src/test/java/nl/uva/sne/semantic/semcore/tmp-junit-ouput.xml";
	
	private TripleInputStreamReader reader;
	private TripleOutputStreamWriter writer;
	
	private File testFile;
	
	private InputStream in;
	
	private OutputStream out;
	
	@Before
	public void setUp() {
		try {
			ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
			
			reader = new TripleInputStreamReader(dictionary);
			writer = new TripleOutputStreamWriter(dictionary);
			
			testFile = new File(TEST_OUTPUT);
			out = new BufferedOutputStream(new FileOutputStream(testFile));
			
			in = new FileInputStream(testFile);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@After
	public void tearDown() {	
		try {
			out.close();
			in.close();
			testFile.delete();
			log.debug("convert service successfully destroyed");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testBasicDataTypeContainer() {
		BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(NS+"BasicConcept");
		try {
			basicConcept.setString("some test");
			basicConcept.setInteger(2000);
			basicConcept.setDouble(2.0001);
			basicConcept.setBoolean(true);
			
			writer.writeOWL(basicConcept, true, out);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			List<TopOntologyConcept> readResult = reader.readOWL(in, "");
			assertTrue(readResult.size()==1);
			assertTrue(readResult.get(0) instanceof BasicTypeContainerConcept);
		
			BasicTypeContainerConcept basicConceptCopy = (BasicTypeContainerConcept) readResult.get(0);
			
			assertTrue(Compare.shallowEquals(basicConcept, basicConceptCopy));
			assertTrue(Compare.deepEquals(basicConcept, basicConceptCopy));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testEnumContainer() {
		try {
			EnumContainerConcept enumConcept = new EnumContainerConcept(NS+"EnumConcept");
			enumConcept.setPerson(People.MATTIJS);
			
			writer.writeOWL(enumConcept, true, out);
			List<TopOntologyConcept> readResult = reader.readOWL(in, "");
			assertTrue(readResult.size()==1);
			assertTrue(readResult.get(0) instanceof EnumContainerConcept);
		
			EnumContainerConcept basicConceptCopy = (EnumContainerConcept) readResult.get(0);
			
			assertTrue(Compare.shallowEquals(enumConcept, basicConceptCopy));
			assertTrue(Compare.deepEquals(enumConcept, basicConceptCopy));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testXMLContainer() {
		try {
			XMLContainerConcept xmlConcept = new XMLContainerConcept(NS+"XMLConcept");
			DatatypeFactory df = DatatypeFactory.newInstance();
			xmlConcept.setXMLGregorianCalendar(df.newXMLGregorianCalendarDate(
					2011,								
					DatatypeConstants.AUGUST, 
					13,
					DatatypeConstants.FIELD_UNDEFINED));
			xmlConcept.setDuration(df.newDuration(true, 0, 5, 0 ,0 ,0 ,0));
			
			writer.writeOWL(xmlConcept, true, out);
			List<TopOntologyConcept> readResult = reader.readOWL(in, "");
			assertTrue(readResult.size()==1);
			assertTrue(readResult.get(0) instanceof XMLContainerConcept);
			
			XMLContainerConcept xmlConceptCopy = (XMLContainerConcept) readResult.get(0);
			
			assertTrue(Compare.shallowEquals(xmlConcept, xmlConceptCopy));
			assertTrue(Compare.deepEquals(xmlConcept, xmlConceptCopy));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testListContainer() {
		try {
			ListContainerConcept listConcept = new ListContainerConcept(NS+"XMLConcept");
			listConcept.setList(Arrays.asList("TEST", "TEST", "HELLO"));
			
			writer.writeOWL(listConcept, true, out);
			List<TopOntologyConcept> readResult = reader.readOWL(in, "");
			assertTrue(readResult.size()==1);
			assertTrue(readResult.get(0) instanceof ListContainerConcept);
			
			ListContainerConcept listConceptCopy = (ListContainerConcept) readResult.get(0);
			
			assertTrue(Compare.shallowEquals(listConcept, listConceptCopy));
			assertTrue(Compare.deepEquals(listConcept, listConceptCopy));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
