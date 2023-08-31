package com.mcbc.nsb.pen;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpPenValResponseNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    List<TField> validResponeCode = null; 
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        getParamValues();
        EbPenNameDetailsNsbRecord epndnRec = new EbPenNameDetailsNsbRecord(currentRecord);
        String responseCode = epndnRec.getPenResponseCode().getValue();
        
        if (!validResponeCode.toString().contains(responseCode)){
            epndnRec.getPenResponseCode().setError("EB-VALID.PEN.NAME");
        }
        
        currentRecord.set(epndnRec.toStructure());
        return epndnRec.getValidationResponse();
    }


    public void getParamValues() {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("PEN.NAME.NSB", new String[] { "VALID.RESPONSE.CODE" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);

        validResponeCode = ParamConfig.get("PEN.NAME.NSB").get("VALID.RESPONSE.CODE");
        
    }
}
