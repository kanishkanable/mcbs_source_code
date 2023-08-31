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

/**
 * TODO: Document me!
 *
 * @author kalyan Pappu
 *
 */
public class ENrfcSavAcNsb extends Enquiry {

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
    private String arrStatus = null;
    private String productLine = null;

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String filterCriteria - 42 ");

        LOGGER.info("String filterCriteria - 48 ");
        for (FilterCriteria filterCrit : filterCriteria) {
            LOGGER.info("String filterCriteria - 50 :  " + filterCrit.toString());
            fieldName = filterCrit.getFieldname();
            LOGGER.info("String filterCriteria - 52 :  " + fieldName);
            if (fieldName.equals("ACCOUNT.NUMBER")) {
                LOGGER.info("String fieldName - 54 :  " + fieldName);
                accountNumber = filterCrit.getValue();
                LOGGER.info("String accountNumber - 56 :  " + accountNumber);
                accNumberExist = true;
                LOGGER.info("String accNumberExist - 58 :  " + accNumberExist);
            }
            LOGGER.info("String filterCriteria - 60 ");
            if (fieldName.equals("OPENING.DATE")) {
                LOGGER.info("String fieldName - 62 :  " + fieldName);
                openingDateOperand = filterCrit.getOperand();
                LOGGER.info("String openingDateOperand - 64 :  " + openingDateOperand);
                openingDateValue = filterCrit.getValue();
                LOGGER.info("String openingDateValue - 66 :  " + openingDateValue);
                validateDate.checkDate(openingDateValue);
                LOGGER.info("String validateDate - 68 :  " + validateDate);
                openingDateExist = true;
                LOGGER.info("String openingDateExist - 70 :  " + openingDateExist);
            }
            LOGGER.info("String openingDateOperand - 72 :  ");
        }
        LOGGER.info("String filterCriteria - 74 ");

        if ((!accNumberExist) && (!openingDateExist)) {
            LOGGER.info(
                    "String accNumberExist openingDateExist - 77 :  " + openingDateExist + "-----> " + accNumberExist);
            throw new T24CoreException("", "OPENING DATE IS MANDATORY IF CUSTOMER NUMBER IS NOT MENTIONED");
        }
        LOGGER.info("String filterCriteria - 80 ");

        if (accNumberExist) {
            LOGGER.info("String filterCriteria - 83 ");
            try {
                LOGGER.info("String filterCriteria - 85 ");
                if (accountNumber.substring(0, 2).equals("AA")) {
                    AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(
                            dataObj.getRecord("AA.ARRANGEMENT", accountNumber));
                    LOGGER.info("String accountNumber - 89 " + accountNumber);
                    accountNumber = AaArrangemetRec.getLinkedAppl(0).getLinkedApplId().getValue();
                    LOGGER.info("String accountNumber - 91 " + accountNumber);
                }
                LOGGER.info("String accountNumber - 93 " + accountNumber);
                accountList = dataObj.selectRecords("", "ACCOUNT", "", accountNumber);
                LOGGER.info("String accountList - 95 " + accountList);
                // "WITH L.NRFC.FLG EQ YES AND ACCOUNT.NUMBER EQ " +
                // accountNumber);
            } catch (Exception e) {
                throw new T24CoreException("", "Incorrect Account Number");
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
        System.out.println("OutputData  89  :   " + OutputData);

        return OutputData;
    }

    private List<String> readDataFromAccountList(List<String> accountList) {
        for (String accNum : accountList) {
            AccountRecord accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", accNum));
            String aaArrangementId = accountRec.getArrangementId().getValue();
            AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(
                    dataObj.getRecord("AA.ARRANGEMENT", aaArrangementId));
            arrStatus = AaArrangemetRec.getArrStatus().getValue();
            productLine = AaArrangemetRec.getProductLine().getValue();
            if ((!arrStatus.equals("AUTH")) || (!productLine.equals("ACCOUNTS"))) {
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
