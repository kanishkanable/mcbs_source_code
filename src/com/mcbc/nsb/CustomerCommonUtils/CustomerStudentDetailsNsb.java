package com.mcbc.nsb.CustomerCommonUtils;

import java.util.ArrayList;
import java.util.List;

import com.mcbc.nsb.CommonUtilsNsb.CustomerPrintAddressNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
//import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerStudentDetailsNsb {
    List<String> errMsg = new ArrayList<String>();

    public void SetStudentDetailsTabNsb(CustomerRecord customerRec, DataAccess dataObj) {

        CheckMandatoryValueNsb(customerRec, "L.TRAN.PURPOSE", "EB-CUST.TRANPURPOSE.NSB");
        // CheckMandatoryValueNsb(CustomerRec, "L.STU.CUSTNO",
        // "EB-CUST.REMCUNO.NSB");
        // CheckMandatoryValueNsb(CustomerRec, "L.STU.REMITNAME",
        // "EB-CUST.REMNAME.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STUDENT.FILE", "EB-CUST.STUDENTFILE.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.ACC.NO", "EB-CUST.STUACCNO.NSB");
        // CheckMandatoryValueNsb(CustomerRec, "L.STU.ACC.CCY",
        // "EB-CUST.STUACCCCY.NSB");
        // CheckMandatoryValueNsb(CustomerRec, "L.STU.PAYE.NAME",
        // "EB-CUST.STUPAYENAME.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STUDENT.ID", "EB-CUST.STUDENTID.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.EDU.INST", "EB-CUST.STUINSTNAME.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.ENROL.DT", "EB-CUST.STUENROLDT.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.CRS.PERID", "EB-CUST.STUCRSPERID.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.TUT.LIM", "EB-CUST.STUTUTLIM.NSB");
        CheckMandatoryValueNsb(customerRec, "L.STU.LIV.EXP", "EB-CUST.STULIVEXP.NSB");

        try {
            customerRec.getLocalRefGroups("L.STU.ADDRESS").get(0);
        } catch (Exception e) {
            System.out.println("try to set error 46  :   ");
            LocalRefGroup grp = customerRec.createLocalRefGroup("L.STU.ADDRESS");
            System.out.println("try to set error 48  :   ");
            grp.getLocalRefField("L.STU.ADDRESS").setValue("NSB");
            System.out.println("try to set error 50  :   ");
            LocalRefList localRefList = customerRec.getLocalRefGroups("L.STU.ADDRESS");
            System.out.println("try to set error 52  :   ");
            localRefList.add(grp);
            System.out.println("try to set error 54  :   ");

            // LocalRefList localRefList =
            // customerRec.getLocalRefGroups("L.STU.ADDRESS");
            List<LocalRefGroup> localrefvalper = (List<LocalRefGroup>) customerRec.getLocalRefGroups("L.STU.ADDRESS");
            System.out.println("try to set error 57  :   ");
            for (LocalRefGroup e1 : localrefvalper) {
                System.out.println("try to set error 61  :   ");
                TField stuAddress = e1.getLocalRefField("L.STU.ADDRESS");
                System.out.println("try to set error 63  :   ");
                String stuAddressvalue = "";
                System.out.println("try to set error 65  :   ");

                if (stuAddress != null) {
                    stuAddressvalue = stuAddress.toString();
                }
                if (stuAddressvalue.equals("NSB")) {
                    System.out.println("try to set error 68  :   ");
                    errMsg.clear();
                    System.out.println("try to set error 70  :   ");
                    errMsg.add("EB-CUST.STUADDRESS.NSB");
                    System.out.println("try to set error 72  :   ");
                    stuAddress.setError(errMsg.toString());
                    System.out.println("try to set error 74  :   ");
                }
            }

        }

        // customerRec.getLocalRefGroups("L.STU.ADDRESS").get(0).getLocalRefField("L.STU.ADDRESS").setError("EB-CUST.STUADDRESS.NSB");

        /*
         * LocalRefList localRefList =
         * customerRec.getLocalRefGroups("L.STU.ADDRESS");
         * System.out.println("try to set error 1  :   " + localRefList); for
         * (LocalRefGroup locRefField : localRefList){
         * System.out.println("try to set error 2  :   " + locRefField);
         * locRefField.getLocalRefField("L.STU.ADDRESS").setError(
         * "EB-CUST.STUADDRESS.NSB");
         * System.out.println("try to set error 3 error set  :   " +
         * locRefField.toString()); }
         * 
         * 
         * List<LocalRefGroup> localrefvalper =
         * (List<LocalRefGroup>)customerRec.getLocalRefGroups("L.STU.ADDRESS");
         * System.out.println("localrefvalper  :  " + localrefvalper);
         * 
         * for (LocalRefGroup stuAddressGroup : localrefvalper){
         * System.out.println("for stuAddressGroup  :  " + stuAddressGroup);
         * TField stuAddressField =
         * stuAddressGroup.getLocalRefField("L.STU.ADDRESS");
         * System.out.println("for stuAddressField  :  " + stuAddressField);
         * String stuAddressFieldstr = "";
         * System.out.println("for stuAddressFieldstr  :  " +
         * stuAddressFieldstr); if (stuAddressField != null){
         * System.out.println("for if stuAddressField  :  " + stuAddressField);
         * stuAddressFieldstr = stuAddressField.toString();
         * System.out.println("for if stuAddressFieldstr  :  " +
         * stuAddressFieldstr); }
         * System.out.println("for end if stuAddressFieldstr  :  " +
         * stuAddressFieldstr); if (!stuAddressFieldstr.isEmpty()){
         * System.out.println("for if empty stuAddressFieldstr  :  " +
         * stuAddressFieldstr); errMsg.clear();
         * System.out.println("for if empty stuAddressFieldstr errMsg :  " +
         * errMsg); errMsg.add("EB-CUST.STUADDRESS.NSB");
         * System.out.println("for if empty stuAddressFieldstr errMsg add :  " +
         * errMsg); stuAddressField.setError(errMsg.toString());
         * System.out.println("for if empty stuAddressFieldstr errMsg set :  ");
         * } System.out.
         * println("for end if empty stuAddressFieldstr errMsg set :  "); }
         * System.out.println("end for empty stuAddressFieldstr errMsg set :  "
         * ); } System.out.println("end catch :  ");
         */
        CheckRemitterAccountNumberNsb(customerRec, dataObj);
        UpdateStuRemitterAddressNsb(customerRec, dataObj);
        customerRec.getLocalRefField("L.STU.ACC.CCY").setValue("LKR");
        CheckPayeeNoNameNsb(customerRec, dataObj);

    }

    private void CheckMandatoryValueNsb(CustomerRecord customerRec, String FieldName, String EbErrorId) {
        if (customerRec.getLocalRefField(FieldName).getValue().isEmpty()) {
            customerRec.getLocalRefField(FieldName).setError(EbErrorId);
        }
    }

    private void UpdateStuRemitterAddressNsb(CustomerRecord customerRec, DataAccess dataObj) {
        if (!customerRec.getLocalRefField("L.STU.CUSTNO").getValue().isEmpty()) {
            String RemCustNo = customerRec.getLocalRefField("L.STU.CUSTNO").getValue();
            String AddressFormat = null;
            try {
                TStructure RemCustNoRecord = dataObj.getRecord("CUSTOMER", RemCustNo);
                CustomerRecord remCusRec = new CustomerRecord(RemCustNoRecord);
                CustomerPrintAddressNsb CusPrintAddrObj = new CustomerPrintAddressNsb();
                CusPrintAddrObj.PrintAddressNsb(remCusRec);
                AddressFormat = CusPrintAddrObj.GetPrintAddressNsb();
            } catch (Exception e) {
                customerRec.getLocalRefField("L.STU.CUSTNO").setError("EB-CUS.STUREMCUSNO");
            }

            LocalRefList localRefList = customerRec.getLocalRefGroups("L.STU.REMITADDR");
            LocalRefGroup lFtcAddrGrp = customerRec.createLocalRefGroup("L.STU.REMITADDR");
            lFtcAddrGrp.getLocalRefField("L.STU.REMITADDR").setValue(AddressFormat);
            int lFtcAddrLength = localRefList.size();
            while (lFtcAddrLength > 0) {
                localRefList.remove(lFtcAddrLength - 1);
                lFtcAddrLength--;
            }
            localRefList.add(lFtcAddrGrp);
        }
    }

    private void CheckRemitterAccountNumberNsb(CustomerRecord customerRec, DataAccess dataObj) {
        if (!customerRec.getLocalRefField("L.STU.ACC.NO").getValue().isEmpty()) {
            try {
                String AccNo = customerRec.getLocalRefField("L.STU.ACC.NO").getValue();
                TStructure AccRecord = dataObj.getRecord("ACCOUNT", AccNo);
                AccountRecord AccountRec = new AccountRecord(AccRecord);
                if (!AccountRec.getCurrency().getValue().equals("LKR")) {
                    customerRec.getLocalRefField("L.STU.ACC.NO").setError("EB-CUST.REMACTCCY.NSB");
                }
                if (!customerRec.getLocalRefField("L.STU.CUSTNO").getValue().isEmpty()) {
                    if (!AccountRec.getCustomer().getValue()
                            .equals(customerRec.getLocalRefField("L.STU.CUSTNO").getValue())) {
                        customerRec.getLocalRefField("L.STU.ACC.NO").setError("EB-CUST.REMACTCOTCUS.NSB");
                    }
                } else {
                    customerRec.getLocalRefField("L.STU.CUSTNO").setValue(AccountRec.getCustomer().getValue());
                    // CustomerRec.getLocalRefField("L.STU.REMITNAME").setValue(AccountRec.getShortTitle(0).getValue());
                }
                customerRec.getLocalRefField("L.STU.REMITNAME").setValue(AccountRec.getShortTitle(0).getValue());
            } catch (Exception e) {
                customerRec.getLocalRefField("L.STU.ACC.NO").setError("EB-STU.ACCNO.NSB");
            }

        }
    }

    private void CheckPayeeNoNameNsb(CustomerRecord customerRec, DataAccess dataObj) {
        if (!customerRec.getLocalRefField("L.STU.PAYEENO").getValue().isEmpty()) {
            try {
                TStructure CustomerPayeeRecord = dataObj.getRecord("CUSTOMER",
                        customerRec.getLocalRefField("L.STU.PAYEENO").getValue());
                CustomerRecord payeeRecObj = new CustomerRecord(CustomerPayeeRecord);
                String payeeName = payeeRecObj.getShortName(0).getValue();
                customerRec.getLocalRefField("L.STU.PAYE.NAME").setValue(payeeName);
            } catch (Exception e) {
                customerRec.getLocalRefField("L.STU.PAYEENO").setError("EB-CUST.PAYEENO.NSB");
            }
        }
    }
}
