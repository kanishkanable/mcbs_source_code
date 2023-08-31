package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * 
 * Routine to get Customer ID using the NIC 
 * Routine is attached to Enquiry : E.CU.API.CHECK.EXIST.NSB.1.0.0
 * Enquity is to check if Customer exist in system when queried using NRFC application
 */
public class ECuApiCheckExistNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        
        DataAccess DataObj = new DataAccess(this);
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("NRFC", new String[] { "ENQ.SEL.NIC", "APPEND.NIC" });
//        Config.AddParam("CUSTOMER", new String[] { "ENQ.SEL.NIC", "APPEND.NIC" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        
        String currEnqSelectionField = ParamConfig.get("NRFC").get("ENQ.SEL.NIC").get(0).getValue();
        String EnqSelectionOperand = ParamConfig.get("NRFC").get("ENQ.SEL.NIC").get(1).getValue();
        String newEnqSelectionField = ParamConfig.get("NRFC").get("ENQ.SEL.NIC").get(2).getValue();
        String appendNewNic = ParamConfig.get("NRFC").get("APPEND.NIC").get(0).getValue();
        String appendOldNic = ParamConfig.get("NRFC").get("APPEND.NIC").get(1).getValue();

        int i = 0;
        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        while (fcIter.hasNext()) {
            FilterCriteria FilterCriteriaList = fcIter.next();
            String FilterCriFieldname = FilterCriteriaList.getFieldname();
            
            if (FilterCriFieldname.equals(currEnqSelectionField)) {
                String nicSelValue = FilterCriteriaList.getValue();
                List<String> CustomerIdList = null;
                String CustomerId = null;
                try {
                    if (nicSelValue.length() == 10) {
                        CustomerIdList = DataObj.getConcatValues("CUS.LEGAL.ID", nicSelValue + "-" + appendOldNic);
                    } else if (nicSelValue.length() == 12) {
                        CustomerIdList = DataObj.getConcatValues("CUS.LEGAL.ID", nicSelValue + "-" + appendNewNic);
                    } else {
                        throw new T24CoreException("EB-ERROR.SELECTION", "EB-INCORRECT.NIC.NSB");
                    }
                    Iterator<String> CusIdIterator = CustomerIdList.iterator();
                    CustomerId = CusIdIterator.next();
                } catch (Exception e) {
                    throw new T24CoreException("EB-ERROR.SELECTION", "EB-NO.NIC.NSB");
                }
                filterCriteria.remove(i);
                String operand = EnqSelectionOperand;
                FilterCriteria fc1 = new FilterCriteria();
                fc1.setFieldname(newEnqSelectionField);
                fc1.setOperand(operand);
                fc1.setValue(CustomerId);
                filterCriteria.add(fc1);
            }
            i = i + 1;
        }
        return filterCriteria;
    }

}
