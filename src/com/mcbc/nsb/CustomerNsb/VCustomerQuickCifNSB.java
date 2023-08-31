package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.Map;
import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CalculateAgeNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustValidateLegalIdNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustGenderFullShortNameNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustLegalIdDefaultValuesNSB;
import com.mcbc.nsb.CustomerCommonUtils.CustomerRiskLevelNsb;
import com.mcbc.nsb.CustomerCommonUtils.UpdateProvinceFromDistrict;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

public class VCustomerQuickCifNSB extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);

    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);

        // ROUTINE TO UPDATE GENDER, FULL NAME & SHORT NAME
        CustGenderFullShortNameNsb GenderFullShortName = new CustGenderFullShortNameNsb();
        GenderFullShortName.UpdateGenderFullAndShortName(CustomerRec, DataObj, currentRecordId);
        String Gender = GenderFullShortName.GetGender();
        LocalRefGroup FullName = GenderFullShortName.GetFullNamegrp();
        String ShortName = GenderFullShortName.GetShortName();
        String Mnemonic = GenderFullShortName.GetMnemonic();

        // DEFAULT MNEMONIC
        CustomerRec.setMnemonic(Mnemonic);

        // UPDATE GENDER BASED ON TITLE
        CustomerRec.setGender(Gender);

        // UPDATE L.FULL.NAME LOCAL FIELD WITH THE GIVEN NAME & FAMILY NAME
        LocalRefList localRefList = CustomerRec.getLocalRefGroups("L.FULL.NAME");
        int lFullNameLength = localRefList.size();
        while (lFullNameLength > 0) {
            localRefList.remove(lFullNameLength - 1);
            lFullNameLength--;
        }
        localRefList.add(FullName);

        // UPDATE SHORT NAME WITH TITLE + INITIALS OF GIVEN NAME + FAMILY
        // NAME
        CustomerRec.setShortName(ShortName, 0);

        currentRecord.set(CustomerRec.toStructure());
    }

    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER",
                new String[] { "LEGAL.AGE.MINOR.DOC", "LEGAL.AGE.LK.DOC", "LEGAL.AGE.NOTLK.DOC", "RISK.LEVEL" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
//        String LegDocBirthCertificate = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.MINOR.DOC").get(0).getValue();
        String LegDocNic = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(0).getValue();
        String LegDocNicOld = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(1).getValue();
        String LegDocNicMismatch = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(2).getValue();
        // String LegDocPassport =
        // ParamConfig.get("CUSTOMER").get("LEGAL.AGE.NOTLK.DOC").get(0).getValue();

        // UPDATING OTHER NAME TO UPPER CASE, IF USER INPUTS MANUALLY
        for (int i = 0; (customerRec.getLocalRefGroups("L.OTHER.NAME").size()) > i; i++) {
            String otherNameField = customerRec.getLocalRefGroups("L.OTHER.NAME").get(i).getLocalRefField("L.OTHER.NAME")
                    .getValue();
            customerRec.getLocalRefGroups("L.OTHER.NAME").get(i).getLocalRefField("L.OTHER.NAME")
                    .setValue(otherNameField.toUpperCase());
        }
        
        // UPDATING FULL NAME TO UPPER CASE, IF USER INPUTS MANUALLY
        for (int i = 0; (customerRec.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            String FullNameField = customerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .getValue();
            customerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .setValue(FullNameField.toUpperCase());
        }

        Date SystemDate = new Date(this);
        String TodayDate = SystemDate.getDates().getToday().getValue();
        CalculateAgeNsb nsbobj = new CalculateAgeNsb(customerRec.getDateOfBirth().getValue(), TodayDate);
        int Age = nsbobj.getAgeInteger();

        // Default Province from District
        UpdateProvinceFromDistrict DistrictProvinceObj = new UpdateProvinceFromDistrict();
        DistrictProvinceObj.AddProvinceFromDistrict(customerRec, DataObj);
        String District = DistrictProvinceObj.getDistrict();
        if (!District.isEmpty()) {
            customerRec.getLocalRefField("L.PROVINCE").setValue(DistrictProvinceObj.getProvince());
        }

        // VALIDATING LEGAL ID
        // DEFAULTING PASSPORT AS LEGAL DOCUMENT FOR NON SRI LANKA
        // DEFAULTING NIC AS LEGAL DOCUMENT FOR SRI LANKA NATIONALITY
        // DEFAULTING BIRTH CERTIFICATE AS LEGAL DOCUMENT FOR MINOR
        CustLegalIdDefaultValuesNSB CustomerLegalObj = new CustLegalIdDefaultValuesNSB();
        CustomerLegalObj.UpdateLegalDefFields(String.valueOf(Age), customerRec, DataObj);

        String LegalDocValue = CustomerLegalObj.getLegalDocValue();
        String LegalDocValueOld = CustomerLegalObj.getLegalDocValueOld();
//        String BirthCertificateNumber = CustomerLegalObj.getBirthCertificate();

        Boolean CheckLegalDocValue = false;
        for (LegalIdClass li : customerRec.getLegalId()) {
            if ((li.getLegalDocName().getValue().equals(LegalDocValue))
                    || (li.getLegalDocName().getValue().equals(LegalDocValueOld))
                    || (li.getLegalDocName().getValue().equals(LegDocNicMismatch))) {
                CheckLegalDocValue = true;
                break;
            }
        }

        if (!CheckLegalDocValue) {
            LegalIdClass newLegalIdClass = new LegalIdClass();
            newLegalIdClass.setLegalDocName(LegalDocValue);
            customerRec.setLegalId(newLegalIdClass, customerRec.getLegalId().size());
        }

        for (LegalIdClass li : customerRec.getLegalId()) {
            //if (!li.getLegalDocName().getValue().equals(LegDocNicMismatch)) {
                if (((li.getLegalDocName().getValue().equals(LegDocNic))
                        || (li.getLegalDocName().getValue().equals(LegDocNicOld)))
                        && (!li.getLegalId().getValue().isEmpty())) {
                    if (li.getLegalId().getValue().length() == 10) {
                        li.setLegalDocName(LegDocNicOld);
                    }
                    if (li.getLegalId().getValue().length() == 12) {
                        li.setLegalDocName(LegDocNic);
                    }
                }

/*                if ((li.getLegalDocName().getValue().equals(LegDocBirthCertificate))) {
                    // && (li.getLegalId().getValue().isEmpty())) {
                    li.setLegalId(BirthCertificateNumber);
                }
*/
        }

        // UPDATING THE LEGAL HOLDER NAME WITH SHORT NAME, IF THE FIELD IS NOT
        // UPDATED
        try {
            TField ShortName = customerRec.getShortName(0);
            for (int i = 0; i < customerRec.getLegalId().size(); i++) {
                if (((LegalIdClass) customerRec.getLegalId().get(i)).getLegalHolderName().getValue().isEmpty()) {
                    ((LegalIdClass) customerRec.getLegalId().get(i)).setLegalHolderName(ShortName);
                }
            }
        } catch (Exception e) {
            customerRec.getShortName(0).setError("EB-SHORTNAME.NSB");
        }

        // UPDATE RISK LEVEL & AUTO NEXT KYC REVIEW DATE BASED ON PEP
        CustomerRiskLevelNsb RiskLevelNsb = new CustomerRiskLevelNsb();
        //String PepStatusValue = customerRec.getCalcRiskClass().getValue();
        String PepStatusValue = customerRec.getManualRiskClass().getValue();

        if ((!PepStatusValue.isEmpty()) && (PepStatusValue.length() > 2)) {
            RiskLevelNsb.UpdateRisklevelNextKycdateNsb(PepStatusValue, customerRec, DataObj, ParamConfig);
        }

        // UPDATE AUTO NEXT KYC DATE
        String NextDate = RiskLevelNsb.UpdateAutoNextKycDate(customerRec, ParamConfig);
        customerRec.setAutoNextKycReviewDate(NextDate);

        currentRecord.set(customerRec.toStructure());
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "TARGET.EPF", "LEGAL.EXP.MAND", "LEGAL.AGE.MINOR.DOC",
                "LEGAL.AGE.LK.DOC", "LEGAL.AGE.NOTLK.DOC" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        String EcpLegalDocName = ParamConfig.get("CUSTOMER").get("LEGAL.EXP.MAND").get(0).getValue();

        // VALIDATION OF LEGAL ID WITH GENDER & DATE OF BIRTH
        Date SystemDate = new Date(this);
        String TodayDate = SystemDate.getDates().getToday().getValue();
        CalculateAgeNsb nsbobj = new CalculateAgeNsb(CustomerRec.getDateOfBirth().toString(), TodayDate);
        String Age = String.valueOf(nsbobj.getAgeInteger());

        CustValidateLegalIdNsb ValidateLegalId = new CustValidateLegalIdNsb();
        ValidateLegalId.ValidateLegalId(Age, CustomerRec, DataObj, currentRecordId);

        // VALIDATION TO MAKE LEGAL EXPIRY DDATE MANDATORY, IF LEGAL
        // DOCUMENT IS
        // PASSPORT

        for (int i = 0; i < CustomerRec.getLegalId().size(); i++) {
            String LegalDocName = ((LegalIdClass) CustomerRec.getLegalId().get(i)).getLegalDocName().getValue()
                    .toString();
            TField LegalExpDate = ((LegalIdClass) CustomerRec.getLegalId().get(i)).getLegalExpDate();
            if (LegalDocName.equals(EcpLegalDocName) && LegalExpDate.getValue().isEmpty()) {
                ((LegalIdClass) CustomerRec.getLegalId().get(i)).getLegalExpDate().setError("EB-CUST.EXP.DT.NSB");
            }
        }

        if (CustomerRec.getLocalRefField("L.AML.CHECK").getValue().isEmpty()){
            CustomerRec.getLocalRefField("L.AML.CHECK").setError("EB-CUST.AML.CHECK.NSB");
        } else {
            try {
                CustomerRec.getLocalRefGroups("L.AML.RESULT").get(0).getLocalRefField("L.AML.RESULT").getValue();
            } catch (Exception e) {
                throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUST.AML.RESULT.NSB");
            }
        }
        
        currentRecord.set(CustomerRec.toStructure());
        return CustomerRec.getValidationResponse();
    }
}
