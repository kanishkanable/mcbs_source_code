package com.mcbc.nsb.pen;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.customer.Phone1Class;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbRecord;
import com.temenos.t24.api.tables.ebpenacxrefnsb.EbPenAcXrefNsbTable;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;
import java.util.List;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class VInputPenAdd extends RecordLifecycle {

    DataAccess da = new DataAccess(this);
    Date dateClass = new Date(this);
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(currentRecord);
        String curpenName = ebpenCur.getPenName().getValue();
        String curpenMobile = ebpenCur.getMobileNo().getValue();

        try {
            EbPenAcXrefNsbRecord recId = new EbPenAcXrefNsbRecord(da.getRecord("EB.PEN.AC.XREF.NSB", curpenName));
            String penId = recId.getId().toString();
            String penStatus = recId.getStatus().toString();
            String penMobile = recId.getMobileNo().toString();
            if (penId.equals(curpenName) || penStatus.equals("A") || penMobile.equals(curpenMobile)) {
                ebpenCur.getPenName().setError("EB-PEN.NAME.EXIST.NSB");
            }
        } catch (Exception exception) {
        }
        return ebpenCur.getValidationResponse();
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        
        EbPenNameDetailsNsbRecord ebpenRec = new EbPenNameDetailsNsbRecord(currentRecord);
        String emailId = null;
        String legalId = null;

        String currentDate = dateClass.getDates().getToday().getValue();

        AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", currentRecordId));
        String CustId = accRec.getCustomer().toString();
        String cusName = accRec.getShortTitle(0).toString();
        CustomerRecord cusRec = new CustomerRecord(da.getRecord("CUSTOMER", CustId));
        String doB = cusRec.getDateOfBirth().toString();

        // String emailId =
        // cusRec.getPhone1().get(0).getEmail1().getValue().toString();
        // String legalId = cusRec.getLegalId().get(0).getLegalId().toString();
        for (Phone1Class phoneClass : cusRec.getPhone1()) {
            emailId = phoneClass.getEmail1().getValue();
            if (!emailId.isEmpty()) {
                break;
            }
        }
        for (LegalIdClass legalIdClass : cusRec.getLegalId()) {
            legalId = legalIdClass.getLegalId().toString();
            if (!legalId.isEmpty()) {
                break;
            }
        }

        ebpenRec.setAcctName(cusName);
        ebpenRec.setDateOfBirth(doB);
        ebpenRec.setEmailId(emailId);
        ebpenRec.setIdNumber(legalId);
        ebpenRec.setPenDateTime(currentDate);

        currentRecord.set(ebpenRec.toStructure());
    }

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        
        EbPenNameDetailsNsbRecord ebpenCur = new EbPenNameDetailsNsbRecord(currentRecord);
        String curpenName = ebpenCur.getPenName().getValue();
        String curpenMobile = ebpenCur.getMobileNo().getValue();
        String curpenStatus = ebpenCur.getStatus().getValue();
        
        EbPenAcXrefNsbRecord ebpencatRec = new EbPenAcXrefNsbRecord(this);
        EbPenAcXrefNsbTable ebpencatTab = new EbPenAcXrefNsbTable(this);
        ebpencatRec.setId(curpenName);
        ebpencatRec.setAcctNo(currentRecordId);
        ebpencatRec.setMobileNo(curpenMobile);
        ebpencatRec.setStatus(curpenStatus);
        
        try {
            ebpencatTab.write(curpenName, ebpencatRec);
        } catch (T24IOException t24IOException) {
        }
        currentRecords.add(ebpencatRec.toStructure());
    }
}