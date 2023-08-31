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
public class VDefCustomerName1Nsb extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        System.out.println("*** update short name ***   20   ***");
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        System.out.println("*** update short name ***   22   ***");
        String shortName = customerRec.getShortName(0).getValue();
        System.out.println("*** update short name ***   24    ****   " + shortName);
        customerRec.setName1(shortName, 0);
        System.out.println("*** update short name ***   26    ****   " + shortName);
        
        
        currentRecord.set(customerRec.toStructure());
    }

    
}
