package com.mcbc.nsb.CustomerMigrationNsb;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CalculateAgeNsb;
//import com.mcbc.nsb.CustomerCommonUtils.CustValidateLegalIdNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustGenderFullShortNameNsb;
//import com.mcbc.nsb.CustomerCommonUtils.CustLegalIdDefaultValuesNSB;
import com.mcbc.nsb.CustomerCommonUtils.CustomerRiskLevelNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustomerStudentDetailsNsb;
import com.mcbc.nsb.CustomerCommonUtils.UpdateProvinceFromDistrict;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
//import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
//import com.temenos.t24.api.tables.ebcancellednicnsb.EbCancelledNicNsbRecord;
import com.temenos.t24.api.tables.ebcustomeragensb.EbCustomerAgeNsbRecord;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class VCustomerIndividualNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);
    Map<String, Map<String, List<TField>>> ParamConfig;
    String EcpTargetEpf;
    String EcpLegalDocName;
    String StuMandtab;
    String TaxPayerValue;

    String LegDocNic;
    String LegDocNicOld;
    String LegDocNicMismatch;
    String MinorTarget;
    
    /*
     * It defaults field values before validation
     */

    /*
     * public void defaultFieldValuesOnHotField(String application, String
     * currentRecordId, TStructure currentRecord, InputValue currentInputValue,
     * TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext
     * transactionContext) {
     * 
     * CustomerRecord customerRec = new CustomerRecord(currentRecord); //
     * ROUTINE TO UPDATE GENDER, FULL NAME & SHORT NAME
     * CustGenderFullShortNameNsb GenderFullShortName = new
     * CustGenderFullShortNameNsb();
     * GenderFullShortName.UpdateGenderFullAndShortName(customerRec, DataObj,
     * currentRecordId); String Gender = GenderFullShortName.GetGender();
     * LocalRefGroup FullName = GenderFullShortName.GetFullNamegrp(); String
     * ShortName = GenderFullShortName.GetShortName(); String Mnemonic =
     * GenderFullShortName.GetMnemonic();
     * 
     * // DEFAULT MNEMONIC customerRec.setMnemonic(Mnemonic);
     * 
     * // UPDATE GENDER BASED ON TITLE customerRec.setGender(Gender);
     * 
     * // UPDATE L.FULL.NAME LOCAL FIELD WITH THE GIVEN NAME & FAMILY NAME
     * 
     * LocalRefList localRefList = customerRec.getLocalRefGroups("L.FULL.NAME");
     * int lFullNameLength = localRefList.size(); while (lFullNameLength > 0) {
     * localRefList.remove(lFullNameLength - 1); lFullNameLength--; }
     * localRefList.add(FullName);
     * 
     * // UPDATE SHORT NAME WITH TITLE + INITIALS OF GIVEN NAME + FAMILY // NAME
     * customerRec.setShortName(ShortName, 0);
     * 
     * currentRecord.set(customerRec.toStructure()); }
     */

    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        // DataAccess DataObj = new DataAccess(this);
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        Date SystemDate = new Date(this);

        // UPDATE GENDER BASED ON TITLE
        CustGenderFullShortNameNsb genderFullShortName = new CustGenderFullShortNameNsb();
        genderFullShortName.UpdateGenderFullAndShortName(customerRec, DataObj, currentRecordId);
        String gender = genderFullShortName.GetGender();

        // UPDATE GENDER BASED ON TITLE
        if (customerRec.getGender().getValue().isEmpty()){
            customerRec.setGender(gender);
        }

        // GETTING PARAMETER VALUES
        getParamValues(DataObj);

        // CHANGE THE GIVEN NAME & FAMILY NAME FIELDS TO UPPER CASE
        customerRec.setGivenNames(customerRec.getGivenNames().getValue().toUpperCase());
        customerRec.setFamilyName(customerRec.getFamilyName().getValue().toUpperCase());
        try {
            String UcShortName;
            UcShortName = customerRec.getShortName(0).getValue().toUpperCase();
            customerRec.setShortName(UcShortName, 0);
        } catch (Exception e) {
            throw new T24CoreException("", "EB-SHORTNAME.NSB");
        }

        // UPDATING FULL NAME TO UPPER CASE, IF USER INPUTS MANUALLY
        for (int i = 0; (customerRec.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            String FullNameField = customerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .getValue();
            customerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .setValue(FullNameField.toUpperCase());
        }

        // Default Province from District
        if (!customerRec.getLocalRefField("L.DISTRICT").getValue().isEmpty()){
            UpdateProvinceFromDistrict DistrictProvinceObj = new UpdateProvinceFromDistrict();
            DistrictProvinceObj.AddProvinceFromDistrict(customerRec, DataObj);
            String District = DistrictProvinceObj.getDistrict();
            String Residence = customerRec.getResidence().getValue();
            if ((!District.isEmpty()) && (Residence.equals("LK"))) {
                customerRec.getLocalRefField("L.PROVINCE").setValue(DistrictProvinceObj.getProvince());
            }
        }

        // CALCULATE AGE FROM DATE OF BIRTH AND UPDATE L.CUST.AGE LOCAL
        // FIELD.
        String DobValue = customerRec.getDateOfBirth().getValue();
        int Age = 0;
        if (!DobValue.isEmpty()) {
            String TodayDate = SystemDate.getDates().getToday().getValue();

            CalculateAgeNsb nsb_obj = new CalculateAgeNsb(DobValue, TodayDate);
            Age = nsb_obj.getAgeInteger();
            String sAge = String.format("%03d", Age);
            customerRec.getLocalRefField("L.CUST.AGE").setValue(sAge + " Years");
        }

        // SET TARGET TO MINOR IF AGE < 16
        if (Age < 16) {
            customerRec.setTarget(MinorTarget);
        }

        // VALIDATING LEGAL ID
        // DEFAULTING PASSPORT AS LEGAL DOCUMENT FOR NON SRI LANKA
        // DEFAULTING NIC AS LEGAL DOCUMENT FOR SRI LANKA NATIONALITY
        // DEFAULTING BIRTH CERTIFICATE AS LEGAL DOCUMENT FOR MINOR
        /*
         * CustLegalIdDefaultValuesNSB CustomerLegalObj = new
         * CustLegalIdDefaultValuesNSB();
         * CustomerLegalObj.UpdateLegalDefFields(String.valueOf(Age),
         * customerRec, DataObj);
         * 
         * String LegalDocValue = CustomerLegalObj.getLegalDocValue(); String
         * LegalDocValueOld = CustomerLegalObj.getLegalDocValueOld(); // String
         * BirthCertificateNumber = CustomerLegalObj.getBirthCertificate();
         * 
         * Boolean CheckLegalDocValue = false; for (LegalIdClass li :
         * customerRec.getLegalId()) { if
         * ((li.getLegalDocName().getValue().equals(LegalDocValue)) ||
         * (li.getLegalDocName().getValue().equals(LegalDocValueOld)) ||
         * (li.getLegalDocName().getValue().equals(LegDocNicMismatch))) {
         * CheckLegalDocValue = true; break; } }
         * 
         * if (!CheckLegalDocValue) { LegalIdClass newLegalIdClass = new
         * LegalIdClass(); newLegalIdClass.setLegalDocName(LegalDocValue);
         * newLegalIdClass.setLegalExpDate("");
         * newLegalIdClass.setLegalHolderName("");
         * newLegalIdClass.setLegalId(""); newLegalIdClass.setLegalIssAuth("");
         * newLegalIdClass.setLegalIssDate("");
         * customerRec.setLegalId(newLegalIdClass,
         * customerRec.getLegalId().size()); }
         */

/*
           for (LegalIdClass li : customerRec.getLegalId()) {
            // if (!li.getLegalDocName().getValue().equals(LegDocNicMismatch)) {
            if (((li.getLegalDocName().getValue().equals(LegDocNic))
                    || (li.getLegalDocName().getValue().equals(LegDocNicOld)))
                    && (!li.getLegalId().getValue().isEmpty())) {
                if (li.getLegalId().getValue().length() == 10) {
                    li.setLegalDocName(LegDocNicOld);
                }
                if (li.getLegalId().getValue().length() == 12) {
                    li.setLegalDocName(LegDocNic);
                }
                String NicValue = li.getLegalId().getValue();
                try {
                    new EbCancelledNicNsbRecord(DataObj.getRecord("EB.CANCELLED.NIC.NSB", NicValue));
                    setPostingRestrict(customerRec);
                } catch (T24CoreException e) {
                }
            }
*/
            /*
             * if
             * ((li.getLegalDocName().getValue().equals(LegDocBirthCertificate))
             * ) { // && (li.getLegalId().getValue().isEmpty())) {
             * li.setLegalId(BirthCertificateNumber); }
             */
            // }
//        }

/*        // UPDATING THE LEGAL HOLDER NAME WITH SHORT NAME, IF THE FIELD IS
        // NOT
        // UPDATED
        try {
            TField ShortName = customerRec.getShortName(0);
            for (int i = 0; i < customerRec.getLegalId().size(); i++) {
                if (((LegalIdClass) customerRec.getLegalId().get(i)).getLegalHolderName().getValue().isEmpty()) {
                    ((LegalIdClass) customerRec.getLegalId().get(i)).setLegalHolderName(ShortName);
                }
            }
        } catch (Exception e) {
            throw new T24CoreException("", "EB-SHORTNAME.NSB");
        }
*/
        // UPDATE RISK LEVEL & AUTO NEXT KYC REVIEW DATE BASED ON PEP
        CustomerRiskLevelNsb RiskLevelNsb = new CustomerRiskLevelNsb();
        //String PepStatusValue = customerRec.getCalcRiskClass().getValue();
        String PepStatusValue = customerRec.getManualRiskClass().getValue();

        if ((!PepStatusValue.isEmpty()) && (PepStatusValue.length() > 2)) {
            RiskLevelNsb.UpdateRisklevelNextKycdateNsb(PepStatusValue, customerRec, DataObj, ParamConfig);
        }

        // DEFAULT KYC.COMPLETE TO YES ONCE ALL MANDATORY FIELDS ARE COMPLETED
        customerRec.setKycComplete("YES");

        // UPDATE AUTO NEXT KYC DATE
        String NextDate = RiskLevelNsb.UpdateAutoNextKycDate(customerRec, ParamConfig);
        customerRec.setAutoNextKycReviewDate(NextDate);
        currentRecord.set(customerRec.toStructure());
    }

    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        DataAccess DataObj = new DataAccess(this);

        // INITIALISE
        // customerRecord customerRec = new customerRecord(currentRecord);

        // GETTING PARAMETER VALUES
        getParamValues(DataObj);
        /*
         * GetParamValueNsb Config = new GetParamValueNsb();
         * Config.AddParam("CUSTOMER", new String[] { "TARGET.EPF",
         * "LEGAL.EXP.MAND", "STUDENT.TAB", "TAX.PAYER.VALUE" }); Map<String,
         * Map<String, List<TField>>> ParamConfig =
         * Config.GetParamValue(DataObj); String EcpTargetEpf =
         * ParamConfig.get("CUSTOMER").get("TARGET.EPF").get(0).getValue();
         * String EcpLegalDocName =
         * ParamConfig.get("CUSTOMER").get("LEGAL.EXP.MAND").get(0).getValue();
         * String StuMandtab =
         * ParamConfig.get("CUSTOMER").get("STUDENT.TAB").get(0).getValue();
         * String TaxPayerValue =
         * ParamConfig.get("CUSTOMER").get("TAX.PAYER.VALUE").get(0).getValue();
         */
        // ERROR IF DOB INPUT MISSING
        if (customerRec.getDateOfBirth().getValue().isEmpty()) {
            customerRec.getDateOfBirth().setError("EB-DOB.MISSING.NSB");
        }

        // VALIDATION OF LEGAL ID WITH GENDER & DATE OF BIRTH
//        String Age = customerRec.getLocalRefField("L.CUST.AGE").getValue().substring(0, 3);

//        CustValidateLegalIdNsb ValidateLegalId = new CustValidateLegalIdNsb();
//        ValidateLegalId.ValidateLegalId(Age, customerRec, DataObj, currentRecordId);

/*        // VALIDATION TO MAKE LEGAL EXPIRY DDATE MANDATORY, IF LEGAL
        // DOCUMENT IS PASSPORT
        for (int i = 0; i < customerRec.getLegalId().size(); i++) {
            String LegalDocName = ((LegalIdClass) customerRec.getLegalId().get(i)).getLegalDocName().getValue()
                    .toString();
            TField LegalExpDate = ((LegalIdClass) customerRec.getLegalId().get(i)).getLegalExpDate();
            if (LegalDocName.equals(EcpLegalDocName) && LegalExpDate.getValue().isEmpty()) {
                ((LegalIdClass) customerRec.getLegalId().get(i)).getLegalExpDate().setError("EB-CUST.EXP.DT.NSB");
            }
        }
*/
        // District is mandatory if residence is LK
/*        String Residence = customerRec.getResidence().getValue();
        if ((Residence.equals("LK")) && (customerRec.getLocalRefField("L.DISTRICT").getValue().isEmpty())) {
            customerRec.getLocalRefField("L.DISTRICT").setError("EB-INPUT.MISSING");
        }
*/
        // EPF NUMBER FIELD IS MANDATORY WHEN TARGET IS STAFF
        String Target = customerRec.getTarget().toString();
        if (customerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty() && Target.equals(EcpTargetEpf)) {
            customerRec.getLocalRefField("L.EPF.NUMBER").setError("EB-CUST.EPF.NO.NSB");
        }

        // VALIDATE EPF NUMBER TO BE IN CORRECT FORMAT
        if (!customerRec.getLocalRefField("L.EPF.NUMBER").getValue().isEmpty()) {
            if (!Target.equals(EcpTargetEpf)) {
                customerRec.getLocalRefField("L.EPF.NUMBER").setError("EB-CUST.NO.STAFF");
            } else {
                String EpfNumber = customerRec.getLocalRefField("L.EPF.NUMBER").getValue();
                int EpfNumberDigits = Integer.parseInt(EpfNumber);
                // if (EpfNumberDigits / 1000 <= 1 || EpfNumberDigits / 1000 >=
                // 99)
                // {
                if (EpfNumberDigits <= 999 || EpfNumberDigits >= 100000) {
                    customerRec.getLocalRefField("L.EPF.NUMBER").setValue(String.format("%05d", EpfNumberDigits));
                    customerRec.getLocalRefField("L.EPF.NUMBER").setError("EB-CUST.EPF.DIGITS.NSB");
                } else {
                    customerRec.getLocalRefField("L.EPF.NUMBER").setValue(String.format("%05d", EpfNumberDigits));
                }
            }
        }

        // TAX PAYER IS MANDATORY WHEN TAX ID IS SELECTED
        // try{
        if ((customerRec.getLocalRefField("L.TAX.PAYER").getValue().equals(TaxPayerValue))
                && (customerRec.getTaxId().isEmpty())) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUST.TAXID.NSB");
        }

        // VALIDATION FOR STUDENT TAB
        Boolean ValidateStudentTab = false;
        for (int xEmpStat = 0; xEmpStat < customerRec.getEmploymentStatus().size(); xEmpStat++) {
            if (customerRec.getEmploymentStatus().get(xEmpStat).getEmploymentStatus().getValue().equals(StuMandtab)) {
                ValidateStudentTab = true;
            }
        }

        if (!customerRec.getLocalRefField("L.STUDENT.FILE").getValue().isEmpty()) {
            ValidateStudentTab = true;
        }

        if (ValidateStudentTab) {
            CustomerStudentDetailsNsb CusStuTabObj = new CustomerStudentDetailsNsb();
            CusStuTabObj.SetStudentDetailsTabNsb(customerRec, DataObj);
        }

        currentRecord.set(customerRec.toStructure());
        return customerRec.getValidationResponse();
    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        // ROUTINE TO UPDATE CUSTOMER NUMBER TO CONCAT TABLE

        DataAccess DataObj = new DataAccess(this);
        CustomerRecord Customer = new CustomerRecord(currentRecord);
        TField DateOfBirth = Customer.getDateOfBirth();
        String AgeRecordId = DateOfBirth.getValue().substring(4, 8);
        Boolean CheckDupValue = false;
        EbCustomerAgeNsbRecord AgeRecord;
        try {
            AgeRecord = new EbCustomerAgeNsbRecord(DataObj.getRecord("EB.CUSTOMER.AGE.NSB", AgeRecordId));
            CheckDupValue = CheckDuplicateAgeCustNsb(currentRecordId, currentRecords, AgeRecord, CheckDupValue);
        } catch (T24CoreException e) {
            AgeRecord = new EbCustomerAgeNsbRecord();
            CheckDupValue = CheckDuplicateAgeCustNsb(currentRecordId, currentRecords, AgeRecord, CheckDupValue);
        }

        if (!CheckDupValue) {
            AgeRecord.addCustomer(currentRecordId);
            currentRecords.add(AgeRecord.toStructure());
        }

        TransactionData td = new TransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setUserName("INPUTT");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(AgeRecordId);
        td.setVersionId("EB.CUSTOMER.AGE.NSB,UPDATE.NSB");
        transactionData.add(td);
    }

    public Boolean CheckDuplicateAgeCustNsb(String currentRecordId, List<TStructure> currentRecords,
            EbCustomerAgeNsbRecord AgeRecord, Boolean CheckDupValue) {
        /*
         * FUNCTION TO CHECK IF THE CUSTOMER ALREADY EXIST IN
         * EB.CUSTOMER.AGE.NSB AND UPDATE THE FIELD
         */
        ListIterator<TField> CustomerList = AgeRecord.getCustomer().listIterator();
        while (CustomerList.hasNext()) {
            TField CustomerId = CustomerList.next();
            if (currentRecordId.equals(CustomerId.getValue())) {
                CheckDupValue = true;
            }
        }
        return CheckDupValue;
    }

/*    private void setPostingRestrict(CustomerRecord customerRec) {
        List<TField> postRestList = customerRec.getPostingRestrict();
        Boolean postRestExist = false;
        for (TField postRest : postRestList) {
            if (postRest.getValue().equals("104")) {
                postRestExist = true;
            }
        }

        if (!postRestExist) {
            customerRec.setPostingRestrict("104", customerRec.getPostingRestrict().size());
        }
    }
*/
    private void getParamValues(DataAccess DataObj) {

        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("CUSTOMER", new String[] { "TARGET.EPF", "LEGAL.EXP.MAND", "STUDENT.TAB", "TAX.PAYER.VALUE",
                "LEGAL.AGE.MINOR.DOC", "LEGAL.AGE.LK.DOC", "LEGAL.AGE.NOTLK.DOC", "RISK.LEVEL", "MINOR.TARGET" });
        ParamConfig = config.GetParamValue(DataObj);
        EcpTargetEpf = ParamConfig.get("CUSTOMER").get("TARGET.EPF").get(0).getValue();
        EcpLegalDocName = ParamConfig.get("CUSTOMER").get("LEGAL.EXP.MAND").get(0).getValue();
        StuMandtab = ParamConfig.get("CUSTOMER").get("STUDENT.TAB").get(0).getValue();
        TaxPayerValue = ParamConfig.get("CUSTOMER").get("TAX.PAYER.VALUE").get(0).getValue();

        LegDocNic = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(0).getValue();
        LegDocNicOld = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(1).getValue();
        LegDocNicMismatch = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(2).getValue();
        MinorTarget = ParamConfig.get("CUSTOMER").get("MINOR.TARGET").get(0).getValue();
    }
}
