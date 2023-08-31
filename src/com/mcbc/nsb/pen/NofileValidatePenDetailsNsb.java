package com.mcbc.nsb.pen;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


import org.json.JSONArray;
import org.json.simple.JSONObject;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.mcbc.nsb.CommonUtilsNsb.VConsumeWebservicesNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class NofileValidatePenDetailsNsb extends Enquiry {

    DataAccess dataObj = new DataAccess(this);
    List<String> returnIds = new ArrayList<>();
    String swaggerUrl;

    String accountNumber;
    String mobileNumber;
    String penName;

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
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        getParamValues();

        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        while (fcIter.hasNext()) {
            FilterCriteria filterCriteriaNext = fcIter.next();
            String filterCriteriaValue = filterCriteriaNext.getFieldname();
            /*
             * if (filterCriteriaValue.equals(ecpAccountNumber)) { accountNumber
             * = filterCriteriaNext.getValue(); }
             */
            if (filterCriteriaValue.equals(ecpMobileNumber)) {
                mobileNumber = filterCriteriaNext.getValue();
            }
            if (filterCriteriaValue.equals(ecpPenName)) {
                penName = filterCriteriaNext.getValue();
            }
        }

        // if ((accountNumber.isEmpty()) || (mobileNumber.isEmpty()) ||
        // (penName.isEmpty())) {
        if ((mobileNumber.isEmpty()) || (penName.isEmpty())) {
            throw new T24CoreException("", "All Selection fields are mandatory");
        } else {
            swaggerUrl = ecpPenUrl + "mobileNumber=" + mobileNumber + "&penName=" + penName;
        }

        System.out.println("getJsonDetails  92  swaggerUrl  :  " + swaggerUrl);
        
        String penStringJson = getJsonDetails(swaggerUrl);
        System.out.println("getJsonDetails  95  penStringJson  :  " + penStringJson);
        if(penStringJson.charAt(0) != '['){
            System.out.println("ConsumeSwaggerToT24Nsb  57  : result  :  " + penStringJson);
            penStringJson = "["+penStringJson+"]";
        }
        JSONArray penJson = new JSONArray(penStringJson);
        System.out.println("getJsonDetails  97  penJson  :  " + penJson.toString());
        
        // JSONObject penStringJson = getPenJsonDetails(swaggerUrl);

        /*
         * String penRequestID = penJson.get("RequestID").toString(); String
         * penResponseID = penJson.get("ResponseID").toString(); String
         * penMobileNumber = penJson.get("MobileNumber").toString(); String
         * penPenName = penJson.get("PEN").toString();
         */
        // "requestID":"20180102608300009","moblieNumber":null,"pen":"pen02","responseID":"6733"

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

        return returnIds;

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