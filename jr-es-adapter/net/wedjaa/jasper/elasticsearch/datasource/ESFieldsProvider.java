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

package net.wedjaa.jasper.elasticsearch.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.wedjaa.elasticparser.ESSearch;

import org.apache.log4j.Logger;

/**
 *
 * @author Fabio Torchetti
 */
public class ESFieldsProvider {
    
    private static ESFieldsProvider instance;
    private static Logger logger = Logger.getLogger(ESFieldsProvider.class);

    private static final Lock lock = new ReentrantLock();

    private ESFieldsProvider() {

    }

    public boolean supportsGetFieldsOperation(Object jConfig) {
        return true;
    }
    
    public static ESFieldsProvider getInstance() {
        lock.lock();
        try {
            if (instance == null) {
                instance = new ESFieldsProvider();
            }
            return instance;
        } finally {
            lock.unlock();
        }
    }

    public List<JRDesignField> getFields(
    		JasperReportsContext context, JRDataset dataset, 
    		Map<String, Object> parameters, ESSearch connection)
            throws JRException {
        
    	logger.debug("Providing fields a query.");
    	
    	String query = "{ query: { match_all: {} } }";
    	
    	if ( dataset.getQuery() != null ) {
    		query = dataset.getQuery().getText();
    	}
    	
    	logger.debug("Passing query to connection: " + query);
    	connection.setSearch(query);
        Map<String, Class<?>> queryFields = connection.getFields(query);
        
        List<JRDesignField> fields = new ArrayList<JRDesignField>();
        
        for (String fieldName: queryFields.keySet() ) {
            JRDesignField field = new JRDesignField();
            field.setName(fieldName);
            field.setValueClass(queryFields.get(fieldName));
            field.setDescription(fieldName);
            fields.add(field);
        }
        
        return fields;
    }
    
}
