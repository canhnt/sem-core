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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import nl.uva.sne.semantic.model.map.MAP;
import nl.uva.sne.semantic.semcore.TopOntologyConcept.Predicate;
import nl.uva.sne.semantic.semcore.dictionary.DictionaryException;
import nl.uva.sne.semantic.semcore.dictionary.ModelDictionary;
import nl.uva.sne.semantic.semcore.dictionary.SimpleDictionary;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;

/**
 * The Class JavaToTripleConvertor.
 */
public class JavaToTripleConvertor {

	private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JavaToTripleConvertor.class);
	
	private static List<TopOntologyConcept> explore(Object object, List<String> foundIDs, boolean crawl) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ConversionException {
		ArrayList<TopOntologyConcept> result = new ArrayList<TopOntologyConcept>();
		if (object instanceof TopOntologyConcept) {
			TopOntologyConcept relatedConcept = (TopOntologyConcept) object;
			if (!foundIDs.contains(relatedConcept.getId())) {
				foundIDs.add(relatedConcept.getId());
				result.add(relatedConcept);
				if (crawl) {
					result.addAll(findRelatedConcepts(relatedConcept, foundIDs, crawl));
				}
			}
		} else {
			result.addAll(findRelatedConcepts(object, foundIDs, crawl));
		}
		return result;
	}
	
	protected static List<TopOntologyConcept> findRelatedConcepts(Object concept, List<String> foundIDs, boolean crawl) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ConversionException {
		ArrayList<TopOntologyConcept> result = new ArrayList<TopOntologyConcept>();
		
		if (concept instanceof TopOntologyConcept) {
			Method[] methods = concept.getClass().getMethods();
			for (Method method : methods) {
				Annotation ann = method.getAnnotation(Predicate.class);
				if (ann != null && ((Predicate)ann).function().equals("getter")) {
					Object obj = method.invoke(concept);
					result.addAll(explore(obj, foundIDs, crawl));
				} else if (ann == null && method.getName().startsWith("get")) {
					Object obj = method.invoke(concept);
					result.addAll(explore(obj, foundIDs, crawl));
				}
			}
		} else if (concept instanceof List<?>) {
			List<?> list = (List<?>) concept;
			for (Object element : list) {
				result.addAll(explore(element, foundIDs, crawl));
			}
		} else if (concept instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) concept;
			Set<?> keySet = map.keySet();
			Iterator<?> keySetIterator = keySet.iterator();
			while (keySetIterator.hasNext()) {
				Object key = keySetIterator.next();
				result.addAll(explore(key, foundIDs, crawl));
				result.addAll(explore(map.get(key), foundIDs, crawl));
			}
		}
		return result;
	}
	
	protected static List<Statement> encodeToTriple(TopOntologyConcept concept, ModelDictionary dictionary, boolean crawl) throws ConversionException, IllegalAccessException, InvocationTargetException {
		ArrayList<Statement> result = new ArrayList<Statement>();
		
		URI subjectURI;
		if (concept.getId()!=null) {
			try {
				subjectURI = new URIImpl(concept.getId());	
			} catch (java.lang.IllegalArgumentException e) {
				throw new ConversionException("cannot convert object to triples. "+concept.getId() + " is not a valid URI");
			}
		} else {
			throw new ConversionException("cannot convert object to triples when id is null.");
		}
		
		Method[] methods = concept.getClass().getMethods();
		
		Statement individualTriple = new StatementImpl(subjectURI, RDF.TYPE, OWL.INDIVIDUAL);
		result.add(individualTriple);
		String nameSpace = dictionary.getNameSpace(concept.getClass());
		if (nameSpace==null) {
			throw new ConversionException("could not find namespace for concept class:"+concept.getClass().toString());
		}
		int offset = dictionary.getModelNameOffset(nameSpace);
		String className = concept.getClass().getSimpleName().substring(offset);
		
		URI typeURI = null;
		try {
			typeURI = new URIImpl(nameSpace + className);
		} catch (java.lang.IllegalArgumentException e) {
			throw new ConversionException("cannot convert object type to triples. " + nameSpace + className + " is not a valid URI");
		}
		Statement typeTriple = new StatementImpl(subjectURI, RDF.TYPE, typeURI);
		result.add(typeTriple);
		
		
		if (crawl) {
			for (Method method : methods) {
				
				boolean getter;
				URI predicateURI = null;
				
				Annotation annotation = method.getAnnotation(Predicate.class);
				if (annotation != null && annotation instanceof Predicate) {
					Predicate predicateAnn = (Predicate) annotation;
					if (predicateAnn.function().equals("getter")) {
						getter = true;
						try {
							predicateURI = new URIImpl(predicateAnn.predicateName());
						} catch (java.lang.IllegalArgumentException e) {
							throw new ConversionException("cannot convert argument type to triples. " + predicateAnn.predicateName() + " is not a valid URI");
						}
						log.debug("predicate getter found with URI:"+predicateURI.toString());
					} else {
						getter = false;
					}
				} else if (method.getName().startsWith("get")) {
					if (method.getName().startsWith("getClass") || method.getName().startsWith("getId")) {
						getter = false;
					} else {
						getter = true;
						String predicateURIString = nameSpace + "has" + method.getName().substring(3);
						try {
							predicateURI = new URIImpl(predicateURIString);
						} catch (java.lang.IllegalArgumentException e) {
							throw new ConversionException("cannot convert argument type to triples. " + predicateURIString + " is not a valid URI");
						}
					}
				} else {
					getter = false;
				}
				if (getter) {
					Object object = method.invoke(concept);
					if (object !=null) {
						if (object instanceof List<?>) {
							List<?> list = (List<?>) object;
							for (Object element : list) {
								if (element != null) {
									result.addAll(createTriplesForObject(subjectURI, predicateURI, element, dictionary));
								} else {
									log.warn("Null element in list! Cannot generate triples for null element.");
								}
							}
						} else if (object instanceof Map<?, ?>) {
							Map<?, ?> map = (Map<?, ?>) object;
							Set<?> keySet = map.keySet();
							
							List<Statement> keyTriples = new ArrayList<Statement>();
							List<Statement> valueTriples = new ArrayList<Statement>();
							
							String mapNameURI = subjectURI.getNamespace() + method.getName().substring(3)+"Map";
							URI mapName;
							try {
								mapName = new URIImpl(mapNameURI);
							} catch (java.lang.IllegalArgumentException e) {
								throw new ConversionException("cannot convert map name to triples. " + mapNameURI + " is not a valid URI");
							}
							
							Statement mapTriple = new StatementImpl(subjectURI, predicateURI, mapName);
							Statement mapIndividualTriple = new StatementImpl(mapName, RDF.TYPE, OWL.INDIVIDUAL);
							Statement mapTypeTriple = new StatementImpl(mapName, RDF.TYPE, MAP.MAP);
							
							result.add(mapTriple);
							result.add(mapIndividualTriple);
							result.add(mapTypeTriple);
							
							Iterator<?> keyIterator = keySet.iterator();
							while (keyIterator.hasNext()) {
								Object mapKey = keyIterator.next();
								String keyLocalName = getObjectAsValue(mapKey, dictionary).stringValue();
								if (!(mapKey instanceof TopOntologyConcept)) {
									// make sure the key is associated with this map (key with same name could be in another map)
									keyLocalName = mapName.getLocalName() + keyLocalName;
								}
								String keyNamespace = subjectURI.getNamespace();
								URI keyName;
								try {
									keyName = new URIImpl(keyNamespace + keyLocalName);
								} catch (java.lang.IllegalArgumentException e) {
									throw new ConversionException("cannot convert map key to triples. " + keyNamespace + keyLocalName + " is not a valid URI");
								}
								Statement keyTriple = new StatementImpl(mapName, MAP.HAS_MAPKEY, keyName);
								Statement keyIndividualTriple = new StatementImpl(keyName, RDF.TYPE, OWL.INDIVIDUAL);
								Statement keyTypeTriple = new StatementImpl(keyName, RDF.TYPE, MAP.MAPKEY);
								
								keyTriples.add(keyTriple);
								valueTriples.add(keyIndividualTriple);
								valueTriples.add(keyTypeTriple);
								
								Object mapValue = map.get(mapKey);
								if (mapValue instanceof List<?> ) {
									List<?> list = (List<?>) mapValue;
									for (Object element : list) {
										Value mapValueValue = getObjectAsValue(element, dictionary);
										Statement valueTriple = new StatementImpl(keyName, MAP.HAS_MAPVALUE, mapValueValue);
										valueTriples.add(valueTriple);
									}
								} else {
									Value mapValueValue = getObjectAsValue(mapValue, dictionary);
									Statement valueTriple = new StatementImpl(keyName, MAP.HAS_MAPVALUE, mapValueValue);
									valueTriples.add(valueTriple);
								}
							}
							result.addAll(valueTriples);
							result.addAll(keyTriples);
							
							
							
						} else {
							result.addAll(createTriplesForObject(subjectURI, predicateURI, object, dictionary));
						}
					}
				}
			}
		}
		
		for (Statement statement : result) {
			log.debug("created triple: " + statement.toString());
		}
		
		return result;
	}
	
	/**
	 * Creates the triples for object.
	 *
	 * @param subjectURI the subject uri
	 * @param predicateURI the predicate uri
	 * @param object the object
	 * @param dictionary the dictionary
	 * @return the list
	 * @throws ConversionException 
	 */
	private static List<Statement> createTriplesForObject(Resource subjectURI, URI predicateURI, Object object, ModelDictionary dictionary) throws ConversionException {
		List<Statement> result = new ArrayList<Statement>();
		
		Value objectURI = getObjectAsValue(object, dictionary);
		Statement triple = new StatementImpl(subjectURI, predicateURI, objectURI);
		result.add(triple);
		
		return result;
	}

	private static Value getObjectAsValue(Object object, ModelDictionary dictionary) throws ConversionException {
		if (object instanceof TopOntologyConcept) {
			TopOntologyConcept myConcept = (TopOntologyConcept) object;
			if (myConcept.getId() != null) {
				return new URIImpl(myConcept.getId());
			} else {
				throw new ConversionException("id of object: " + myConcept.getClass().getName() + " was not set.");
			}
		} else if (object instanceof Point2D.Double) {
			Point2D.Double point = (Point2D.Double) object;
			return new LiteralImpl(point.getX()+","+point.getY());
		} else if (object.getClass().isEnum()) {
			return new URIImpl(dictionary.getNameSpace(object.getClass()) + object.toString());
		} else if (object instanceof XMLGregorianCalendar) {
			XMLGregorianCalendar dateTime = (XMLGregorianCalendar) object;
			return new LiteralImpl(dateTime.toXMLFormat());
		} else {
			return new LiteralImpl(object.toString());
		}
	}

	
	
}
