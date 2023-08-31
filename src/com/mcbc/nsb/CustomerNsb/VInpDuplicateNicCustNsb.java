package com.mcbc.nsb.CustomerNsb;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * 
 * Routine to check if user input duplicate legal ID in customer screens
 * 
 * VERSIONS:
 *  CUSTOMER,INDIVIDUAL.NSB
 *  CUSTOMER,NON.INDIVIDUAL.NSB
 *  CUSTOMER,AMEND.INDIVIDUAL.NSB
 *  CUSTOMER,PROSPECT.NSB
 *  CUSTOMER,QUICK.CIF.NSB
 *  
 */
public class VInpDuplicateNicCustNsb extends RecordLifecycle {
    List<String> legalDocList = new ArrayList<String>();
    // 1st item at i=0
    //

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        for (LegalIdClass liClass : customerRec.getLegalId()) {
            
            if (!legalDocList.contains(liClass.getLegalDocName().getValue())){
                legalDocList.add(liClass.getLegalDocName().getValue());    
            } else {
                liClass.getLegalDocName().setError("EB-DUP.LEGALDOC.NSB");
            }
        }
                
        currentRecord.set(customerRec.toStructure());
        return customerRec.getValidationResponse(); 
    }

    
}
