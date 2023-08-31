// 
// Decompiled by Procyon v0.5.36
// 

package com.mcbc.nsb.exchangerates;

import com.temenos.t24.api.records.ebfileuploadtype.EbFileUploadTypeRecord;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.t24.api.system.DataAccess;
import java.nio.file.Path;
import com.temenos.t24.api.records.ebfileupload.EbFileUploadRecord;
import com.temenos.t24.api.system.Session;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbTable;
import com.temenos.tafj.api.client.impl.T24Context;
import com.temenos.t24.api.tables.ebccyrateuploadlognsb.EbCcyRateUploadLogNsbRecord;
import java.util.ArrayList;
//import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.io.File;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import java.util.List;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;

import java.time.Instant;
import java.security.Timestamp;
import com.temenos.t24.api.hook.system.RecordLifecycle;

public class VAuthRevalRates extends RecordLifecycle {

    final DataAccess da = new DataAccess((T24Context) this);
    
    Timestamp timeStampObj;
    Instant instantObj;
    public static final int RANDMIN = 10000;
    public static final int RANDMAX = 99999;

    String currency = "";
    String midValue1 = "";
    String midValue2 = "";
    String midValue3 = "";
    String midValue4 = "";
    String midValue5 = "";

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    final Date currentDate = new Date();

    final Session session = new Session((T24Context) this);
    final String currUser = session.getUserId();

    BufferedReader read = null;

    public VAuthRevalRates() {
        this.timeStampObj = null;
        this.instantObj = null;
    }

    public void postUpdateRequest(final String application, final String currentRecordId,
            final TStructure currentRecord, final List<TransactionData> transactionData,
            final List<TStructure> currentRecords, final TransactionContext transactionContext) {
        // final String id = currentRecordId;

        System.out.println("Reval Rates Upload : Current processing record id : " + currentRecordId);

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
        final String filename2 = lastModifiedFile.getAbsolutePath();
        System.out.println("Path " + filename1);
        System.out.println("Absolute Path " + filename2);
        try {
            read = new BufferedReader(new FileReader(filename2));
        } catch (Exception e1) {
            throw new T24CoreException(filename2, "File Does not exist");
        }
        final List<String> rowCountarr = new ArrayList<String>();
        String line = null;
        String rowData = null;
        final EbCcyRateUploadLogNsbRecord logrec = new EbCcyRateUploadLogNsbRecord();
        final EbCcyRateUploadLogNsbTable logtble = new EbCcyRateUploadLogNsbTable(this);
        try {
            while ((line = read.readLine()) != null) {
                final String[] tmp = line.split(",");
                for (int i = 0; i < tmp.length; i++) {
                    String midValue = checkNullValue(tmp[i]);
                    System.out.println("Ready to write 23 : " + midValue);
                    
                    if (i == 0) {
                        rowData = midValue;
                    } else {
                        rowData = rowData + "," + midValue;
                    }
                }
/*                currency = tmp[0];
                midValue1 = checkNullValue(tmp[1]);
                midValue2 = checkNullValue(tmp[2]);
                midValue3 = checkNullValue(tmp[3]);
                midValue4 = checkNullValue(tmp[4]);
                midValue5 = checkNullValue(tmp[5]);

                System.out.println(
                        "Ready to write " + currency + midValue1 + midValue2 + midValue3 + midValue4 + midValue5);
                rowData = "";
                rowData = String.valueOf(currency) + "," + midValue1 + "," + midValue2 + "," + midValue3 + ","
                        + midValue4 + "," + midValue5;
*/                
                rowCountarr.add(rowData);
                logrec.setId(currentRecordId);
            }
        } catch (Exception e) {
            throw new T24CoreException(filename2, "File Does not exist");
        }

        final Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        final EbFileUploadRecord fileUpload = new EbFileUploadRecord(currentRecord);
        final String fileName = fileUpload.getSystemFileName().toString();
        final String uploadStatus = fileUpload.getStatus().toString();
        logrec.setId((CharSequence) fileName);
        for (int j = 0; j < rowCountarr.size(); ++j) {
            logrec.setRowDetail((CharSequence) rowCountarr.get(j), j);
        }
        logrec.setFileName((CharSequence) fileName);
        logrec.setFileUploadStatus((CharSequence) uploadStatus);
        logrec.setUploadUser((CharSequence) currUser);
        logrec.setUploadDateTime((CharSequence) dateFormat.format(currentDate));
        System.out.println("File name " + fileName);
        try {
            logtble.write((CharSequence) fileName, logrec);
        } catch (Exception e) {
            throw new T24CoreException(fileName, "Write Failed");
        }
        System.out.println("Ready to write " + currency + midValue4 + midValue5);
        try {
            read.close();
        } catch (Exception e) {
            throw new T24CoreException(fileName, "File Does not exist");
        }
    }

    public String getCurrencyUploadPath() {
        final GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("REVALRATE.FILE.GEN", new String[] { "IN.PATH" });
        final Map<String, Map<String, List<TField>>> ParamConfig = (Map<String, Map<String, List<TField>>>) config
                .GetParamValue(da);
        final String fileGenPath = ParamConfig.get("REVALRATE.FILE.GEN").get("IN.PATH").get(0).getValue();
        
        final EbFileUploadTypeRecord fileUploadType = new EbFileUploadTypeRecord(
                da.getRecord("EB.FILE.UPLOAD.TYPE", "CURRENCY.REVAL.RATE"));
        final String uploadPathREM = fileUploadType.getUploadDir().toString();
        System.out.println("Reval rates file upload path retrieved1 " + uploadPathREM);
        final String uploadPath = String.valueOf(fileGenPath) + "/" + uploadPathREM;
        System.out.println("Reval rates file upload path retrieved2 " + uploadPath);
        return uploadPath;
    }

    private String checkNullValue(String midvalue) {
        if (midvalue == null) {
            midvalue = "NA";
        }
        return midvalue;
    }
}
