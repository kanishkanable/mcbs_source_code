package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.newtest.MultiThreadRtnDemo;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class BIMAcctcheckGiftNsb extends ServiceLifecycle {

    final Logger LOGGER = Logger.getLogger(BIMAcctcheckGiftNsb.class.getName());
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
                
        LOGGER.info("Calling routien - ");
        
        DataAccess da = new DataAccess(this);
        
//        List<String> recIds = da.selectRecords("BNK", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ 'ACCOUNTS' AND PRODUCT EQ 'IM.NSB'");
        List<String> recIds = da.selectRecords("BNK", "AA.ARRANGEMENT", "", "WITH PRODUCT.LINE EQ 'ACCOUNTS' AND PRODUCT EQ 'AR.SAVINGS.ACCOUNT'");
        LOGGER.info("Record Ids" + recIds);
        
        return recIds;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub
        
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling updateRecord method - ");
        LOGGER.info("id  - " + id);
        
        super.updateRecord(id, serviceData, controlItem, transactionControl, transactionData, records);
    }

    
    
}
