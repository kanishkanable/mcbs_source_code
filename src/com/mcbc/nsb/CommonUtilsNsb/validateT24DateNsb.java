package com.mcbc.nsb.CommonUtilsNsb;

import com.ibm.icu.text.SimpleDateFormat;
import com.temenos.api.exceptions.T24CoreException;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class validateT24DateNsb {

    public void checkDate(String date) {
        
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
        DateFormat.setLenient(false);
        try
        {
            DateFormat.parse(date);
        } catch (Exception e) {
          throw new T24CoreException("", "Invalid date");  
        }
    }
}
