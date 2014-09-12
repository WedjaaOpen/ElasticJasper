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

package net.wedjaa.jasper.elasticsearch.query;

import net.sf.jasperreports.engine.design.JRDesignQuery;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.jaspersoft.studio.data.designer.QueryDesigner;
import com.jaspersoft.studio.wizards.ContextHelpIDs;

/**
 *
 * @author Fabio Torchetti
 */

public class ESQueryDesigner extends QueryDesigner {
	/* Text area where enter the query */
	protected StyledText queryTextArea;
	
	private static Logger logger = Logger.getLogger(ESQueryDesigner.class);

	public Control createControl(Composite parent) {
		control = (StyledText) super.createControl(parent);
		return control;
	}

	protected void queryTextAreaModified() {
		((JRDesignQuery) jDataset.getQuery()).setText(queryTextArea.getText());
	}

	@Override
	public String getContextHelpId() {
        logger.debug("Testing in Query designer");
		return ContextHelpIDs.PREFIX.concat("query_elasticsearch");
	}
}
