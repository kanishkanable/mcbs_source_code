package com.mcbc.nsb.CustomerMigrationNsb;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *  routine to check customer is deceased
 *  attached to Version: CUSTOMER,AMEND.INDIVIDUAL.NSB
 *  
 */
public class VIdCheckCustomerDeceasedNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Map<String, Map<String, List<TField>>> ParamConfig;
    String deceasedPostRestrict;

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

     // GETTING PARAMETER VALUES
        getParamValues(dataObj);
        
        CustomerRecord customerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", currentRecordId));  
        List<TField> postingRestrictList = customerRec.getPostingRestrict();
        
        for (TField postingRestrict : postingRestrictList){
            String PostRestrict = postingRestrict.getValue();
            if (PostRestrict.equals(deceasedPostRestrict)) {
                throw new T24CoreException("", "EB-CUSTOMER.DECEASED.NSB");
            }
        }
        
        return currentRecordId;
    }
    private void getParamValues(DataAccess DataObj){
        
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "DECEASED.POST.RESTRICT" });
        ParamConfig = Config.GetParamValue(DataObj);
        deceasedPostRestrict = ParamConfig.get("CUSTOMER").get("DECEASED.POST.RESTRICT").get(0).getValue();
    }
    
}
