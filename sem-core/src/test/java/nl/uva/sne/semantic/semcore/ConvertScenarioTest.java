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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import nl.uva.sne.semantic.model.BookDictionary;
import nl.uva.sne.semantic.model.TestDictionary;
import nl.uva.sne.semantic.model.ontology.Book;
import nl.uva.sne.semantic.model.ontology.BookAuthor;
import nl.uva.sne.semantic.model.ontology.BookCase;
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.semcore.compare.Compare;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryRepository;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class ConvertScenarioTest extends TestCase {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConvertScenarioTest.class);
	static {
		BasicConfigurator.configure();
	}
	
	private static final String TEST_OUTPUT = "src/test/java/nl/uva/sne/semantic/semcore/tmp-junit-scenario-ouput.xml";
	
	private static final String NS = "http://testconcept.sne.uva.nl#";
	
	private TripleOutputStreamWriter writer;
	private TripleInputStreamReader reader;

	private OutputStream out;
	private InputStream in;
	
	@Before
	public void setUp() {
		ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
		writer = new TripleOutputStreamWriter(dictionary);
		reader = new TripleInputStreamReader(dictionary);
		
		try {
			File file = new File(TEST_OUTPUT);
			out = new FileOutputStream(file);
			in = new FileInputStream(file);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@After
	public void tearDown() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testShallowWriteRead() {
		try {
			log.info("testSimpleShallowWriteRead");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, bookCase);
			
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			stephenKing.setWrittenBook(Arrays.asList(theShining, dumaKey));
			
			writer.writeOWL(bookCase, false, out);
			List<TopOntologyConcept> results = reader.readOWL(in, "");
			assertTrue(results.size() == 3);
			for (TopOntologyConcept result : results) {
				if (result instanceof BookCase) {
					BookCase resultBookCase = (BookCase) result;
					assertTrue(Compare.shallowEquals(bookCase, resultBookCase));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void tesDeepWriteRead() {
		try {
			log.info("tesDeepWriteRead");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, bookCase);
			
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			stephenKing.setWrittenBook(Arrays.asList(theShining, dumaKey));
			
			writer.writeOWL(bookCase, true, out);
			List<TopOntologyConcept> results = reader.readOWL(in, "");
			assertTrue(results.size() == 4);
			for (TopOntologyConcept result : results) {
				if (result instanceof BookCase) {
					BookCase resultBookCase = (BookCase) result;
					assertTrue(Compare.deepEquals(bookCase, resultBookCase));
				} else if (result instanceof Book) {
					Book resultBook = (Book) result;
					assertTrue(Compare.deepEquals(theShining, resultBook) || Compare.deepEquals(dumaKey, resultBook));
				} else if (result instanceof BookAuthor) {
					BookAuthor resultAuthor = (BookAuthor) result;
					assertTrue(Compare.deepEquals(stephenKing, resultAuthor));
				} else {
					fail();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testListWriteRead() {
		try {
			log.info("testSimpleWriteRead");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, bookCase);
			
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			stephenKing.setWrittenBook(Arrays.asList(theShining, dumaKey));
			
			writer.writeOWL(Arrays.asList((TopOntologyConcept)bookCase, theShining, dumaKey), false, out);
			List<TopOntologyConcept> results = reader.readOWL(in, "");
			assertTrue(results.size() == 4);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
}
