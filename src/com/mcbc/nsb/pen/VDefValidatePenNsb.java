package com.mcbc.nsb.pen;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CommonUtilsNsb.VConsumeWebservicesNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefValidatePenNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);
    List<String> returnIds = new ArrayList<>();
    String swaggerUrl;
    
    String mobileNumber = "";
    String penName = "";
        
    String ecpAccountNumber;
    String ecpMobileNumber;
    String ecpPenName;
    String ecpPenUrl;
    String ecpValidationCode;

    String penerrorCode;
    String penerrorMessage;
    String penResponseCode;
    String penResponseMsg;

    static URL url;
    static HttpURLConnection connection;
    static InputStream responseStream;
    static JSONObject json;
    static StringBuilder sb;
    
    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        getParamValues();
        System.out.println("getJsonDetails  59  defaultFieldValuesOnHotField");
        
        EbPenNameDetailsNsbRecord epndnRec = new EbPenNameDetailsNsbRecord(currentRecord);
        mobileNumber = epndnRec.getMobileNo().getValue();
        penName = epndnRec.getPenName().getValue();
        System.out.println("getJsonDetails  64  mobileNumber  :  " + mobileNumber);
        System.out.println("getJsonDetails  65  penName  :  " + penName);
        
        if ((mobileNumber.isEmpty()) || (mobileNumber.equals(null))) {
           throw new T24CoreException("", "EB-PEN.MOBILE.MISSING"); 
        }
        if ((penName.isEmpty()) || (penName.equals(null))) {
            throw new T24CoreException("", "EB-PEN.NAME.MISSING");
        }
        System.out.println("getJsonDetails  73  mobileNumber  :  " + mobileNumber);
        System.out.println("getJsonDetails  74  penName  :  " + penName);
        
        epndnRec = validatePenName(mobileNumber, penName, epndnRec);
        System.out.println("getJsonDetails  80  epndnRec  :  " + epndnRec);
        System.out.println("getJsonDetails  81  mobileNumber  :  " + mobileNumber);
        System.out.println("getJsonDetails  82  penName  :  " + penName);
        
        currentRecord.set(epndnRec.toStructure());
    }

    private EbPenNameDetailsNsbRecord validatePenName(String mobileNumber, String penName, EbPenNameDetailsNsbRecord epndnRec){
        
        swaggerUrl = ecpPenUrl + "mobileNumber=" + mobileNumber + "&penName=" + penName;
        
        System.out.println("getJsonDetails  92  swaggerUrl  :  " + swaggerUrl);
        
        String penStringJson = getJsonDetails(swaggerUrl);
        System.out.println("getJsonDetails  95  penStringJson  :  " + penStringJson);
        if(penStringJson.charAt(0) != '['){
            System.out.println("ConsumeSwaggerToT24Nsb  57  : result  :  " + penStringJson);
            penStringJson = "["+penStringJson+"]";
        }
        JSONArray penJson = new JSONArray(penStringJson);
        System.out.println("getJsonDetails  97  penJson  :  " + penJson.toString());
        
        try {
            // penResponseCode = penStringJson.get("responseCode").toString();
            System.out.println("getJsonDetails  115  penJson  :  " + penJson.toString());
            penResponseCode = penJson.getJSONObject(0).get("responseCode").toString();
        } catch (Exception e) {
            String errorCode = penJson.toString().split("\\*")[0];
            String errorMessage = penJson.toString().split("\\*")[1];
            if (errorCode.equals("[")){
                errorCode = errorCode.substring(1);
            }
            if (errorMessage.equals("[")){
                errorMessage = errorMessage.substring(1);
            }
            throw new T24CoreException(errorMessage, errorCode);
        }
        
        try {
            // penResponseCode = penStringJson.get("responseMsg").toString();
            penResponseMsg = penJson.getJSONObject(0).get("responseMsg").toString();
        } catch (Exception e) {
            String errorCode = penJson.toString().split("\\*")[0];
            String errorMessage = penJson.toString().split("\\*")[1];
            if (errorCode.equals("[")){
                errorCode = errorCode.substring(1);
            }
            if (errorMessage.equals("[")){
                errorMessage = errorMessage.substring(1);
            }
            throw new T24CoreException(errorMessage, errorCode);
        }

        try {
            // penResponseCode = penStringJson.get("errorCode").toString();
            penerrorCode = penJson.getJSONObject(0).get("errorCode").toString();
        } catch (Exception e) {
            penerrorCode = "";
        }

        try {
            // penResponseCode = penStringJson.get("errorMessage").toString();
            penerrorMessage = penJson.getJSONObject(0).get("errorMessage").toString();
        } catch (Exception e) {
            penerrorMessage = "";
        }
        
        if (penerrorCode.isEmpty()) {
            // returnIds.add(penRequestID + "*" + penResponseID + "*" +
            // penMobileNumber + "*" + penPenName + "*" + penResponseCode +
            // "*" + penResponseMsg);
            returnIds.add(penResponseCode + "*" + penResponseMsg);
        } else {
            throw new T24CoreException(penerrorCode, penerrorMessage);
        }
        
        epndnRec.setPenResponseCode(penResponseCode);
        epndnRec.setPenResponseMessage(penResponseMsg);
        
        return epndnRec;
    }
 
    public void getParamValues() {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("PEN.NAME.NSB", new String[] { "ENQ.SELECTION.FIELD", "PEN.URL", "VALIDATION.CODE" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);

        ecpAccountNumber = ParamConfig.get("PEN.NAME.NSB").get("ENQ.SELECTION.FIELD").get(0).getValue();
        ecpMobileNumber = ParamConfig.get("PEN.NAME.NSB").get("ENQ.SELECTION.FIELD").get(1).getValue();
        ecpPenName = ParamConfig.get("PEN.NAME.NSB").get("ENQ.SELECTION.FIELD").get(2).getValue();
        ecpPenUrl = ParamConfig.get("PEN.NAME.NSB").get("PEN.URL").get(0).getValue();
    }
    
    public String getJsonDetails(String swaggerUrl) {
        System.out.println("getJsonDetails  156  swaggerUrl  :  " + swaggerUrl);
        VConsumeWebservicesNsb webserviceCall = new VConsumeWebservicesNsb();
        String jsonArr = null;
        
        try {
            System.out.println("getJsonDetails  161  swaggerUrl  :  " + swaggerUrl);
            jsonArr = webserviceCall.ConsumeSwaggerToT24Nsb(swaggerUrl);
            System.out.println("getJsonDetails  163  jsonArr  :  " + jsonArr);
        } catch (Exception e) {
            System.out.println("getJsonDetails  165  jsonArr  :  ");
            throw new T24CoreException("", "Error in Connection");
        }

        System.out.println("getJsonDetails  168  jsonArr  :  " + jsonArr);
        return jsonArr;
    }
}
