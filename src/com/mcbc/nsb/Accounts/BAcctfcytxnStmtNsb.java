package com.mcbc.nsb.Accounts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class BAcctfcytxnStmtNsb extends ServiceLifecycle {

    DataAccess da = new DataAccess(this);
    Logger LOGGER = Logger.getLogger(BAcctfcytxnStmtNsb.class.getName());
    List<String> Returnval = new ArrayList<String>();
    // String outputLocation = "/nsbt24/nsbdev/bnk/CLIENT_BUILD/UD/";
    String outputLocation = "C:/R20/t24/bnk/UD/DS.DIR/";

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling getIds method - ");
        List<String> recIds = da.selectRecords("", "ACCT.ENT.LWORK.DAY", "", "");
        LOGGER.info("recIds size - " + recIds.size());
        LOGGER.info("recIds - " + recIds);
        
        recIds.clear();
        recIds.add("98973");
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
        LOGGER.info("serviceData  - " + serviceData);
        LOGGER.info("controlItem  - " + controlItem);
        LOGGER.info("transactionControl  - " + transactionControl);
        LOGGER.info("transactionData  - " + transactionData);
        LOGGER.info("records  - " + records);

        try {
            if (id.matches("[0-9]+") && id.length() > 2) {
                AccountRecord acct = new AccountRecord(da.getRecord("ACCOUNT", id));
                String curr = acct.getCurrency().getValue();
                String category = acct.getCategory().getValue();
                // Validate only customer account category. ignore others like
                // nostro.
                if (!curr.equals("LKR")) {
                    List<String> StmtIdList = da.getConcatValues("ACCT.ENT.LWORK.DAY", id);
                    for (String stmtid : StmtIdList) {
                        if (stmtid.substring(0, 1).equals("S!")) {
                            // STMT.ENTRY.DETAIL.XREF process
                            // currently not updating this logic
                        } else {
                            StmtEntryRecord stmt = new StmtEntryRecord(da.getRecord("STMT.ENTRY", stmtid));
                            String CompanyCode = stmt.getCompanyCode().getValue();
                            String AmountFcy = stmt.getAmountFcy().getValue();
                            String CustomerId = stmt.getCustomerId().getValue();
                            String Category = stmt.getProductCategory().getValue();
                            String ValueDate = stmt.getValueDate().getValue();
                            String Currency = stmt.getCurrency().getValue();
                            String Txnref = stmt.getTransReference().getValue();
                            String Ourref = stmt.getOurReference().getValue();
                            Returnval.add(CompanyCode);
                            Returnval.add(AmountFcy);
                            Returnval.add(CustomerId);
                            Returnval.add(Category);
                            Returnval.add(ValueDate);
                        }
                    }
                                        
                    outputLocation = outputLocation.concat(String.valueOf(System.currentTimeMillis()));

                    FileWriter myWriter;
                    try {
                        myWriter = new FileWriter(outputLocation);
                        myWriter.write(Returnval.toString());
                        myWriter.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        // Uncomment and replace with appropriate logger
                        // LOGGER.error(e, e);
                    }
                }
            } else {
                LOGGER.info("id not numeric" + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
