package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TValidationResponse;
import com.temenos.api.TStructure;
import com.temenos.api.TString;

import java.util.List;
import java.io.FileWriter;

import com.temenos.api.LocalRefClass;
import com.temenos.api.TBoolean;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;

import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.LookupData;
import com.temenos.t24.api.complex.aa.activityhook.TransactionData;
import com.temenos.t24.api.records.aaaccountdetails.*;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aacustomerarrangement.*;
import com.temenos.t24.api.records.aacustomerrelatedarrangements.*;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalRecord;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalTable;
import com.temenos.tafj.api.client.impl.T24Context;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAPostrtn extends ActivityLifecycle {

    @Override
    public void postCoreTableUpdate(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            List<TransactionData> transactionData, List<TStructure> transactionRecord) {

        String customerid = arrangementRecord.getCustomer(0).getCustomer().getValue();

        final Logger LOGGER = Logger.getLogger(AAPostrtn.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("accountDetailRecord - " + accountDetailRecord);
        LOGGER.info("arrangementActivityRecord - " + arrangementActivityRecord);
        LOGGER.info("arrangementContext - " + arrangementContext);
        LOGGER.info("arrangementRecord - " + arrangementRecord);
        LOGGER.info("masterActivityRecord - " + masterActivityRecord);
        LOGGER.info("productRecord - " + productRecord);
        LOGGER.info("transactionData - " + transactionData);
        LOGGER.info("transactionRecord - " + transactionRecord);
        
        LOGGER.info("customer id - " + customerid);
             
        // AaArrangementRecord aarec = new AaArrangementRecord(record);
        // CustomerClass customerid = aarec.getCustomer(0);
        // ArrangementContext arcont = new ArrangementContext();
        // String arrid = arcont.getArrangementId();
        // String accid = arcont.getLinkedAccount();

        String arrid = arrangementContext.getArrangementId();
        String accid = arrangementContext.getLinkedAccount();

        LOGGER.info("arrangement id - " + arrid);
        LOGGER.info("AA account id - " + accid);
        
        DataAccess dataobj2 = new DataAccess(this);

        try {

//            TStructure acc_str = dataobj2.getRecord("AA.ACCOUNT.LEGAL", arrid);
//            AaAccountLegalRecord aalegal = new AaAccountLegalRecord(acc_str);
//            LOGGER.info("aalegal obj - " + aalegal);            
//            aalegal.setAccountId(accid);            
//            aalegal.setCustomerId(customerid);
            
            T24Context aa=new T24Context("AA.ACCOUNT.LEGAL");
            AaAccountLegalTable aaacctlegaltable = new AaAccountLegalTable(aa);
            AaAccountLegalRecord aalegalrecord = new AaAccountLegalRecord();                    
            
            LOGGER.info("aalegalrecord obj - " + aalegalrecord);
            LOGGER.info("aalegaltable obj - " + aaacctlegaltable);
            
            aalegalrecord.setAccountId(accid);
            aalegalrecord.setCustomerId(customerid);
            
            LOGGER.info("aalegal get acc id - " + aalegalrecord.getAccountId().getValue());
            LOGGER.info("aalegal get cust id - " + aalegalrecord.getCustomerId().getValue());
            
//            AaAccountLegalTable aalegaltable = new AaAccountLegalTable(this);
//            aalegaltable.write(arrid, aalegal);
            
            transactionRecord.add(aalegalrecord.toStructure());
            aaacctlegaltable.write(arrid, aalegalrecord);
                     
        } catch (Exception e) {
            System.out.println("write failed" + e);
        }
    }
}

