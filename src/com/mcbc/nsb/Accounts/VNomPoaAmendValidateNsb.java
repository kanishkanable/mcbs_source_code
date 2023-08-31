package com.mcbc.nsb.Accounts;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.tables.aanomineepoansb.AaNomineePoaNsbRecord;
import com.temenos.t24.api.tables.aanomineepoansb.TypeClass;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class VNomPoaAmendValidateNsb extends RecordLifecycle {

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        final Logger LOGGER = Logger.getLogger(VNomPoaAmendValidateNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine - NomPoaAmendValidate");
        int Totpercentage = 0;
        
        AaNomineePoaNsbRecord AaNomPoa = new AaNomineePoaNsbRecord(currentRecord);
        List<TypeClass> Choosetype = AaNomPoa.getType();        
        for(TypeClass choosetype : Choosetype) {
            String Percentage = choosetype.getPercentage().getValue();
            if (choosetype.getType().getValue().equalsIgnoreCase("POA") && choosetype.getEndDate().getValue().isEmpty()) {
//                choosetype.getEndDate().setError("END.DATE Mandatory for POA");
                throw new T24CoreException("END.DATE Mandatory for POA ", " ");
            }
            
         // Valiadte Percentagage eq 100 
            if(!Percentage.isEmpty()) {
                Totpercentage = Totpercentage + Integer.parseInt(Percentage);
            }            
        }
        if (Totpercentage !=0 && Totpercentage!= 100 ){
            Choosetype.get(0).getPercentage().setError("Sum of percentage Not equal to 100");
        }
        return AaNomPoa.getValidationResponse();
    }    
}
