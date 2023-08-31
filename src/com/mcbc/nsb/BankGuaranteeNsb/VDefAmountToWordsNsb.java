package com.mcbc.nsb.BankGuaranteeNsb;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefAmountToWordsNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
            MdDealRecord MdDealRecord = new MdDealRecord(currentRecord);
            String amount = MdDealRecord.getPrincipalAmount().getValue();
            String currency = MdDealRecord.getCurrency().getValue();
            
            if (currency.isEmpty()){
                throw new T24CoreException("", "Currency is mandatory");
            }
  
            UtilEnglishNumberToWords convertAmtToWords = new UtilEnglishNumberToWords();
            String amountInWords = convertAmtToWords.getWord(amount, dataObj, currency);
            
            MdDealRecord.getLocalRefField("L.AMT.WORDS").setValue(amountInWords);            
            currentRecord.set(MdDealRecord.toStructure());
    }

}
