package com.mcbc.nsb.tfs;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.override.OverrideRecord;
import com.temenos.t24.api.records.tellerfinancialservices.TellerFinancialServicesRecord;
import com.temenos.t24.api.records.tellerfinancialservices.TransactionClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: TT.16
 *
 * @author prameela
 *
 */
public class VFCYCashWithdrawalThresholdTfs extends RecordLifecycle {
    
    public double minAmtLevelOne;
    public double maxAmtLevelOne;
    public double minAmtLevelTwo;
    public double maxAmtLevelTwo;
    public double minAmtLevelThree;
    public double maxAmtLevelThree;
    public String overrideLevelOne;
    public String overrideLevelTwo;
    public String yOverrideMsg;
    DataAccess dataVar = new DataAccess(this);
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
      //  TellerRecord tellerRecord = new TellerRecord(currentRecord);
        TellerFinancialServicesRecord tellerfsRecord = new TellerFinancialServicesRecord(currentRecord);
        List<TransactionClass> txnValR = tellerfsRecord.getTransaction();
        try
        {
            
        //    TField withdrawAmount = ((Account1Class)tellerRecord.getAccount1().get(0)).getAmountFcy1();
        //    TField withdrawCurrency = tellerRecord.getCurrency1();
        //    System.out.println("withdrawAmount->" + withdrawAmount);
            getParameters();
            for (TransactionClass i : txnValR) 
            {

            if (i.getTransaction().getValue().equals("FCY.CASHWDL.NSB"))
            {   
                String txnAmountField = i.getAmount().getValue();
                String withdrawCurrency = i.getCurrency().getValue();

                 double withdrawAmount = Double.parseDouble(txnAmountField);
                 

            
            
            if (!String.valueOf(withdrawAmount).isEmpty())
                
            {
                List<String> yOverrideMsg = new ArrayList<>();
             OverrideRecord ebOverrideRecord = new OverrideRecord(dataAccess.getRecord("OVERRIDE","TFS-FCY.OFFICER.A.NSB"));
                if (withdrawAmount >= minAmtLevelOne && withdrawAmount <= maxAmtLevelOne)
                {
                   
                    yOverrideMsg.add("TFS-FCY.OFFICER.A.NSB");
                    
    
              
                   
     
                 
                }
               
                if (withdrawAmount >= minAmtLevelTwo && withdrawAmount <= maxAmtLevelTwo)
                {
                    yOverrideMsg.add("TFS-FCY.OFFICER.A.NSB");
                   // yOverrideMsg.add(String.valueOf(i.getAmount()));
                    
                    System.out.println("yoverridemsg1" + yOverrideMsg.toString().replace("&",String.valueOf(i.getAmount())) );
                   
                   // i.getAmount().setOverride("TFS-FCY.OFFICER.A.NSB".concat(String.valueOf(i.getAmount())));
                    i.getCurrency().setOverride("TT-FCY.OFFICER.B.NSB");
                   
                }
         
                if (withdrawAmount >= minAmtLevelThree)
                {
                    System.out.println("OVERRIDE OFFICER A AND MANAGER A TO BE RAISED");
                    
                    yOverrideMsg.add("TFS-FCY.OFFICER.A.NSB");
                   // yOverrideMsg.add(String.valueOf(i.getAmount()));
                    
                    System.out.println("yoverridemsg1" + yOverrideMsg.toString().replace("&",String.valueOf(i.getAmount())) );
                      i.getCurrency().setOverride("TT-FCY.MANAGER.A.NSB");
                    
                }
                 
            }
            
            } 
        } 
        } catch(Exception tellerRecordException)
        {
            
        }
        
        return tellerfsRecord.getValidationResponse();
        
    }
    
    public void getParameters()
    {
        String[] tempValues;
        
        //T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");
        //DataAccess DataOjb = new DataAccess(EcpNsb);
        //GetParamValueNsb Config = new GetParamValueNsb();
        
        DataAccess DataOjb = new DataAccess(this);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "WITHDRAWAL.THRESHOLD.FCY" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(0).getValue().split("-");
        minAmtLevelOne = Double.parseDouble(Array.get(tempValues, 0).toString());
        maxAmtLevelOne = Double.parseDouble(Array.get(tempValues, 1).toString());
        //overrideLevelOne = Array.get(tempValues, 2).toString();
        System.out.println("minAmtLevelOne->" + minAmtLevelOne);
        System.out.println("maxAmtLevelOne->" + maxAmtLevelOne);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(1).getValue().split("-");
        minAmtLevelTwo = Double.parseDouble(Array.get(tempValues, 0).toString());
        maxAmtLevelTwo = Double.parseDouble(Array.get(tempValues, 1).toString());
        //overrideLevelOne = Array.get(tempValues, 2).toString();
        //overrideLevelTwo = Array.get(tempValues, 3).toString();
        System.out.println("minAmtLevelTwo->" + minAmtLevelTwo);
        System.out.println("maxAmtLevelTwo->" + maxAmtLevelTwo);
        
        tempValues = ParamConfig.get("TELLER").get("WITHDRAWAL.THRESHOLD.FCY").get(2).getValue().split("-");
        minAmtLevelThree = Double.parseDouble(Array.get(tempValues, 0).toString());
        //maxAmtLevelThree = Double.parseDouble(Array.get(tempValues, 1).toString());
        //overrideLevelOne = Array.get(tempValues, 2).toString();
        //overrideLevelTwo = Array.get(tempValues, 3).toString();
        System.out.println("minAmtLevelThree->" + minAmtLevelThree);
        //System.out.println("AmtLevelThree->" + maxAmtLevelThree);
    }
}
