package com.mcbc.nsb.pen;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TBoolean;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document me!
 *
 * @author Ravindra Basnayake
 * Routine to generate the output file for PEN Interface
 * 
 * @author kalyan pappu
 * Modified code based on changes to file format
 * 
 */
public class GetPenDetails extends ServiceLifecycle {
    
    DataAccess da = new DataAccess(this);
    GetParamValueNsb config = new GetParamValueNsb();
    String fileGenPath;
    String fileName;
    String outString = "";
    List<String> penRecIds = null;
    
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
      List<String> recIds = new ArrayList<>();
      recIds.add("PEN.DETAILS");
      return recIds;
    }
    
    
    @Override
    public void inputRecord(String id, ServiceData serviceData, String controlItem, TBoolean setZeroAuth, List<String> versionNames, List<String> recordIds, List<TStructure> records) {
//      String fileName = "7779";
        System.out.println("inputRecord  48  :  ");
      try {
        getOutputGenPath();
        System.out.println("inputRecord  51  :  ");
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        String formattedDate = formatter.format(LocalDate.now());
        System.out.println("inputRecord  54  : formattedDate  :  " + formattedDate);
        fileName = String.valueOf(fileName) + "_" + formattedDate;
        System.out.println("inputRecord  56  : fileName  :  " + fileName);
        String outputPath = String.valueOf(fileGenPath) + "/" + fileName + ".txt";
        System.out.println("inputRecord  58  : outputPath  :  " + outputPath);
        File fileChecker = new File(outputPath);
        System.out.println("inputRecord  60  : formattedDate  :  ");
        if (fileChecker.exists() && fileChecker.isFile()) {
            System.out.println("inputRecord  62  : formattedDate  :  ");
          fileChecker.delete();
          System.out.println("inputRecord  64  : formattedDate  :  ");
        } 
        System.out.println("inputRecord  66  : formattedDate  :  ");
        
        BufferedWriter bWriter = new BufferedWriter(new FileWriter(outputPath, true));
        System.out.println("inputRecord  69  : formattedDate  :  ");
        penRecIds = da.selectRecords("", "EB.PEN.NAME.DETAILS.NSB", "", "");
        System.out.println("inputRecord  71  : penRecIds  :  " + penRecIds.toString());
        for (int i = 0; i < penRecIds.size(); i++) {
            System.out.println("inputRecord  73  : penRecIds  :  " + penRecIds.get(i));
          EbPenNameDetailsNsbRecord penRec = new EbPenNameDetailsNsbRecord(da.getRecord("EB.PEN.NAME.DETAILS.NSB", penRecIds.get(i)));
          System.out.println("inputRecord  75  : penRecIds  :  " + penRecIds.get(i));
          outString = String.valueOf(penRecIds.get(i)) + "|" + 
            penRec.getPenName() + "|" + 
            penRec.getAcctName() + "|" + 
            penRec.getMobileNo() + "|" + 
            penRec.getDateOfBirth() + "|" + 
            penRec.getStatus() + "|" + 
            penRec.getPenDateTime() + "|" + 
            penRec.getIdNumber();
          System.out.println("inputRecord  84  : outString  :  " + outString);
          bWriter.write(outString);
          System.out.println("inputRecord  86  : outString  :  " );
          bWriter.newLine();
          System.out.println("inputRecord  88  : outString  :  " );
/*          System.out.println("PEN data line written >> " + (String)penRecIds.get(i) + "|" + 
              penRec.getPenName() + "|" + 
              penRec.getAcctName() + "|" + 
              penRec.getMobileNo() + "|" + 
              penRec.getDateOfBirth() + "|" + 
              penRec.getStatus() + "|" + 
              penRec.getPenDateTime() + "|" + 
              penRec.getIdNumber());
*/
        } 
        bWriter.close();
      } catch (Exception genException) {
      } 
    }
    
    public void getOutputGenPath() {
      config.AddParam("PEN.FILE.GEN", new String[] { "OUT.PATH", "FILE.NAME" });
      Map<String, Map<String, List<TField>>> ParamConfig = config.GetParamValue(da);
      fileGenPath = ParamConfig.get("PEN.FILE.GEN").get("OUT.PATH").get(0).getValue();
      System.out.println("inputRecord  108  : fileGenPath  :  " + fileGenPath);
      fileName = ParamConfig.get("PEN.FILE.GEN").get("FILE.NAME").get(0).getValue();
      System.out.println("inputRecord  108  : fileName  :  " + fileName);
    }
  }





