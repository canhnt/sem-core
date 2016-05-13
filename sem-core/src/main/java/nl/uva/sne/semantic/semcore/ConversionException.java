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
 * The Class ConversionException is used in case.
 */
public class ConversionException extends Exception {

	private static final long serialVersionUID = -8231392667513345510L;

	/**
	 * Instantiates a new conversion exception.
	 *
	 * @param reason the reason why the exception is thrown
	 */
	public ConversionException(String reason) {
		super(reason);
	}
}
