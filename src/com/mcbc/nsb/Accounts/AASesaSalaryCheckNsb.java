package com.mcbc.nsb.Accounts;
import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.Product;
import com.temenos.t24.api.arrangement.PropertyClass;
import com.temenos.t24.api.complex.aa.activityhook.FieldPair;
import com.temenos.t24.api.complex.aa.activityhook.Property;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

/**
 * 
 * @author rajdur
 * 
 * TRANSACTION.CODE should be unique code for salary credit. it is mandatory to compare on this month end cob job to check
 * convert this salary accoount to another product.
 */


public class AASesaSalaryCheckNsb extends ServiceLifecycle {

    // Initialse
    List<String> recIds = null;
    DataAccess da = new DataAccess(this);
    Boolean flag = false;
    Date dateRec = new Date();
    // Date dateRec = new Date(this);
    String todayDate = dateRec.getDates().getToday().getValue().toString();
    String nextwDate = dateRec.getDates().getNextWorkingDay().getValue().toString();
    String lastwDate = dateRec.getDates().getLastWorkingDay().getValue();
    TDate startDate = new TDate("20200401");
    TDate endDate = new TDate("20200417");
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        String producttype = "AR.SAVINGS.ACCOUNT";
        String selectStmt = "WITH PRODUCT.LINE EQ 'ACCOUNTS' AND ARR.STATUS EQ 'AUTH' AND PRODUCT EQ " + producttype;        
        recIds = da.selectRecords("BNK", "AA.ARRANGEMENT", "", selectStmt);
        return recIds;        
    }

    @Override
    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub
        
//        id = "AA20108D1Q2Y";
        if(!id.equalsIgnoreCase("AA20108D1Q2Y")) 
            return;
        
        String aaProduct = "ARACCT.CBU.ACCOUNT";
        
        TStructure aarec = da.getRecord("AA.ARRANGEMENT", id);
        AaArrangementRecord aaarrrec = new AaArrangementRecord(aarec);
        String acId = aaarrrec.getLinkedAppl(0).getLinkedApplId().getValue();
        Account acct = new Account(this);
        acct.setAccountId(acId);        
        List<String> acctEntries = acct.getEntries("BOOK", "", "", "", startDate, endDate);
        for(String acctentries : acctEntries) {
            String stmtid = acctentries;
            TStructure stmtrec = da.getRecord("BNK", "STMT.ENTRY", "", stmtid);
            StmtEntryRecord stmtrecord = new StmtEntryRecord(stmtrec);
            String txncode = stmtrecord.getTransactionCode().getValue();
            String amountlcy = stmtrecord.getAmountLcy().getValue();
            String valuedate = stmtrecord.getValueDate().getValue();            
            if(txncode.equals("258")) {
                flag = true;      
                break;
            }
        }
        
        int arrCusSize = 0;
        
        if(!flag) {
            //post ofs method to change product.
            try {
                
                Product prod = new Product(this);
                prod.setProductId(aaProduct);
                AaProductCatalogRecord aaPrd = prod.getProduct();
                
                com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass fldType1 = new com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass();
                fldType1.setFieldName("OTHER.OFFICER");
                fldType1.setFieldValue("11");                                                
                com.temenos.t24.api.arrangement.PropertyClass propClass = new com.temenos.t24.api.arrangement.PropertyClass(this);
                propClass.setPropertyClassId("OFFICERS");
                List<String> propList = propClass.getPropertyIdsForProduct(aaPrd);                    
                com.temenos.t24.api.records.aaarrangementactivity.PropertyClass officerClass = new com.temenos.t24.api.records.aaarrangementactivity.PropertyClass();
//                officerClass.setProperty(propList.get(0));
                officerClass.setProperty("OFFICERS");
                officerClass.setFieldName(fldType1, 0);
                
                com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass fldType2 = new com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass();
                fldType2.setFieldName("ACCRUAL.RULE");
                fldType2.setFieldValue("BOTH");                
                com.temenos.t24.api.arrangement.PropertyClass propClass1 = new com.temenos.t24.api.arrangement.PropertyClass(this);
                propClass.setPropertyClassId("INTEREST");
                List<String> propList1 = propClass.getPropertyIdsForProduct(aaPrd);                    
                com.temenos.t24.api.records.aaarrangementactivity.PropertyClass intClass = new com.temenos.t24.api.records.aaarrangementactivity.PropertyClass();
//                intClass.setProperty(propList1.get(0));
                intClass.setProperty("ARCRINTEREST");
                intClass.setFieldName(fldType2, 0);
                
                AaArrangementActivityRecord aaarec = new AaArrangementActivityRecord(this);
                aaarec.setArrangement(id);
                aaarec.setEffectiveDate(todayDate);
                aaarec.setActivity("ACCOUNTS-CHANGE.PRODUCT-ARRANGEMENT");
                aaarec.setProduct(aaProduct);
                aaarec.addProperty(officerClass);                
                aaarec.addProperty(intClass);
                
                TransactionData TxnData = new TransactionData();
                TxnData.setVersionId("AA.ARRANGEMENT.ACTIVITY,TEST");
                TxnData.setFunction("INPUT");
                TxnData.setUserName("INPUTTER");
                TxnData.setNumberOfAuthoriser("0");
//                TxnData.setTransactionId(accNum);
                TxnData.setSourceId("AA.CORR");
//                TxnData.setResponseId(accNum);
                transactionData.add(TxnData);                
                records.add(aaarec.toStructure());
                
            } catch (Exception e) {
                
            }                       
        }
    }
}
