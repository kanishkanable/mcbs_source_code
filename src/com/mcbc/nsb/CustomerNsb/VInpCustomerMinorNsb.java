package com.mcbc.nsb.CustomerNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *  ROUTINE TO CHECK CUSTOMER IS MINOR OR INDIVIDUAL BASED ON DATE.OF.BIRTH AND DISPLAY ERRORS
 *  
 *  ATTACHED TO BELOW VERSIONS
 *      CUSTOMER,INDIVIDUAL.NSB
 *      CUSTOMER,NON.INDIVIDUAL.NSB
 *      CUSTOMER,AMEND.INDIVIDUAL.NSB
 *      CUSTOMER,PROSPECT.NSB
 *      CUSTOMER,QUICK.CIF.NSB
 *  
 *
 */
public class VInpCustomerMinorNsb extends RecordLifecycle {

    Date SystemDate = new Date(this);

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        String age = customerRec.getLocalRefField("L.CUST.AGE").getValue();
        if (age.startsWith("0")){
            if (age.startsWith("00")){
                age = age.substring(2, 3);
            } else {
                age = age.substring(1, 3);
            }
        } else {
            age = age.substring(0, 3);
        }
        String target = customerRec.getTarget().getValue();
        if ((Integer.parseInt(age) < 16) && (!target.equals("1002"))) {
            customerRec.getTarget().setError("EB-CUS.MINOR.NSB");
        } 
        if ((Integer.parseInt(age) >= 16) && (target.equals("1002"))){
            customerRec.getTarget().setError("EB-CUS.NOTMINOR.NSB");
        }
        currentRecord.set(customerRec.toStructure());
        
        return customerRec.getValidationResponse();
    }
}
