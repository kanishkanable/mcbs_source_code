package com.mcbc.nsb.teller;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.TellerRecord;

/**
 * This Java program will check if the transaction involves cross-currencies
 * and raise overrides accordingly.
 *
 * @author girlow
 *
 */
public class CheckCrossCurrencyTransaction extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        try
        {
            TField debitCurency = tellerRecord.getCurrency1();
            TField creditCurency = tellerRecord.getCurrency2();
            System.out.println("debitCurency->" + debitCurency);
            System.out.println("creditCurency->" + creditCurency);
            
            if (!debitCurency.toString().equals(creditCurency.toString()))
            {
                debitCurency.setOverride("TT-CROSS.CURRENCY.NSB");
                creditCurency.setOverride("");
            }
        } catch(Exception localRefException)
        {
            
        }
        
        return tellerRecord.getValidationResponse();
    }

}
