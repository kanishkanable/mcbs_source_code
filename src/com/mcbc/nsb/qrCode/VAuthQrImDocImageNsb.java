package com.mcbc.nsb.qrCode;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.imdocumentimage.ImDocumentImageRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthQrImDocImageNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        System.out.println("Routine started");
        EbQrCodeNsbRecord QrCodeImagesRec = new EbQrCodeNsbRecord(currentRecord);
        System.out.println("Reading QrCodeImagesRec");

        ImDocumentImageRecord ImDocRec = new ImDocumentImageRecord();
        System.out.println("Creating new ImDocumentImageRecord");

        ImDocRec.setImageType("QR.CODE.MRCHNT");
        System.out.println("setImageType");
        ImDocRec.setImageApplication("EB.QR.CODE.NSB");
        System.out.println("setImageApplication");
        ImDocRec.setImageReference(currentRecordId);
        System.out.println("setImageReference");
        
        ImDocRec.setDescription("QR.CODE.IMAGES", 0);
        ImDocRec.setShortDescription("QR.CODE.IMAGES");
        ImDocRec.setMultiMediaType("IMAGE");
        System.out.println("setShortDescription");
        
        ImDocRec.setImage(QrCodeImagesRec.getImageName().getValue());
        System.out.println("setImage");
        System.out.println(
                "QrCodeImagesRec.getImageName().getValue()   :   " + QrCodeImagesRec.getImageName().getValue());

        
        currentRecords.add(ImDocRec.toStructure());
        System.out.println("Adding Values");

        TransactionData td = new TransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        
        td.setUserName("INPUTT");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setVersionId("IM.DOCUMENT.IMAGE,AUT.QR.CODE.NSB");
        System.out.println("Posting OFS   :   " + td);
        transactionData.add(td);
        System.out.println("Routine Complete");
    }
}
