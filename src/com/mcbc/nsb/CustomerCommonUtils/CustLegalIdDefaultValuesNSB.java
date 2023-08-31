package com.mcbc.nsb.CustomerCommonUtils;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustLegalIdDefaultValuesNSB {

    private String legalDocValue;
    private String legalDocValueOld;
    private String legalIdError;
//    private String BirthCertificateNumber;
    private String legDocBirthCertificate;
    private String legDocNic;
    private String legDocNicOld;
    private String legDocPassport;
    
    public String getLegalDocValue() {
        return this.legalDocValue;
    }

    public String getLegalDocValueOld() {
        return this.legalDocValueOld;
    }
    
    public String getLegalIdError() {
        return this.legalIdError;
    }
    
/*    public String getBirthCertificate() {
        return this.BirthCertificateNumber;
    }
*/    
    public void UpdateLegalDefFields(String age, CustomerRecord customerRec, DataAccess dataObj) {
        GetParamValueNsb config = new GetParamValueNsb();
        config.AddParam("CUSTOMER", new String[] { "LEGAL.AGE.MINOR.DOC", "LEGAL.AGE.LK.DOC", "LEGAL.AGE.NOTLK.DOC" });
        Map<String, Map<String, List<TField>>> paramConfig = config.GetParamValue(dataObj);
        setParamValues(paramConfig);
        
        setlegalDocValueError(customerRec, age);
        
//        setBirthCetificate(CustomerRec);
        
    }
    
/*    private void setBirthCetificate(CustomerRecord CustomerRec) {
        String Birthcertificate = CustomerRec.getLocalRefField("L.BIRTH.CERTIFY").getValue();
        String DistrictCode = CustomerRec.getLocalRefField("L.DIST.BC.CODE").getValue();
        String Dob = CustomerRec.getDateOfBirth().getValue();
        if ((!Birthcertificate.isEmpty()) && (!DistrictCode.isEmpty()) && (!Dob.isEmpty())){
            this.BirthCertificateNumber = DistrictCode + "-" + Birthcertificate + "-" + Dob;
        }
    }
*/
    private void setlegalDocValueError(CustomerRecord customerRec, String age) {        
        if (Integer.parseInt(age) < 16) {
            this.legalDocValue = legDocBirthCertificate;
        } else {
            if (customerRec.getNationality().getValue().equals("LK")) {
                this.legalDocValue = legDocNic;
                this.legalDocValueOld = legDocNicOld;
            } else {
                this.legalDocValue = legDocPassport;
            }
        }
    }
    
    private void setParamValues(Map<String, Map<String, List<TField>>> paramConfig){
        this.legDocBirthCertificate = paramConfig.get("CUSTOMER").get("LEGAL.AGE.MINOR.DOC").get(0).getValue();
        this.legDocNic = paramConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(0).getValue();
        this.legDocNicOld = paramConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(1).getValue();
        this.legDocPassport = paramConfig.get("CUSTOMER").get("LEGAL.AGE.NOTLK.DOC").get(0).getValue();
    }
    
    
}
