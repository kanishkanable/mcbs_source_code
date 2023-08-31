package com.mcbc.nsb.cheque;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;

public class VHotfieldpurfcyNsb extends RecordLifecycle {
    TellerRecord tellRec = null;
    AccountRecord accRec = null;
    CustomerRecord cusRec = null;
    String creditAcctVal = "";
    String creditAcctId = "";
    String currVal = "";
    String customerName = "";
    String customerId = "";
    String custIdName = "";
    DataAccess da = new DataAccess(this);

    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {

        tellRec = new TellerRecord(currentRecord);
        creditAcctVal = currentInputValue.getFieldName();
        creditAcctId = tellRec.getAccount2().getValue();
        if (creditAcctVal.equals("ACCOUNT.2")) {
            accRec = new AccountRecord(da.getRecord("ACCOUNT", creditAcctId));
            currVal = accRec.getCurrency().getValue();
            customerId = accRec.getCustomer().getValue();
            cusRec = new CustomerRecord(da.getRecord("CUSTOMER", customerId));
            customerName = cusRec.getName1(0).getValue();
            tellRec.setCurrency2(currVal);
            custIdName = customerId + customerName;
            tellRec.setCustomer1(custIdName);
        }
        currentRecord.set(tellRec.toStructure());
    }

}
