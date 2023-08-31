package com.mcbc.nsb.CustomerNsb;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.EmploymentStatusClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.eboccupationnsb.EbOccupationNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCustSetEnrichmentNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess();
    String occupation;
    String jobTitle;
    String jobTitleDescription;
    String occupationDescription;

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        System.out.println("validate record 33");
        for (EmploymentStatusClass Employment : customerRec.getEmploymentStatus()) {
            System.out.println("validate record for 35");
//            jobTitle = Employment.getJobTitle().getValue();
//            System.out.println("validate record for 37  : " + jobTitle);
            occupation = Employment.getOccupation().getValue();
            System.out.println("validate record for 39  : " + occupation);

/*            if (!jobTitle.isEmpty()) {
                System.out.println("validate record for 41  : ");
                try {
                    JobTitleRecord JobTitleRec = new JobTitleRecord(dataObj.getRecord("JOB.TITLE", jobTitle));
                    System.out.println("validate record for 43  : ");
                    jobTitleDescription = JobTitleRec.getDescription(0).getValue();
                    System.out.println("validate record for 45  : " + jobTitleDescription);
                    Employment.getJobTitle().setEnrichment(jobTitleDescription);
                    System.out.println("validate record for 47  : ");
                } catch (Exception e) {
                    Employment.getJobTitle().setError("EB-JOB.OCC.MISSING.NSB");
                }
            }
            System.out.println("validate record for 49  : ");
*/
            if (!occupation.isEmpty()) {
                try {
                    EbOccupationNsbRecord occupationRec = new EbOccupationNsbRecord(
                            dataObj.getRecord("EB.OCCUPATION.NSB", occupation));
                    occupationDescription = occupationRec.getOccupation().getValue();
                    Employment.getOccupation().setEnrichment(occupationDescription);
                } catch (Exception e) {
                    Employment.getOccupation().setError("EB-JOB.OCC.MISSING.NSB");
                }
            }
        }
        currentRecord.set(customerRec.toStructure());
        
        return customerRec.getValidationResponse();
    }

}
