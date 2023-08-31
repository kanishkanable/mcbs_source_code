package com.mcbc.nsb.pen;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.temenos.api.TBoolean;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;
import java.io.File;
import java.io.FileReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document me!
 *
 * @author Ravindra Basnayake
 *
 */
public class UploadPenDetails extends ServiceLifecycle {

    Timestamp timeStampObj = null;
    Instant instantObj = null;
    DataAccess dataObject = new DataAccess(this);
    List<String> recIds = new ArrayList<>();
    List<String> finalDataArray = new ArrayList<>();

    public static final int RANDMIN = 10000;
    public static final int RANDMAX = 99999;

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        System.out.println("*** getIds ***  :  ");
        String fileInPath = getPenInPath();
        System.out.println("*** getIds ***  : fileInPath 50 :  " + fileInPath);
        File folder = new File(fileInPath);
        System.out.println("*** getIds ***  : folder  : 52 " + folder);
        File[] listOfFiles = folder.listFiles();
        System.out.println("*** getIds ***  : listOfFiles 54 :  " + listOfFiles);
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println("*** getIds ***  : i  : 56 " + i);
            if (listOfFiles[i].isFile()) {
                System.out.println("*** getIds ***  : listOfFiles[i] 58  :  " + listOfFiles[i]);
                if (validateFileName(listOfFiles[i].getName().toLowerCase())) {
                    System.out.println("*** getIds ***  : validateFileName  : 60 " + validateFileName(listOfFiles[i].getName().toLowerCase()));   
                    recIds = getUploadRecs(fileInPath + "/" + listOfFiles[i].getName());
                    System.out.println("*** getIds ***  : recIds  :62   " + recIds);
                    try {
                        System.out.println("*** getIds ***  : recIds  : 64 " + fileInPath);
                        System.out.println("*** getIds ***  : recIds  : 64 " + listOfFiles[i].getName());
                        Files.move(Paths.get(fileInPath + "\\/" + listOfFiles[i].getName(), new String[0]),
                                Paths.get(fileInPath + "\\/BACKUP\\/" + listOfFiles[i].getName(), new String[0]),
                                new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                        System.out.println("*** getIds ***  : recIds  :  68 ");
                    } catch (Exception e) {
                        System.out.println("*** getIds ***  : recIds  :  70  ");
                    }
                    System.out.println("*** getIds ***  : recIds  : - 72 " + recIds);
                    return recIds;
                }
                System.out.println("*** getIds ***  : recIds  :  75 ");
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("*** getIds ***  : recIds  :  77 ");
            }
            System.out.println("*** getIds ***  : recIds  :  79 ");
        }
        System.out.println("*** getIds ***  : recIds  :  81 ");
        return recIds;
    }

    public String getPenInPath() {
        System.out.println("*** getIds ***  : ECP  :  86 ");
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("PEN.UPLOAD", new String[] { "IN.PATH" });
        Map<String, Map<String, List<TField>>> ParamConfig = config.GetParamValue(dataObject);
        String fileInPath = ParamConfig.get("PEN.UPLOAD").get("IN.PATH").get(0).getValue();
        System.out.println("*** getIds ***  : fileInPath  :  91 " + fileInPath);
        return fileInPath;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        String recordId = null;
        String ordId = null;
        
        System.out.println("*** getIds ***  : recIds  101  :  " + id);
        String status = id.split("\\*")[1];
        System.out.println("*** getIds ***  : status  103  :  " + status);
        String mobileNo = id.split("\\*")[3];
        System.out.println("*** getIds ***  : mobileNo  105  :  " + mobileNo);
        String accountNo = id.split("\\*")[4];
        System.out.println("*** getIds ***  : accountNo  107  :  " + accountNo);
        String penName = id.split("\\*")[5];
        System.out.println("*** getIds ***  : penName  109  :  " + penName);
        String nic = id.split("\\*")[6];
        System.out.println("*** getIds ***  : nic  111  :  " + nic);
        String cusName = id.split("\\*")[8];
        System.out.println("*** getIds ***  : cusName  113  :  " + cusName);
        
        String createdYear = id.split("\\*")[9].split("\\/")[2].substring(0, 4);
        System.out.println("*** getIds ***  : createdYear  115  :  " + createdYear);
        String createdMonth = id.split("\\*")[9].split("\\/")[1];
        System.out.println("*** getIds ***  : createdMonth  116  :  " + createdMonth);
        
        StringBuilder sbMonPad = new StringBuilder();
        for (int i = 0; i < 2 - createdMonth.length(); i++) {
            sbMonPad.append('0');
        }
        createdMonth = sbMonPad + createdMonth;

        String createdDate = id.split("\\*")[9].split("\\/")[0];
        System.out.println("*** getIds ***  : createdDate  126  :  " + createdDate);
        
        StringBuilder sbDatePad = new StringBuilder();
        for (int j = 0; j < 2 - createdDate.length(); j++) {
            sbDatePad.append('0');
        }
        createdDate = sbDatePad + createdDate;

        createdDate = String.valueOf(createdYear) + createdMonth + createdDate;

        recordId = accountNo;

        EbPenNameDetailsNsbRecord penRec = null;
        try {
            penRec = new EbPenNameDetailsNsbRecord(dataObject.getRecord("EB.PEN.NAME.DETAILS.NSB", recordId));
        } catch (Exception e) {
            penRec = new EbPenNameDetailsNsbRecord();
        }
        penRec.setStatus(status);
        penRec.setMobileNo(mobileNo);
        penRec.setPenName(penName);
        penRec.setId(nic);
        penRec.setIdNumber(nic);
        penRec.setAcctName(cusName);
        penRec.setPenDateTime(createdDate);

        records.add(penRec.toStructure());
        SynchronousTransactionData td = new SynchronousTransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(recordId);
        td.setVersionId("EB.PEN.NAME.DETAILS.NSB,UPLOAD.NSB");

        ordId = "PEN" + Long.toString(getTimeStamp().longValue()) + getRandomNumber();
        td.setResponseId(ordId);
        transactionData.add(td);

        TBoolean ordExistFlag = null;
        dataObject.getRequestResponse(ordId, ordExistFlag);
    }

    public boolean validateFileName(String incomingFileName) {
        if (!incomingFileName.endsWith(".csv")) {
            return false;
        }
        if (!incomingFileName.startsWith("6719_")) {
            return false;
        }
        return true;
    }

    public List<String> getUploadRecs(String fullFileName) {
        FileReader fileReader;
        CSVParser parser;
        CSVReader datainput;
        try {
            fileReader = new FileReader(fullFileName);
            parser = new CSVParserBuilder().withSeparator(',').build();
            datainput = new CSVReaderBuilder(fileReader).withSkipLines(3).withCSVParser(parser).build();

            String[] nextLine;
            while ((nextLine = datainput.readNext()) != null) {
                String dataLine = "";
                for (String eachCellValue : nextLine) {
                    if (dataLine.isEmpty()) {
                        dataLine = eachCellValue;
                    } else {
                        dataLine = dataLine + "*" + eachCellValue;
                    }
                }
                if (!dataLine.isEmpty()) {
                    finalDataArray.add(dataLine);
                }
            }

            return finalDataArray;
        } catch (Exception e) {
            return finalDataArray;
        }
    }

    public Long getTimeStamp() {
        this.timeStampObj = new Timestamp(System.currentTimeMillis());
        return Long.valueOf(this.timeStampObj.getTime());
    }

    public int getRandomNumber() {
        return (int) (Math.random() * 90000.0D + 10000.0D);
    }
}
