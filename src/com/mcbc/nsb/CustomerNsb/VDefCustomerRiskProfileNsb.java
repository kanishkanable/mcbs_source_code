package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.RiskAssetTypeClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefCustomerRiskProfileNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Boolean RiskLevelBool = false;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        String targetValue = customerRec.getTarget().getValue();
        String riskValue = getParamValues(targetValue);
        
        if ((!riskValue.isEmpty()) || (!riskValue.equals("NA"))){
            for (RiskAssetTypeClass RiskAssetClass : customerRec.getRiskAssetType()) {
                if (RiskAssetClass.getRiskLevel().getValue().equals(riskValue)) {
                    RiskLevelBool = true;
                }
            }
            if (!RiskLevelBool) {
                RiskAssetTypeClass RiskAssetType = new RiskAssetTypeClass();
                RiskAssetType.setRiskLevel(riskValue);
                customerRec.addRiskAssetType(RiskAssetType);
            }
        }
        
        currentRecord.set(customerRec.toStructure());
    }

    private String getParamValues(String targetValue){
        
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER.RISK.PROFILE", new String[] { targetValue });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        String riskValue = ParamConfig.get("CUSTOMER.RISK.PROFILE").get(targetValue).get(0).getValue();
        
        return riskValue;
    }    
}
