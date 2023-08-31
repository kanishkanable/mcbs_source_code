package com.mcbc.nsb.qrCode;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.imdocumentimage.ImDocumentImageRecord;
import com.temenos.t24.api.records.imdocumentupload.ImDocumentUploadRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthImDocUpNsb extends RecordLifecycle {

    EbQrCodeNsbRecord QrCodeImagesRec;
    DataAccess DataObj = new DataAccess(this);
    ImDocumentUploadRecord ImDocRec = new ImDocumentUploadRecord();
    String ImageReference;
    String ImageName;
    
    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        System.out.println("Routine started");
        ImDocumentImageRecord ImDocImageRec = new ImDocumentImageRecord(currentRecord);
        System.out.println("READING ImDocumentImageRecord");
        ImageReference = ImDocImageRec.getImageReference().getValue();
        System.out.println("ImageReference   :   " + ImageReference);
        
        QrCodeImagesRec = new EbQrCodeNsbRecord(DataObj.getRecord("EB.QR.CODE.NSB", ImageReference));
        System.out.println("ImageReference   :   " + ImageReference);
        ImageName = QrCodeImagesRec.getImageName().getValue();
        System.out.println("ImageName   :   " + ImageName);
        
        ImDocRec.setFileUpload(ImageName);
        ImDocRec.setUploadId(currentRecordId);
        System.out.println("setting values ");
        
        currentRecords.add(ImDocRec.toStructure());
        System.out.println("Updating OFS");
        
        TransactionData td = new TransactionData();
        
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setUserName("INPUTT");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setVersionId("IM.DOCUMENT.UPLOAD,AUT.QR.CODE.NSB");
        System.out.println("Posting OFS   :   " + td);
        transactionData.add(td);
        System.out.println("Routine Complete");
    }

    
}
