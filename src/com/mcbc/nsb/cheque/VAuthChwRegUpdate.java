package com.mcbc.nsb.cheque;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebolbchqsregisternsb.EbOlbchqsRegisterNsbTable;
import com.temenos.t24.api.tables.ebolbchqsregisternsb.EbOlbchqsRegisterNsbRecord;

public class VAuthChwRegUpdate extends RecordLifecycle {
    DataAccess da = new DataAccess(this);
    EbOlbchqsRegisterNsbTable nsbChqRegTable = new EbOlbchqsRegisterNsbTable(this);
    EbOlbchqsRegisterNsbRecord nsbChqRegRecord = new EbOlbchqsRegisterNsbRecord(this);
    String benName = "";
    String chqCurr = "";
    String chqAmount = "";
    String cusAccountNo = "";
    String issueDate = "";
    String txnRef = "";
    String chqIssBank = "";
    String chqType = "";
    String chqRegId = "";

    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        TellerRecord tellRec = new TellerRecord(currentRecord);
        TransactionData txnData = new TransactionData();
        
        chqCurr = tellRec.getCurrency1().getValue();
        chqAmount = tellRec.getAmountLocal2().getValue();
        benName = tellRec.getPayeeName().getValue();
        cusAccountNo = tellRec.getAccount2().getValue();
        issueDate = tellRec.getValueDate1().getValue();
        txnRef = currentRecordId;
        chqIssBank = tellRec.getLocalRefField("L.CHQ.ISS.BANK").getValue();
        chqType = tellRec.getCheqType().getValue();
        chqRegId = chqType + "." + cusAccountNo;

        nsbChqRegRecord.setCurrency(chqCurr);
        nsbChqRegRecord.setChequeAmount(chqAmount);
        nsbChqRegRecord.setBeneficiaryName(benName);
        nsbChqRegRecord.setCusAccountNo(cusAccountNo);
        nsbChqRegRecord.setIssuedDate(issueDate);
        nsbChqRegRecord.setTransactionRef(txnRef);
        nsbChqRegRecord.setIssBankCode(chqIssBank);
        nsbChqRegRecord.setStatus("OUR.CHQ.ISS"); // to create eb.lookup
        nsbChqRegRecord.setChequePrinted("No");

        txnData.setFunction("INPUT");
        txnData.setSourceId("OFS.SOURCE");
        txnData.setNumberOfAuthoriser("0");
        txnData.setVersionId("EB.OLBCHQS.REGISTER.NSB,INPUT");
        txnData.setTransactionId(chqIssBank);
        transactionData.add(txnData);
        currentRecords.add(nsbChqRegRecord.toStructure());
    }
}
