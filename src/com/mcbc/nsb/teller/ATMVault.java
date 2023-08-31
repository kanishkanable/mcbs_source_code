package com.mcbc.nsb.teller;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.records.tellerparameter.TellerParameterRecord;
import com.temenos.t24.api.records.tellertransaction.TellerTransactionRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class ATMVault extends RecordLifecycle {

    String paramCurrency;
    String paramCompany;
    String paramAccountNumber1;
    String paramAccountNumber2;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        try
        {
            TellerRecord tellerRecord = new TellerRecord(currentRecord);
            Session SessionContext = new Session((T24Context)this);
            DataAccess da = new DataAccess((T24Context)this);
            String vaultAcctNo = "";
            String tellerTransaction = tellerRecord.getTransactionCode().toString();
            String txnCcy = tellerRecord.getCurrency1().toString();
            String vaultCategory = "";
            String vaultID = "";
            String subDivCode = "";
            
            TellerTransactionRecord tellerTxnRec = new TellerTransactionRecord(da.getRecord("TELLER.TRANSACTION", tellerTransaction));
            vaultCategory = tellerTxnRec.getCatDeptCode1().toString();
            
            TellerParameterRecord tellerParamRec = new TellerParameterRecord(da.getRecord("TELLER.PARAMETER", SessionContext.getCompanyId()));
            vaultID = tellerParamRec.getVaultId(0).getVaultId().getValue();
            
            CompanyRecord CompanyRec = new CompanyRecord(da.getRecord("COMPANY", SessionContext.getCompanyId()));
            subDivCode = CompanyRec.getSubDivisionCode().toString();
            
            vaultAcctNo = txnCcy + vaultCategory + vaultID + subDivCode;
            System.out.println("vaultAcctNo->"+vaultAcctNo);
            

            if (vaultAcctNo != "")
            {
                tellerRecord.setAccount2(vaultAcctNo);
            }
            
            try{
                String accountNo = tellerRecord.getNarrative2(0).getValue();
                //System.out.println("accountNo->" + accountNo);
                if (!accountNo.isEmpty())
                {
                    //System.out.println("accountNo to set->" + accountNo);                    
                    ((Account1Class)tellerRecord.getAccount1().get(0)).setAccount1(accountNo);
                }
            }
            catch (Exception tellerRecordException)
            {
                //System.out.println(tellerRecordException.toString());
            }
            
            currentRecord.set(tellerRecord.toStructure());
        }catch (Exception tellerRecordException)
        {
            
        }
        
    }
        
}
