package com.mcbc.nsb.CustomerNsb;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class EAmlResultNsb extends Enquiry {
    
    DataAccess dataObj = new DataAccess(this);
    
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "AML.CHECK.VALUE", "ENQ.SELECTION.FIELD" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(dataObj);
        
        String EcpAmlCheckValue = ParamConfig.get("CUSTOMER").get("AML.CHECK.VALUE").get(0).getValue();
        String EnqSelectionField = ParamConfig.get("CUSTOMER").get("ENQ.SELECTION.FIELD").get(0).getValue();
        String EnqSelectionOperand = ParamConfig.get("CUSTOMER").get("ENQ.SELECTION.FIELD").get(1).getValue();
        
        int i = 0;
        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        while (fcIter.hasNext()) {
            FilterCriteria fc = fcIter.next();
            String fv = fc.getFieldname();
            
            if (fv.equals(EnqSelectionField)) {
                String FinalSelection = null;
                String AmlCheckValue = fc.getValue();
                if (AmlCheckValue.equals(EcpAmlCheckValue)) {
                    FinalSelection = ParamConfig.get("CUSTOMER").get("AML.CHECK.VALUE").get(1).getValue();
//                    FinalSelection = "1...";
                } else {
                    FinalSelection = ParamConfig.get("CUSTOMER").get("AML.CHECK.VALUE").get(2).getValue();
//                    FinalSelection = "2...";
                }
                filterCriteria.remove(i);
                String operand = EnqSelectionOperand;
                FilterCriteria fc1 = new FilterCriteria();
                fc1.setFieldname(EnqSelectionField);
                fc1.setOperand(operand);
                fc1.setValue(FinalSelection);
                filterCriteria.add(fc1);
            }
            i = i + 1;
        }
        return filterCriteria;
    }
}
