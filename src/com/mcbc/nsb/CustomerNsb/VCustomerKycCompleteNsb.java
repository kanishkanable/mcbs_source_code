package com.mcbc.nsb.CustomerNsb;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 * routine to set KYC.COMPLETE to YES  
 * once all Mandatory fields are input
 * 
 * Also to remove POSTING Restrict 103
 * 
 */
public class VCustomerKycCompleteNsb extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        String KycComplete = CustomerRec.getKycComplete().getValue();
        if (!KycComplete.equals("YES")) {
            CustomerRec.setKycComplete("YES");
        }

        List<TField> PostTestricList = new ArrayList<TField>( CustomerRec.getPostingRestrict());
        
        for(int PostRestCount=0; PostRestCount < PostTestricList.size(); PostRestCount++){
            if (PostTestricList.get(PostRestCount).getValue().equals("30")) {
                CustomerRec.removePostingRestrict(PostRestCount);
                //PostTestricList.remove(PostRestCount);
                //i =-1;  If 103 is occuring twice, uncommet this line and comment break
                break;
            }
        }   
        currentRecord.set(CustomerRec.toStructure());
    }
}
