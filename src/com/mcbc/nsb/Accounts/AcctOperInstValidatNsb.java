package com.mcbc.nsb.Accounts;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaprddescustomer.AaPrdDesCustomerRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class AcctOperInstValidatNsb extends ActivityLifecycle {

        
        @Override
    public TValidationResponse validateRecord(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {
        // TODO Auto-generated method stub
    
        final Logger LOGGER = Logger.getLogger(AcctOperInstValidatNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling routine - ");
        
        Contract contract = new Contract(this);        
        String arrangementid = arrangementContext.getArrangementId();
        contract.setContractId(arrangementid);
        TStructure arrCustomerRec1 = contract.getConditionForProperty("CUSTOMER");
        AaPrdDesCustomerRecord arrCustomerRec = new AaPrdDesCustomerRecord(arrCustomerRec1);
        LOGGER.info("TStructure arrCustomerRec L.OPE.INST - " +  arrCustomerRec.getLocalRefField("L.OPE.INST").getValue());
        LOGGER.info("TStructure arrCustomerRec L.OPER.CIF - " +  arrCustomerRec.getLocalRefGroups("L.OPER.CIF"));        
        
/*  below code not working for AA.ARR.CUSTOMER
 * getLocalRefGroups - For Local ref multivalue
 * getLocalRefFields - For local ref single value field
 * 
        AaPrdDesCustomerRecord arrCustomerRec2 = new AaPrdDesCustomerRecord(record);
        LOGGER.info("TStructure arrCustomerRec L.OPE.INST - " +  arrCustomerRec2.getLocalRefField("L.OPE.INST").getValue());
        LOGGER.info("TStructure arrCustomerRec L.OPER.CIF - " +  arrCustomerRec2.getLocalRefGroups("L.OPER.CIF"));
*/
/*        
        "No Operating Instructions
        All of Us
        Either of Us
        Two of Us
        Three of Us
        Any One of Us
        Both of Us
        Only One of Us [Specify Person Name]
        One of Us With [Specify Person Name]
        Two of Us With [Specify Person Name]
        Three of Us With [Specify Person Name]
        Other Instruction
        "
*/
        String Oper_Inst = arrCustomerRec.getLocalRefField("L.OPE.INST").getValue();
        LocalRefList Oper_CIF = arrCustomerRec.getLocalRefGroups("L.OPER.CIF");
                
        if(Oper_Inst.equalsIgnoreCase("Two of Us")){
            int cnt = 0;
            for (LocalRefGroup Oper_cif : Oper_CIF) {                
                String OperCIF = Oper_cif.getLocalRefField("L.OPER.CIF").getValue();                
                if(!OperCIF.isEmpty()){
                    cnt = cnt + 1;
                }                                
            }            
            if(!(cnt == 2)){   
                Oper_CIF.get(0).getLocalRefField("L.OPER.CIF").setError("Select Valid Operating CIF for - " + Oper_Inst);                
            }
        }
//        aaarracctrec.getLocalRefField("L.PURPOSE").getValue().toString()
        
        return arrCustomerRec.getValidationResponse();
        
    }



    
}
