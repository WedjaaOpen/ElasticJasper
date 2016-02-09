/****
 * 
 * Copyright 2013-2016 Wedjaa <http://www.wedjaa.net/>
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

package net.wedjaa.jasper.elasticsearch.server;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.wedjaa.elasticparser.ESSearch;
import net.wedjaa.jasper.elasticsearch.adapter.ESAdapterService;
import net.wedjaa.jasper.elasticsearch.datasource.ESIconDescriptor;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceProperty;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.util.IIconDescriptor;
import com.jaspersoft.studio.server.model.datasource.MRDatasourceCustom;
import com.jaspersoft.studio.server.protocol.restv2.DiffFields;

public class MRDatasourceES extends MRDatasourceCustom {

	public static final String CUSTOM_CLASS = "net.wedjaa.jasper.elasticsearch.adapter.ESAdapterService";
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public MRDatasourceES(ANode parent, ResourceDescriptor rd, int index) {
		super(parent, rd, index);
	}

	private static IIconDescriptor iconDescriptor;

	public static IIconDescriptor getIconDescriptor() {
		if (iconDescriptor == null)
			iconDescriptor = new ESIconDescriptor("datasource-elasticsearch");
		return iconDescriptor;
	}

	@Override
	public IIconDescriptor getThisIconDescriptor() {
		return getIconDescriptor();
	}

	public static ResourceDescriptor createDescriptor(ANode parent) {
		ResourceDescriptor rd = MRDatasourceCustom.createDescriptor(parent);
		ResourceProperty rp = new ResourceProperty(MRDatasourceCustom.PROP_DATASOURCE_CUSTOM_PROPERTY_MAP);
		List<ResourceProperty> props = new ArrayList<ResourceProperty>();
		// Index and Type Parameters
		props.add(new ResourceProperty(ESAdapterService.ES_INDEX_PARAM,  ""));
		props.add(new ResourceProperty(ESAdapterService.ES_TYPE_PARAM,  ""));
		// Connection Parameters
		props.add(new ResourceProperty(ESAdapterService.ES_HOST_PARAM,  ESSearch.ES_DEFAULT_HOST));
		props.add(new ResourceProperty(ESAdapterService.ES_PORT_PARAM,  Integer.toString(ESSearch.ES_DEFAULT_PORT)));
		props.add(new ResourceProperty(ESAdapterService.ES_CLUSTER_PARAM,  ESSearch.ES_DEFAULT_CLUSTER));
		// Authentication Parameters
		props.add(new ResourceProperty(ESAdapterService.ES_USER_PARAM,  ""));
		props.add(new ResourceProperty(ESAdapterService.ES_PASSWORD_PARAM,  ""));
		// Search Type
		props.add(new ResourceProperty(ESAdapterService.ES_MODE_PARAM,  "0"));
		
		props.add(new ResourceProperty("_cds_name", "ESDataSource"));
		rp.setProperties(props);
		rd.setResourceProperty(rp);
		rp = new ResourceProperty(ResourceDescriptor.PROP_DATASOURCE_CUSTOM_SERVICE_CLASS, CUSTOM_CLASS);
		rd.setResourceProperty(rp);
		rd.setResourceProperty(DiffFields.DATASOURCENAME, "ESDataSource");
		return rd;
	}
}
