package com.mcbc.nsb.CustomerNsb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcancellednicnsb.EbCancelledNicNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthNicPostRestrictNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    private Boolean postRestExist = false;
    private String oldNicNumber = null;
    private String newNicNumber = null;
    private TField postingRestrict = null;
    private String disableYes = null;
    private String cusLegalId = null;

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        // SETTING PARAMETER VALUES FROM T24
        setParamValues();

        EbCancelledNicNsbRecord cancelledNicRec = new EbCancelledNicNsbRecord(currentRecord);
        String disableNic = cancelledNicRec.getDisable().getValue();

//        String DisableNic = cancelledNicRec.getDisable().getValue();

        if (currentRecordId.length() == 10) {
            cusLegalId = currentRecordId + "-" + oldNicNumber;
        } else if (currentRecordId.length() == 12) {
            cusLegalId = currentRecordId + "-" + newNicNumber;
        }

        List<String> CustomerList = dataObj.getConcatValues("CUS.LEGAL.ID", cusLegalId);
        Iterator<String> ListIterator = CustomerList.iterator();
        
        while (ListIterator.hasNext()) {
            String CustomerId = ListIterator.next();
            CustomerRecord customerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", CustomerId));
            
            List<TField> postRestList = customerRec.getPostingRestrict();
            int postRestCount = 0;
            for (TField postRest : postRestList) {
                if ((postRest.getValue().equals(postingRestrict.getValue())) ) {
                    postRestExist = true;
                    if(!disableNic.equals(disableYes)){
                        if (customerRec.getPostingRestrict().size() > 1) {
                            customerRec.setPostingRestrict("-", postRestCount);
                        } else {
                            customerRec.setPostingRestrict("NULL", postRestCount); 
                        }
                    }
                }
                postRestCount +=1;
            }
            
            if ((!postRestExist) && (disableNic.equals(disableYes))){
                customerRec.setPostingRestrict(postingRestrict.getValue(), customerRec.getPostingRestrict().size());
            }

            currentRecords.add(customerRec.toStructure());

            TransactionData td = new TransactionData();
            td.setFunction("INPUT");
            td.setNumberOfAuthoriser("0");
            td.setUserName("INPUTT");
            td.setSourceId("GENERIC.OFS.PROCESS");
            td.setTransactionId(CustomerId);
            td.setVersionId("CUSTOMER,SET.PO.CANCELNIC.NSB");
            transactionData.add(td);
        }
    }

    private void setParamValues() {
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("CANCELLED.NIC.NSB", new String[] { "NIC.VALUE", "POSTING.RESTRICT", "CANCEL.NIC.DISABLE" });
        Map<String, Map<String, List<TField>>> paramConfig = config.GetParamValue(dataObj);
        oldNicNumber = paramConfig.get("CANCELLED.NIC.NSB").get("NIC.VALUE").get(0).getValue();
        newNicNumber = paramConfig.get("CANCELLED.NIC.NSB").get("NIC.VALUE").get(1).getValue();
        postingRestrict = paramConfig.get("CANCELLED.NIC.NSB").get("POSTING.RESTRICT").get(0);
        disableYes = paramConfig.get("CANCELLED.NIC.NSB").get("CANCEL.NIC.DISABLE").get(0).getValue();
    }
}
