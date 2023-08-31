package com.mcbc.nsb.CustomerNsb;

import java.time.LocalDate;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 *  SET ERROR IF CUSTOMER CREATIONG DATE IS BACKDATED
 */
public class VCustomerSinceTodayNsb extends RecordLifecycle {

    Date t24Dates = new Date(this);
    String today = t24Dates.getDates().getToday().getValue();
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        String customerSince = customerRec.getCustomerSince().getValue();
//        String today = t24Dates.getDates().getToday().getValue();
        String currNum = customerRec.getCurrNo();
        if ((customerSince.isEmpty()) && (currNum.isEmpty())) {
            customerRec.setCustomerSince(today);   
        }
        currentRecord.set(customerRec.toStructure());
    }
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        String currNum = customerRec.getCurrNo();
        
        if (currNum.isEmpty()) {
            String creationDate = customerRec.getCustomerSince().getValue();
            LocalDate creationDateFormat = LocalDate.of(Integer.parseInt(creationDate.substring(0, 4)),
                    Integer.parseInt(creationDate.substring(4, 6)), Integer.parseInt(creationDate.substring(6, 8)));
            LocalDate TodayFormat = LocalDate.of(Integer.parseInt(today.substring(0, 4)),
                    Integer.parseInt(today.substring(4, 6)), Integer.parseInt(today.substring(6, 8)));
            if (creationDateFormat.isBefore(TodayFormat)) {
                customerRec.getCustomerSince().setError("EB-CREATION.DATE");
            }
        }
        
        currentRecord.set(customerRec.toStructure());
        
        return customerRec.getValidationResponse();
    }
}
 