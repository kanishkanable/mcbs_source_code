package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TDate;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.*;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalRecord;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalTable;
import com.temenos.tafj.api.client.impl.T24Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class CustAuthUpdateLegalNsb extends RecordLifecycle {
    
    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        

        final Logger LOGGER = Logger.getLogger(CustAuthUpdateLegalNsb.class.getName());
        LOGGER.setLevel(Level.INFO);

        CustomerRecord CustRec = new CustomerRecord(currentRecord);
        List<TField> legaldoc = CustRec.getLegalIdDocName();
        for (TField Legaldoc : legaldoc) {
            String Legaldocvalue = Legaldoc.getValue().toString();
            String arr[] = Legaldocvalue.split("-");
            String LegalID = arr[0];
            String LegalDocName = arr[1];
            LOGGER.info("String LegalID - " + LegalID);
            LOGGER.info("String LegalDocName - " + LegalDocName);
            
            try {

                T24Context aa = new T24Context("AA.ACCOUNT.LEGAL");
                AaAccountLegalTable aaacctlegaltable = new AaAccountLegalTable(aa);
                AaAccountLegalRecord aalegalrecord = new AaAccountLegalRecord();

                LOGGER.info("aalegalrecord obj - " + aalegalrecord);
                LOGGER.info("aalegaltable obj - " + aaacctlegaltable);

                CharSequence LegalIDvar = null;
                List<String> list = aaacctlegaltable.select();
                for(int i=0;i<3;i++){
                    LOGGER.info("aaacctlegaltable record - " + list.get(i).toString());
                }
                AaAccountLegalRecord aAccountLegalRecord = aaacctlegaltable.read(LegalID);
                LOGGER.info("aAccountLegalRecord obj - " + aAccountLegalRecord.toString());
                if (aAccountLegalRecord == null) {
                    aalegalrecord.setAccountId(LegalDocName);
                    aalegalrecord.setCustomerId(currentRecordId);
                    LOGGER.info("aalegal get acc id - " + aalegalrecord.getAccountId().getValue());
                    LOGGER.info("aalegal get cust id - " + aalegalrecord.getCustomerId().getValue());

                    // AaAccountLegalTable aalegaltable = new
                    // AaAccountLegalTable(this);
                    // aalegaltable.write(arrid, aalegal);
                    //transactionData.add(aalegalrecord);
                    
                    aaacctlegaltable.write(LegalID, aalegalrecord);
                }
            } catch (Exception e) {
                System.out.println("write failed" + e);
            }
        }
       
        // TODO Auto-generated method stub
//        super.postUpdateRequest(application, currentRecordId, currentRecord, transactionData, currentRecords,
//                transactionContext);
    }

}
