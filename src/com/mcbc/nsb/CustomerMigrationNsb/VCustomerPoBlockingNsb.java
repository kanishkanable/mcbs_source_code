package com.mcbc.nsb.CustomerMigrationNsb;

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

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        DataAccess daObj = new DataAccess(this);

        // GETTING PARAMETER VALUES
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "BLACKLIST.CUSTOMER" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(daObj);
        String PoBlocking = ParamConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(0).getValue();
        String PoBlockingValue = ParamConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(1).getValue();

        for (TField porestCnt : CustomerRec.getPostingRestrict()) {
            if (porestCnt.getValue().equals(PoBlocking)) {
                CustomerRec.getLocalRefField("L.BLACK.LIST").setValue(PoBlockingValue);
            }
        }

        currentRecord.set(CustomerRec.toStructure());
    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        // ROUTINE TO UPDATE CUSTOMER NUMBER TO CONCAT TABLE

        DataAccess DataObj = new DataAccess(this);
        CustomerRecord CustomerRec = new CustomerRecord(currentRecord);
        if (CustomerRec.getLocalRefField("L.BLACK.LIST").getValue().equals("YES")) {
            String EndDate = CustomerRec.getLocalRefField("L.END.DATE").getValue();
            Boolean CheckDupValue = false;
            EbBlacklistCustNsbRecord EbBlacklistCustNsbRec;
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
    }

    public Boolean CheckDuplicateAgeCustNsb(String currentRecordId, List<TStructure> currentRecords,
            EbBlacklistCustNsbRecord EbBlacklistCustNsbRec, Boolean CheckDupValue) {
        /*
         * FUNCTION TO CHECK IF THE CUSTOMER ALREADY EXIST IN
         * EB.CUSTOMER.AGE.NSB AND UPDATE THE FIELD
         */
        ListIterator<TField> CustomerList = EbBlacklistCustNsbRec.getCustomer().listIterator();
        while (CustomerList.hasNext()) {
            TField CustomerId = CustomerList.next();
            if (currentRecordId.equals(CustomerId.getValue())) {
                CheckDupValue = true;
            }
        }
        return CheckDupValue;
    }

}
