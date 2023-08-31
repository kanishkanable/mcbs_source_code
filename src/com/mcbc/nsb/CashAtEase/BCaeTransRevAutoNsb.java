package com.mcbc.nsb.CashAtEase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.tables.ebcaetransactionsnsb.EbCaeTransactionsNsbRecord;
import com.temenos.t24.api.tables.ebinterfaceloadnsb.EbInterfaceLoadNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BCaeTransRevAutoNsb extends ServiceLifecycle {

    Logger LOGGER = Logger.getLogger(BCaeTransRevAutoNsb.class.getName());
    
    DataAccess dataObj = new DataAccess(this);
    Date sysDate = new Date(this);
    
    String debitAcctNo = null;
    String debitCurrency = null;
    String debitValueDate = null;
    String creditAcctNo = null;
    String creditCurrency = null;
    String creditAmount = null;
    String creditValueDate = null;
    String bankCode = null;
    String flag = null;
    String transactionStatus = null;
    String fileStatus = null;
    String inputter = null;
    String authoriser = null;
    String printLine = null;
    BufferedWriter bWriter = null;
    String outputPath = null;
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String returnIds  75 - ");
        
        String bankCode = "DFCC";
        List<String> selectIds = dataObj.selectRecords("", "EB.CAE.TRANSACTIONS.NSB", "", "WITH FILE.STATUS EQ NO AND BANK.CODE EQ " + bankCode);
        
        getFileDetailsSelect();
        
        try {
            bWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
            System.out.println("inputRecord  120  : bWriter.write(outString)  :  ");
        }
        
        return selectIds;
    }

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String returnIds  75 - ");
        
        getFileDetailsProcess();
        EbCaeTransactionsNsbRecord eCaeTranRec = new EbCaeTransactionsNsbRecord(dataObj.getRecord("EB.CAE.TRANSACTIONS.NSB", id));

        
        debitAcctNo = eCaeTranRec.getDebitAcctNo().getValue();
        debitCurrency = eCaeTranRec.getDebitCurrency().getValue();
        debitValueDate = eCaeTranRec.getDebitValueDate().getValue();
        creditAcctNo = eCaeTranRec.getCreditAcctNo().getValue();
        creditCurrency = eCaeTranRec.getCreditCurrency().getValue();
        creditAmount = eCaeTranRec.getCreditAmount().getValue();
        creditValueDate = eCaeTranRec.getCreditValueDate().getValue();
        bankCode = eCaeTranRec.getBankCode().getValue();
        flag = eCaeTranRec.getFlag().getValue();
        transactionStatus = eCaeTranRec.getTransactionStatus().getValue();
        fileStatus = eCaeTranRec.getFileStatus().getValue();
        inputter = eCaeTranRec.getInputter().getValue();
        authoriser = eCaeTranRec.getAuthoriser().getValue();
        
        printOutputLines();
        
        super.postUpdateRequest(id, serviceData, controlItem, transactionData, records);
    }
    
    private void printOutputLines() {
        LOGGER.info("String returnIds  179 - " );
        printLine = "";
        printLine = debitAcctNo + "|" + debitCurrency + "|" + debitValueDate + "|" + creditAcctNo + "|" + creditCurrency + "|" + creditAmount + "|" + creditValueDate + "|" + bankCode + "|" + flag + "|"  + transactionStatus + "|" + fileStatus + "|" + inputter + "|" + authoriser;
        LOGGER.info("String returnIds  186 - " );
        if (!printLine.isEmpty()) {
            try {
                bWriter = new BufferedWriter(new FileWriter(outputPath, true));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  69  : bWriter  :  ");
            }

            try {
                bWriter.newLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  98  : bWriter.write(outString)  :  ");
            }

            try {
                bWriter.write(printLine);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  89  : bWriter.write(outString)  :  ");
            }
            System.out.println("inputRecord  91  : bWriter.write(outString)  :  ");
        }
    }

    private void getFileDetailsSelect() {
        
        EbInterfaceLoadNsbRecord intExtRec = new EbInterfaceLoadNsbRecord(dataObj.getRecord("INTERFACE.EXTRACT.NSB", "IWD.CAE.LOAD"));
         String filePath = intExtRec.getLoadDir().getValue();
         String fileName = intExtRec.getVersionId().getValue();
         outputPath = filePath + "/" + fileName;
         
        File fileChecker = new File(outputPath);
        System.out.println("inputRecord  55  : formattedDate  :  ");

        if (fileChecker.exists() && fileChecker.isFile()) {
            System.out.println("inputRecord  57  : formattedDate  :  ");
            fileChecker.delete();
            System.out.println("inputRecord  59  : formattedDate  :  ");
        }
        System.out.println("inputRecord  61  : formattedDate  :  ");

        try {
            bWriter = new BufferedWriter(new FileWriter(outputPath, true));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
            System.out.println("inputRecord  69  : bWriter  :  ");
        }
    }

    private void getFileDetailsProcess() {

        EbInterfaceLoadNsbRecord intExtRec = new EbInterfaceLoadNsbRecord(dataObj.getRecord("INTERFACE.EXTRACT.NSB", "IWD.CAE.LOAD"));
        String filePath = intExtRec.getLoadDir().getValue();
        String fileName = intExtRec.getVersionId().getValue();
        outputPath = filePath + "/" + fileName;
        
        System.out.println("inputRecord  71  : formattedDate  :  ");
    }
}
