package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.List;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class ENrfcCuNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        String customerId = "";
        String customerSince = "";
        for (FilterCriteria fc : filterCriteria) {
            if (fc.getFieldname().equals("@ID")) {
                customerId = fc.getValue();/* will have the Customer Id */
            } else if (fc.getFieldname().equals("CUSTOMER.SINCE")) {
                customerSince = fc.getValue();
            }
        }

        if (customerId.equals("") && customerSince.equals("")) {
            throw new T24CoreException("", "Creation Date is mandatory");
        } 
        
        return filterCriteria;
    }

}
