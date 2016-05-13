/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.model.ontology;

public class Book extends TopConcept {

	private People author;
	private BookCase bookCase;
	private BookAuthor bookCover;
	
	public Book(String id) {
		super(id);
	}
	
	public Book(String id, People author, BookAuthor bookCover, BookCase bookCase) {
		super(id);
		this.author = author;
		this.bookCover = bookCover;
		this.bookCase = bookCase;
	}

	@Predicate(function="getter", predicateName="http://sne.uva.nl/anotherminibookontology.owl#hasAuthor")
	public People testGetAuthor() {
		return this.author;
	}
	
	@Predicate(function="setter", predicateName="http://sne.uva.nl/anotherminibookontology.owl#hasAuthor")
	public void testSetAuthor(People author) {
		this.author = author;
	}
	
	public BookCase getBookCase() {
		return this.bookCase;
	}
	
	public void setBookCase(BookCase bookCase) {
		this.bookCase = bookCase;
	}
	
	public BookAuthor getBookCover() {
		return this.bookCover;
	}
	
	public void setBookCover(BookAuthor bookCover) {
		this.bookCover = bookCover;
	}
 
}
