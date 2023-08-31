package com.mcbc.nsb.qrCode;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.imdocumentupload.ImDocumentUploadRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthQrImDocuploadNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
            System.out.println("Routine started");
            EbQrCodeNsbRecord QrCodeImagesRec = new EbQrCodeNsbRecord(currentRecord);
            System.out.println("Reading QrCodeImagesRec");
            String ImageName = QrCodeImagesRec.getImageName().getValue();
            System.out.println("Get ImageName   :   " + ImageName);

            ImDocumentUploadRecord ImDocRec = new ImDocumentUploadRecord();
            System.out.println("Creating new ImDocumentUploadRecord");
            ImDocRec.setFileUpload(ImageName);
            System.out.println("Setting Image Name");
            
            List<String> ImDocumentImageIds = DataObj.selectRecords("", "IM.DOCUMENT.IMAGE", "",
                    "WITH IMAGE.REFERENCE EQ " + currentRecordId);
            System.out.println("Selected records   :  with image name " + ImDocumentImageIds);
            for (String ImDocImageId : ImDocumentImageIds) {
                System.out.println("Getting Image ID ");
                ImDocRec.setUploadId(ImDocImageId);
                System.out.println("Getting Image ID   :   " + ImDocImageId);
            }

            System.out.println("Updating OFS");
            currentRecords.add(ImDocRec.toStructure());

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
