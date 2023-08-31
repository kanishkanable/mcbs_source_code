package com.mcbc.nsb.CustomerRiskProfile;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.EmploymentStatusClass;
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
    GetParamValueNsb config = new GetParamValueNsb();

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        System.out.println("defaultFieldValues  34 ");
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        System.out.println("defaultFieldValues  36 ");
        
        // Risk Level based on TARGET
        String targetValue = customerRec.getTarget().getValue();
        System.out.println("defaultFieldValues  40 : targetValue = " + targetValue);
        String riskValue = "";
        System.out.println("defaultFieldValues  42 : riskValue = " + riskValue);
        try {
            System.out.println("defaultFieldValues  44 : riskValue = ");
            riskValue = getParamValues(targetValue);
            System.out.println("defaultFieldValues  46 : riskValue = " + riskValue);
        } catch (Exception e) {
            System.out.println("defaultFieldValues  48 : riskValue = " + riskValue);
            throw new T24CoreException("MISSING TARGET : " + targetValue, "EB-CRP.MISSING.TARGET");
        }
        
        System.out.println("defaultFieldValues  52 : riskValue = " + riskValue);
        customerRec = setRiskLevel(riskValue, customerRec);
        System.out.println("defaultFieldValues  54 : riskValue = ");
        
        // Risk Level based on Job Title
        List<EmploymentStatusClass> employmentClass = customerRec.getEmploymentStatus();
        System.out.println("defaultFieldValues  58 : employmentClass = ");
        for (EmploymentStatusClass employment : employmentClass) {
            System.out.println("defaultFieldValues  60 : employment = " + employment);
            String jobTitle = employment.getJobTitle().getValue();
            System.out.println("defaultFieldValues  62 : jobTitle = " + jobTitle);
            try {
                System.out.println("defaultFieldValues  64 : jobTitle = " + jobTitle);
                riskValue = getParamValues(jobTitle);
                System.out.println("defaultFieldValues  66 : jobTitle = " + riskValue);
            } catch (Exception e) {
                System.out.println("defaultFieldValues  68 : jobTitle = " + riskValue);
                throw new T24CoreException("MISSING JOB.TITLE : " + jobTitle, "EB-CRP.MISSING.JOBTITLE");
            }
            
            System.out.println("defaultFieldValues  72 : jobTitle = " + riskValue);
            customerRec = setRiskLevel(riskValue, customerRec);
            System.out.println("defaultFieldValues  74 : jobTitle = " );
        }
        System.out.println("defaultFieldValues  76 : jobTitle = " );
        
        // Risk Level based on Blacklist Customer
        String blackList = customerRec.getLocalRefField("L.BLACK.LIST").getValue();
        System.out.println("defaultFieldValues  80 : blackList = " + blackList);
        if (blackList.equals("YES")) {
            System.out.println("defaultFieldValues  82 : blackList = " + blackList);
            riskValue = "HIGH";
            System.out.println("defaultFieldValues  84 : riskValue = " + riskValue);
            customerRec = setRiskLevel(riskValue, customerRec);
            System.out.println("defaultFieldValues  86 : riskValue = " );
        }
        System.out.println("defaultFieldValues  88 : riskValue = " );
        
        // Risk Level based on Non-Resident
        String residence = customerRec.getResidence().getValue();
        System.out.println("defaultFieldValues  92 : residence = " + residence);
        if (!residence.equals("LK")) {
            System.out.println("defaultFieldValues  94 : residence = " + residence);
            riskValue = "HIGH";
            System.out.println("defaultFieldValues  96 : riskValue = " + riskValue);
            customerRec = setRiskLevel(riskValue, customerRec);
            System.out.println("defaultFieldValues  98 : residence = ");
        }
        System.out.println("defaultFieldValues  100 : residence = ");
        
        currentRecord.set(customerRec.toStructure());
    }

    private String getParamValues(String paramName) {
        System.out.println("defaultFieldValues  106 : paramName = " + paramName);
        config.AddParam("CUSTOMER.RISK.PROFILE", new String[] { paramName });
        Map<String, Map<String, List<TField>>> ParamConfig = config.GetParamValue(dataObj);
        String riskValue = "";
        riskValue = ParamConfig.get("CUSTOMER.RISK.PROFILE").get(paramName).get(0).getValue();
        System.out.println("defaultFieldValues  111 : riskValue = " + riskValue);
        
        return riskValue;
    }

    private CustomerRecord setRiskLevel(String riskValue, CustomerRecord customerRec) {
        System.out.println("defaultFieldValues  117 : riskValue = " + riskValue);
        
        if (!riskValue.equals("NA")){
            if (riskValue.equals("HIGH")){
                System.out.println("riskValue to high 1 : " + riskValue);
                riskValue = "1";
                System.out.println("riskValue to high 2 : " + riskValue);
            }
            System.out.println("riskValue to high 3 : " + riskValue);
            if (riskValue.equals("MEDIUM")){
                System.out.println("riskValue to medium 1 : " + riskValue);
                riskValue = "2";
                System.out.println("riskValue to medium 2 : " + riskValue);
            }
            System.out.println("riskValue to medium 3 : " + riskValue);
            if (riskValue.equals("LOW")){
                System.out.println("riskValue to low 1 : " + riskValue);
                riskValue = "3";
                System.out.println("riskValue to low 2 : " + riskValue);
            }
            System.out.println("riskValue to low 3 : " + riskValue);
        }
        
        Boolean riskLevelBool = false;
        if (customerRec.getRiskAssetType().toString().contains(riskValue)) {
            System.out.println("defaultFieldValues  119 : customerRec.getRiskAssetType() = " + customerRec.getRiskAssetType().toString());
            riskLevelBool = true;
            System.out.println("defaultFieldValues  121 : riskValue = " + riskValue);
        }
        System.out.println("defaultFieldValues  123 : riskValue = " + riskValue);
        
        if ((!riskLevelBool) && (!riskValue.equals("NA"))) {
            System.out.println("defaultFieldValues  126 : riskLevelBool = " + riskLevelBool);
            RiskAssetTypeClass RiskAssetType = new RiskAssetTypeClass();
            System.out.println("defaultFieldValues  128 : riskValue = " + riskValue);
            RiskAssetType.setRiskLevel(riskValue);
            System.out.println("defaultFieldValues  130 : RiskAssetType = " + RiskAssetType);
            customerRec.addRiskAssetType(RiskAssetType);
            System.out.println("defaultFieldValues  132 : RiskAssetType = " + RiskAssetType.getRiskLevel().getValue());
        }
        System.out.println("defaultFieldValues  134 : riskValue = " + riskValue);
        
        return customerRec;
    }
}
