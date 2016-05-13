/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic;

import nl.uva.sne.semantic.model.ModelStructureTest;
import nl.uva.sne.semantic.semcore.CompareBasicDataTest;
import nl.uva.sne.semantic.semcore.ConvertBasicDataTest;
import nl.uva.sne.semantic.semcore.ConvertScenarioTest;
import nl.uva.sne.semantic.semcore.MapTest;
import nl.uva.sne.semantic.semcore.StorageBasicDataTest;
import nl.uva.sne.semantic.semcore.StorageScenarioTest;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class SneCoreTestSuite extends TestSuite {

	public static TestSuite buildSuite() {
		Class<?>[] testCases = {ModelStructureTest.class,
								ConvertBasicDataTest.class,
								StorageBasicDataTest.class,
								CompareBasicDataTest.class,
								StorageScenarioTest.class,
								ConvertScenarioTest.class,
								MapTest.class};
		
		return new TestSuite(testCases);
	}
	
	public static void main(String[] args) {
		TestSuite suite = buildSuite();
		TestRunner.run(suite);
	}
}
