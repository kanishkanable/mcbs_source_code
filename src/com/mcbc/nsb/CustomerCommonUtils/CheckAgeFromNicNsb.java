package com.mcbc.nsb.CustomerCommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckAgeFromNicNsb {
    public String GetDateFromNicNsb(String LegalYearDob, int NoOfDays) {
        String LegalDob = String.valueOf(LegalYearDob) + "-01-01";
        SimpleDateFormat LegalDobFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar CalenderInstance = Calendar.getInstance();
        try {
            CalenderInstance.setTime(LegalDobFormat.parse(LegalDob));
        } catch (ParseException excep) {
            excep.printStackTrace();
        }

        CalenderInstance.add(5, NoOfDays-1);
        String LegalDobDate = LegalDobFormat.format(CalenderInstance.getTime());
        LegalDobDate = LegalDobDate.toString().replace("-", "");
        return LegalDobDate;
    }
}