package com.mcbc.nsb.teller;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * This Java program will check the amount against the different thresholds
 * and raise overrides accordingly.
 *
 * @author girlow
 *
 */
public class CheckFcyWithdrawal extends RecordLifecycle {
    
    public double minAmtLevelOne;
    public double maxAmtLevelOne;
    public double minAmtLevelTwo;
    public double maxAmtLevelTwo;
    public double minAmtLevelThree;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        try
        {
            TField withdrawAmount = tellerRecord.getAmountLocal2();
            TField withdrawCurrency = tellerRecord.getCurrency1();
            System.out.println("withdrawAmount->" + withdrawAmount);
            getParameters();
            
            if (Double.parseDouble(withdrawAmount.toString()) > minAmtLevelOne && Double.parseDouble(withdrawAmount.toString()) <= maxAmtLevelOne)
            {
                withdrawAmount.setOverride("TT-OFFICER.LEVEL1.NSB");
            }
            
            if (Double.parseDouble(withdrawAmount.toString()) > minAmtLevelTwo && Double.parseDouble(withdrawAmount.toString()) <= maxAmtLevelTwo)
            {
                withdrawAmount.setOverride("TT-OFFICER.LEVEL1.NSB");
                withdrawCurrency.setOverride("TT-OFFICER.LEVEL2.NSB");
            }
            
            if (Double.parseDouble(withdrawAmount.toString()) > minAmtLevelThree)
            {
                withdrawAmount.setOverride("TT-IBU.AUTH.NSB");
                withdrawCurrency.setOverride("TT-BRANCH.AUTH.NSB");
            }
            
        } catch(Exception tellerRecordException)
        {
            
        }
        
        return tellerRecord.getValidationResponse();
    }
    
    public void getParameters()
    {
        String[] tempValues;
        
        T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");
        DataAccess DataOjb = new DataAccess(EcpNsb);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "WITHDRAWAL.THRESHOLD.FCY" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(0).getValue().split("-");
        minAmtLevelOne = Double.parseDouble(Array.get(tempValues, 0).toString());
        maxAmtLevelOne = Double.parseDouble(Array.get(tempValues, 1).toString());
        System.out.println("minAmtLevelOne->" + minAmtLevelOne);
        System.out.println("maxAmtLevelOne->" + maxAmtLevelOne);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(1).getValue().split("-");
        minAmtLevelTwo = Double.parseDouble(Array.get(tempValues, 0).toString());
        maxAmtLevelTwo = Double.parseDouble(Array.get(tempValues, 1).toString());
        System.out.println("minAmtLevelTwo->" + minAmtLevelTwo);
        System.out.println("maxAmtLevelTwo->" + maxAmtLevelTwo);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(2).getValue().split("-");
        minAmtLevelThree = Double.parseDouble(Array.get(tempValues, 0).toString());
        System.out.println("minAmtLevelThree->" + minAmtLevelThree);
        
    }

}
