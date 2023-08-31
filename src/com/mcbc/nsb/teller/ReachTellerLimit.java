package com.mcbc.nsb.teller;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class ReachTellerLimit extends RecordLifecycle {

    public double reachThreshold;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        try
        {
            TField txnAmount = ((Account1Class)tellerRecord.getAccount1().get(0)).getAmountLocal1();
            //TField withdrawCurrency = tellerRecord.getCurrency1();
            //System.out.println("withdrawAmount->" + withdrawAmount);
            getParameters();
            
            if (!txnAmount.toString().isEmpty())
            {                
                if (Double.parseDouble(txnAmount.toString()) > reachThreshold)
                {
                    //withdrawAmount.setOverride("TT-IBU.AUTH.NSB");
                    //withdrawCurrency.setOverride("TT-BRANCH.AUTH.NSB");
                    txnAmount.setOverride("TT-REACH.LIMIT.NSB");
                }
            }
            
        } catch(Exception tellerRecordException)
        {
            
        }
        
        return tellerRecord.getValidationResponse();
        
    }
    
    public void getParameters()
    {     
        DataAccess dataObj = new DataAccess(this);
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("TELLER", new String[] { "REACH.LIMIT.LCY" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        
        reachThreshold = Double.parseDouble(ParamConfig.get("TELLER").get("REACH.LIMIT.LCY").get(0).getValue());
        System.out.println("reachThreshold NEW-> " + reachThreshold);
        
    }
    
}
