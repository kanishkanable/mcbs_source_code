package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TDate;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.*;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.records.dates.DatesRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

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
public class AcctEffDateValidateNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
                      
        final Logger LOGGER = Logger.getLogger(AcctEffDateValidateNsb.class.getName());
        LOGGER.setLevel(Level.INFO);

        LOGGER.info("String application - " + application);
        LOGGER.info("String currentRecordId - " + currentRecordId);
        LOGGER.info("TStructure currentRecord - " + currentRecord);
        LOGGER.info("TStructure unauthorisedRecord - " + unauthorisedRecord);
        LOGGER.info("TStructure liveRecord - " + liveRecord);
        LOGGER.info("TransactionContext transactionContext - " + transactionContext);

        AaArrangementActivityRecord AaaaRec = new AaArrangementActivityRecord(currentRecord);

        String effectdate = AaaaRec.getEffectiveDate().getValue();
        String customerid = AaaaRec.getCustomer(0).getCustomer().getValue();
        String accountproduct = AaaaRec.getProduct().getValue();
        String currency = AaaaRec.getCurrency().getValue();

        LOGGER.info("String effectdate - " + effectdate);
        LOGGER.info("String customerid - " + customerid);

        DataAccess da = new DataAccess(this);
        TStructure custrec = da.getRecord("CUSTOMER", customerid);
        LOGGER.info("TStructure custrec - " + custrec);

        CustomerRecord mycust = new CustomerRecord(custrec);
        String custopendate = mycust.getLocalRefField("USIRAC.DATE.OF.DEATH").toString();
        LOGGER.info("String custopendate - " + custopendate);

        // Date daterec = new Date();
        // Not sure how to use Date class
        TStructure daterec = da.getRecord("DATES", "GB0010001");
        DatesRecord dr = new DatesRecord(daterec);

        String T24today = dr.getToday().getValue();
        LOGGER.info("String T24today - " + T24today);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        java.util.Date date1;
        java.util.Date date2;
        try {
            date1 = format.parse(custopendate);

            date2 = format.parse(T24today);
            if (date1.compareTo(date2) <= 0) {
                System.out.println("earlier");
            }
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e1, e1);
        }
      
        
        // TNumber = '30';

        // Delta AC.17 - Account Validation
        // Replace AA.SAVINGS.ACCOUNT value to PFCA and IIA in NSB area
        // Replace USD to LKR in NSB area

        try {
            if (null != accountproduct && accountproduct == "AR.SAVINGS.ACCOUNT" && currency != "USD") {
                List<TField> legldocname = mycust.getLegalIdDocName();
                List<LegalIdClass> legalid = mycust.getLegalId();
                LOGGER.info("List<LegalIdClass> legalid - " + legalid);
                LOGGER.info("List<TField> legldocname - " + legldocname);

                for (TField Legalname : legldocname) {
                    String Legaldocname = Legalname.getValue().toString();
                    LOGGER.info("String Legaldocname - " + Legaldocname);
                }
            }
        } catch (Exception e) {
            System.out.println("condition fails");
        }
        
        return validateRecord(application,currentRecordId,currentRecord,unauthorisedRecord,liveRecord,transactionContext);
    }    
}

