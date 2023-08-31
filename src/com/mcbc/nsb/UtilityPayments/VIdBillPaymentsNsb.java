package com.mcbc.nsb.UtilityPayments;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.icu.text.SimpleDateFormat;
import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
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
public class VIdBillPaymentsNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    String id = null;
    String timeStamp = null;
    Boolean checkId = false;
    String branchId = null;
    
    Map<String, Map<String, List<TField>>> paramConfig;
    
    @Override
    public String checkId(String currentRecordId, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        Logger LOGGER = Logger.getLogger(VIdBillPaymentsNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String currentRecordId 31  - " + currentRecordId);
        LOGGER.info("String currentRecordId 32  - " + transactionContext.getHasLiveRecord());
        
     // GETTING PARAMETER VALUES
        getParamValues();
              
        try {
            LOGGER.info("String currentRecordId 44  - " + currentRecordId);
            dataObj.getRecord("", "EB.BILL.PAYMENTS.NSB", "", currentRecordId);
            LOGGER.info("String currentRecordId 50  - " + currentRecordId);
            return currentRecordId;
        } catch (T24CoreException e) {
        }

        try {
            LOGGER.info("String currentRecordId 45  - " + currentRecordId);
            dataObj.getRecord("", "EB.BILL.PAYMENTS.NSB", "$NAU", currentRecordId);
            LOGGER.info("String currentRecordId 49  - " + id);
            return currentRecordId;
        } catch (T24CoreException e) {
        }

        LOGGER.info("String currentRecordId 52  - " + checkId);
        if (transactionContext.getCurrentFunction().equals("INPUT")) {
            LOGGER.info("String function 37  - " + currentRecordId);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyyyyhhmmss");
                currentRecordId = branchId + "-" + dateformat.format(c.getTime());
            LOGGER.info("String timeStamp 41  - " + currentRecordId);
        }

        LOGGER.info("String currentRecordId 59  - " + currentRecordId);

        return currentRecordId;
    }
    
    private void getParamValues(){
        
        GetParamValueNsb config = new GetParamValueNsb();
        
        config.AddParam("UTILITY.PAYMENTS", new String[] { "BRANCH.ID"});
       
        paramConfig = config.GetParamValue(dataObj);
        this.branchId = paramConfig.get("UTILITY.PAYMENTS").get("BRANCH.ID").get(0).getValue();
    }
}
