package com.mcbc.nsb.Accounts;

import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.api.TStructure;
import com.temenos.api.TString;

import com.temenos.t24.api.hook.arrangement.ActivityLifecycle;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.arrangement.accounting.Contract;
import com.temenos.t24.api.complex.aa.activityhook.ArrangementContext;
import com.temenos.t24.api.complex.aa.activityhook.LookupData;
import com.temenos.t24.api.complex.aa.activityhook.TransactionData;
import com.temenos.t24.api.complex.aa.contractapi.BalanceMovement;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.records.aaaccountdetails.*;
import com.temenos.t24.api.records.aaarraccount.AaArrAccountRecord;
import com.temenos.t24.api.records.aaarraccount.AltIdTypeClass;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.CustomerClass;
import com.temenos.t24.api.records.aaarrangementactivity.AaArrangementActivityRecord;
import com.temenos.t24.api.records.aacustomerarrangement.*;
import com.temenos.t24.api.records.aacustomerrelatedarrangements.*;
import com.temenos.t24.api.records.aaproductcatalog.AaProductCatalogRecord;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalRecord;
import com.temenos.t24.api.tables.aaaccountlegal.AaAccountLegalTable;
import com.temenos.t24.api.tables.ebbranchconcatparam.EbBranchConcatParamRecord;
import com.temenos.t24.api.tables.ebbranchconcatparam.EbBranchConcatParamTable;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */
public class DefaultAltIDNsb extends ActivityLifecycle {

    public static String Branch_code;
    String Ac_type;
    public static int Section_number;
    public static int Serial_number;
    public static String check_digit = "3";        

    @Override
    public void defaultFieldValues(AaAccountDetailsRecord accountDetailRecord,
            AaArrangementActivityRecord arrangementActivityRecord, ArrangementContext arrangementContext,
            AaArrangementRecord arrangementRecord, AaArrangementActivityRecord masterActivityRecord,
            TStructure productPropertyRecord, AaProductCatalogRecord productRecord, TStructure record) {

        final Logger LOGGER = Logger.getLogger(DefaultAltIDNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        
        // Account Type (A)
        // No.of Digit - 1
        List<String> productparamvaluelist = Arrays.asList("1.ACCOUNTS", "2.DEPOSITS", "7.LENDING", "8.EASYCASH");
        String productlinevalue = arrangementRecord.getProductLine().getValue();
        String productvalue = arrangementRecord.getProduct(0).toString();
        if (productlinevalue != null) {
            Ac_type = productnamecheck(productparamvaluelist, productlinevalue);
        } else {
            LOGGER.info("Product mismatch for Alternate.ID");
        }

        // Branch Code (B)
        // No.of Digit - 4
        Session session = new Session(this);
        Branch_code = session.getCompanyId().substring(5);

        // Section Number(C) & Serial Number (D)
        // No.of Digit - 2 & No.of Digit - 4
        Section_number = 01;
        Serial_number = 0001;        
        CompanyRecord companyrec = session.getCompanyRecord();
        TField mnemonic = companyrec.getMnemonic();
        String locking_id = ("F" + mnemonic + "." + "ALT.ID");
        DataAccess da = new DataAccess(this);

        try {
            System.out.println("Increment the count +1 in EB.BRANCH.CONCAT.PARAM>FBNK.ALT.ID");
            TStructure branchrec = da.getRecord("EB.BRANCH.CONCAT.PARAM", locking_id);
            LOGGER.info("EB.BRANCH.CONCAT.PARAM read success - " + locking_id);
            EbBranchConcatParamRecord brconcatrecord = new EbBranchConcatParamRecord(branchrec);
            EbBranchConcatParamTable brconcattable = new EbBranchConcatParamTable(this);
            String altsec_num = brconcatrecord.getAltSecNumber().getValue();
            String altser_num = brconcatrecord.getAltSerialNumber().getValue();
            Section_number = Integer.parseInt(altsec_num) + 1;
            Serial_number = Integer.parseInt(altser_num) + 1;
            brconcatrecord.setAltSecNumber(String.valueOf(Section_number));
            brconcatrecord.setAltSerialNumber(String.valueOf(Serial_number));
            brconcattable.write(locking_id, brconcatrecord);            
            
        } catch (Exception e) {
            System.out.println("Creating new record");
            EbBranchConcatParamTable brconcattable = new EbBranchConcatParamTable(this);
            EbBranchConcatParamRecord brconcatrecord = new EbBranchConcatParamRecord();
            brconcatrecord.setAltSecNumber(String.valueOf(Section_number));
            brconcatrecord.setAltSerialNumber(String.valueOf(Serial_number));
            LOGGER.info("new section number - " + brconcatrecord.getAltSecNumber().getValue());
            LOGGER.info("locking_id - " + locking_id);
            try {
                brconcattable.write(locking_id, brconcatrecord);
            } catch (T24IOException e1) {
                System.out.println("Write failed");
            }
        }

        // Check Digit (E)
        // No.of Digit - 3
        // Check digit formula is not clear. hence hard coding the value for timebieng

        // super.defaultFieldValues(accountDetailRecord,
        // arrangementActivityRecord, arrangementContext, arrangementRecord,
        // masterActivityRecord, productPropertyRecord, productRecord, record);

        String Alt_act_id = (Ac_type + Branch_code + String.format("%02d", Section_number) + String.format("%04d", Serial_number) + check_digit);
        LOGGER.info("Alt_act_id - " + Alt_act_id);
        AaArrAccountRecord aaacctrec = new AaArrAccountRecord(record);
        List<AltIdTypeClass> Altidtype = aaacctrec.getAltIdType();
        for(AltIdTypeClass altidtype : Altidtype){
            String altidtypelable = altidtype.getAltIdType().getValue();
            String legacyaccno = altidtype.getAltId().getValue();
            LOGGER.info("altidtypelable - " + altidtypelable);
            if (altidtypelable.equalsIgnoreCase("INFORMIX") && (legacyaccno.isEmpty())){
                altidtype.setAltId(Alt_act_id);
            }                
        }       
        record.set(aaacctrec.toStructure());        
    }

    

    public static String calcheckdigit() {
        int weight = 2;
        int counter = 6;
        int control = 0;
        int i;
        String retval = null;
        int[] strnumber = { Section_number, Serial_number };
        for (i = 6; i >= 1; i--) {
            int tmpint = strnumber[i];
            tmpint = tmpint * weight;
            control = control * tmpint;
            weight = weight * 1;
        }
        return check_digit;
    }

    public static String productnamecheck(List<String> prodvallist, String name) {
        String number = "";
        boolean flag = true;

        for (String prodName : prodvallist) {
            System.out.println("prodName - " + prodName);

            if (prodName.contains(".") && flag) {
                String[] prodNameArray = prodName.split("\\.");

                for (String ss : prodNameArray) {
                    if (ss.equalsIgnoreCase(name)) {
                        flag = false;
                    } else {
                        number = ss;
                    }
                }
            }
        }
        return number;
    }

}
