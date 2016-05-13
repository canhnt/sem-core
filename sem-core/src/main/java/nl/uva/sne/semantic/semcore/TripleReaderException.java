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

/**
 * The Class TripleReaderException.
 */
public class TripleReaderException extends Exception {

	private static final long serialVersionUID = -4134171986819756724L;

	/**
	 * Instantiates a new triple reader exception.
	 *
	 * @param reason the reason
	 */
	public TripleReaderException(String reason) {
		super(reason);
	}
	
}
