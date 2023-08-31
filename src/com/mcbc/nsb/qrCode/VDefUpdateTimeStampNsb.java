package com.mcbc.nsb.qrCode;

import java.util.Calendar;

import com.ibm.icu.text.SimpleDateFormat;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 * ROUTINE TO UPDATE STATUS TIME STAMP WHEN THERE IS ANY CHANGE IN THE STATUS OF QRCODE
 * ROUTINE ATTACHED TO 
 * VERSION: 
 *      EB.QR.CODE.NSB,IF.INPUT.NSB
 *      
 * 
 */
public class VDefUpdateTimeStampNsb extends RecordLifecycle {

    String CurrStatus;
    String LiveStatus;
    EbQrCodeNsbRecord CurrQrCodeRec;
    EbQrCodeNsbRecord LiveQrCodeRec;

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CurrQrCodeRec = new EbQrCodeNsbRecord(currentRecord);
        CurrStatus = CurrQrCodeRec.getStatus().getValue();
        if (CurrStatus.isEmpty()) {
            CurrQrCodeRec.getStatus().setError("EB-QR.CODE.STATUS.NSB");
        }
        try {
            LiveQrCodeRec = new EbQrCodeNsbRecord(liveRecord);
            LiveStatus = LiveQrCodeRec.getStatus().getValue();
        } catch (T24CoreException e) {

        }

        if (!LiveStatus.equals(CurrStatus)) {
            // String CurrTimeStamp = CurrQrCodeRec.getDateTime(0);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM yy hh:mm");
            String CurrTimeStamp = dateformat.format(c.getTime());
            CurrQrCodeRec.setStatusTimestamp(CurrTimeStamp);
        }
        currentRecord.set(CurrQrCodeRec.toStructure());
    }

}
