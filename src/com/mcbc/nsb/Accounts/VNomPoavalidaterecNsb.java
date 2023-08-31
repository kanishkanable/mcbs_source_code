package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.aanomineepoansb.AaNomineePoaNsbRecord;
import com.temenos.t24.api.tables.aanomineepoansb.TypeClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class VNomPoavalidaterecNsb extends RecordLifecycle {

    DataAccess da = new DataAccess(this);
    TStructure custrec;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {

        final Logger LOGGER = Logger.getLogger(VNomPoavalidaterecNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine - NomPoavalidaterec");
        int Totpercentage = 0;
        
        AaNomineePoaNsbRecord NomPoaRec = new AaNomineePoaNsbRecord(currentRecord);
        List<TypeClass> Choosetype = NomPoaRec.getType();
        for (TypeClass choosetype : Choosetype) {
            String type = choosetype.getType().getValue();
            String end_date = choosetype.getEndDate().getValue();
            String customerid = choosetype.getCustomerNumber().getValue();
            String Percentage = choosetype.getPercentage().getValue();
            
            // Validate POA and END.DATE is mandatory
            if (type != null && type != "" && type.equalsIgnoreCase("POA")) {
                if (end_date.isEmpty()) {
                    choosetype.getEndDate().setError("END.DATE is Mandatory for POA");
                }
                // Validate POA and AGE of customer
                if (!customerid.isEmpty()) {
                    custrec = ReadCustomer(customerid);
                    CustomerRecord mycust = new CustomerRecord(custrec);
                    TField cust_age = mycust.getLocalRefField("L.CUST.AGE");
                    String[] age1 = cust_age.toString().replaceAll("^0*", "").split(" ");
                    
                    if (age1 != null && !age1[0].isEmpty()) {
                        int age = Integer.parseInt(age1[0]);
                        if (age <= 18) {
                            choosetype.getCustomerNumber().setError("POA age is Less than 18 yrs.");
                        }
                    }
                }
            }

            // Validate Customer with title ‘VENERABLE’ for monk
            
            if (!customerid.isEmpty()) {
                custrec = ReadCustomer(customerid);
                CustomerRecord mycust = new CustomerRecord(custrec);
                String cus_title = mycust.getTitle().getValue();                                
                if (!cus_title.isEmpty() && cus_title.equalsIgnoreCase("VEN")) {
                    choosetype.getCustomerNumber().setError("Customer title ‘VENERABLE’ for monk");
                }
            }
           
          // Valiadte Percentagage eq 100 
            if(!Percentage.isEmpty()) {
                Totpercentage = Totpercentage + Integer.parseInt(Percentage);
            }            
        }
        if (Totpercentage !=0 && Totpercentage!= 100 ){
            Choosetype.get(0).getPercentage().setError("Sum of percentage Not equal to 100");
        }
        return NomPoaRec.getValidationResponse();
    }

    public final TStructure ReadCustomer(String customerid) {
        try {
            custrec = da.getRecord("CUSTOMER", customerid);
        } catch (Exception e) {
            throw new T24CoreException("Invalid customer Id.");
        }
        return custrec;
    }
}
