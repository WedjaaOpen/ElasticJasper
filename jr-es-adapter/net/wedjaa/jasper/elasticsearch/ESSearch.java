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

package net.wedjaa.jasper.elasticsearch;

 
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.wedjaa.jasper.elasticsearch.pager.ESAggregationPager;
import net.wedjaa.jasper.elasticsearch.pager.ESEmptyPager;
import net.wedjaa.jasper.elasticsearch.pager.ESFacetsPager;
import net.wedjaa.jasper.elasticsearch.pager.ESHitsPager;
import net.wedjaa.jasper.elasticsearch.pager.ESResultsPager;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 *
 * @author Fabio Torchetti
 */
public class ESSearch implements Connection {
    	
	private ESResultsPager pager;
    private Client esClient;
    private String mainSearch;
    private String cluster;
    private String [] indexes;
    private String [] types;
    private String strIndexes;
    private String strTypes;
    private String username;
    private String password;
    private String hostname;
    private int port;
    private int searchMode;
    
    private SearchResponse searchResponse;
    private static Logger logger = Logger.getLogger(ESSearch.class);

    public final static int ES_MODE_HITS = 0;
    public final static int ES_MODE_FACETS = 1;
    public final static int ES_MODE_AGGS = 2;

    public final static String ES_DEFAULT_HOST = "localhost";
    public final static int ES_DEFAULT_PORT = 9300;
    public final static String ES_DEFAULT_CLUSTER = "elasticsearch";
    public final static int ES_DEFAULT_SEARCH_MODE = ES_MODE_HITS;
    
    public ESSearch() {
        this(null, null);
    }
  
    public ESSearch(String indexes, String types, int searchType) {
        this(indexes, types, searchType, ES_DEFAULT_HOST, ES_DEFAULT_PORT, null, null, ES_DEFAULT_CLUSTER);
    }
    
    public ESSearch(String indexes, String types) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, ES_DEFAULT_HOST, ES_DEFAULT_PORT, null, null, ES_DEFAULT_CLUSTER);
    }

    public ESSearch(String indexes, String types, String hostname,int port) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, hostname, port, null, null, null);
    }

    public ESSearch(String indexes, String types, int searchType, String hostname,int port) {
        this(indexes, types, searchType, hostname, port, null, null, null);
    }

    public ESSearch(String indexes, String types, String hostname,int port, String cluster) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, hostname, port, null, null, cluster);
    }

    public ESSearch(String indexes, String types, int searchMode, String hostname,int port, String cluster) {
        this(indexes, types, searchMode, hostname, port, null, null, cluster);
    }

    public ESSearch(String indexes, String types, int searchMode, String hostname, int port, String username, String password, String cluster) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.cluster = cluster;
        this.port = port;
        this.searchMode = searchMode;
        this.setIndexes(indexes);
        this.setTypes(types);       
        this.strIndexes = indexes;
        this.strTypes = types;
        this.esClient = null;
    }

    public ESSearch clone() {
    	return new ESSearch(strIndexes, strTypes, searchMode, hostname, port, username, password, cluster);
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setSearch(String search) {
    	logger.debug("Setting ElasticSearch query: " + search);
        mainSearch = search;
    }
    
    public String getSearch() {
        return mainSearch;
    }

    public void setIndexes(String indexes) {
    	logger.debug("Setting search indexes: " + indexes);
    	
        this.indexes = new String[0];
        
        if ( indexes != null && indexes.length() > 0) {
        	this.indexes = indexes.split("\\s*,\\s*");
    	}        
        
    }
    
    public void setIndexes(String [] indexes) {
    	this.indexes = indexes;
    }
    
    public String [] getIndexes() {
        return this.indexes;
    }

    public void setTypes(String types) {

    	logger.debug("Setting search types: " + types);
        
        this.types = new String[0];
        if ( types != null && types.length() > 0) {
        	this.types = types.split("\\s*,\\s*");
        }
    }
    
    public void setTypes(String [] types) {
    	this.types = types;
    }
    
    public String [] getTypes() {
    	return this.types;
    }
    
 
    public void connect() {

    	if ( this.esClient != null ) {
    		logger.debug("Disconnecting existing client.");
    		this.esClient.close();
    		this.esClient = null;
    	}
    	
    	logger.debug("Creating new client to connect to: " + this.hostname);
    	logger.debug("ClassPath: " + System.getProperty("java.class.path"));
    	
    	// Prepare a client for the ES Server
    	Settings settings =  ImmutableSettings.settingsBuilder()
    			.put("cluster.name", this.cluster)
    			.put("client.transport.sniff", true)
    			.build();
    	
    	InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(this.hostname, this.port);
    	TransportClient transportClient = new TransportClient(settings);
    	transportClient.addTransportAddress(transportAddress);
 		this.esClient = transportClient;
        
    }
    

    
    private void runQuery(String query) {
    	    	
        logger.debug("Complete search request: " + query);
        
        connect();

        
        
        SearchRequestBuilder searchBuilder;
        if ( indexes.length > 0 ) {
        	searchBuilder = esClient.prepareSearch(indexes);
        } else {
        	searchBuilder = esClient.prepareSearch();
        	
        }
        if ( types.length> 0 ) {
        	searchBuilder.setTypes(types);
        }
        
        SearchResponse searchRes = 	searchBuilder
        	.setSource(query.getBytes())
        	.execute()
        	.actionGet();
        
        logger.debug("The query returns " + searchRes.getHits().getTotalHits() + " total matches.");
        logger.trace("Response: " + searchRes.toString());

        switch (searchMode) {
        case ESSearch.ES_MODE_HITS:
        	pager = new ESHitsPager(searchRes, query, searchRes.getHits().getTotalHits());
        	break;
        case ESSearch.ES_MODE_FACETS:
        	pager = new ESFacetsPager(searchRes, query);
        	break;
        case ESSearch.ES_MODE_AGGS:
        	pager = new ESAggregationPager(searchRes, query);
        	break;
        default:
        	pager = new ESEmptyPager();	
        }
        
        logger.trace("OK, ready to process the results");

   }
   
    public void search(String query) {
        runQuery(query);
    } 
    
    public void search() {
        search(mainSearch);
    }
    
    public void close() {
    	if ( this.esClient != null ) {
    		logger.debug("Disconnecting client");
    		this.esClient.close();
    		this.esClient = null;
    	}
    }
    
    public Map<String, Object> next() {

    	if ( pager.done() ) {
    		logger.debug("Pager is done - disposing of client.");
    		if ( this.esClient != null ) {
    			this.esClient.close();
    		}
    		return null;
    	}
    	
    	if ( !pager.hit_available() ) {
    		logger.debug("Gettin a page of hits");
    		// No hits are available - fetch the next
    		// page of hits for the query
        	searchResponse = esClient.prepareSearch(indexes)
                	.setTypes(types)
                	.setSource(pager.get_query().getBytes())
                	.setFrom((int) pager.next_page()) 
                	.setSize(pager.page_size())
                	.execute()
                	.actionGet();
        	pager.set_page_size(searchResponse.getHits().getHits().length);
        	logger.debug("Page hits: " + pager.page_size());
    	}
    	    	
    	logger.trace("Returning hit at: " + (pager.current_hit_idx() + 1) +  " of " + pager.page_size());
    	
        return pager.next(searchResponse);
        
    }
    
    public Map<String, Class<?>> getFields(String query) {
    	
    	Map<String,Class<?>> result = new HashMap<String, Class<?>>();
    	logger.debug("Getting fields using " + query);
    	
    	// Let runQuery prepare the pager
    	runQuery(query);
    	result = pager.getResponseFields();
    	close();
    	
    	logger.debug("Fields: " + result.toString());
    	return result;
    }
    
    public Map<String, Class<?>> getFields() {
        return getFields(mainSearch);
    }

    public void test() {
    	String search = getSearch();
    	setSearch("{ \"query\": { \"match_all\": {} } }");
        getFields();
        setSearch(search);
    }

	@Override
	public boolean isWrapperFor(Class<?> targetClass) throws SQLException {
		return targetClass.getCanonicalName().equals(ESSearch.class.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> targetClass) throws SQLException {
		if ( isWrapperFor(targetClass) ) {
			return  (T) this;
		}
		throw new SQLException();
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public void commit() throws SQLException {
	}

	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	@Override
	public Statement createStatement() throws SQLException {
		return null;
	}

	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return null;
	}

	@Override
	public Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {
		return null;
	}

	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return null;
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	@Override
	public String getClientInfo(String arg0) throws SQLException {
		return null;
	}

	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean isValid(int arg0) throws SQLException {
		return true;
	}

	@Override
	public String nativeSQL(String arg0) throws SQLException {
		return this.mainSearch;
	}

	@Override
	public CallableStatement prepareCall(String arg0) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		return null;
	}

	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
	}

	@Override
	public void rollback() throws SQLException {
	}

	@Override
	public void rollback(Savepoint arg0) throws SQLException {
	}

	@Override
	public void setAutoCommit(boolean arg0) throws SQLException {
	}

	@Override
	public void setCatalog(String arg0) throws SQLException {
	}

	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
	}

	@Override
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
	}

	@Override
	public void setHoldability(int arg0) throws SQLException {
	}

	@Override
	public void setReadOnly(boolean arg0) throws SQLException {
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return null;
	}

	@Override
	public void setTransactionIsolation(int arg0) throws SQLException {
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
	}
    
}
