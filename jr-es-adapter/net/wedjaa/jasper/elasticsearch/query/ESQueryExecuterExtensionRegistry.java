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

package net.wedjaa.jasper.elasticsearch.query/**
 *
 */
;

import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;

import org.apache.log4j.Logger;

 public class ESQueryExecuterExtensionRegistry implements ExtensionsRegistryFactory {
    
    private static Logger logger = Logger.getLogger(ESQueryExecuterExtensionRegistry.class);
        
	private static final ExtensionsRegistry defaultExtensionsRegistry = new ExtensionsRegistry() {
                
		@SuppressWarnings("unchecked")
		public <T> List<T> getExtensions(Class<T> extensionType) {
			if (JRQueryExecuterFactoryBundle.class.equals(extensionType)) {
				logger.warn("Registering the ESQueryExecuterFactoryBundle");
				return (List<T>) Collections
						.singletonList(ESQueryExecuterFactoryBundle
								.getInstance());
			}
			return null;
		}
	};

	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) {
        logger.warn("Returning our default extension registry handler");
		return defaultExtensionsRegistry;
	}
}


