package com.mcbc.nsb.CustomerNsb;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import java.util.List;
import java.util.ListIterator;

public class ECustomerEnquiryNsb extends Enquiry {
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        String fieldName = "TARGET.CODE";
        int i = 0;
        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        while (fcIter.hasNext()) {
            FilterCriteria fc = fcIter.next();
            String fv = fc.getFieldname();
            if (fv.equals(fieldName)) {
                    String SectorValue = fc.getValue();
                    filterCriteria.remove(i);
                    String final1 = Character.toString(SectorValue.charAt(0)) + "...";
                    String operand = "LK";
                    FilterCriteria fc1 = new FilterCriteria();
                    fc1.setFieldname(fieldName);
                    fc1.setOperand(operand);
                    fc1.setValue(final1);
                    filterCriteria.add(fc1);    
                }

            i = i + 1;
        }

        return filterCriteria;
        
    }
}