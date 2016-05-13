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

public class BookCase extends TopConcept {

	private List<Book> books;
	private Integer size;
	
	public BookCase(String id) {
		super(id);
		books = new ArrayList<Book>();
	}
	
	public List<Book> getBook() {
		return this.books;
	}
	
	public void setBook(List<Book> books) {
		this.books = books;
	}
	
	public Integer getSize() {
		return this.size;
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}

}
