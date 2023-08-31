package com.mcbc.nsb.teller;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Delta TT.09
 *
 * @author girlow
 *
 */
public class LCYCashDepositPassbook extends RecordLifecycle {
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        DataAccess da = new DataAccess((T24Context)this);
        
        //TField accountId = ((Account1Class)tellerRecord.getAccount1().get(0)).getAccount1();
        TField accountId = tellerRecord.getAccount2();
        AccountRecord acctRec = new AccountRecord(da.getRecord("ACCOUNT", accountId.toString()));
        
        try
        {
            TField passbook = tellerRecord.getLocalRefField("L.PASSBOOK.FLAG");
            String passbookFlag = passbook.toString();
            //if(acctRec.getPassbook().getValue().equals("Y") && !acctRec.getPassbook().getValue().equals(passbookFlag))
            if(acctRec.getPassbook().getValue().equals("Y") && !passbookFlag.equals(acctRec.getPassbook().getValue()))
            {
                passbook.setOverride("TT-NO.PASSBOOK.NSB");
            }
        } catch(Exception localRefException){}
        
        currentRecord.set(tellerRecord.toStructure());
        
        return tellerRecord.getValidationResponse();
        
    }
}
