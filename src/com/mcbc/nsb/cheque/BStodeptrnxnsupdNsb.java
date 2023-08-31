package com.mcbc.nsb.cheque;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.standingorder.StandingOrderRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.standingorder.ActualExecutionStageClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebstodeptrxnsthrchqsnsb.EbStoDepTrxnsThrchqsNsbRecord;
import com.temenos.t24.api.tables.ebstodeptrxnsthrchqsnsb.EbStoDepTrxnsThrchqsNsbTable;

public class BStodeptrnxnsupdNsb extends ServiceLifecycle {
    List<String> recIds = new ArrayList<String>();
    List<ActualExecutionStageClass> actExcStage = null;
    DataAccess da = new DataAccess(this);
    StandingOrderRecord stoRec = null;
    CustomerRecord cusRec = null;
    EbStoDepTrxnsThrchqsNsbRecord stoTrnxRec = null;
    AccountRecord accRec = null;
    EbStoDepTrxnsThrchqsNsbTable stoTrnxTable = new EbStoDepTrxnsThrchqsNsbTable(this);
    String transRef = "";
    String transType = "";
    String transAmt = "";
    String transdate = "";
    String customerId = "";
    String customerName = "";
    String drAcct = "";
    String benName = "";
    String benId = "";
    String cntrlst = "";

    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        if (controlList.isEmpty()) {
            controlList.add("STO.ENTRY");
            controlList.add("PAYOUT.ENTRY");
        }
        cntrlst = controlList.get(0);
        if (cntrlst.equals("STO.ENTRY")) {
            recIds = da.selectRecords("", "STANDING.ORDER", "", "WITH PAY.METHOD EQ OC");
        }
        if (cntrlst.equals("PAYOUT.ENTRY")) {
            recIds = da.selectRecords("", "DEPOSIT.INT.PAYOUT", "", "");
        }
        return recIds;
    }

    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        switch (controlItem) {
        case "STO.ENTRY":
            stoRec = new StandingOrderRecord(da.getRecord("STANDING.ORDER", id));
            actExcStage = stoRec.getActualExecutionStage();
            transRef = id;
            transdate = stoRec.getLocalRefField("CREATION.DATE").getValue();
            transAmt = stoRec.getCurrentAmountBal().getValue();
            String[] idSplit = id.split("[.]");
            drAcct = idSplit[0];
            accRec = new AccountRecord(da.getRecord("ACCOUNT", drAcct));
            customerId = accRec.getCustomer().getValue();
            cusRec = new CustomerRecord(da.getRecord("CUSTOMER", customerId));
            customerName = cusRec.getName1(0).getValue();
            benId = stoRec.getBeneficiaryId().getValue();
            benName = stoRec.getLocalRefField("L.BEN.REM.NAME").getValue();
            transType = "STO";
            writeDataToStoLocTable();

            break;
        case "PAYOUT.ENTRY":
        }
    }

    public void writeDataToStoLocTable() {
        stoTrnxRec = new EbStoDepTrxnsThrchqsNsbRecord(this);
        stoTrnxRec.setCustomerId(customerId);
        stoTrnxRec.setCustomerName(customerName);
        stoTrnxRec.setBeneficiaryName(benName);
        stoTrnxRec.setBeneficiaryId(benId);
        stoTrnxRec.setDebitAccount(drAcct);

        try {
            stoTrnxTable.write(customerId, stoTrnxRec);
        } catch (T24IOException e) {
        }
    }

}
