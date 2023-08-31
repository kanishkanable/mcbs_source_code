package com.mcbc.nsb.CashAtEase;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.records.user.UserRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebcaetransactionsnsb.EbCaeTransactionsNsbRecord;
import com.temenos.t24.api.tables.ebcaetransactionsnsb.EbCaeTransactionsNsbTable;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAutCaeTransIdentifyNsb extends RecordLifecycle {

    Session session = new Session(this);
    DataAccess dataObj = new DataAccess(this); 

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        PaymentOrderRecord PoRec = new PaymentOrderRecord(currentRecord);
        
        String inputter = null;
        
        try {
            String userId = PoRec.getInputter().get(PoRec.getInputter().size() -1);
            inputter = getUserName(userId.split("_")[1]);
        } catch (T24CoreException e) {
            inputter = "";
        }
        
        // EB.CAE.TRANSACTIONS.NSB
        EbCaeTransactionsNsbRecord eCaeTranRec = new EbCaeTransactionsNsbRecord();

        eCaeTranRec.setDebitAcctNo(PoRec.getDebitAccount().getValue());
        eCaeTranRec.setDebitCurrency(PoRec.getDebitCcy().getValue());
        eCaeTranRec.setDebitValueDate(PoRec.getDebitValueDate().getValue());

        eCaeTranRec.setCreditAcctNo(PoRec.getCreditAccount().getValue());
        eCaeTranRec.setCreditValueDate(PoRec.getCreditValueDate().getValue());
        eCaeTranRec.setCreditCurrency(PoRec.getCreditCurrency().getValue());
        eCaeTranRec.setCreditAmount(PoRec.getCreditAmount().getValue());

        eCaeTranRec.setInputter(inputter);
        eCaeTranRec.setAuthoriser(session.getUserRecord().getSignOnName().getValue());

        EbCaeTransactionsNsbTable eCaeTranTable = new EbCaeTransactionsNsbTable(this);
        try {
            eCaeTranTable.write(currentRecordId, eCaeTranRec);
        } catch (T24IOException e) {
        }

    }

    private String getUserName(String inputter) {
        String userName = new String();
        try {
//            UserRecord UserRec = new UserRecord(UserDataObj.getRecord("USER", Inputter));
            UserRecord userRec = new UserRecord(dataObj.getRecord("USER", inputter));
            userName = userRec.getUserName().getValue();
        } catch (T24CoreException e) {
            userName = inputter;
        }
        return userName;
    }
}
