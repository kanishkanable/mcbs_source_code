package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TBoolean;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.LookupData;
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
public class AAPstRanavupdateNsb extends ActivityLifecycle {

    @Override
    public TBoolean updateLookupTable(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            List<LookupData> lookupDataList) {

        final Logger LOGGER = Logger.getLogger(AAPstRanavupdateNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("calling routine - ");

        String customerid = arrangementRecord.getCustomer(0).getCustomer().getValue();
        String aaId = arrangementContext.getArrangementId();
        String accid = arrangementContext.getLinkedAccount();
        String actStatus = arrangementContext.getActivityStatus();
        String activity = arrangementActivityRecord.getActivity().getValue();
        String currency = arrangementActivityRecord.getCurrency().getValue();
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

        // if(product.equalsIgnoreCase("")) {
        // return;
        // }

        DataAccess da = new DataAccess(this);
        MonthStDateClass Mstdateobj = new MonthStDateClass();

        if (actStatus.equalsIgnoreCase("AUTH")) {
            try {

                if (currency.equalsIgnoreCase(loccurr) && !String.valueOf(OrigtxnamtLcy).isEmpty() && !TxncontractId.isEmpty()) {
                    TStructure acc_str = da.getRecord("EB.ACCT.RANAV.MAINT.NSB", aaId);
                    EbAcctRanavMaintNsbRecord Ranavrec = new EbAcctRanavMaintNsbRecord(acc_str);
                    EbAcctRanavMaintNsbTable RanavTable = new EbAcctRanavMaintNsbTable(this);
                    List<MonthStDateClass> MonthData = Ranavrec.getMonthStDate();
                    MaxAmtperMon = Integer.parseInt(Ranavrec.getMaxAmtDrPerMonth().getValue());
                    int cnt = MonthData.size();
                    for (MonthStDateClass monthdata : MonthData) {
                        String MonthStDate = monthdata.getMonthStDate().getValue();
                        String MonthEnDate = monthdata.getMonthEnDate().getValue();
                        if (MonthEnDate.substring(0, 6).equalsIgnoreCase(YYMM)) {
                            flag = true;
                            int Totdr = Integer.parseInt(monthdata.getTotalDr().getValue());
                            CurrMpendAmt = Integer.parseInt(monthdata.getCurPendAmt().getValue());
                            int PreMpendAmt = Integer.parseInt(monthdata.getPrevPendAmt().getValue());
                            int OverrPendAmt = Integer.parseInt(monthdata.getOverPendAmt().getValue());

                            if (String.valueOf(Totdr).isEmpty())
                                Totdr = 0;
                            if (String.valueOf(CurrMpendAmt).isEmpty())
                                CurrMpendAmt = 0;
                            if (String.valueOf(PreMpendAmt).isEmpty())
                                PreMpendAmt = 0;
                            if (String.valueOf(OverrPendAmt).isEmpty())
                                OverrPendAmt = 0;
                            if (!String.valueOf(Totdr).isEmpty()) {
                                Totdr = +Totdr + OrigtxnamtLcy;
                                CurrMpendAmt = CurrMpendAmt - OrigtxnamtLcy;
                                OverrPendAmt = OverrPendAmt - OrigtxnamtLcy;
                                Ranavrec.getMonthStDate(cnt-1).getTotalDr().setValue(String.valueOf(Totdr));
                                Ranavrec.getMonthStDate(cnt-1).getCurPendAmt().setValue(String.valueOf(CurrMpendAmt));
                                Ranavrec.getMonthStDate(cnt-1).getOverPendAmt().setValue(String.valueOf(OverrPendAmt));

                                RanavTable.write(aaId, Ranavrec);
                                break;
                            }
                        }
                    }

                    if (flag == false) {
                        // Write new multi value set in the table for current
                        // month
                        /*
                        Ranavrec.setMaxAmtDrPerMonth(String.valueOf(MaxAmtperMon));
                        Ranavrec.getMonthStDate(cnt + 1).getMonthStDate().setValue(YYMM.concat("01"));
                        Ranavrec.getMonthStDate(cnt + 1).getMonthEnDate().setValue(YYMM.concat("31"));
                        Ranavrec.getMonthStDate(cnt + 1).getTotalDr().setValue(String.valueOf(OrigtxnamtLcy));

                        CurrMpendAmt = MaxAmtperMon - OrigtxnamtLcy;
                        Ranavrec.getMonthStDate(cnt + 1).getCurPendAmt().setValue(String.valueOf(CurrMpendAmt));

                        String prevOpendamt = MonthData.get(cnt - 1).getOverPendAmt().getValue();
                        String Totdr = MonthData.get(cnt - 1).getTotalDr().getValue();
                        int OverpendAmt = Integer.parseInt(prevOpendamt) + Integer.parseInt(Totdr);
                        Ranavrec.getMonthStDate(cnt).getOverPendAmt().setValue(String.valueOf(OverpendAmt));
                        */
                        
                        CurrMpendAmt = MaxAmtperMon - OrigtxnamtLcy;
                        Mstdateobj.setMonthStDate(YYMM.concat("01"));
                        Mstdateobj.setMonthEnDate(YYMM.concat("31"));
                        Mstdateobj.setCurPendAmt(String.valueOf(CurrMpendAmt));
                        Mstdateobj.setTotalDr(String.valueOf(OrigtxnamtLcy));
                        Mstdateobj.setPrevPendAmt(MonthData.get(cnt - 1).getOverPendAmt().getValue());
                        String prevOpendamt1 = MonthData.get(cnt - 1).getOverPendAmt().getValue();
                        String Totdr1 = MonthData.get(cnt - 1).getTotalDr().getValue();
                        int OverpendAmt1 = CurrMpendAmt + Integer.parseInt(prevOpendamt1);
                        Mstdateobj.setOverPendAmt(String.valueOf(OverpendAmt1));                        
                                                
                        Ranavrec.setMonthStDate(Mstdateobj, cnt);
                        RanavTable.write(aaId, Ranavrec);
                    }
                }
            } catch (Exception e) {
                System.out.println("Creating a new record");
                EbAcctRanavMaintNsbRecord Ranavrec = new EbAcctRanavMaintNsbRecord();
                EbAcctRanavMaintNsbTable RanavTable = new EbAcctRanavMaintNsbTable(this);
                try {
                    TStructure acc_str = da.getRecord("EB.ACCT.RANAV.MAINT.NSB", aaId);
                } catch (Exception e2) {
                    // Ensure that New record creating only for because of
                    // missing record in that table.
                    // Sometime due to other exception also catch section can
                    // execute.
                    Ranavrec.setAccountNumber(accid);
                    Ranavrec.setCustomerNumber(customerid);
                    Ranavrec.setMaxAmtDrPerMonth(String.valueOf(MaxAmtperMon));
                    Mstdateobj.setMonthStDate(YYMM.concat("01"));
                    Mstdateobj.setMonthEnDate(YYMM.concat("31"));
                    CurrMpendAmt = MaxAmtperMon - OrigtxnamtLcy;
                    Mstdateobj.setCurPendAmt(String.valueOf(CurrMpendAmt));
                    Mstdateobj.setTotalDr(String.valueOf(OrigtxnamtLcy));
                    Mstdateobj.setPrevPendAmt("0");
                    Mstdateobj.setOverPendAmt(String.valueOf(CurrMpendAmt));
                    Ranavrec.setMonthStDate(Mstdateobj, 0);
                    try {
                        RanavTable.write(aaId, Ranavrec);
                    } catch (Exception e3) {
                        System.out.println("Write failed");
                    }
                }
            }
        }

        if (actStatus.equalsIgnoreCase("REV")) {
            // Write logic if TXN reverse happens
        }
        // TODO Auto-generated method stub
        return null;
    }
}
