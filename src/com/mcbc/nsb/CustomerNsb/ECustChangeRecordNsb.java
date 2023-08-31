package com.mcbc.nsb.CustomerNsb;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class ECustChangeRecordNsb extends Enquiry {
    
    
    @Override
    public String setValue(String value, String currentId, TStructure currentRecord,
            List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        
        value = value + ";";
        return value;
    }

}
