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

import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.wedjaa.elasticparser.ESSearch;

/**
 *
 * @author Fabio Torchetti
 */
public class ESDataSource implements JRDataSource {

    public static String QUERY_LANGUAGE = "elasticsearch";
    public static String ELASTIC_SEARCH = "es_search";
    
    private static Logger logger = Logger.getLogger(ESDataSource.class);
    
    ESSearch esSearch = null;
    Map<String,Object> currentEvent = null;
    
    public ESDataSource(ESSearch esSearch) {
        this.esSearch = esSearch;
        logger.debug("Created a new elasticsearch datasource connected to " + esSearch.getHostname());
        this.esSearch.search();
        logger.debug("Search for " + this.esSearch.getSearch() + " has started....");
    }
    
    @Override
    public boolean next() throws JRException {
        currentEvent = esSearch.next();
        return currentEvent != null;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        Object value = currentEvent.get(jrf.getName());
        if ( value == null ) {
            return "";
        }
        return value;
    }
    
    public void dispose() {
        esSearch.close();
        esSearch = null;
        currentEvent = null;
    }
    
}
