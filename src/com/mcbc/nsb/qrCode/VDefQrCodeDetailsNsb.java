package com.mcbc.nsb.qrCode;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebqrcodensb.EbQrCodeNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * Routine to set Customer details upon inserting Account number in the QR.CODE.NSB screen
 * attached to EB.QR.CODE.NSB,IF.INPUT.NSB
 *
 */
public class VDefQrCodeDetailsNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    String accountNumber;
    String customerNumber;
    String firstName;
    String lastName;
    String legDocNewNic;
    String legDocOldNic;
    String legalId;
    String email;
    String phoneNumber;
    String currency;
    String category;
    List<TField> ecpAccCategList;
    AccountRecord accountRec;
    
    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        setParamValues(dataObj);
        
        EbQrCodeNsbRecord QrCodeNsbRec = new EbQrCodeNsbRecord(currentRecord);
        accountNumber = QrCodeNsbRec.getAccountNumber().getValue();
                
        try {
            if (accountNumber.startsWith("AA")){
                AaArrangementRecord aaaRec = new AaArrangementRecord(dataObj.getRecord("AA.ARRANGEMENT", accountNumber));
                List<LinkedApplClass> linkedApplClass = aaaRec.getLinkedAppl();
                for(LinkedApplClass linkedAppl : linkedApplClass){
                    if (linkedAppl.getLinkedAppl().getValue().equals("ACCOUNT")){
                        accountNumber = linkedAppl.getLinkedAppl().getValue();
                    }
                }
            }
            accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", accountNumber));
        } catch (Exception e1) {
            throw new T24CoreException("", "EB-QR.INVALID.ACCNO.NSB");
//            QrCodeNsbRec.getAccountNumber().setError("EB-QR.INVALID.ACCNO.NSB");
        }
        
        customerNumber = accountRec.getCustomer().getValue();
        currency = accountRec.getCurrency().getValue();
        category = accountRec.getCategory().getValue();
        if (!ecpAccCategList.toString().contains(category)){
            throw new T24CoreException("", "EB-QR.INVALID.ACCNO.NSB");
        }
        
        CustomerRecord CustomerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", customerNumber));
        firstName = CustomerRec.getGivenNames().getValue();
        lastName = CustomerRec.getFamilyName().getValue();
        
        if (QrCodeNsbRec.getEmail().getValue().isEmpty()) {
            try {
                email = CustomerRec.getPhone1().get(0).getEmail1().getValue();
                QrCodeNsbRec.setEmail(email);
            } catch (Exception e) {
            }
        }
        
        if (QrCodeNsbRec.getPhoneNumber().getValue().isEmpty()) {
            try {
                phoneNumber = CustomerRec.getPhone1().get(0).getPhone1().getValue();
                if (phoneNumber.isEmpty()){
                    phoneNumber = CustomerRec.getPhone1().get(0).getSms1().getValue();    
                }
                QrCodeNsbRec.setPhoneNumber(phoneNumber);
            } catch (Exception e) {
            }
        }
        
        for (LegalIdClass li : CustomerRec.getLegalId()) {
            if ((li.getLegalDocName().getValue().equals(legDocNewNic))
                    || (li.getLegalDocName().getValue().equals(legDocOldNic))) {
                legalId = li.getLegalId().getValue();
                break;
            }
        }
        
        QrCodeNsbRec.setFirstName(firstName);
        QrCodeNsbRec.setLastName(lastName);
        QrCodeNsbRec.setCurrencyCode(currency);
        QrCodeNsbRec.setNic(legalId);
                
        currentRecord.set(QrCodeNsbRec.toStructure());
    }
    
    private void setParamValues(DataAccess dataObj) {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("QR.CODE.NSB", new String[] { "ACCOUNT.CATEGORY", "LEGAL.DOC.NAMES" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        this.ecpAccCategList = ParamConfig.get("QR.CODE.NSB").get("ACCOUNT.CATEGORY");
        this.legDocNewNic = ParamConfig.get("QR.CODE.NSB").get("LEGAL.DOC.NAMES").get(0).getValue();
        this.legDocOldNic = ParamConfig.get("QR.CODE.NSB").get("LEGAL.DOC.NAMES").get(1).getValue();
        
    }
}
