package com.mcbc.nsb.CustomerMigrationNsb;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustomerRiskLevelNsb;
import com.mcbc.nsb.CustomerCommonUtils.UpdateProvinceFromDistrict;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.customer.RelationCodeClass;
import com.temenos.t24.api.records.customer.RiskAssetTypeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

import java.util.List;
import java.util.Map;

public class VCustomerNonIndividualNsb extends RecordLifecycle {

/*    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        
        // DEFAULT MNEMONIC
        String Mnemonic = "C-" + currentRecordId;
        CustomerRec.setMnemonic(Mnemonic);
        
        String lFullName = null;
        String setFullName = null;
        for (int i = 0; (CustomerRec.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME").setValue(CustomerRec
                    .getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME").getValue().toUpperCase());
            lFullName = CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME").getValue()
                    .toUpperCase();
            if (i >= 1) {
                lFullName = lFullName + CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i)
                        .getLocalRefField("L.FULL.NAME").getValue().toUpperCase();
            }
        }

        if (lFullName.length() > 50) {
            setFullName = lFullName.substring(0, 50);
        } else {
            setFullName = lFullName;
        }

        CustomerRec.setShortName(setFullName, 0);
        currentRecord.set(CustomerRec.toStructure());
    }
*/
    
    
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        DataAccess DataObj = new DataAccess((T24Context) this);

        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "RISK.LEVEL", "RELATION.CODE" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        List<TField> EcpRelationCodes = ParamConfig.get("CUSTOMER").get("RELATION.CODE");

     // UPDATING FULL NAME TO UPPER CASE, IF USER INPUTS MANUALLY
        String lFullName = null;
        for (int i = 0; (CustomerRec.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            String FullNameField = CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i)
                    .getLocalRefField("L.FULL.NAME").getValue();
            CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .setValue(FullNameField.toUpperCase());
            lFullName = FullNameField;
            if (i >= 1) {
                lFullName = lFullName + CustomerRec.getLocalRefGroups("L.FULL.NAME").get(i)
                        .getLocalRefField("L.FULL.NAME").getValue().toUpperCase();
            }
        }
        
        //SET SHORT NAME FROM COMPANY NAME
        String setFullName = null;
        
        if (lFullName.length() > 50) {
            setFullName = lFullName.substring(0, 50);
        } else {
            setFullName = lFullName;
        }

        CustomerRec.setShortName(setFullName.toUpperCase(), 0);

        
        // Default Province from District
        UpdateProvinceFromDistrict DistrictProvinceObj = new UpdateProvinceFromDistrict();
        DistrictProvinceObj.AddProvinceFromDistrict(CustomerRec, DataObj);
        String District = DistrictProvinceObj.getDistrict();
        if (!District.isEmpty()) {
            CustomerRec.getLocalRefField("L.PROVINCE").setValue(DistrictProvinceObj.getProvince());
        }

        // To check If any owner/shareholder/office bearer is recognized as a
        // PEP, the entity shall be automatically categorized as ‘High Risk’ in
        // Risk ratng field
        boolean PepCustomer = false;
        for (RelationCodeClass RelatioCode : CustomerRec.getRelationCode()) {
            for (TField EcpRelCode : EcpRelationCodes) {
                if (RelatioCode.getRelationCode().getValue().equals(EcpRelCode.getValue())) {
                    String RelCustId = RelatioCode.getRelCustomer().getValue();
                    PepCustomer = CheckCustomerPep(RelCustId, DataObj);
                }
            }
        }
        if (PepCustomer) {
            boolean RiskLevelBool = false;
            for (RiskAssetTypeClass RiskAssetClass : CustomerRec.getRiskAssetType()) {
                if (RiskAssetClass.getRiskLevel().getValue().equals("1")) {
                    RiskLevelBool = true;
                }
            }
            if (!RiskLevelBool) {
                RiskAssetTypeClass RiskAssetType = new RiskAssetTypeClass();
                RiskAssetType.setRiskLevel("1");
                CustomerRec.addRiskAssetType(RiskAssetType);
            }
        }

        // UPDATE RISK LEVEL & AUTO NEXT KYC REVIEW DATE BASED ON PEP
        CustomerRiskLevelNsb RiskLevelNsb = new CustomerRiskLevelNsb();
        //String PepStatusValue = CustomerRec.getCalcRiskClass().getValue();
        String PepStatusValue = CustomerRec.getManualRiskClass().getValue();

        if ((!PepStatusValue.isEmpty()) && (PepStatusValue.length() > 2)) {
            RiskLevelNsb.UpdateRisklevelNextKycdateNsb(PepStatusValue, CustomerRec, DataObj, ParamConfig);
        }

        // UPDATE AUTO NEXT KYC DATE
        String NextDate = RiskLevelNsb.UpdateAutoNextKycDate(CustomerRec, ParamConfig);
        CustomerRec.setAutoNextKycReviewDate(NextDate);
        currentRecord.set(CustomerRec.toStructure());

        TField ShortName = CustomerRec.getShortName(0);
        for (int i = 0; i < CustomerRec.getLegalId().size(); i++) {
            if (((LegalIdClass) CustomerRec.getLegalId().get(i)).getLegalHolderName().getValue().isEmpty()) {
                ((LegalIdClass) CustomerRec.getLegalId().get(i)).setLegalHolderName(ShortName);
            }
        }

        //DEFAULT FIELD KYC COMPLETE VALUES BASED ON PEP VALUE
        String KycValue = CustomerRec.getKycComplete().getValue();
        boolean KycYes = false;
        if (!KycValue.equals("YES")) {
            CustomerRec.setKycComplete("YES");
            for (int i = 0; CustomerRec.getPostingRestrict().size() > i; i++) {
                String PostingRestrict = CustomerRec.getPostingRestrict().get(i).getValue();
                if (PostingRestrict.equals("30")) {
                    KycYes = true;
                }
            }
            if (!KycYes) {
                CustomerRec.setPostingRestrict("30", CustomerRec.getPostingRestrict().size());
            }
        } else {
            for (int i = 0; CustomerRec.getPostingRestrict().size() > i; i++) {
                String PostingRestrict = CustomerRec.getPostingRestrict().get(i).getValue();
                if (PostingRestrict.equals("30")) {
                    CustomerRec.getPostingRestrict().get(i).setValue("");
                }
            }
        }
        
        currentRecord.set(CustomerRec.toStructure());
    }

    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        CustomerRecord Customer = new CustomerRecord(currentRecord);

        // VALIDATING L.FULL.NAME
        try {
            Customer.getLocalRefGroups("L.FULL.NAME").get(0).getLocalRefField("L.FULL.NAME").getValue();
        } catch (Exception e) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-FULLNAME.NSB");
        }

/*        for (int i = 0; (Customer.getLocalRefGroups("L.FULL.NAME").size()) > i; i++) {
            String FullNameField = Customer.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                    .getValue();
            Pattern my_pattern = Pattern.compile("[^a-z ]", Pattern.CASE_INSENSITIVE);
            Matcher my_match = my_pattern.matcher(FullNameField);
            boolean check = my_match.find();
            if (check) {
                Customer.getLocalRefGroups("L.FULL.NAME").get(i).getLocalRefField("L.FULL.NAME")
                        .setError("EB-CU.SPECIAL.CHAR.NSB");
            }
        }
*/        
        if (Customer.getLocalRefField("L.TAX.PAYER").getValue().equals("YES")) {
            try {
                Customer.getTaxId().get(0);
            } catch (Exception e) {
                throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUST.TAXID.NSB");
            }
        }

        

/*        if (Customer.getLocalRefField("L.AML.CHECK").getValue().isEmpty()){
            Customer.getLocalRefField("L.AML.CHECK").setError("EB-CUST.AML.CHECK.NSB");
        } else {
            try {
                Customer.getLocalRefGroups("L.AML.RESULT").get(0).getLocalRefField("L.AML.RESULT").getValue();
            } catch (Exception e) {
                throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUST.AML.RESULT.NSB");
            }
        }
*/                
        // To check if More Role info field is mandatory if Equity is selected
        // in Source of Beneficial Ownership field        
        for (int Cnt = 0; (Customer.getLocalRefGroups("L.BEN.OWNER").size()) > Cnt; Cnt++) {
            String BenOwner = Customer.getLocalRefGroups("L.BEN.OWNER").get(Cnt).getLocalRefField("L.BEN.OWNER")
                    .getValue();
            if (BenOwner.equals("EQUITY")) {
                try {
                    Customer.getRelationCode().get(Cnt).getRelDelivOpt().get(0).getRoleMoreInfo().getValue();
                } catch (Exception e) {
                    throw new T24CoreException("", "EB-MORE.ROLE.INFO");
                }
            }
        }

/*        //BLOCK CUSTOMER SCREEN FOR BLACKLIST CUSTOMER
        if (Customer.getLocalRefField("L.BLACK.LIST").getValue().equals("YES")) {
            Customer.getLocalRefField("L.BLACK.LIST").setError("EB-BLACKLIST.CUST.NSB");
        }
*/        
/*     // SET ERROR IF CUSTOMER CREATIONG DATE IS BACKDATED
        String CreationDate = Customer.getCustomerSince().getValue();
        Date SystemDate = new Date(this);
        String Today = SystemDate.getDates().getToday().getValue();
        LocalDate CreationDateFormat = LocalDate.of(Integer.parseInt(CreationDate.substring(0, 4)),
                Integer.parseInt(CreationDate.substring(4, 6)), Integer.parseInt(CreationDate.substring(6, 8)));
        LocalDate TodayFormat = LocalDate.of(Integer.parseInt(Today.substring(0, 4)),
                Integer.parseInt(Today.substring(4, 6)), Integer.parseInt(Today.substring(6, 8)));
        if (CreationDateFormat.isBefore(TodayFormat)) {
            Customer.getCustomerSince().setError("EB-CREATION.DATE");
        }
  */
        currentRecord.set(Customer.toStructure());

        return Customer.getValidationResponse();
    }

    private boolean CheckCustomerPep(String RelCustId, DataAccess DataObj) {
        boolean PepCustomer = false;
        CustomerRecord NewCustRec = new CustomerRecord(DataObj.getRecord("CUSTOMER", RelCustId));
        if (NewCustRec.getCalcRiskClass().getValue().startsWith("PEP")) {
            PepCustomer = true;
        }
        return PepCustomer;
    }
}