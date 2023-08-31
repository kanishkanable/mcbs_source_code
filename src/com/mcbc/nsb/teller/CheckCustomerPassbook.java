package com.mcbc.nsb.teller;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.TellerRecord;

/**
* This Java program will check whether the customer who came to the counter
* brought his passbook with him. The teller will flag the local field with 'Y'
* or 'N'. In case it is 'N', an override will be raised.
*
* @author  girlow
* 
*/
public class CheckCustomerPassbook extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        Account1Class acctOne = new Account1Class(currentRecord);
        acctOne.getAmountFcy1();
        try
        {
            TField passbookFlag = tellerRecord.getLocalRefField("L.PASSBOOK.FLAG");
            if (!passbookFlag.toString().equals("Y"))
            {
                passbookFlag.setOverride("TT-NO.PASSBOOK.NSB");
            }
        } catch(Exception localRefException)
        {
            
        }
        
        return tellerRecord.getValidationResponse();
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        
        
    }
        
}
