package com.mcbc.nsb.CustomerNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpLegalDocNameNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        try {
            CustomerRec.getLegalId().get(0).getLegalId().getValue();
            for (LegalIdClass legalIdClass : CustomerRec.getLegalId()) {
                if (legalIdClass.getLegalId().getValue().isEmpty()){
                    legalIdClass.getLegalId().setError("EB-INP.MISS");
                }
                if (legalIdClass.getLegalDocName().getValue().isEmpty()){
                    legalIdClass.getLegalDocName().setError("EB-INP.MISS");
                }
            }
        } catch (Exception e) {
            throw new T24CoreException("", "EB-LEGAL.ID.MSND.NSB");
        }
        
        currentRecord.set(CustomerRec.toStructure());
        return CustomerRec.getValidationResponse();        
    }

}
