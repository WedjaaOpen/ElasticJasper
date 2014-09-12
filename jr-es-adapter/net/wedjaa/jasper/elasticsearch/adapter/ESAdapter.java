
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

import net.sf.jasperreports.data.DataAdapter;

/**
 *
 * @author Fabio Torchetti
 */
public interface ESAdapter extends DataAdapter {

    public void setElasticSearchHost(String esHost);

    public String getElasticSearchHost();

    public void setElasticSearchPort(String esPort);

    public String getElasticSearchPort();

    public void setElasticSearchPassword(String esPassword);

    public String getElasticSearchPassword();

    public void setElasticSearchUsername(String esUsername);

    public String getElasticSearchUsername();

    public void setElasticSearchIndexes(String esIndexes);

    public String getElasticSearchIndexes();

    public void setElasticSearchTypes(String esTypes);

    public String getElasticSearchTypes();
    
    public void setElasticSearchCluster(String esCluster);
    
    public String getElasticSearchCluster();
    
    public String getElasticSearchMode();
    
    public void setElasticSearchMode(String searchMode);
    
}