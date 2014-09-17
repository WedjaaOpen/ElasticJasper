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

package net.wedjaa.jasper.elasticsearch.datasource;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseField;
import net.wedjaa.elasticparser.ESSearch;

/**
 *
 * @author Fabio Torchetti
 */
public class ESDataSourceProvider implements JRDataSourceProvider {

    private class ESField extends JRBaseField {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		ESField(String name, String description, Class<?> objType ) {
            this.name = name;
            this.description = description;
            this.valueClass = objType;
            this.valueClassName = objType.getName();
        }
        
		@SuppressWarnings("unused")
		ESField(String name, String description) {
            this(name, description, String.class);
        }
    }
    
    @Override
    public boolean supportsGetFieldsOperation() {
        return true;
    }

    private ESSearch getESSearch(JasperReport jr) {

        ESSearch esSearch = null;
        
        if ( jr == null ) {
            return esSearch;
        }
        
        JRValueParameter[] reportParameters = (JRValueParameter[]) jr.getParameters();
        for (JRValueParameter parameter: reportParameters) {
            if ( parameter.getName().equals(JRParameter.REPORT_CONNECTION) ) {
                esSearch = (ESSearch) parameter.getValue();
                break;
            }
        }
        
        return esSearch;
        
    }
    
    @Override
    public JRField[] getFields(JasperReport jr) throws JRException {
        
        ESSearch esSearch = getESSearch(jr);

        if ( esSearch == null) {
            throw new JRException("No ElasticSearch connection for this report!!");
        }
        
        Map<String, Class<?>> fields = esSearch.getFields();
        
        JRField[] result = new JRField[fields.size()];
        int idx=0;
        
        for (String field: fields.keySet()) {
          result[idx] = new ESField(field, field, fields.get(field));
          idx++;
        }
        
        return result;
    }

    @Override
    public JRDataSource create(JasperReport jr) throws JRException {

        
        ESSearch esSearch = getESSearch(jr);
        
        if ( esSearch == null) {
            throw new JRException("No ElasticSearcg connection for this report!!");
        }
        
        return new ESDataSource(esSearch);
        
    }

    @Override
    public void dispose(JRDataSource jrds) throws JRException {
        ((ESDataSource) jrds).dispose();
    }
    
}
