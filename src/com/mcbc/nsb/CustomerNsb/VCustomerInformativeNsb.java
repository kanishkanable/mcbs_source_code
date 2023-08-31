package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
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
public class VCustomerInformativeNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);

        // GETTING PARAMETER VALUES
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "PO.INFO" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        String PoBlocking = ParamConfig.get("CUSTOMER").get("PO.INFO").get(0).getValue();

        for (LocalRefGroup porestCnt : CustomerRec.getLocalRefGroups("L.PO.INFORM")) {
            if (porestCnt.getLocalRefField("L.PO.INFORM").getValue().equals(PoBlocking)) {
                CustomerRec = CheckStartEndRemarks(porestCnt, CustomerRec);
            }
        }

        currentRecord.set(CustomerRec.toStructure());

        return CustomerRec.getValidationResponse();
    }

    private CustomerRecord CheckStartEndRemarks(LocalRefGroup porestCnt, CustomerRecord CustomerRec) {

        try {
            CustomerRec.getLocalRefGroups("L.START.DATE").get(0);
        } catch (Exception e) {
            return CustomerRec;
           // throw new T24CoreException("", "EB-PO.STARTDATE.MV.NSB");
        }

        for (int i = 0; (CustomerRec.getLocalRefGroups("L.START.DATE").size()) > i; i++) {
           
            String StartDate = CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.START.DATE").getValue();
            if (StartDate.isEmpty()) {
                CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.START.DATE").setError("EB-STARTDT.MAND");
            }

            String EndDate = CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.END.DATE").getValue();
            if (EndDate.isEmpty()) {
                CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.END.DATE").setError("EB-ENDDT.MAND");
            }

            String PoNarr = CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.PO.NARR").getValue();
            if (PoNarr.isEmpty()) {
                CustomerRec.getLocalRefGroups("L.START.DATE").get(i).getLocalRefField("L.PO.NARR").setError("EB-NARRATIVE.MAND");
            }
        }
        return CustomerRec;
    }
}
