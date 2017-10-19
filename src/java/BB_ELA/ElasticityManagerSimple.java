/**Copyright 2016, University of Messina.
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package BB_ELA;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import MDBInt.DBMongo;
import BB_ELA.Policies.SunLightPolicy;
import java.util.logging.Level;
//import osffmcli.OSFFM_ELA.Policies.SunLight_Policy;

/**
 * Simple elasticity module, It provides a function that simulate Vm suffer.
 * As answer for this event the VM it will be shutdown and start on another cloud.
 * @author Giuseppe Tricomi
 */
public class ElasticityManagerSimple {
     static final Logger LOGGER = Logger.getLogger(ElasticityManagerSimple.class);
     DBMongo m;
     static HashMap <String,HashMap <String,SunLightPolicy>>tenantHash=new HashMap<String,HashMap <String,SunLightPolicy>>();
    public ElasticityManagerSimple() {
        this.m=new DBMongo();
        //this.m.init();
       // this.m.init("../webapps/OSFFM/WEB-INF/Configuration_bit");
        this.m.connectLocale("10.9.240.1");//this.m.connectLocale(this.m.getMdbIp());
    }
    public ElasticityManagerSimple(DBMongo m) {
        this.m=m;
    }
    public ElasticityManagerSimple startMonitoringThreads(String tenant,String stack,HashMap<String,ArrayList<ArrayList<String>>> dcList,String userFederation, String pswFederation,String minimumgap,String firstCloudID,String templateName) throws ElasticityPolicyException{
        HashMap <String,SunLightPolicy> monitoringPolicy=new HashMap<String,SunLightPolicy>();
        HashMap<String, Object> paramsMap=new HashMap<String, Object>();
        paramsMap.put("tenantName", tenant);
        paramsMap.put("templateName", templateName);
        paramsMap.put("stack", stack);
        paramsMap.put("dcList", dcList.get(stack));
        paramsMap.put("mongoConnector", this.m); 
        paramsMap.put("userFederation", userFederation);
        paramsMap.put("pswFederation",pswFederation);
        paramsMap.put("minimumGap",minimumgap);
        paramsMap.put("firstCloudID",firstCloudID);
        SunLightPolicy slp=null;
         try {
             slp = new SunLightPolicy(paramsMap);
         } catch (Exception ex) {
             java.util.logging.Logger.getLogger(ElasticityManagerSimple.class.getName()).log(Level.SEVERE, null, ex);
         }
        monitoringPolicy.put(stack, slp);
        System.out.println("Starting Monitoring Thread");
        slp.run();
        System.out.println("End startMonitoringThreads");
        tenantHash.put(tenant,monitoringPolicy);
        return this;
    }  
    
    
}
