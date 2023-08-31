package com.mcbc.nsb.teller;

import java.util.Arrays;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.TellerRecord;

/**
 * This Java program will default values just before commit of record.
 *
 * @author girlow
 *
 */
public class GenerateTellerFieldsValues extends RecordLifecycle {  
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        TField exchangeRate = tellerRecord.getLocalRefField("L.EXCH.RATE");
        TField txnAmount = tellerRecord.getLocalRefField("L.TXN.AMOUNT");
        
        try
        {
            exchangeRate.setValue(tellerRecord.getDealRate().getValue());
            txnAmount.setValue(tellerRecord.getAmountFcy2().getValue());
            
            currentRecord.set(tellerRecord.toStructure());
            
        } catch(Exception localRefException)
        {
            System.out.println("Exception in GenerateTellerFieldsValues: " + localRefException);
        }
        
    }
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        //TField exchangeRate = tellerRecord.getLocalRefField("L.EXCH.RATE");
        //TField txnAmount = tellerRecord.getLocalRefField("L.TXN.AMOUNT");
        Account1Class acctOne = new Account1Class(currentRecord);

        System.out.println("Amount fcy->" + acctOne.getAmountFcy1().getValue());
        System.out.println("Date/Version->" + Account1Class.getBuildDate() + " " + Account1Class.getBuildVersion());
        System.out.println("ACCOUNT1CLASS OBJ->" + acctOne.getClass().getFields());
        System.out.println("Array 1->" + Arrays.toString(acctOne.getClass().getFields()));
        System.out.println("Array 2->" + Arrays.deepToString(acctOne.getClass().getFields()));
        
        try
        {
            //exchangeRate.setValue(tellerRecord.getDealRate().getValue());
            //txnAmount.setValue(tellerRecord.getAmountFcy2().getValue());
            
            currentRecord.set(tellerRecord.toStructure());
            
        } catch(Exception localRefException)
        {
            System.out.println("Exception in GenerateTellerFieldsValues: " + localRefException);
        }
        
        return tellerRecord.getValidationResponse();
    }
    
}
