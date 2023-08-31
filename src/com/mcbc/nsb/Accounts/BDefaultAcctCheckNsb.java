package com.mcbc.nsb.Accounts;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TNumber;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aainterestaccruals.AaInterestAccrualsRecord;
import com.temenos.t24.api.records.aainterestaccruals.FromDateClass;
import com.temenos.t24.api.records.aainterestaccruals.PeriodStartClass;
import com.temenos.t24.api.records.acchargerequest.AcChargeRequestRecord;
import com.temenos.t24.api.records.acchargerequest.ChargeCodeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.aadefaultacctmaintnsb.AaDefaultAcctMaintNsbRecord;
import com.temenos.t24.api.tables.aadefaultacctmaintnsb.AaDefaultAcctMaintNsbTable;
import com.temenos.t24.api.tables.aasthreeacctmaintnsb.AaSthreeAcctMaintNsbRecord;
import com.temenos.t24.api.tables.aasthreeacctmaintnsb.AaSthreeAcctMaintNsbTable;
import com.temenos.t24.api.tables.aasthreeacctmaintnsb.MonthPeriodStartClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class BDefaultAcctCheckNsb extends ServiceLifecycle {

    DataAccess da = new DataAccess(this);
    List<String> recIds = null;
    Contract contract = new Contract(this);

    TNumber target_period = new TNumber(2);
    TNumber target_amt = new TNumber(125000);
    Boolean flag = false;
    TDate minbalDate = null;
    Date dateRec = new Date(this);
    // Date dateRec = new Date(this);
    String todayDate = dateRec.getDates().getToday().getValue().toString();
    String nextwDate = dateRec.getDates().getNextWorkingDay().getValue().toString();
    String lastwDate = dateRec.getDates().getLastWorkingDay().getValue();
    String mstartdate = todayDate.substring(0, 6);
    TDate sdate = new TDate(mstartdate.concat("01"));
    TDate edate = new TDate(todayDate);
    
    BigDecimal final_balamt = new BigDecimal(0.0);

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        // String producttype = "AR.SAVINGS.ACCOUNT";
         String producttype = "FIVEYEARPLUS.SAVINGS.NSB FIVEYEAR.SAVINGS.NSB NSBREALITY.SAVINGS.NSB SAVIBALA1.SAVINGS.NSB SAVIBALA2.SAVINGS.NSB";
         String selectStmt = "WITH PRODUCT.LINE EQ 'ACCOUNTS' AND ARR.STATUS EQ 'AUTH' AND PRODUCT EQ " + producttype;
                selectStmt = "AA20076TMQXQ";
        recIds = da.selectRecords("BNK", "AA.ARRANGEMENT", "", selectStmt);
        return recIds;
    }

    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // initialise
        String aaId = id;
        String cust_num = null;
        String acc_num = null;
        String arr_status = null;
        String Open_date = null;
        BigDecimal totalMonBal = new BigDecimal(0.0);
        BigDecimal totalCrBal = new BigDecimal(0.0);    
        BigDecimal totalDrBal = new BigDecimal(0.0);
        BigDecimal Totbalpermon = new BigDecimal(0.0);

        TDate prev_month;
        contract.setContractId(aaId);

        // db read
        try {
            TStructure aa_arr = da.getRecord("AA.ARRANGEMENT", aaId);
            AaArrangementRecord aaarrrec = new AaArrangementRecord(aa_arr);
            cust_num = aaarrrec.getCustomer(0).getCustomer().getValue();
            acc_num = aaarrrec.getLinkedAppl(0).getLinkedApplId().getValue();
            arr_status = aaarrrec.getArrStatus().getValue();
            Open_date = aaarrrec.getStartDate().getValue();
        } catch (Exception e) {
            System.out.println("Database read failed");
        }
        if (!arr_status.equals("AUTH")) {
            return;
        }
        List<BalanceMovement> Contractbal = contract.getBalanceMovementsForPeriod("CURBALANCE", "VALUE", sdate, edate);

        if (Contractbal.size() > 0 && Contractbal != null) {
            for (BalanceMovement contractbal : Contractbal) {
                totalMonBal = totalMonBal.add(new BigDecimal(contractbal.getBalance().toString()));
                if (!contractbal.getCreditMovement().isEmpty())
                    totalCrBal = totalCrBal.add(new BigDecimal(contractbal.getCreditMovement()));
                if (!contractbal.getDebitMovement().isEmpty())
                    totalDrBal = totalDrBal.add(new BigDecimal(contractbal.getDebitMovement()));
            }
        }
        Totbalpermon = totalCrBal.subtract(totalDrBal.abs());
        if (target_amt.intValue() > Totbalpermon.intValue())
            flag = true;

        if (flag) {
            try {
                TStructure acc_str = da.getRecord("AA.DEFAULT.ACCT.MAINT.NSB", aaId);
                AaDefaultAcctMaintNsbRecord Defaultacctrecord = new AaDefaultAcctMaintNsbRecord(acc_str);
                AaDefaultAcctMaintNsbTable Defaultaccttable = new AaDefaultAcctMaintNsbTable(this);
                if (Defaultacctrecord.getAccountNumber().getValue().isEmpty())
                    Defaultacctrecord.setAccountNumber(acc_num);
                if (Defaultacctrecord.getCustomerNumber().getValue().isEmpty())
                    Defaultacctrecord.setCustomerNumber(cust_num);
                if(Defaultacctrecord.getAccountStatus().equals("PROCESSED"))
                    return;
                String Month_per_end = Defaultacctrecord.getMonthPeriodStart(0).getMonthPeriodEnd().getValue();
                List<com.temenos.t24.api.tables.aadefaultacctmaintnsb.MonthPeriodStartClass> Month_period_set = Defaultacctrecord
                        .getMonthPeriodStart();

                if (!Month_per_end.isEmpty()) {
                    if (Month_per_end.substring(0, 6).equals(mstartdate))
                        return;
                    prev_month = new TDate(Month_per_end);
                    TNumber Diff = dateRec.getMonthDifference(sdate, prev_month);
                    if (Diff.intValue() == 0) {
                        Defaultacctrecord.setAccountStatus("PROCESSED");
                        int cnt = Month_period_set.size();
                        com.temenos.t24.api.tables.aadefaultacctmaintnsb.MonthPeriodStartClass month_period_rec = new com.temenos.t24.api.tables.aadefaultacctmaintnsb.MonthPeriodStartClass();
                        month_period_rec.setMonthPeriodStart(sdate.toString());
                        month_period_rec.setMonthPeriodEnd(edate.toString());
                        month_period_rec.setMonthTotCr(totalCrBal.toString());
                        month_period_rec.setMonthTotDr(totalDrBal.toString());
                        month_period_rec.setMonthEndBal(totalMonBal.toString());
                        Defaultacctrecord.setMonthPeriodStart(month_period_rec, cnt);
                        Defaultaccttable.write(aaId, Defaultacctrecord);

                        // If value is 0 then two consequetive months are
                        // defaulted. hence convert this account to normal
                        // saving.
                        // Develop logic to post OFS
                        String aaId1 =  aaId.concat("-CRINTEREST");
                        TStructure aa_arr = da.getRecord("AA.INTEREST.ACCRUALS", aaId1);
                        final_balamt = Findnewintamount(aa_arr);
                        if(final_balamt.signum() != 0) {
                            AcChargeRequestRecord chargreqrec = new AcChargeRequestRecord();
                            chargreqrec.setRequestType("BOOK");
                            chargreqrec.setDebitAccount(acc_num);
                            ChargeCodeClass chrgcode = new ChargeCodeClass();
                            chrgcode.setChargeCode("CORRBKCHG");
                            chargreqrec.setChargeCode(chrgcode, 0);
                            chargreqrec.setExtraDetails("Reverse capitalise interest", 0);
                            chargreqrec.setStatus("PAID");
                            records.add(chargreqrec.toStructure());

                            TransactionData td = new TransactionData();
                            td.setFunction("INPUT");
                            td.setNumberOfAuthoriser("0");
                            // td.setUserName("INPUTT");
                            td.setSourceId("BULK.OFS");
                            // td.setTransactionId(AgeRecordId);
                            td.setVersionId("AC.CHARGE.REQUEST,OFS.NSB");
                            transactionData.add(td);
                        }
                    } else {
                        // Since it is not following two consequetive, removing
                        // records from concat table.
                        Defaultacctrecord.setAccountStatus("CLEARED");
                        Defaultaccttable.delete(aaId);
                    }
                }

            } catch (Exception e) {
                System.out.println("Creating a new record");
                AaDefaultAcctMaintNsbRecord Defaultacctrecord = new AaDefaultAcctMaintNsbRecord();
                Defaultacctrecord.setAccountNumber(acc_num);
                Defaultacctrecord.setCustomerNumber(cust_num);
                Defaultacctrecord.setOpeningDate(Open_date);
                Defaultacctrecord.setAccountStatus("NEW");
                com.temenos.t24.api.tables.aadefaultacctmaintnsb.MonthPeriodStartClass month_period_rec = new com.temenos.t24.api.tables.aadefaultacctmaintnsb.MonthPeriodStartClass();
                month_period_rec.setMonthPeriodStart(sdate.toString());
                month_period_rec.setMonthPeriodEnd(edate.toString());
                month_period_rec.setMonthTotCr(totalCrBal.toString());
                month_period_rec.setMonthTotDr(totalDrBal.toString());
                month_period_rec.setMonthEndBal(totalMonBal.toString());
                Defaultacctrecord.setMonthPeriodStart(month_period_rec, 0);
                AaDefaultAcctMaintNsbTable Defaultaccttable = new AaDefaultAcctMaintNsbTable(this);
                try {
                    Defaultaccttable.write(aaId, Defaultacctrecord);
                } catch (Exception e1) {
                    System.out.println("Write failed");
                }
            }
        }
    }
    
    public static BigDecimal Findnewintamount(TStructure aa_arr) {

        BigDecimal totalexistacramt = new BigDecimal(0.0);
        BigDecimal totalnewacramt = new BigDecimal(0.0);

        BigDecimal period_totaccramt1 = new BigDecimal(0.0);
        BigDecimal period_totdueamt1 = new BigDecimal(0.0);

        BigDecimal tempnewaccramt;
        BigDecimal final_balamt = new BigDecimal(0.0);
        BigDecimal calc_basis = new BigDecimal("36000");

        Boolean flag1 = false;
        String fromdate_balance;
        String fromdate_days;
        String fromdate_basis;
        String fromdate_rate;
        String new_rate = "0.5";
        String fromdate_accrualamt;
        String fromdate;
        String todate;

        String period_totaccramt;
        String period_totdueamt;
        
        AaInterestAccrualsRecord AaAccrrec = new AaInterestAccrualsRecord(aa_arr);
        List<FromDateClass> AaFromDate = AaAccrrec.getFromDate();
        List<PeriodStartClass> AaPeriodStart = AaAccrrec.getPeriodStart();        

        for (PeriodStartClass aaperiodstart : AaPeriodStart) {
            period_totaccramt = aaperiodstart.getTotAccrAmt().getValue();
            period_totdueamt = aaperiodstart.getTotDueAmt().getValue();
            if (!(period_totaccramt.isEmpty() && period_totdueamt.isEmpty())) {
                if (!(period_totaccramt.equalsIgnoreCase("0")) && !(period_totdueamt.equalsIgnoreCase("0"))
                        && !period_totdueamt.isEmpty()) {
                    // Interest amount already capitalised. sum balance                    
                    period_totaccramt1 = period_totaccramt1.add(new BigDecimal(period_totaccramt.toString()));
                    period_totdueamt1 = period_totdueamt1.add(new BigDecimal(period_totdueamt.toString()));
                    flag1 = true;
                }
                if (!(period_totaccramt.equalsIgnoreCase("0"))
                        && ((period_totdueamt.equalsIgnoreCase("0")) || (period_totdueamt.isEmpty()))) {
                    // Interest accrued, not yet capitalise, no need to reverse from acct.                    
                    period_totaccramt1 = period_totaccramt1.add(new BigDecimal(period_totaccramt.toString()));
                    // period_totdueamt1 = period_totdueamt1.add(new
                    // BigDecimal(period_totdueamt.toString()));
                    flag1 = true;
                }
            }
        }

        if (flag1) {
            for (int i = AaFromDate.size() - 1; i >= 0; i--) {
                if (AaFromDate.get(i).getFromDate().getValue().isEmpty())
                    continue;
                tempnewaccramt = new BigDecimal(1);
                fromdate = AaFromDate.get(i).getFromDate().getValue();
                todate = AaFromDate.get(i).getToDate().getValue();
                fromdate_basis = AaFromDate.get(i).getBasis().getValue();
                fromdate_days = AaFromDate.get(i).getDays().getValue();
                fromdate_rate = AaFromDate.get(i).getRate(0).getValue();
                fromdate_balance = AaFromDate.get(i).getBalance(0).getValue();
                fromdate_accrualamt = AaFromDate.get(i).getAccrualAmt(0).getAccrualAmt().getValue();

                if (!fromdate_accrualamt.isEmpty() && !fromdate_accrualamt.equals("0.00")) {
                    totalexistacramt = totalexistacramt.add(new BigDecimal(fromdate_accrualamt.toString()));
                    if (!period_totdueamt1.equals(totalexistacramt)) {
                        tempnewaccramt = tempnewaccramt.multiply(new BigDecimal(fromdate_balance));
                        tempnewaccramt = tempnewaccramt.multiply(new BigDecimal(new_rate));
                        tempnewaccramt = tempnewaccramt.multiply(new BigDecimal(fromdate_days));
                        tempnewaccramt = tempnewaccramt.divide(calc_basis, new MathContext(2));

                        // tempnewaccramt = (Integer.parseInt(fromdate_balance)
                        // * Integer.parseInt(fromdate_rate)
                        // * Integer.parseInt(fromdate_days)) / (100 * 360);
                        totalnewacramt = totalnewacramt.add(tempnewaccramt);
                    } else {
                        break;
                    }
                }
            }

            // period_totaccramt1 & totalexistacramt should be equal.
            // period_totdueamt1 - it contains total capitalised balance.
            final_balamt = period_totdueamt1.subtract(totalnewacramt);

        }        
        return final_balamt;        
    }
}
