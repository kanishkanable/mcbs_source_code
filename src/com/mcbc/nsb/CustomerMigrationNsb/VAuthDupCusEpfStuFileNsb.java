package com.mcbc.nsb.CustomerMigrationNsb;

import java.util.List;
import java.util.ListIterator;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.eb.templatehook.TransactionData;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 * routine to check duplicate EPF & Student File Numbers during after unauth routine
 *  attached to all customer versions
 *  
 */
public class VAuthDupCusEpfStuFileNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<TransactionData> transactionData, List<TStructure> currentRecords) {
        // TODO Auto-generated method stub
        
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        
        try {
            ListIterator<TField> OverrideIterator = customerRec.getOverride().listIterator();
            while (OverrideIterator.hasNext()) {
                TField Overide = OverrideIterator.next();
                if (Overide.getValue().contains("POSSIBLE DUPLICATE CONTRACT")) {
                    String CustomerId = Overide.getValue().split(" ")[3].substring(2, 8);
                    setEpfStudentfileError(customerRec, CustomerId, dataObj, Overide);
                }
            }
        } catch (Exception e) {
        }
    }

    private void setEpfStudentfileError(CustomerRecord customerRec, String CustomerId, DataAccess dataObj,
            TField Overide) {

        TStructure dupCustomerRecord = dataObj.getRecord("CUSTOMER", CustomerId);
        CustomerRecord newCustRec = new CustomerRecord(dupCustomerRecord);

        String epfNumber = newCustRec.getLocalRefField("L.EPF.NUMBER").getValue();

        String studentFile = newCustRec.getLocalRefField("L.STUDENT.FILE").getValue();

        if ((customerRec.getLocalRefField("L.EPF.NUMBER").getValue().equals(epfNumber))
                && (!customerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty())) {
            throw new T24CoreException("", "Duplicate EPF NUmber with Customer : " + CustomerId);
        }
        if ((customerRec.getLocalRefField("L.STUDENT.FILE").getValue().equals(studentFile))
                && (!customerRec.getLocalRefField("L.STUDENT.FILE").getValue().isEmpty())) {
            throw new T24CoreException("", "Duplicate STUDENT FILE NUmber with Customer : " + CustomerId);
        }
    }
}
