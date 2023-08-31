package com.mcbc.nsb.qrCode;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCheckAccountNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    Map<String, Map<String, List<TField>>> ParamConfig;
    List<TField> ecpAccCategList;
    String accountNumber;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        // GETTING PARAMETER VALUES
        getParamValues(dataObj);
        
        EbQrCodeNsbRecord ebQrCodeNsbRec = new EbQrCodeNsbRecord(currentRecord);
        accountNumber = ebQrCodeNsbRec.getAccountNumber().getValue();
        
        if (!accountNumber.isEmpty()){
            AccountRecord accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", accountNumber));
            accountRec.getCategory().getValue();
        }
        
        
        return ebQrCodeNsbRec.getValidationResponse();
    }

    private void getParamValues(DataAccess dataObj){
        
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("QR.CODE.NSB", new String[] { "ACCOUNT.CATEGORY"});
        ParamConfig = config.GetParamValue(dataObj);
        ecpAccCategList = ParamConfig.get("QR.CODE.NSB").get("ACCOUNT.CATEGORY");
    }
}
