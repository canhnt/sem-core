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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import junit.framework.TestCase;

import nl.uva.sne.semantic.model.ontology.BasicTypeContainerConcept;
import nl.uva.sne.semantic.model.ontology.EnumContainerConcept;
import nl.uva.sne.semantic.model.ontology.ListContainerConcept;
import nl.uva.sne.semantic.model.ontology.People;
import nl.uva.sne.semantic.model.ontology.XMLContainerConcept;
import nl.uva.sne.semantic.semcore.compare.Compare;

import org.junit.Test;

public class CompareBasicDataTest extends TestCase {
    
	private static final String NS = "http://test1.sne.uva.nl#";
	
	@Test
    public void testEqualsNull()
    {
		assertTrue(Compare.shallowEquals(null, null));
		assertTrue(Compare.deepEquals(null, null));
		
		BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(NS+"BasicConcept");
		assertFalse(Compare.shallowEquals(basicConcept, null));
		assertFalse(Compare.shallowEquals(null, basicConcept));
		assertFalse(Compare.deepEquals(basicConcept, null));
		assertFalse(Compare.deepEquals(null, basicConcept));		
	}
	
	@Test
	public void testEqualsClassTypes() {
		BasicTypeContainerConcept basicConcept = new BasicTypeContainerConcept(NS+"BasicConcept");
		XMLContainerConcept xmlConcept = new XMLContainerConcept(NS+"XMLConcept");
		
		assertFalse(Compare.shallowEquals(basicConcept, xmlConcept));
		assertFalse(Compare.shallowEquals(xmlConcept, basicConcept));
		assertFalse(Compare.deepEquals(basicConcept, xmlConcept));
		assertFalse(Compare.deepEquals(xmlConcept, basicConcept));
	}
	
	@Test
	public void testEqualsObjectID() {

		BasicTypeContainerConcept basicConceptA = new BasicTypeContainerConcept(NS+"BasicConceptA");
		BasicTypeContainerConcept basicConceptB = new BasicTypeContainerConcept(null);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB = new BasicTypeContainerConcept("");
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB = new BasicTypeContainerConcept(NS+"basicConceptA");
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB = new BasicTypeContainerConcept(NS+"BasicConceptA");
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));
	}
	
	@Test
	public void testEqualsStringProperty() {
		BasicTypeContainerConcept basicConceptA = new BasicTypeContainerConcept(NS+"BasicConcept");
		BasicTypeContainerConcept basicConceptB = new BasicTypeContainerConcept(NS+"BasicConcept");
		
		basicConceptA.setString("Test");
		basicConceptB.setString(null);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB.setString("");
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB.setString("test");
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB.setString("Test");
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));
	}
	
	@Test
	public void testEqualsInteger() {
		BasicTypeContainerConcept basicConceptA = new BasicTypeContainerConcept(NS+"BasicConcept");
		BasicTypeContainerConcept basicConceptB = new BasicTypeContainerConcept(NS+"BasicConcept");
		
		basicConceptA.setInteger(0);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptA.setInteger(9);
		basicConceptB.setInteger(10);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB.setInteger(9);
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));
	}

	
	@Test
	public void testEqualsDouble() {
		BasicTypeContainerConcept basicConceptA = new BasicTypeContainerConcept(NS+"BasicConcept");
		BasicTypeContainerConcept basicConceptB = new BasicTypeContainerConcept(NS+"BasicConcept");
		
		basicConceptA.setDouble(0.0);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptA.setDouble(1.12345);
		basicConceptB.setDouble(9.87654);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
		
		basicConceptB.setDouble(1.12345);
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));
	}
	
	@Test
	public void testEqualsBoolean() {
		BasicTypeContainerConcept basicConceptA = new BasicTypeContainerConcept(NS+"BasicConcept");
		BasicTypeContainerConcept basicConceptB = new BasicTypeContainerConcept(NS+"BasicConcept");
		
		basicConceptA.setBoolean(false);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
	
		basicConceptB.setBoolean(true);
		assertFalse(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertFalse(Compare.deepEquals(basicConceptA, basicConceptB));
		assertFalse(Compare.deepEquals(basicConceptB, basicConceptA));
	
		basicConceptA.setBoolean(true);
		basicConceptB.setBoolean(true);
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));

		basicConceptA.setBoolean(false);
		basicConceptB.setBoolean(false);
		assertTrue(Compare.shallowEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.shallowEquals(basicConceptB, basicConceptA));
		assertTrue(Compare.deepEquals(basicConceptA, basicConceptB));
		assertTrue(Compare.deepEquals(basicConceptB, basicConceptA));
	}
	
	@Test
    public void testEqualsEnumProperty() {
		EnumContainerConcept enumConceptA = new EnumContainerConcept(NS+"EnumConcept");
		EnumContainerConcept enumConceptB = new EnumContainerConcept(NS+"EnumConcept");
		
		enumConceptA.setPerson(People.MATTIJS);
		enumConceptB.setPerson(null);
		assertFalse(Compare.shallowEquals(enumConceptA, enumConceptB));
		assertFalse(Compare.shallowEquals(enumConceptB, enumConceptA));
		assertFalse(Compare.deepEquals(enumConceptA, enumConceptB));
		assertFalse(Compare.deepEquals(enumConceptB, enumConceptA));
		
		enumConceptB.setPerson(People.GUIDO);
		assertFalse(Compare.shallowEquals(enumConceptA, enumConceptB));
		assertFalse(Compare.shallowEquals(enumConceptB, enumConceptA));
		assertFalse(Compare.deepEquals(enumConceptA, enumConceptB));
		assertFalse(Compare.deepEquals(enumConceptB, enumConceptA));
		
		enumConceptB.setPerson(People.MATTIJS);
		assertTrue(Compare.shallowEquals(enumConceptA, enumConceptB));
		assertTrue(Compare.shallowEquals(enumConceptB, enumConceptA));
		assertTrue(Compare.deepEquals(enumConceptA, enumConceptB));
		assertTrue(Compare.deepEquals(enumConceptB, enumConceptA));
		
    }
	
	public void testEqualsXMLDuration() {
		try {
			XMLContainerConcept xmlConceptA = new XMLContainerConcept(NS+"XMLConcept");
			XMLContainerConcept xmlConceptB = new XMLContainerConcept(NS+"XMLConcept");
			
			DatatypeFactory df = DatatypeFactory.newInstance();
			xmlConceptA.setDuration(df.newDuration(true, 0, 5, 0 ,0 ,0 ,0));
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptB.setDuration(df.newDuration(false, 0, 5, 0 ,0 ,0 ,0));
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptB.setDuration(df.newDuration(true, 0, 0, 0 ,8 ,0 ,0));
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptB.setDuration(df.newDuration(true, 0, 5, 0 ,0 ,0 ,0));
			assertTrue(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertTrue(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertTrue(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertTrue(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
		} catch (DatatypeConfigurationException e) {
			fail(e.getMessage());
		}
		
	}

	public void testEqualsXMLGregorianCalendar() {
		try {
			XMLContainerConcept xmlConceptA = new XMLContainerConcept(NS+"XMLConcept");
			XMLContainerConcept xmlConceptB = new XMLContainerConcept(NS+"XMLConcept");
			
			DatatypeFactory df = DatatypeFactory.newInstance();
			xmlConceptA.setXMLGregorianCalendar(df.newXMLGregorianCalendar());
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptA.setXMLGregorianCalendar(df.newXMLGregorianCalendarDate(
					2011,								
					DatatypeConstants.AUGUST, 
					13,
					DatatypeConstants.FIELD_UNDEFINED));
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptB.setXMLGregorianCalendar(df.newXMLGregorianCalendarDate(
					2011,								
					DatatypeConstants.JUNE, 
					14,
					DatatypeConstants.FIELD_UNDEFINED));
			assertFalse(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertFalse(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertFalse(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
			xmlConceptB.setXMLGregorianCalendar(df.newXMLGregorianCalendarDate(
					2011,								
					DatatypeConstants.AUGUST, 
					13,
					DatatypeConstants.FIELD_UNDEFINED));
			assertTrue(Compare.shallowEquals(xmlConceptA, xmlConceptB));
			assertTrue(Compare.shallowEquals(xmlConceptB, xmlConceptA));
			assertTrue(Compare.deepEquals(xmlConceptA, xmlConceptB));
			assertTrue(Compare.deepEquals(xmlConceptB, xmlConceptA));
			
		} catch (DatatypeConfigurationException e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void testEqualsList() {
		ListContainerConcept device1 = new ListContainerConcept(NS+"ListConcept");
		ListContainerConcept device2 = new ListContainerConcept(NS+"ListConcept");
		
		device1.setList(Arrays.asList("TEST"));
		device2.setList(null);
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));

		device2.setList(new ArrayList<String>());
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));
		
		device2.setList(Arrays.asList("HELLO"));
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));

		device1.setList(Arrays.asList("TEST", "TEST"));
		device2.setList(Arrays.asList("TEST"));
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));
		
		device1.setList(Arrays.asList("TEST", "TEST"));
		device2.setList(Arrays.asList("TEST", null));
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));
		
		device1.setList(Arrays.asList("TEST", "TEST"));
		device2.setList(Arrays.asList("TEST", "HELLO"));
		assertFalse(Compare.shallowEquals(device1, device2));
		assertFalse(Compare.shallowEquals(device2, device1));
		assertFalse(Compare.deepEquals(device1, device2));
		assertFalse(Compare.deepEquals(device2, device1));
		
		device1.setList(Arrays.asList("TEST", "HELLO"));
		device2.setList(Arrays.asList("HELLO", "TEST"));
		assertTrue(Compare.shallowEquals(device1, device2));
		assertTrue(Compare.shallowEquals(device2, device1));
		assertTrue(Compare.deepEquals(device1, device2));
		assertTrue(Compare.deepEquals(device2, device1));

		device1.setList(Arrays.asList("TEST", "HELLO"));
		device2.setList(Arrays.asList("TEST", "HELLO"));
		assertTrue(Compare.shallowEquals(device1, device2));
		assertTrue(Compare.shallowEquals(device2, device1));
		assertTrue(Compare.deepEquals(device1, device2));
		assertTrue(Compare.deepEquals(device2, device1));
	}
}
