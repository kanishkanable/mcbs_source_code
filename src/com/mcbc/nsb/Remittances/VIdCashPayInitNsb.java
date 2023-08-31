package com.mcbc.nsb.Remittances;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.icu.text.SimpleDateFormat;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * ID Routine is getting triggered again during validation and new ID is generated 
 */
public class VIdCashPayInitNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    String id = null;
    String timeStamp = null;
    Boolean checkId = false;

    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        Logger LOGGER = Logger.getLogger(VIdCashPayInitNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String currentRecordId 31  - " + currentRecordId);
        LOGGER.info("String currentRecordId 32  - " + transactionContext.getHasLiveRecord());
        
              
        try {
            LOGGER.info("String currentRecordId 44  - " + currentRecordId);
            dataObj.getRecord("", "EB.CASHPAY.INIT.NSB", "", currentRecordId);
            LOGGER.info("String currentRecordId 50  - " + currentRecordId);
            return currentRecordId;
        } catch (T24CoreException e) {
        }

        try {
            LOGGER.info("String currentRecordId 45  - " + currentRecordId);
            dataObj.getRecord("", "EB.CASHPAY.INIT.NSB", "$NAU", currentRecordId);
            LOGGER.info("String currentRecordId 49  - " + id);
            return currentRecordId;
        } catch (T24CoreException e) {
        }

        LOGGER.info("String currentRecordId 52  - " + checkId);
        if (transactionContext.getCurrentFunction().equals("INPUT")) {
            LOGGER.info("String function 37  - " + currentRecordId);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyyyyhhmmss");
                currentRecordId = dateformat.format(c.getTime());
            LOGGER.info("String timeStamp 41  - " + currentRecordId);
        }

        LOGGER.info("String currentRecordId 59  - " + currentRecordId);

        return currentRecordId;
    }
}
