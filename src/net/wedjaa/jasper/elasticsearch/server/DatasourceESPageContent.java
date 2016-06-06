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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceProperty;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.server.model.AMResource;
import com.jaspersoft.studio.server.model.datasource.MRDatasourceCustom;
import com.jaspersoft.studio.server.utils.ResourceDescriptorUtil;
import com.jaspersoft.studio.server.wizard.resource.APageContent;
import com.jaspersoft.studio.utils.Misc;

import net.wedjaa.elasticparser.ESSearch;
import net.wedjaa.jasper.elasticsearch.adapter.ESAdapterService;


@SuppressWarnings("deprecation")
public class DatasourceESPageContent extends APageContent {

	private Text esHostField;
	private Text esPortField;
	private Text esClusterField;
	private Text esIndexesField;
	private Text esTypesField;
	private Text esUsernameField;
	private Text esPasswordField;
	private Combo esSearchModeField;

	public DatasourceESPageContent(ANode parent, AMResource resource, DataBindingContext bindingContext) {
		super(parent, resource, bindingContext);
	}

	public DatasourceESPageContent(ANode parent, AMResource resource) {
		super(parent, resource);
	}

	@Override
	public String getPageName() {
		return "net.wedjaa.jasper.elasticsearch.server.page.datasource.elasticsearch";
	}

	@Override
	public String getName() {
		return "ElasticSearch DataSource";
	}

	private void createLabel(Composite composite, String text) {
		Label label = new Label(composite, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}

	private Text createTextField(Composite composite, boolean password) {
		Text textField = new Text(composite, !password ? SWT.BORDER : SWT.BORDER | SWT.PASSWORD);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return textField;
	}

	private Combo createComboField(Composite composite) {
		Combo comboField = new Combo(composite, SWT.BORDER);
		comboField.add("Hits Mode - Returns Hits Data", ESSearch.ES_MODE_HITS);
		comboField.add("Aggregation Mode - Returns Aggregations Data", ESSearch.ES_MODE_AGGS);
		comboField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return comboField;
	}

	public Control createContent(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		createLabel(composite, "Indexes - comma separated");
		esIndexesField = createTextField(composite, false);
		createLabel(composite, "Types - comma separated");
		esTypesField = createTextField(composite, false);
		createLabel(composite, "Hostname");
		esHostField = createTextField(composite, false);
		createLabel(composite, "Port");
		esPortField = createTextField(composite, false);
		createLabel(composite, "Cluster");
		esClusterField = createTextField(composite, false);
		createLabel(composite, "Username");
		esUsernameField = createTextField(composite, false);
		createLabel(composite, "Password");
		esPasswordField = createTextField(composite, true);
		createLabel(composite, "Query Mode");
		esSearchModeField = createComboField(composite);

		rebind();
		return composite;
	}

	@Override
	protected void rebind() {

		ResourceProperty resprop = ResourceDescriptorUtil
				.getProperty(MRDatasourceCustom.PROP_DATASOURCE_CUSTOM_PROPERTY_MAP, res.getValue().getProperties());

		ResourceProperty rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_INDEX_PARAM,
				resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));		
		bindingContext.bindValue(SWTObservables.observeText(esIndexesField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_TYPE_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esTypesField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_HOST_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esHostField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_PORT_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esPortField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_CLUSTER_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esClusterField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_USER_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esUsernameField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_PASSWORD_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeText(esPasswordField, SWT.Modify),
				PojoObservables.observeValue(rsp, "value"));

		rsp = ResourceDescriptorUtil.getProperty(ESAdapterService.ES_MODE_PARAM, resprop.getProperties());
		rsp.setValue(Misc.nvl(rsp.getValue()));
		bindingContext.bindValue(SWTObservables.observeSingleSelectionIndex(esSearchModeField),
				PojoObservables.observeValue(rsp, "value"));

	}

	@Override
	public String getHelpContext() {
		return "net.wedjaa.jasper.elasticsearch.toc";
	}
}
