package com.mcbc.nsb.tfs;

import java.util.List;

//import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
//import com.temenos.t24.api.records.teller.Account1Class;
//import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.records.tellerfinancialservices.TellerFinancialServicesRecord;
import com.temenos.t24.api.records.tellerfinancialservices.TransactionClass;
import com.temenos.t24.api.records.user.UserRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author Prameela
 *
 */
public class VLcyCashWithdrawalUserLimitTfs extends RecordLifecycle {
    DataAccess da = new DataAccess((T24Context) this);
    Session SessionContext = new Session((T24Context) this);

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        TellerFinancialServicesRecord tellerfsRecord = new TellerFinancialServicesRecord(currentRecord);
        try {
            List<TransactionClass> txnValR = tellerfsRecord.getTransaction();

            String userId = SessionContext.getUserId();

            UserRecord userRec = new UserRecord(da.getRecord("USER", userId));

            try {
                double thresholdLimit = Double.parseDouble(userRec.getLocalRefField("L.THRESHOLD.LIMIT").getValue());
                System.out.println("user threshold limit-> " + thresholdLimit);

                for (TransactionClass i : txnValR) {

                    if (i.getTransaction().getValue().equals("LCY.CASHWDL.NSB"))

                    {
                        System.out.println("Transaction-> " + i.getTransaction().getValue());

                        String txnAmountField = i.getAmount().getValue();

                        double txnAmount = Double.parseDouble(txnAmountField);
                        System.out.println("Transaction amtF-> " + txnAmountField);
                        System.out.println("Transaction amt-> " + txnAmount);
                        System.out.println("I Value-> " + i);
                        if ((!userRec.getLocalRefField("L.THRESHOLD.LIMIT").getValue().isEmpty())
                                && (txnAmount > thresholdLimit)) {
                            System.out.println("OVERRIDE CHECK get amount-> " + i.getAmount());
                    
                            i.getAmount().setOverride("User has exceeded Limit Amount " + i.getAmount());
                            
                        }
                    }
                }
            } catch (Exception localRefException) {
            }

        } catch (Exception eu) {
            eu.printStackTrace();
        }
        return tellerfsRecord.getValidationResponse();
     }

}
