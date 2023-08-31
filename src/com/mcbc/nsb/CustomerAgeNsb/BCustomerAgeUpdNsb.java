package com.mcbc.nsb.CustomerAgeNsb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustGenderFullShortNameNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebcustomeragensb.EbCustomerAgeNsbRecord;
import com.temenos.t24.api.tables.ebcustomermajornsb.EbCustomerMajorNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BCustomerAgeUpdNsb extends ServiceLifecycle {

    DataAccess dataObj = new DataAccess(this);
//    String AgeRecordId = "0826";
    String AgeRecordId = "0114";
    
    List<String> Returnval = new ArrayList<String>();
    Session SessionContext = new Session(this);
    String Branch = SessionContext.getCompanyId();
    Date TodayDt = new Date(this);
    String TodayDtFormat = TodayDt.getDates().getToday().getValue();
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub

        System.out.println("********* getIds  **************");
        TStructure AgeRecord = dataObj.getRecord("EB.CUSTOMER.AGE.NSB", AgeRecordId);
        System.out.println("********* getIds 1 ************** ");
        EbCustomerAgeNsbRecord CustAgeRecord = new EbCustomerAgeNsbRecord(AgeRecord);
        System.out.println("********* getIds 2 ************** ");
        for (TField i : CustAgeRecord.getCustomer()) {
            System.out.println("********* getIds 3 **************  :  " + i.getValue());
            Returnval.add(i.getValue());
            System.out.println("********* getIds 4 **************  :  " + Returnval);
        }
        System.out.println("********* getIds 5 **************  :  " + Returnval);
        return Returnval;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub

        System.out.println("********* updateRecord  **************");
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER",
                new String[] { "MAJOR.TARGET", "MAJOR.TITLE.MAST", "MAJOR.TITLE.MISS" });

        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        String MajorTarget = ParamConfig.get("CUSTOMER").get("MAJOR.TARGET").get(0).getValue();
        String MastTitle = ParamConfig.get("CUSTOMER").get("MAJOR.TITLE.MAST").get(0).getValue();
        String MrTitle = ParamConfig.get("CUSTOMER").get("MAJOR.TITLE.MAST").get(1).getValue();
        String MissTitle = ParamConfig.get("CUSTOMER").get("MAJOR.TITLE.MISS").get(0).getValue();
        String MsTitle = ParamConfig.get("CUSTOMER").get("MAJOR.TITLE.MISS").get(1).getValue();

        System.out.println("********* updateRecord 77 **************  " + MajorTarget);
        System.out.println("********* updateRecord 78 **************  " + MastTitle);
        System.out.println("********* updateRecord 79 **************  " + MrTitle);
        System.out.println("********* updateRecord 80 **************  " + MissTitle);
        System.out.println("********* updateRecord 81 **************  " + MsTitle);
        
        CustomerRecord CustomerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", id));
        System.out.println("********* updateRecord 84 **************  ");
        String DateOfDeath = CustomerRec.getDeathDate().getValue();
        System.out.println("********* updateRecord 86 **************  "  + DateOfDeath);
        
        int newage = Integer.parseInt(CustomerRec.getLocalRefField("L.CUST.AGE").getValue().substring(0, 3)) + 1;
        System.out.println("********* updateRecord 89 **************  "  + newage);
        
        String CustMajId = id + "-" + TodayDtFormat + "-" + Branch;
        System.out.println("********* updateRecord 92 **************  "  + CustMajId);
        
        Collection<SynchronousTransactionData> ReturTransactionDataList = new ArrayList<SynchronousTransactionData>();
        System.out.println("********* updateRecord 95 **************  ");
        
        if (newage == 16) {
            System.out.println("********* updateRecord 98 **************  "  + newage);
            CustomerRec.setTarget(MajorTarget);
            System.out.println("********* updateRecord 100 **************  "  + MajorTarget);
            EbCustomerMajorNsbRecord CustMajorRecord = new EbCustomerMajorNsbRecord();
            System.out.println("********* updateRecord 102 **************  ");
            CustMajorRecord.setCustomer(id, 0);
            System.out.println("********* updateRecord 104 **************  "  + id);
            CustMajorRecord.setDateCustMajor(TodayDtFormat);
            System.out.println("********* updateRecord 106 **************  "  + TodayDtFormat);

            // Changing title to Mr or Miss when customer is 16 years old
            if (CustomerRec.getTitle().getValue().equals(MastTitle)) {
                System.out.println("********* updateRecord 110 **************  "  + CustomerRec.getTitle().getValue());
                System.out.println("********* updateRecord 111 **************  "  + MastTitle);
                CustomerRec.setTitle(MrTitle);
                System.out.println("********* updateRecord 113 **************  "  + MrTitle);
            } else if (CustomerRec.getTitle().getValue().equals(MissTitle)) {
                System.out.println("********* updateRecord 115 **************  "  + CustomerRec.getTitle().getValue());
                System.out.println("********* updateRecord 116 **************  "  + MissTitle);
                CustomerRec.setTitle(MsTitle);
                System.out.println("********* updateRecord 118 **************  "  + MsTitle);
            }
            System.out.println("********* updateRecord 120 **************  ");

            // Changing the Short name of the customer since the title is
            // changed
            CustGenderFullShortNameNsb GenderFullShortName = new CustGenderFullShortNameNsb();
            System.out.println("********* updateRecord 125 **************  ");
            GenderFullShortName.UpdateGenderFullAndShortName(CustomerRec, dataObj, id);
            System.out.println("********* updateRecord 127 **************  ");
            String ShortName = GenderFullShortName.GetShortName();
            System.out.println("********* updateRecord 129 **************  " + ShortName);
            CustomerRec.setShortName(ShortName, 0);
            System.out.println("********* updateRecord 131 **************  ");

            // KYC complete to NO
            CustomerRec.setKycComplete("NO");
            System.out.println("********* updateRecord 135 **************  ");
            
            // Set posting restrict 103
            Boolean PostRestExist = false;
            System.out.println("********* updateRecord 139 **************  ");
            List<TField> PostRestList = CustomerRec.getPostingRestrict();
            System.out.println("********* updateRecord 131 **************  " + PostRestList);

            for (TField PostRest : PostRestList) {
                System.out.println("********* updateRecord 144 **************  ");    
                if (PostRest.equals("30")) {
                    System.out.println("********* updateRecord 146 **************  ");
                    PostRestExist = true;
                    System.out.println("********* updateRecord 148 **************  ");
                }
                System.out.println("********* updateRecord 150 **************  ");
            }
            
            System.out.println("Routine Triggered    :    " + PostRestExist);
            if (!PostRestExist) {
                System.out.println("Routine Triggered    :    " + PostRestExist);
                CustomerRec.setPostingRestrict("103", CustomerRec.getPostingRestrict().size());
                System.out.println("Routine Triggered    :    " + PostRestExist);
            }
            
            System.out.println("********* updateRecord 160 **************  ");
            // CustMajorRecord.
            records.add(CustMajorRecord.toStructure());
            System.out.println("********* updateRecord 163 **************  ");
            
            SynchronousTransactionData td = new SynchronousTransactionData();
            td.setFunction("INPUT");
            td.setNumberOfAuthoriser("0");
            td.setSourceId("GENERIC.OFS.PROCESS");
            td.setTransactionId(CustMajId);
            td.setVersionId("EB.CUSTOMER.MAJOR.NSB,UPDATE");
            ReturTransactionDataList.add(td);
            System.out.println("********* updateRecord 172 **************  " + td);
        }

        System.out.println("********* updateRecord 175 **************  ");
        if (DateOfDeath.isEmpty()) {
            System.out.println("********* updateRecord 177 **************  ");
            CustomerRec.getLocalRefField("L.CUST.AGE").setValue(String.format("%03d", newage) + " Years");
            System.out.println("********* updateRecord 179 **************  ");
        }
        System.out.println("********* updateRecord 181 **************  ");
        records.add(CustomerRec.toStructure());
        System.out.println("********* updateRecord 183 **************  ");
        
        SynchronousTransactionData td1 = new SynchronousTransactionData();
        td1.setFunction("INPUT");
        td1.setNumberOfAuthoriser("0");
        // td.setUserName("INPUTT"); td1.setSourceId("GENERIC.OFS.PROCESS");
        td1.setTransactionId(id);
        td1.setVersionId("CUSTOMER,OFS.CUSTAGE.NSB");
        ReturTransactionDataList.add(td1);

        System.out.println("********* updateRecord 193 **************  ");
        
        // transactionData.add(td1);
        transactionData.addAll(ReturTransactionDataList);
    }

}
