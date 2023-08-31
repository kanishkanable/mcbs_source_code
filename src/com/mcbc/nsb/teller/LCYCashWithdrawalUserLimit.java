package com.mcbc.nsb.teller;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.records.user.UserRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class LCYCashWithdrawalUserLimit extends RecordLifecycle {

    DataAccess da = new DataAccess(this);
    Session SessionContext = new Session(this);
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        
        String userId = SessionContext.getUserId();
        
        TField txnAmountField = ((Account1Class)tellerRecord.getAccount1().get(0)).getAmountLocal1();
        double txnAmount = Double.parseDouble(txnAmountField.getValue());
        UserRecord userRec = new UserRecord(da.getRecord("USER", userId));

        try
        {
            double thresholdLimit = Double.parseDouble(userRec.getLocalRefField("L.THRESHOLD.LIMIT").getValue());
            System.out.println("user threshold limit-> "+thresholdLimit);
            if(!userRec.getLocalRefField("L.THRESHOLD.LIMIT").getValue().isEmpty() && txnAmount > thresholdLimit)
            {
                txnAmountField.setOverride("TT-USER.THRESHOLD.NSB");
            }
        } catch(Exception localRefException){}
        
        return tellerRecord.getValidationResponse();
    }

}
