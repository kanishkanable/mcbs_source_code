package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.customer.AddressClass;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class NoFileCuApiCheckExist extends Enquiry {

    DataAccess CustomerCurrDataObj = new DataAccess(this);
    
    private String liveCustName = "";
    private String liveCustAddr1 = "";
    private String liveCustAddr2 = "";
    private String liveCustAddr3 = "";
    private String liveCustAddrZip = "";
    private String liveCustAddCntry = "";
    private String liveRecStatus = "";

    private String nauCustName = "";
    private String nauCustAddr1 = "";
    private String nauCustAddr2 = "";
    private String nauCustAddr3 = "";
    private String nauCustAddrZip = "";
    private String nauCustAddCntry = "";
    private String nauRecStatus = "";

    public List<String> makeCustomer(String s1, DataAccess da, List<String> retId) {
        try {
            CustomerRecord cusRecNau = new CustomerRecord(CustomerCurrDataObj.getRecord("", "CUSTOMER", "$NAU", s1));

            if (!cusRecNau.getShortName().equals("")) {
                this.nauCustName = cusRecNau.getShortName().toString().replaceAll("\\[", "").replaceAll("\\]", "");
            }
            if (!cusRecNau.getStreet().toString().equals("")) {
                this.nauCustAddr1 = cusRecNau.getStreet().toString().replaceAll("\\[", "").replaceAll("\\]", "");
            }
            if (!cusRecNau.getAddress().isEmpty()) {
                List<AddressClass> addressClasses = cusRecNau.getAddress();

                for (AddressClass addressClass : addressClasses) {

                    String x = addressClass.toString().split(":")[1];
                    x = x.replaceAll("\"", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", "");
                    this.nauCustAddr2 = x + "," + this.nauCustAddr2;

                }
                this.nauCustAddr2 = this.nauCustAddr2.substring(0, this.nauCustAddr2.length() - 1);
            }

            if (!cusRecNau.getLocalRefField("L.ADDRESS.3").getValue().toString().equals("")) {
                this.nauCustAddr3 = cusRecNau.getLocalRefField("L.ADDRESS.3").getValue().toString()
                        .replaceAll("\\[", "").replaceAll("\\]", "");
            }

            if (!cusRecNau.getPostCode().toString().equals("")) {
                this.nauCustAddrZip = cusRecNau.getPostCode().toString().replaceAll("\\[", "").replaceAll("\\]", "");
            }

            if (!cusRecNau.getAddressCountry().toString().equals("")) {
                this.nauCustAddCntry = cusRecNau.getAddressCountry().toString().replaceAll("\\[", "").replaceAll("\\]",
                        "");

            }

            if (!cusRecNau.getRecordStatus().toString().equals("")) {
                this.nauRecStatus = cusRecNau.getRecordStatus().toString().replaceAll("\\[", "").replaceAll("\\]", "");
            }
            retId.add(s1 + "*" + nauCustName.toString() + "*" + nauCustAddr1 + "*" + nauCustAddr2 + "*" + nauCustAddr3
                    + "*" + nauCustAddrZip + "*" + nauCustAddCntry + "*" + nauRecStatus);

        } catch (Exception e) {

            try {

                CustomerRecord cusRec = new CustomerRecord(da.getRecord("CUSTOMER", s1));

                if (!cusRec.getShortName().toString().equals("")) {
                    this.liveCustName = cusRec.getShortName().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                }

                if (!cusRec.getStreet().toString().equals("")) {
                    this.liveCustAddr1 = cusRec.getStreet().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                }

                if (!cusRec.getAddress().isEmpty()) {
                    List<AddressClass> addressClasses = cusRec.getAddress();

                    for (AddressClass addressClass : addressClasses) {
                        String x = addressClass.toString().split(":")[1];
                        x = x.replaceAll("\"", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", "");
                        this.liveCustAddr2 = x + "," + this.liveCustAddr2;
                    }
                    this.liveCustAddr2 = this.liveCustAddr2.substring(0, this.liveCustAddr2.length() - 1);
                }

                if (!cusRec.getLocalRefField("L.ADDRESS.3").getValue().toString().equals("")) {
                    this.liveCustAddr3 = cusRec.getLocalRefField("L.ADDRESS.3").getValue().toString()
                            .replaceAll("\\[", "").replaceAll("\\]", "");
                }

                if (!cusRec.getPostCode().toString().equals("")) {
                    this.liveCustAddrZip = cusRec.getPostCode().toString().replaceAll("\\[", "").replaceAll("\\]", "");
                }

                if (!cusRec.getAddressCountry().toString().equals("")) {
                    this.liveCustAddCntry = cusRec.getAddressCountry().toString().replaceAll("\\[", "")
                            .replaceAll("\\]", "");
                }

                if (!cusRec.getRecordStatus().toString().equals("")) {
                    this.liveRecStatus = cusRec.getRecordStatus().toString().replaceAll("\\[", "").replaceAll("\\]",
                            "");
                }

                retId.add(s1 + "*" + liveCustName.toString() + "*" + liveCustAddr1 + "*" + liveCustAddr2 + "*"
                        + liveCustAddr3 + "*" + liveCustAddrZip + "*" + liveCustAddCntry + "*" + liveRecStatus);

            } catch (Exception IO) {
                IO.printStackTrace();

            }
        }
        return retId;
    }

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        DataAccess da = new DataAccess(this);

        String s1 = "";
        String s2 = "";
        for (FilterCriteria fc : filterCriteria) {
            if (fc.getFieldname().equals("CID")) {
                s1 = fc.getValue();
            }

            if (fc.getFieldname().equals("NIC")) {
                s2 = fc.getValue();
            }
        }

        // filterCriteria = NICConvert(filterCriteria, enquiryContext);
        List<String> retId = new ArrayList<String>();

        if (!s1.equals("")) {
            return makeCustomer(s1, da, retId);

        } else if (!s2.equals("")) {
            String newCustID = "";
            // T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");

            DataAccess DataObj = new DataAccess(this);
            GetParamValueNsb Config = new GetParamValueNsb();
            Config.AddParam("NRFC", new String[] { "ENQ.SEL.NIC", "APPEND.NIC" });

            Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
            String appendNewNic = ParamConfig.get("NRFC").get("APPEND.NIC").get(0).getValue();
            String appendOldNic = ParamConfig.get("NRFC").get("APPEND.NIC").get(1).getValue();

            if (s2.length() == 10) {
                List<String> d = DataObj.getConcatValues("CUS.LEGAL.ID", s2 + "-" + appendOldNic);
                if(!d.isEmpty()){
                    newCustID = d.get(0);
                }else{
                    List<String> custIDList = da.selectRecords("BNK", "CUSTOMER$NAU", "", " WITH LEGAL.ID EQ " + s2);
                    if(!custIDList.isEmpty()){
                        newCustID = da.selectRecords("BNK", "CUSTOMER$NAU", "", " WITH LEGAL.ID EQ " + s2).get(0);
                    }else{
                        throw new T24CoreException("EB-ERROR.SELECTION", "EB-INCORRECT.NIC.NSB");
                    }
                }
            } else if (s2.length() == 12) {
                List<String> d = DataObj.getConcatValues("CUS.LEGAL.ID", s2 + "-" + appendNewNic);
                if(!d.isEmpty()){
                    newCustID = d.get(0);
                }else{
                    List<String> custIDList = da.selectRecords("BNK", "CUSTOMER$NAU", "", " WITH LEGAL.ID EQ " + s2);
                    if(!custIDList.isEmpty()){
                        newCustID = da.selectRecords("BNK", "CUSTOMER$NAU", "", " WITH LEGAL.ID EQ " + s2).get(0);
                    }else{
                        throw new T24CoreException("EB-ERROR.SELECTION", "EB-INCORRECT.NIC.NSB");
                    }
                }
            } else {
                throw new T24CoreException("EB-ERROR.SELECTION", "EB-INCORRECT.NIC.NSB");
            }

            return makeCustomer(newCustID, da, retId);

        } else {
            return null;
        }
        
        
        
        
        //LIST-ITEM FBNK.CUSTOMER$NAU WITH LEGAL.ID EQ '880790420V'

    }

}
