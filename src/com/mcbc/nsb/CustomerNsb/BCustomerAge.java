package com.mcbc.nsb.CustomerNsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CustomerCommonUtils.CustGenderFullShortNameNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebcustomeragensb.EbCustomerAgeNsbRecord;
import com.temenos.t24.api.tables.ebcustomermajornsb.EbCustomerMajorNsbRecord;
import com.temenos.t24.api.tables.ebcustomermajornsb.EbCustomerMajorNsbTable;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * Added line by Prakash for GIT Testing
 * Added new line for Testing
 *
 */

public class BCustomerAge extends ServiceLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Session sessionContext = new Session(this);
    Date systemDate = new Date(this);
    
    String TodayDate = systemDate.getDates().getToday().getValue();
    //0419
    String lastWorkingDay = systemDate.getDates().getLastWorkingDay().getValue();
    //0503
    
    String AgeRecordIdLwd = lastWorkingDay.substring(4, 8);
    String AgeRecordIdToday = TodayDate.substring(4, 8);
    //String AgeRecordId = "0828";
    List<String> returnValue = new ArrayList<String>();
    
    String majorTarget = null;
    String mastTitle = null;
    String mrTitle = null;
    String missTitle = null;
    String msTitle = null;
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        
        try {
            List<String> AgeRecordId = dataObj.selectRecords("", "EB.CUSTOMER.AGE.NSB", "", "WITH @ID LIKE 123...");
                    //(, AgeRecordId);
            System.out.println("git branch");
            TStructure AgeRecord = dataObj.getRecord("EB.CUSTOMER.AGE.NSB", AgeRecordId);
            EbCustomerAgeNsbRecord CustAgeRecord = new EbCustomerAgeNsbRecord(AgeRecord);
            for (TField i : CustAgeRecord.getCustomer()) {
                returnValue.add(i.getValue());
            }
        } catch (Exception e) {
            return returnValue;
        }
        return returnValue;
    }

    
@Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub

    settingParameterValues();
            
    CustomerRecord customerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", id));
    String dateOfDeath = customerRec.getDeathDate().getValue();
    
    int newage = Integer.parseInt(customerRec.getLocalRefField("L.CUST.AGE").getValue().substring(0, 3)) + 1;
    
    String branch = sessionContext.getCompanyId();
    
    String todayDtFormat = systemDate.getDates().getToday().getValue();
    String custMajId = id + "-" + todayDtFormat + "-" + branch;
    
    if (newage == 16) {
        customerRec.setTarget(majorTarget);
        EbCustomerMajorNsbRecord CustMajorRecord = new EbCustomerMajorNsbRecord();
        CustMajorRecord.setCustomer(id, 0);
        CustMajorRecord.setDateCustMajor(todayDtFormat);
        
        //Changing title to Mr or Miss when customer is 16 years old
        if (customerRec.getTitle().getValue().equals(mastTitle)){
            customerRec.setTitle(mrTitle);
        } else if (customerRec.getTitle().getValue().equals(missTitle)){
            customerRec.setTitle(msTitle);
        }
        
        //Changing the Short name of the customer since the title is changed 
        CustGenderFullShortNameNsb GenderFullShortName = new CustGenderFullShortNameNsb();
        GenderFullShortName.UpdateGenderFullAndShortName(customerRec, dataObj, id);
        String ShortName = GenderFullShortName.GetShortName();
        customerRec.setShortName(ShortName, 0);
        
        //KYC complete to NO
        customerRec.setKycComplete("NULL");
        
        //Set posting restrict 103
        Boolean PostRestExist = false;
        List<TField> PostRestList = customerRec.getPostingRestrict();
        for (TField PostRest : PostRestList){
            if (PostRest.equals("30")) {
                PostRestExist = true;
            }
        }

        if (!PostRestExist) {
            customerRec.setPostingRestrict("103", customerRec.getPostingRestrict().size());
        }
        
        // CustMajorRecord.
/*            records.add(CustMajorRecord.toStructure());
        System.out.println("updateRecord   171  : " + records);
        
        SynchronousTransactionData td = new SynchronousTransactionData();
//        TransactionData td = new TransactionData();
        td.setFunction("INPUTT");
        td.setNumberOfAuthoriser("0");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(custMajId);
        td.setVersionId("EB.CUSTOMER.MAJOR.NSB,UPDATE");
        ReturTransactionDataList.add(td);
        System.out.println("updateRecord   180  : " + td);
*/          
        EbCustomerMajorNsbTable EbCustomerMajorNsbTable = new EbCustomerMajorNsbTable(this);
        try {
            EbCustomerMajorNsbTable.write(custMajId, CustMajorRecord);
        } catch (T24IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
        }
    }
    
    if (dateOfDeath.isEmpty()){
        customerRec.getLocalRefField("L.CUST.AGE").setValue(String.format("%03d", newage) + " Years");
    }
    records.add(customerRec.toStructure());

    TransactionData td1 = new TransactionData();
    td1.setFunction("INPUTT");
    td1.setNumberOfAuthoriser("0");
    td1.setSourceId("GENERIC.OFS.PROCESS");
    td1.setTransactionId(id);
    td1.setVersionId("CUSTOMER,OFS.CUSTAGE.NSB");
    transactionData.add(td1);

    }

    private void settingParameterValues(){ 
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("CUSTOMER", new String[] { "MAJOR.TARGET", "MAJOR.TITLE.MAST", "MAJOR.TITLE.MISS" });

        Map<String, Map<String, List<TField>>> paramConfig = config.GetParamValue(dataObj);
        majorTarget = paramConfig.get("CUSTOMER").get("MAJOR.TARGET").get(0).getValue();
        mastTitle = paramConfig.get("CUSTOMER").get("MAJOR.TITLE.MAST").get(0).getValue();
        mrTitle = paramConfig.get("CUSTOMER").get("MAJOR.TITLE.MAST").get(1).getValue();
        missTitle = paramConfig.get("CUSTOMER").get("MAJOR.TITLE.MISS").get(0).getValue();
        msTitle = paramConfig.get("CUSTOMER").get("MAJOR.TITLE.MISS").get(1).getValue();
        
        dataObj.GET
    }
}