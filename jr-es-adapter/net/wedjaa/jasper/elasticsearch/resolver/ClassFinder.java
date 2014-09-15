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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class ClassFinder {

	private final static Logger logger = Logger.getLogger(ClassFinder.class);
	private List<Class<?>> implementedClasses;
	private Map<String, Class<?>> classes;
	private Map<Class<?>, List<Method>> classMethods;
	
	public ClassFinder(String classMatch, String packageName) {
		this.implementedClasses = null;
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
	}

	public ClassFinder(Class<?> implementedClass, String packageName) {
		this.implementedClasses = new ArrayList<Class<?>>();
		implementedClasses.add(implementedClass);
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
	}
	
	public ClassFinder(List<Class<?>> implementedClasses, String packageName) {
		this.implementedClasses = implementedClasses;
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
	}
	
	public boolean hasMethod(String method, Class<?> objClass) {

		boolean result = false;

		List<Method> methods = getClassMethods(objClass);
		
		if (methods == null) {
			logger.warn("Failed to verify presence of " + method + " on class "
					+ objClass.getCanonicalName());
			return false;
		}

		Iterator<Method> iter = methods.iterator();
		while (iter.hasNext() && !result)
			result = iter.next().getName().equals(method);

		return result;

	}
	
	public Method getMethod(String method, Class<?> objClass) {

		Method result = null;
		
		List<Method> methods = getClassMethods(objClass);
		
		if (methods == null) {
			logger.warn("Failed to find method " + methods + " on class "
					+ objClass);
			return null;
		}

		Iterator<Method> iter = methods.iterator();
		while (iter.hasNext() && result == null) {
			Method current = iter.next();
			if (current.getName().equals(method)) {
				result = current;
			}
		}
		return result;
	}
	
	public Class<?> getClassByType(String objType) {
		return classes.get(objType);
	}
	
	public List<Method> getClassMethods(Class<?> objClass) {

		if (!classMethods.containsKey(objClass)) {
			populateMethods(objClass);
		}

		return classMethods.get(objClass);
	}
	
	
	public static Set<Class<?>> getRelatedClasses(Class<?> clazz) {
	    List<Class<?>> res = new ArrayList<Class<?>>();
	    do {
	        res.add(clazz);
	        // First, add all the interfaces implemented by this class
	        Class<?>[] interfaces = clazz.getInterfaces();
	        if (interfaces.length > 0) {
	            res.addAll(Arrays.asList(interfaces));

	            for (Class<?> interfaze : interfaces) {
	                res.addAll(getRelatedClasses(interfaze));
	            }
	        }

	        // Add the super class
	        Class<?> superClass = clazz.getSuperclass();

	        // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
	        if (superClass == null) {
	            break;
	        }

	        // Now inspect the superclass 
	        clazz = superClass;
	    } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

	    return new HashSet<Class<?>>(res);
	}  
	
	private void populateMethods(Class<?> inspectedClass) {
		logger.trace("Examining methods for class " + inspectedClass);
		Method[] inspectedClassMethods = inspectedClass.getMethods();
		if (logger.isDebugEnabled()) {
			for (Method method : inspectedClassMethods) {
				logger.trace("  ->" + method + "  -> "
						+ method.getGenericReturnType());
			}
		}
		classMethods.put(inspectedClass, Arrays.asList(inspectedClassMethods));
	}


}
