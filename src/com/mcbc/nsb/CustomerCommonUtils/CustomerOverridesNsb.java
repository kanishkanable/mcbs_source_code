package com.mcbc.nsb.CustomerCommonUtils;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerOverridesNsb {

    private boolean OverrideSet1 = false;
    private boolean OverrideSet2 = false;
    private List<TField> Override1 = null;
    private List<TField> Override2 = null;

    public void setCustomerOverride(CustomerRecord CustomerRec, DataAccess DataObj) {
        setParamValues(DataObj);

        for (LegalIdClass li : CustomerRec.getLegalId()) {
            SetOverrideLegalID(li);
        }
        
        if (OverrideSet2) {
            CustomerRec.getLegalId().get(0).getLegalId().setOverride("EB-AUTH.LEVEL2");
        } else if (OverrideSet1) {
            CustomerRec.getLegalId().get(0).getLegalId().setOverride("EB-AUTH.LEVEL1");
        }
        
        setOverridePepStatus(CustomerRec);
    }

    private void setParamValues(DataAccess DataObj) {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "OVERRIDE.ONE", "OVERRIDE.TWO" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        this.Override1 = ParamConfig.get("CUSTOMER").get("OVERRIDE.ONE");
        this.Override2 = ParamConfig.get("CUSTOMER").get("OVERRIDE.TWO");
    }

    private void SetOverrideLegalID(LegalIdClass li) {
        for (int j = 0; Override2.size() > j; j++) {
            if (li.getLegalDocName().getValue().equals(Override2.get(j).getValue())) {
                this.OverrideSet2 = true;
            }
        }
        for (int i = 0; Override1.size() > i; i++) {
            if (li.getLegalDocName().getValue().equals(Override1.get(i).getValue())) {
                this.OverrideSet1 = true;
            }
        }
    }

    private void setOverridePepStatus(CustomerRecord CustomerRec) {
        String PepValue = CustomerRec.getCalcRiskClass().getValue();
        if (PepValue.startsWith("PEP")) {
            CustomerRec.getCalcRiskClass().setOverride("EB-PEP.OVERRIDE");
        }
    }
}