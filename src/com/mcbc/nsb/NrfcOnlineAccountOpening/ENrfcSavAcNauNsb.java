package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mcbc.nsb.CommonUtilsNsb.validateT24DateNsb;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;

import com.temenos.t24.api.system.DataAccess;
import com.temenos.unicon.NoFileCustDetsViewNsb;

/**
 * TODO: Document me!
 *
 * @author Kalyan Pappu
 *
 */
public class ENrfcSavAcNauNsb extends Enquiry {

    Logger LOGGER = Logger.getLogger(ENrfcSavAcNauNsb.class.getName());
    validateT24DateNsb validateDate = new validateT24DateNsb();
    DataAccess dataObj = new DataAccess(this);
    private String accountNumber = null;
    private String fieldName = null;
    private Boolean accNumberExist = false;
    private String openingDateOperand = null;
    private String openingDateValue = null;
    private Boolean openingDateExist = false;
    private List<String> accountList = null;
    private List<String> OutputData = new ArrayList<String>();
    private String accountData = null;

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String filterCriteria - " + filterCriteria);
        
        for (FilterCriteria filterCrit : filterCriteria) {
            fieldName = filterCrit.getFieldname();
            if (fieldName.equals("ACCOUNT.NUMBER")) {
                accountNumber = filterCrit.getValue();
                accNumberExist = true;
            }
            if (fieldName.equals("OPENING.DATE")) {
                openingDateOperand = filterCrit.getOperand();
                openingDateValue = filterCrit.getValue();
                validateDate.checkDate(openingDateValue);
                openingDateExist = true;
            }
        }

        if ((!accNumberExist) && (!openingDateExist)) {
            throw new T24CoreException("", "OPENING DATE IS MANDATORY IF ACCOUNT NUMBER IS NOT MENTIONED");
        }

        if (accNumberExist) {
            try {
                if (accountNumber.substring(0, 2).equals("AA")) {
                    AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(
                            dataObj.getRecord("AA.ARRANGEMENT", accountNumber));
                    accountNumber = AaArrangemetRec.getLinkedAppl(0).getLinkedApplId().getValue();
                }
//                accountList = dataObj.selectRecords("", "ACCOUNT", "",
//                        "WITH L.NRFC.FLG EQ YES AND ACCOUNT.NUMBER EQ " + accountNumber);
                accountList = dataObj.selectRecords("", "ACCOUNT", "", "WITH ACCOUNT.NUMBER EQ " + accountNumber);
            } catch (Exception e) {
                throw new T24CoreException("", "Account Number Doesnot Exist");
            }
        } else if (openingDateExist) {
            if (openingDateOperand.equals("1")) {
                accountList = dataObj.selectRecords("", "ACCOUNT", "",
                        "WITH L.NRFC.FLG EQ YES AND OPENING.DATE EQ " + openingDateValue);
            } else if (openingDateOperand.equals("2")) {
                String date1 = openingDateValue.substring(0, 8);
                validateDate.checkDate(date1);
                String date2 = openingDateValue.substring(9, 17);
                validateDate.checkDate(date2);

                accountList = dataObj.selectRecords("", "ACCOUNT", "",
                        "WITH L.NRFC.FLG EQ YES AND OPENING.DATE BETWEEN " + date1 + " " + date2);
            }
        }

        if (!accountList.isEmpty()) {
            OutputData = readDataFromAccountList(accountList);
        }
        
        return OutputData;
    }

    private List<String> readDataFromAccountList(List<String> accountList) {
        for (String accNum : accountList) {
            AccountRecord accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", accNum));
            String aaArrangementId = accountRec.getArrangementId().getValue();
            AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(
                    dataObj.getRecord("AA.ARRANGEMENT", aaArrangementId));
            String arrStatus = AaArrangemetRec.getArrStatus().getValue();
            String productLine = AaArrangemetRec.getProductLine().getValue();
            if ((!arrStatus.equals("UNAUTH")) || (!productLine.equals("ACCOUNTS"))) {
                continue;
            }
            String customerId = accountRec.getCustomer().getValue();
            String customerName = accountRec.getShortTitle(0).getValue();
            String openingDate = accountRec.getOpeningDate().getValue();

            accountData = accNum + "*" + customerId + "*" + customerName + "*" + openingDate + "*" + arrStatus;
            OutputData.add(accountData);
        }
        return OutputData;
    }

}
