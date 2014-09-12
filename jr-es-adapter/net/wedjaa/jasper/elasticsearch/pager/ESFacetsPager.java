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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wedjaa.jasper.elasticsearch.resolver.FacetResolver;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.facet.Facets;

public class ESFacetsPager implements ESResultsPager {

	private Logger logger = Logger.getLogger(ESFacetsPager.class);

	private String query = "";
	private final FacetResolver facetResolver;
	private List<Map<String,Object>> facet_values;
	private Iterator<Map<String, Object>> valueIterator;
	private int current_idx = 0;
	
	public ESFacetsPager(SearchResponse initialResponse, String query) {
		
		this.query = query;
		
		this.facetResolver= FacetResolver.getInstance();
		Facets facets = initialResponse.getFacets();
		if ( facets != null ) {
			logger.debug("Facets pager is being populated");
	        this.facet_values = facetResolver.explode(facets);

		} else {
			logger.warn("Facets pager not being populated: no facets have been found in the result");
			this.facet_values = new ArrayList<Map<String,Object>>();
		}
		
		this.valueIterator = this.facet_values.iterator();
	}
	
	@Override
	public boolean done() {
		return !valueIterator.hasNext();
	}

	@Override
	public boolean hit_available() {
		return valueIterator.hasNext();
	}

	@Override
	public void set_page_size(int page_size) {
	}

	@Override
	public int current_hit_idx() {
		return current_idx;
	}

	@Override
	public int page_size() {
		return facet_values.size();
	}

	@Override
	public int next_page() {
		return 0;
	}

	@Override
	public String get_query() {
		return query;
	}

	@Override
	public Map<String, Object> next(SearchResponse response) {
		current_idx++;
		return valueIterator.next();
	}

	@Override
	public Map<String,Class<?>> getResponseFields() {
		Map<String,Class<?>> result = new HashMap<String,Class<?>>();
		
		if ( facet_values != null ) {
			for (Map<String,Object> value: facet_values) {
				for (String fieldName: value.keySet() ) {
					if ( !result.containsKey(fieldName)) {
						result.put(fieldName, value.get(fieldName).getClass());
					}
				}
			}
		}
	
		return result;
	}
	
}
