package com.mcbc.nsb.Accounts;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.FieldPair;
import com.temenos.t24.api.complex.aa.activityhook.Property;
import com.temenos.t24.api.complex.aa.activityhook.SecondaryActivity;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAPostGenScondarAct extends ActivityLifecycle {

    @Override
    public void generateSecondaryActivity(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            SecondaryActivity secondaryActivity) {
        // TODO Auto-generated method stub

        final Logger LOGGER = Logger.getLogger(AAPostGenScondarAct.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info(" accountDetailRecord - " + accountDetailRecord);
        LOGGER.info("arrangementContext  - " + arrangementContext);
        LOGGER.info("arrangementActivityRecord  - " + arrangementActivityRecord);
        LOGGER.info("masterActivityRecord  - " + masterActivityRecord);
        LOGGER.info("productPropertyRecord  - " + productPropertyRecord);
        LOGGER.info("productRecord  - " + productRecord);
        LOGGER.info("secondaryActivity  - " + secondaryActivity);
        
        LOGGER.info("arrangementActivityRecord.txncontractId  - " + arrangementActivityRecord.getTxnContractId().getValue());
        LOGGER.info("Masteract.txncontract  - " + masterActivityRecord.getTxnContractId().getValue());     
        
        String aarangementId = arrangementContext.getArrangementId();
        String aaAaActivityId = arrangementContext.getArrangementActivityId();
        String aaActStatus = arrangementContext.getActivityStatus();
        String efdate1 = arrangementContext.getActivityEffectiveDate().toString();
        String formatactEffDate = efdate1.substring(0, 4) + "-" + efdate1.substring(4, 6) + "-" + efdate1.substring(6);
        TDate efdate = new TDate(efdate1);
        
        // to avoid triggering of duplicate LENDING-UPDATE-ACCOUNT activities
        // During Apply payment activity child transaction activity
        if (!arrangementActivityRecord.getTxnContractId().getValue().isEmpty()
                && arrangementActivityRecord.getTxnContractId().getValue().startsWith("AAA")) {
            return;
        }
        
        //This is for FT/TT transaction status
        if(aaActStatus.equals("UNAUTH")) {
            return;
        }

        int arrCusSize = 0;

        // String masterAAid = masterActivityRecord.getMasterAaa().toString();

        Contract contract = new Contract(this);
        contract.setContractId(aarangementId);

        String aaStatus = arrangementRecord.getArrStatus().toString();

        String acctDetAgeStatus = accountDetailRecord.getArrAgeStatus().toString();

        try {
            if (aaStatus.equals("AUTH")) {

                Property p1 = new Property();
                p1.setPropertyName("ARDRINTEREST");
                
                int newposition = arrCusSize + 1;
                String position = String.valueOf(newposition);

                String flnName1 = "MARGIN.RATE:" + position + ":" + position;
                FieldPair f1 = new FieldPair();

                f1.setFieldName(flnName1);
                f1.setFieldValue("17");
                p1.addFieldPairs(f1);
                                               
                secondaryActivity.setArrangementId(aarangementId);
                secondaryActivity.setArrangementActivityId(aaAaActivityId);
                secondaryActivity.setArrangementEffectivedate(efdate);
                secondaryActivity.setNewActivity("ACCOUNTS-CHANGE-ARDRINTEREST");
                
                secondaryActivity.setProperties(p1, 0);
            }
        } catch (Exception e) {
        }
    }

}
