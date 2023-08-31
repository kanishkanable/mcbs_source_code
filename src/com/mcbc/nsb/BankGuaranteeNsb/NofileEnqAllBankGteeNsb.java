package com.mcbc.nsb.BankGuaranteeNsb;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.records.user.UserRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class NofileEnqAllBankGteeNsb extends Enquiry {

    String FieldValue;
    String MdDealId;
    String MdDealCurrId;
    String MdDealHisId;
    int CurrNum;
    int HisNum;
    String CustomerId;
    String CustomerName;
    String ProcessGo;
    String InputterName;
    String AuthoriserName;
    int NewRecordValue = 0;

    String HistoryRecord;
    String CurrentRecord;
    List<String> HistoryRecordsList = new ArrayList<String>();
    List<String> CurrentRecordList = new ArrayList<String>();

    // Check Current fields
    String AlternateId;
    String Reference2;
    String Currency;
    String PrincipalAmount;
    String ValueDate;
    String AdviceExpiryDate;
    String MaturityDate;
    String BenefCustomer;
    List<String> BenefCustomerList = new ArrayList<String>();
    String BenAddress;
    List<String> BenAddressList = new ArrayList<String>();
    String Text;
    List<String> TextList = new ArrayList<String>();
    String ProvAmount;

    // Check History fields
    String HisAlternateId;
    String HisReference2;
    String HisCurrency;
    String HisPrincipalAmount;
    String HisValueDate;
    String HisAdviceExpiryDate;
    String HisMaturityDate;
    String HisBenefCustomer;
    List<String> HisBenefCustomerList = new ArrayList<String>();
    String HisBenAddress;
    List<String> HisBenAddressList = new ArrayList<String>();
    String HisText;
    List<String> HisTextList = new ArrayList<String>();
    String HisProvAmount;

    // Output Variables
    String FieldName;
    String OldValue;
    String NewValue;
    String CurrNumber;
    String InputDateTime;
    String Inputter;
    String Authoriser;

    MdDealRecord MdDealCurrRec;
    MdDealRecord MdDealLiveRec;
    MdDealRecord MdDealHisRec;
    List<String> outputData = new ArrayList<String>();

/*    T24Context EcpContext = new T24Context("EB.COMMON.PARAM.NSB");
    T24Context MdDealCurrContext = new T24Context("MD.DEAL");
    T24Context CustomerContext = new T24Context("CUSTOMER");
    T24Context UserContext = new T24Context("USER");

    DataAccess EcpDataObj = new DataAccess(EcpContext);
    DataAccess MdDealCurrDataObj = new DataAccess(MdDealCurrContext);
    DataAccess CustomerDataObj = new DataAccess(CustomerContext);
    DataAccess UserDataObj = new DataAccess(UserContext);
*/
    DataAccess DataObj = new DataAccess(this);
    
    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        
        try{
        MdDealId = getRecordIdfromFc(filterCriteria);
        
        try {
//            MdDealLiveRec = new MdDealRecord(MdDealCurrDataObj.getRecord("MD.DEAL", MdDealId));
            MdDealLiveRec = new MdDealRecord(DataObj.getRecord("MD.DEAL", MdDealId));
            
            CurrNumber = MdDealLiveRec.getCurrNo();
            CustomerId = MdDealLiveRec.getCustomer().getValue();
//            CustomerRecord CustomerRec = new CustomerRecord(CustomerDataObj.getRecord("CUSTOMER", CustomerId));
            CustomerRecord CustomerRec = new CustomerRecord(DataObj.getRecord("CUSTOMER", CustomerId));
            CustomerName = CustomerRec.getShortName(0).getValue();
        } catch (T24CoreException e) {
            throw new T24CoreException("", "EB-ENQ.NO.RECORD.NSB");
        }
        
        if (Integer.parseInt(CurrNumber) == 1){
            getHisRecordValues(MdDealLiveRec);
            String Inputter = MdDealLiveRec.getInputter().get(0).split("_")[1];
            String Authoriser = MdDealLiveRec.getAuthoriser().split("_")[1];
            this.InputterName = getUserName(Inputter);
            this.AuthoriserName = getUserName(Authoriser);
            this.InputDateTime = MdDealLiveRec.getDateTime(0);
            MdDealHisId = MdDealId;
            updateOutput(outputData);
        } else {
            checkRecords(MdDealLiveRec, CurrNumber);
        }
        
        } catch (T24CoreException e){
            e.printStackTrace();
        }
        return outputData;
    }

    private String getRecordIdfromFc(List<FilterCriteria> filterCriteria) {
        String FieldValue = null;
        for (FilterCriteria fieldNames : filterCriteria) {
            String FieldName = fieldNames.getFieldname();
            if (FieldName.equals("MD.DEAL")) {
                FieldValue = fieldNames.getValue();
            }
        }
        return FieldValue;
    }
    
    private List<String> checkRecords(MdDealRecord MdDealLiveRec, String CurrNumber){
        CurrNum = Integer.parseInt(CurrNumber);
        while (CurrNum > 1) {
            getCurrRecordValues(MdDealLiveRec);
            
            String Inputter = MdDealLiveRec.getInputter().get(0).split("_")[1];
            String Authoriser = MdDealLiveRec.getAuthoriser().split("_")[1];
            this.InputterName = getUserName(Inputter);
            this.AuthoriserName = getUserName(Authoriser);
            this.InputDateTime = MdDealLiveRec.getDateTime(0);
            
            CurrNum = CurrNum - 1;
            System.out.println("MdDealId 1  :  "+ MdDealId);
            MdDealHisId = MdDealId.split(";")[0] + ";" + String.valueOf(CurrNum);
            System.out.println("MdDealHisId 3 :  "+ MdDealHisId);
            
//            MdDealHisRec = new MdDealRecord(MdDealCurrDataObj.getRecord("", "MD.DEAL", "$HIS", MdDealHisId));
            MdDealHisRec = new MdDealRecord(DataObj.getRecord("", "MD.DEAL", "$HIS", MdDealHisId));
            getHisRecordValues(MdDealHisRec);
            
            NewRecordValue = 0;
            updateOutput(outputData);
            MdDealId = MdDealHisId;
            System.out.println("MdDealId 2 :  "+ MdDealId);
            MdDealLiveRec = MdDealHisRec;
        }
        return outputData;
    }
    
    private void getHisRecordValues(MdDealRecord MdDealHisRec) {
        this.HisAlternateId = MdDealHisRec.getAlternateId().getValue();
        this.HisReference2 = MdDealHisRec.getReference2().getValue();
        this.HisCurrency = MdDealHisRec.getCurrency().getValue();
        this.HisPrincipalAmount = MdDealHisRec.getPrincipalAmount().getValue();
        this.HisValueDate = MdDealHisRec.getValueDate().getValue();
        this.HisAdviceExpiryDate = MdDealHisRec.getAdviceExpiryDate().getValue();
        this.HisMaturityDate = MdDealHisRec.getMaturityDate().getValue();
        
        for (TField HisBenefCustomers : MdDealHisRec.getBenefCust1()) {
            this.HisBenefCustomer = HisBenefCustomers.getValue();
            HisBenefCustomerList.add(HisBenefCustomer);
        }

        for (TField HisBenAddresses : MdDealHisRec.getBenAddress()) {
            this.HisBenAddress = HisBenAddresses.getValue();
            HisBenAddressList.add(HisBenAddress);
        }

        for (TField HisTexts : MdDealHisRec.getText1()) {
            this.HisText = HisTexts.getValue();
            HisTextList.add(HisText);
        }
        this.HisProvAmount = MdDealHisRec.getProvAmount().getValue();
    }
    
    private void getCurrRecordValues(MdDealRecord MdDealCurrRec) {
        this.AlternateId = MdDealCurrRec.getAlternateId().getValue();
        this.Reference2 = MdDealCurrRec.getReference2().getValue();
        this.Currency = MdDealCurrRec.getCurrency().getValue();
        this.PrincipalAmount = MdDealCurrRec.getPrincipalAmount().getValue();
        this.ValueDate = MdDealCurrRec.getValueDate().getValue();
        this.AdviceExpiryDate = MdDealCurrRec.getAdviceExpiryDate().getValue();
        this.MaturityDate = MdDealCurrRec.getMaturityDate().getValue();

        for (TField BenefCustomers : MdDealCurrRec.getBenefCust1()) {
            this.BenefCustomer = BenefCustomers.getValue();
            this.BenefCustomerList.add(BenefCustomer);
        }

        for (TField BenAddresses : MdDealCurrRec.getBenAddress()) {
            this.BenAddress = BenAddresses.getValue();
            this.BenAddressList.add(BenAddress);
        }

        for (TField Texts : MdDealCurrRec.getText1()) {
            this.Text = Texts.getValue();
            this.TextList.add(Text);
        }

        this.ProvAmount = MdDealCurrRec.getProvAmount().getValue();
        String Inputter = MdDealCurrRec.getInputter().get(0).split("_")[1];
        String Authoriser = MdDealCurrRec.getAuthoriser().split("_")[1];

        this.InputterName = getUserName(Inputter);
        this.AuthoriserName = getUserName(Authoriser);
        this.InputDateTime = MdDealCurrRec.getDateTime(0);
    }
    
    private List<String> CompareAlternateIdRecords(List<String> OutputData) {
        HistoryRecord = HisAlternateId;
        CurrentRecord = AlternateId;
        FieldName = "Guarantee Ref";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareReference2Records(List<String> OutputData) {
        HistoryRecord = HisReference2;
        CurrentRecord = Reference2;
        FieldName = "Customer’s Reference";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareCurrencyRecords(List<String> OutputData) {
        HistoryRecord = HisCurrency;
        CurrentRecord = Currency;
        FieldName = "Currency";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> ComparePrincipalAmountRecords(List<String> OutputData) {
        HistoryRecord = HisPrincipalAmount;
        CurrentRecord = PrincipalAmount;
        FieldName = "Amount";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareValueDateRecords(List<String> OutputData) {
        HistoryRecord = HisValueDate;
        CurrentRecord = ValueDate;
        FieldName = "Start Date";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareAdviceExpiryDateRecords(List<String> OutputData) {
        HistoryRecord = HisAdviceExpiryDate;
        CurrentRecord = AdviceExpiryDate;
        FieldName = "Expiry Date";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareMaturityDateRecords(List<String> OutputData) {
        HistoryRecord = HisMaturityDate;
        CurrentRecord = MaturityDate;
        FieldName = "Maturity Date";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareBenefCustomerRecords(List<String> OutputData) {
        List<String> HistoryRecordsList = HisBenefCustomerList;
        List<String> CurrentRecordList = BenefCustomerList;
        FieldName = "Beneficiary Customer";
        OutputData = CompareMultivalueRecords(HistoryRecordsList, CurrentRecordList, FieldName);
        return OutputData;
    }

    private List<String> CompareBenAddressRecords(List<String> OutputData) {
        List<String> HistoryRecordsList = HisBenAddressList;
        List<String> CurrentRecordList = BenAddressList;
        FieldName = "Beneficiary Address";
        OutputData = CompareMultivalueRecords(HistoryRecordsList, CurrentRecordList, FieldName);
        return OutputData;
    }

    private List<String> CompareTextRecords(List<String> OutputData) {
        List<String> HistoryRecordsList = HisTextList;
        List<String> CurrentRecordList = TextList;
        FieldName = "Purpose";
        OutputData = CompareMultivalueRecords(HistoryRecordsList, CurrentRecordList, FieldName);
        return OutputData;
    }

    private List<String> CompareProvAmountRecords(List<String> OutputData) {
        HistoryRecord = HisProvAmount;
        CurrentRecord = ProvAmount;
        FieldName = "Margin Amount";
        OutputData = setOutputValues(HistoryRecord, CurrentRecord, OutputData, FieldName);
        return OutputData;
    }

    private List<String> CompareMultivalueRecords(List<String> HistoryRecordsList, List<String> CurrentRecordList,
            String FieldName) {
        int HisCount = HisBenefCustomerList.size();
        int currCount = BenefCustomerList.size();
        int itercount = Integer.max(HisCount, currCount);
        String CurrentRecord;
        String HistoryRecord;
        for (int loopCount = 0; loopCount > itercount; loopCount++) {
            try {
                HistoryRecord = HistoryRecordsList.get(loopCount);
            } catch (T24CoreException e) {
                HistoryRecord = null;
            }
            try {
                CurrentRecord = CurrentRecordList.get(loopCount);
            } catch (T24CoreException e) {
                CurrentRecord = null;
            }
            FieldName = FieldName + "." + String.valueOf(loopCount);
            outputData = setOutputValues(HistoryRecord, CurrentRecord, outputData, FieldName);
        }
        return outputData;
    }

    private List<String> setOutputValues(String HistoryRecord, String CurrentRecord, List<String> OutputData,
            String FieldName) {
        OldValue = getRecordValue(HistoryRecord);
        NewValue = getRecordValue(CurrentRecord);
        
        if (!OldValue.equals(NewValue)) {
            NewRecordValue = NewRecordValue + 1;
            
            if (NewRecordValue == 1) {
                OutputData.add(CustomerId + "*" + CustomerName + "*" + MdDealId + "*" + FieldName + "*" + OldValue + "*"
                        + NewValue + "*" + InputDateTime + "*" + InputterName + "*" + AuthoriserName);
            } else {
                OutputData.add("" + "*" + "" + "*" + "" + "*" + FieldName + "*" + OldValue + "*" + NewValue + "*" + "" + "*" + "" + "*" + "");
            }
        }
        return OutputData;
    }
    
    private String getRecordValue(String RecordValue) {
        String OldorNewValue = null;

        try {
            RecordValue.isEmpty();
            OldorNewValue = RecordValue;
        } catch (NullPointerException e) {
            OldorNewValue = "";
        }

        return OldorNewValue;
    }
    
    private String getUserName(String Inputter) {
        String UserName = new String();
        try {
//            UserRecord UserRec = new UserRecord(UserDataObj.getRecord("USER", Inputter));
            UserRecord UserRec = new UserRecord(DataObj.getRecord("USER", Inputter));
            UserName = UserRec.getUserName().getValue();
        } catch (T24CoreException e) {
            UserName = Inputter;
        }
        return UserName;
    }
    
    private List<String> updateOutput(List<String> OutputData){
        OutputData = CompareAlternateIdRecords(OutputData);
        OutputData = CompareReference2Records(OutputData);
        OutputData = CompareCurrencyRecords(OutputData);
        OutputData = ComparePrincipalAmountRecords(OutputData);
        OutputData = CompareValueDateRecords(OutputData);
        OutputData = CompareAdviceExpiryDateRecords(OutputData);
        OutputData = CompareMaturityDateRecords(OutputData);
        OutputData = CompareBenefCustomerRecords(OutputData);
        OutputData = CompareBenAddressRecords(OutputData);
        OutputData = CompareTextRecords(OutputData);
        OutputData = CompareProvAmountRecords(OutputData);
        return OutputData;
    }
}
