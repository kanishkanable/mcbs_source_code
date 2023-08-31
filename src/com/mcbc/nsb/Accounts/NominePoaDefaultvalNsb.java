package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;

import com.temenos.t24.api.records.aaarrangementactivity.*;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.records.aaaccountdetails.*;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.*;
import com.temenos.t24.api.hook.arrangement.*;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.complex.aa.activityhook.*;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;

import java.util.logging.Level;
import java.util.logging.Logger;


/* NOT USED NOW
 * 
 */


/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class NominePoaDefaultvalNsb extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        super.defaultFieldValues(application, currentRecordId, currentRecord, unauthorisedRecord, liveRecord,
                transactionContext);
        
        
        final Logger LOGGER = Logger.getLogger(NominePoaDefaultvalNsb.class.getName());
        LOGGER.setLevel(Level.INFO);

        LOGGER.info("String currentRecordId - " + currentRecordId);
        LOGGER.info("TransactionContext transactionContext - " + transactionContext);
        
        try {
            DataAccess da = new DataAccess(this);
            TStructure aaarrrec = da.getRecord("AA.ARRANGEMENT", currentRecordId);
            AaArrangementRecord AaArec = new AaArrangementRecord(aaarrrec);
            String accid = AaArec.getLinkedAppl(0).getLinkedApplId().getValue().toString();
            LOGGER.info("String accid - " + accid);
            
            TStructure accrec = da.getRecord("ACCOUNT", accid);
            LOGGER.info("TStructure accrec - " + accrec);   
            AccountRecord AccRec = new AccountRecord(accrec);
            String accshortname = AccRec.getShortTitle(0).getValue().toString();
            String name1 = AccRec.getAccountTitle1(0).getValue().toString();
            String customerid = AccRec.getCustomer().getValue().toString();
            LOGGER.info("String accshortname - " + accshortname);
            LOGGER.info("String name1 - " + name1);
            LOGGER.info("String customerid - " + customerid);
            
            TStructure cusrec = da.getRecord("CUSTOMER", customerid);
            CustomerRecord CusRec = new CustomerRecord(cusrec);
            
            
// Write logic to default ACCOUNT.NUMBER, NAME AND CUSTOMER.ID            
            
        } catch (Exception e) {
            System.out.println("Default failed");
        }                      
    }    
}
