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

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Fabio Torchetti
 */

public class ESCreator implements IDataAdapterCreator {

	@Override
	public DataAdapterDescriptor buildFromXML(Document docXML) {
		
		ESAdapterImplementation result = new ESAdapterImplementation();
		
		NamedNodeMap rootAttributes = docXML.getChildNodes().item(0).getAttributes();
		String connectionName = rootAttributes.getNamedItem("name").getTextContent();
		result.setName(connectionName);
		
		NodeList children = docXML.getChildNodes().item(0).getChildNodes();
		for(int i=0; i<children.getLength(); i++){
			Node node = children.item(i);
			if (node.getNodeName().equals("connectionParameter")){
		
				String paramName = node.getAttributes().getNamedItem("name").getTextContent();	
				if (paramName.equals("elasticSearchIndexes")) result.setElasticSearchIndexes(node.getTextContent());
				if (paramName.equals("elasticSearchTypes")) result.setElasticSearchTypes(node.getTextContent());
			    if (paramName.equals("elasticSearchMode")) result.setElasticSearchMode(node.getTextContent());
			    if (paramName.equals("elasticSearchHost")) result.setElasticSearchHost(node.getTextContent());
			    if (paramName.equals("elasticSearchPort")) result.setElasticSearchPort(node.getTextContent());
			    if (paramName.equals("elasticSearchCluster")) result.setElasticSearchCluster(node.getTextContent());
			    if (paramName.equals("elasticSearchUsername")) result.setElasticSearchUsername(node.getTextContent());
			    if (paramName.equals("elasticSearchPassword")) result.setElasticSearchPassword(node.getTextContent());
			}
		}

		ESAdapterDescriptor desc = new ESAdapterDescriptor();
		desc.setDataAdapter(result);
		return desc;
	}

	@Override
	public String getID() {
		return "net.wedjaa.jasper.elasticSearch.ESConnection";
	}


}