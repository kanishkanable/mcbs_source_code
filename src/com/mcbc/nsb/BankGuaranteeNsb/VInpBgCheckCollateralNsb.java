package com.mcbc.nsb.BankGuaranteeNsb;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpBgCheckCollateralNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);
    Boolean collInCustomer = false;
    
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        System.out.println("validateRecord  : 33 ");
        MdDealRecord mdDealRec = new MdDealRecord(currentRecord);
        System.out.println("validateRecord  : 35 ");
        String customer = mdDealRec.getCustomer().getValue();
        System.out.println("validateRecord  : 37  customer =  " + customer);
        
        List<String> customerList = new ArrayList<String>();
        System.out.println("validateRecord  : 40  customerList =  " + customerList);
        if (!customer.isEmpty()) {
            System.out.println("validateRecord  : 42  customer =  " + customer);
            customerList.add(customer);
            System.out.println("validateRecord  : 44  customerList =  " + customerList);
        }
        System.out.println("validateRecord  : 46  customerList =  " + customerList);
        
        LocalRefList jointCustomerGroup = mdDealRec.getLocalRefGroups("L.CUST.JOINT");
        System.out.println("validateRecord  : 49  jointCustomerGroup =  " + jointCustomerGroup);
        for (LocalRefGroup jointCustomerField : jointCustomerGroup) {
            System.out.println("validateRecord  : 51  jointCustomerField =  " + jointCustomerField);
            customerList.add(jointCustomerField.getLocalRefField("L.CUST.JOINT").getValue());
            System.out.println("validateRecord  : 53  customerList =  " + customerList);
        }
        System.out.println("validateRecord  : 55  customerList =  " + customerList);
        
        LocalRefList securityGroup = mdDealRec.getLocalRefGroups("L.SECURITY");
        System.out.println("validateRecord  : 58  securityGroup =  " + securityGroup);
        for (LocalRefGroup securityField : securityGroup) {
            System.out.println("validateRecord  : 60  securityField =  " + securityField);
            String security = securityField.getLocalRefField("L.SECURITY").getValue();
            System.out.println("validateRecord  : 62  security =  " + security);
            String collateralCustomer = "";
            System.out.println("validateRecord  : 64  collateralCustomer =  " + collateralCustomer);
            try{
                System.out.println("validateRecord  : 66  collateralCustomer =  " + collateralCustomer);
                TStructure collateralObj = DataObj.getRecord("COLLATERAL", security);
                System.out.println("validateRecord  : 68  collateralObj =  " + collateralObj);
                CollateralRecord collateralRec = new CollateralRecord(collateralObj);
                System.out.println("validateRecord  : 70  collateralRec =  " + collateralRec);
                collateralCustomer = collateralRec.getCustomerId().getValue();
                System.out.println("validateRecord  : 72  collateralCustomer =  " + collateralCustomer);
            } catch (Exception e) {
                System.out.println("validateRecord  : 74  collateralCustomer =  " );
                securityField.getLocalRefField("L.SECURITY").setError("EB-INCORRECT.SECURITY");
                System.out.println("validateRecord  : 76  collateralCustomer =  " );
            }
            System.out.println("validateRecord  : 78  collateralCustomer =  " );
            
            if (!collateralCustomer.isEmpty()){
                System.out.println("validateRecord  : 81  collateralCustomer =  " + collateralCustomer);
                if (!customerList.contains(collateralCustomer)) {
                    System.out.println("validateRecord  : 83  customerList =  " + customerList);
                    securityField.getLocalRefField("L.SECURITY").setOverride("EB-BG.SECURITY.CUSTOMER");
                    System.out.println("validateRecord  : 85  customerList =  " );
                }
                System.out.println("validateRecord  : 87  customerList =  " );
            }
            System.out.println("validateRecord  : 89  customerList =  " );
        }
        System.out.println("validateRecord  : 91  customerList =  " );
        
        currentRecord.set(mdDealRec.toStructure());
        
        return mdDealRec.getValidationResponse();
    }
    
}
