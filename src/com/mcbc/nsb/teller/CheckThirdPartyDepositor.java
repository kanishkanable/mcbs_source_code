package com.mcbc.nsb.teller;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * This Java program will retrieve the values of the Third-Party Flag (Y or N)
 * and the parameterised maximum amount allowed for deposit. If Flag is Y and
 * the amount deposited is greater or equal to the parameter, then a check is
 * done on the local fields that should be mandatory - Depositor Name,
 * Depositor Address, Depositor NIC, Deposit Purpose, Depositor Telephone No.
 * Error should be raised if fields above mentioned are blank.
 *
 * @author girlow
 *
 */
public class CheckThirdPartyDepositor extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        //try
        //{
            TField thirdPartyFlag = tellerRecord.getLocalRefField("L.OTH.PRTY.FLG");
            TField depositorNIC = tellerRecord.getLocalRefField("L.DEP.NIC.NO");
            TField depositorTelNo = tellerRecord.getLocalRefField("L.DEP.PHONE");
            TField depositAmount = tellerRecord.getAmountLocal2();
            double thresholdAmount = getAmountParameter();
            System.out.println("thirdPartyFlag->" + thirdPartyFlag);
            System.out.println("depositAmount->" + depositAmount);
            System.out.println("thresholdAmount->" + thresholdAmount);
            if (thirdPartyFlag.toString().equals("Y") && !depositAmount.toString().isEmpty() && (Double.parseDouble(depositAmount.toString()) > thresholdAmount)) //and param not null
            {
                if (depositorNIC.toString().trim().length() == 0)
                {
                    System.out.println("EB-MAND.DEP.NIC.NSB");
                    depositorNIC.setError("EB-MAND.DEP.NIC.NSB");
                }
                                
                if (depositorTelNo.toString().trim().length() == 0)
                {
                    System.out.println("EB-MAND.DEP.PHONE.NSB");
                    depositorTelNo.setError("EB-MAND.DEP.PHONE.NSB");
                }
                
                try {
                    tellerRecord.getLocalRefGroups("L.DEP.NAME").get(0).getLocalRefField("L.DEP.NAME").getValue();
                } catch (Exception e) {
                    System.out.println("EB-MAND.DEP.NAME.NSB");
                    throw new T24CoreException("EB-ERROR.SELECTION","EB-MAND.DEP.NAME.NSB");
                }
                
                try {
                    tellerRecord.getLocalRefGroups("L.DEP.ADDRESS").get(0).getLocalRefField("L.DEP.ADDRESS").getValue();
                } catch (Exception e) {
                    System.out.println("EB-MAND.DEP.ADDRESS.NSB");
                    throw new T24CoreException("EB-ERROR.SELECTION","EB-MAND.DEP.ADDRESS.NSB");
                }
                
                try {
                    tellerRecord.getLocalRefGroups("L.DEP.PURPOSE").get(0).getLocalRefField("L.DEP.PURPOSE").getValue();
                } catch (Exception e) {
                    System.out.println("EB-MAND.DEP.PURPOSE.NSB");
                    throw new T24CoreException("EB-ERROR.SELECTION","EB-MAND.DEP.PURPOSE.NSB");
                }
                                
            }
        //} catch(Exception tellerRecordException)
        //{
            //System.out.println("Error in CheckThirdPartyDepositor : " + tellerRecordException.toString());
        //}
        
        return tellerRecord.getValidationResponse();
        
    }
    
    public double getAmountParameter()
    {
        T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");
        DataAccess DataOjb = new DataAccess(EcpNsb);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "THIRD.PARTY.THRESHOLD.LCY" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        return Double.parseDouble(ParamConfig.get("TELLER").get("THIRD.PARTY.THRESHOLD.LCY").get(0).getValue());
    }

}
