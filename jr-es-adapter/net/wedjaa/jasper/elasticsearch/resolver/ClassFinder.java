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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.eclipse.osgi.framework.internal.core.BundleURLConnection;

@SuppressWarnings("restriction")
public class ClassFinder {

	private final static Logger logger = Logger.getLogger(ClassFinder.class);
	private String packageName;
	private String classMatch;
	private List<Class<?>> implementedClasses;
	private Map<String, Class<?>> classes;
	private Map<Class<?>, List<Method>> classMethods;
	
	public ClassFinder(String classMatch, String packageName) {
		this.packageName = packageName;
		this.classMatch = classMatch;
		this.implementedClasses = null;
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
		//findPackageClasses();
	}

	public ClassFinder(Class<?> implementedClass, String packageName) {
		this.packageName = packageName;
		this.classMatch = null;
		this.implementedClasses = new ArrayList<Class<?>>();
		implementedClasses.add(implementedClass);
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
		//findPackageClasses();
	}
	
	public ClassFinder(List<Class<?>> implementedClasses, String packageName) {
		this.packageName = packageName;
		this.classMatch = null;
		this.implementedClasses = implementedClasses;
		this.classes = new HashMap<String, Class<?>>();
		this.classMethods = new HashMap<Class<?>, List<Method>>();
		//findPackageClasses();
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
	
	private void findPackageClasses() {

		// Transform package name to path
		String path = packageName.replace('.', File.separatorChar);

		// Get the classloader
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		assert classLoader != null;

		// Get the resources in the package
		Enumeration<URL> resources;
		try {
			// Find the resources that have this path
			resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				// Now find the classes for the facets in this path
				findClasses(resources.nextElement(), path);
			}

		} catch (IOException e) {
			logger.warn("Failed to load package classes: "
					+ e.getLocalizedMessage());
		}

	}

	private static JarFile getAlternativeJarFile(URL url) {
		logger.debug("Determining Jar File for " + url);
		String urlFile = url.getFile();
		logger.debug("Backing file: " + urlFile);
		// Trim off any suffix - which is prefixed by "!/" on Weblogic
		int separatorIndex = urlFile.indexOf("!/");

		// OK, didn't find that. Try the less safe "!", used on OC4J
		if (separatorIndex == -1) {
			separatorIndex = urlFile.indexOf('!');
		}

		if (separatorIndex != -1) {

			String jarFileUrl = urlFile.substring(0, separatorIndex);
			logger.debug("Resolved to JAR: " + jarFileUrl);
			// And trim off any "file:" prefix.
			if (jarFileUrl.startsWith("file:")) {
				jarFileUrl = jarFileUrl.substring("file:".length());
				// https://java.net/jira/browse/FACELETS-306
				try {
					jarFileUrl = URLDecoder.decode(jarFileUrl, "UTF-8");
					logger.debug("Final URL for JAR: " + jarFileUrl);
					return new JarFile(jarFileUrl);
				} catch (UnsupportedEncodingException e) {
					logger.warn("Decoding error: " + e);
				} catch (IOException e) {
					logger.warn("IOError: " + e);
				}
			}

		}
		logger.warn("Could not find Jar File for " + url);
		return null;
	}

	private List<String> getJarClasses(JarFile jarFile) {
		List<String> results = new ArrayList<String>();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			results.add(entry.getName());
		}
		return results;
	}

	private  List<String> listFileTree(File dir, String packagePath) {
		List<String> result =  new ArrayList<String>();
		if (null == dir || !dir.isDirectory()) {
			return result;
		}

		for (File entry : dir.listFiles()) {
			if (entry.isFile()) {
				String fullPath = entry.getAbsolutePath();
				if (fullPath.lastIndexOf(packagePath)>=0) {
					String classPath = fullPath.substring(fullPath.lastIndexOf(packagePath));
					result.add(classPath);
				}
			} else {
				result.addAll(listFileTree(entry, packagePath));
			}
		}
		return result;
	}

	private List<String> getDirectoryClasses(URL directoryUrl, String packagePath) {
		File dirFile = new File(directoryUrl.getPath());
		return listFileTree(dirFile, packagePath);
	}

	private boolean isAcceptedByName(String classFile) {
		boolean result = false;
		if ( classFile.endsWith(classMatch) ) {
			result = true;
		}
		return result;
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
	
	private String getRealClassname(String classFilename) {
		String className = classFilename.replace("/", ".");
		className = className.substring(0,className.lastIndexOf('.'));	
		if ( className.indexOf('$') > 0 ) {
			className = className.substring(0,className.indexOf('$'));		
		}
		return className;
	}
	
	private boolean isAcceptedByExtends(String classFile) {
		boolean result = false;
		String className = getRealClassname(classFile);
		try {
			Class<?> current_class = Class.forName(className);
			if  ( classes.containsValue(current_class) ) {
				logger.trace("Skipping already registered class " + current_class);
				return false;
			} 
			Set<Class<?>> extensions = getRelatedClasses(current_class);
			Iterator<Class<?>> implClassIter = implementedClasses.iterator();
			while ( implClassIter.hasNext() && !result ) {
				Class<?> impClass = implClassIter.next();
				result = extensions.contains(impClass);
				if ( !result && className.endsWith("histogram.InternalDateHistogram") ) {
					logger.debug(current_class + " has no " + impClass);
				}
			}
			if ( !result && className.endsWith("histogram.InternalDateHistogram") ) {
				logger.debug("What about " + current_class + ": ");
				logger.debug(extensions);
			} 
		} catch (ClassNotFoundException e) {
			logger.warn("Failed to check class " + className + ": " + e);
		}
		return result;
	}
	
	private boolean isAccepted(String classFile) {
		
		if ( !classFile.endsWith(".class") || 
				!classFile.startsWith(packageName.replace('.', '/')) ) {
			return false;
		}

		if ( classMatch != null )
			return isAcceptedByName(classFile);
		return isAcceptedByExtends(classFile);
	}
	
	private void findClasses(URL directory, String packagePath) {

		logger.debug("Looking for classes in: " + directory + " for path: "
				+ packagePath);

		URLConnection connection;
		try {
			connection = directory.openConnection();
		} catch (IOException e) {
			logger.warn("Could not get connection to " + directory + ": " + e);
			return;
		}

		List<String> classFiles;
		
		if (connection instanceof JarURLConnection) {
			try {
				JarFile jarFile = ((JarURLConnection) connection).getJarFile();
				logger.debug("Loading classes from JAR file");
				classFiles = getJarClasses(jarFile);
			} catch (IOException e) {
				logger.warn("Could not get jar file from " + connection + ": "
						+ e);
				return;
			}
		} else {
			if (connection instanceof BundleURLConnection) {
				logger.debug("This is a bundle reference!");
				BundleURLConnection bundle = (BundleURLConnection) connection;
				URL dirUrl = bundle.getFileURL();
				logger.debug("File  URL: " + bundle.getFileURL());
				classFiles = getDirectoryClasses(dirUrl, packagePath);
			} else {
				JarFile jarFile = getAlternativeJarFile(directory);
				logger.debug("Getting a different jar file");
				if (jarFile == null) {
					logger.warn("Could not get jar file from " + directory
							+ ": could not resolve it!");
					return;
				}
				classFiles = getJarClasses(jarFile);
			}
		}

		try {
			Iterator<String> classFile = classFiles.iterator();
			while (classFile.hasNext()) {
				String entryName = classFile.next();
				if (isAccepted(entryName)) {
					String className = getRealClassname(entryName);
					String class_type = className;
					Class<?> current_class = Class.forName(className);
						Field field;
						try {
							field = current_class.getDeclaredField("TYPE");
							Object fieldValue = field.get(null);
							logger.trace("Class: " + current_class + " - Field: " + field + " - Value: " + fieldValue );
							if ( fieldValue.getClass().equals(String.class) ) {
								logger.trace("Using string value for type: " + fieldValue);
								class_type = (String) fieldValue;
							} else {
								try {
									logger.trace("Invoking name on " + fieldValue );
									class_type = (String) fieldValue.getClass().getMethod("name").invoke(fieldValue);
								} catch (InvocationTargetException e) {
									logger.warn("Failed to get type of class: " + className + ": " + e.getLocalizedMessage());
									class_type = className;
								} catch (NoSuchMethodException e) {
									logger.warn("Failed to get type of class: " + className + ": " + e.getLocalizedMessage());
									class_type = className;
								}
							}
						} catch (SecurityException e) {
							logger.trace("Class " + current_class + " has no type information, registering it under their classname. Error: " + e);
						} catch (NoSuchFieldException e) {
							logger.trace("Class " + current_class + " has no type information, registering it under their classname. Error: " + e);
						} catch (IllegalArgumentException e) {
							logger.trace("Class " + current_class + " has no type information, registering it under their classname. Error: " + e);
						} catch (IllegalAccessException e) {
							logger.trace("Class " + current_class + " has no type information, registering it under their classname. Error: " + e);
						}
						logger.trace("Registering " + className + " for type: " + class_type);
						classes.put(class_type, current_class);
						// Now get the methods for this class
						populateMethods(current_class);
				} 			}
		} catch (ClassNotFoundException cnfe) {
			logger.warn("Failed to load " + classMatch + " classes from " + directory
					+ ": " + cnfe.getLocalizedMessage());
		}

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
