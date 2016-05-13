/**
 * Copyright (c) 2011, University of Amsterdam
 * All rights reserved according to BSD 2-clause license. 
 * For full text see http://staff.science.uva.nl/~mattijs/LICENSE
 * 
 * @author Mattijs Ghijsen (m.ghijsen@uva.nl) 
 * 
 * 
 */
package nl.uva.sne.semantic.semcore.compare;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.semantic.semcore.TopOntologyConcept;


public class Compare {

	/**
	 * Compares two objects by first comparing the class and id of the objects.
	 * Next the other attributes of the objects are compared. In the case the attribute
	 * is a Java object, the java.lang.Object.equals(Object object) is used. In the case
	 * the attribute is a subclass of IMFConcept, the id's of the objects are compared.
	 * 
	 * To obtain the attributes of an object, shallowEquals uses reflection to obtain all "getters"
	 * and invoke those methods on the two objects.
	 *
	 * @param obj1 the obj1
	 * @param obj2 the obj2
	 * @return true if the two objects are equal
	 */
	public static boolean shallowEquals(TopOntologyConcept obj1, TopOntologyConcept obj2) {
		if (obj1==null && obj2==null) {
			return true;
		} else if (obj1 == null || obj2==null || !obj1.getClass().equals(obj2.getClass())) {
			return false;
		} else {
			List<Pair> found = new ArrayList<Pair>();
			List<Pair> explore = new ArrayList<Pair>();
			
			explore.add(new SimplePair(obj1, obj2));
			while (explore.size() > 0) {
				Pair pair = explore.get(0);
				explore.remove(0);
				found.add(pair);
				
				if (pair instanceof SimplePair) {
					if (!shallowEvaluateSimplePair((SimplePair) pair, explore, found)) {
						return false;
					}
				} else if (pair instanceof ListPair) {
					if (!shallowEvaluateListPair((ListPair) pair, explore, found)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Shallow evaluate list pair.
	 *
	 * @param pair the pair
	 * @param explore the explore
	 * @param found the found
	 * @return true, if successful
	 */
	private static boolean shallowEvaluateListPair(ListPair pair, List<Pair> explore, List<Pair> found) {
		// compare the id's in both lists,
		if (pair.list1.size() != pair.list2.size()) {
			return false;
		}
		// every time two id's match, the pair will be added to the explore list for further investigation
		List<?> list2copy = new ArrayList<Object>(pair.list2);
		for (Object obj1 : pair.list1) {	
			if (obj1 instanceof TopOntologyConcept) {
				TopOntologyConcept target = (TopOntologyConcept)obj1;
				int index = -1;
				// find the index of obj1 in list2copy
				for (int i = 0; i < list2copy.size(); i++){
					Object obj = list2copy.get(i);
					if (obj instanceof TopOntologyConcept) {
						TopOntologyConcept concept = (TopOntologyConcept) obj;
						if (concept.getId().equals(target.getId())) {
							index = i;
							break;
						}
					}
				}
				if (index == -1) {
					return false;
				} else {
					list2copy.remove(index);
				}
			} else {
				if (!list2copy.contains(obj1)) {
					return false;
				} else {
					list2copy.remove(obj1);
				}
			}
		}
		return true;
	}

	/**
	 * Shallow evaluate simple pair.
	 *
	 * @param pair the pair
	 * @param explore the explore
	 * @param found the found
	 * @return true, if successful
	 */
	private static boolean shallowEvaluateSimplePair(SimplePair pair, List<Pair> explore, List<Pair> found) {
		Method[] methods = ((SimplePair) pair).concept1.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				try {
					Object result1 = method.invoke(pair.concept1);
					Object result2 = method.invoke(pair.concept2);
					
					if (!(result1 == null && result2 == null)) {
						if (result1 == null || result2==null) {
							return false;
						} else if (result1 instanceof List<?> && result2 instanceof List<?>) {
							Pair resultPair = new ListPair((List<?>)result1,(List<?>) result2);
							if (!found.contains(resultPair) && !explore.contains(resultPair)) {
								explore.add(resultPair);
							}
						} else if (result1 instanceof TopOntologyConcept && result2 instanceof TopOntologyConcept) {
							TopOntologyConcept concept1 = (TopOntologyConcept)result1;
							TopOntologyConcept concept2 = (TopOntologyConcept)result2;
							if (!concept1.getId().equals(concept2.getId())) {
								return false;
							}
						} else {
							if (!result1.equals(result2)) {
								return false;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	/**
	 * Compares two objects by first comparing the class and id of the objects. 
	 * Next the other attributes of the objects are compared. In the case the attribute
	 * is a Java object, the java.lang.Object.equals(Object object) is used. In the case
	 * the attribute is a subclass of IMFConcept, all attributes of the IMFConcept attribute
	 * are compared. This process of comparing IMFConcept continues until all objects
	 * connected (directly and indirectly) to the two root objects have been compared.
	 *  
	 * To obtain the attributes of an object, shallowEquals uses reflection to obtain all "getters"
     * and invoke those methods on the two objects.
	 * 
	 * @param obj1
	 * @param obj2
	 * @return true if the two objects are equal
	 */
	public static boolean deepEquals(TopOntologyConcept obj1, TopOntologyConcept obj2) {
		if (obj1==null && obj2==null) {
			return true;
		} else if (obj1 == null || obj2==null || !obj1.getClass().equals(obj2.getClass())) {
			return false;
		} else {
			List<Pair> found = new ArrayList<Pair>();
			List<Pair> explore = new ArrayList<Pair>();
			
			explore.add(new SimplePair(obj1, obj2));
			while (explore.size() > 0) {
				Pair pair = explore.get(0);
				explore.remove(0);
				found.add(pair);
				
				if (pair instanceof SimplePair) {
					if (!deepEvaluateSimplePair((SimplePair) pair, explore, found)) {
						return false;
					}
				} else if (pair instanceof ListPair) {
					if (!deepEvaluateListPair((ListPair) pair, explore, found)) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Deep evaluate list pair.
	 *
	 * @param pair the pair
	 * @param explore the explore
	 * @param found the found
	 * @return true, if successful
	 */
	private static boolean deepEvaluateListPair(ListPair pair, List<Pair> explore, List<Pair> found) {
		// compare the id's in both lists,
		if (pair.list1.size() != pair.list2.size()) {
			return false;
		}
		// every time two id's match, the pair will be added to the explore list for further investigation
		List<?> list2copy = new ArrayList<Object>(pair.list2);
		for (Object obj1 : pair.list1) {	
			if (obj1 instanceof TopOntologyConcept) {
				TopOntologyConcept target = (TopOntologyConcept)obj1;
				int index = -1;
				// find the index of obj1 in list2copy
				for (int i = 0; i < list2copy.size(); i++){
					Object obj = list2copy.get(i);
					if (obj instanceof TopOntologyConcept) {
						TopOntologyConcept concept = (TopOntologyConcept) obj;
						if (concept.getId().equals(target.getId())) {
							index = i;
							break;
						}
					}
				}
				if (index == -1) {
					return false;
				} else {
					Pair simplePair = new SimplePair(target, (TopOntologyConcept) list2copy.get(index));
					if (!found.contains(simplePair) && !explore.contains(simplePair)) {
						explore.add(simplePair);
					}
					list2copy.remove(index);
				}
			} else {
				if (!list2copy.contains(obj1)) {
					return false;
				} else {
					list2copy.remove(obj1);
				}
			}
		}
		return true;
	}

	/**
	 * Deep evaluate simple pair.
	 *
	 * @param pair the pair
	 * @param explore the explore
	 * @param found the found
	 * @return true, if successful
	 */
	private static boolean deepEvaluateSimplePair(SimplePair pair, List<Pair> explore, List<Pair> found) {
		Method[] methods = ((SimplePair) pair).concept1.getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("get")) {
				try {
					Object result1 = method.invoke(pair.concept1);
					Object result2 = method.invoke(pair.concept2);
					
					if (!(result1 == null && result2 == null)) {
						if (result1 == null || result2==null) {
							return false;
						} else if (result1 instanceof List<?> && result2 instanceof List<?>) {
							Pair resultPair = new ListPair((List<?>)result1,(List<?>) result2);
							if (!found.contains(resultPair) && !explore.contains(resultPair)) {
								explore.add(resultPair);
							}
						} else if (result1 instanceof TopOntologyConcept && result2 instanceof TopOntologyConcept) {
							Pair resultPair = new SimplePair((TopOntologyConcept)result1, (TopOntologyConcept)result2);
							if (!found.contains(resultPair) && !explore.contains(resultPair)) {
								explore.add(resultPair);
							}
						} else {
							if (!result1.equals(result2)) {
								return false;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private static interface Pair {
		
	}
	
	private static class SimplePair implements Pair {
		
		public final TopOntologyConcept concept1;
		public final TopOntologyConcept concept2;
		
		public SimplePair(TopOntologyConcept concept1, TopOntologyConcept concept2) {
			this.concept1 = concept1;
			this.concept2 = concept2;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof SimplePair) {
				SimplePair pair = (SimplePair) obj;
				return (this.concept1.equals(pair.concept1) &&
					    this.concept2.equals(pair.concept2)) ||
					   (this.concept1.equals(pair.concept2) &&
					   	this.concept2.equals(pair.concept1));
			} else {
				return false;
			}
		}
	}
	
	private static class ListPair implements Pair {
		
		public final List<?> list1;
		public final List<?> list2;
		
		public ListPair(List<?> list1, List<?> list2) {
			this.list1 = list1;
			this.list2 = list2;
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof ListPair) {
				ListPair pair = (ListPair) obj;
				return (this.list1.equals(pair.list1) &&
					    this.list2.equals(pair.list2)) ||
					   (this.list1.equals(pair.list2) &&
					   	this.list2.equals(pair.list1));
			} else {
				return false;
			}
		}
	}
}
