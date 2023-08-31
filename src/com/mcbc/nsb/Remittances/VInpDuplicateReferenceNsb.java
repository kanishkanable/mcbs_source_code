package com.mcbc.nsb.Remittances;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.Session;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpDuplicateReferenceNsb extends RecordLifecycle {

    Session ssn = new Session(this);
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        
        
        return super.validateRecord(application, currentRecordId, currentRecord, unauthorisedRecord, liveRecord,
                transactionContext);
    }

    
}
