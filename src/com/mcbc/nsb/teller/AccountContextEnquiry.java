package com.mcbc.nsb.teller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaprddesaccount.AaPrdDesAccountRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.records.aclockedevents.AcLockedEventsRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.imdocumentimage.ImDocumentImageRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class AccountContextEnquiry extends Enquiry {

    DataAccess dataAccess = new DataAccess(this);
    Contract accountContract = new Contract(this);

    String aaId = "";
    String customerId = "";
    String photoPath = "";
    String signaturePath = "";
    String passbookSerialNo = "";
    String operativeInstructions = "";
    String inoperativeMarker = "";
    String customerName = "";
    String postingRestrict = "";
    String legalId = "";
    String blockedFunds = "";
    String accountType = "";
    String capitalOutstanding = "";
    String interestOutstanding = "";
    String penaltyInterest = "";
    String originatingBranch = "";
    String accruedPrincipalInt = "";
    String accruedPenaltyInt = "";
    List<String> outArray = new ArrayList<String>();
    AccountRecord acctRecord;
    StringBuilder custSignatures = new StringBuilder("");
    StringBuilder custPhotos = new StringBuilder("");

    @Override
    public List<String> setIds(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {
        System.out.println("*** setIds *** line 59 ");
        String accountId = filterCriteria.get(0).getValue();
        System.out.println("*** setIds *** line 61 :  " + accountId);

        try {
            System.out.println("*** setIds *** line 64 ");
            acctRecord = new AccountRecord(dataAccess.getRecord("ACCOUNT", accountId));
            System.out.println("*** setIds *** line 66 ");
            customerId = acctRecord.getCustomer().getValue();
            System.out.println("*** setIds *** line 68 : " + customerId);
            aaId = acctRecord.getArrangementId().getValue();
            System.out.println("*** setIds *** line 70 : " + aaId);
            inoperativeMarker = acctRecord.getInactivMarker().getValue();
            System.out.println("*** setIds *** line 72 : " + inoperativeMarker);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 74 ");
            throw new T24CoreException("", "Invalid Account number");
        }
        System.out.println("*** setIds *** line 77 ");
        AaPrdDesAccountRecord aaPrdDesAcct = new AaPrdDesAccountRecord();
        System.out.println("*** setIds *** line 79 ");
        if (!aaId.isEmpty()) {
            System.out.println("*** setIds *** line 81 ");
            accountContract.setContractId(aaId);
            System.out.println("*** setIds *** line 83 ");
            aaPrdDesAcct = accountContract.getAccountCondition("BALANCE");
            System.out.println("*** setIds *** line 85 : " + aaPrdDesAcct);
            // Get the property id here - KALPAP
        }
        System.out.println("*** setIds *** line 88 ");

        try {
            System.out.println("*** setIds *** line 91 ");
            passbookSerialNo = aaPrdDesAcct.getLocalRefField("L.OPE.INST").getValue();
            System.out.println("*** setIds *** line 93 : " + passbookSerialNo);
            operativeInstructions = aaPrdDesAcct.getLocalRefField("L.PASSBOOK.SERIAL.NO").getValue();
            System.out.println("*** setIds *** line 95 : " + operativeInstructions);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 1091 : ");
        }

        try {
            capitalOutstanding = accountContract.getBalanceMovements("CURBALANCE", "VALUE").get(0).getBalance()
                    .toString();
            System.out.println("*** setIds *** line 97 : " + capitalOutstanding);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 109 2: ");
        }
        
        try {
            interestOutstanding = accountContract.getBalanceMovements("DUEBALAANCE", "VALUE").get(0).getBalance()
                    .toString();
            System.out.println("*** setIds *** line 101 : " + interestOutstanding);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 1093 : ");
        }
        try {
            accruedPrincipalInt = accountContract.getBalanceMovements("ACCCRINTEREST", "VALUE").get(0).getBalance()
                    .toString();
            System.out.println("*** setIds *** line 104 : " + accruedPrincipalInt);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 1094 : ");
        }
        try {
            accruedPenaltyInt = accountContract.getBalanceMovements("ACCDEPOSITINT", "VALUE").get(0).getBalance()
                    .toString();
            System.out.println("*** setIds *** line 107 : " + accruedPenaltyInt);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 1095 : ");
        }

        System.out.println("*** setIds *** line 111 : ");
        // System.out.println("before penalty interest");
        if (!accruedPrincipalInt.isEmpty() && !accruedPenaltyInt.isEmpty()) {
            System.out.println("*** setIds *** line 114 : ");
            penaltyInterest = String
                    .valueOf(Double.parseDouble(accruedPrincipalInt) + Double.parseDouble(accruedPenaltyInt));
            System.out.println("*** setIds *** line 117 : " + penaltyInterest);
        }
        System.out.println("*** setIds *** line 119 : ");
        originatingBranch = acctRecord.getCoCode();
        System.out.println("*** setIds *** line 121 : " + originatingBranch);
        // System.out.println("originatingBranch->" + originatingBranch);
        CustomerRecord custRecord = new CustomerRecord(dataAccess.getRecord("CUSTOMER", customerId));
        System.out.println("*** setIds *** line 124 : ");
        try {
            System.out.println("*** setIds *** line 126 : ");
            postingRestrict = custRecord.getPostingRestrict(0).getValue();
            System.out.println("*** setIds *** line 128 : " + postingRestrict);
            // System.out.println("postingRestrict->" + postingRestrict);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 131 : ");
        }

        try {
            System.out.println("*** setIds *** line 135 : ");
            customerName = custRecord.getShortName(0).getValue();
            System.out.println("*** setIds *** line 137 : " + customerName);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 139 : ");
        }

        try {
            System.out.println("*** setIds *** line 143 : ");
            legalId = custRecord.getLegalId(0).getLegalId().toString();
            System.out.println("*** setIds *** line 145 : " + legalId);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 147 : ");
        }

        System.out.println("*** setIds *** line 150 : ");
        List<String> imDocImgList = dataAccess.selectRecords("", "IM.DOCUMENT.IMAGE", "",
                "WITH (IMAGE.TYPE EQ PHOTOS OR IMAGE.TYPE EQ SIGNATURES) AND IMAGE.REFERENCE EQ " + customerId);
        System.out.println("*** setIds *** line 140 : " + imDocImgList);
        if (!imDocImgList.isEmpty()) {
            System.out.println("*** setIds *** line 142 : " + imDocImgList);
            for (String currentDocImg : imDocImgList) {
                System.out.println("*** setIds *** line 144 : " + currentDocImg);
                ImDocumentImageRecord docImgRec = new ImDocumentImageRecord(
                        dataAccess.getRecord("IM.DOCUMENT.IMAGE", currentDocImg));
                System.out.println("*** setIds *** line 147 : ");
                if (docImgRec.getImageType().toString().equals("PHOTOS")) {
                    System.out.println("*** setIds *** line 149 : ");
                    custPhotos.append(docImgRec.getImage()).append(" ");
                    System.out.println("*** setIds *** line 151 : " + custPhotos);
                } else {
                    System.out.println("*** setIds *** line 153 : ");
                    custSignatures.append(docImgRec.getImage()).append(" ");
                    System.out.println("*** setIds *** line 155 : " + custSignatures);
                }
                System.out.println("*** setIds *** line 157 : ");
            }
            System.out.println("*** setIds *** line 159 : ");
        }
        System.out.println("*** setIds *** line 161 : ");
        photoPath = custPhotos.toString();
        System.out.println("*** setIds *** line 163 : " + photoPath);
        signaturePath = custSignatures.toString();
        System.out.println("*** setIds *** line 165 : " + signaturePath);

        // System.out.println("photoPath->" + photoPath);
        // System.out.println("signaturePath->" + signaturePath);

        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        System.out.println("*** setIds *** line 171 : " + formatter);
        StringBuilder lockedAmount = new StringBuilder("");
        System.out.println("*** setIds *** line 173 : " + lockedAmount);
        List<String> acLockedAmtList = dataAccess.selectRecords("", "AC.LOCKED.EVENTS", "",
                "WITH ACCOUNT.NUMBER EQ " + accountId);
        System.out.println("*** setIds *** line 176 : " + acLockedAmtList);
        if (!acLockedAmtList.isEmpty()) {
            System.out.println("*** setIds *** line 178 : " + acLockedAmtList);
            // System.out.println("acLockedAmtList not empty");
            for (String currentAcLockedAmt : acLockedAmtList) {
                System.out.println("*** setIds *** line 181 : " + currentAcLockedAmt);
                AcLockedEventsRecord acLockedAmt = new AcLockedEventsRecord(
                        dataAccess.getRecord("AC.LOCKED.EVENTS", currentAcLockedAmt));
                System.out.println("*** setIds *** line 184 : " + acLockedAmt);
                try {
                    TField fromDate = acLockedAmt.getFromDate();
                    System.out.println("*** setIds *** line 186 : " + fromDate);
                    TField toDate = acLockedAmt.getToDate();
                    System.out.println("*** setIds *** line 188 : " + toDate);
                    LocalDate fromDateFmt = LocalDate.parse(fromDate.toString(), formatter);
                    System.out.println("*** setIds *** line 190 : " + fromDateFmt);
                    LocalDate toDateFmt = LocalDate.parse(toDate.toString(), formatter);
                    System.out.println("*** setIds *** line 192 : " + toDateFmt);
                    LocalDate currentDateFmt = LocalDate.now();
                    System.out.println("*** setIds *** line 194 : " + currentDateFmt);
                    if (currentDateFmt.compareTo(fromDateFmt) >= 0 && currentDateFmt.compareTo(toDateFmt) <= 0) {
                        System.out.println("*** setIds *** line 196 : " + currentDateFmt);
                        lockedAmount.append(acLockedAmt.getLockedAmount()).append(" ");
                        System.out.println("*** setIds *** line 198 : " + currentDateFmt);
                    }
                } catch (Exception e) {
                    System.out.println("*** setIds *** line 215 : ");
                }
                System.out.println("*** setIds *** line 200 : ");
            }
            System.out.println("*** setIds *** line 202 : ");
        }
        System.out.println("*** setIds *** line 204 : ");
        // System.out.println("aa acct det");

        try {
            System.out.println("*** setIds *** line 208 : ");
            AaAccountDetailsRecord aaAcctDetRecord = new AaAccountDetailsRecord(
                    dataAccess.getRecord("AA.ACCOUNT.DETAILS", aaId));
            System.out.println("*** setIds *** line 211 : ");
            accountType = aaAcctDetRecord.getArrAgeStatus().toString();
            System.out.println("*** setIds *** line 213 : " + accountType);
        } catch (Exception e) {
            System.out.println("*** setIds *** line 215 : ");
        }
        System.out.println("*** setIds *** line 217 : ");
        outArray.add(accountId + "|" + aaId + "|" + customerId + "|" + photoPath + "|" + signaturePath + "|"
                + passbookSerialNo + "|" + operativeInstructions + "|" + inoperativeMarker + "|" + customerName + "|"
                + postingRestrict + "|" + legalId + "|" + blockedFunds + "|" + accountType + "|" + capitalOutstanding
                + "|" + interestOutstanding + "|" + penaltyInterest + "|" + originatingBranch);
        System.out.println("*** setIds *** line 222 : " + outArray);
        return outArray;
    }

}
