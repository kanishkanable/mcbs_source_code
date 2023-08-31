package com.mcbc.nsb.CustomerMigrationNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpIndiMandatoryFieldsNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
 
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        
        try {
            String employmentStatus = customerRec.getEmploymentStatus(0).getEmploymentStatus().getValue();
            if (employmentStatus.isEmpty()){
                customerRec.getEmploymentStatus(0).getEmploymentStatus().setError("EB-INP.MISS");
            }
        } catch (T24CoreException e) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-MAND.EMPSTATUS.NSB");
        }
        
/*        if (customerRec.getLocalRefField("L.AML.CHECK").getValue().isEmpty()){
            customerRec.getLocalRefField("L.AML.CHECK").setError("EB-CUST.AML.CHECK.NSB");
        } else {
            try {
                customerRec.getLocalRefGroups("L.AML.RESULT").get(0).getLocalRefField("L.AML.RESULT").getValue();
            } catch (Exception e) {
                throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUST.AML.RESULT.NSB");
            }
        }
*/
        return customerRec.getValidationResponse();
    }

}
