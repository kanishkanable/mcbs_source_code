package com.mcbc.nsb.Accounts;

import com.temenos.api.TField;
import com.temenos.api.TNumber;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.records.aaaccountdetails.AaAccountDetailsRecord;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.aaarrangement.LinkedApplClass;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.locking.LockingRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.complex.eb.templatehook.InputValue;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.tables.aanomineepoa.AaNomineePoaRecord;
import com.temenos.t24.api.tables.aanomineepoa.AaNomineePoaTable;

import com.temenos.t24.api.tables.aanomineepoansb.AaNomineePoaNsbRecord;
import com.temenos.t24.api.tables.aanomineepoansb.AaNomineePoaNsbTable;
import com.temenos.t24.api.tables.aanomineepoansb.TypeClass;

import com.temenos.t24.api.tables.ebbranchconcatparam.EbBranchConcatParamRecord;
import com.temenos.t24.api.tables.ebbranchconcatparam.EbBranchConcatParamTable;
import com.temenos.tafj.api.client.impl.T24Context;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;

/**
 * TODO: Document me!
 *
 * @author rajdur
 *
 */

// Routine used to default REGISTRATION.REF, SERIAL.NUMBER if TYPE choose.
// default age, shortname, legalid & address if CIF entered.

public class VNomPoaRegDefvalNsb extends RecordLifecycle {

    private String PrintAddress;

    public void defaultFieldValuesOnHotField(String application, String currentRecordId, TStructure currentRecord,
            InputValue currentInputValue, TStructure unauthorisedRecord, TStructure liveRecord,
            TransactionContext transactionContext) {

        final Logger LOGGER = Logger.getLogger(VNomPoaRegDefvalNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling defaultFieldValuesOnHotField routine - ");
        Session session = new Session(this);

        // Year
        LocalDate myObj = LocalDate.now();
        int Year = myObj.getYear();

        // Logic to maintain serial number
        int serial_number = 00001;
        String def_ser_num = String.format("%05d", serial_number);
        String co_code = session.getCompanyId();
        CompanyRecord companyrec = session.getCompanyRecord();
        TField mnemonic = companyrec.getMnemonic();
        String locking_id = ("F" + mnemonic + "." + "RISK.SER.NUM");

        DataAccess da = new DataAccess(this);

        AaNomineePoaNsbRecord NomPoaRec = new AaNomineePoaNsbRecord(currentRecord);
        List<TypeClass> Choosetype = NomPoaRec.getType();

        String HotfieldName = currentInputValue.getFieldName();
        int HotfieldName_mvpos = currentInputValue.getMultiValueIndex().intValue();
        int HotfieldName_svpos = currentInputValue.getSubValueIndex().intValue();
        if (HotfieldName.contains("CUSTOMER.NUMBER")) {
            try {
                TypeClass aa = new TypeClass();                
                String customer_id = Choosetype.get(HotfieldName_mvpos).getCustomerNumber().getValue();
                TStructure custrecord = da.getRecord("CUSTOMER", customer_id);
                CustomerRecord custrec = new CustomerRecord(custrecord);
                PrintAddressNsb(custrec);
                TField Nompoa_age = custrec.getLocalRefField("L.CUST.AGE");
                TField Nompoa_cust_shortname = custrec.getShortName().get(0);
                TField Nompoa_cust_legalid = custrec.getLegalId(0).getLegalId();
                String[] age1 = Nompoa_age.toString().replaceAll("^0*", "").split(" ");
                
//                aa.setCustomerNumber(customer_id);
//                aa.setAge(Nompoa_age);
//                aa.setCustomerName(Nompoa_cust_shortname);
//                aa.setLegalId(Nompoa_cust_legalid);   
//                NomPoaRec.setType(aa, HotfieldName_mvpos);
                
//                Choosetype.get(HotfieldName_mvpos).setAge(Nompoa_age);
//                Choosetype.get(HotfieldName_mvpos).setCustomerName(Nompoa_cust_shortname);
//                Choosetype.get(HotfieldName_mvpos).setLegalId(Nompoa_cust_legalid);
                
                NomPoaRec.getType().get(HotfieldName_mvpos).setAge(age1[0]);
                NomPoaRec.getType().get(HotfieldName_mvpos).setCustomerName(Nompoa_cust_shortname);
                NomPoaRec.getType().get(HotfieldName_mvpos).setLegalId(Nompoa_cust_legalid);                                                   
                try {
//                    NomPoaRec.getType().get(HotfieldName_mvpos).getAddress().get(1).setValue("2nd line");
//                    NomPoaRec.getType().get(HotfieldName_mvpos).getAddress().get(0).setValue("this is testing");
                    if(PrintAddress.length() > 48 && PrintAddress.length() < 96) {
                        NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(0, 47), 0);
                        NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(48, PrintAddress.length()), 1);
                    } else {
                        if(PrintAddress.length() > 96) {
                            NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(0, 47), 0);
                            NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(48, 95), 1);
                            NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(96, PrintAddress.length()), 2);
                        } else {
                        NomPoaRec.getType().get(HotfieldName_mvpos).setAddress(PrintAddress.substring(0, PrintAddress.length()), 0);
                        }
                    }                                            
                } catch(Exception e) {
                    System.out.println("Invalid Customer ID");
                }                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Invalid Customer ID");
            }
        }

/*        
        if (HotfieldName.contains("CUSTOMER.NUMBER")) {
            try {
                for (int i = 0; i < Choosetype.size(); i++) {
                    TypeClass aa = new TypeClass();
                    String customer_id = Choosetype.get(i).getCustomerNumber().getValue();
                    TStructure custrecord = da.getRecord("CUSTOMER", customer_id);
                    CustomerRecord custrec = new CustomerRecord(custrecord);
                    TField Nompoa_age = custrec.getLocalRefField("L.AGE");
                    TField Nompoa_cust_shortname = custrec.getShortName().get(0);
                    TField Nompoa_cust_legalid = custrec.getLegalId(0).getLegalId();

                    aa.setCustomerNumber(customer_id);
                    aa.setAge(Nompoa_age);
                    aa.setCustomerName(Nompoa_cust_shortname);
                    aa.setLegalId(Nompoa_cust_legalid); // Choosetype.set(i,
                                                        // aa);
                    NomPoaRec.setType(aa, i);
                }
                currentRecord.set(NomPoaRec.toStructure());
            } catch (Exception e) {
                System.out.println("Invalid Customer ID");
            }
        }
*/
        
        if (HotfieldName.contains("TYPE")) {
            for (TypeClass choosetype : Choosetype) {
                boolean flag1 = false;
                String regisref = choosetype.getRegistrationRef().getValue();
                String type = choosetype.getType().getValue();
                String SerialNumber = choosetype.getSerialNumber().getValue();
                String RegistrationRef = choosetype.getRegistrationRef().getValue();
                LOGGER.info("String regisref - " + regisref);
                LOGGER.info("String type - " + type);
                LOGGER.info("String SerialNumber - " + SerialNumber);

                if (type.isEmpty()) {
                    TField reg_ref = null;
                    choosetype.setRegistrationRef(reg_ref);
                    choosetype.setSerialNumber(reg_ref);
                }
                if (!(type.isEmpty())) {
                    try {
                        if (SerialNumber.isEmpty() && RegistrationRef.isEmpty()) {
                            System.out.println("Increment the count +1 in EB.BRANCH.CONCAT.PARAM>FBNK.RISK.SER.NUM");
                            TStructure branchrec = da.getRecord("EB.BRANCH.CONCAT.PARAM", locking_id);
                            LOGGER.info("EB.BRANCH.CONCAT.PARAM read success - " + locking_id);
                            EbBranchConcatParamRecord brconcatrecord = new EbBranchConcatParamRecord(branchrec);
                            EbBranchConcatParamTable brconcattable = new EbBranchConcatParamTable(this);
                            String serial_number1 = brconcatrecord.getRiskSerialNumber().toString();
                            LOGGER.info("String serial_number2 - " + serial_number1);
                            serial_number = Integer.parseInt(serial_number1) + 1;
                            String serial_number2 = String.format("%05d", serial_number);
                            brconcatrecord.setRiskSerialNumber(serial_number2);
                            serial_number = Integer.parseInt(serial_number2); //
                            brconcatrecord.setRiskSerialNumber(String.valueOf(serial_number));
                            LOGGER.info("serial_number - " + serial_number);
                            brconcattable.write(locking_id, brconcatrecord);
                            flag1 = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Creating new record");
                        EbBranchConcatParamRecord brconcatrecord = new EbBranchConcatParamRecord();
                        brconcatrecord.setRiskSerialNumber(String.valueOf(def_ser_num));
                        EbBranchConcatParamTable brconcattable = new EbBranchConcatParamTable(this);
                        flag1 = true;
                        try {
                            brconcattable.write(locking_id, brconcatrecord);
                        } catch (T24IOException e1) {
                            System.out.println("Write failed");
                        }
                    }

                    String Registration_temp = null;

                    if (flag1) {
                        if (type.equals("NOMINEE")) {
                            Registration_temp = ("NOMINATION" + '/' + co_code + '/' + Year + '/'
                                    + String.format("%05d", serial_number));
                        } else {
                            Registration_temp = ("POA" + '/' + co_code + '/' + Year + '/'
                                    + String.format("%05d", serial_number));
                        }
                        choosetype.setRegistrationRef(Registration_temp);
                        choosetype.setSerialNumber(String.format("%05d", serial_number));
                    }
                    LOGGER.info("String Registration_temp - " + Registration_temp);

                }

            }
        }
        currentRecord.set(NomPoaRec.toStructure());
    }

    public String PrintAddressNsb(CustomerRecord CustomerRec) {

        String Address = "";
        for (int xAddr = 0; xAddr < CustomerRec.getAddress().size(); xAddr++) {
            String AddressIndex = CustomerRec.getAddress(xAddr).get(0).getValue();
            if (Address.isEmpty()) {
                Address = AddressIndex;
            } else {
                Address = String.valueOf(Address) + ", " + AddressIndex;
            }
            String Street = CustomerRec.getStreet(0).toString();
            String TownCountry = CustomerRec.getTownCountry(0).getValue();
            this.PrintAddress = String.valueOf(Street) + ", " + Address + ", " + TownCountry;
        }
        return PrintAddress;
    }

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        TStructure rec_ids;
        final Logger LOGGER = Logger.getLogger(DefaultAAAcctoffNsb.class.getName());
        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Calling defaultFieldValues routine ");
        DataAccess da = new DataAccess(this);
        AaNomineePoaNsbRecord NomPoaRec = new AaNomineePoaNsbRecord(currentRecord);
        rec_ids = da.getRecord("AA.ARRANGEMENT", currentRecordId);
        LOGGER.info("rec_ids " + rec_ids);
        AaArrangementRecord aaArr = new AaArrangementRecord(rec_ids);
        String acctid = aaArr.getLinkedAppl(0).getLinkedApplId().getValue();
        LOGGER.info("acctid " + acctid);
        NomPoaRec.setAccountNumber(acctid);
        currentRecord.set(NomPoaRec.toStructure());
    }

}
