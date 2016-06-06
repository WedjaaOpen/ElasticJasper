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

import com.jaspersoft.studio.data.ADataAdapterComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.wedjaa.elasticparser.ESSearch;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

/**
 *
 * @author Fabio Torchetti
 */
@SuppressWarnings("deprecation")
public class ESAdapterComposite extends ADataAdapterComposite {
    

	private ESAdapterDescriptor dataAdapterDescriptor;
	private Text esHostField;
	private Text esPortField;
	private Text esClusterField;
	private Text esIndexesField;
	private Text esTypesField;
	private Text esUsernameField;
	private Text esPasswordField;
	private Combo esSearchModeField;
	
	public ESAdapterComposite(Composite parent, int style, JasperReportsContext jrContext) {
		super(parent, style, jrContext);
		initComponents();
	}

	private void initComponents() {
		setLayout(new GridLayout(2, false));
		createLabel("Indexes - comma separated");
		esIndexesField = createTextField(false);
		createLabel("Types - comma separated");
		esTypesField = createTextField(false);                
		createLabel("Hostname");
		esHostField = createTextField(false);
		createLabel("Port");
		esPortField = createTextField(false);
		createLabel("Cluster");
		esClusterField = createTextField(false);
		createLabel("Username");
		esUsernameField = createTextField(false);
		createLabel("Password");
		esPasswordField = createTextField(true);
		createLabel("Query Mode");
		esSearchModeField = createComboField();                
                
	}

	private void createLabel(String text) {
		Label label = new Label(this, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}

	private Text createTextField(boolean password) {
		Text textField = new Text(this, !password ? SWT.BORDER : SWT.BORDER | SWT.PASSWORD);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return textField;
	}
	
	private Combo createComboField() {
		Combo comboField = new Combo(this, SWT.BORDER);
		comboField.add("Hits Mode - Returns Hits Data", ESSearch.ES_MODE_HITS);
		comboField.add("Aggregation Mode - Returns Aggregations Data", ESSearch.ES_MODE_AGGS);
		comboField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return comboField;
	}
	
	public DataAdapterDescriptor getDataAdapter() {
		if (dataAdapterDescriptor == null) {
			dataAdapterDescriptor = new ESAdapterDescriptor();
		}
		return dataAdapterDescriptor;
	}

	@Override
	public void setDataAdapter(DataAdapterDescriptor dataAdapterDescriptor) {
		super.setDataAdapter(dataAdapterDescriptor);

		this.dataAdapterDescriptor = (ESAdapterDescriptor) dataAdapterDescriptor;
		ESAdapter dataAdapter = (ESAdapter) dataAdapterDescriptor.getDataAdapter();
		bindWidgets(dataAdapter);
	}

	@Override
	protected void bindWidgets(DataAdapter dataAdapter) {
		bindingContext.bindValue(SWTObservables.observeText(esHostField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchHost"));
		bindingContext.bindValue(SWTObservables.observeText(esPortField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchPort"));
		bindingContext.bindValue(SWTObservables.observeText(esClusterField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchCluster"));
		bindingContext.bindValue(SWTObservables.observeText(esIndexesField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchIndexes"));
		bindingContext.bindValue(SWTObservables.observeText(esTypesField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchTypes"));
		bindingContext.bindValue(SWTObservables.observeText(esUsernameField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchUsername"));
		bindingContext.bindValue(SWTObservables.observeText(esPasswordField, SWT.Modify), PojoObservables.observeValue(dataAdapter, "elasticSearchPassword"));
		bindingContext.bindValue(SWTObservables.observeSingleSelectionIndex(esSearchModeField), PojoObservables.observeValue(dataAdapter, "elasticSearchMode"));		
	}

	@Override
	public String getHelpContextId() {
		return PREFIX.concat("adapter_elasticsearch");
	}


}