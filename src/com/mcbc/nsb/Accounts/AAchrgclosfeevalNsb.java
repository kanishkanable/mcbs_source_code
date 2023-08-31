package com.mcbc.nsb.Accounts;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TNumber;
import com.temenos.api.TString;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.hook.arrangement.Calculation;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddescharge.AaPrdDesChargeRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAchrgclosfeevalNsb extends Calculation {

    @Override
    public void calculateAdjustedCharge(String arrangementId, String arrangementCcy, TDate adjustEffectiveDate,
            String adjustChargeProperty, String chargeType, AaPrdDesChargeRecord chargePropertyRecord,
            TNumber adjustBaseAmount, String adjustPeriodStartDate, String adjustPeriodEndDate, String sourceActivity,
            String chargeAmount, String activityId, TNumber adjustChargeAmount, TNumber newChargeAmount,
            TString adjustReason, AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, TStructure productPropertyRecord,
            AaProductCatalogRecord productRecord, TStructure record, AaArrangementActivityRecord masterActivityRecord) {
        // TODO Auto-generated method stub
        
        final Logger LOGGER = Logger.getLogger(AAchrgclosfeevalNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String arrangementId - " + arrangementId);
        LOGGER.info("String arrangementCcy - " + arrangementCcy);
        LOGGER.info("TDate adjustEffectiveDate - " + adjustEffectiveDate);
        LOGGER.info("String adjustChargeProperty - " + adjustChargeProperty);
        LOGGER.info("String chargeType - " + chargeType);
        LOGGER.info("TNumber adjustBaseAmount - " + adjustBaseAmount);
        LOGGER.info("String adjustPeriodStartDate - " + adjustPeriodStartDate);
        LOGGER.info("String adjustPeriodEndDate - " + adjustPeriodEndDate);
        LOGGER.info("String sourceActivity - " + sourceActivity);
        LOGGER.info("String chargeAmount - " + chargeAmount);
        LOGGER.info("String activityId - " + activityId);
        LOGGER.info("TNumber adjustChargeAmount - " + adjustChargeAmount);
        LOGGER.info("TNumber newChargeAmount - " + newChargeAmount);
        LOGGER.info("String adjustReason - " + adjustReason);
        LOGGER.info("ArrangementContext arrangementContext - " + arrangementContext);
        LOGGER.info("AaAccountDetailsRecord accountDetailRecord - " + accountDetailRecord);
        LOGGER.info("AaArrangementActivityRecord arrangementActivityRecord - " + arrangementActivityRecord);
        LOGGER.info("AaArrangementRecord arrangementRecord - " + arrangementRecord);
        LOGGER.info("TStructure record - " + record);
        LOGGER.info("AaProductCatalogRecord productRecord - " + productRecord);
        
        List<TField> acc_contractdate = accountDetailRecord.getContractDate();
        String acct_open_date = acc_contractdate.get(0).getValue();
        if (!acct_open_date.isEmpty()) {
            int yy = Integer.parseInt(acct_open_date.substring(0, 4));
            int mm = Integer.parseInt(acct_open_date.substring(4, 6));
            int dd = Integer.parseInt(acct_open_date.substring(6, 8));
            LocalDate pdate = LocalDate.of(yy, mm, dd);
            LOGGER.info("LocalDate pdate - " + pdate);
            Date SystemDate = new Date(this);
            String TodayDate = SystemDate.getDates().getToday().getValue();
            int today_yy = Integer.parseInt(TodayDate.substring(0, 4));
            int today_mm = Integer.parseInt(TodayDate.substring(4, 6));
            LocalDate now = LocalDate.of(today_yy, today_mm, Integer.parseInt(TodayDate.substring(6, 8)));
            LOGGER.info("LocalDate now - " + now);
            
//            LocalDate now = LocalDate.now();
            Period diff = Period.between(pdate, now);
            int acct_open_years = diff.getYears();
            int acct_open_month = diff.getMonths();
            LOGGER.info("acct_open_years - " + acct_open_years);
            LOGGER.info("acct_open_month - " + acct_open_month);
            if (acct_open_years == 0 && acct_open_month < 6 ){
                LOGGER.info("account less than 6month");
                newChargeAmount.set(chargeAmount);
            }                
        } 
    }    
}
