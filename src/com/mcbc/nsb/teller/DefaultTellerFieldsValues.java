package com.mcbc.nsb.teller;

import java.time.LocalDateTime;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.teller.TellerRecord;

/**
 * This Java program will default some values upon opening the version.
 *
 * @author girlow
 *
 */
public class DefaultTellerFieldsValues extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        CompanyRecord companyRecord = new CompanyRecord(currentRecord);
        TField serialNo = tellerRecord.getLocalRefField("L.SERIAL.NO");
        TField companyCode = tellerRecord.getLocalRefField("L.INSTIT.CODE");
        TField branchCode = tellerRecord.getLocalRefField("L.BRANCH.CODE");
        TField txnReference = tellerRecord.getLocalRefField("L.TXN.REF");
        
        String coCode = companyRecord.getCoCode();
        
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();  
        String concatSerial = coCode + branchCode.getValue() + currentYear + currentRecordId;
        System.out.println("concatSerial-> " + concatSerial);

        try
        {
            serialNo.setValue(concatSerial);
            companyCode.setValue(coCode);
            branchCode.setValue("BRANCH CODE");
            txnReference.setValue(currentRecordId);
            
            currentRecord.set(tellerRecord.toStructure());
            
        } catch(Exception localRefException)
        {
            System.out.println("Exception in DefaultTellerFieldsValues: " + localRefException);
        }
        
    }

}
