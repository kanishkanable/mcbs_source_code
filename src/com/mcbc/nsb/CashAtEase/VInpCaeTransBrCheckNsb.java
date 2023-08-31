package com.mcbc.nsb.CashAtEase;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCaeTransBrCheckNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Session session = new Session(this);
    String companyCode = null;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        PaymentOrderRecord poRec = new PaymentOrderRecord(currentRecord);
        
        String debitAccount = poRec.getDebitAccount().getValue();
        try{
            AccountRecord accRec = new AccountRecord(dataObj.getRecord("ACCOUNT", debitAccount));
            companyCode = accRec.getCoCode();
            if (!session.getCompanyId().equals(companyCode)) {
                poRec.getDebitAccount().setError("Account is not in same branch");
            }
        } catch (T24CoreException e) {
            poRec.getDebitAccount().setError("Accoount does not exist");
        }
        
        String creditAccount = poRec.getCreditAccount().getValue();
        try{
            AccountRecord accRec = new AccountRecord(dataObj.getRecord("ACCOUNT", creditAccount));
            companyCode = accRec.getCoCode();
            if (!session.getCompanyId().equals(companyCode)) {
                poRec.getCreditAccount().setError("Account is not in same branch");
            }
        } catch (T24CoreException e) {
            poRec.getCreditAccount().setError("Accoount does not exist");    
        }
        
        
        currentRecord.set(poRec.toStructure());
        return poRec.getValidationResponse();
    }
}
