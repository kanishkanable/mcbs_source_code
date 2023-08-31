package com.mcbc.nsb.BankGuaranteeNsb;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.RelationCodeClass;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCheckCustomerNsb extends RecordLifecycle {

    DataAccess da = new DataAccess(this);

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        // Routine to check Customer age is less than 18 years

        MdDealRecord mdDealRec = new MdDealRecord(currentRecord);
            String Customer = mdDealRec.getCustomer().getValue();
            System.out.println("1111111111111111111111   Customer   =   " + Customer);
            try {
                CustomerRecord CustomerRec = new CustomerRecord(da.getRecord("CUSTOMER", Customer));
                System.out.println("222222222222222222222222   Customer   =   " + Customer);
                String custAge = CustomerRec.getLocalRefField("L.CUST.AGE").getValue();
                System.out.println("333333333333333333333333   CustAge   =   " + custAge);
                if (custAge.startsWith("0")){
                    System.out.println("333333333333333333333333 41  CustAge   =   " + custAge);
                    if (custAge.startsWith("00")){
                        System.out.println("333333333333333333333333 42  CustAge   =   " + custAge);
                        custAge = custAge.substring(2, 3);
                        System.out.println("333333333333333333333333  43 CustAge   =   " + custAge);
                    } else {
                        System.out.println("333333333333333333333333  44 CustAge   =   " + custAge);
                        custAge = custAge.substring(1, 3);
                        System.out.println("333333333333333333333333 45  CustAge   =   " + custAge);
                    }
                    System.out.println("333333333333333333333333 46  CustAge   =   " + custAge);
                } else {
                    System.out.println("333333333333333333333333  47  CustAge   =   " + custAge);
                    if (!custAge.isEmpty()){
                        custAge = custAge.substring(0, 3);
                    }
                    System.out.println("333333333333333333333333  48 CustAge   =   " + custAge);
                }
                System.out.println("333333333333333333333333  49 CustAge   =   " + custAge);
                
                if (Integer.parseInt(custAge) < 18) {
                    System.out.println("444444444444444444444   CustAge   =   " + custAge);
                    mdDealRec.getCustomer().setError("EB-BG.CUST.AGE.NSB");
                    System.out.println("555555555555555555555555   CustAge   =   " + custAge);
                }

            } catch (T24CoreException e) {
                System.out.println("666666666666666666666666666666   Customer   =   " + Customer);
                mdDealRec.getCustomer().setError("EB-BG.NO.CUST.NSB");
            }

            // Routine to check Customer is not trustee
            System.out.println("777777777777777777777777777777   Customer   =   " + Customer);
            List<String> customerRecIds = da.selectRecords("BNK", "CUSTOMER", "", "WITH REL.CUSTOMER EQ " + Customer);
            System.out.println("88888888888888888888888888888   Customer   =   " + customerRecIds);
            if (!customerRecIds.isEmpty()) {
                System.out.println("99999999999999999999999999999999999   Customer   =   " + Customer);
                for (String CustRecId : customerRecIds) {
                    System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA   CustRecId   =   " + CustRecId);
                    CustomerRecord CustRelRec = new CustomerRecord(da.getRecord("CUSTOMER", CustRecId));
                    System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB   CustRecId   =   " + CustRecId);
                    for (RelationCodeClass RelationClass : CustRelRec.getRelationCode()) {
                       System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   CustRecId   =   " + CustRecId);
                        if (RelationClass.getRelCustomer().getValue().equals(Customer)) {
                            System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD   RelationClass.getRelCustomer().getValue()   =   " + RelationClass.getRelCustomer().getValue());
                            String CustRelation = RelationClass.getRelationCode().getValue();
                            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE   CustRelation   =   " + CustRelation);
                            if (CustRelation.equals("38")) {
                                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE   CustRelation   =   " + CustRelation);
                                RelationClass.getRelationCode().setError("EB-BG.CUST.TRUSTEE.NSB");
                                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE   CustRelation   =   " + CustRelation);
                            }
                            System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE1111 ");
                        }
                        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE222222   ");
                    }
                    System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE3333333   ");
                }
                System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE444444   ");
            }
        return mdDealRec.getValidationResponse();
    }
}
