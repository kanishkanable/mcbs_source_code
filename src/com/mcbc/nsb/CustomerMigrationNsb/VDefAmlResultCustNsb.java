package com.mcbc.nsb.CustomerMigrationNsb;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefAmlResultCustNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Map<String, Map<String, List<TField>>> ParamConfig;
    String amlCheckNo;
    String amlResultNo;
    Boolean checkNoExist = false;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
     // GETTING PARAMETER VALUES
        getParamValues(dataObj);
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        String amlCheck = customerRec.getLocalRefField("L.AML.CHECK").getValue();
        
        if (amlCheck.equals(amlCheckNo)) {
            LocalRefList amlResultGroup = customerRec.getLocalRefGroups("L.AML.RESULT");
            for (LocalRefGroup amlResult : amlResultGroup) {
                if (amlResult.getLocalRefField("L.AML.RESULT").getValue().equals(amlResultNo)){
                    checkNoExist = true;
                }
            }
            
            if (!checkNoExist) {
//                customerRec.getLocalRefGroups("").set(customerRec.getLocalRefGroups("").size(), amlResult);
                LocalRefGroup setAmlResult = customerRec.createLocalRefGroup("L.AML.RESULT");
                setAmlResult.getLocalRefField("L.AML.RESULT").setValue(amlResultNo);
                LocalRefList localRefList = customerRec.getLocalRefGroups("L.AML.RESULT");
                localRefList.add(setAmlResult);
            }
        }
        currentRecord.set(customerRec.toStructure());
    }
    
 private void getParamValues(DataAccess dataObj){
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("AML.CHECK", new String[] { "AML.CHECK.VALUE", "AML.RESULT.VALUE" });
        ParamConfig = Config.GetParamValue(dataObj);
        amlCheckNo = ParamConfig.get("AML.CHECK").get("AML.CHECK.VALUE").get(0).getValue();
        amlResultNo = ParamConfig.get("AML.CHECK").get("AML.RESULT.VALUE").get(0).getValue();
    }
}
