package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CalculateAgeNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustGenderFullShortNameNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustValidateLegalIdNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VCusUpdNamesNsb extends RecordLifecycle {

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

        LocalRefGroup FullName = GenderFullShortName.GetFullNamegrp();
        String ShortName = GenderFullShortName.GetShortName();
        String Mnemonic = GenderFullShortName.GetMnemonic();
        
     // DEFAULT MNEMONIC
        CustomerRec.setMnemonic(Mnemonic);
        
     // UPDATE L.FULL.NAME LOCAL FIELD WITH THE GIVEN NAME & FAMILY NAME
        LocalRefList localRefList = CustomerRec.getLocalRefGroups("L.FULL.NAME");
        int lFullNameLength = localRefList.size();
        while (lFullNameLength > 0) {
            localRefList.remove(lFullNameLength - 1);
            lFullNameLength--;
        }
        localRefList.add(FullName);
        
        // UPDATE SHORT NAME WITH TITLE + INITIALS OF GIVEN NAME + FAMILY NAME

        CustomerRec.setShortName(ShortName, 0);
        currentRecord.set(CustomerRec.toStructure());
    }



    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        // ROUTINE TO UPDATE GENDER, FULL NAME & SHORT NAME
        CustGenderFullShortNameNsb GenderFullShortName = new CustGenderFullShortNameNsb();
        GenderFullShortName.UpdateGenderFullAndShortName(CustomerRec, DataObj, currentRecordId);

        LocalRefGroup FullName = GenderFullShortName.GetFullNamegrp();
        String ShortName = GenderFullShortName.GetShortName();
        String Mnemonic = GenderFullShortName.GetMnemonic();
        
     // DEFAULT MNEMONIC
        CustomerRec.setMnemonic(Mnemonic);
        
     // UPDATE L.FULL.NAME LOCAL FIELD WITH THE GIVEN NAME & FAMILY NAME
        LocalRefList localRefList = CustomerRec.getLocalRefGroups("L.FULL.NAME");
        int lFullNameLength = localRefList.size();
        while (lFullNameLength > 0) {
            localRefList.remove(lFullNameLength - 1);
            lFullNameLength--;
        }
        localRefList.add(FullName);
        
        // UPDATE SHORT NAME WITH TITLE + INITIALS OF GIVEN NAME + FAMILY NAME

        CustomerRec.setShortName(ShortName, 0);
        currentRecord.set(CustomerRec.toStructure());
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
        
        currentRecord.set(CustomerRec.toStructure());
        return CustomerRec.getValidationResponse();
    }    
    
    
}
