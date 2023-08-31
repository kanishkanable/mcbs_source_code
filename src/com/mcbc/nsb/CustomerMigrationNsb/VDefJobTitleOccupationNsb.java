package com.mcbc.nsb.CustomerMigrationNsb;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.EmploymentStatusClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebjobtitlensb.EbJobTitleNsbRecord;
//import com.temenos.t24.api.tables.eboccupationnsb.EbOccupationNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefJobTitleOccupationNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        for (EmploymentStatusClass EmploymentStatus : CustomerRec.getEmploymentStatus()) {
            String JobTitleId = EmploymentStatus.getJobTitle().getValue();
            String Occupation = EmploymentStatus.getOccupation().getValue();
            if (Occupation.isEmpty()) {
                try {
                    EbJobTitleNsbRecord EbJobTitleNsbRec = new EbJobTitleNsbRecord(
                            DataObj.getRecord("EB.JOB.TITLE.NSB", JobTitleId));
                    String OccupationId = EbJobTitleNsbRec.getOccupationMatchId().getValue();
                    EmploymentStatus.setOccupation(OccupationId);
//                    try {
//                        EbOccupationNsbRecord EbOccupationNsbRec = new EbOccupationNsbRecord(
//                                DataObj.getRecord("EB.OCCUPATION.NSB", OccupationId));
//                        String OccupationValue = EbOccupationNsbRec.getOccupation().getValue();
//                        EmploymentStatus.setOccupation(OccupationValue);
//                    } catch (T24CoreException e) {
//                        EmploymentStatus.getJobTitle().setError("EB-MISSING.OCCUPATION.NSB");
//                    }
                } catch (T24CoreException e) {
                    EmploymentStatus.getJobTitle().setError("EB-MISSING.JOBTITLE.NSB");
                }
            }
        }
        currentRecord.set(CustomerRec.toStructure());
    }
}
