package com.mcbc.nsb.Accounts;

import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaprddescustomer.AaPrdDesCustomerRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.deaddress.DeAddressRecord;
import com.temenos.t24.api.system.DataAccess;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class EBldaremailaddrNsb extends Enquiry {

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub

        final Logger LOGGER = Logger.getLogger(EBldaremailaddrNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("List<FilterCriteria> filterCriteria - " + filterCriteria);
        LOGGER.info("EnquiryContext enquiryContext - " + enquiryContext);

        /*
         * String Txnrecid = enquiryContext.getCurrentTransactionRecordId();
         * LOGGER.info("String Txnrecid - " + Txnrecid); Contract contract = new
         * Contract(this); String str = Txnrecid.substring(0, 12);
         * LOGGER.info("arrangementid - " + str); contract.setContractId(str);
         * AaPrdDesCustomerRecord arrAccountRec = null; try { arrAccountRec =
         * contract.getCustomerCondition("CUSTOMER"); } catch (Exception e) {
         * e.printStackTrace(); } LOGGER.info("arrAccountRec - " +
         * arrAccountRec.getCustomer(0).getCustomer().getValue());
         */
        // AaPrdDesCustomerRecord custrec = new
        // AaPrdDesCustomerRecord(arrAccountRec);
        // com.temenos.t24.api.records.aaprddescustomer.CustomerClass cust =
        // custrec.getCustomer(0);
        // LOGGER.info("cust - " + cust);
        // String customerid = cust.getCustomer().getValue();
        // LOGGER.info("customerid - " + customerid);
        ListIterator<FilterCriteria> fcIter = filterCriteria.listIterator();
        // Iterator<FilterCriteria> fcIter = filterCriteria.iterator();

        String custemail = null;
        while (fcIter.hasNext()) {
            FilterCriteria fc = fcIter.next();
            String fv = fc.getFieldname();
            String customerid = fc.getValue();
            LOGGER.info("String fv - " + fv);
            LOGGER.info("String customerid - " + customerid);

            DataAccess da = new DataAccess(this);
            TStructure custrec = da.getRecord("CUSTOMER", customerid);
            CustomerRecord mycust = new CustomerRecord(custrec);
            String cocode = mycust.getCoCode();
            LOGGER.info("TStructure custrec - " + custrec);

//            for (int i=1; i<=1; i++) {
                int i = 1;
                String deaddid = cocode.concat(".C-").concat(customerid).concat(".PRINT.") + i + "";
                filterCriteria.remove(0);
                LOGGER.info("String deaddid - " + deaddid);
                try {
                    TStructure deaddrec = da.getRecord("DE.ADDRESS", deaddid);
                    if (deaddrec != null) {
                        DeAddressRecord mydeadd = new DeAddressRecord(deaddrec);
                        custemail = mydeadd.getEmail1().getValue();
                        LOGGER.info("String custemail - " + custemail);
                    }
                } catch (Exception e) {
                    System.out.println("failed to read");
                }
//            }
            String final1 = deaddid;
            String operand = "EQ";            
            FilterCriteria fc1 = new FilterCriteria();
            fc1.setFieldname("@ID");
            fc1.setOperand(operand);
            fc1.setValue(final1);
            filterCriteria.add(fc1);
        }

        return filterCriteria;

        // return super.setFilterCriteria(filterCriteria, enquiryContext);
    }
}
