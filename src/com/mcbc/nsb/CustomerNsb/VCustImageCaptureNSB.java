package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.imdocumentimage.ImDocumentImageRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VCustImageCaptureNSB extends RecordLifecycle {

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        // GETTING PARAMETER VALUES
        GetParamValueNsb Config = new GetParamValueNsb();

        DataAccess DataObj = new DataAccess(this);
        Config.AddParam("IMAGE.CAPTURE", new String[] { "IMAGE.TYPE.VALUE" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        String EcpPhotos = ParamConfig.get("IMAGE.CAPTURE").get("IMAGE.TYPE.VALUE").get(0).getValue();
        String EcpSignatures = ParamConfig.get("IMAGE.CAPTURE").get("IMAGE.TYPE.VALUE").get(1).getValue();
        String EcpMultiMediTypeImage = ParamConfig.get("IMAGE.CAPTURE").get("IMAGE.TYPE.VALUE").get(2).getValue();
        String EcpDocuments = ParamConfig.get("IMAGE.CAPTURE").get("IMAGE.TYPE.VALUE").get(3).getValue();
        String EcpMultiMediTypeDocument = ParamConfig.get("IMAGE.CAPTURE").get("IMAGE.TYPE.VALUE").get(4).getValue();

        
        ImDocumentImageRecord ImDocumentImageRec = new ImDocumentImageRecord(currentRecord);
        String ImageType = ImDocumentImageRec.getImageType().getValue();
        String MultiMediaType = null;
        if (!ImageType.isEmpty()) {
            if ((ImageType.equals(EcpPhotos)) || (ImageType.equals(EcpSignatures))) {
                MultiMediaType = EcpMultiMediTypeImage;
            } else if (ImageType.equals(EcpDocuments)) {
                MultiMediaType = EcpMultiMediTypeDocument;
            }
            ImDocumentImageRec.setMultiMediaType(MultiMediaType);
        }
        currentRecord.set(ImDocumentImageRec.toStructure());
    }
}
