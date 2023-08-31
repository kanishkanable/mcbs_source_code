package com.mcbc.nsb.cheque;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.stockentry.StockEntryRecord;
import com.temenos.t24.api.system.Session;

public class VDefstockentryNsb extends RecordLifecycle{

    StockEntryRecord stckRec = null;
    Session sess = new Session(this);
    String compId = "";
    String chq = "DRAFT";
    String stockReg = "";
    
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        stckRec = new StockEntryRecord(currentRecord);
        compId = sess.getCompanyId();
        stockReg = chq+"."+compId;
        stckRec.setToRegister(stockReg);
        currentRecord.set(stckRec.toStructure());
    }
}
