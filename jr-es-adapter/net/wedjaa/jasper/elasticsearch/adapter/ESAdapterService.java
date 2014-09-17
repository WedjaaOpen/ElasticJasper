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

package net.wedjaa.jasper.elasticsearch.adapter;

import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.jasperreports.data.AbstractDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.wedjaa.elasticparser.ESSearch;

/**
 *
 * @author Fabio Torchetti
 */
public class ESAdapterService extends AbstractDataAdapterService {
    
	public final static String ES_HOST_PARAM = "elasticSearchHost";
	public final static String ES_PORT_PARAM = "elasticSearchPort";
	public final static String ES_CLUSTER_PARAM = "elasticSearchCluster";
	public final static String ES_USER_PARAM = "elasticSearchUsername";
	public final static String ES_PASSWORD_PARAM = "elasticSearchPassword";
	public final static String ES_INDEX_PARAM = "elasticSearchIndexes";
	public final static String ES_TYPE_PARAM = "elasticSearchTypes";
	public final static String ES_MODE_PARAM = "elasticSearchMode";
	
    private ESSearch esSearch;

    private final ESAdapter dataAdapter;
    private static Logger logger = Logger.getLogger(ESAdapterService.class);

    @SuppressWarnings("deprecation")
	public ESAdapterService(ESAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.esSearch = null;
        logger.debug("Just created a new ESAdapterService with a dataAdapter");
    }

    @Override
    public void contributeParameters(Map<String, Object> parameters) throws JRException {
    	logger.debug("Contributing a couple of parameters to the report.");
        if (esSearch != null) {
            dispose();
        }
        if (dataAdapter != null) {
            try {
                createESSearch();
                parameters.put(JRParameter.REPORT_CONNECTION, esSearch);
                parameters.put(ESAdapterService.ES_HOST_PARAM, dataAdapter.getElasticSearchHost());
                parameters.put(ESAdapterService.ES_PORT_PARAM, dataAdapter.getElasticSearchPort());
                parameters.put(ESAdapterService.ES_INDEX_PARAM, dataAdapter.getElasticSearchIndexes());
                parameters.put(ESAdapterService.ES_MODE_PARAM, dataAdapter.getElasticSearchMode());
                parameters.put(ESAdapterService.ES_TYPE_PARAM, dataAdapter.getElasticSearchTypes());
                parameters.put(ESAdapterService.ES_USER_PARAM, dataAdapter.getElasticSearchUsername());
                parameters.put(ESAdapterService.ES_PASSWORD_PARAM, dataAdapter.getElasticSearchPassword());
                parameters.put(ESAdapterService.ES_CLUSTER_PARAM, dataAdapter.getElasticSearchCluster());
            } catch (Exception e) {
                throw new JRException(e);
            }
        }
    }

    private void createESSearch() throws JRException {
    	
    	esSearch = new ESSearch(
    				dataAdapter.getElasticSearchIndexes(),
    				dataAdapter.getElasticSearchTypes(),
    				Integer.parseInt(dataAdapter.getElasticSearchMode()),
    				dataAdapter.getElasticSearchHost(),
    				Integer.parseInt(dataAdapter.getElasticSearchPort()),
    				dataAdapter.getElasticSearchUsername(),
    				dataAdapter.getElasticSearchPassword(),
    				dataAdapter.getElasticSearchCluster()
    			);
    	
        // Creating a base search - it will be a correct query
        // once it hits the QueryExecuter.
        esSearch.setSearch("{ \"query\": { \"match_all\": {} } }");
    }

    @Override
    public void dispose() {
        if (esSearch != null) {
            esSearch.close();
            esSearch = null;
        }
    }

    @Override
    public void test() throws JRException {
        try {
            if (esSearch != null) {
            } else {
                createESSearch();
            }
            esSearch.test();
        } finally {
            dispose();
        }
    }
}
