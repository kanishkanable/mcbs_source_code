package com.mcbc.nsb.CustomerMigrationNsb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *  routine to check duplicate NIC during after unauth routine
 *  attached to all customer versions
 *  
 *
 */
public class VAuthCustDuplicateValuesNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    String legDocNic;
    String legDocNicOld;
    String nicId;
    Boolean duplicateNic = false;
    String customerId;

    
    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<com.temenos.t24.api.complex.eb.templatehook.TransactionData> transactionData,
            List<TStructure> currentRecords) {
        // TODO Auto-generated method stub
        
        // GETTING PARAMETER VALUES
        setParamValues(dataObj);
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        
        for (LegalIdClass li : customerRec.getLegalId()) {
            String nicDocValue = li.getLegalDocName().getValue();
            if ((nicDocValue.equals(legDocNic))
                    || (nicDocValue.equals(legDocNicOld))) {
            checkDuplicateNic(customerRec, li, currentRecordId, nicDocValue);
            }
            if (duplicateNic){
                //li.getLegalId().setError("EB-DUPLICATE.NIC" + customerId);
                throw new T24CoreException("", "Duplicate NIC with Customer " + customerId);
            }
        }
        
        currentRecord.set(customerRec.toStructure());
        
    }

/*    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        
    }
*/
    private void checkDuplicateNic(CustomerRecord customerRec, LegalIdClass li, String currentRecordId, String nicDocValue) {
        String nicValue = li.getLegalId().getValue();
        String nicValueNew = null;
        if (nicValue.length() == 12) {
            nicId = nicValue + "-" + nicDocValue;
            setDuplicateLegalIdError(nicId, li, currentRecordId);   
        } else if (nicValue.length() == 10) {
            nicId = nicValue + "-" + nicDocValue;
            setDuplicateLegalIdError(nicId, li, currentRecordId);
            if (nicValue.endsWith("V")) {
                nicValueNew = nicValue.subSequence(0, 9) + "X-" + nicDocValue;
            } else if (nicValue.endsWith("X")) {
                nicValueNew = nicValue.subSequence(0, 9) + "V-" + nicDocValue;
            }
            setDuplicateLegalIdError(nicValueNew, li, currentRecordId);
        }
    }

    private String setDuplicateLegalIdError(String nicId, LegalIdClass li, String currentRecordId) {
        try {
            List<String> CustomerList = dataObj.getConcatValues("CUS.LEGAL.ID", nicId);
            Iterator<String> listIterator = CustomerList.iterator();
            while (listIterator.hasNext()) {
                customerId = listIterator.next();
                if (!customerId.equals(currentRecordId)) {
                    duplicateNic = true;
//                    li.getLegalId().setError("EB-DUPLICATE.NIC" + CustomerId);
                }
            }
        } catch (Exception e) {
        }
        return customerId;
    }

    private void setParamValues(DataAccess dataObj) {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "LEGAL.AGE.LK.DOC" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        this.legDocNic = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(0).getValue();
        this.legDocNicOld = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(1).getValue();
    }
}
