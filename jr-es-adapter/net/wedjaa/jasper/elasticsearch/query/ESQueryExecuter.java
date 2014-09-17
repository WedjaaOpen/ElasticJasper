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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.wedjaa.elasticparser.ESSearch;
import net.wedjaa.jasper.elasticsearch.datasource.ESDataSource;

/**
 *
 * @author Fabio Torchetti
 */
public class ESQueryExecuter extends JRAbstractQueryExecuter {

	private final Map<String, ? extends JRValueParameter> reportParameters;

	private final Map<String, Object> parameters;

	private final boolean directParameters;

    private ESSearch esSearch;
    
    private static Logger logger = Logger.getLogger(ESQueryExecuter.class);
    
	public ESQueryExecuter(JasperReportsContext jasperReportsContext,
			JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters)
			throws JRException {
		this(jasperReportsContext, dataset, parameters, false);
	}

	public ESQueryExecuter(JasperReportsContext jasperReportsContext,
			JRDataset dataset,
			Map<String, ? extends JRValueParameter> parameters,
			boolean directParameters) {
		super(jasperReportsContext, dataset, parameters);
		
		JasperReport report = (JasperReport) parameters.get(JRFillParameter.JASPER_REPORT).getValue();
		if ( report != null) {
			logger.debug("ESQueryExecuter for report: " + report.getName());
			logger.debug("Report query: " + report.getQuery().getText());
		}
		if ( logger.isTraceEnabled() ) {
			for (String param: parameters.keySet()) {
				JRFillParameter paramVal = (JRFillParameter) parameters.get(param);
				logger.trace("  queryParam["+param+"]: " + paramVal.getValue());
			}
			
			Map<String,String> jrCtx = jasperReportsContext.getProperties();
			for ( String propName: jrCtx.keySet()) {
				logger.trace("  ctxParam["+propName+"]: " + jrCtx.get(propName));
			}
		}
		logger.trace("Dataset Query: " + dataset.getQuery().getText());
		
		this.directParameters = directParameters;
		this.reportParameters = parameters;
		this.parameters = new HashMap<String, Object>();
		logger.debug("Started a query executer for ElasticSearch");
		parseQuery();
	}

	/**
	 * Method not implemented
	 */
	@Override
	public boolean cancelQuery() throws JRException {
		return false;
	}

	@Override
	public void close() {
        esSearch.close();
		esSearch = null;
	}

	private ESSearch processConnection(JRValueParameter valueParameter)
			throws JRException {
		if (valueParameter == null) {
			throw new JRException("No ElasticSearch connection");
		}
		return (ESSearch) valueParameter.getValue();
	}


	@Override
	public JRDataSource createDatasource() throws JRException {
		ESSearch connection = (ESSearch) ((Map<?, ?>) getParameterValue(JRParameter.REPORT_PARAMETERS_MAP))
				.get(JRParameter.REPORT_CONNECTION);
		if (connection == null) {
			connection = processConnection(reportParameters
					.get(JRParameter.REPORT_CONNECTION));
			if (connection == null) {
				throw new JRException("No ES connection");
			}
		}
		// We create a new connection
		// for the datasource based on
		// the one that was handed over
		// to us.
		ESSearch newSearch = connection.clone();
		newSearch.setSearch(getQueryString());
        
		esSearch = connection;
        logger.debug("Create new DataSource witha clone of the current connection.");
        logger.debug("Setting the search to query: " + getQueryString());
		return new ESDataSource(newSearch);
	}

	/**
	 * Replacement of parameters
	 */
	@Override
	protected String getParameterReplacement(String parameterName) {
		logger.debug("Getting replacement for: " + parameterName);
		Object parameterValue = reportParameters.get(parameterName);
		if (parameterValue == null) {
			throw new JRRuntimeException("Parameter \"" + parameterName
					+ "\" does not exist.");
		}
		if (parameterValue instanceof JRValueParameter) {
			parameterValue = ((JRValueParameter) parameterValue).getValue();
		}
		return processParameter(parameterName, parameterValue);
	}

	private String processParameter(String parameterName, Object parameterValue) {
		if (parameterValue instanceof Collection) {
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			for (Object value : (Collection<?>) parameterValue) {
				if (value instanceof String) {
					builder.append("'");
					builder.append(value);
					builder.append("'");
				} else {
					builder.append(String.valueOf(value));
				}
				builder.append(", ");
			}
			if (builder.length() > 2) {
				builder.delete(builder.length() - 2, builder.length());
			}
			builder.append("]");
			logger.debug("Processed parameter: " + builder.toString());
			return builder.toString();
		}
		logger.debug("Adding parameter: " + parameterName);
		parameters.put(parameterName, parameterValue);
		return generateParameterObject(parameterName);
	}

	private String generateParameterObject(String parameterName) {
		return "{'" + parameterName + "':null}";
	}

	public String getProcessedQueryString() {
		return getQueryString();
	}

	@Override
	protected Object getParameterValue(String parameterName, boolean ignoreMissing) {
		try {
			return super.getParameterValue(parameterName, ignoreMissing);
		} catch (Exception e) {
			if (e.getMessage()
					.endsWith(
							"cannot be cast to net.sf.jasperreports.engine.JRValueParameter")
					&& directParameters) {
				return reportParameters.get(parameterName);
			}
		}
		return null;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
    
}