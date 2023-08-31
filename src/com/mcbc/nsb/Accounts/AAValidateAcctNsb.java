package com.mcbc.nsb.Accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAValidateAcctNsb extends ActivityLifecycle{

    @Override
    public TValidationResponse validateRecord(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {
        // TODO Auto-generated method stub
                
        final Logger LOGGER = Logger.getLogger(AAValidateAcctNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine ");
        DataAccess da = new DataAccess(this);
        String customerid = arrangementRecord.getCustomer(0).getCustomer().getValue();
        TStructure cusrec = da.getRecord("CUSTOMER", customerid);
        CustomerRecord CusRec = new CustomerRecord(cusrec);
        String cust_dob = CusRec.getDateOfBirth().getValue();
        String cust_age = CusRec.getLocalRefField("L.CUST.AGE").getValue();
        String cust_addr= CusRec.getAddress(0).get(0).getValue();
        
        Contract contract = new Contract(this);        
        String arrangementid = arrangementContext.getArrangementId();
        contract.setContractId(arrangementid);
        TStructure arrAccountRec = contract.getConditionForProperty("BALANCE");
        AaArrAccountRecord aaarracctrec = new AaArrAccountRecord(arrAccountRec);        
        AaPrdDesAccountRecord arrPrdAccountRec = new AaPrdDesAccountRecord(arrAccountRec);

        LocalRefList localCustomTag = arrPrdAccountRec.getLocalRefGroups("L.ADDRESS");
        
        List<String> locarrCustomList = new ArrayList<String>();

        for (LocalRefGroup localRefGroup : localCustomTag) {
            TField yNamesvals = localRefGroup.getLocalRefField("L.ADDRESS");
            locarrCustomList.add(yNamesvals.getValue());
            LOGGER.info("yNamesvals -" + yNamesvals.getValue());
        }

        
        LOGGER.info("passbook -" + aaarracctrec.getPassbook().getValue());
        LOGGER.info("purpose -" + aaarracctrec.getLocalRefField("L.PURPOSE").getValue().toString());        
                
        LOGGER.info("L.NIC -" + aaarracctrec.getLocalRefField("L.NIC").getValue());
        LOGGER.info("L.ADDRESS -" + aaarracctrec.getLocalRefField("L.ADDRESS").getValue());
        LOGGER.info("L.DOB -" + aaarracctrec.getLocalRefField("L.DOB").getValue());
        LOGGER.info("L.AGE -" + aaarracctrec.getLocalRefField("L.AGE").getValue());
        
        aaarracctrec.getLocalRefField("L.NIC").setValue(cust_age);
        aaarracctrec.getLocalRefField("L.DOB").setValue(cust_dob);
        aaarracctrec.getLocalRefField("L.ADDRESS").setValue(cust_addr.toString());
        aaarracctrec.getLocalRefField("L.AGE").setValue(cust_age);

        LOGGER.info("L.NIC -" + aaarracctrec.getLocalRefField("L.NIC").getValue());
        
        return aaarracctrec.getValidationResponse();
//        return super.validateRecord(accountDetailRecord, arrangementActivityRecord, arrangementContext, arrangementRecord,
//                masterActivityRecord, productPropertyRecord, productRecord, record);
    }   
}
