package com.mcbc.nsb.CommonUtilsNsb;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcommonparamnsb.EbCommonParamNsbRecord;
import com.temenos.t24.api.tables.ebcommonparamnsb.ParamNameClass;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GetParamValueNsb {

    Map<String, String[]> Config = new HashMap<String, String[]>();

    public void AddParam(String RecordName, String[] ParameterName) {
        this.Config.put(RecordName, ParameterName);
    }
    
    public Map<String, String[]> getconfig() {
        return this.Config;
    }

    public Map<String, Map<String, List<TField>>> GetParamValue(DataAccess da) {
        //1st String -- record ; 2nd --- Paramname 3rd-- ParamValue
        Map<String, Map<String, List<TField>>> Output = new HashMap<String, Map<String, List<TField>>>();
        Map<String, List<TField>> ParamOutput = new HashMap<String, List<TField>>();
        String RecordId = "";

        Iterator<Entry<String, String[]>> ConfigIterator = this.Config.entrySet().iterator();
        while (ConfigIterator.hasNext()) {
            Entry<String, String[]> config = ConfigIterator.next();

            RecordId = (String) config.getKey();
            String[] Parameters = (String[]) config.getValue();
            TStructure record = da.getRecord("EB.COMMON.PARAM.NSB", RecordId);
            EbCommonParamNsbRecord ecp = new EbCommonParamNsbRecord(record);
            ParamOutput = new HashMap<String, List<TField>>();
            for (ParamNameClass p : ecp.getParamName()) {
                for (String pn : Parameters) {
                    if (p.getParamName().toString().trim().equals(pn.trim()))
                        ParamOutput.put(pn.trim(), p.getParamValue());
                }
            }

            Output.put(RecordId, ParamOutput);
        }
        return Output;
    }
}

// BELOW EXAMPLE FOR CALLING EB.COMON.PARAM

/*
 DataAccess DataObj = new DataAccess(this);
 uGetParamValueNsb Config = new uGetParamValueNsb();
 Config.AddParam("CUSTOMER", new String[] { "TARGET.EPF", "LEGAL.EXP.MAND" });
 
 //  Here CUSTOMER is record ID in EB.COMMON.PARAM 
 //  "TARGET.EPF", "LEGAL.EXP.MAND" are the PARAM NAMES in CUSTOMER RECORD.
 //  if we need data from multiple records just add data as below.
 
 Config.AddParam("ACCOUNT", new String[] { "TEST1", "TEST2"});
 Config.AddParam("TELLER", new String[] { "TEST3", "TEST4"});
  
 //  below is how we retrieve the data
  
 Map<String, Map<String, List<TField>>> ParamConfig =
 Config.GetParamValue(DataObj); 
  
 //Below command to retrieve the data
 String EcpTargetEpf =
 ParamConfig.get("CUSTOMER").get("TARGET.EPF").get(0).getValue(); 
 
 String EcpLegalDocName =
 ParamConfig.get("CUSTOMER").get("LEGAL.EXP.MAND").get(0).getValue(); 
  
 String LegDocBirthCertificate =
 ParamConfig.get("ACCOUNT").get("TEST1").get(0).getValue();
 String LegDocBirthCertError =
 ParamConfig.get("ACCOUNT").get("TEST2").get(0).getValue();
  
 // get(0) is like a loop, if we have multiple sub values in PARAM.VALUE
 // we loop like get(0), get(1),....
 
  
 */