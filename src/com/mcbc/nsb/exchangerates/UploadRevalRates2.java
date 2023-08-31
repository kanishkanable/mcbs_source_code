// 
// Decompiled by Procyon v0.5.36
// 

package com.mcbc.nsb.exchangerates;

import com.temenos.t24.api.records.ofsrequestdetail.OfsRequestDetailRecord;
import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
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
import com.temenos.tafj.api.client.impl.T24Context;
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

public class UploadRevalRates2 extends ServiceLifecycle
{
    Timestamp timeStampObj;
    Instant instantObj;
    public static final int RANDMIN = 10000;
    public static final int RANDMAX = 99999;
    final DataAccess da = new DataAccess(this);
    final Session session = new Session(this);
    List<String> rowCountarr = new ArrayList<String>();
    
    public UploadRevalRates2() {
        this.timeStampObj = null;
        this.instantObj = null;
    }
    
    public List<String> getIds(final ServiceData serviceData, final List<String> controlList) {
        if (controlList.isEmpty()) {
            controlList.add(0, "PROCESS.OFS");
            controlList.add(1, "CHECK.ORD");
        }
        
        final String outPutLocation = this.getCurrencyUploadPath();
        final File dir = new File(outPutLocation);
        final File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Directory does not have any files");
        }
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; ++i) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        final String fileFullName = lastModifiedFile.toString();
        final Path filePath = Paths.get(fileFullName, new String[0]);
        System.out.println("Get File Name " + filePath);
        final String getFileName = filePath.getFileName().toString();
        System.out.println("Get File Name " + getFileName);
        System.out.println("Latest file " + lastModifiedFile);
        final String filename1 = lastModifiedFile.getPath();
        System.out.println("Path " + filename1);
        final String s = controlList.get(0);
        switch (s) {
            case "PROCESS.OFS": {
                try {
                    final BufferedReader read = new BufferedReader(new FileReader(filename1));
                    String line = null;
                    while ((line = read.readLine()) != null) {
                        rowCountarr.add(line);
                    }
                    read.close();
                }
                catch (Exception e) {
                    System.out.println("Unable to read the file");
                }
                break;
            }
            case "CHECK.ORD": {
                rowCountarr = null;
                rowCountarr = new ArrayList<String>();
                rowCountarr.add(getFileName);
                break;
            }
            default:
                break;
        }
        return rowCountarr;
    }
    
    public String getCurrencyUploadPath() {
        final GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("REVALRATE.FILE.GEN", new String[] { "IN.PATH" });
        final Map<String, Map<String, List<TField>>> ParamConfig = (Map<String, Map<String, List<TField>>>)config.GetParamValue(da);
        final String fileGenPath = ParamConfig.get("REVALRATE.FILE.GEN").get("IN.PATH").get(0).getValue();
        List<String> path1 = null;
        System.out.println("Ready to select path");
//        final DataAccess dba = new DataAccess((T24Context)this);
        System.out.println("Ready to select path from table");
        path1 = (List<String>)da.selectRecords("", "EB.FILE.UPLOAD.TYPE", "", " WITH @ID EQ CURRENCY.REVAL.RATE");
        System.out.println("Path: " + path1);
        final EbFileUploadTypeRecord fileUploadType = new EbFileUploadTypeRecord(da.getRecord("EB.FILE.UPLOAD.TYPE", (String)path1.get(0)));
        final String uploadPathREM = fileUploadType.getUploadDir().toString();
        System.out.println("Reval rates file upload path retrieved1 " + uploadPathREM);
        final String uploadPath = String.valueOf(fileGenPath) + "/" + uploadPathREM;
        System.out.println("Reval rates file upload path retrieved2 " + uploadPath);
        return uploadPath;
    }
    
    public boolean validateFileName(final String incomingFileName) {
        final String currUser = session.getUserId();
        if (!incomingFileName.endsWith(".csv")) {
            System.out.println("Reval Rates Upload : Invalid extension " + incomingFileName);
            return false;
        }
        if (!incomingFileName.startsWith(currUser)) {
            System.out.println("Reval Rates Upload : Invalid prefix " + incomingFileName);
            return false;
        }
        return true;
    }
    
    public void updateRecord(final String id, final ServiceData serviceData, final String controlItem, final TransactionControl transactionControl, final List<SynchronousTransactionData> transactionData, final List<TStructure> records) {
        System.out.println("updateRecord controlItem >>> " + controlItem);
        switch (controlItem) {
            case "PROCESS.OFS": {
                String recordId = null;
                try {
                    System.out.println("Reval Rates Upload : Current processing record ID : " + id);
                    final String ccy = id.split("\\,")[0];
                    final String revalRate1 = id.split("\\,")[1];
                    final String revalRate2 = id.split("\\,")[2];
                    final String revalRate3 = id.split("\\,")[3];
                    final String revalRate4 = id.split("\\,")[4];
                    final String revalRate5 = id.split("\\,")[5];
                    int i = 0;
                    recordId = ccy;
                    final DataAccess da = new DataAccess((T24Context)this);
                    CurrencyRecord currRec = null;
                    try {
                        currRec = new CurrencyRecord(da.getRecord("FBNK.CURRENCY", recordId));  // FBNK TO BE REMOVED
                        System.out.println("Reval Rates Upload : Existing record initialized for update : " + recordId);
                    }
                    catch (Exception e2) {
						// UPDATE ERROR TABLE
                        currRec = new CurrencyRecord();
                        System.out.println("Reval Rates Upload : New record initialized for update : " + recordId);
                    }
                    final CurrencyMarketClass ccyMktCls1 = new CurrencyMarketClass();
                    if (!revalRate1.equals("NA")) {
                        ccyMktCls1.setCurrencyMarket((CharSequence)"1");
                        ccyMktCls1.setRevalRate((CharSequence)revalRate1);
                        currRec.setCurrencyMarket(ccyMktCls1, i);
                        ++i;
                        System.out.println("Reval Rates Upload : Currency market class update : " + ccyMktCls1);
                    }
                    final CurrencyMarketClass ccyMktCls2 = new CurrencyMarketClass();
                    if (!revalRate2.equals("NA")) {
                        ccyMktCls2.setCurrencyMarket((CharSequence)"5");
                        ccyMktCls2.setRevalRate((CharSequence)revalRate2);
                        currRec.setCurrencyMarket(ccyMktCls2, i);
                        ++i;
                        System.out.println("Reval Rates Upload : Currency market class update : " + ccyMktCls2);
                    }
                    final CurrencyMarketClass ccyMktCls3 = new CurrencyMarketClass();
                    if (!revalRate3.equals("NA")) {
                        ccyMktCls3.setCurrencyMarket((CharSequence)"10");
                        ccyMktCls3.setRevalRate((CharSequence)revalRate3);
                        currRec.setCurrencyMarket(ccyMktCls3, i);
                        ++i;
                        System.out.println("Reval Rates Upload : Currency market class update : " + ccyMktCls3);
                    }
                    final CurrencyMarketClass ccyMktCls4 = new CurrencyMarketClass();
                    if (!revalRate4.equals("NA")) {
                        ccyMktCls4.setCurrencyMarket((CharSequence)"15");
                        ccyMktCls4.setRevalRate((CharSequence)revalRate4);
                        currRec.setCurrencyMarket(ccyMktCls4, i);
                        ++i;
                        System.out.println("Reval Rates Upload : Currency market class update : " + ccyMktCls4);
                    }
                    final CurrencyMarketClass ccyMktCls5 = new CurrencyMarketClass();
                    if (!revalRate5.equals("NA")) {
                        ccyMktCls5.setCurrencyMarket((CharSequence)"19");
                        ccyMktCls5.setRevalRate((CharSequence)revalRate5);
                        currRec.setCurrencyMarket(ccyMktCls5, i);
                        ++i;
                        System.out.println("Reval Rates Upload : Currency market class update : " + ccyMktCls5);
                    }
                    System.out.println("Reval Rates Upload : Record set for add or update : " + recordId);
                    records.add(currRec.toStructure());
                    final SynchronousTransactionData td = new SynchronousTransactionData();
                    td.setFunction("INPUT");
                    td.setSourceId("GENERIC.OFS.PROCESS");
                    td.setTransactionId(recordId);
                    td.setVersionId("CURRENCY,REVAL.INPUT.NSB");
                    td.setResponseId("REVAL.UPLOAD*" + ccy);
                    transactionData.add(td);
                    System.out.println("Reval Rates Upload : Record updated : " + ccy);
                }
                catch (Exception e) {
                    System.out.println("Reval Rates Upload : Record insert error : " + recordId + " : " + e.getMessage());
                }
                break;
            }
            case "CHECK.ORD": {
                System.out.println("CHECK.ORD >>> " + id);
                final DataAccess da2 = new DataAccess((T24Context)this);
                final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(da2.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", id));
                final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable((T24Context)this);
                String rowDetailCcy = "";
                final TBoolean ordExistFlag = null;
                OfsRequestDetailRecord ordRec = null;
                for (int logRecRowDataCounter = 0; logRecRowDataCounter < logrec.getRowDetail().size(); ++logRecRowDataCounter) {
                    rowDetailCcy = logrec.getRowDetail().get(logRecRowDataCounter).toString().split("\\,")[0];
                    System.out.println("Exchange Rates Upload : Fetch row detail : " + rowDetailCcy);
                    rowDetailCcy = "REVAL.UPLOAD*" + rowDetailCcy;
                    System.out.println("Exchange Rates Upload : Fetch row detail from ORD : " + rowDetailCcy);
                    ordRec = da2.getRequestResponse(rowDetailCcy, ordExistFlag);
                    logrec.setOfsIn(ordRec.getMsgIn(), logRecRowDataCounter);
                    System.out.println("Exchange Rates Upload : Set OFS Out");
                    logrec.setOfsOut(ordRec.getMsgOut(), logRecRowDataCounter);
                    System.out.println("Exchange Rates Upload : Set OFS Out");
                    try {
                        logtble.write((CharSequence)id, logrec);
                    }
                    catch (Exception e3) {
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
}
