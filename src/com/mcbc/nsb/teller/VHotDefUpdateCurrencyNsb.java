package com.mcbc.nsb.teller;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VHotDefUpdateCurrencyNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    AccountRecord accountRec;
    
    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
    
        TellerRecord tellerRec = new TellerRecord(currentRecord);
        String debitAccount = tellerRec.getAccount2().getValue();
        
        if (!debitAccount.isEmpty()){
            try {
                accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", debitAccount));
            } catch (Exception e) {
                AaArrangementRecord aaRec= new AaArrangementRecord(dataObj.getRecord("ACCOUNT", debitAccount));
                debitAccount = aaRec.getLinkedAppl().get(0).getLinkedAppl().getValue();
                accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", debitAccount));
            }
            String currency = accountRec.getCurrency().getValue();
            tellerRec.setCurrency2(currency);
        }
        currentRecord.set(tellerRec.toStructure());
    }
}
