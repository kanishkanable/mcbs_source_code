package com.mcbc.nsb.CustomerMigrationNsb;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VIdCancelledNicNsb extends RecordLifecycle {

    DataAccess Da = new DataAccess(this);
    
    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        int LegalIdLength = currentRecordId.toString().length();
        
        if (LegalIdLength == 10) {
            if ((!currentRecordId.endsWith("V")) && (!currentRecordId.endsWith("X"))) {
                throw new T24CoreException("", "EB-NIC.FORMAT.NSB");
            }
        } else if (LegalIdLength != 12) {
            throw new T24CoreException("", "EB-INCORRECT.NIC.NSB");
        }
        
        //return super.checkId(currentRecordId, transactionContext);
        return currentRecordId;
    
    }
}
