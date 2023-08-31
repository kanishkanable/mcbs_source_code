package com.mcbc.nsb.BankGuaranteeNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.mddeal.MdDealRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VValCheckOverrideNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        MdDealRecord MdDealRec = new MdDealRecord(currentRecord);
        String AmountTest = MdDealRec.getPrincipalAmount().getValue();
        MdDealRec.getPrincipalAmount().setOverride("EB-BG.TEST.NSB" +  AmountTest);        

        //
        // + );
        
        return MdDealRec.getValidationResponse();
    }

    
}
