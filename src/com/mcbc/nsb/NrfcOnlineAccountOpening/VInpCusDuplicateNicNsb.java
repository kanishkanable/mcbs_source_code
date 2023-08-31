package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.records.customer.LegalIdClass;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcancellednicnsb.EbCancelledNicNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VInpCusDuplicateNicNsb extends RecordLifecycle {

    DataAccess dataObj = new DataAccess(this);

    private String Gender;
    private String LegalDobDate;
    private String LegDocNic;
    private String LegDocNicOld;
    // private String LegDocNicMismatch;
//    private String LegDocNicError;
    private Boolean checkNictru = false;
    private Boolean CheckJulian = false;
    private Boolean CheckNicYear = false;
    private Boolean duplicateSet = false;

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        CustomerRecord customerRec = new CustomerRecord(currentRecord);

        // GETTING PARAMETER VALUES
        setParamValues(dataObj);

        for (LegalIdClass li : customerRec.getLegalId()) {
            if ((li.getLegalDocName().getValue().equals(LegDocNic))
                    || (li.getLegalDocName().getValue().equals(LegDocNicOld))) {
                setNoLegalIdError(customerRec, li, dataObj, currentRecordId);
            }
        }

        return customerRec.getValidationResponse();
    }

    // SET ERROR IF LEGAL ID IS EMPTY
    private void setNoLegalIdError(CustomerRecord CustomerRec, LegalIdClass li, DataAccess DataObj,
            String currentRecordId) {
        if (!li.getLegalId().getValue().isEmpty()) {
//            li.getLegalId().setError(LegDocNicError);
//        } else {
            checkNicDocLength(CustomerRec, li);
            checkNicFormat(CustomerRec, li);
            checkDuplicateNic(CustomerRec, li, DataObj, currentRecordId);
            checkCancelledNic(li, DataObj, CustomerRec);
        }
    }

    private void checkNicDocLength(CustomerRecord CustomerRec, LegalIdClass li) {
        String LegalId = li.getLegalId().getValue();
        if ((li.getLegalDocName().getValue().equals(LegDocNic)) && (LegalId.length() != 12)) {
            li.getLegalId().setError("EB-NEW.NIC.ERROR");
        }
        if ((li.getLegalDocName().getValue().equals(LegDocNicOld)) && (LegalId.length() != 10)
                && ((!LegalId.endsWith("V")) || (!LegalId.endsWith("X")))) {
            li.getLegalId().setError("EB-OLD.NIC.ERROR");
        }
    }

    private void checkNicFormat(CustomerRecord CustomerRec, LegalIdClass li) {
        String LegalIDMand = li.getLegalId().getValue();
        checkValidNic(LegalIDMand);
        int LegalIdLength = LegalIDMand.toString().length();
        String NoOfDays = null;
        String LegalYearDob = null;

        if (LegalIdLength == 12) {
            LegalYearDob = LegalIDMand.substring(0, 4);
            NoOfDays = LegalIDMand.substring(4, 7);
        } else if (LegalIdLength == 10) {
            LegalYearDob = "19" + LegalIDMand.substring(0, 2);
            NoOfDays = LegalIDMand.substring(2, 5);
            if ((!LegalIDMand.endsWith("V")) && (!LegalIDMand.endsWith("X"))) {
                li.getLegalId().setError("EB-NIC.FORMAT.NSB");
            }
        } else {
            this.checkNictru = true;
            li.getLegalId().setError("EB-INCORRECT.NIC.NSB");
        }

        if (!checkNictru) {
            int Yearint = Integer.parseInt(LegalYearDob);
            if ((Yearint < 1800) || (Yearint > 2199)) {
                this.CheckNicYear = true;
                li.getLegalId().setError("EB-DOBYEAR.NIC.NSB");
            }
            if (!CheckNicYear) {
                Boolean CheckJulianNumber = CheckJulianNumberNsb(NoOfDays, li);
                if (!CheckJulianNumber) {
                    this.LegalDobDate = getLegalDateofBirth(NoOfDays, LegalYearDob);
                    checkLegalDob(CustomerRec, LegalDobDate);
                    this.Gender = getLegalGender(NoOfDays, LegalYearDob);
                    checkLegalGender(CustomerRec, Gender);
                } else {
                    li.getLegalId().setError("EB-JULIAN.NIC.NSB");
                }
            }
        }
    }

    private void checkLegalDob(CustomerRecord CustomerRec, String LegalDobDate) {
        if (!LegalDobDate.equals(CustomerRec.getDateOfBirth().getValue())) {
            CustomerRec.getDateOfBirth().setError("EB-CUST.DOB.NSB");
        }
    }

    private void checkLegalGender(CustomerRecord CustomerRec, String Gender) {
        if (!Gender.equals(CustomerRec.getGender().getValue())) {
            CustomerRec.getGender().setError("EB-CUST.GENDER.NSB");
        }
    }

    private String getLegalDateofBirth(String NoOfDays, String LegalYearDob) {
        String GenderNoofdays = GetGenderNsb(NoOfDays);
        this.Gender = GenderNoofdays.split("#")[0];
        NoOfDays = GenderNoofdays.split("#")[1];
        String LegalDobDate = GetDateFromNicNsb(LegalYearDob, NoOfDays);
        return LegalDobDate;
    }

    private String getLegalGender(String NoOfDays, String LegalYearDob) {
        String GenderNoofdays = GetGenderNsb(NoOfDays);
        String LegalGender = GenderNoofdays.split("#")[0];
        return LegalGender;
    }

    private boolean CheckJulianNumberNsb(String NoOfDays, LegalIdClass li) {
        int NoofDaysInt = Integer.parseInt(NoOfDays);
        if ((NoofDaysInt < 0 && NoofDaysInt >= 367) || (NoofDaysInt < 500 && NoofDaysInt >= 867)) {
            CheckJulian = true;
        }
        return CheckJulian;
    }

    private String GetGenderNsb(String NoOfDays) {
        int NoofDaysInt = Integer.parseInt(NoOfDays);
        String GenderAndDays = null;
        if (NoofDaysInt > 500) {
            int Juliandays = NoofDaysInt - 500;
            GenderAndDays = "FEMALE#" + String.valueOf(Juliandays);
        } else {
            GenderAndDays = "MALE#" + NoOfDays;
        }
        return GenderAndDays;
    }

    private String GetDateFromNicNsb(String LegalYearDob, String NoOfDays) {
        int NoofDaysInt = Integer.parseInt(NoOfDays);
        String LegalDob = String.valueOf(LegalYearDob) + "-01-01";

        SimpleDateFormat LegalDobFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar CalenderInstance = Calendar.getInstance();
        try {
            CalenderInstance.setTime(LegalDobFormat.parse(LegalDob));
        } catch (ParseException excep) {
            excep.printStackTrace();
        }

        int intLegalYearDob = Integer.parseInt(LegalYearDob);
        if (intLegalYearDob % 4 == 0) {
            // if the year is century
            if (intLegalYearDob % 100 == 0) {

                // if year is divided by 400
                // then it is a leap year
                if (intLegalYearDob % 400 == 0)
                    CalenderInstance.add(5, NoofDaysInt - 1);
                else if (Integer.parseInt(NoOfDays) > 59) {
                    CalenderInstance.add(5, NoofDaysInt - 2);
                } else {
                    CalenderInstance.add(5, NoofDaysInt - 1);
                }
            } else {
                CalenderInstance.add(5, NoofDaysInt - 1);
            }
        } else {
            if (Integer.parseInt(NoOfDays) > 59) {
                CalenderInstance.add(5, NoofDaysInt - 2);
            } else {
                CalenderInstance.add(5, NoofDaysInt - 1);
            }
        }

        String LegalDobDate = LegalDobFormat.format(CalenderInstance.getTime());
        LegalDobDate = LegalDobDate.toString().replace("-", "");

        return LegalDobDate;
    }

    private void checkDuplicateNic(CustomerRecord CustomerRec, LegalIdClass li, DataAccess DataObj,
            String currentRecordId) {
        String nicValue = li.getLegalId().getValue();
        String nicDocValue = li.getLegalDocName().getValue();
        String nicId = null;
        String nicValueNew = null;
        if (nicValue.length() == 12) {
            nicId = nicValue + "-" + nicDocValue;
            setDuplicateLegalIdError(nicId, li, DataObj, currentRecordId);
            if (!duplicateSet) {
                String formattedNewNic = formatnewNictoOldNic(nicValue);
                nicDocValue = "NATIONAL.ID.OLD";

                nicId = formattedNewNic + "X-" + nicDocValue;
                setDuplicateLegalIdError(nicId, li, DataObj, currentRecordId);
                if (!duplicateSet) {
                    nicId = formattedNewNic + "V-" + nicDocValue;
                    setDuplicateLegalIdError(nicId, li, DataObj, currentRecordId);
                }
            }
        } else if (nicValue.length() == 10) {
            nicId = nicValue + "-" + nicDocValue;
            setDuplicateLegalIdError(nicId, li, DataObj, currentRecordId);
            if (nicValue.endsWith("V")) {
                nicValueNew = nicValue.subSequence(0, 9) + "X-" + nicDocValue;
            } else if (nicValue.endsWith("X")) {
                nicValueNew = nicValue.subSequence(0, 9) + "V-" + nicDocValue;
            }
            setDuplicateLegalIdError(nicValueNew, li, DataObj, currentRecordId);

            if (!duplicateSet) {
                String nicNewDocValue = "NATIONAL.ID";
                String oldToNewNicFormat = "19" + nicId.substring(0, 5) + "0" + nicId.substring(5, 9);
                String nicValueNewFormat = oldToNewNicFormat + "-" + nicNewDocValue;
                setDuplicateLegalIdError(nicValueNewFormat, li, DataObj, currentRecordId);
            }
        }
    }

    private void setDuplicateLegalIdError(String NicId, LegalIdClass li, DataAccess DataObj, String currentRecordId) {
        try {
            List<String> CustomerList = DataObj.getConcatValues("CUS.LEGAL.ID", NicId);
            Iterator<String> ListIterator = CustomerList.iterator();
            while (ListIterator.hasNext()) {
                String CustomerId = ListIterator.next();
                if (!CustomerId.equals(currentRecordId)) {
                    li.getLegalId().setError("Duplicate NIC with Customer " + CustomerId);
                    duplicateSet = true;
                }
            }
        } catch (Exception e) {

        }
    }

    private void setParamValues(DataAccess DataObj) {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "LEGAL.AGE.LK.DOC", "LEGAL.AGE.NOTLK.DOC" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        this.LegDocNic = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(0).getValue();
        this.LegDocNicOld = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(1).getValue();
        // this.LegDocNicMismatch =
        // ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(2).getValue();
        // this.LegDocNicError = ParamConfig.get("CUSTOMER").get("LEGAL.AGE.LK.DOC").get(3).getValue();
    }

    private void checkCancelledNic(LegalIdClass li, DataAccess DataObj, CustomerRecord CustomerRec) {
        String NicValue = li.getLegalId().getValue();
        try {
            new EbCancelledNicNsbRecord(DataObj.getRecord("EB.CANCELLED.NIC.NSB", NicValue));
            li.getLegalId().setOverride("EB-CANCELLED.NIC.NSB");
            // setPostingRestrict(CustomerRec);
        } catch (T24CoreException e) {
        }
    }

    private String formatnewNictoOldNic(String oldNic) {
        String FormattedNewNic = null;
        FormattedNewNic = oldNic.substring(2, 7) + oldNic.substring(8, 12);
        return FormattedNewNic;
    }

    private void checkValidNic(String validLegalIdNic) {
        String validNic = validLegalIdNic;
        if (validLegalIdNic.length() == 10) {
            validNic = validLegalIdNic.substring(0, 9);
        }
        Pattern my_pattern = Pattern.compile("[^0-9 ]");
        Matcher my_match = my_pattern.matcher(validNic);
        boolean check = my_match.find();
        if (check) {
            throw new T24CoreException("EB-ERROR.SELECTION", "EB-CUS.INVALID.NIC.NSB");
        }
    }
}
