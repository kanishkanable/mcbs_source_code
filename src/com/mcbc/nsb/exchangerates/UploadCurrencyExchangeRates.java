// 
// Decompiled by Procyon v0.5.36
// 

package com.mcbc.nsb.exchangerates;

import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.temenos.api.TBoolean;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbTable;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbRecord;
import com.temenos.t24.api.records.currency.CurrencyMarketClass;
import com.temenos.t24.api.records.currency.CurrencyRecord;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.records.ebfileuploadtype.EbFileUploadTypeRecord;
import java.util.Map;
import com.temenos.api.TField;
import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.t24.api.system.DataAccess;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import java.time.Instant;
import java.sql.Timestamp;
import com.temenos.t24.api.hook.system.ServiceLifecycle;

public class UploadCurrencyExchangeRates extends ServiceLifecycle {
   
    Timestamp timeStampObj;
    Instant instantObj;
    public static final int RANDMIN = 10000;
    public static final int RANDMAX = 99999;

    public UploadCurrencyExchangeRates() {
        this.timeStampObj = null;
        this.instantObj = null;
    }

    final DataAccess da = new DataAccess(this);
    List<String> rowCountarr = new ArrayList<String>();
    final Session session = new Session(this);
    final String currUser = session.getUserId();
    
    final String outPutLocation = this.getCurrencyUploadPath();
    final File dir = new File(outPutLocation);
    final File[] files = dir.listFiles();

    File lastModifiedFile = files[0];
    
    final String fileFullName = getLastModifiedFile(lastModifiedFile);
    final Path filePath = Paths.get(fileFullName, new String[0]);
    final String getFileName = filePath.getFileName().toString();
    final String filename1 = lastModifiedFile.getPath();
    
    public List<String> getIds(final ServiceData serviceData, final List<String> controlList) {
        System.out.println("getIds  63 controlList :  " + controlList.toString());
        if (controlList.isEmpty()) {
            controlList.add(0, "PROCESS.OFS");
            controlList.add(1, "CHECK.ORD");
        }
        System.out.println("getIds  68 controlList :  " + controlList.toString());
        if (files == null || files.length == 0) {
            System.out.println("Directory does not have any files");
        }
        
        System.out.println("getIds  73 files :  " + files.toString());
        
        final String s = controlList.get(0);
        System.out.println("getIds  76 switch s :  " + s);
        switch (s) {
        case "PROCESS.OFS": {
            System.out.println("getIds  79 switch filename1 :  " + filename1);
            try {
                System.out.println("getIds  81 try :  ");
                final BufferedReader read = new BufferedReader(new FileReader(filename1));
                System.out.println("getIds  83 try :  ");
                String line = null;
                System.out.println("getIds  85 try :  ");
                while ((line = read.readLine()) != null) {
                    System.out.println("getIds  87 try :  ");
                    rowCountarr.add(line);
                    System.out.println("getIds  89 rowCountarr :  " + rowCountarr.toString());
                }
                read.close();
            } catch (Exception e) {
                System.out.println("getIds  93 catch :  ");
                System.out.println("Unable to read the file");
            }
            System.out.println("getIds  96 break :  ");
            break;
        }
        case "CHECK.ORD": {
            System.out.println("getIds  100 check.ord :  ");
            rowCountarr = null;
            rowCountarr = new ArrayList<String>();
            rowCountarr.add(getFileName);
            System.out.println("getIds  104 rowCountarr :  " + rowCountarr);
            break;
        }
        default:
            break;
        }
        System.out.println("rowCountarr  110 :  " + rowCountarr);
        return rowCountarr;
    }

    public String getCurrencyUploadPath() {
        System.out.println("getCurrencyUploadPath  115 :  " );
        final GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("EXRATE.FILE.GEN", new String[] { "IN.PATH" });
        final Map<String, Map<String, List<TField>>> ParamConfig = (Map<String, Map<String, List<TField>>>) config
                .GetParamValue(da);
        final String fileGenPath = ParamConfig.get("EXRATE.FILE.GEN").get("IN.PATH").get(0).getValue();
        System.out.println("getCurrencyUploadPath  121 :  " + fileGenPath);
/*
        List<String> path1 = null;
        System.out.println("Ready to select path");
        
        System.out.println("Ready to select path from table");
        path1 = (List<String>) da.selectRecords("", "EB.FILE.UPLOAD.TYPE", "", " WITH @ID EQ CURRENCY.EXCHANGE.RATE");
        System.out.println("Path: " + path1);
*/
        final EbFileUploadTypeRecord fileUploadType = new EbFileUploadTypeRecord(
                da.getRecord("EB.FILE.UPLOAD.TYPE", "CURRENCY.EXCHANGE.RATE"));
        final String uploadPathREM = fileUploadType.getUploadDir().getValue();
        System.out.println("Exchange rates file upload path retrieved1- 133  :   " + uploadPathREM);
        final String uploadPath = fileGenPath + "/" + uploadPathREM;
        System.out.println("Exchange rates file upload path retrieved2 135  :  " + uploadPath);
        
        return uploadPath;
    }

    public boolean validateFileName(final String incomingFileName) {
        
        if (!incomingFileName.endsWith(".csv")) {
            System.out.println("Exchange Rates Upload : Invalid extension 143 " + incomingFileName);
            return false;
        }
        if (!incomingFileName.startsWith(currUser)) {
            System.out.println("Exchange Rates Upload : Invalid prefix 147 " + incomingFileName);
            return false;
        }
        return true;
    }

    public void updateRecord(final String id, final ServiceData serviceData, final String controlItem,
            final TransactionControl transactionControl, final List<SynchronousTransactionData> transactionData,
            final List<TStructure> records) {
        
        System.out.println("updateRecord controlItem >>> 157  :  " + controlItem);
        switch (controlItem) {
        case "PROCESS.OFS": {
            System.out.println("updateRecord controlItem >>> 160 process.ofs :  " + controlItem);
            String recordId = null;
//            try {
                System.out.println("Exchange Rates Upload : Current processing record ID  163 : " + id);
                
                final String ccy = id.split("\\,")[0];
                final String buyRate1 = id.split("\\,")[1];
                final String buyRate2 = id.split("\\,")[2];
                final String buyRate3 = id.split("\\,")[3];
                final String buyRate4 = id.split("\\,")[4];
                final String buyRate5 = id.split("\\,")[5];
                final String sellRate1 = id.split("\\,")[6];
                final String sellRate2 = id.split("\\,")[7];
                final String sellRate3 = id.split("\\,")[8];
                final String sellRate4 = id.split("\\,")[9];
                final String sellRate5 = id.split("\\,")[10];
                
                System.out.println("Exchange Rates Upload : Current processing record ID  177 : " + id);
                
                recordId = ccy;
                System.out.println("Exchange Rates Upload : recordId  180 : " + recordId);
                
                CurrencyRecord currRec = null;
                try {
                    System.out.println("Exchange Rates Upload : recordId  184 : " + recordId);
                    currRec = new CurrencyRecord(da.getRecord("CURRENCY", recordId));
                    System.out.println("Exchange Rates Upload : recordId  186 : " + recordId);
                } catch (Exception e2) {
                    System.out.println("Exchange Rates Upload : recordId  188 error currency doesnt exist : " + recordId);
                    updateErrorLogTable(ccy);
//                    currRec = new CurrencyRecord();
                    System.out.println("Exchange Rates Upload : New record initialized for update 191  : " + recordId);
                }
                
                
                for (CurrencyMarketClass ccyMktClass : currRec.getCurrencyMarket()){
                    System.out.println("Exchange Rates Upload : recordId  198 : " + ccyMktClass.toString());
                    
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("1")) && (!buyRate1.equals("NA"))){
                        ccyMktClass.setBuyRate(buyRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + buyRate1);
                        ccyMktClass.setSellRate(sellRate1);  
                        System.out.println("Exchange Rates Upload : sellRate1  204 : " + sellRate1);
                        break;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("5")) && (!buyRate2.equals("NA"))){
                        ccyMktClass.setBuyRate(buyRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + buyRate2);
                        ccyMktClass.setSellRate(sellRate1);  
                        System.out.println("Exchange Rates Upload : sellRate1  204 : " + sellRate2);
                        break;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("10")) && (!buyRate3.equals("NA"))){
                        ccyMktClass.setBuyRate(buyRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + buyRate3);
                        ccyMktClass.setSellRate(sellRate1);  
                        System.out.println("Exchange Rates Upload : sellRate1  204 : " + sellRate3);
                        break;                     
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("15")) && (!buyRate4.equals("NA"))){
                        ccyMktClass.setBuyRate(buyRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + buyRate4);
                        ccyMktClass.setSellRate(sellRate1);  
                        System.out.println("Exchange Rates Upload : sellRate1  204 : " + sellRate4);
                        break;                    
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("20")) && (!buyRate5.equals("NA"))){
                        ccyMktClass.setBuyRate(buyRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + buyRate5);
                        ccyMktClass.setSellRate(sellRate1);  
                        System.out.println("Exchange Rates Upload : sellRate1  204 : " + sellRate5);
                        break;
                    }
                }
                
                System.out.println("Exchange Rates Upload : currRec  251 : " + currRec.toString());
                records.add(currRec.toStructure());
                System.out.println("Exchange Rates Upload : currRec  253 : " + records.toString());
                
                final SynchronousTransactionData td = new SynchronousTransactionData();
                System.out.println("Exchange Rates Upload : td  256 : " + td);
                td.setFunction("INPUT");
                System.out.println("Exchange Rates Upload : td  258 : " + td);
                td.setSourceId("GENERIC.OFS.PROCESS");
                System.out.println("Exchange Rates Upload : td  260 : " + td);
                td.setTransactionId(recordId);
                System.out.println("Exchange Rates Upload : td  262 : " + recordId);
                td.setVersionId("CURRENCY,UPLOAD.INPUT.NSB");
                System.out.println("Exchange Rates Upload : td  264 : " + td);
                td.setResponseId("RATES.UPLOAD*" + ccy);
                System.out.println("Exchange Rates Upload : td  266 : " + td);
                transactionData.add(td);
                System.out.println("Exchange Rates Upload : td  268 : " + td);
//            } catch (Exception e) {
//                System.out
//                        .println("Exchange Rates Upload : 271 : Record insert error : " + recordId + " : " + e.getMessage());
//            }
            break;
        }
        case "CHECK.ORD": {
            System.out.println("CHECK.ORD >>> " + id);
            final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(
                    da.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", id));
            final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);
            String rowDetailCcy = "";
            final TBoolean ordExistFlag = null;
            OfsRequestDetailRecord ordRec = null;
            for (int logRecRowDataCounter = 0; logRecRowDataCounter < logrec.getRowDetail()
                    .size(); ++logRecRowDataCounter) {
                rowDetailCcy = logrec.getRowDetail().get(logRecRowDataCounter).toString().split("\\,")[0];
                System.out.println("Exchange Rates Upload : rowDetailCcy 291 : " + rowDetailCcy);
                rowDetailCcy = "RATES.UPLOAD*" + rowDetailCcy;
                System.out.println("Exchange Rates Upload : rowDetailCcy 293 : " + rowDetailCcy);
                ordRec = da.getRequestResponse(rowDetailCcy, ordExistFlag);
                System.out.println("Exchange Rates Upload : ordRec  295  : " + ordRec);
                logrec.setOfsIn(ordRec.getMsgIn(), logRecRowDataCounter);
                System.out.println("Exchange Rates Upload : ordRec.getMsgIn()  297  : " + ordRec.getMsgIn());
                logrec.setOfsOut(ordRec.getMsgOut(), logRecRowDataCounter);
                System.out.println("Exchange Rates Upload : ordRec.getMsgOut()  299  : " + ordRec.getMsgOut());
                try {
                    logtble.write((CharSequence) id, logrec);
                } catch (Exception e3) {
                    System.out.println("Exchange Rates Upload : Unable to write : " + id);
                }
            }
            System.out.println("Exchange Rates Upload : Fetch row detail size : " + logrec.getRowDetail().size());
            break;
        }
        default:
            break;
        }
    }
    
    
    
    private void updateErrorLogTable(String ccy){
        final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(
                da.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", getFileName));
        
        int rowCount = 0;
        for (TField rowDetailValue : logrec.getRowDetail()){
            String rowCcy = rowDetailValue.getValue().split("\\,")[0];
            if (rowCcy.equals(ccy)){
                logrec.setOfsIn(ccy + " - Currency record Doesnot exist", rowCount);
            }
            rowCount += 1;
        }
        
        final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);
        try {
            logtble.write((CharSequence) getFileName, logrec);
        } catch (Exception e3) {
            
        }
    }
    
    private String getLastModifiedFile(File lastModifiedFile){
        
        for (int i = 1; i < files.length; ++i) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        
        return lastModifiedFile.toString();
    }
}
