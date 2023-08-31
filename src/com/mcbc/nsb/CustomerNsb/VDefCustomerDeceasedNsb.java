package com.mcbc.nsb.CustomerNsb;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefCustomerDeceasedNsb extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        String DateOfDeath = CustomerRec.getDeathDate().getValue();
        if (!DateOfDeath.isEmpty()) {
            CustomerRec.getLocalRefField("L.CUSTOMER.TYPE").setValue("DECEASED");
        }
        
        currentRecord.set(CustomerRec.toStructure());
    }

    
}
