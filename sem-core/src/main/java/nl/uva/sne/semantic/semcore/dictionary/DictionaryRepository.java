package nl.uva.sne.semantic.semcore.dictionary;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.uva.sne.semantic.semcore.TopOntologyConcept;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class DictionaryRepository implements ModelDictionary {

	private final List<ModelDictionary> dictionaries;
	
	public DictionaryRepository(List<ModelDictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
	
	private ModelDictionary findDictionary(String nameSpace) throws DictionaryException {
		for (ModelDictionary dictionary : dictionaries) {
			if (dictionary instanceof SimpleDictionary) {
				if (((SimpleDictionary)dictionary).getNameSpace().equals(nameSpace)) {
					return dictionary;
				}
			} else if (dictionary instanceof DictionaryRepository) {
				ModelDictionary result = ((DictionaryRepository)dictionary).findDictionary(nameSpace);
				if (result != null) {
					return result;
				}
			}
		}
		throw new DictionaryException("No dictionary could be found for " + nameSpace);
		
	}
	
	@Override
	public TopOntologyConcept createConcept(URI classType, Value conceptId) throws DictionaryException {
		ModelDictionary dictionary = findDictionary(classType.getNamespace());
		return dictionary.createConcept(classType, conceptId);
	}

	@Override
	public Class<? extends TopOntologyConcept> getClassType(URI classType) throws DictionaryException {
		ModelDictionary dictionary = findDictionary(classType.getNamespace());
		return dictionary.getClassType(classType);
	}

	@Override
	public List<Class<? extends TopOntologyConcept>> getClassConcepts() {
		List<Class<? extends TopOntologyConcept>> result = new ArrayList<Class<? extends TopOntologyConcept>>();
		for (ModelDictionary dictionary : dictionaries) {
			result.addAll(dictionary.getClassConcepts());
		}
		return result;
	}

	@Override
	public int getModelNameOffset(String nameSpace) throws DictionaryException {
		ModelDictionary dictionary = findDictionary(nameSpace);
		return dictionary.getModelNameOffset(nameSpace);
	}

	@Override
	public String getNameSpace(Class<?> classType) throws DictionaryException {
		for (ModelDictionary dictionary : dictionaries) {
			String nameSpace = dictionary.getNameSpace(classType);
			if (nameSpace != null) 
				return nameSpace;
		}
		throw new DictionaryException("No namespace could be found for " + classType.toString());
	}

	@Override
	public SimpleDictionary getSimpleDictionary(String nameSpace) throws DictionaryException {
		for (ModelDictionary dictionary : dictionaries) {
			SimpleDictionary simple = dictionary.getSimpleDictionary(nameSpace);
			if (simple != null) {
				return simple;
			}
		}
		return null;
	}

}
