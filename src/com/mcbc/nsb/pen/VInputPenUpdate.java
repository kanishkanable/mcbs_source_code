package com.mcbc.nsb.pen;

import java.util.List;

import com.temenos.api.TStructure;

import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbRecord;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbTable;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class VInputPenUpdate extends RecordLifecycle {

    DataAccess da = new DataAccess(this);

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(currentRecord);

        String curpenN = ebpenCur.getPenName().getValue();
        String curpenM = ebpenCur.getMobileNo().getValue();

        try {
            EbPenAcXrefNsbRecord recId = new EbPenAcXrefNsbRecord(da.getRecord("EB.PEN.AC.XREF.NSB", curpenN));

            String penId = recId.getId().toString();
            String penS = recId.getStatus().toString();
            String penM = recId.getMobileNo().toString();

            if (penId.equals(curpenN) || penS.equals("A") || penM.equals(curpenM)) {
                ebpenCur.getPenName().setError("EB-PEN.NAME.EXIST.NSB");
            }
        } catch (Exception IO) {
            // ebpenCur.setPenName(curpenN);
            // throw new T24CoreException("", "Record Id Does Not Exist");
        }
        return ebpenCur.getValidationResponse();
    }

    @Override
    public void updateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext,
            List<com.temenos.t24.api.complex.eb.templatehook.TransactionData> transactionData,
            List<TStructure> currentRecords) {

        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(liveRecord);

        // String curpenI = ebpenCur.getId().getValue();
        String curN = ebpenCur.getPenName().getValue();

        EbPenAcXrefNsbTable ebpencatT = new EbPenAcXrefNsbTable(this);

        try {
            EbPenAcXrefNsbRecord recId = new EbPenAcXrefNsbRecord(da.getRecord("EB.PEN.AC.XREF.NSB", curN));
            String penId = recId.getId().toString();
            String accId = recId.getAcctNo().toString();

            if (penId.equals(curN) || accId.equals(currentRecordId)) {
                ebpencatT.delete(penId);
            } else {
            }
        } catch (Exception IO) {
        }

    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {

        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(currentRecord);

        // String curpenI = ebpenCur.getId().getValue();
        String curN = ebpenCur.getPenName().getValue();
        String curM = ebpenCur.getMobileNo().getValue();
        String curS = ebpenCur.getStatus().getValue();

        EbPenAcXrefNsbRecord ebpencatR = new EbPenAcXrefNsbRecord(this);
        EbPenAcXrefNsbTable ebpencatT = new EbPenAcXrefNsbTable(this);

        ebpencatR.setId(curN);
        ebpencatR.setAcctNo(currentRecordId);
        ebpencatR.setMobileNo(curM);
        ebpencatR.setStatus(curS);

        try {
            ebpencatT.write(curN, ebpencatR);
        } catch (T24IOException IO) {

        }
        currentRecords.add(ebpencatR.toStructure());
    }

}