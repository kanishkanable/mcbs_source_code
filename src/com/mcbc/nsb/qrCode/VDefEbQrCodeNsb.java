package com.mcbc.nsb.qrCode;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.user.UserRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author Kalyan Pappu
 * Routine to set merchant details in the QR.CODE.NSB screen
 * attached to EB.QR.CODE.NSB,IF.INPUT.NSB
 *
 */
public class VDefEbQrCodeNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);
    Session CurrentSession = new Session(this);
    Date SystemDate = new Date();
    EbQrCodeNsbRecord CurrQrCodeRec;
    EbQrCodeNsbRecord HisQrCodeRec;

    String NetworkType = "1";
    String AcquiringBank = "6719";
    String SubAcquirerId = "000";
    String TID = "0000";
    String InitialInputterName;
    String CustomerNumber;

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CurrQrCodeRec = new EbQrCodeNsbRecord(currentRecord);
        
        try {
            HisQrCodeRec = new EbQrCodeNsbRecord(
                    DataObj.getRecord("BNK", "EB.QR.CODE.IMAGES.NSB", "$HIS", currentRecordId + ";1"));

            InitialInputterName = getInitialInputter(CurrQrCodeRec, currentRecordId, HisQrCodeRec);
        } catch (T24CoreException e) {
            InitialInputterName = CurrentSession.getUserRecord().getSignOnName().getValue();
        }
        
        String MerchantCode = NetworkType + AcquiringBank + SubAcquirerId + currentRecordId + TID;

        CurrQrCodeRec.setMerchantCode(MerchantCode);
        CurrQrCodeRec.setLastUpdateDate(SystemDate.getDates().getToday());
        CurrQrCodeRec.setCreatedUser(InitialInputterName);

        currentRecord.set(CurrQrCodeRec.toStructure());
    }

    private String getUserName(String Inputter) {
        String UserName = new String();
        try {
            UserRecord UserRec = new UserRecord(DataObj.getRecord("USER", Inputter));
            UserName = UserRec.getUserName().getValue();
        } catch (T24CoreException e) {
            UserName = Inputter;
        }
        return UserName;
    }

    private String getInitialInputter(EbQrCodeNsbRecord CurrQrCodeRec, String currentRecordId,
            EbQrCodeNsbRecord HisQrCodeRec) {

        String InitialInputter = HisQrCodeRec.getInputter(0);
        InitialInputter = InitialInputter.split("_")[1];
        String InputterName = getUserName(InitialInputter);

        return InputterName;
    }
}
