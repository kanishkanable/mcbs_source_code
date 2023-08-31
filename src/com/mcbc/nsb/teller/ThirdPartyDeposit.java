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
public class ThirdPartyDeposit extends RecordLifecycle {
    
    double thresholdAmount;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        TField tpdFlag = null;
        TField tpdName = null;
        TField tpdAddress = null;
        TField tpdNic = null;
        TField tpdPurpose = null;
//        TField tpdTelNo = null;
        
        try
        {
            try{
                tpdFlag = tellerRecord.getLocalRefField("L.TPD.FLAG");
            }catch(Exception e){}
            try{
                tpdName = tellerRecord.getLocalRefField("L.TPD.NAME");
            }catch(Exception e){}
            try{
                tpdAddress = tellerRecord.getLocalRefField("L.TPD.ADDRESS");
            }catch(Exception e){}
            try{
                tpdNic = tellerRecord.getLocalRefField("L.TPD.NIC");
            }catch(Exception e){}
            try{
                tpdPurpose = tellerRecord.getLocalRefField("L.TPD.PURPOSE");
            }catch(Exception e){}
/*            try{
                tpdTelNo = tellerRecord.getLocalRefField("L.TPD.TEL.NO");
            }catch(Exception e){}
  */          
            TField depositAmount = ((Account1Class)tellerRecord.getAccount1().get(0)).getAmountLocal1();
            
            getParameters();
            
            System.out.println("thirdPartyFlag->" + tpdFlag);
            System.out.println("depositAmount->" + depositAmount);
            System.out.println("thresholdAmount->" + thresholdAmount);
            
            if (tpdFlag.toString().isEmpty() && (Double.parseDouble(depositAmount.toString()) > thresholdAmount))
            {
                System.out.println("TT-MAND.TDP.FLAG.NSB");
                tpdFlag.setOverride("TT-MAND.TDP.FLAG.NSB");
            }
            
            if (tpdFlag.toString().equals("Y") && !depositAmount.toString().isEmpty() && (Double.parseDouble(depositAmount.toString()) > thresholdAmount)) //and param not null
            {
                if (tpdName.toString().trim().length() == 0)
                {
                    System.out.println("TT-MAND.TDP.NAME.NSB");
                    tpdName.setOverride("TT-MAND.TDP.NAME.NSB");
                }
                                
                if (tpdAddress.toString().trim().length() == 0)
                {
                    System.out.println("TT-MAND.TDP.ADDRESS.NSB");
                    tpdAddress.setOverride("TT-MAND.TDP.ADDRESS.NSB");
                }
                
                if (tpdNic.toString().trim().length() == 0)
                {
                    System.out.println("TT-MAND.TDP.NIC.NSB");
                    tpdNic.setOverride("TT-MAND.TDP.NIC.NSB");
                }
                                
                if (tpdPurpose.toString().trim().length() == 0)
                {
                    System.out.println("TT-MAND.TDP.PURPOSE.NSB");
                    tpdPurpose.setOverride("TT-MAND.TDP.PURPOSE.NSB");
                }
                
/*                if (tpdTelNo.toString().trim().length() == 0)
                {
                    System.out.println("TT-MAND.TDP.TEL.NSB");
                    tpdTelNo.setOverride("TT-MAND.TDP.TEL.NSB");
                }
*/
                                
            }
        } catch (Exception tellerRecordException) {}
        
        return tellerRecord.getValidationResponse();
        
    }
    
    public void getParameters()
    {
        String tempValues;
        
        //T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");
        //DataAccess DataOjb = new DataAccess(EcpNsb);
        //GetParamValueNsb Config = new GetParamValueNsb();
        
        DataAccess DataOjb = new DataAccess(this);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "THIRD.PARTY.THRESHOLD.LCY" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        tempValues = ParamConfig.get("TELLER").get("THIRD.PARTY.THRESHOLD.LCY").get(0).getValue();
        
        thresholdAmount = Double.parseDouble(tempValues);
        
    }

}
