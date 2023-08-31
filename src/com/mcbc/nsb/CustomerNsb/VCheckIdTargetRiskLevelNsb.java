package com.mcbc.nsb.CustomerNsb;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.target.TargetRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VCheckIdTargetRiskLevelNsb extends RecordLifecycle {

    DataAccess Da = new DataAccess(this);
    
    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        //ID routine to validate the ID of the application input
        //Attached to the below Version.
        //

        try {
            //TargetRecord Target =
            new TargetRecord(Da.getRecord("TARGET", currentRecordId));
                return currentRecordId;
                
        } catch (Exception e) {
            throw new T24CoreException("EB-INVALID.TARGET.NSB", "EB-ERROR.SELECTION");
        }
    }

}
