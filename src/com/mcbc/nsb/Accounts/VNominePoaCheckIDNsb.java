package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;

import com.temenos.t24.api.records.aaarrangementactivity.*;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
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


/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */

// ID ROUTINE FOR AA.NOMINEE.POA.NSB,INPUT

public class VNominePoaCheckIDNsb extends RecordLifecycle {

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
//        return super.checkId(currentRecordId, transactionContext);
        
        final Logger LOGGER = Logger.getLogger(VNominePoaCheckIDNsb.class.getName());
        LOGGER.setLevel(Level.INFO);

        LOGGER.info("String currentRecordId - " + currentRecordId);
        LOGGER.info("TransactionContext transactionContext - " + transactionContext);

        TStructure accrec = null;
        
        if(currentRecordId != null) {
            DataAccess da = new DataAccess(this);
            accrec = da.getRecord("AA.ARRANGEMENT", currentRecordId);
            LOGGER.info("TStructure accrec - " + accrec);
            
            try {
                if(accrec!=null){
                    LOGGER.info("Record read success");
                } else {
                    throw new Exception("Invalid Account or Deposit");
                }
            }
            catch (Exception e){
                System.out.println(e);
            }      
        }
//Add logic to return proper error msg if @id not valid            
//Add validation to check NOMINEE/POA is YES at account level then only allow to create NOMINEE/POA .
        
        return currentRecordId;
        
    }        

}
