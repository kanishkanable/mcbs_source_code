package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.Property;
import com.temenos.t24.api.complex.aa.activityhook.TransactionData;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.aaneoaccttxndrawnsb.AaNeoAcctTxnDrawNsbRecord;
import com.temenos.t24.api.tables.aaneoaccttxndrawnsb.AaNeoAcctTxnDrawNsbTable;
import com.temenos.t24.api.tables.ebacctranavmaintnsb.EbAcctRanavMaintNsbRecord;
import com.temenos.t24.api.tables.ebacctranavmaintnsb.EbAcctRanavMaintNsbTable;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAPstNeoAccttxnCheckNsb extends ActivityLifecycle {

    @Override
    public void postCoreTableUpdate(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            List<TransactionData> transactionData, List<TStructure> transactionRecord) {
        // TODO Auto-generated method stub
        final Logger LOGGER = Logger.getLogger(AAPostGenScondarAct.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info(" accountDetailRecord - " + accountDetailRecord);
        LOGGER.info("arrangementContext  - " + arrangementContext);
        LOGGER.info("arrangementActivityRecord  - " + arrangementActivityRecord);
        LOGGER.info("masterActivityRecord  - " + masterActivityRecord);
        LOGGER.info("productPropertyRecord  - " + productPropertyRecord);
        LOGGER.info("productRecord  - " + productRecord);
        LOGGER.info("arrangementActivityRecord.txncontractId  - "
                + arrangementActivityRecord.getTxnContractId().getValue());
        LOGGER.info("Masteract.txncontract  - " + masterActivityRecord.getTxnContractId().getValue());

        String aarangementId = arrangementContext.getArrangementId();
        String aaAaActivityId = arrangementContext.getArrangementActivityId();
        String aaActStatus = arrangementContext.getActivityStatus();
        String aaStatus = arrangementRecord.getArrStatus().toString();
        
        //Write logic to validate the mode of txn to see if txn withdraw other than online method
        //
        
        
        String txnref = arrangementActivityRecord.getTxnContractId().getValue();
        String effdate = arrangementActivityRecord.getEffectiveDate().getValue();

        DataAccess da = new DataAccess(this);

        // to avoid triggering of duplicate LENDING-UPDATE-ACCOUNT activities
        // During Apply payment activity child transaction activity
        if (!arrangementActivityRecord.getTxnContractId().getValue().isEmpty()
                && arrangementActivityRecord.getTxnContractId().getValue().startsWith("AAA")) {
            return;
        }

        // This is for FT/TT transaction status
        if (aaActStatus.equals("UNAUTH")) {
            return;
        }

        try {
            if ((aaStatus.equals("AUTH")) && (aaActStatus.equals("AUTH"))) {
                TStructure neo_str = da.getRecord("AA.NEO.ACCT.TXN.DRAW.NSB", aarangementId);
                AaNeoAcctTxnDrawNsbRecord Neorec = new AaNeoAcctTxnDrawNsbRecord(neo_str);
                AaNeoAcctTxnDrawNsbTable Neotable = new AaNeoAcctTxnDrawNsbTable(this);
                if(Neorec.getStatus().equals("CANCEL")) {
                    Neorec.setAccountNumber(aarangementId);
                    Neorec.setStatus("NEW");
                    Neorec.setTxnReference(txnref);
                    Neorec.setValueDate(effdate);                    
                    Neotable.write(aarangementId, Neorec);
                }
            }
        } catch (Exception e) {
            System.out.println("Creating a new record");
            AaNeoAcctTxnDrawNsbRecord Neorec = new AaNeoAcctTxnDrawNsbRecord();
            AaNeoAcctTxnDrawNsbTable Neotable = new AaNeoAcctTxnDrawNsbTable(this);
            try {
                TStructure acc_str = da.getRecord("AA.NEO.ACCT.TXN.DRAW.NSB", aarangementId);
            } catch (Exception e2) {
                // Ensure that New record creating only for because of
                // missing record in that table.
                // Sometime due to other exception also catch section can
                // execute.
                Neorec.setAccountNumber(aarangementId);
                Neorec.setStatus("NEW");
                Neorec.setTxnReference(txnref);
                Neorec.setValueDate(effdate);
                try {
                    Neotable.write(aarangementId, Neorec);
                } catch (Exception e3) {
                    System.out.println("Write failed");
                }
            }
        }
    }
}
