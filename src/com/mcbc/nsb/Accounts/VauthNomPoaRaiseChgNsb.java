package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.acchargerequest.ChargeCodeClass;
import com.temenos.t24.api.tables.aanomineepoa.AaNomineePoaRecord;
import com.temenos.t24.api.tables.aanomineepoansb.AaNomineePoaNsbRecord;
import com.temenos.t24.api.tables.aanomineepoansb.TypeClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */

// auth routine to raise the charge for AA.NOMINEE.POA.NSB,AUTH

public class VauthNomPoaRaiseChgNsb extends RecordLifecycle {

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        // super.postUpdateRequest(application, currentRecordId, currentRecord,
        // transactionData, currentRecords,
        // transactionContext);
        final Logger LOGGER = Logger.getLogger(VauthNomPoaRaiseChgNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine - ");

        AaNomineePoaNsbRecord aanominpoa = new AaNomineePoaNsbRecord(currentRecord);

        List<TypeClass> Choosetype = aanominpoa.getType();
        for (TypeClass choosetype : Choosetype) {
            boolean flag1 = false;
            String charge_acc_no = choosetype.getChargeAccount().getValue();
            String charge_type = choosetype.getServiceCharge().getValue();
            if (!charge_type.isEmpty() && charge_type.equalsIgnoreCase("By Debit Of Account")) {
                AcChargeRequestRecord chargreqrec = new AcChargeRequestRecord();
                chargreqrec.setRequestType("BOOK");
                chargreqrec.setDebitAccount(charge_acc_no);
                ChargeCodeClass chrgcode = new ChargeCodeClass();
                chrgcode.setChargeCode("CORRBKCHG");
                chargreqrec.setChargeCode(chrgcode, 0);
                chargreqrec.setExtraDetails("NOMINEE/POA CHARGE", 0);
                chargreqrec.setStatus("PAID");

                currentRecords.add(chargreqrec.toStructure());

                TransactionData td = new TransactionData();
                td.setFunction("INPUT");
                td.setNumberOfAuthoriser("0");
                // td.setUserName("INPUTT");
                td.setSourceId("BULK.OFS");
                // td.setTransactionId(AgeRecordId);
                td.setVersionId("AC.CHARGE.REQUEST,OFS.NSB");
                transactionData.add(td);
            }
        }
    }
}
