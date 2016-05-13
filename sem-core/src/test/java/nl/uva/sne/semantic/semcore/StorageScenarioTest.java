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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.uva.sne.semantic.model.BookDictionary;
import nl.uva.sne.semantic.model.TestDictionary;
import nl.uva.sne.semantic.model.ontology.Book;
import nl.uva.sne.semantic.model.ontology.BookCase;
import nl.uva.sne.semantic.model.ontology.BookAuthor;
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.semcore.compare.Compare;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryRepository;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
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

public class StorageScenarioTest extends TestCase {
	
	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StorageScenarioTest.class);
	static {
		BasicConfigurator.configure();
	}
	
	private static final String NS = "http://testconcept.sne.uva.nl#";
	
	private TripleStorageWriter writer;
	private TripleStorageReader reader;
	
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
	public void testShallowDeepStore() {
		
		try {
			log.info("create datastructure");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, null, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, null, bookCase);
			
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			
			log.info("first, try shallow storage and shallow retrieval");
			writer.storeConcept(bookCase, false);
			log.info("shallow retrieval");
			TopOntologyConcept result = reader.retrieveConcept(new URIImpl(bookCase.getId()), false);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			BookCase resultBookCase = (BookCase) result;
			assertTrue(Compare.shallowEquals(bookCase, resultBookCase));
			assertFalse(Compare.deepEquals(bookCase, resultBookCase));
			// in case there is some bug in Compare
			List<Book> resultBooks = resultBookCase.getBook();
			assertTrue(resultBooks.size() == 2);
			for (Book book : resultBooks) {
				assertNotNull(book.getId());
				assertNull(book.testGetAuthor());
				assertNull(book.getBookCase());
			}
			
			writer.clear();
			
			log.info("also try shallow storage with deep retrieval");
			writer.storeConcept(bookCase, false);
			log.info("deep retrieval");
			result = reader.retrieveConcept(new URIImpl(bookCase.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			resultBookCase = (BookCase) result;	
			assertTrue(Compare.shallowEquals(bookCase, resultBookCase));
			assertFalse(Compare.deepEquals(bookCase, resultBookCase));
			
			writer.clear();
			
			log.info("now try deep storage with shallow retrieval");
			writer.storeConcept(bookCase, true);
			log.info("shallow retrieval");
			result = reader.retrieveConcept(new URIImpl(bookCase.getId()), false);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			resultBookCase = (BookCase) result;
			assertTrue(Compare.shallowEquals(bookCase, resultBookCase));
			assertFalse(Compare.deepEquals(bookCase, resultBookCase));
			
			writer.clear();
			log.info("finally we try deep storage with deep retrieval");
			writer.storeConcept(bookCase, true);
			log.info("deep retrieval");
			result = reader.retrieveConcept(new URIImpl(bookCase.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			resultBookCase = (BookCase) result;
			assertTrue(Compare.shallowEquals(bookCase, resultBookCase));
			assertTrue(Compare.deepEquals(bookCase, resultBookCase));
			// double check on Compare
			resultBooks = resultBookCase.getBook();
			assertTrue(resultBooks.size() == 2);
			for (Book book : resultBooks) {
				if (book.getId().equals(theShining.getId())) {
					assertTrue(book.testGetAuthor().equals(theShining.testGetAuthor()));
					assertTrue(book.getBookCase().getId().equals(theShining.getBookCase().getId()));
				} else if (book.getId().equals(dumaKey.getId())) {
					assertTrue(book.testGetAuthor().equals(dumaKey.testGetAuthor()));
					assertTrue(book.getBookCase().getId().equals(dumaKey.getBookCase().getId()));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	
	/**
	 * Test simple delete. 
	 * - create a bookcase with 2 books
	 * - deep-store the bookcase
	 * - delete one of the books from the database
	 * - retrieve the bookcase
	 * - the bookcase should contain only one book
	 */
	@Test
	public void testSimpleDelete() {
		try {
			log.info("testSimpleDelete");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, bookCase);
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			
			writer.storeConcept(bookCase, true);	
			writer.deleteConcept(theShining, false);
			
			TopOntologyConcept result = reader.retrieveConcept(new URIImpl(bookCase.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			BookCase resultBookCase = (BookCase) result;
			assertTrue(resultBookCase.getBook().size() == 1);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test update delete.
	 * Part 1:
	 * - create a bookcase with two books
	 * - deep-store the bookcase
	 * - remove book1 from the bookcase
	 * - deep-store the updated bookcase
	 * - retrieve the bookcase from the DB
	 * - the bookcase should contain only one book
	 * 
	 * Part 2:
	 * - the bookcase doesn't know about the removed book1
	 * - the removed book1 still knows about the bookcase
	 * - remove the remaining book2 from the bookcase
	 * - deep-delete book2 from the database
	 * - deep-delete also tries to delete the bookcase but then sees that a reference 
	 * 		still exists to the bookcase from book1.
	 * - retrieve book1 from the database, check if it contains a reference to the bookcase 
	 */
	@Test
	public void testUpdateDelete() {
		try {
			log.info("testUpdateDelete");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, bookCase);
		
			bookCase.setBook(Arrays.asList(theShining, dumaKey));
			
			writer.storeConcept(bookCase, true);
			bookCase.setBook(Arrays.asList(theShining));
			// the bookCase now thinks it no longer contains dumaKey
			// thus, dumaKey won't be removed if we delete the bookCase and all related concepts from the storage
			// but, dumaKey still knows about the bookCase, so the bookCase shouldn't be removed at all!
			writer.storeConcept(bookCase, true);
			
			TopOntologyConcept result = reader.retrieveConcept(new URIImpl(bookCase.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof BookCase);
			BookCase resultBookCase = (BookCase) result;
			assertTrue(resultBookCase.getBook().size() == 1);
			
			writer.deleteConcept(theShining, true);
			// update the bookcase accordingly
			bookCase.setBook(new ArrayList<Book>());
			
			result = reader.retrieveConcept(new URIImpl(dumaKey.getId()), true);
			assertNotNull(result);
			assertTrue(result instanceof Book);
			Book resultBook = (Book) result;
			assertTrue(Compare.deepEquals(dumaKey, resultBook));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test nested delete.
	 * - create a bookcase with book "the Shining"
	 * - create another book "Duma Key"
	 * - both books are written by the same author
	 * - deep-delete the bookcase
	 * - because all concepts are reachable from the bookcase, everything will be deleted
	 * Part 2:
	 * - repeat the scenario but now there is no link from author to "Duma Key"
	 * - all concepts related to "Duma Key" should now be retained.
	 */
	@Test 
	public void testNestedDelete() {
		try {
			log.info("testNestedDelete");
			BookCase bookCase = new BookCase(NS + "MyBookCase");
			bookCase.setSize(5);
			
			BookAuthor stephenKing = new BookAuthor(NS+"StephenKing");
			
			Book theShining = new Book(NS + "TheShining", People.STEPHEN_KING, stephenKing, bookCase);
			Book dumaKey = new Book(NS + "DumaKey", People.STEPHEN_KING, stephenKing, null);
		
			stephenKing.setWrittenBook(Arrays.asList(theShining, dumaKey));
			bookCase.setBook(Arrays.asList(theShining));
			
			log.info("storing bookcase");
			writer.storeConcept(bookCase, true);
			log.info("storing dumakey");
			writer.storeConcept(dumaKey, true);
			log.info("deleting bookcase");
			writer.deleteConcept(bookCase, true);
			
			assertTrue(reader.isEmpty());
			stephenKing.setWrittenBook(Arrays.asList(theShining));
			
			log.info("storing bookcase");
			writer.storeConcept(bookCase, true);
			log.info("storing dumakey");
			writer.storeConcept(dumaKey, true);
			log.info("deleting bookcase");
			writer.deleteConcept(bookCase, true);
			
			TopOntologyConcept result = reader.retrieveConcept(new URIImpl(dumaKey.getId()), true);
			assertNotNull(result);
			result = reader.retrieveConcept(new URIImpl(stephenKing.getId()), true);
			assertNotNull(result);
			result = reader.retrieveConcept(new URIImpl(theShining.getId()), true);
			assertNotNull(result);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRetrieveType() {
		
	}
	
}
