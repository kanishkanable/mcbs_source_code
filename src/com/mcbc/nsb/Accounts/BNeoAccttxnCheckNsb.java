package com.mcbc.nsb.Accounts;

import java.util.List;

import com.temenos.api.TDate;
import com.temenos.api.TStructure;
import com.temenos.t24.api.arrangement.Product;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.party.Account;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class BNeoAccttxnCheckNsb extends ServiceLifecycle {

    List<String> recIds = null;
    DataAccess da = new DataAccess(this);
    Boolean flag = false;
    Date dateRec = new Date();
    String todayDate = dateRec.getDates().getToday().getValue().toString();
    String nextwDate = dateRec.getDates().getNextWorkingDay().getValue().toString();
    String lastwDate = dateRec.getDates().getLastWorkingDay().getValue();
    TDate startDate = new TDate("20200401");
    TDate endDate = new TDate("20200417");

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        String selectStmt = "WITH STATUS EQ NEW";
        recIds = da.selectRecords("BNK", "AA.NEO.ACCT.TXN.DRAW.NSB", "", selectStmt);
        return recIds;
    }

    public void postUpdateRequest(String id, ServiceData serviceData, String controlItem,
            List<TransactionData> transactionData, List<TStructure> records) {
        // TODO Auto-generated method stub

        TStructure aarec = null;
        String product = null;
        try {
            aarec = da.getRecord("AA.ARRANGEMENT", id);
        } catch (Exception e) {
            System.out.println("Invalid AA Id");
        }
        AaArrangementRecord aaarrrec = new AaArrangementRecord(aarec);
        product = aaarrrec.getProduct(0).getProduct().getValue();
        String acId = aaarrrec.getLinkedAppl(0).getLinkedApplId().getValue();
        /* Not validating at this stage.
        if(!product.equalsIgnoreCase("NEO.TYPE"))
            return;

        Account acct = new Account(this);
        acct.setAccountId(acId);
        List<String> acctEntries = acct.getEntries("BOOK", "", "", "", startDate, endDate);
        for (String acctentries : acctEntries) {
            String stmtid = acctentries;
            TStructure stmtrec = da.getRecord("BNK", "STMT.ENTRY", "", stmtid);
            StmtEntryRecord stmtrecord = new StmtEntryRecord(stmtrec);
            String txncode = stmtrecord.getTransactionCode().getValue();
            String amountlcy = stmtrecord.getAmountLcy().getValue();
            String valuedate = stmtrecord.getValueDate().getValue();
            if (txncode.equals("258")) {
                flag = true;
                break;
            }
        }
        */
        
        int arrCusSize = 0;

        if (!flag) {
            // post ofs method to change product.
            try {

                Product prod = new Product(this);
                prod.setProductId(product);
                AaProductCatalogRecord aaPrd = prod.getProduct();

                com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass fldType1 = new com.temenos.t24.api.records.aaarrangementactivity.FieldNameClass();
                fldType1.setFieldName("FIXED.RATE");
                fldType1.setFieldValue("4.5");
                com.temenos.t24.api.arrangement.PropertyClass propClass = new com.temenos.t24.api.arrangement.PropertyClass(
                        this);
                propClass.setPropertyClassId("INTEREST");
                List<String> propList = propClass.getPropertyIdsForProduct(aaPrd);
                com.temenos.t24.api.records.aaarrangementactivity.PropertyClass intClass = new com.temenos.t24.api.records.aaarrangementactivity.PropertyClass();
                // officerClass.setProperty(propList.get(0));
                intClass.setProperty("ARCRINTEREST");
                intClass.setFieldName(fldType1, 0);
                
                AaArrangementActivityRecord aaarec = new AaArrangementActivityRecord(this);
                aaarec.setArrangement(id);
                aaarec.setEffectiveDate(todayDate);
                aaarec.setActivity("ACCOUNTS-CHANGE-ARCRINTEREST");
                aaarec.setProduct(product);
                aaarec.addProperty(intClass);                

                TransactionData TxnData = new TransactionData();
                TxnData.setVersionId("AA.ARRANGEMENT.ACTIVITY,TEST");
                TxnData.setFunction("INPUT");
                TxnData.setUserName("INPUTTER");
                TxnData.setNumberOfAuthoriser("0");
                // TxnData.setTransactionId(accNum);
                TxnData.setSourceId("AA.CORR");
                // TxnData.setResponseId(accNum);
                transactionData.add(TxnData);
                records.add(aaarec.toStructure());

            } catch (Exception e) {

            }
        }
    }
}
