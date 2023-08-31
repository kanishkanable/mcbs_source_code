package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AANewAcctKycValidateNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        AaArrangementActivityRecord curRec = new AaArrangementActivityRecord(currentRecord);

        TField f1 = curRec.getProduct();

        int noCustomer = curRec.getCustomer().size();

        System.out.println("" + noCustomer);

        CustomerRecord client;

        Boolean present = false;

        DataAccess da = new DataAccess(this);

        for (int y = 0; y < noCustomer; y++) {

            String currentCustomerRec = curRec.getCustomer(y).getCustomer().getValue().toString();
            try {
                client = new CustomerRecord(da.getRecord("CUSTOMER", currentCustomerRec));
                String cust_kyc = client.getKycComplete().getValue();
                if (!cust_kyc.equalsIgnoreCase("YES")) {
                    curRec.getCustomer(0).getCustomer().setError("Customer Not Complete KYC");
                }
            } catch (Exception e) {
                System.out.println("Invalid Customer ID");    
            }
        }
        return curRec.getValidationResponse();
    }
}
