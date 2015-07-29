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

import java.util.List;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.wedjaa.jasper.elasticsearch.Activator;
import net.wedjaa.jasper.elasticsearch.datasource.ESDataSource;

import org.apache.log4j.Logger;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.data.AWizardDataEditorComposite;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.IWizardDataEditorProvider;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.data.ui.WizardQueryEditorComposite;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

/**
 *
 * @author Fabio Torchetti
 */
public class ESAdapterDescriptor extends DataAdapterDescriptor implements IFieldsProvider, IWizardDataEditorProvider {

	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static final Logger logger = Logger.getLogger(ESAdapterDescriptor.class);

	private IFieldsProvider fieldsProvider;

	@Override
	public ESAdapter getDataAdapter() {

		if (dataAdapter == null)
			dataAdapter = new ESAdapterImplementation();

		return (ESAdapter) dataAdapter;
	}

	@Override
	public ESAdapterEditor getEditor() {
		return new ESAdapterEditor();
	}

	@Override
	public Image getIcon(int size) {
		logger.debug("Fetching an image of size: " + size);

		Activator activator = Activator.getDefault();
		logger.debug("Activator: " + activator);
		if (activator == null) {
			logger.debug("Activator is null");
			return null;
		}

		Image image = Activator.getDefault().getImage(Activator.ICON_NAME);

		if (image == null) {
			logger.warn("Could not find image for: " + Activator.ICON_NAME);
			return null;
		}

		logger.debug("Returning an icon.");
		return image;

	}

	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig,
			JRDataset reportDataset) throws JRException, UnsupportedOperationException {
		getFieldProvider();
		return fieldsProvider.getFields(con, jConfig, reportDataset);
	}

	private void getFieldProvider() {
		if (fieldsProvider == null)
			fieldsProvider = new ESFieldsProvider();
	}

	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig) {
		getFieldProvider();
		return fieldsProvider.supportsGetFieldsOperation(jConfig);
	}

	@Override
	public AWizardDataEditorComposite createDataEditorComposite(Composite parent, WizardPage page) {
		return new WizardQueryEditorComposite(parent, page, this, ESDataSource.QUERY_LANGUAGE);
	}

}