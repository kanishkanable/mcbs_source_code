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
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
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
import java.util.Calendar;
import java.util.List;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import java.time.Instant;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.temenos.t24.api.hook.system.ServiceLifecycle;

public class UploadRevalRates extends ServiceLifecycle {
    Timestamp timeStampObj;
    Instant instantObj;
    public static final int RANDMIN = 10000;
    public static final int RANDMAX = 99999;

    public UploadRevalRates() {
        this.timeStampObj = null;
        this.instantObj = null;
    }

    final DataAccess da = new DataAccess(this);
    List<String> rowCountarr = new ArrayList<String>();
    final Session session = new Session(this);
    Calendar c = Calendar.getInstance();
    SimpleDateFormat dateformat = new SimpleDateFormat("yyMMddhhmmssSSSS");
    String appendOrdId = dateformat.format(c.getTime());
    List<String> OrdIdArr = new ArrayList<String>();

    final String today = "";
    final String currUser = session.getUserId();
    String recordId = null;

    final String outPutLocation = this.getCurrencyUploadPath();
    final File dir = new File(outPutLocation);
    final File[] files = dir.listFiles();
    File lastModifiedFile = files[0];

    final String fileFullName = getLastModifiedFile(lastModifiedFile);
    final Path filePath = Paths.get(fileFullName, new String[0]);
    final String getFileName = filePath.getFileName().toString();

    // final String filename1 = lastModifiedFile.getPath();
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

        System.out.println("Latest 73 file " + lastModifiedFile);

        final String s = controlList.get(0);
        switch (s) {
        case "PROCESS.OFS": {
            try {
                final BufferedReader read = new BufferedReader(new FileReader(fileFullName));
                String line = null;
                while ((line = read.readLine()) != null) {
                    rowCountarr.add(line);
                }
                read.close();
            } catch (Exception e) {
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
        final Map<String, Map<String, List<TField>>> ParamConfig = (Map<String, Map<String, List<TField>>>) config
                .GetParamValue(da);
        final String fileGenPath = ParamConfig.get("REVALRATE.FILE.GEN").get("IN.PATH").get(0).getValue();
        List<String> path1 = null;
        System.out.println("Ready to select path");

        System.out.println("Ready to select path from table");
        path1 = (List<String>) da.selectRecords("", "EB.FILE.UPLOAD.TYPE", "", " WITH @ID EQ CURRENCY.REVAL.RATE");
        System.out.println("Path: " + path1);
        final EbFileUploadTypeRecord fileUploadType = new EbFileUploadTypeRecord(
                da.getRecord("EB.FILE.UPLOAD.TYPE", "CURRENCY.REVAL.RATE"));
        final String uploadPathREM = fileUploadType.getUploadDir().toString();
        System.out.println("Reval rates file upload path retrieved1 " + uploadPathREM);
        final String uploadPath = fileGenPath + "/" + uploadPathREM;
        System.out.println("Reval rates file upload path retrieved2 " + uploadPath);
        return uploadPath;
    }

    public boolean validateFileName(final String incomingFileName) {

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

    
    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
       
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
                
                recordId = ccy;

                CurrencyRecord currRec = null;
                final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(
                        da.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", getFileName));
                try {
                    currRec = new CurrencyRecord(da.getRecord("CURRENCY", recordId)); 
                    System.out.println("Exchange Rates Upload : recordId  186 : " + recordId);
                } catch (Exception e2) {
                    System.out
                            .println("Exchange Rates Upload : recordId  188 error currency doesnt exist : " + recordId);
                    updateErrorLogTable(ccy, logrec);
                    // currRec = new CurrencyRecord();
                    System.out.println("Reval Rates Upload : Existing record initialized for update : " + recordId);
                }

                for (CurrencyMarketClass ccyMktClass : currRec.getCurrencyMarket()){
                    System.out.println("Exchange Rates Upload : recordId  198 : " + ccyMktClass.toString());
                    System.out.println("Exchange Rates Upload : recordId  341 : " + currRec.getCurrencyMarket().toString());
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("1")) && (!revalRate1.equals("NA"))){
                        ccyMktClass.setRevalRate(revalRate1);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + revalRate1);
                        continue;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("5")) && (!revalRate2.equals("NA"))){
                        ccyMktClass.setRevalRate(revalRate2);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + revalRate2);
                        continue;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("10")) && (!revalRate3.equals("NA"))){
                        ccyMktClass.setRevalRate(revalRate3);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + revalRate3);
                        continue;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("15")) && (!revalRate4.equals("NA"))){
                        ccyMktClass.setRevalRate(revalRate4);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + revalRate4);
                        continue;
                    }
                    if ((ccyMktClass.getCurrencyMarket().getValue().equals("20")) && (!revalRate5.equals("NA"))){
                        ccyMktClass.setRevalRate(revalRate5);
                        System.out.println("Exchange Rates Upload : buyRate1  202 : " + revalRate5);
                        continue;
                    }
                }

                currRec.getLocalRefField("L.FILE.NAME").setValue(getFileName);
                System.out.println("Reval Rates Upload : Record set for add or update : " + recordId);
                records.add(currRec.toStructure());
                String ordId = ccy + "-" + getFileName ;
                System.out.println("Exchange Rates Upload : currRec  403 : " + ordId);
                setOrdId(ordId, logrec);
                final TransactionData td = new TransactionData();
                td.setFunction("INPUT");
                td.setSourceId("GENERIC.OFS.PROCESS");
                td.setTransactionId(recordId);
                td.setVersionId("CURRENCY,REVAL.INPUT.NSB");
//                td.setResponseId("REVAL.UPLOAD*" + ccy);
                transactionData.add(td);
                System.out.println("Reval Rates Upload : Record updated : " + ccy);
            } catch (Exception e) {
                System.out.println("Reval Rates Upload : Record insert error : " + recordId + " : " + e.getMessage());
            }
            break;
        }
        case "CHECK.ORD": {
            System.out.println("CHECK.ORD >>> " + id);

            final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord(
                    da.getRecord("EB.CCY.RATE.UPLOAD.LOG.NSB", id));
            final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);
            
            final TBoolean ordExistFlag = null;
            OfsRequestDetailRecord ordRec = null;
            int ordCount = 0;
            System.out.println("CHECK.ORD >>> 46  :  " + id);
            for (TField ordIdTfield : logrec.getOrdId()) {
                System.out.println("CHECK.ORD >>> 48  : id  :  " + ordIdTfield);
                String ordId = ordIdTfield.getValue();
                System.out.println("CHECK.ORD >>> 50  :  " + ordId);
                ordRec = da.getRequestResponse(ordId, ordExistFlag);
                System.out.println("CHECK.ORD >>> 52  :  " + id);

                logrec.setOfsIn(ordRec.getMsgIn(), ordCount);
                System.out.println("CHECK.ORD >>> 55  : in  :  " + ordRec.getMsgIn());
                logrec.setOfsOut(ordRec.getMsgOut(), ordCount);
                System.out.println("CHECK.ORD >>> 57  : out :  " + ordRec.getMsgOut());

                try {
                    System.out.println("CHECK.ORD >>> 60  :  " + id);
                    logtble.write(id, logrec);
                } catch (Exception e3) {
                    System.out.println("Exchange Rates Upload : Unable to write : " + ordId);
                }
            }
/*            for (int logRecRowDataCounter = 0; logRecRowDataCounter < logrec.getRowDetail()
                    .size(); ++logRecRowDataCounter) {
                rowDetailCcy = logrec.getRowDetail().get(logRecRowDataCounter).toString().split("\\,")[0];
                System.out.println("Exchange Rates Upload : Fetch row detail : " + rowDetailCcy);
                rowDetailCcy = "REVAL.UPLOAD*" + rowDetailCcy;
                System.out.println("Exchange Rates Upload : Fetch row detail from ORD : " + rowDetailCcy);
                ordRec = da.getRequestResponse(rowDetailCcy, ordExistFlag);
                logrec.setOfsIn(ordRec.getMsgIn(), logRecRowDataCounter);
                System.out.println("Exchange Rates Upload : Set OFS Out");
                logrec.setOfsOut(ordRec.getMsgOut(), logRecRowDataCounter);
                System.out.println("Exchange Rates Upload : Set OFS Out");
                try {
                    logtble.write((CharSequence) id, logrec);
                } catch (Exception e3) {
                    System.out.println("Exchange Rates Upload : Unable to write : " + id);
                }
            }
*/            System.out.println("Exchange Rates Upload : Fetch row detail size : " + logrec.getRowDetail().size());
            break;
        }
        default:
            break;
        }
    }

    private void updateErrorLogTable(String ccy, EbCcyRateUploadLogNsbRecord logrec) {

        int rowCount = 0;
        for (TField rowDetailValue : logrec.getRowDetail()) {
            String rowCcy = rowDetailValue.getValue().split("\\,")[0];
            if (rowCcy.equals(ccy)) {
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

    private void setOrdId(String ordId, EbCcyRateUploadLogNsbRecord logrec) {
        System.out.println("Exchange Rates Upload : setOrdId  483 : " + logrec.getOrdId().size());
        logrec.setOrdId(ordId, logrec.getOrdId().size());

        System.out.println("Exchange Rates Upload : setOrdId  485 : " + logrec.toString());
        final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);
        System.out.println("Exchange Rates Upload : setOrdId  487 : ");
        try {
            System.out.println("Exchange Rates Upload : setOrdId  489 : ");
            logtble.write((CharSequence) getFileName, logrec);
            System.out.println("Exchange Rates Upload : setOrdId  491 : ");
        } catch (Exception e3) {
            System.out.println("Exchange Rates Upload : setOrdId  493 : ");
        }
    }

    private String getLastModifiedFile(File lastModifiedFile) {

        for (int i = 1; i < files.length; ++i) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }

        return lastModifiedFile.toString();
    }
}
