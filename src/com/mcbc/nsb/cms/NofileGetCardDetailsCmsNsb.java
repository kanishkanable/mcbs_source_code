package com.mcbc.nsb.cms;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class NofileGetCardDetailsCmsNsb extends Enquiry {

    DataAccess dataObj = new DataAccess(this);
    List<String> returnIds = new ArrayList<>();
    String swaggerUrl;

    String ecpAccountNumber;
    String ecpCmsUrl;
    String accountNumber;

    static URL url;
    static HttpURLConnection connection;
    static InputStream responseStream;
    static JSONObject json;
    static StringBuilder sb;

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        getParamValues();
        System.out.println("NofileValidatePenDetailsNsb  : 47  ");

        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        System.out.println("NofileValidatePenDetailsNsb  : 50  ");
        while (fcIter.hasNext()) {
            System.out.println("NofileValidatePenDetailsNsb  : 52 while  ");
            FilterCriteria filterCriteriaNext = fcIter.next();
            System.out.println("NofileValidatePenDetailsNsb  : 54   : " + filterCriteriaNext.getValue());
            String filterCriteriaValue = filterCriteriaNext.getFieldname();
            System.out.println("NofileValidatePenDetailsNsb  : 56   : " + filterCriteriaValue);
            if (filterCriteriaValue.equals(ecpAccountNumber)) {
                System.out.println("NofileValidatePenDetailsNsb  : 58   : " + ecpAccountNumber);
                accountNumber = filterCriteriaNext.getValue();
                System.out.println("NofileValidatePenDetailsNsb  : 60   : " + accountNumber);
            }
            System.out.println("NofileValidatePenDetailsNsb  : 72   : ");
        }
        if (accountNumber.isEmpty()) {
            System.out.println("NofileValidatePenDetailsNsb  : 77   : ");
            throw new T24CoreException("", "Account Number is mandatory");
        } else {
            swaggerUrl = ecpCmsUrl + "accountNumber=" + accountNumber;
            // "http://192.168.132.42:7080/esb/cms/v1/getCardDetailsCms?accountNumber="
        }

        System.out.println("NofileValidatePenDetailsNsb  : 84   : ");
        String cmsJsonString = getJsonDetails(swaggerUrl);
        if (cmsJsonString.charAt(0) != '[') {
            System.out.println("ConsumeSwaggerToT24Nsb  57  : result  :  " + cmsJsonString);
            cmsJsonString = "[" + cmsJsonString + "]";
        }
        JSONArray cmsJson = new JSONArray(cmsJsonString);

        System.out.println("NofileValidatePenDetailsNsb  : 87   : ");

        // (JSONObject) jsonArr.get(0)).get("CardNumber");
        for (int i = 0; i < cmsJson.length(); i++) {

            try {
                System.out.println("NofileValidatePenDetailsNsb  : 88   : ");
                String cmsDescription = ((JSONObject) cmsJson.get(i)).get("Description").toString();
                System.out.println("NofileValidatePenDetailsNsb  : 90   : " + cmsDescription);
                String cmsStatus = ((JSONObject) cmsJson.get(i)).get("Status").toString();
                System.out.println("NofileValidatePenDetailsNsb  : 92   : " + cmsStatus);

                if (!cmsDescription.equals(null)) {
                    System.out.println("NofileValidatePenDetailsNsb  : 92-96   : ");
                    throw new T24CoreException(cmsDescription, "Status : " + cmsStatus);

                }
            } catch (Exception e) {
                String cmscardNumber = ((JSONObject) cmsJson.get(i)).get("CardNumber").toString();
                System.out.println("NofileValidatePenDetailsNsb cmscardNumber : 96   : " + cmscardNumber);
                if (cmscardNumber.equals("{}")) {
                    cmscardNumber = "";
                }

                String cmsAccountNumber = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("LinkedAccounts"))
                        .get("AccountNumber").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAccountNumber : 96   : " + cmsAccountNumber);
                if (cmsAccountNumber.equals("{}")) {
                    cmsAccountNumber = "";
                }

                String cmsAccountType = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("LinkedAccounts"))
                        .get("AccountType").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAccountType : 96   : " + cmsAccountType);
                if (cmsAccountType.equals("{}")) {
                    cmsAccountType = "";
                }

                String cmsAccountStatus = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("LinkedAccounts"))
                        .get("AccountStatus").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAccountStatus : 96   : " + cmsAccountStatus);
                if (cmsAccountStatus.equals("{}")) {
                    cmsAccountStatus = "";
                }

                String cmsIssuedDate = ((JSONObject) cmsJson.get(i)).get("IssuedDate").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsIssuedDate : 96   : " + cmsIssuedDate);
                if (cmsIssuedDate.equals("{}")) {
                    cmsIssuedDate = "";
                }

                String cmsActivationDate = ((JSONObject) cmsJson.get(i)).get("ActivationDate").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsActivationDate : 96   : " + cmsActivationDate);
                if (cmsActivationDate.equals("{}")) {
                    cmsActivationDate = "";
                }

                String cmsCardStatus = ((JSONObject) cmsJson.get(i)).get("CardStatus").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsCardStatus : 96   : " + cmsCardStatus);
                if (cmsCardStatus.equals("{}")) {
                    cmsCardStatus = "";
                }

                String cmsCustomerID = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("CustomerID").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsCustomerID : 96   : " + cmsCustomerID);
                if (cmsCustomerID.equals("{}")) {
                    cmsCustomerID = "";
                }

                String cmsCustomerName = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("CustomerName").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsCustomerName : 96   : " + cmsCustomerName);
                if (cmsCustomerName.equals("{}")) {
                    cmsCustomerName = "";
                }

                String cmsDOB = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails")).get("DOB")
                        .toString();
                System.out.println("NofileValidatePenDetailsNsb cmsDOB : 96   : " + cmsDOB);
                if (cmsDOB.equals("{}")) {
                    cmsDOB = "";
                }

                String cmsAddressLine1 = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("AddressLine1").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAddressLine1 : 96   : " + cmsAddressLine1);
                if (cmsAddressLine1.equals("{}")) {
                    cmsAddressLine1 = "";
                }

                String cmsAddressLine2 = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("AddressLine2").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAddressLine2 : 96   : " + cmsAddressLine2);
                if (cmsAddressLine2.equals("{}")) {
                    cmsAddressLine2 = "";
                }

                String cmsAddressLine3 = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("AddressLine3").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsAddressLine3 : 96   : " + cmsAddressLine3);
                if (cmsAddressLine3.equals("{}")) {
                    cmsAddressLine3 = "";
                }

                String cmsCity = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails")).get("City")
                        .toString();
                System.out.println("NofileValidatePenDetailsNsb cmsCity : 96   : " + cmsCity);
                if (cmsCity.equals("{}")) {
                    cmsCity = "";
                }

                String cmsEmailId = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails")).get("EmailId")
                        .toString();
                System.out.println("NofileValidatePenDetailsNsb cmsEmailId : 96   : " + cmsEmailId);
                if (cmsEmailId.equals("{}")) {
                    cmsEmailId = "";
                }

                String cmsMobileNumber = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails"))
                        .get("MobileNumber").toString();
                System.out.println("NofileValidatePenDetailsNsb cmsMobileNumber : 96   : " + cmsMobileNumber);
                if (cmsMobileNumber.equals("{}")) {
                    cmsMobileNumber = "";
                }

                String cmsBranch = ((JSONObject) ((JSONObject) cmsJson.get(i)).get("CustomerDetails")).get("Branch")
                        .toString();
                System.out.println("NofileValidatePenDetailsNsb cmsBranch : 96   : " + cmsBranch);
                if (cmsBranch.equals("{}")) {
                    cmsBranch = "";
                }

                String cmsErrorCode = null;
                String cmsErrorMessage = null;
                try {
                    cmsErrorCode = ((JSONObject) cmsJson.get(i)).get("errorCode").toString();
                    System.out.println("NofileValidatePenDetailsNsb  : 100   : " + cmsErrorCode);
                } catch (Exception e1) {
                    cmsErrorCode = "";
                }

                try {
                    cmsErrorMessage = ((JSONObject) cmsJson.get(i)).get("errorMessage").toString();
                    System.out.println("NofileValidatePenDetailsNsb  : 102   : " + cmsErrorMessage);
                } catch (Exception e1) {
                    cmsErrorMessage = "";
                }

                if (cmsErrorCode.isEmpty()) {
                    System.out.println("NofileValidatePenDetailsNsb  : 105   : " + cmsErrorCode);
                    // returnIds.add(penRequestID + "*" + penResponseID + "*" +
                    // penMobileNumber + "*" + penPenName + "*" +
                    // penResponseCode +
                    // "*" + penResponseMsg);
                    returnIds.add(cmscardNumber + "|" + cmsAccountNumber + "|" + cmsAccountType + "|" + cmsAccountStatus
                            + "|" + cmsIssuedDate + "|" + cmsActivationDate + "|" + cmsCardStatus + "|" + cmsCustomerID
                            + "|" + cmsCustomerName + "|" + cmsDOB + "|" + cmsAddressLine1 + "|" + cmsAddressLine2 + "|"
                            + cmsAddressLine3 + "|" + cmsCity + "|" + cmsEmailId + "|" + cmsMobileNumber + "|"
                            + cmsBranch);
                    System.out.println("NofileValidatePenDetailsNsb  : 108   : " + returnIds);
                } else {
                    System.out.println("NofileValidatePenDetailsNsb  : 110   : ");
                    throw new T24CoreException(cmsErrorCode, cmsErrorMessage);
                }
            }
        }

        /*
         * String cmscardNumber = cmsJson.get("cmscardNumber").toString();
         * String cmsAccountNumber = cmsJson.get("cmsAccountNumber").toString();
         * String cmsAccountType = cmsJson.get("cmsAccountType").toString();
         * String cmsAccountStatus = cmsJson.get("cmsAccountStatus").toString();
         * String cmsIssuedDate = cmsJson.get("cmsIssuedDate").toString();
         * String cmsActivationDate =
         * cmsJson.get("cmsActivationDate").toString(); String cmsCardStatus =
         * cmsJson.get("cmsCardStatus").toString(); String cmsCustomerID =
         * cmsJson.get("cmsCustomerID").toString(); String cmsCustomerName =
         * cmsJson.get("cmsCustomerName").toString(); String cmsDOB =
         * cmsJson.get("cmsDOB").toString(); String cmsEmailId =
         * cmsJson.get("cmsEmailId").toString(); String cmsMobileNumber =
         * cmsJson.get("cmsMobileNumber").toString(); String cmsAddressLine1 =
         * cmsJson.get("cmsAddressLine1").toString(); String cmsAddressLine2 =
         * cmsJson.get("cmsAddressLine2").toString(); String cmsAddressLine3 =
         * cmsJson.get("cmsAddressLine3").toString(); String cmsBranch =
         * cmsJson.get("cmsBranch").toString(); String cmsCity =
         * cmsJson.get("cmsCity").toString();
         */
        // }

        System.out.println("NofileValidatePenDetailsNsb  : 114   : " + returnIds);

        return returnIds;
    }

    public void getParamValues() {
        System.out.println("getParamValues  : 192  ");
        GetParamValueNsb Config = new GetParamValueNsb();
        System.out.println("getParamValues  : 194  ");
        Config.AddParam("CMS.NSB", new String[] { "ENQ.SELECTION.FIELD", "URL" });
        System.out.println("getParamValues  : 196  ");
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        System.out.println("getParamValues  : 198  ");

        ecpAccountNumber = ParamConfig.get("CMS.NSB").get("ENQ.SELECTION.FIELD").get(0).getValue();
        System.out.println("getParamValues  : 200 ecpAccountNumber =  " + ecpAccountNumber);
        ecpCmsUrl = ParamConfig.get("CMS.NSB").get("URL").get(0).getValue();
        System.out.println("getParamValues  : 202  ecpCmsUrl =  " + ecpCmsUrl);
    }

    public String getJsonDetails(String swaggerUrl) {

        VConsumeWebservicesNsb webserviceCall = new VConsumeWebservicesNsb();
        String jsonArr = null;
        try {
            jsonArr = webserviceCall.ConsumeSwaggerToT24Nsb(swaggerUrl);
        } catch (Exception e) {
            throw new T24CoreException("", "Error in Connection");
        }

        return jsonArr;
    }

    /*
     * public JSONObject getPenJsonDetails(String swaggerUrl) {
     * 
     * try { URL url = new URL(swaggerUrl); connection = (HttpURLConnection)
     * url.openConnection(); System.out.println(" ... 2"); //
     * connection.setRequestProperty("ValidationCode", "20180102608300009");
     * System.out.println(" ... 5"); //
     * http://192.168.132.10:7080/pen/v1/nickname/validate?mobileNumber=
     * 0765460550&penName=pen02
     * 
     * // This line makes the request InputStream responseStream;
     * System.out.println(" ... 31"); responseStream =
     * connection.getInputStream(); System.out.println(" ... 32"); StringBuilder
     * sb = new StringBuilder(); System.out.println(" ... 33"); for (int ch; (ch
     * = responseStream.read()) != -1;) { sb.append((char) ch); }
     * System.out.println("   ...52...." + sb.toString());
     * 
     * 
     * JSONParser parser = new JSONParser(); System.out.println(" ... 55" );
     * json = (JSONObject) parser.parse(sb.toString());
     * System.out.println(" ... 57" ); } catch (Exception e){ throw new
     * T24CoreException("", "Error in Connection"); } return json; }
     */
}
