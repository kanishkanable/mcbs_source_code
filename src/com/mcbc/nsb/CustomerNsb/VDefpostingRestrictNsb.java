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
public class VDefpostingRestrictNsb extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
    
        CustomerRecord currCustomerRec = new CustomerRecord(currentRecord);
        CustomerRecord liveCustomerRec = new CustomerRecord(liveRecord);
        
        compareAndSetPostingRestrictCount(currCustomerRec, liveCustomerRec);
    
    }

    private CustomerRecord compareAndSetPostingRestrictCount(CustomerRecord currCustomerRec, CustomerRecord liveCustomerRec){
        
        int currPostingRestCount = currCustomerRec.getPostingRestrict().size(); 
        int livePostingRestCount = liveCustomerRec.getPostingRestrict().size();
//        int currStEndDateGrpCount = currCustomerRec.getLocalRefGroups("L.START.DATE").size();
        if (currPostingRestCount > livePostingRestCount){
            
        }
            
        return currCustomerRec;
    }
    
    
}
