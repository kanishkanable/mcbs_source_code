package com.mcbc.nsb.CustomerCommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CalenderFunctionsNsb {

    public String AddYearstoDate(String Date, int years) {
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        
        try {
            // Setting the date to the given date
            c.setTime(DateFormat.parse(Date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Number of Days to add
        c.add(Calendar.YEAR, years);
        String newDate = DateFormat.format(c.getTime());
        return newDate;
    }
}
