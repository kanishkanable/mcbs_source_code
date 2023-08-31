package com.mcbc.nsb.Accounts;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.customer.AddressClass;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAIMacctemailValidateNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        AaArrangementActivityRecord curRec = new AaArrangementActivityRecord(currentRecord);

        TField f1 = curRec.getProduct();

        int noCustomer = curRec.getCustomer().size();
        String AcctProdType = curRec.getProduct().getValue();

        // ********************************
        // Give correct IM TYPE product name in the IF condition to trigger this
        // routine and validate only for
        // IM product.
        // if(!AcctProdType.equalsIgnoreCase("")) {
        // return curRec.getValidationResponse();
        // }
        //**********************************

        System.out.println("" + noCustomer);

        CustomerRecord client;

        Boolean present = false;

        DataAccess da = new DataAccess(this);

        for (int y = 0; y < noCustomer; y++) {

            String currentCustomerRec = curRec.getCustomer(y).getCustomer().getValue().toString();
            try {
                client = new CustomerRecord(da.getRecord("CUSTOMER", currentCustomerRec));
                String cust_email = client.getPhone1().get(0).getEmail1().getValue();
                System.out.println("CUstomer email_id:: " + cust_email);
                if (!cust_email.isEmpty()) {
                    present = true;
                }
            } catch (Exception e1) {
                client = new CustomerRecord();
            }
        }

        if (!present.equals(true)) {
            curRec.getCustomer(0).getCustomer().setError("Email_Id Missing for this Customer");
            System.out.println("Email id missing.");
        }

        return curRec.getValidationResponse();
    }
}
