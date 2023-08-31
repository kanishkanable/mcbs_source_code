package com.mcbc.nsb.pen;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpengetfileidnsb.EbPenGetFileIdNsbRecord;
import com.temenos.t24.api.tables.ebpengetfileidnsb.EbPenGetFileIdNsbTable;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAuthsetfileGenIdNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);

    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<com.temenos.t24.api.complex.eb.templatehook.TransactionData> transactionData,
            List<TStructure> currentRecords) {
        // TODO Auto-generated method stub
        System.out.println("updateRecord  30  : updateRecord  ");
        String penNameLive = "";
        System.out.println("updateRecord  32  : penNameLive :  " + penNameLive);
        EbPenNameDetailsNsbRecord EbPenNameDetCurRec = new EbPenNameDetailsNsbRecord(currentRecord);
        String penNameCur = EbPenNameDetCurRec.getPenName().getValue();
        System.out.println("updateRecord  35  : penNameCur :  " + penNameCur);

        try {
            System.out.println("updateRecord  38  : penNameCur :   " + penNameCur);
            EbPenNameDetailsNsbRecord EbPenNameDetLiveRec = new EbPenNameDetailsNsbRecord(liveRecord);
            System.out.println("updateRecord  40  : EbPenNameDetLiveRec  ");
            penNameLive = EbPenNameDetLiveRec.getPenName().getValue();
            System.out.println("updateRecord  42  : penNameLive :  " + penNameLive);
        } catch (Exception e) {
            System.out.println("updateRecord  44  : updateRecord  ");
            penNameLive = "";
        }
        System.out.println("updateRecord  47  : penNameCur :  " + penNameCur);
        System.out.println("updateRecord  48  : penNameLive :  " + penNameLive);
        if (!penNameCur.equals(penNameLive)) {
            System.out.println("updateRecord  50  : penNameLive :  " + penNameLive);
            EbPenGetFileIdNsbRecord penFileIdRec = new EbPenGetFileIdNsbRecord(this);
            System.out.println("updateRecord  52  : penFileIdRec  ");
            String penFileGenId = currentRecordId;
            System.out.println("updateRecord  64  : penFileGenId  :  " + penFileGenId);

            penFileIdRec.setPenId(currentRecordId, 0);
            System.out.println("updateRecord  54  : currentRecordId :   " + currentRecordId);
            EbPenGetFileIdNsbTable penFileIdTable = new EbPenGetFileIdNsbTable(this);
            System.out.println("updateRecord  56  : penFileIdTable  ");
            try {
                System.out.println("updateRecord  30  : penFileIdTable.write  " + penFileIdRec);
                penFileIdTable.write(penFileGenId, penFileIdRec);
                System.out.println("updateRecord  30  : currentRecordId  " + currentRecordId);
            } catch (T24IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  109  : bWriter.write(outString)  :  ");
            }
        }
    }

}
