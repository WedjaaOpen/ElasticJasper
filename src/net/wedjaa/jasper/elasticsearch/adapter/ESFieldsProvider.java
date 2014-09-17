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

import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.parameter.ParameterUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.wedjaa.elasticparser.ESSearch;

/**
 *
 * @author Fabio Torchetti
 */

public class ESFieldsProvider implements IFieldsProvider {
	
	private static Logger logger = Logger.getLogger(ESFieldsProvider.class);
	
    
    public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig) {
        return true;
    }

    public List<JRDesignField> getFields(DataAdapterService dataAdapterService,
            JasperReportsConfiguration jasperReportsConfiguration, JRDataset dataset) throws JRException,
            UnsupportedOperationException {
    	
    	logger.debug("Was asked to provide a list of fields.");
    	
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(JRParameter.REPORT_MAX_COUNT, 0);
        dataAdapterService.contributeParameters(parameters);
        ParameterUtil.setParameters(jasperReportsConfiguration, dataset, parameters);
        logger.debug("Getting fields for query: " + dataset.getQuery().getText());
        return net.wedjaa.jasper.elasticsearch.datasource.ESFieldsProvider.getInstance().getFields(jasperReportsConfiguration, dataset, parameters,
                (ESSearch) parameters.get(JRParameter.REPORT_CONNECTION));
    }
    
}
