package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TField;
import com.temenos.api.TStructure;

import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.records.aaaccountdetails.*;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddesofficers.AaPrdDesOfficersRecord;
import com.temenos.t24.api.records.aaprddesofficers.OtherOfficerClass;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.records.aaarrangementactivity.CustomerClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class DefaultAcctfieldsNsb extends ActivityLifecycle {
    @Override
    public void defaultFieldValues(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {

        final Logger LOGGER = Logger.getLogger(DefaultAcctfieldsNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine ");
        DataAccess da = new DataAccess(this);
        String customerid = arrangementRecord.getCustomer(0).getCustomer().getValue();
        TStructure cusrec = da.getRecord("CUSTOMER", customerid);
        CustomerRecord CusRec = new CustomerRecord(cusrec);
        String cust_dob = CusRec.getDateOfBirth().getValue();
        String cust_age = CusRec.getLocalRefField("L.CUST.AGE").getValue();
        String cust_addr= CusRec.getAddress(0).get(0).getValue();
        
        LOGGER.info("cust_dob - " + cust_dob);
        LOGGER.info("cust_age - " + cust_age);        
                
        AaArrAccountRecord aaarracctrec = new AaArrAccountRecord(record);        
        LOGGER.info("passbook -" + aaarracctrec.getPassbook().getValue());
//        LOGGER.info("L.ADDRESS -" + aaarracctrec.getLocalRefField("L.ADDRESS").getValue());
//        LOGGER.info("L.ADDRESS -" + aaarracctrec.getLocalRefField("L.ADDRESS").getValue());
//        LOGGER.info("L.DOB -" + aaarracctrec.getLocalRefField("L.DOB").getValue());
//        LOGGER.info("L.NIC -" + aaarracctrec.getLocalRefField("L.NIC").getValue());
        
//        aaarracctrec.getLocalRefField("L.NIC").setValue(cust_age.toString());
//        aaarracctrec.getLocalRefField("L.DOB").setValue(cust_dob.toString());
//        aaarracctrec.getLocalRefField("L.ADDRESS").setValue(cust_addr);
//        aaarracctrec.getLocalRefField("L.AGE").setValue(cust_age.toString());
        aaarracctrec.setPassbook("Y");
        aaarracctrec.setShortTitle("Durai default", 0);
        
        record.set(aaarracctrec.toStructure());
                       
    }

}
