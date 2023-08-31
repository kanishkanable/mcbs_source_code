package com.mcbc.nsb.cms;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class ECustomerViewDetailsNsb extends Enquiry {

    Logger LOGGER = Logger.getLogger(ECustomerViewDetailsNsb.class.getName());
    
    DataAccess dataObj = new DataAccess(this);
    String accountId = null;
    String arrId = null;
    String customerId = null;
    String currency = null;
    String category = null;
    String shortName = null;
    String branchCode = null;
    String gender = null;
    String address1 = null;
    String address2 = null;
    String address3 = null;
    String postalCode = null;
    String mobileNo = null;
    String emailId = null;
    String nicNo = null;
    String dateOfBirth = null;
    String mailingAddr1 = null;
    String mailingAddr2 = null;
    String mailingAddr3 = null;
    String mailingAddr4 = null;
    
    List<String> outputData = new ArrayList<String>();
    
    AccountRecord accountRec;
    AaArrangementRecord AaArrangemetRec;
    CustomerRecord customerRec;
    
    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("String setIds  61  -  " + filterCriteria );
        
        arrId = getRecordIdfromFc(filterCriteria);
        LOGGER.info("String arrId  64  -  " + arrId );
        
        if (!arrId.substring(0, 2).equals("AA")){
            LOGGER.info("String arrId  67  -  " + arrId );
            try{
                LOGGER.info("String arrId  69  -  " + arrId );
                accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", arrId));
                LOGGER.info("String arrId  71  -  " + arrId );
                arrId = accountRec.getArrangementId().getValue();
                LOGGER.info("String arrId  73  -  " + arrId );
            } catch (T24CoreException e){
                LOGGER.info("String arrId  75  -  " + arrId );
                throw new T24CoreException("", "Account Number Doesnt Exist");
            }
            LOGGER.info("String arrId  78  -  " + arrId );
        }
        LOGGER.info("String arrId  80  -  " + arrId );
        
        try{
            LOGGER.info("String arrId  83  -  " + arrId );
            AaArrangemetRec = new AaArrangementRecord(dataObj.getRecord("AA.ARRANGEMENT", arrId));
            LOGGER.info("String arrId  85  -  " + arrId );
            accountId = AaArrangemetRec.getLinkedAppl(0).getLinkedApplId().getValue();
            LOGGER.info("String accountId  87  -  " + accountId );
            accountRec = new AccountRecord(dataObj.getRecord("ACCOUNT", accountId));
            LOGGER.info("String accountId  89  -  " + accountId );
        } catch (T24CoreException e){
            LOGGER.info("String accountId  91  -  " + accountId );
            throw new T24CoreException("", "Account Number Doesnt Exist");
        }
        
        LOGGER.info("String arrId  95  -  " + arrId );
        currency = AaArrangemetRec.getCurrency().getValue();
        LOGGER.info("String arrId  97  -  " + arrId );
        category = accountRec.getCategory().getValue();
        LOGGER.info("String arrId  99  -  " + arrId );
        
        for (CustomerClass customerList : AaArrangemetRec.getCustomer()){
            LOGGER.info("String customerList  102  -  " + customerList );
            customerId = customerList.getCustomer().getValue();
            LOGGER.info("String customerId  104  -  " + customerId );
            customerRec = new CustomerRecord(dataObj.getRecord("CUSTOMER", customerId));
            LOGGER.info("String customerList  106  -  " + customerList );
            shortName = customerRec.getShortName(0).getValue();
            LOGGER.info("String shortName  108  -  " + shortName );
            branchCode = customerRec.getCompanyBook().getValue();
            LOGGER.info("String branchCode  110  -  " + branchCode );
            gender = customerRec.getGender().getValue();
            LOGGER.info("String gender  112  -  " + gender );
            
            try{
                LOGGER.info("String gender  114  -  " + gender );
                address1 = customerRec.getStreet(0).getValue();
                LOGGER.info("String address1  116  -  " + address1 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String address1  118  -  " + address1 );
                address1 = "";
            }
            LOGGER.info("String address1  122  -  " + address1 );
            try{
                LOGGER.info("String address1  124  -  " + address1 );
                address2 = customerRec.getAddress(0).get(0).getValue();
                List<String> address2Array = new ArrayList<String>();
                for (int count = 0; customerRec.getAddress().size() > count; count++ ){
                    address2Array.add(customerRec.getAddress(count).get(count).getValue() + " ");
                }
                address2 = address2Array.toString().replace("[", "").replace("]", "");
                LOGGER.info("String address2  126  -  " + address2 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String address2  128  -  " + address2 );
                address2 = "";
            }
            LOGGER.info("String address2  131  -  " + address2 );
            try{
                LOGGER.info("String address2  133  -  " + address2 );
                address3 = customerRec.getTownCountry(0).getValue();
                LOGGER.info("String address3  135  -  " + address3 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String address3  137  -  " + address3 );
                address3 = "";
            }
            LOGGER.info("String gender  140  -  " + gender );
            try{
                LOGGER.info("String postalCode  142  -  " + postalCode );
                postalCode = customerRec.getPostCode(0).getValue();
                LOGGER.info("String postalCode  144  -  " + postalCode );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String postalCode  146  -  " + postalCode );
                postalCode = "";
            }
            LOGGER.info("String postalCode  149  -  " + postalCode );
            try{
                LOGGER.info("String postalCode  151  -  " + postalCode );
                mobileNo = customerRec.getPhone1(0).getSms1().getValue();
                LOGGER.info("String mobileNo  153  -  " + mobileNo );
                emailId = customerRec.getPhone1(0).getEmail1().getValue();
                LOGGER.info("String emailId  155  -  " + emailId );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String emailId  157  -  " + emailId );
                mobileNo = "";
                emailId = "";
                LOGGER.info("String emailId  159  -  " + emailId );
            }
            LOGGER.info("String emailId  161  -  " + emailId );
            nicNo = getLegalIdNic(customerRec);
            LOGGER.info("String nicNo  163  -  " + nicNo );
            dateOfBirth = customerRec.getDateOfBirth().getValue();
            LOGGER.info("String dateOfBirth  165  -  " + dateOfBirth );
            
            try{
                LOGGER.info("String gender  169  -  " + gender );
                mailingAddr1 = customerRec.getStreet(0).getValue();
                LOGGER.info("String mailingAddr1  171  -  " + mailingAddr1 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String mailingAddr1  173  -  " + mailingAddr1 );
                mailingAddr1 = "";
                LOGGER.info("String mailingAddr1  175  -  " + mailingAddr1 );
            }
            LOGGER.info("String mailingAddr1  177  -  " + mailingAddr1 );
            try{
                LOGGER.info("String mailingAddr2  179  -  " + mailingAddr2 );
//                mailingAddr2 = customerRec.getAddress(0).get(0).getValue();
                List<String> mailAddress2Array = new ArrayList<String>();
                for (int count = 0; customerRec.getAddress().size() > count; count++ ){
                    mailAddress2Array.add(customerRec.getAddress(count).get(count).getValue() + " ");
                }
                mailingAddr2 = mailAddress2Array.toString().replace("[", "").replace("]", "");;
                LOGGER.info("String mailingAddr2  181  -  " + mailingAddr2 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String mailingAddr2  183  -  " + mailingAddr2 );
                mailingAddr2 = "";
            }
            LOGGER.info("String mailingAddr2  186  -  " + mailingAddr2 );
            try{
                LOGGER.info("String mailingAddr3  188  -  " + mailingAddr3 );
                mailingAddr3 = customerRec.getTownCountry(0).getValue();
                LOGGER.info("String mailingAddr3  190  -  " + mailingAddr3 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String mailingAddr3  192  -  " + mailingAddr3 );
                mailingAddr3 = "";
            }
            LOGGER.info("String mailingAddr3  195 -  " + mailingAddr3 );
            try{
                LOGGER.info("String mailingAddr4  197  -  " + mailingAddr4 );
                mailingAddr4 = customerRec.getPostCode(0).getValue();
                LOGGER.info("String mailingAddr4  199  -  " + mailingAddr4 );
            } catch (IndexOutOfBoundsException e) {
                LOGGER.info("String mailingAddr4  201  -  " + mailingAddr4 );
                mailingAddr3 = "";
            }
            LOGGER.info("String mailingAddr4  204  -  " + mailingAddr4 );

            String outputLine = null;
            LOGGER.info("String outputLine  207  -  " + outputLine );
            outputLine = accountId + "|" + currency + "|" + category + "|" + customerId + "|" + shortName + "|" + branchCode + "|" + gender + "|" + address1 + "|" + address2 + "|" + address3 + "|" + postalCode + "|" + mobileNo + "|" + emailId + "|" + nicNo + "|" + dateOfBirth + "|" + mailingAddr1 + "|" + mailingAddr2 + "|" + mailingAddr3 + "|" + mailingAddr4;
            LOGGER.info("String outputLine  209  -  " + outputLine );
            outputData.add(outputLine);
            LOGGER.info("String outputData  211  -  " + outputData );
        }
        LOGGER.info("String outputData  213  -  " + outputData );        
        
        return outputData;
    }

   
    private String getRecordIdfromFc(List<FilterCriteria> filterCriteria) {
        LOGGER.info("String filterCriteria  220  -  " + filterCriteria );
        String fieldValue = null;
        for (FilterCriteria fieldNames : filterCriteria) {
            LOGGER.info("String fieldNames  223  -  " + fieldNames );
            String fieldName = fieldNames.getFieldname();
            LOGGER.info("String fieldName  225  -  " + fieldName );
            if (fieldName.equals("accountNumber")) {
                fieldValue = fieldNames.getValue();
                LOGGER.info("String fieldValue  228  -  " + fieldValue );
            }
        }
        LOGGER.info("String FieldValue  231  -  " + fieldValue );
        return fieldValue;
    }
    
    private String getLegalIdNic(CustomerRecord customerRec){
        LOGGER.info("String getLegalIdNic  236  -  " );
        String legalId = null;
        LOGGER.info("String legalId  238  -  " + legalId);
        for (LegalIdClass legalIdClass : customerRec.getLegalId()) {
            LOGGER.info("String legalIdClass  240  -  " + legalIdClass);
            String legaldoc = legalIdClass.getLegalDocName().getValue();
            LOGGER.info("String legaldoc  242  -  " + legaldoc);
            if (legaldoc.equals("NATIONAL.ID")) {
                LOGGER.info("String legaldoc  244  -  " + legaldoc);
                legalId = legalIdClass.getLegalId().getValue();
                break;
            }
            LOGGER.info("String legalId  247  -  " + legalId);
            if (legaldoc.equals("NATIONAL.ID.OLD")) {
                LOGGER.info("String legaldoc  250  -  " + legaldoc);
                legalId = legalIdClass.getLegalId().getValue();
            }
            LOGGER.info("String legalId  253  -  " + legalId);
        }
        LOGGER.info("String legalId  255  -  " + legalId);
        return legalId;
    }
}
