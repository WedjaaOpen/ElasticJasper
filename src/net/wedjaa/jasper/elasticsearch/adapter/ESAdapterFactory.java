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
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.wedjaa.jasper.elasticsearch.Activator;

import org.eclipse.swt.graphics.Image;
import org.apache.log4j.*;

/**
 *
 * @author Fabio Torchetti
 */
public class ESAdapterFactory implements DataAdapterFactory {

	private static Logger logger = Logger.getLogger(ESAdapterFactory.class);
	
    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#createDataAdapter()
     */
    public DataAdapterDescriptor createDataAdapter() {

        ESAdapterDescriptor descriptor = new ESAdapterDescriptor();
        descriptor.getDataAdapter().setElasticSearchIndexes("");
        descriptor.getDataAdapter().setElasticSearchTypes("");
        descriptor.getDataAdapter().setElasticSearchHost("localhost");
        descriptor.getDataAdapter().setElasticSearchPort("9300");
        descriptor.getDataAdapter().setElasticSearchCluster("elasticsearch");
        descriptor.getDataAdapter().setElasticSearchUsername(null);
        descriptor.getDataAdapter().setElasticSearchPassword(null);
        descriptor.getDataAdapter().setElasticSearchMode("0");
        logger.info("Returning an ESAdapterDescriptor");
        
        return descriptor;
        
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.jaspersoft.studio.data.DataAdapterFactory#getDataAdapterClassName()
     */
    public String getDataAdapterClassName() {
    	logger.debug("Returning " + ESAdapterImplementation.class.getName() + " as adapter class name");
        return ESAdapterImplementation.class.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getLabel() {
    	logger.info("Returning our label");
        return "ElasticSearch DataSource";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getDescription() {
    	logger.info("Returning our description");
        return "Makes possible to retrieve data from ElasticSearch";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
     */
    public Image getIcon(int size) {
        logger.info("Was requested an icon of size " + size + " - returning the only one we have!");
        if (size == 16) {
            return Activator.getDefault().getImage(Activator.ICON_NAME);
        }
        return Activator.getDefault().getImage(Activator.ICON_NAME);
    }

    
	@Override
	public DataAdapterService createDataAdapterService(
			JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
        logger.info("Returning a service for data adapter: " + dataAdapter.getClass().getName());
        if (dataAdapter instanceof ESAdapter)
            return new ESAdapterService(jasperReportsContext, (ESAdapter) dataAdapter);
        logger.info("Returning null, I don't know what the are talking about!");
        return null;
	}
	
	
    @Override
    public IDataAdapterCreator iReportConverter() {
            logger.info("Returning an elasticsearch creator!");
            return new ESCreator();
    }
    
    @Override
    public boolean isDeprecated() {
        logger.info("Returning false to isDeprecated");
    	return false;
    }


}
