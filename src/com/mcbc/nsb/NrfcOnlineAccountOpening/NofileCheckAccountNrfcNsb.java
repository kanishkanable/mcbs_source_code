package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.stmtentry.StmtEntryRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 * ENQUIRY TO CHECK IF ACCOUNT EXIST IN T24, WHEN QUERIED FROM NRFC
 * 
 */
public class NofileCheckAccountNrfcNsb extends Enquiry {

    String AccountNumber = null;
    String stmtPrintedId = null;
    String customerName = null;
    String currentBalance = null;
    String transactionNumber = null;
    String lineNumber = null;
    String transactionDate = null;
    String accountSign = null;
    String TransactionAmount = null;
    String ShortName = null;
    String Currency = null;
    String WorkingBalance = null;
    Boolean unauthRecord = false;

    List<String> StmtEntryIdArray = new ArrayList<String>();
    List<String> OutputIds = new ArrayList<String>();
    DataAccess DataObj = new DataAccess(this);

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        // TODO Auto-generated method stub
        
        AccountNumber = getRecordIdfromFc(filterCriteria);

        getAccountDetails(AccountNumber);
        
        if (!unauthRecord) {
            List<String> ListRecordIds = DataObj.getConcatValues("ACCT.STMT.PRINT", AccountNumber);
            Collections.reverse(ListRecordIds);

            for (String RecordId : ListRecordIds) {
                String FormatId = RecordId.split("/")[0];
                String StmtPrintedId = AccountNumber + "-" + FormatId;
                List<String> StmtEntryIds = DataObj.getConcatValues("STMT.PRINTED", StmtPrintedId);
                StmtEntryIdArray.addAll(StmtEntryIds);
                if (StmtEntryIdArray.size() > 5) {
                    break;
                }
            }
            
            updateStmtEntryDetails(StmtEntryIdArray);
        } else {
            this.transactionNumber = "UNAUTHORIZED ACCOUNT EXISTS";
            OutputIds.add(ShortName + "*" + currentBalance + "*" + transactionNumber + "*" + lineNumber + "*"
                    + transactionDate + "*" + accountSign + "*" + TransactionAmount);
        }
        
        return OutputIds;
    }

    private String getRecordIdfromFc(List<FilterCriteria> filterCriteria) {
        String FieldValue = null;
        for (FilterCriteria fieldNames : filterCriteria) {
            String FieldName = fieldNames.getFieldname();
            if (FieldName.equals("ACCOUNT.NUMBER")) {
                FieldValue = fieldNames.getValue();
            }
        }
        return FieldValue;
    }

    private void updateStmtEntryDetails(List<String> StmtEntryIdArray) {
        // String StmtEntryDetails = null;
        this.transactionNumber = null;
        this.transactionDate = null;
        this.lineNumber = null;
        this.TransactionAmount = null;
        this.accountSign = null;

        int Count = 1;
        for (String StmtEntryId : StmtEntryIdArray) {
            if (Count > 5) {
                break;
            }
            StmtEntryRecord StmtEntryRec = new StmtEntryRecord(DataObj.getRecord("STMT.ENTRY", StmtEntryId));
            this.transactionNumber = StmtEntryRec.getOurReference().getValue();
            if (transactionNumber.isEmpty()){
                this.transactionNumber = StmtEntryRec.getTransReference().getValue();
            }

            String transDateTime = StmtEntryRec.getDateTimeRec(0).getValue();
            this.transactionDate = getDateTimeFormat(transDateTime);

            this.lineNumber = "1";
            if (Currency.equals("LKR")) {
                this.TransactionAmount = StmtEntryRec.getAmountLcy().getValue();
            } else {
                this.TransactionAmount = StmtEntryRec.getAmountFcy().getValue();
            }
            try {
                TransactionAmount = TransactionAmount.split("-")[1];
                this.accountSign = "DR";
            } catch (ArrayIndexOutOfBoundsException e) {
                this.accountSign = "CR";
            }
            OutputIds.add(ShortName + "*" + currentBalance + "*" + transactionNumber + "*" + lineNumber + "*"
                    + transactionDate + "*" + accountSign + "*" + TransactionAmount);

            Count += 1;
        }

        // return StmtEntryDetails;
    }

    private void getAccountDetails(String AccountNumber) {
        AccountRecord AccountRec;
        
        if (AccountNumber.substring(0, 2).equals("AA")){
            try{
                AaArrangementRecord AaArrangemetRec = new AaArrangementRecord(DataObj.getRecord("AA.ARRANGEMENT", AccountNumber));
                AccountNumber = AaArrangemetRec.getLinkedAppl(0).getLinkedApplId().getValue();
            } catch (T24CoreException e){
                throw new T24CoreException("", "Account Number Doesnt Exist");
            }
        }
        try{
            try {
                AccountRec = new AccountRecord(DataObj.getRecord("ACCOUNT", AccountNumber));
            } catch (T24CoreException e) {
                AccountRec = new AccountRecord(DataObj.getRecord("BNK", "ACCOUNT", "$NAU", AccountNumber));
                this.unauthRecord = true;
            }
        } catch (T24CoreException e){
            throw new T24CoreException("", "Account Number Doesnt Exist");
        }
        
        this.ShortName = AccountRec.getShortTitle(0).getValue();
        this.Currency = AccountRec.getCurrency().getValue();

        String AcctWorkBal = AccountRec.getWorkingBalance().getValue();
        try {
            AcctWorkBal = AcctWorkBal.split("-")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        this.WorkingBalance = AcctWorkBal;
        this.currentBalance = "(" + Currency + ") " + WorkingBalance;
        
        this.AccountNumber = AccountNumber;
    }

    private String getDateTimeFormat(String transDateTime) {
        String Yy = transDateTime.substring(0, 2);
        String Mm = transDateTime.substring(2, 4);
        String Dd = transDateTime.substring(4, 6);
        String Hh = transDateTime.substring(6, 8);
        String Mins = transDateTime.substring(8, 10);
        String Secs = "00";
        String AmPm = null;
        if (Integer.parseInt(Hh) < 12) {
            AmPm = "AM";
        } else {
            AmPm = "PM";
        }
        String DateTimeFormat = Dd + "/" + Mm + "/" + Yy + " " + Hh + ":" + Mins + ":" + Secs + " " + AmPm;
        return DateTimeFormat;
    }
}