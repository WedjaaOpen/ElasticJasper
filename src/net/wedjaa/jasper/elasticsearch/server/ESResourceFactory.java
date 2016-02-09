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

package net.wedjaa.jasper.elasticsearch.server;

import java.text.ParseException;
import java.util.Set;

import org.eclipse.jface.wizard.IWizardPage;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceProperty;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.plugin.IResourceFactory;
import com.jaspersoft.studio.server.protocol.restv2.ARestV2Connection;
import com.jaspersoft.studio.server.protocol.restv2.WsTypes;
import com.jaspersoft.studio.server.utils.ResourceDescriptorUtil;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.server.wizard.resource.page.ResourcePageContent;

public class ESResourceFactory implements IResourceFactory {

	public AMResource getResource(ANode parent, ResourceDescriptor resource, int index) {
		if (resource.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE_CUSTOM)) {
			ResourceProperty rp = ResourceDescriptorUtil.getProperty(ResourceDescriptor.PROP_DATASOURCE_CUSTOM_SERVICE_CLASS, resource.getProperties());
			if (rp != null && rp.getValue().equals(MRDatasourceES.CUSTOM_CLASS))
				return new MRDatasourceES(parent, resource, index);
		}
		return null;
	}

	public IWizardPage[] getResourcePage(ANode parent, AMResource resource) {
		if (resource instanceof MRDatasourceES)
			return APageContent.getPages(resource, new ResourcePageContent(parent, resource), new DatasourceESPageContent(parent, resource));
		return null;
	}

	public ANode createNewResource(ANode root, ANode parent) {
		return null;
	}

	@Override
	public ANode createNewDatasource(ANode root, ANode parent) {
		return new MRDatasourceES(root, MRDatasourceES.createDescriptor(parent), -1);
	}

	@Override
	public void initWsTypes(WsTypes wsType) {

	}

	@Override
	public ResourceDescriptor getRD(ARestV2Connection rc, ClientResource<?> cr, ResourceDescriptor rd) throws ParseException {
		return null;
	}

	@Override
	public ClientResource<?> getResource(ARestV2Connection rc, ClientResource<?> cr, ResourceDescriptor rd) throws ParseException {
		return null;
	}

	@Override
	public void initContainers(Set<Class<? extends ClientResource<?>>> containers) {

	}


}
