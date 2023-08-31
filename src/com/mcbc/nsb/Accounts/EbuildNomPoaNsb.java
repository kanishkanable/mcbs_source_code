package com.mcbc.nsb.Accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.aacustomerarrangement.AaCustomerArrangementRecord;
import com.temenos.t24.api.records.aacustomerarrangement.AaCustomerArrangementRecord.*;
import com.temenos.t24.api.records.aacustomerarrangement.ArrangementClass;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class EbuildNomPoaNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        // return super.setFilterCriteria(filterCriteria, enquiryContext);

        final Logger LOGGER = Logger.getLogger(EbuildNomPoaNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine - ");

        try {
            System.out.println("Printing filterCriteria: " + filterCriteria.toString());
            System.out.println("Printing Enquiry context: " + enquiryContext.toString());

            FilterCriteria fc = new FilterCriteria();

            int j = 0;
            String s1 = null;
            String s2 = null;
            for (int i = 0; i < filterCriteria.size(); i++) {
                if (!filterCriteria.get(i).getFieldname().toString().isEmpty()) {
                    j++;
                    s1 = filterCriteria.get(i).getFieldname();
                    s2 = filterCriteria.get(i).getValue();
                }
            }
            if (j > 1) {
                throw new RuntimeException("error_msg: Please Select one criteria.");
            }
            if (j == 0) {
                throw new RuntimeException("error_msg: Please Input Valid Selection Criteria.");
            }

            fc.setFieldname("@ID");
            fc.setOperand("EQ");

            DataAccess da = new DataAccess(this);

            List<String> recIds;
            List<String> CustomerIdList;
            filterCriteria.clear();
            // SHORT.NAME is dummy field for LEGAL.ID selection. just used for
            // ENQ sel. Always Null
            if (s1.equals("SHORT.TITLE")) {
                String Sel_leg_Val = s2.toString();
                recIds = da.selectRecords("BNK", "CUS.LEGAL.ID", "", "WITH @ID LIKE '" + Sel_leg_Val + "...'");
                // System.out.println("recIds eq: "+recIds.toString());
//                recIds = da.getConcatValues("CUS.LEGAL.ID", Sel_leg_Val);
                int noCirs = recIds.size();
                if (noCirs > 10) {
                    throw new RuntimeException("Legal Id has 2 CIR..");
                } else {
                    CustomerIdList = da.getConcatValues("CUS.LEGAL.ID", recIds.get(0).toString());
                    fc = aacustarrangement(fc, CustomerIdList.get(0).toString());
                    filterCriteria.add(fc);
                }
            }

            if (s1.equals("CUSTOMER")) {
                String Sel_Cus_Val = s2.toString();
                fc = aacustarrangement(fc, Sel_Cus_Val);
                filterCriteria.add(fc);
            }

            // This section used if @ID - Account/Deposit ID selected on screen.
            if (s1.equals("@ID")) {                
                fc.setValue(s2.toString());          
                filterCriteria.add(fc);
            }

        } catch (Exception e1) {
            /*
             * FilterCriteria fc1 = new FilterCriteria();
             * fc1.setFieldname("CUSTOMER"); fc1.setOperand("EQ");
             * fc1.setValue("ALL");
             * 
             * filterCriteria.add(fc1);
             * 
             * if (filterCriteria.get(0).getFieldname().toString().isEmpty()) {
             * throw new
             * RuntimeException("error_msg: Please Input Valid Selection Criteria."
             * ); } // else { // throw new
             * RuntimeException("error_msg: error occured"); // }
             */ System.out.println("error_msg: Please Input Valid Selection Criteria.");
        }
        return filterCriteria;
    }

    public FilterCriteria aacustarrangement(FilterCriteria fc, String cust_id) {
        TStructure rec_ids;
        TStructure rec_ids1;
        final String SEPARATOR = " ";
        StringBuilder csvBuilder = new StringBuilder();
        List<String> acclist = new ArrayList<>();
        DataAccess da = new DataAccess(this);
        try {
            rec_ids = da.getRecord("AA.CUSTOMER.ARRANGEMENT", cust_id);
            AaCustomerArrangementRecord aacusarr = new AaCustomerArrangementRecord(rec_ids);
            List<com.temenos.t24.api.records.aacustomerarrangement.ProductLineClass> Prod_Line = aacusarr
                    .getProductLine();
            for (com.temenos.t24.api.records.aacustomerarrangement.ProductLineClass prod_line : Prod_Line) {
                String prod_name = prod_line.getProductLine().getValue();
                if (prod_name.equals("ACCOUNTS") || prod_name.equals("DEPOSITS")) {
                    List<ArrangementClass> Acarrlist = prod_line.getArrangement();
                    for (ArrangementClass acarrlist : Acarrlist) {
                        rec_ids1 = da.getRecord("AA.ARRANGEMENT", acarrlist.getArrangement().getValue());
                        AaArrangementRecord aaArr = new AaArrangementRecord(rec_ids1);
                        List<LinkedApplClass> Aa_arr = aaArr.getLinkedAppl();
                        // acclist.add(acarrlist.getArrangement().getValue());
                        csvBuilder.append(Aa_arr.get(0).getLinkedApplId().getValue());
                        csvBuilder.append(SEPARATOR);
                    }
                    // fc.setValue("75884");
                     fc.setValue(csvBuilder.toString());
                    // fc.setValue(acclist.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid CIF process in CONCAT table");
        }
        return fc;
    }

}
