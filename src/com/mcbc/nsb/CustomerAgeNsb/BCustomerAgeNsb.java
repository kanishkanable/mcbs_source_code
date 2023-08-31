package com.mcbc.nsb.CustomerAgeNsb;

import java.util.List;
import java.util.ListIterator;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcustomeragensb.EbCustomerAgeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BCustomerAgeNsb extends ServiceLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        
        List<String> customerIds = dataObj.selectRecords("", "CUSTOMER", "", "WITH L.CUST.AGE NE '' ");
        
        System.out.println("@@@@@@customerIds  :    " + customerIds);
        return customerIds;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub
        
        CustomerRecord Customer = new CustomerRecord(dataObj.getRecord("CUSTOMER", id));
        TField DateOfBirth = Customer.getDateOfBirth();
        String AgeRecordId = DateOfBirth.getValue().substring(4, 8);
        Boolean CheckDupValue = false;
        EbCustomerAgeNsbRecord AgeRecord;
        try {
            AgeRecord = new EbCustomerAgeNsbRecord(dataObj.getRecord("EB.CUSTOMER.AGE.NSB", AgeRecordId));
            CheckDupValue = CheckDuplicateAgeCustNsb(id, AgeRecord, CheckDupValue);
        } catch (T24CoreException e) {
            AgeRecord = new EbCustomerAgeNsbRecord();
            CheckDupValue = CheckDuplicateAgeCustNsb(id, AgeRecord, CheckDupValue);
        }

        if (!CheckDupValue) {
            AgeRecord.addCustomer(id);
            records.add(AgeRecord.toStructure());
        }

        SynchronousTransactionData td = new SynchronousTransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(AgeRecordId);
        td.setVersionId("EB.CUSTOMER.AGE.NSB,UPDATE.NSB");
        transactionData.add(td);
    }

    public Boolean CheckDuplicateAgeCustNsb(String id, EbCustomerAgeNsbRecord AgeRecord, Boolean CheckDupValue) {
        /*
         * FUNCTION TO CHECK IF THE CUSTOMER ALREADY EXIST IN
         * EB.CUSTOMER.AGE.NSB AND UPDATE THE FIELD
         */
        ListIterator<TField> CustomerList = AgeRecord.getCustomer().listIterator();
        while (CustomerList.hasNext()) {
            TField CustomerId = CustomerList.next();
            if (id.equals(CustomerId.getValue())) {
                CheckDupValue = true;
            }
        }
        return CheckDupValue;
    }
    
}
