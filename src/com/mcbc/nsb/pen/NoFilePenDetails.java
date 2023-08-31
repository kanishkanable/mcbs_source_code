package com.mcbc.nsb.pen;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebpennamedetailsnsb.EbPenNameDetailsNsbRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class NoFilePenDetails extends Enquiry {

    DataAccess da = new DataAccess(this);
    List<String> retId = new ArrayList<>();
    AccountRecord accRec;
    CustomerRecord cusRec;
    
    String accountId = null;
    String CustId = "";
    String cusName = "";
    String legalId = "";
    String dateofBirth = "";
    String penName = "";
    String penStatus = "";

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        int i = 0;
        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        while (fcIter.hasNext()) {
            FilterCriteria filtCrit = fcIter.next();
            String filtName = filtCrit.getFieldname();
            if (filtName.equals("ACCOUNT.NO")) {
                accountId = filtCrit.getValue();        
            }
            i = i + 1;
        }
        
        
        if (!accountId.isEmpty()){
            
            if (accountId.substring(0, 2).equals("AA")) {
                try{
                AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT", accountId));
                accountId = AaArrangemetRec.getLinkedAppl(0).getLinkedApplId().getValue();
                } catch (Exception e) {
                    throw new T24CoreException("", "Account Record does not exist for Account " + accountId);
                }
            }
            
            try {
                accRec = new AccountRecord(da.getRecord("ACCOUNT", accountId));
            } catch (Exception e) {
                //throw new T24CoreException("", "Error Processing ACCOUNT Number");
                throw new T24CoreException("", "Account Record does not exist for Account " + accountId);
            }
            CustId = accRec.getCustomer().toString();
            cusName = accRec.getAccountTitle1(0).toString();
        }
        

        if (!CustId.isEmpty()) {
            try {
                cusRec = new CustomerRecord(da.getRecord("CUSTOMER", CustId));
            } catch (Exception e) {
                throw new T24CoreException("", "CUSTOMER Number" + CustId);
            }
            dateofBirth = cusRec.getDateOfBirth().toString();
        }
        
        try{
            legalId = cusRec.getLegalId().get(0).getLegalId().getValue(); 
        } catch (Exception e) {
            legalId = "";
        }
        
        if (dateofBirth.isEmpty()){
            dateofBirth = "";
        }
        
        try {
            EbPenNameDetailsNsbRecord ebpenRec = new EbPenNameDetailsNsbRecord(
                    da.getRecord("EB.PEN.NAME.DETAILS.NSB", accountId));
            penName = ebpenRec.getPenName().toString();
            penStatus = ebpenRec.getStatus().toString();
        } catch (Exception e) {
        }
        
        if (penName.isEmpty()){
            penName = "";
        }
        if (penStatus.isEmpty()){
            penStatus = "";
        }
        
        retId.add(String.valueOf(accountId) + "*" + cusName.toString() + "*" + legalId + "*" + dateofBirth + "*" + penName
                + "*" + penStatus);

        return retId;
    }
}
