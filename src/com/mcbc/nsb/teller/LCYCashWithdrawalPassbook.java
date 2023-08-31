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
public class LCYCashWithdrawalPassbook extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        DataAccess da = new DataAccess((T24Context)this);
        
        //TField accountId = ((Account1Class)tellerRecord.getAccount1().get(0)).getAccount1();
        TField accountId = tellerRecord.getAccount2();
        AccountRecord acctRec = new AccountRecord(da.getRecord("ACCOUNT", accountId.toString()));

        try
        {
            TField passbookFlag = tellerRecord.getLocalRefField("L.PASSBOOK.FLAG");
            if(passbookFlag.getValue().isEmpty())
            {
                if(acctRec.getPassbook().getValue().equals("Y"))
                {
                    System.out.println("PASSBOOK IS YES");
                    passbookFlag.setValue("Y");
                }
                else
                {
                    System.out.println("PASSBOOK IS NO");
                    passbookFlag.setValue("N");
                }
            }
        } catch(Exception localRefException){}

        currentRecord.set(tellerRecord.toStructure());
        
    }


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
            System.out.println("passbook flag->"+passbook);
            System.out.println("account passbook flag->"+acctRec.getPassbook().getValue());
            //if(acctRec.getPassbook().getValue().equals("Y") && !acctRec.getPassbook().getValue().equals(passbookFlag))
            if(acctRec.getPassbook().getValue().equals("Y") && !passbookFlag.equals(acctRec.getPassbook().getValue()))
            {
                System.out.println("raise passbook oVerride->");
                passbook.setOverride("TT-NO.PASSBOOK.INAO.NSB");
            }
        } catch(Exception localRefException){}
        
        return tellerRecord.getValidationResponse();
        
    }
    
}
