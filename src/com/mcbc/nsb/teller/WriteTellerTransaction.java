package com.mcbc.nsb.teller;

import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.tellerdefault.TellerDefaultRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.eballtransactionlognsb.EbAllTransactionLogNsbRecord;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class WriteTellerTransaction extends RecordLifecycle {

    TField transactionCurrency;
    TField transactionAccount;
    TField transactionAmount;
    TField transactionValueDate;
    
    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        DataAccess DataObj = new DataAccess(this);
        TellerDefaultRecord tellerRecord = new TellerDefaultRecord(currentRecord);
        
        if (tellerRecord.getAmountFcy1().toString().length() == 0)
        {
            transactionAmount = tellerRecord.getAmountLocal1();
        }
        else
        {
            transactionAmount = tellerRecord.getAmountFcy1();
        }
        
        transactionCurrency = tellerRecord.getCurrency2();
        transactionAccount = tellerRecord.getAccount2();
        transactionValueDate = tellerRecord.getValueDate2();
        
        EbAllTransactionLogNsbRecord transactionLogRecord;
        try {
            transactionLogRecord = new EbAllTransactionLogNsbRecord(DataObj.getRecord("EB.ALL.TRANSACTION.LOG.NSB", currentRecordId));
        } catch (Exception e1) {
            transactionLogRecord = new EbAllTransactionLogNsbRecord();
        }
        
//        System.out.println("Account -> " + transactionAccount);
//        System.out.println("Currency -> " + transactionCurrency);
//        System.out.println("Amount Local -> " + transactionAmount);
//        System.out.println("Value Date -> " + transactionValueDate);
        
        //transactionLogRecord.setAccount(transactionAccount);
        //transactionLogRecord.setTxnCurrency(transactionCurrency);
        //transactionLogRecord.setTxnAmount(transactionAmount);
        transactionLogRecord.setValueDate(transactionValueDate);
        currentRecords.add(transactionLogRecord.toStructure());

        TransactionData td = new TransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setUserName("INPUTT");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(currentRecordId);
        td.setVersionId("EB.ALL.TRANSACTION.LOG.NSB,UPDATE.NSB");
        transactionData.add(td);
    }

}
