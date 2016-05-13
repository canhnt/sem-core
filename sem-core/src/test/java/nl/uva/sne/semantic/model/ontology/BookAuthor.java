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

import java.util.ArrayList;
import java.util.List;

public class BookAuthor extends TopConcept {

	private List<Book> writtenBook;
	
	public BookAuthor(String id) {
		super(id);
		writtenBook = new ArrayList<Book>();
	}

	
	public List<Book> getWrittenBook() {
		return this.writtenBook;
	}
	
	public void setWrittenBook(List<Book> writtenBook) {
		this.writtenBook = writtenBook;
	}

	
}
