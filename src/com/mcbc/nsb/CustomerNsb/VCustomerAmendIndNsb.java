package com.mcbc.nsb.CustomerNsb;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mcbc.nsb.CustomerCommonUtils.CustomerOverridesNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VCustomerAmendIndNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        
        
        String kycValue = customerRec.getKycComplete().getValue();
        boolean kycYes = false;
//        customerRec.setKycComplete("YES");
        if (!kycValue.equals("YES")) {
            for (int i = 0; customerRec.getPostingRestrict().size() > i; i++) {
                String PostingRestrict = customerRec.getPostingRestrict().get(i).getValue();
                if (PostingRestrict.equals("30")) {
                    kycYes = true;
                }
            }
            if (!kycYes) {
                customerRec.setPostingRestrict("30", customerRec.getPostingRestrict().size());
            }
        } else {
            for (int i = 0; customerRec.getPostingRestrict().size() > i; i++) {
                String PostingRestrict = customerRec.getPostingRestrict().get(i).getValue();
                if (PostingRestrict.equals("30")) {
                    customerRec.getPostingRestrict().get(i).setValue("");
                }
            }
        }
        
        currentRecord.set(customerRec.toStructure());
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord customerRec = new CustomerRecord(currentRecord);

        //Gender & Title validation
        String customerTitle = customerRec.getTitle().getValue();
        String customerGender = customerRec.getGender().getValue();
        if ((customerTitle.equals("MAST")) && (customerGender.equals("FEMALE"))) {
            customerRec.getGender().setError("Title is chosen as " + customerTitle + ", Hence the Gender should be MALE !");
        }
            
            
     // VALIDATING L.FULL.NAME
        try {
            customerRec.getLocalRefGroups("L.FULL.NAME").get(0).getLocalRefField("L.FULL.NAME").getValue();
        } catch (Exception e) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-FULLNAME.NSB");
        }

        for (int i = 0; (customerRec.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            String FullNameField = customerRec.getLocalRefGroups("L.FULL.NAME").get(i)
                    .getLocalRefField("L.FULL.NAME").getValue();
            Pattern my_pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE);
            Matcher my_match = my_pattern.matcher(FullNameField);
            boolean check = my_match.find();
            if (check) {
                customerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                        .setError("EB-CU.SPECIAL.CHAR.NSB");
            }
        }
        
        //BLOCK CUSTOMER SCREEN FOR BLACKLIST CUSTOMER
        if (customerRec.getLocalRefField("L.BLACK.LIST").getValue().equals("YES")) {
            customerRec.getLocalRefField("L.BLACK.LIST").setError("EB-BLACKLIST.CUST.NSB");
        }

        // SET OVERRIDES FOR DUPLICATE EPF NUMBER & STUDENT fILE NUMBER
        CustomerOverridesNsb CustomerOverrideObj = new CustomerOverridesNsb();
        CustomerOverrideObj.setCustomerOverride(customerRec, dataObj);
        
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

/*        // SET ERROR IF CUSTOMER CREATIONG DATE IS BACKDATED
        
        String CreationDate = customerRec.getCustomerSince().getValue();
        Date SystemDate = new Date(this);
        String Today = SystemDate.getDates().getToday().getValue();
        LocalDate CreationDateFormat = LocalDate.of(Integer.parseInt(CreationDate.substring(0, 4)),
                Integer.parseInt(CreationDate.substring(4, 6)), Integer.parseInt(CreationDate.substring(6, 8)));
        LocalDate TodayFormat = LocalDate.of(Integer.parseInt(Today.substring(0, 4)),
                Integer.parseInt(Today.substring(4, 6)), Integer.parseInt(Today.substring(6, 8)));
        if (CreationDateFormat.isBefore(TodayFormat)) {
            customerRec.getCustomerSince().setError("EB-CREATION.DATE");
        }
*/
        
     // DEATH NOTIFY DATE IS MANDATORY IF DEATH DATE IS SPECIFIED

        if ((customerRec.getNotificationOfDeath().getValue().isEmpty())
                && (!customerRec.getDeathDate().getValue().isEmpty())) {
            customerRec.getNotificationOfDeath().setError("EB-CUST.DEATH.NOTIFY.NSB");
            customerRec.getLocalRefField("L.CUSTOMER.TYPE").setValue("DECEASED");
        }
        
        return customerRec.getValidationResponse();
    }

    private void setEpfStudentfileError(CustomerRecord customerRec, String CustomerId, DataAccess dataObj,
            TField Overide) {

        TStructure dupCustomerRecord = dataObj.getRecord("CUSTOMER", CustomerId);
        CustomerRecord newCustRec = new CustomerRecord(dupCustomerRecord);
        
        String epfNumber = newCustRec.getLocalRefField("L.EPF.NUMBER").getValue();

        String studentFile = newCustRec.getLocalRefField("L.STUDENT.FILE").getValue();

        if ((customerRec.getLocalRefField("L.EPF.NUMBER").getValue().equals(epfNumber))
                && (!customerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty())) {
            customerRec.getLocalRefField("L.EPF.NUMBER").setError("Duplicate EPF NUmber with Customer : " + CustomerId);
        }
        if ((customerRec.getLocalRefField("L.STUDENT.FILE").getValue().equals(studentFile))
                && (!customerRec.getLocalRefField("L.STUDENT.FILE").getValue().isEmpty())) {
            customerRec.getLocalRefField("L.STUDENT.FILE")
                    .setError("Duplicate STUDENT FILE NUmber with Customer : " + CustomerId);
        }
    }

}