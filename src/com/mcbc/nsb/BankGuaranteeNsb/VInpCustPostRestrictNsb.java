package com.mcbc.nsb.BankGuaranteeNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCustPostRestrictNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    CustomerRecord customerRec;
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        MdDealRecord mdDealRec = new MdDealRecord(currentRecord);
        String customerId = mdDealRec.getCustomer().getValue();
        
        try{
            customerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", customerId));
        } catch (Exception e) {
            throw new T24CoreException("", "LI-CUSTOMER.INVALID.ID");
        }
        
        String postingRestrict = customerRec.getPostingRestrict().toString();
        
        if (postingRestrict.contains("12")){
            mdDealRec.getCustomer().setError("EB-CUSTOMER.DECEASED.NSB");
        }
        if (postingRestrict.contains("14")){
            mdDealRec.getCustomer().setError("EB-BLACKLIST.CUST.NSB");
        }
        
        return mdDealRec.getValidationResponse();
    }

    
}
