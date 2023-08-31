package com.mcbc.nsb.CustomerMigrationNsb;

import java.util.Iterator;
import java.util.List;

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

    DataAccess DataObj = new DataAccess(this);

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        EbCancelledNicNsbRecord CancelledNicRec = new EbCancelledNicNsbRecord(currentRecord);
        String DisableNic = CancelledNicRec.getDisable().getValue();
        String CusLegalId = null;
        String OldNicNumber = "NATIONAL.ID.OLD";
        String NewNicNumber = "NATIONAL.ID";

        if (currentRecordId.length() == 10) {
            CusLegalId = currentRecordId + "-" + OldNicNumber;
        } else if (currentRecordId.length() == 12) {
            CusLegalId = currentRecordId + "-" + NewNicNumber;
        }

        if (DisableNic.equals("YES")) {
            List<String> CustomerList = DataObj.getConcatValues("CUS.LEGAL.ID", CusLegalId);
            Iterator<String> ListIterator = CustomerList.iterator();
            while (ListIterator.hasNext()) {
                String CustomerId = ListIterator.next();
                CustomerRecord CustomerRec = new CustomerRecord(DataObj.getRecord("CUSTOMER", CustomerId));
                Boolean PostRestExist = false;

                List<TField> PostRestList = CustomerRec.getPostingRestrict();
                for (TField PostRest : PostRestList) {
                    if (PostRest.equals("104")) {
                        PostRestExist = true;
                    }
                }

                if (!PostRestExist) {
                    CustomerRec.setPostingRestrict("104", CustomerRec.getPostingRestrict().size());
                    currentRecords.add(CustomerRec.toStructure());

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
        }
    }

}
