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

import org.apache.log4j.Logger;

import net.wedjaa.jasper.elasticsearch.adapter.ESAdapter;
import net.sf.jasperreports.data.AbstractDataAdapter;

/**
 *
 * @author Fabio Torchetti
 */

public class ESAdapterImplementation extends AbstractDataAdapter implements ESAdapter {
    
    private String elasticSearchIndexes;
    private String elasticSearchTypes;
    private String elasticSearchMode;
    private String elasticSearchHost;
    private String elasticSearchPort;
    private String elasticSearchCluster;
    private String elasticSearchUsername;
    private String elasticSearchPassword;
	Logger logger = Logger.getLogger(ESAdapterImplementation.class);
    
    public ESAdapterImplementation() {
    	super();
    	logger.debug("Providing our implementation of DataAdapter");
    }
    
	public String getElasticSearchHost() {
		return elasticSearchHost;
	}
	
	public void setElasticSearchHost(String elasticSearchHost) {
    	logger.debug("Set Host: " + elasticSearchHost);
		this.elasticSearchHost = elasticSearchHost;
	}
	
	public String getElasticSearchPort() {
		return elasticSearchPort;
	}
	
	public void setElasticSearchPort(String elasticSearchPort) {
    	logger.debug("Set Port: " + elasticSearchPort);
		this.elasticSearchPort = elasticSearchPort;
	}
	
	public String getElasticSearchUsername() {
		return elasticSearchUsername;
	}
	
	public void setElasticSearchUsername(String elasticSearchUsername) {
    	logger.debug("Set Username: " + elasticSearchUsername);
		this.elasticSearchUsername = elasticSearchUsername;
	}
	
	public String getElasticSearchPassword() {
		return elasticSearchPassword;
	}
	
	public void setElasticSearchPassword(String elasticSearchPassword) {
    	logger.debug("Set Password: " + elasticSearchPassword);
		this.elasticSearchPassword = elasticSearchPassword;
	}
	
	public String getElasticSearchIndexes() {
		return elasticSearchIndexes;
	}
	
	public void setElasticSearchIndexes(String elasticSearchIndex) {
    	logger.debug("Set Indexes: " + elasticSearchIndex);
		this.elasticSearchIndexes = elasticSearchIndex;
	}
	
	public String getElasticSearchTypes() {
		return elasticSearchTypes;
	}
	
	public void setElasticSearchTypes(String elasticSearchTypes) {
    	logger.debug("Set Types: " + elasticSearchTypes);
		this.elasticSearchTypes = elasticSearchTypes;
	}

	@Override
	public void setElasticSearchCluster(String esCluster) {
    	logger.debug("Set Cluster: " + esCluster);
		this.elasticSearchCluster = esCluster;
	}

	@Override
	public String getElasticSearchCluster() {
		return this.elasticSearchCluster;
	}

	@Override
	public String getElasticSearchMode() {
		return this.elasticSearchMode;
	}

	@Override
	public void setElasticSearchMode(String searchMode) {
    	logger.debug("Set Mode: " + searchMode);
		this.elasticSearchMode = searchMode;
	}
    
}