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

package net.wedjaa.jasper.elasticsearch.query;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRSingletonCache;
import net.wedjaa.jasper.elasticsearch.datasource.ESDataSource;

import org.apache.log4j.*;

/**
 * 
 * @author Fabio Torchetti
 * 
 */
public class ESQueryExecuterFactoryBundle implements JRQueryExecuterFactoryBundle {
	private static final JRSingletonCache<QueryExecuterFactory> cache = new JRSingletonCache<QueryExecuterFactory>(
			QueryExecuterFactory.class);

	private static final ESQueryExecuterFactoryBundle instance = new ESQueryExecuterFactoryBundle();

	private static final String[] languages = new String[] { ESDataSource.QUERY_LANGUAGE };

	private static final Logger logger = Logger.getLogger(ESQueryExecuterFactoryBundle.class);
	
			
	private ESQueryExecuterFactoryBundle() {
			if ( logger != null ) {
				logger.info("This is the query executer for ES");
			} else {
				System.out.println("Logger is null - can you figure out why???");
			}
	}

	public static ESQueryExecuterFactoryBundle getInstance() {
        logger.info("Someone asked for an instance??");
		return instance;
	}

	public String[] getLanguages() {
                logger.info("Someone asked for languages??");
		return languages;
	}

	public QueryExecuterFactory getQueryExecuterFactory(String language)
			throws JRException {
                        logger.info("Begin asked for a factory for: " +language);
		if (ESDataSource.QUERY_LANGUAGE.equals(language)) {
			logger.info("Returning a ESQueryExecuterFactory");
			return (QueryExecuterFactory) cache
					.getCachedInstance(ESQueryExecuterFactory.class
							.getName());
		}
		return null;
	}
}

