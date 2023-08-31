package com.mcbc.nsb.pen;

import java.util.List;


import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
//import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbRecord;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbTable;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class VInputPenDelete extends RecordLifecycle {

//    DataAccess da = new DataAccess(this);
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        EbPenNameDetailsNsbRecord ebpenRec = new EbPenNameDetailsNsbRecord(currentRecord);
        
        try {
            EbPenNameDetailsNsbRecord recId = new EbPenNameDetailsNsbRecord(liveRecord);
            String penS = recId.getStatus().toString();
            if (penS.equals("D")) {
                ebpenRec.getStatus().setError("PEN Already Deleted");
            }
        } catch (Exception IO) {
        }
        return ebpenRec.getValidationResponse();

    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {

        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(currentRecord);
        
        String curM = ebpenCur.getMobileNo().getValue();
        String curN = ebpenCur.getPenName().getValue();

        EbPenAcXrefNsbRecord ebpencatR = new EbPenAcXrefNsbRecord(this);
        EbPenAcXrefNsbTable ebpencatT = new EbPenAcXrefNsbTable(this);

        ebpencatR.setId(curN);
        ebpencatR.setAcctNo(currentRecordId);
        ebpencatR.setMobileNo(curM);
        ebpencatR.setStatus("D");

        try {
            ebpencatT.write(curN, ebpencatR);
        } catch (T24IOException IO) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
        }

        currentRecords.add(ebpencatR.toStructure());

    }

}
