package com.mcbc.nsb.CustomerCommonUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.temenos.api.TField;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.RiskAssetTypeClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 *         Pass the below code in the main routine for
 *         "GetRisklevelNextKycdateNsb" uGetParamValueNsb Config = new
 *         uGetParamValueNsb(); Config.AddParam("CUSTOMER", new String[] {
 *         "RISK.LEVEL.HIGH" }); Map<String, Map<String, List<TField>>>
 *         ParamConfig = Config.GetParamValue(DataObj);
 * 
 */
public class CustomerRiskLevelNsb {

    private String EcpRiskLevel = null;
    private String EcpRiskLevelHigh = null;
    private String EcpRiskLevelMedium = null;
    private String EcpRiskLevelLow = null;

    public void UpdateRisklevelNextKycdateNsb(String PepStatusValue, CustomerRecord CustomerRec, DataAccess DataObj,
            Map<String, Map<String, List<TField>>> ParamConfig) {
        setParamValues(ParamConfig);
        String PepStatSubstring = PepStatusValue.substring(0, 3);

        if (PepStatSubstring.equals(EcpRiskLevel)) {
            SetRiskLevelNsb(CustomerRec);
        }
    }

    private void SetRiskLevelNsb(CustomerRecord CustomerRec) {
        Boolean RiskLevelBool = false;
        for (RiskAssetTypeClass RiskAssetClass : CustomerRec.getRiskAssetType()) {
            if (RiskAssetClass.getRiskLevel().getValue().equals(EcpRiskLevelHigh)) {
                RiskLevelBool = true;
            }
        }
        if (!RiskLevelBool) {
            RiskAssetTypeClass RiskAssetType = new RiskAssetTypeClass();
            RiskAssetType.setRiskLevel(EcpRiskLevelHigh);
            CustomerRec.addRiskAssetType(RiskAssetType);
        }
    }

    public String UpdateAutoNextKycDate(CustomerRecord CustomerRec, Map<String, Map<String, List<TField>>> ParamConfig) {
        setParamValues(ParamConfig);
        Boolean RiskLevelHigh = false;
        Boolean RiskLevelMedium = false;
        Boolean RiskLevelLow = false;
        for (RiskAssetTypeClass RiskAssetClass : CustomerRec.getRiskAssetType()) {
            if (RiskAssetClass.getRiskLevel().getValue().equals(EcpRiskLevelHigh)) {
                RiskLevelHigh = true;
            }
            if (RiskAssetClass.getRiskLevel().getValue().equals(EcpRiskLevelMedium)) {
                RiskLevelMedium = true;
            }
            if (RiskAssetClass.getRiskLevel().getValue().equals(EcpRiskLevelLow)) {
                RiskLevelLow = true;
            }
        }
        String NextDate = getNextDate(CustomerRec, RiskLevelHigh, RiskLevelMedium, RiskLevelLow);
        return NextDate;
    }

    private String getNextDate(CustomerRecord CustomerRec, Boolean RiskLevelHigh, Boolean RiskLevelMedium,
            Boolean RiskLevelLow) {
        String LastKycReviewDate = CustomerRec.getLastKycReviewDate().getValue();
        String NextDate = null;
        if (!LastKycReviewDate.isEmpty()){
            LocalDate StartDate = LocalDate.of(Integer.parseInt(LastKycReviewDate.substring(0, 4)),
                    Integer.parseInt(LastKycReviewDate.substring(4, 6)),
                    Integer.parseInt(LastKycReviewDate.substring(6, 8)));

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
        }
        return NextDate;
    }

    private void setParamValues(Map<String, Map<String, List<TField>>> ParamConfig) {
        this.EcpRiskLevel = ParamConfig.get("CUSTOMER").get("RISK.LEVEL").get(0).getValue();
        this.EcpRiskLevelHigh = ParamConfig.get("CUSTOMER").get("RISK.LEVEL").get(1).getValue();
        this.EcpRiskLevelMedium = ParamConfig.get("CUSTOMER").get("RISK.LEVEL").get(2).getValue();
        this.EcpRiskLevelLow = ParamConfig.get("CUSTOMER").get("RISK.LEVEL").get(3).getValue();
    }
}
