package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TValidationResponse;
import com.temenos.api.TStructure;
import com.temenos.api.TString;

import java.util.List;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.Period;

import com.temenos.api.LocalRefClass;
import com.temenos.api.TBoolean;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.TransactionData;
import com.temenos.t24.api.records.aaaccountdetails.*;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.RiskAssetTypeClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.ebriskpepparamnsb.CashVolumCodeClass;
import com.temenos.t24.api.tables.ebriskpepparamnsb.CustRelationCodeClass;
import com.temenos.t24.api.tables.ebriskpepparamnsb.EbRiskPepParamNsbRecord;
import com.temenos.t24.api.tables.ebriskpepparamnsb.ModeTxnCodeClass;
import com.temenos.t24.api.tables.ebriskpepparamnsb.PurposeCodeClass;
import com.temenos.t24.api.tables.ebriskpepparamnsb.SourceFundCodeClass;
import com.temenos.tafj.api.client.impl.T24Context;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AAPstRiskPepNsb extends ActivityLifecycle {

    public static String purpose_rank;
    public static String purpose_weight;
    public static String source_fund_rank;
    public static String source_fund_weight;
    public static String ant_volume_rank;
    public static String ant_volume_weight;
    public static String mode_txn_rank;
    public static String mode_txn_weight;
    public static String cust_rel_rank;
    public static String cust_rel_weight;
    public static int cust_open_years;

    public static int result;
    public static double risk_value;
    public static String risk_category;

    private String EcpRiskLevel = null;
    private String EcpRiskLevelHigh = null;
    private String EcpRiskLevelMedium = null;
    private String EcpRiskLevelLow = null;
    
    @Override
    public void postCoreTableUpdate(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record,
            List<TransactionData> transactionData, List<TStructure> transactionRecord) {

        final Logger LOGGER = Logger.getLogger(AAPstRiskPepNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Routine calling - AAPstRiskPepNsb");
        String customerid = arrangementRecord.getCustomer(0).getCustomer().getValue();
        String arrangementid = arrangementContext.getArrangementId();
        String aaActStatus = arrangementContext.getActivityStatus();

        Contract contract = new Contract(this);
        contract.setContractId(arrangementid);

        if (!aaActStatus.equals("AUTH")) {
            return;
        }

        TStructure arrAccountRec1 = contract.getConditionForProperty("BALANCE");
        TStructure arrAccountRec = contract.getAccountCondition("BALANCE").toStructure();

        AaPrdDesAccountRecord aaarrAccountRec1 = new AaPrdDesAccountRecord(arrAccountRec1);
        AaPrdDesAccountRecord aaarrAccountRec = new AaPrdDesAccountRecord(arrAccountRec);

        String purpose = aaarrAccountRec.getLocalRefField("L.PURPOSE").getValue().toString();
        String source_fund = aaarrAccountRec.getLocalRefField("L.SOURCE.FUND").getValue().toString();
        String ant_volume = aaarrAccountRec.getLocalRefField("L.ANT.VOLUME").getValue().toString();
        String mode_txn = aaarrAccountRec.getLocalRefField("L.EXP.MODE.TRANSACT").getValue().toString();

        DataAccess da = new DataAccess(this);
        Date SystemDate = new Date(this);
        String TodayDate = SystemDate.getDates().getToday().getValue();
        String riskparamid = "SYSTEM";
        TStructure riskparamrec;
        EbRiskPepParamNsbRecord riskpeprec;
        TStructure customerrec;
        CustomerRecord custrec;

        try {
            customerrec = da.getRecord("CUSTOMER", customerid);
            custrec = new CustomerRecord(customerrec);
            String cust_open_date = custrec.getLocalRefField("L.CREATION.DATE").getValue().toString();
            if (cust_open_date != null) {
                int yy = Integer.parseInt(cust_open_date.substring(0, 4));
                int mm = Integer.parseInt(cust_open_date.substring(4, 6));
                int dd = Integer.parseInt(cust_open_date.substring(6, 8));
                LocalDate pdate = LocalDate.of(yy, mm, dd);
                LocalDate now = LocalDate.now();
                Period diff = Period.between(pdate, now);
                cust_open_years = diff.getYears();
            } else {
                cust_open_years = 0;
            }

        } catch (Exception e) {
            System.out.println("Missing CUSTOMER -" + customerid);
        }

        try {
            riskparamrec = da.getRecord("EB.RISK.PEP.PARAM.NSB", riskparamid);
            riskpeprec = new EbRiskPepParamNsbRecord(riskparamrec);

            List<PurposeCodeClass> Param_purpose = riskpeprec.getPurposeCode();
            List<SourceFundCodeClass> Param_source_fund = riskpeprec.getSourceFundCode();
            List<CashVolumCodeClass> Param_volume = riskpeprec.getCashVolumCode();
            List<ModeTxnCodeClass> Param_mode_txn = riskpeprec.getModeTxnCode();
            List<CustRelationCodeClass> Param_cust_rel = riskpeprec.getCustRelationCode();

            if (!purpose.isEmpty() && purpose != null) {
                for (PurposeCodeClass param_purpose : Param_purpose) {
                    String purpose_dec = param_purpose.getPurposeDesc().getValue().toString();
                    if (purpose_dec.contains(purpose)) {
                        purpose_rank = param_purpose.getPurposeRank().getValue().toString();
                        purpose_weight = riskpeprec.getPrWeightage().getValue().toString();
                    }
                }
            } else {
                purpose_rank = "0";
                purpose_weight = "0";
            }

            if (!source_fund.isEmpty() && source_fund != null) {
                for (SourceFundCodeClass param_source_fund : Param_source_fund) {
                    String sourcefund_dec = param_source_fund.getSourceFundDesc().getValue().toString();
                    if (sourcefund_dec.contains(source_fund)) {
                        source_fund_rank = param_source_fund.getSourceFundRank().getValue().toString();
                        source_fund_weight = riskpeprec.getSfWeightage().getValue().toString();
                    }
                }
            } else {
                source_fund_rank = "0";
                source_fund_weight = "0";
            }

            if (!ant_volume.isEmpty() && ant_volume != null) {
                for (CashVolumCodeClass param_volume : Param_volume) {
                    String volume_range = param_volume.getCashVolumRange().getValue().toString();
                    if (volume_range.contains(ant_volume)) {
                        ant_volume_rank = param_volume.getCashVolumRank().getValue().toString();
                        ant_volume_weight = riskpeprec.getCvWeightage().getValue().toString();
                    }
                }
            } else {
                ant_volume_rank = "0";
                ant_volume_weight = "0";
            }

            if (!mode_txn.isEmpty() && mode_txn != null) {
                for (ModeTxnCodeClass param_mode_txn : Param_mode_txn) {
                    String modetxn_dec = param_mode_txn.getModeTxnDesc().getValue().toString();
                    if (modetxn_dec.contains(mode_txn)) {
                        mode_txn_rank = param_mode_txn.getModeTxnRank().getValue().toString();
                        mode_txn_weight = riskpeprec.getMtWeightage().getValue().toString();
                    }
                }
            } else {
                mode_txn_rank = "0";
                mode_txn_weight = "0";
            }

            cust_rel_rank = customerrelation(Param_cust_rel, cust_open_years);
            if (cust_rel_rank != null)
                cust_rel_weight = riskpeprec.getCrWeightage().getValue().toString();

        } catch (Exception e) {
            System.out.println("Missing parameter EB.RISK.PEP.PARAM.NSB>SYSTEM");
        }

        result = Integer.parseInt(purpose_rank) * Integer.parseInt(purpose_weight);
        result = result + Integer.parseInt(source_fund_rank) * Integer.parseInt(source_fund_weight);
        result = result + Integer.parseInt(ant_volume_rank) * Integer.parseInt(ant_volume_weight);
        result = result + Integer.parseInt(mode_txn_rank) * Integer.parseInt(mode_txn_weight);
        result = result + Integer.parseInt(cust_rel_rank) * Integer.parseInt(cust_rel_weight);
        LOGGER.info("result - " + result);
        if (result > 0){
            risk_value = (result / 100);
        } else {
            risk_value = 0;
        }        

        if (risk_value < 3.00 && risk_value >= 2.34)
            // risk_category = 'H';
            risk_category = "1";
        if (risk_value < 2.33 && risk_value >= 1.67)
            // risk_category = 'M';
            risk_category = "2";
        if (risk_value < 1.66 && risk_value >= 1.00)
            // risk_category = 'L';
            risk_category = "3";
        if (risk_value < 1.00)
            // risk_category = 'E';
            risk_category = "4";
        LOGGER.info("risk_category - " + risk_category);
        // TStructure updcustrec = da.getRecord("CUSTOMER", customerid);
        CustomerRecord updcusrec = new CustomerRecord(this);
        
        List<RiskAssetTypeClass> RiskAssetType = updcusrec.getRiskAssetType();        
        for (RiskAssetTypeClass riskAssetClass : RiskAssetType) {            
            if (!riskAssetClass.getRiskLevel().getValue().isEmpty()) {
                LOGGER.info("Risk level set already overwriting");
            }
        }
        
        RiskAssetTypeClass RiskAssetType1 = new RiskAssetTypeClass();
        RiskAssetType1.setRiskLevel(risk_category);
        updcusrec.addRiskAssetType(RiskAssetType1);
                
//        String NextDate = "20200101";
//        String NextDate = RiskLevelNsb.GetAutoNextKycDate(CustomerRec, ParamConfig);
//        updcusrec.setAutoNextKycReviewDate(NextDate);
        
        TransactionData sTxnData = new TransactionData();
        sTxnData.setVersionId("CUSTOMER,JAVA");
        sTxnData.setFunction("INPUT");
        sTxnData.setNumberOfAuthoriser("0");
        sTxnData.setUserName("INPUTTER");
        sTxnData.setTransactionId(customerid);
        sTxnData.setSourceId("BULK.OFS");
        transactionData.add(sTxnData);
//        updcusrec.setCalcRiskClass("PEPH");

        transactionRecord.add(updcusrec.toStructure());

    }

    public static String customerrelation(List<CustRelationCodeClass> Param_cust_rel, int val) {
        String cust_rel_rank = "";
        int[] arr1;
        for (CustRelationCodeClass param_cust_rel : Param_cust_rel) {
            String ar = param_cust_rel.getCustRelationDesc().getValue().toString();
            if (ar.contains(">")) {
                String[] cusrelval = ar.split("\\>");
                for (String ss : cusrelval) {
                    if (ss != null && !ss.isEmpty()) {
                        if (val > Integer.parseInt(ss)) {
                            cust_rel_rank = param_cust_rel.getCustRelationRank().getValue().toString();
                        }
                    }
                }
            }
            if (ar.contains("<")) {
                String[] cusrelval = ar.split("\\<");
                for (String ss : cusrelval) {
                    if (ss != null && !ss.isEmpty()) {
                        if (val < Integer.parseInt(ss)) {
                            cust_rel_rank = param_cust_rel.getCustRelationRank().getValue().toString();
                        }
                    }
                }
            }
            if (ar.contains("-")) {
                String[] cusrelval = ar.split("\\-");
                int i = 0;
                arr1 = new int[5];
                for (String ss : cusrelval) {
                    if (ss != null && !ss.isEmpty()) {
                        arr1[i] = Integer.parseInt(ss);
                        i++;
                    }
                }
                if (i > 0) {
                    if (val >= arr1[0] && val <= arr1[1]) {
                        cust_rel_rank = param_cust_rel.getCustRelationRank().getValue().toString();
                    }
                }
            }

            if (ar.contains("0")) {
                if (val == 0)
                    cust_rel_rank = param_cust_rel.getCustRelationRank().getValue().toString();
            }
        }
        return cust_rel_rank;
    }
    
    private String CheckNextDate(CustomerRecord CustomerRec, Boolean RiskLevelHigh, Boolean RiskLevelMedium,
            Boolean RiskLevelLow) {
        String LastKycReviewDate = CustomerRec.getLastKycReviewDate().getValue();
        LocalDate StartDate = LocalDate.of(Integer.parseInt(LastKycReviewDate.substring(0, 4)),
                Integer.parseInt(LastKycReviewDate.substring(4, 6)),
                Integer.parseInt(LastKycReviewDate.substring(6, 8)));
        String NextDate = null;

        Boolean check = false;
        if (!check && RiskLevelHigh) {
            NextDate = StartDate.plusYears(Integer.parseInt(EcpRiskLevelHigh)).toString().replace("-", "");
            check = true;
        }
        if (!check && !RiskLevelHigh && RiskLevelMedium) {
            NextDate = StartDate.plusYears(Integer.parseInt(EcpRiskLevelMedium)).toString().replace("-", "");
            check = true;
        }
        if (!check && !RiskLevelHigh && !RiskLevelMedium && RiskLevelLow) {
            NextDate = StartDate.plusYears(Integer.parseInt(EcpRiskLevelLow)).toString().replace("-", "");
            check = true;
        }

        return NextDate;
    }    
}
