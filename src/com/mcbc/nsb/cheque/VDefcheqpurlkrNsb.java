package com.mcbc.nsb.cheque;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.currency.CurrencyRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

public class VDefcheqpurlkrNsb extends RecordLifecycle {
    TellerRecord tellRec = null;
    AccountRecord accRec = null;
    CustomerRecord cusRec = null;
    CurrencyRecord currRec = null;
    String creditAcctId = "";
    String custId = "";
    String custName = "";
    String custUpdVal = "";
    String curr = "";
    String cutOff = "";
    String timeSys = "";
    DataAccess da = new DataAccess(this);
    Date dt = new Date(this);

    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        tellRec = new TellerRecord(currentRecord);
        creditAcctId = tellRec.getAccount2().getValue();
        accRec = new AccountRecord(da.getRecord("ACCOUNT", creditAcctId));
        custId = accRec.getCustomer().getValue();
        cusRec = new CustomerRecord(da.getRecord("CUSTOMER", custId));
        custName = cusRec.getName1(0).getValue();
        curr = tellRec.getCurrency1().getValue();
        currRec = new CurrencyRecord(da.getRecord("CURRENCY", curr));
        cutOff = currRec.getCutOffTime().getValue();
        custUpdVal = custId + custName;
        tellRec.setCustomer2(custUpdVal);
        timeSys = dt.getDates().getDateTime(0).toString();
        currentRecord.set(tellRec.toStructure());
    }

}
