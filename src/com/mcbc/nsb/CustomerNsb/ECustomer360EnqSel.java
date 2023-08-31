package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.ListIterator;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */

public class ECustomer360EnqSel extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        String FullName = "L.FULL.NAME";
//        String AccountOfficer = "ACCOUNT.OFFICER";
        String CompanyBook = "COMPANY.BOOK";
        ListIterator<FilterCriteria> fieldIter = filterCriteria.listIterator();
        Boolean iShortName = false;
        Boolean iAccountOfficer = false;
        while (fieldIter.hasNext()) {
            FilterCriteria Enqfield = fieldIter.next();
            if (Enqfield.getFieldname().equals(FullName)) {
                String FullNameValue = Enqfield.getValue();
                int FullNameLength = FullNameValue.length();
                if (FullNameLength < 4) {
                    //throw new RuntimeException("MINIMUM 4 CHARECTERS MUST BE INPUT FOR SHORT NAME");
                    throw new T24CoreException("EB-ERROR.SELECTION", "EB-SH.NAME.ERROR.NSB");
                    
                    // "EB-E.CUST.FNAME.NSB");
                } else {
                    iShortName = true;
                    iAccountOfficer = true;
                }
            }
            if ((Enqfield.getFieldname().equals(CompanyBook)) && (!Enqfield.getValue().equals(null))) {
                iAccountOfficer = false;
            }
        }
        if ((iShortName) && (iAccountOfficer)) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-E.ACOFF.SHNAME.NSB");
            //, "EB-ERROR.SELECTION");
        }
        
        return filterCriteria;
    }
}