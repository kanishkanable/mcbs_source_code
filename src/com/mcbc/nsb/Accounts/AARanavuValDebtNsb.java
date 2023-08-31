package com.mcbc.nsb.Accounts;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.ebacctranavmaintnsb.EbAcctRanavMaintNsbRecord;
import com.temenos.t24.api.tables.ebacctranavmaintnsb.EbAcctRanavMaintNsbTable;
import com.temenos.t24.api.tables.ebacctranavmaintnsb.MonthStDateClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AARanavuValDebtNsb extends ActivityLifecycle {

    @Override
    public TValidationResponse validateRecord(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {
        // TODO Auto-generated method stub

        final Logger LOGGER = Logger.getLogger(AAPstRanavupdateNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("calling routine - ");

        String aaId = arrangementContext.getArrangementId();
        String accid = arrangementContext.getLinkedAccount();
        String actStatus = arrangementContext.getActivityStatus();
        String currency = arrangementActivityRecord.getCurrency().getValue();
        String activity = arrangementActivityRecord.getActivity().getValue();
        String effdate = arrangementActivityRecord.getEffectiveDate().getValue();
        int Orgtxnamt = Integer.parseInt(arrangementActivityRecord.getOrigTxnAmt().getValue());
        int OrigtxnamtLcy = Integer.parseInt(arrangementActivityRecord.getOrigTxnAmtLcy().getValue());
        String TxncontractId = arrangementActivityRecord.getTxnContractId().getValue();
        String product = arrangementActivityRecord.getProduct().getValue();
        String loccurr = "USD";
        Date dateRec = new Date(this);
        String todayDate = dateRec.getDates().getToday().getValue().toString();
        String YY = todayDate.substring(0, 4);
        // String MM = todayDate.substring(4, 2);
        String YYMM = todayDate.substring(0, 6);
        Boolean flag = false;
        int CurrMpendAmt = 0;
        int MaxAmtperMon = 750;

        DataAccess da = new DataAccess(this);

        MonthStDateClass Mstdateobj = new MonthStDateClass();

        if (actStatus.equalsIgnoreCase("UNAUTH")) {
            try {
                if (currency.equalsIgnoreCase(loccurr) && !String.valueOf(OrigtxnamtLcy).isEmpty()
                        && !TxncontractId.isEmpty()) {
                    TStructure acc_str = da.getRecord("EB.ACCT.RANAV.MAINT.NSB", aaId);
                    EbAcctRanavMaintNsbRecord Ranavrec = new EbAcctRanavMaintNsbRecord(acc_str);
                    List<MonthStDateClass> MonthData = Ranavrec.getMonthStDate();
                    for(MonthStDateClass monthdata : MonthData) {
                        String Mstartdate = monthdata.getMonthStDate().getValue();
                        if(Mstartdate.substring(0, 6).equalsIgnoreCase(YYMM)) {
                            flag = true;
                            String RemainAmt = monthdata.getOverPendAmt().getValue();
                            if(OrigtxnamtLcy > Integer.parseInt(RemainAmt)) {
                                throw new RuntimeException("error_msg: Ranavu product Exceeds the Max Amount per Month.");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (OrigtxnamtLcy > MaxAmtperMon) {
                    throw new RuntimeException("error_msg: Ranavu product Exceeds the Max Amount per Month.");
                }
            }
        }
        return super.validateRecord(accountDetailRecord, arrangementActivityRecord, arrangementContext,
                arrangementRecord, masterActivityRecord, productPropertyRecord, productRecord, record);
    }
}
