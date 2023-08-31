package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebblacklistcustnsb.EbBlacklistCustNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VCustomerPoBlockingNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);
    String endDate = null;
    String poBlocking;
    String poBlockingValue;
    String poNoBlockingValue;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        CustomerRecord customerRec = new CustomerRecord(currentRecord);
        // GETTING PARAMETER VALUES
        setParamValues();
        
/*
        for (TField porestCnt : customerRec.getPostingRestrict()) {
            if ((porestCnt.getValue().equals(poBlocking))) {
                customerRec.getLocalRefField("L.BLACK.LIST").setValue(poBlockingValue);
            }
        }
*/
        String postRestrictList = customerRec.getPostingRestrict().toString();
        
        if (postRestrictList.contains(poBlocking)){
            customerRec.getLocalRefField("L.BLACK.LIST").setValue(poBlockingValue);
        } else {
            customerRec.getLocalRefField("L.BLACK.LIST").setValue(poNoBlockingValue);
        }
            
        currentRecord.set(customerRec.toStructure());
    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        // ROUTINE TO UPDATE CUSTOMER NUMBER TO CONCAT TABLE
        try {
            CustomerRecord customerRec = new CustomerRecord(currentRecord);
            EbBlacklistCustNsbRecord EbBlacklistCustNsbRec;
            if (customerRec.getLocalRefField("L.BLACK.LIST").getValue().equals("YES")) {
                String EndDate = getEndDate(customerRec);
                Boolean CheckDupValue = false;
                try {
                    TStructure BlackListCustRec = DataObj.getRecord("EB.BLACKLIST.CUST.NSB", EndDate);
                    EbBlacklistCustNsbRec = new EbBlacklistCustNsbRecord(BlackListCustRec);
                    CheckDupValue = CheckDuplicateAgeCustNsb(currentRecordId, currentRecords, EbBlacklistCustNsbRec,
                            CheckDupValue);
                } catch (Exception e) {
                    EbBlacklistCustNsbRec = new EbBlacklistCustNsbRecord();
                    CheckDupValue = CheckDuplicateAgeCustNsb(currentRecordId, currentRecords, EbBlacklistCustNsbRec,
                            CheckDupValue);
                }
                if (!CheckDupValue) {
                    EbBlacklistCustNsbRec.setCustomer(currentRecordId, EbBlacklistCustNsbRec.getCustomer().size());
                    currentRecords.add(EbBlacklistCustNsbRec.toStructure());
                }
                
                TransactionData td = new TransactionData();
                td.setFunction("INPUT");
                td.setNumberOfAuthoriser("0");
                td.setUserName("INPUTT");
                td.setSourceId("GENERIC.OFS.PROCESS");
                td.setTransactionId(EndDate);
                td.setVersionId("EB.BLACKLIST.CUST.NSB,UPDATE.NSB");
                transactionData.add(td);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean CheckDuplicateAgeCustNsb(String currentRecordId, List<TStructure> currentRecords,
            EbBlacklistCustNsbRecord ebBlacklistCustNsbRec, Boolean checkDupValue) {
        /*
         * FUNCTION TO CHECK IF THE CUSTOMER ALREADY EXIST IN
         * EB.CUSTOMER.AGE.NSB AND UPDATE THE FIELD
         */
        ListIterator<TField> customerList = ebBlacklistCustNsbRec.getCustomer().listIterator();
        while (customerList.hasNext()) {
            TField customerId = customerList.next();
            if (currentRecordId.equals(customerId.getValue())) {
                checkDupValue = true;
            }
        }
        return checkDupValue;
    }

    private String getEndDate(CustomerRecord customerRec){
        
        List<TField> postingRestrictList = customerRec.getPostingRestrict();
        int postRestCount = 0;
        for (TField postRest : postingRestrictList){
            if (postRest.getValue().equals("14")){
             break;   
            }
            postRestCount =+1;
        }
        
        String endDatelt = customerRec.getLocalRefGroups("").get(postRestCount).getLocalRefField("L.END.DATE").getValue();
        
        return endDatelt;
    }
 
    private void setParamValues(){
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("CUSTOMER", new String[] { "BLACKLIST.CUSTOMER" });
        Map<String, Map<String, List<TField>>> paramConfig = config.GetParamValue(DataObj);
        poBlocking = paramConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(0).getValue();
        poBlockingValue = paramConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(1).getValue();        
        poNoBlockingValue = paramConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(2).getValue();
    }
    
}
