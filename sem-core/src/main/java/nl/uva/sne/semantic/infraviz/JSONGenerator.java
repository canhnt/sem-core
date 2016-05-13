package nl.uva.sne.semantic.infraviz;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import nl.uva.sne.semantic.infraviz.IVConcept.getter;

public class JSONGenerator {

	
	public static JSON createJSON(String listName, List<IVConcept> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		JSONArray jsonList = new JSONArray();
		
		for (IVConcept obj : list) {
			JSON sjonNode = createJSON(obj);
			jsonList.add(sjonNode);
		}
		
		map.put(listName, jsonList);
		
		return JSONSerializer.toJSON(map);
	}
	
	public static JSON createJSON(IVConcept object) {
		Map<String, Object> objectMap = new HashMap<String, Object>();
		try {

			Method[] methods = object.getClass().getMethods();
			for (Method method : methods) {
				Annotation getter = method.getAnnotation(getter.class);
				if (getter != null) {
					Object result = method.invoke(object);
					if (result != null) {
						String keyVal = method.getName().substring(3).toLowerCase();
						objectMap.put(keyVal, result);
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
		return JSONSerializer.toJSON(objectMap);
	}
	
}
