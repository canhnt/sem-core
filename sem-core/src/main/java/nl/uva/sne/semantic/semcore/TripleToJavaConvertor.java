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

import java.awt.geom.Point2D;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.uva.sne.semantic.model.map.MAP;
import nl.uva.sne.semantic.semcore.TopOntologyConcept.Predicate;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryException;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;


/**
 * The Class TripleToJavaConvertor.
 */
public abstract class TripleToJavaConvertor {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TripleToJavaConvertor.class);
	
	private static final URI NAMEDINDIVIDUAL = new URIImpl("http://www.w3.org/2002/07/owl#NamedIndividual");
	
	/**
	 * Triples to java.
	 *
	 * @param triples the triples
	 * @param dictionary the dictionary
	 * @return the collection
	 * @throws DictionaryException the dictionary exception
	 * @throws ConversionException the conversion exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws NoSuchFieldException 
	 */
	protected static Collection<TopOntologyConcept> triplesToJava(Collection<Statement> triples, ModelDictionary dictionary) throws DictionaryException, ConversionException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DatatypeConfigurationException, URISyntaxException, NoSuchFieldException {
		
		log.debug("Conversion phase 1 starting.");
		
		Map<String, TopOntologyConcept> concepts = new Hashtable<String, TopOntologyConcept>();

		List<Statement> mapTriples = new ArrayList<Statement>();
		
		Iterator<Statement> statementIterator = triples.iterator();
		while (statementIterator.hasNext()) {
			Statement triple = statementIterator.next();
			if (triple.getPredicate().equals(RDF.TYPE)) {
				if (triple.getObject().equals(MAP.MAPKEY) || triple.getObject().equals(MAP.MAP)) {
					mapTriples.add(triple);
				} else if (!(triple.getObject().equals(OWL.INDIVIDUAL) || triple.getObject().equals(NAMEDINDIVIDUAL))) {
					log.debug("converting triple to java:" + triple.toString());
					TopOntologyConcept concept = dictionary.createConcept((URI)triple.getObject(), triple.getSubject());
					log.debug("created concept:"+concept.getClass().getCanonicalName()+" "+concept.getId()); 
					concepts.put(concept.getId(), concept); 
				}
			} else if (triple.getPredicate().equals(MAP.HAS_MAPKEY)) {
				mapTriples.add(triple);
			} else if (triple.getPredicate().equals(MAP.HAS_MAPVALUE)) {
				mapTriples.add(triple);
			}
		}
		statementIterator = mapTriples.iterator();
		while (statementIterator.hasNext()) {
			log.debug("mapTriple found:"+statementIterator.next().toString());
		}
		triples.removeAll(mapTriples);
		
		log.debug("Conversion phase 2 starting.");
		statementIterator = triples.iterator();
		while (statementIterator.hasNext()) {
			Statement triple = statementIterator.next();
			if (triple.getPredicate().equals(OWL.SAMEAS)) {
				log.warn("ignoring owl:sameas triple:"+triple.toString());
			} else if (!triple.getPredicate().equals(RDF.TYPE)) {
				log.debug("converting triple to java:" + triple.toString());
				TripleToJavaConvertor.fillConcept(triple, concepts, mapTriples);
			} 
		}
		
		return concepts.values();
	}
	

	/*
	private static void fillMapWrapperConcept(MapWrapper<?, ?> mapWrapper, List<Statement> mapTriples, Map<String, TopOntologyConcept> concepts) throws SecurityException, NoSuchFieldException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, DictionaryException, ConversionException, InstantiationException, DatatypeConfigurationException, URISyntaxException {
		// find all the keys for this mapWrapper
		URI mapWrapperURI = new URIImpl(mapWrapper.getId());
		
		@SuppressWarnings("unchecked")
		Map<Object, Object> map = (Map<Object, Object>) mapWrapper.getMap();
		List<Statement> usedTriples = new ArrayList<Statement>();
		for (Statement keyTriple : mapTriples) {
			if (keyTriple.getSubject().equals(mapWrapperURI) && keyTriple.getPredicate().equals(MAP.HAS_MAPKEY)) {
				usedTriples.add(keyTriple);
				// use the map to determine the key type
				Method method = mapWrapper.getClass().getMethod("getMap");
				if (method.getGenericReturnType() instanceof ParameterizedType) {
					ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
					Type[] actualTypes = returnType.getActualTypeArguments();
					if (actualTypes.length == 2) {
						Type keyType = actualTypes[0];
						Object key = getJavaObject(keyTriple.getObject(), (Class<?>) keyType, concepts);
						
						Type valueType = actualTypes[1];
						log.debug("keyType " + keyType +" valueType "+valueType);
						
						for (Statement valueTriple : mapTriples) {
							if (valueTriple.getSubject().equals(keyTriple.getObject()) && valueTriple.getPredicate().equals(MAP.HAS_MAPVALUE)) {
								if (isParameterizedListType(valueType)) {
									Type listArgType = getListArgumentType((ParameterizedType) valueType);
									Object value = getJavaObject(valueTriple.getObject(), (Class<?>) listArgType, concepts);
									@SuppressWarnings("unchecked")
									List<Object> list = (List<Object>) map.get(key);
									if (list == null) {
										list = new ArrayList<Object>();
									}
									list.add(value);
									map.put(key, list);
								} else if (isParameterizedMapType(valueType)) {
									Type listArgType = getListArgumentType((ParameterizedType) valueType);
									
								}
								
								Type listType = getListType(valueType);
								if (listType == null) {
									Object value = getJavaObject(valueTriple.getObject(), (Class<?>) valueType, concepts);
									map.put(key, value);
								} else {
									
								}
							}
						}
					}
				}
			}
		}
		mapTriples.removeAll(usedTriples);
		
	}
	*/
	
	private static boolean isParameterizedListType(Type type) {
		if (type instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) type).getRawType();
			return List.class.isAssignableFrom((Class<?>) rawType);
		}
		return false;
	}
	
	private static boolean isParameterizedMapType(Type type) {
		if (type instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType) type).getRawType();
			return Map.class.isAssignableFrom((Class<?>) rawType);
		}
		return false;
	}
	
	private static Type getListArgumentType(ParameterizedType type) throws ConversionException {
		Type[] actualTypeArgs = type.getActualTypeArguments();
		if (actualTypeArgs.length == 1) {
			// handle wildcard types
			if (actualTypeArgs[0] instanceof WildcardType) {
				WildcardType actualParType = (WildcardType) actualTypeArgs[0];
				Type[] upper = actualParType.getUpperBounds();
				if (upper.length == 1) {
					return upper[0];
				} else {
					throw new ConversionException("Unexpected amount of arguments in upper bound of wildcard type");
				}
			} else {
				return actualTypeArgs[0];
			}
		} else {
			throw new ConversionException("Unexpected amount of arguments in actual argument types in List<>");
		}
	}
	
	private static Type getMapKeyArgumentType(ParameterizedType type) throws ConversionException {
		Type[] actualTypeArgs = type.getActualTypeArguments();
		if (actualTypeArgs.length == 2) {
			// handle wildcard types
			if (actualTypeArgs[0] instanceof WildcardType) {
				WildcardType actualParType = (WildcardType) actualTypeArgs[0];
				Type[] upper = actualParType.getUpperBounds();
				if (upper.length == 1) {
					return upper[0];
				} else {
					throw new ConversionException("Unexpected amount of arguments in upper bound of wildcard type");
				}
			} else {
				return actualTypeArgs[0];
			}
		} else {
			throw new ConversionException("Unexpected amount of arguments in actual argument types in Map<>");
		}
	}
	
	private static Type getMapValueArgumentType(ParameterizedType type) throws ConversionException {
		Type[] actualTypeArgs = type.getActualTypeArguments();
		if (actualTypeArgs.length == 2) {
			// handle wildcard types
			if (actualTypeArgs[1] instanceof WildcardType) {
				WildcardType actualParType = (WildcardType) actualTypeArgs[1];
				Type[] upper = actualParType.getUpperBounds();
				if (upper.length == 1) {
					return upper[0];
				} else {
					throw new ConversionException("Unexpected amount of arguments in upper bound of wildcard type");
				}
			} else {
				return actualTypeArgs[1];
			}
		} else {
			throw new ConversionException("Unexpected amount of arguments in actual argument types in Map<>");
		}
	}
	


	/**
	 * Fill concept.
	 *
	 * @param conceptId the concept id
	 * @param triple the triple
	 * @param concepts the concepts
	 * @param mapTriples 
	 * @throws ConversionException the conversion exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws DictionaryException the dictionary exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	@SuppressWarnings("unchecked")
	protected static void fillConcept(Statement triple, Map<String, TopOntologyConcept> concepts, List<Statement> mapTriples) throws ConversionException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DictionaryException, DatatypeConfigurationException, URISyntaxException {
		
		String conceptId = triple.getSubject().stringValue();
		TopOntologyConcept concept = concepts.get(conceptId);
		
		if (concept == null) {
			throw new ConversionException("Concept with id: "+conceptId+" could not be found in concepts Map.");
		}
		
		Method setter = null;
		Method getter = null;
		
		// try to find the getter and setter based on annotations
		Method[] methods = concept.getClass().getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Predicate) {
					Predicate predicateAnn = (Predicate) annotation;
					if (predicateAnn.predicateName().equals(triple.getPredicate().toString())) {
						log.debug("annotation found:"+annotation);
						if (predicateAnn.function().equals("getter")) {
							getter = method;
						} else if (predicateAnn.function().equals("setter")) {
							setter = method;
						}
					}
				}
			}
		}
		
		// if no matching annotation could be found, try to find the getter and setter based on method name
		if (setter == null) {
			String setterName = "set"+triple.getPredicate().getLocalName().substring(3);
			for (Method method : methods) {
				if (method.getName().equals(setterName)) setter = method;
			}
		}
		if (getter == null) {
			String getterName = "get"+triple.getPredicate().getLocalName().substring(3);
			for (Method method : methods) {
				if (method.getName().equals(getterName)) getter = method;
			}
		}
		if (setter==null) throw new ConversionException("No setter could be found when filling object " + concept.getId() + "  with triple: " + triple.toString());
		if (getter==null) throw new ConversionException("No getter could be found when filling object " + concept.getId() + " with triple: " + triple.toString());
		
		// check which argument type is expected by the setter
		Type[] parameterTypes = setter.getGenericParameterTypes();
		if (parameterTypes.length != 1) {
			throw new ConversionException("no target class found for setter:"+setter.getName());
		} else {
			Type argType = parameterTypes[0];
			if (isParameterizedListType(argType)) {
				// invoke the getter to get the list and add the argument
				Type actualArgType = getListArgumentType((ParameterizedType) argType);
				Object arg = getJavaObject(triple.getObject(), (Class<?>) actualArgType, concepts);
				List<Object> myList = (List<Object>) getter.invoke(concept);
				myList.add(arg);
				setter.invoke(concept, myList);
			} else if (isParameterizedMapType(argType)) {
				Type actualKeyType = getMapKeyArgumentType((ParameterizedType) argType);
				Type actualValueType = getMapValueArgumentType((ParameterizedType) argType);
				
				Map<Object, Object> map = (Map<Object, Object>) getter.invoke(concept);
				
				List<Statement> keyTriples = getKeyTriples(triple.getObject().stringValue(), mapTriples);
				mapTriples.removeAll(keyTriples);
				for (Statement keyTriple : keyTriples) {
					log.debug("filling map with key triple:" + keyTriple.toString());
					Value keyValue = keyTriple.getObject();
					if (! (TopOntologyConcept.class.isAssignableFrom((Class<?>) actualKeyType))) {
						if (keyTriple.getObject() instanceof URI && triple.getObject() instanceof URI) {
							String keyName = ((URI)keyTriple.getObject()).getLocalName();
							String mapName = ((URI)triple.getObject()).getLocalName();
							keyValue = new LiteralImpl(keyName.substring(mapName.length()));
							log.debug("reconstructed key:" + keyValue.toString());
						} else {
							throw new ConversionException("Expecting URI as map and/or key value");
						}
					}
					Object key = getJavaObject(keyValue, (Class<?>) actualKeyType, concepts);
					
					List<Statement> valueTriples = getValueTriples(keyTriple.getObject().stringValue(), mapTriples);
					if (isParameterizedListType(actualValueType)) {
						if (map.get(key) == null) {
							map.put(key, new ArrayList<Object>());
						}
						for (Statement valueTriple : valueTriples ) {
							Type actualListArgType = getListArgumentType((ParameterizedType) actualValueType);
							Object value = getJavaObject(valueTriple.getObject(), (Class<?>) actualListArgType, concepts);
							List<Object> myList = (List<Object>) map.get(key);
							myList.add(value);
							map.put(key, myList);
						}
					} else {
						if (valueTriples.size() == 1) {
							Object value = getJavaObject(valueTriples.get(0).getObject(), (Class<?>) actualValueType, concepts);
							map.put(key, value);
						} else if (valueTriples.size() > 1) {
							throw new ConversionException("Multiple triples found while max. one was expected");
						}
					}
				}
				
				
			} else {
				// here we assume that getJavaObject is able handle the conversion of argType
				Object arg = getJavaObject(triple.getObject(), (Class<?>) argType, concepts);
				setter.invoke(concept, arg);
			}
		}
	}
	
	private static List<Statement> getValueTriples(String keyName, List<Statement> mapTriples) {
		List<Statement> result = new ArrayList<Statement>();
		
		for (Statement mapTriple : mapTriples) {
			if (mapTriple.getSubject().stringValue().equals(keyName)) {
				if (mapTriple.getPredicate().equals(MAP.HAS_MAPVALUE)) {
					result.add(mapTriple);
				}
			}
		}
		
		return result;
	}


	private static List<Statement> getKeyTriples(String mapName, List<Statement> mapTriples) {
		List<Statement> result = new ArrayList<Statement>();
		
		for (Statement mapTriple : mapTriples) {
			if (mapTriple.getSubject().stringValue().equals(mapName)) {
				if (mapTriple.getPredicate().equals(MAP.HAS_MAPKEY)) {
					result.add(mapTriple);
				}
			}
		}
		
		return result;
	}


	/**
	 * Gets the java object.
	 *
	 * @param value the value
	 * @param targetClass the target class
	 * @param concepts the concepts
	 * @return the java object
	 * @throws ConversionException the conversion exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws DictionaryException the dictionary exception
	 * @throws DatatypeConfigurationException the datatype configuration exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	protected static Object getJavaObject(Value value, Class<?> targetClass, Map<String, TopOntologyConcept> concepts) throws ConversionException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DictionaryException, DatatypeConfigurationException, URISyntaxException {
		String valueString = value.stringValue();
		if (value instanceof URI) {
			log.debug("URI value detected:" + value.stringValue());
			if (TopOntologyConcept.class.isAssignableFrom(targetClass)) {
				return concepts.get(value.stringValue());
			} else {
				// strip the value
				URI uri = (URI) value;
				valueString = uri.getLocalName();
			}
		}
		
		if (Point2D.class.isAssignableFrom(targetClass)) {
			String[] parray = valueString.split(",");
			double x = Double.valueOf(parray[0]);
			double y = Double.valueOf(parray[1]);
			return new Point2D.Double(x,y);
		}
		if (Integer.class.isAssignableFrom(targetClass)) {
			return new Integer(valueString);
		}
		if (Boolean.class.isAssignableFrom(targetClass)) {
			return new Boolean(valueString);	
		}
		if (Double.class.isAssignableFrom(targetClass)) {
			return new Double(valueString);	
		}
		if (XMLGregorianCalendar.class.isAssignableFrom(targetClass)) {
			
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(valueString);
			
		}
		if (Duration.class.isAssignableFrom(targetClass)) {
			return DatatypeFactory.newInstance().newDuration(valueString);
		}
		
		if (java.net.URI.class.isAssignableFrom(targetClass)) {
			return new java.net.URI(valueString);
		}
		
		if (String.class.isAssignableFrom(targetClass)) {
			return valueString;
		}
		
		if (targetClass.isEnum()) {
			Enum<?>[] constants = (Enum[]) targetClass.getEnumConstants();
			for (Enum<?> constant : constants) {
				if (constant.name().equals(valueString)) {
					return constant;
				}
			}
		}
		
		
		
		
		
		throw new ConversionException("could not translate \""+value.stringValue() + "\" to java targetclass: " + targetClass.getCanonicalName());
	}

}
