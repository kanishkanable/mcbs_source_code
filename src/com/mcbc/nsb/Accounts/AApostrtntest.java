package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.TransactionData;
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
public class AApostrtntest extends ActivityLifecycle {

    @Override
    public void postCoreTableUpdate(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            List<TransactionData> transactionData, List<TStructure> transactionRecord) {
        // TODO Auto-generated method stub
     
        final Logger LOGGER = Logger.getLogger(AApostrtntest.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("calling Post routine - ");
    }
}
