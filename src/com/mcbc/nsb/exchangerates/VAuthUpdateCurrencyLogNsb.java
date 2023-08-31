package com.mcbc.nsb.exchangerates;

import java.util.List;

import com.temenos.api.TBoolean;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.currency.CurrencyRecord;
import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbRecord;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbTable;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthUpdateCurrencyLogNsb extends RecordLifecycle {

    final DataAccess da = new DataAccess(this);
    final TBoolean ordExistFlag = null;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        CurrencyRecord currencyRec = new CurrencyRecord(currentRecord);
        String currencyFileName = currencyRec.getLocalRefField("L.FILE.NAME").getValue();

        System.out.println("CHECK.ORD >>> 37  :  " + currentRecordId);
        final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(
                da.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", currentRecordId + "-" + currencyFileName));
        EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);

        System.out.println("CHECK.ORD >>> 42  :  " + currentRecordId);
        OfsRequestDetailRecord ordRec = new OfsRequestDetailRecord();
        System.out.println("CHECK.ORD >>> 44  :  " + currentRecordId);
        int ordCount = 0;
        System.out.println("CHECK.ORD >>> 46  :  " + currentRecordId);
        for (TField ordIdTfield : logrec.getOrdId()) {
            System.out.println("CHECK.ORD >>> 48  : id  :  " + ordIdTfield);
            String ordId = ordIdTfield.getValue();
            System.out.println("CHECK.ORD >>> 50  :  " + ordId);
            ordRec = da.getRequestResponse(ordId, ordExistFlag);
            System.out.println("CHECK.ORD >>> 52  :  " + currentRecordId);

            logrec.setOfsIn(ordRec.getMsgIn(), ordCount);
            System.out.println("CHECK.ORD >>> 55  : in  :  " + ordRec.getMsgIn());
            logrec.setOfsOut(ordRec.getMsgOut(), ordCount);
            System.out.println("CHECK.ORD >>> 57  : out :  " + ordRec.getMsgOut());

            try {
                System.out.println("CHECK.ORD >>> 60  :  " + currentRecordId);
                logtble.write(ordId, logrec);
            } catch (Exception e3) {
                System.out.println("Exchange Rates Upload : Unable to write : " + ordId);
            }
        }
    }


    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<com.temenos.t24.api.complex.eb.servicehook.TransactionData> transactionData,
            List<TStructure> currentRecords, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        

        }
    }
