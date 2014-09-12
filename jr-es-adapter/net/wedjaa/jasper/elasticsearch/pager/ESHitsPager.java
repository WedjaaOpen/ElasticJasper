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

package net.wedjaa.jasper.elasticsearch.pager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;


public class ESHitsPager implements ESResultsPager {
	
	public final static int PAGE_SIZE = 100;
	
	private long total_hits = 0;
	private int page = 0;
	private int page_size = 0;
	private long hits_count = 0;
	private int result_idx = 0;
	private String query;
	private SearchResponse initialResponse;
	private Logger logger = Logger.getLogger(ESHitsPager.class);
	
	public ESHitsPager(SearchResponse initialResponse, String query, long total) {
		this(initialResponse, query, total, ESHitsPager.PAGE_SIZE);
	}
	
	public ESHitsPager(SearchResponse initialResponse, String query, long total, int page_size) {
		this.total_hits = total;
		this.hits_count = 0;
		this.page_size = page_size;
		this.query = query;
		this.page = -1;
		this.initialResponse = initialResponse;
	}
	
	public int next_page() {
		page++;
		result_idx = -1;
		return page * page_size;
	}
	
	public boolean hit_available() {
		return (result_idx + 1) < page_size && page >= 0;
	}
	
	public int current_hit_idx() {
		return result_idx;
	}
	
	public int next_hit_idx() {
		hits_count++;
		return ++result_idx;
	}
	
	public int page_size() {
		return page_size;
	}

	public void  set_page_size(int page_size) {
		this.page_size = page_size;
	}
	
	public boolean page_complete() {
		return result_idx >= page_size;
	}
	
	public long get_start() {
		return page * page_size;
	}
	
	public boolean done() {
		return hits_count >= total_hits;
	}
	
	public String get_query() {
		return query;
	}

	@Override
	public Map<String, Object> next(SearchResponse response) {
		return response.getHits().getAt(this.next_hit_idx()).getSource();
	}

	@Override
	public Map<String,Class<?>> getResponseFields() {
		
		Map<String, Class<?>> result = new HashMap<String, Class<?>>();
		logger.debug("Hit Parser - Getting fields");
		
		if ( initialResponse != null
				&& initialResponse.getHits() != null ) {
			logger.debug("Response has hits...");
			SearchHit[] hits = initialResponse.getHits().getHits();
			for ( SearchHit hit: hits) {
				Set<String> field_names =  hit.getSource().keySet();
				for ( String field_name: field_names) {
					if (!result.containsKey(field_name)) {
						result.put(field_name, hit.getSource().get(field_name).getClass());
					}
				}
			}
			
		}
		return result;
	}
	
}
