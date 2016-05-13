/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.model.map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class MAP {
	
	public static final String NAMESPACE = "http://sne.uva.nl/map.owl#";
	
	public static final URI MAP = new URIImpl(NAMESPACE + "Map");
	public static final URI MAPKEY = new URIImpl(NAMESPACE + "MapKey");
	public static final URI HAS_MAPKEY = new URIImpl(NAMESPACE + "hasMapKey");
	public static final URI HAS_MAPVALUE = new URIImpl(NAMESPACE + "hasMapValue");
	
}
