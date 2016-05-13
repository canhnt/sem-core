/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.semcore.dictionary;

import nl.uva.sne.semantic.semcore.ConversionException;

/**
 * The Class DictionaryException.
 */
public class DictionaryException extends ConversionException {

	private static final long serialVersionUID = -3568209737551945509L;

	/**
	 * Instantiates a new dictionary exception.
	 *
	 * @param message the message
	 */
	public DictionaryException(String message) {
		super(message);
	}

	
}
