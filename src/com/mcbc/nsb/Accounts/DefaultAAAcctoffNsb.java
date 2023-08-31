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
public class DefaultAAAcctoffNsb extends ActivityLifecycle {
    @Override
    public void defaultFieldValues(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {

        final Logger LOGGER = Logger.getLogger(DefaultAAAcctoffNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine ");
        DataAccess da = new DataAccess(this);

        String arrangementid = arrangementContext.getArrangementId();
        Contract contract = new Contract(this);
        contract.setContractId(arrangementid);
        // TStructure officer =
        // contract.getOfficersCondition("OFFICERS").toStructure();
        AaPrdDesOfficersRecord aaprddesoff = new AaPrdDesOfficersRecord(record);
        // String primoff =
        // aaprddesoff.getPrimaryOfficer().getValue().toString();
        // List<OtherOfficerClass> otheroff = aaprddesoff.getOtherOfficer();

        List<CustomerClass> Customerid = arrangementActivityRecord.getCustomer();
        for (CustomerClass customerid : Customerid) {
            TStructure cusrec = da.getRecord("CUSTOMER", customerid.getCustomer().getValue());
            CustomerRecord CusRec = new CustomerRecord(cusrec);
            String cust_acctoff = CusRec.getAccountOfficer().getValue();
            // aaprddesoff.setPrimaryOfficer(cust_acctoff);
            if (!cust_acctoff.isEmpty()) {
                aaprddesoff.getPrimaryOfficer().setValue(cust_acctoff);
            }

            OtherOfficerClass othoffclass = new OtherOfficerClass();
            List<TField> cust_Otheroff = CusRec.getOtherOfficer();
            if (!cust_Otheroff.isEmpty()) {
                for (int i = 0; i < cust_Otheroff.size(); i++) {
                    othoffclass.setOtherOfficer(cust_Otheroff.get(i).getValue());
                    aaprddesoff.setOtherOfficer(othoffclass, i);
                    // aaprddesoff.getOtherOfficer(i).setOtherOfficer(cust_Otheroff.get(i).getValue());
                    LOGGER.info("String otheroff - " + aaprddesoff.getOtherOfficer(i).getOtherOfficer().getValue());
                }
            }
        }

        LOGGER.info("String primaryoff - " + aaprddesoff.getPrimaryOfficer().getValue());
        LOGGER.info("String otheroff - " + aaprddesoff.getOtherOfficer(0).getOtherOfficer().getValue());

        record.set(aaprddesoff.toStructure());

    }

}
