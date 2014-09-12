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

import net.wedjaa.jasper.elasticsearch.resolver.AggregateResolver;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregations;

public class ESAggregationPager implements ESResultsPager {

	private Logger logger = Logger.getLogger(ESAggregationPager.class);

	private String query = "";
	private final AggregateResolver aggregateResolver;
	private List<Map<String,Object>> aggregate_values;
	private Iterator<Map<String, Object>> valueIterator;
	private int current_idx = 0;
	
	public ESAggregationPager(SearchResponse initialResponse, String query) {
		
		this.query = query;
		
		this.aggregateResolver= AggregateResolver.getInstance();
		Aggregations aggregations = initialResponse.getAggregations();
		if ( aggregations != null ) {
			logger.debug("Aggregations pager is being populated");
	        this.aggregate_values = aggregateResolver.explode(aggregations);

		} else {
			logger.warn("Aggregation pager not being populated: no aggregations have been found in the result");
			this.aggregate_values = new ArrayList<Map<String,Object>>();
		}
		
		this.valueIterator = this.aggregate_values.iterator();
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
		return aggregate_values.size();
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
		
		if ( aggregate_values != null ) {
			for (Map<String,Object> value: aggregate_values) {
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
