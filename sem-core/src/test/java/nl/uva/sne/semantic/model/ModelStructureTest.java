/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */

package nl.uva.sne.semantic.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;

import nl.uva.sne.semantic.model.ontology.TopConcept;
import nl.uva.sne.semantic.semcore.TopOntologyConcept;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryRepository;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.junit.Test;

import junit.framework.TestCase;

public class ModelStructureTest extends TestCase {

	
	@Test
	public void testConstructor() {
		ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
		
		List<Class<? extends TopOntologyConcept>> concepts = dictionary.getClassConcepts(); 
	
		Iterator<Class<? extends TopOntologyConcept>> classIterator = concepts.iterator();
		while (classIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Class<? extends TopConcept> imfClass = (Class<? extends TopConcept>) classIterator.next();
			try {
				// every subclass of IMFConcept should have a constructor which accepts a String as argument.
				@SuppressWarnings("unused")
				Constructor<? extends TopConcept> constructor = imfClass.getConstructor(String.class);
			} catch (SecurityException e) {
				fail(e.getMessage());
			} catch (NoSuchMethodException e) {
				fail(e.getMessage());
			}
		}
	}
	
	@Test
	public void testGetterSetter() {
		ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
		
		
		List<Class<? extends TopOntologyConcept>> concepts = dictionary.getClassConcepts(); 
		
		Iterator<Class<? extends TopOntologyConcept>> classIterator = concepts.iterator();
		while (classIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Class<? extends TopConcept> imfClass = (Class<? extends TopConcept>) classIterator.next();
			Method[]  methods = imfClass.getMethods();
			
			for (Method method : methods) {
				
				
				String methodName = method.getName();
				if (methodName.startsWith("get") && !methodName.equals("getClass")) {
					String setterName = "set" + methodName.substring(3);
					Method setter = null;
					for (Method m : methods) {
						if (m.getName().equals(setterName)) {
							setter = m;
							break;
						}
					}
					assertNotNull("no setter found for method "+method.getName()+" in class "+ imfClass.getName(), setter);
					compareGetterAndSetterParameters(method, setter);	
				} else if (methodName.startsWith("set")) {
					String getterName = "get" + methodName.substring(3);
					Method getter = null;
					for (Method m : methods) {
						if (m.getName().equals(getterName)) {
							getter = m;
							break;
						}
					}
					assertNotNull("no getter found for method "+method.getName()+" in class "+ imfClass.getName(), getter);
					compareGetterAndSetterParameters(getter, method);
				}
			}
		}
	}
	
	private void compareGetterAndSetterParameters(Method getter, Method setter) {
		Type getterReturnType = getter.getGenericReturnType();
		assertNotNull("getter " + getter.getName() + " does not return any parameter", getterReturnType);
		
		Type[] setterParameters = setter.getGenericParameterTypes();
		assertTrue("setter " + setter.getName() + " should contain one argument instead of " + setterParameters.length, setterParameters.length == 1);
		
		Type setterParameter = setterParameters[0];
		assertTrue("parameters do not match:" + getterReturnType.toString() +" and "+ setterParameter.toString(), setterParameter.equals(getterReturnType));
	}
	
	
	@Test
	public void testInitialized() {
		ModelDictionary dictionary = new DictionaryRepository(Arrays.asList((ModelDictionary)new BookDictionary(), new TestDictionary()));
		
		List<Class<? extends TopOntologyConcept>> concepts = dictionary.getClassConcepts(); 
		
		Iterator<Class<? extends TopOntologyConcept>> classIterator = concepts.iterator();
		while (classIterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Class<? extends TopConcept> imfClass = (Class<? extends TopConcept>) classIterator.next();
			
			try {
				Constructor<? extends TopConcept> constructor = imfClass.getConstructor(String.class);
				TopConcept imfObject = (TopConcept) constructor.newInstance("http://sne.uva.nl/testObject");
				Method[]  methods = imfClass.getMethods();
				for (Method method : methods) {
					if (method.getName().startsWith("get")) {
						Class<?> returnClass = method.getReturnType();
						if (returnClass.isAssignableFrom(List.class)) {
							Object list = method.invoke(imfObject);
							assertNotNull("result of " + method.getName() + " in class " + imfClass.getName() + " was not initialized",list);
						} else if (returnClass.isAssignableFrom(Map.class)) {
							Object map = method.invoke(imfObject);
							assertNotNull("result of " + method.getName() + " in class " + imfClass.getName() + " was not initialized",map);
						}
					}
				}
			} catch (java.lang.InstantiationException e) {
				fail(imfClass.getName() + " is probably an abstract class and should not be in IMFDictionary");
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage() + " when loading getter of class "+imfClass.getName());
			}
		}
		
	}
	
}
