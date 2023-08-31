package com.mcbc.nsb.CustomerNsb;

import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class ECustMinorToMajorNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        String fieldName = "@ID";
        
/*        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        String TodayDate = DateFormat.format(c1.getTime()).substring(4, 8);
        */
        Date SystemDate = new Date(this);
        String TodayDate = SystemDate.getDates().getToday().toString().substring(4, 8);
        String IdFormat = "..." + "-" + "..." + TodayDate + "-...";
        String operand = "LK";
        FilterCriteria fc = new FilterCriteria();
        fc.setFieldname(fieldName);
        fc.setOperand(operand);
        fc.setValue(IdFormat);
        filterCriteria.add(fc);

        return filterCriteria;
    }

}
