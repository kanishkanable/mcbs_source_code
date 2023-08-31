package com.mcbc.nsb.BankGuaranteeNsb;

import java.time.LocalDate;
import java.time.Period;

import com.temenos.api.TDate;
import com.temenos.api.TNumber;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.mddeal.MdDealRecord;
import com.temenos.t24.api.system.Date;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VDefGuaranteePeriodNsb extends RecordLifecycle {

    Date dates = new Date(this);
    MdDealRecord mdDealRec;

    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub

        mdDealRec = new MdDealRecord(currentRecord);
        // Default ADVICED.EXPIRY.DATE
        String valueDate = mdDealRec.getValueDate().getValue();
        // String maturityDate = mdDealRec.getMaturityDate().getValue();
        String guaranteePeriod = mdDealRec.getLocalRefField("L.GTEE.PERIOD").getValue();
        String advExpiryDate = mdDealRec.getAdviceExpiryDate().getValue();

        if ((!guaranteePeriod.isEmpty())) {
            if ((guaranteePeriod.substring(0, 1).equals("M")) || (guaranteePeriod.subSequence(0, 1).equals("D"))) {
                String expiryDate = getExpiryDateWithGuaranteePeriod(valueDate, guaranteePeriod);
                mdDealRec.setAdviceExpiryDate(expiryDate);
            }
        } else if ((guaranteePeriod.isEmpty()) && (!valueDate.isEmpty()) && (!advExpiryDate.isEmpty())) {
            guaranteePeriod = getGuaranteePeriod(valueDate, advExpiryDate);
            mdDealRec.getLocalRefField("L.GTEE.PERIOD").setValue(guaranteePeriod);
        }

        currentRecord.set(mdDealRec.toStructure());
    }

    private String getExpiryDateWithGuaranteePeriod(String valueDate, String guaranteePeriod) {
        String guaranteePeriodValue = guaranteePeriod.substring(1);
        String expiryDate = null;
        if (guaranteePeriod.substring(0, 1).equals("M")) {
            String formattedDate = valueDate.substring(0, 4) + "-" + valueDate.substring(4, 6) + "-"
                    + valueDate.substring(6, 8);
            LocalDate date = LocalDate.parse(formattedDate);
            LocalDate newDate = date.plusMonths(Long.parseLong(guaranteePeriodValue));
            expiryDate = newDate.toString().replace("-", "");
        } else if (guaranteePeriod.subSequence(0, 1).equals("D")) {
            TDate tValueDate = new TDate(valueDate);
            TNumber tGuaranteePeriod = new TNumber(guaranteePeriodValue);
            TDate newDate = dates.addWorkingDays(tValueDate, tGuaranteePeriod);
            expiryDate = newDate.toString().replace("-", "");
        }
        return expiryDate;
    }

    private String getGuaranteePeriod(String valueDate, String advExpiryDate) {

        LocalDate StartDate = LocalDate.of(Integer.parseInt(valueDate.substring(0, 4)),
                Integer.parseInt(valueDate.substring(4, 6)), Integer.parseInt(valueDate.substring(6, 8)));
        LocalDate EndDate = LocalDate.of(Integer.parseInt(advExpiryDate.substring(0, 4)),
                Integer.parseInt(advExpiryDate.substring(4, 6)), Integer.parseInt(advExpiryDate.substring(6, 8)));

        Period diff = Period.between(StartDate, EndDate);

        int yearsToMonths = 0;
        int years = diff.getYears();
        if (years > 0) {
            yearsToMonths = years * 12;
        }
        int months = diff.getMonths();
        if (yearsToMonths != 0) {
            months = months + yearsToMonths;
        }
        int days = diff.getDays();

        String guaranteePeriod = null;
        if (months > 0) {
            if (months < 100) {
                guaranteePeriod = "M0" + months;
            } else {
                guaranteePeriod = "M" + months;
            }
        } else {
            if (days < 100) {
                guaranteePeriod = "D0" + days;
            } else {
                guaranteePeriod = "D" + days;
            }
        }
        return guaranteePeriod;
    }

    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        mdDealRec = new MdDealRecord(currentRecord);
        String valueDate = mdDealRec.getValueDate().getValue();
        if (valueDate.isEmpty()) {
            mdDealRec.getValueDate().setError("EB-INPUT.MISSING");
        }
        String guaranteePeriod = mdDealRec.getLocalRefField("L.GTEE.PERIOD").getValue();
        String advExpiryDate = mdDealRec.getAdviceExpiryDate().getValue();

        if ((guaranteePeriod.isEmpty()) && (advExpiryDate.isEmpty())) {
            mdDealRec.getLocalRefField("L.GTEE.PERIOD").setError("EB-INPUT.MISSING");
            // mdDealRec.getAdviceExpiryDate().setError("EB-INPUT.MISSING");
        }

        if ((!guaranteePeriod.isEmpty()) && ((!guaranteePeriod.substring(0, 1).equals("M"))
                && (!guaranteePeriod.subSequence(0, 1).equals("D")))) {
            mdDealRec.getLocalRefField("L.GTEE.PERIOD").setError("EB-GURRANTEE.PERIOD.FMT");
        }
        return mdDealRec.getValidationResponse();
    }

}
