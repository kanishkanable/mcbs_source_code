package com.mcbc.nsb.BankGuaranteeNsb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.ProductClass;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.collateral.CollateralRecord;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefBgLetterGteeNsb extends RecordLifecycle {

    DataAccess DataObj = new DataAccess(this);
    Date dates = new Date(this);

    int percent;
    List<TField> RenewalProducts;
    List<TField> NonRenewalProducts;
    List<String> collateralIdArray = new ArrayList<String>();
    Float SumSecurityAmount;
    Boolean renewableProduct = false;
    Boolean nonRenewableProduct = false;

    @Override
    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        System.out.println("defaultFieldValuesOnHotField  49  :  " + currentRecordId);
        System.out.println("defaultFieldValuesOnHotField  450  :  " + currentRecord);
        MdDealRecord mdDealRec = new MdDealRecord(currentRecord);
        System.out.println("defaultFieldValuesOnHotField  51  : RenewalProducts : " );
        getParametervalues(DataObj, RenewalProducts, NonRenewalProducts);
        System.out.println("defaultFieldValuesOnHotField  54  : RenewalProducts : " + RenewalProducts.toString());
        System.out.println("defaultFieldValuesOnHotField  55  : NonRenewalProducts : " + NonRenewalProducts.toString());
        int securityGroupcount = 0;
        System.out.println("defaultFieldValuesOnHotField  57  : securityGroupcount : " + securityGroupcount);
        LocalRefList securityLoRefGroup = mdDealRec.getLocalRefGroups("L.SECURITY");
        System.out.println("defaultFieldValuesOnHotField  59  : securityLoRefGroup : " + securityLoRefGroup.toString());

        // List<String> removeArrayList = new ArrayList<>();
        for (LocalRefGroup securityIdGroup : securityLoRefGroup) {
            System.out.println("defaultFieldValuesOnHotField  63  : securityIdGroup : " + securityIdGroup.toString());
            String SecurityId = securityIdGroup.getLocalRefField("L.SECURITY").getValue();
            System.out.println("defaultFieldValuesOnHotField  65  : SecurityId : " + SecurityId.toString());
            
//            try {
                System.out.println("defaultFieldValuesOnHotField  68  : try : " );
                CollateralRecord CollateralRec = new CollateralRecord(DataObj.getRecord("COLLATERAL", SecurityId));
                System.out.println("defaultFieldValuesOnHotField  70  : CollateralRec : " + CollateralRec.toString());
                String SecurityAmount = getSecurityAmount(CollateralRec);
                System.out.println("defaultFieldValuesOnHotField  72  : SecurityAmount : " + SecurityAmount);
                if (!nonRenewableProduct && !renewableProduct) {
                    System.out.println("defaultFieldValuesOnHotField  74  : RenewalProducts : " + RenewalProducts.toString());
                    System.out.println("defaultFieldValuesOnHotField  75  : NonRenewalProducts : " + NonRenewalProducts.toString());
                    securityIdGroup.getLocalRefField("L.SECURITY")
                            .setError("Security Account does not belong to Renewable or Non Renewable product");
                    System.out.println("defaultFieldValuesOnHotField  78  : set error" );
                } else {
                    System.out.println("defaultFieldValuesOnHotField  80  : ifelse : ");
                    mdDealRec.getLocalRefGroups("L.SECURITY").remove(securityGroupcount);
                    System.out.println("defaultFieldValuesOnHotField  82  : mdDealRec.getLocalRefGroups : " + mdDealRec.getLocalRefGroups("L.SECURITY").toString());
                    LocalRefGroup setSecAmount = mdDealRec.createLocalRefGroup("L.SECURITY");
                    System.out.println("defaultFieldValuesOnHotField  84  : setSecAmount : " + setSecAmount);
                    setSecAmount.getLocalRefField("L.SECURITY").setValue(SecurityId);
                    System.out.println("defaultFieldValuesOnHotField  86  : setSecAmount : " + setSecAmount.getLocalRefField("L.SECURITY").getValue());
                    setSecAmount.getLocalRefField("L.SEC.AMOUNT").setValue(SecurityAmount);
                    System.out.println("defaultFieldValuesOnHotField  88  : setSecAmount : " + setSecAmount.getLocalRefField("L.SEC.AMOUNT").getValue());
                    mdDealRec.getLocalRefGroups("L.SECURITY").add(securityGroupcount, setSecAmount);
                    System.out.println("defaultFieldValuesOnHotField  90  : mdDealRec.getLocalRefGroups : " + mdDealRec.getLocalRefGroups("L.SECURITY").toString());
                }
/*            } catch (T24CoreException e) {
                System.out.println("defaultFieldValuesOnHotField  93  : catch : ");
                e.printStackTrace();
//                throw new T24CoreException("", "EB-BG.NOSECURITY.NSB");
            }
*/            System.out.println("defaultFieldValuesOnHotField  97  : catch : ");
            securityGroupcount += 1;
        }
        System.out.println("defaultFieldValuesOnHotField  100  : catch : ");
        currentRecord.set(mdDealRec.toStructure());
    }

/*    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub.

        // DEFAULT Total security amount.
        MdDealRecord mdDealRec = new MdDealRecord(currentRecord);

        LocalRefList jointCustomer = mdDealRec.getLocalRefGroups("L.CUST.JOINT");
        List<String> collateralIdArray = getcollateralIdArray(jointCustomer);
        
        for (String collateralId : collateralIdArray){
            try {
                CollateralRecord CollateralRec = new CollateralRecord(DataObj.getRecord("COLLATERAL", collateralId));
                String SecurityAmount = getSecurityAmount(CollateralRec);
                if (!nonRenewableProduct && !nonRenewableProduct) {
                    mdDealRec.getLocalRefField("L.SECURITY")
                            .setError("Security Account does not belong to Renewable or Non Renewable product");
                } else {
                    mdDealRec.getLocalRefGroups("L.SECURITY").remove(securityGroupcount);
                    LocalRefGroup setSecAmount = mdDealRec.createLocalRefGroup("L.SECURITY");
                    setSecAmount.getLocalRefField("L.SECURITY").setValue(SecurityId);
                    setSecAmount.getLocalRefField("L.SEC.AMOUNT").setValue(SecurityAmount);
                    mdDealRec.getLocalRefGroups("L.SECURITY").add(securityGroupcount, setSecAmount);
                }
            } catch (T24CoreException e) {
                // e.printStackTrace();
                throw new T24CoreException("", "EB-BG.NOSECURITY.NSB");
            }
        }
        
        
        
        int securityCount = 0;
        for (LocalRefGroup securityIdGroup : mdDealRec.getLocalRefGroups("L.SECURITY")) {
            String SecurityAmount = securityIdGroup.getLocalRefField("L.SEC.AMOUNT").getValue();
            if (securityCount == 0) {
                SumSecurityAmount = Float.parseFloat(SecurityAmount);
            } else {
                SumSecurityAmount = SumSecurityAmount + Float.parseFloat(SecurityAmount);
            }
            securityCount += 1;
        }
        mdDealRec.getLocalRefField("L.SEC.TOTAL").setValue(String.valueOf(SumSecurityAmount));

        currentRecord.set(mdDealRec.toStructure());
    }
*/
    private String getSecurityAmount(CollateralRecord CollateralRec) {
        AccountRecord AccountRec;
        AaArrangementRecord AaArrRec;

        String arrangement = CollateralRec.getApplicationId().getValue();
        String accountNumber = null;
        if (arrangement.substring(0, 2).equals("AA")) {
            AaArrRec = new AaArrangementRecord(DataObj.getRecord("AA.ARRANGEMENT", arrangement));
            accountNumber = AaArrRec.getLinkedAppl(0).getLinkedApplId().getValue();
            AccountRec = new AccountRecord(DataObj.getRecord("ACCOUNT", accountNumber));
        } else {
            accountNumber = arrangement;
            AccountRec = new AccountRecord(DataObj.getRecord("ACCOUNT", accountNumber));
            arrangement = AccountRec.getArrangementId().getValue();
            AaArrRec = new AaArrangementRecord(DataObj.getRecord("AA.ARRANGEMENT", arrangement));
        }

        // ******************************************************************************************************
        // whether the renewable/nonrenewable products to be compared from
        // Product or ProductGroup or ProductLine
        for (ProductClass Product : AaArrRec.getProduct()) {
            percent = getPercentfromProduct(Product.getProduct().getValue(), RenewalProducts, NonRenewalProducts);
        }
        // ******************************************************************************************************
        String workingBalance = AccountRec.getWorkingBalance().getValue();
        String securityAmount = getSecurityAmountPercent(workingBalance, percent);
        return securityAmount;
    }

    private int getPercentfromProduct(String Product, List<TField> RenewalProducts, List<TField> NonRenewalProducts) {

        for (TField RenewalProduct : RenewalProducts) {
            if (Product.equals(RenewalProduct.getValue())) {
                percent = 100;
                renewableProduct = true;
            }
        }
        for (TField NonRenewalProduct : NonRenewalProducts) {
            if (Product.equals(NonRenewalProduct.getValue())) {
                percent = 100;
                nonRenewableProduct = true;
            }
        }
        return percent;
    }

    private String getSecurityAmountPercent(String WorkingBalance, int Percent) {
        Float SecurityAmount = (Float.parseFloat(WorkingBalance) * Percent) / 100;
        return String.valueOf(SecurityAmount);
    }

    private void getParametervalues(DataAccess DataObj, List<TField> RenewalProducts, List<TField> NonRenewalProducts) {
        // GETTING PARAMETER VALUES
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("BANK.GUARANTEE", new String[] { "PRODUCT.RENEWAL", "PRODUCT.NON.RENEWAL" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        this.RenewalProducts = ParamConfig.get("BANK.GUARANTEE").get("PRODUCT.RENEWAL");
        this.NonRenewalProducts = ParamConfig.get("BANK.GUARANTEE").get("PRODUCT.NON.RENEWAL");
    }

/*    private List<String> getcollateralIdArray(LocalRefList jointCustomer){
        List<String> array = new ArrayList<String>(); 
        for ( LocalRefGroup jointCustomerGrp : jointCustomer){
            String eachJointCustomer = jointCustomerGrp.getLocalRefField("L.CUST.JOINT").getValue();
            List<String> collaterIdList = DataObj.selectRecords("", "COLLATERAL", "", "CUSTOMER.ID EQ " + eachJointCustomer);
            if (!collaterIdList.toString().isEmpty()){
                array.addAll(collaterIdList);
            }
        }
        return array;
    }
    */
}

