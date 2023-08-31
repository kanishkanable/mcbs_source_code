package com.mcbc.nsb.qrCode;

import java.util.Calendar;
import java.util.List;

import com.ibm.icu.text.SimpleDateFormat;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VIdRtnQrCodeNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        String Function = transactionContext.getCurrentFunction();
        if (Function.equals("INPUT")){
            getExistingInauRecord(dataObj, currentRecordId);
            
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyyhhmmssSSSS");
            currentRecordId = dateformat.format(c.getTime());     
        }
        
        return currentRecordId;
    }
    
    private void getExistingInauRecord(DataAccess dataObj, String currentRecordId){
        EbQrCodeNsbRecord QrCodeImagesRec;
        try {
            QrCodeImagesRec = new EbQrCodeNsbRecord(dataObj.getRecord("", "EB.QR.CODE.NSB", "$INAU", currentRecordId));
            throw new T24CoreException("", "Record ID already exist");
        } catch (T24CoreException e) {
            
        }
        
    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        
        
        super.postUpdateRequest(application, currentRecordId, currentRecord, transactionData, currentRecords,
                transactionContext);
    }

    
}

