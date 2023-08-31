package com.mcbc.nsb.pen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpengetfileidnsb.EbPenGetFileIdNsbTable;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BGeneratePenDetailsNsb extends ServiceLifecycle {

    DataAccess dataObj = new DataAccess(this);
    String fileGenPath;
    String fileName;
    String outString = "";
    List<String> penRecIds = null;

    DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
    BufferedWriter bWriter = null;

    @Override
    public void processSingleThreaded(ServiceData serviceData) {
        // TODO Auto-generated method stub

        List<String> penGenIdList = dataObj.selectRecords("", "EB.PEN.GET.FILE.ID.NSB", "", "");
        getParamValues();

        String formattedDate = formatter.format(LocalDate.now());
        System.out.println("inputRecord  48  : formattedDate  :  " + formattedDate);
        fileName = String.valueOf(fileName) + "_" + formattedDate;
        System.out.println("inputRecord  50  : fileName  :  " + fileName);
        String outputPath = String.valueOf(fileGenPath) + "/" + fileName + ".txt";
        System.out.println("inputRecord  52  : outputPath  :  " + outputPath);

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
        System.out.println("inputRecord  71  : formattedDate  :  ");

        for (String penGenId : penGenIdList) {
            System.out.println("inputRecord  74  : penRecIds  :  " + penGenId);
            EbPenNameDetailsNsbRecord penRec = new EbPenNameDetailsNsbRecord(
                    dataObj.getRecord("EB.PEN.NAME.DETAILS.NSB", penGenId));
            System.out.println("inputRecord  77  : penRecIds  :  " + penGenId);
            outString = penGenId + "|" + penRec.getPenName().getValue() + "|" + penRec.getAcctName().getValue() + "|"
                    + penRec.getMobileNo().getValue() + "|" + penRec.getDateOfBirth().getValue() + "|" + penRec.getStatus().getValue() + "|"
                    + penRec.getPenDateTime().getValue() + "|" + penRec.getIdNumber().getValue();
            System.out.println("inputRecord  81  : outString  :  " + outString);
            
            try {
                bWriter.write(outString);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  89  : bWriter.write(outString)  :  ");
            }
            System.out.println("inputRecord  91  : bWriter.write(outString)  :  ");
            try {
                bWriter.newLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  98  : bWriter.write(outString)  :  ");
            }
            System.out.println("inputRecord  100  : bWriter.write(outString)  :  ");
            
            EbPenGetFileIdNsbTable penFileIdTable = new EbPenGetFileIdNsbTable(this);
            try {
                penFileIdTable.delete(penGenId);
            } catch (T24IOException e) {
                // TODO Auto-generated catch block
                // Uncomment and replace with appropriate logger
                // LOGGER.error(e, e);
                System.out.println("inputRecord  109  : bWriter.write(outString)  :  ");
            }
            System.out.println("inputRecord  111  : bWriter.write(outString)  :  ");
        }
        
        try {
            bWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
            System.out.println("inputRecord  120  : bWriter.write(outString)  :  ");
        }
        System.out.println("inputRecord  122  : bWriter.write(outString)  :  ");
     
//        super.processSingleThreaded(serviceData);
    }

    public void getParamValues() {
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("PEN.FILE.GEN", new String[] { "OUT.PATH", "FILE.NAME" });
        Map<String, Map<String, List<TField>>> ParamConfig = config.GetParamValue(dataObj);

        fileGenPath = ParamConfig.get("PEN.FILE.GEN").get("OUT.PATH").get(0).getValue();
        System.out.println("inputRecord  108  : fileGenPath  :  " + fileGenPath);
        fileName = ParamConfig.get("PEN.FILE.GEN").get("FILE.NAME").get(0).getValue();
        System.out.println("inputRecord  108  : fileName  :  " + fileName);
    }
}
