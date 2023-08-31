package com.mcbc.nsb.NrfcOnlineAccountOpening;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * DEFAULT MNEMONIC for NRFC customers
 */
public class VCusDefaultMnemonicNrfcNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        
        String mnemonic = "C-" + currentRecordId;
        customerRec.setMnemonic(mnemonic);
        
        currentRecord.set(customerRec.toStructure());
    }
    
}
