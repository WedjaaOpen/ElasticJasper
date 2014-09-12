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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ESSearchTester {

	private static String getQuery(String queryName) {
		
		StringBuffer sb = new StringBuffer();

		BufferedReader br;
		
		try {
			br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(queryName), "UTF-8"));
			for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
		} catch (IOException e) {
			System.err.println("Failed to read query: " + e.getLocalizedMessage());
		}

		return sb.toString(); 	
	}
	
	public static void main(String[] args) {
		
		System.out.println("ES Search Testing started.");
		
		ESSearch search = new ESSearch("sentiwarn", "message", ESSearch.ES_MODE_AGGS,  "elastix.novalocal", 9300, null, null, "elasticsearch");
		search.search(getQuery("test-aggs.json"));
		Map<String, Object> hit = null;
		while (  (hit = search.next()) != null ) {
			System.out.println("Hit: {");
			for ( String key: hit.keySet() ) {
				System.out.println("  "+key+": " + hit.get(key));
				
			}
			System.out.println("};");
		}
		search.close();
		/*
		Map<String, Class<?>> fields = search.getFields(getQuery("test-aggs.json"));
		List<String> sortedKeys=new ArrayList<String>(fields.keySet());
		Collections.sort(sortedKeys);
		Iterator<String> sortedKeyIter = sortedKeys.iterator();
		while ( sortedKeyIter.hasNext() ) {
			String fieldname = sortedKeyIter.next();
			System.out.println(" --> " + fieldname + "["+ fields.get(fieldname).getCanonicalName() +"]");
		}
		*/
	}

}
