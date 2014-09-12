/****
 * 
 * Copyright 2013-2014 Wedjaa <http://www.wedjaa.net/>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */


package net.wedjaa.jasper.elasticsearch.resolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.wedjaa.jasper.elasticsearch.ESSearch;

import org.apache.log4j.Logger;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.Facets;

public class FacetResolver {

	private ClassFinder classFinder;

	private static FacetResolver instance = null;

	private static final Logger logger = Logger.getLogger(FacetResolver.class);

	protected FacetResolver() {
		this.classFinder = new ClassFinder("Facet.class", "org.elasticsearch.search.facet");
	}

	public static FacetResolver getInstance() {

		if (instance == null) {
			instance = new FacetResolver();
		}

		return instance;

	}

	private Class<?> getEntriesClass(Class<?> facetClass) {

		if (!classFinder.hasMethod("getEntries", facetClass)) {
			logger.warn("Can't get getEntries method for "
					+ facetClass.getCanonicalName());
			return null;
		}

		return getEntriesClass(classFinder.getMethod("getEntries", facetClass));

	}

	private Class<?> getEntriesClass(Method getEntriesMethod) {

		if (getEntriesMethod == null) {
			logger.warn("Can't get entries class type because of null getEntries method.");
			return null;
		}

		ParameterizedType returnType = (ParameterizedType) getEntriesMethod.getGenericReturnType();
		logger.trace("Analysing method: " + getEntriesMethod);
		logger.trace("Return Type: " + returnType + " ("+returnType.getClass().getCanonicalName()+")");
		Type ret =  returnType.getActualTypeArguments()[0];
		logger.trace("Actual Type: " + ret + " ("+ret.getClass().getCanonicalName()+")");
		
		if ( WildcardType.class.isAssignableFrom(ret.getClass()) ) {
			WildcardType entriesClass = (WildcardType) ret;
			Class<?> entryClass = (Class<?>) entriesClass.getUpperBounds()[0];			
			return entryClass;
		}		
		logger.trace("Method returning Class: " + ret.getClass().getCanonicalName());
		return (Class<?>) ret;
	}
	
	public Class<?> getFacetClass(Facet facet) {
		Class<?> result =  (Class<?>) classFinder.getClassByType(facet.getType());
		if ( result == null) {
			result = facet.getClass();
			logger.trace("Using native class: " + result);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<Class<?>> getEntries(Facet facet) {

		List<Class<?>> entries = null;

		Class<?> facetClass = getFacetClass(facet);

		if (facetClass == null) {
			logger.warn("Can't get entries for unknown facet type: "
					+ facet.getType());
			return null;
		}

		Method getEntriesMethod = classFinder.getMethod("getEntries", facetClass);
		if (getEntriesMethod == null) {
			logger.warn("Seems that " + facetClass.getCanonicalName()
					+ " has no getEntries method. Funny you should call it.");
			return null;
		}

		try {
			entries = (List<Class<?>>) getEntriesMethod.invoke(facet);
			if (entries.size() > 0) {
				logger.debug("Returning " + entries.size() + " entries.");
			}
		} catch (IllegalAccessException e) {
			logger.warn("Failed to get entries on facet of type "
					+ facet.getType() + ": " + e.getLocalizedMessage());
		} catch (IllegalArgumentException e) {
			logger.warn("Failed to get entries on facet of type "
					+ facet.getType() + ": " + e.getLocalizedMessage());
		} catch (InvocationTargetException e) {
			logger.warn("Failed to get entries on facet of type "
					+ facet.getType() + ": " + e.getLocalizedMessage());
		}

		return entries;
	}



	private Map<String, Object> createEntryMap(Facet facet, Object entry,
			Class<?> entryClass) {

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("FacetName", facet.getName());
		result.put("FacetType", facet.getType());

		List<Method> entryMethods = classFinder.getClassMethods(entryClass);

		for (Method method : entryMethods) {
			if (method.getName().startsWith("get")) {
				String key = method.getName().substring(3);
				try {
					Object value = method.invoke(entry);
					if (value != null && !value.toString().equals("NaN")) {
						logger.debug("   entry[" + key + "] = " + value + " - "
								+ value.getClass());
						result.put(key, value);
					}
				} catch (IllegalAccessException e) {
					logger.warn("Failed to execute method " + method.getName()
							+ " on entry: " + entry);
				} catch (IllegalArgumentException e) {
					logger.warn("Failed to execute method " + method.getName()
							+ " on entry: " + entry);
				} catch (InvocationTargetException e) {
					logger.warn("Failed to execute method " + method.getName()
							+ " on entry: " + entry);
				}
			}
		}

		return result;
	}

	public List<Map<String, Object>> unrollSimpleFacet(Facet facet) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Class<?> facetClass = getFacetClass(facet);
		result.add(createEntryMap(facet, facet, facetClass));
		return result;
	}

	public List<Map<String, Object>> unrollFacetEntries(Facet facet) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		// Find out the type of entries on this facet
		Class<?> facetClass = getFacetClass(facet);
		Class<?> entryClass = getEntriesClass(facetClass);

		// Get the list of entries
		List<Class<?>> entries = getEntries(facet);

		if (entries != null) {
			for (Object entry : entries) {
				Map<String, Object> entryMap = createEntryMap(facet, entry,
						entryClass);
				result.add(entryMap);
			}
		}

		return result;
	}

	public List<Map<String, Object>> unrollFacet(Facet facet) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		Class<?> facetClass = getFacetClass(facet);

		if (facetClass == null) {
			logger.warn("Failed to get a facet class for type: "
					+ facet.getType());
			return result;
		}

		logger.debug("Class for facet type '" + facet.getType() + "': "
				+ facetClass.getCanonicalName());
		
		if ( classFinder.getClassMethods(facetClass) == null ) {
			logger.warn("Failed to get methods for facet class "
					+ facetClass.getCanonicalName());
			return result;
		}

		if (classFinder.hasMethod("getEntries", facetClass)) {
			logger.debug("Is a collection of facets, unrolling it");
			return unrollFacetEntries(facet);

		}
		logger.debug("Is a single facet - like statistics");
		return unrollSimpleFacet(facet);
	}

	public List<Map<String, Object>> explode(Facets facets) {
		return explode(facets.facetsAsMap());
	}

	public List<Map<String, Object>> explode(Map<String, Facet> facets) {

		List<Map<String, Object>> entries = new ArrayList<Map<String, Object>>();

		Set<String> facet_names = facets.keySet();
		for (String facet_name : facet_names) {
			entries.addAll(unrollFacet(facets.get(facet_name)));
		}

		return entries;
	}

	public List<Map<String, Object>> getFacets() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		return result;
	}

	public String getFacetClass(String facetType) {
		return ((Class<?>) classFinder.getClassByType(facetType)).getCanonicalName();
	}


	public List<String> getFacetFields(Facet facet) {

		List<String> result = new ArrayList<String>();

		Class<?> facetClass = getFacetClass(facet);

		if (facetClass == null) {
			logger.warn("Failed to get a facet class for type: "
					+ facet.getType());
			return result;
		}

		logger.debug("Class for facet type '" + facet.getType() + "': "
				+ facetClass.getCanonicalName());
		
		if ( classFinder.getClassMethods(facetClass) == null ) {
			logger.warn("Failed to get methods for facet class "
					+ facetClass.getCanonicalName());
			return result;
		}

		List<Map<String, Object>> facetData;

		if (classFinder.hasMethod("getEntries", facetClass)) {
			logger.debug("Is a collection of facets, unrolling it");
			facetData = unrollFacetEntries(facet);

		} else {
			logger.debug("Is a single facet - like statistics");
			facetData = unrollSimpleFacet(facet);
		}

		for (Map<String, Object> entry : facetData) {
			Set<String> fields = entry.keySet();
			for (String field : fields) {
				if (!result.contains(field)) {
					result.add(field);
				}
			}
		}

		return result;
	}

}
