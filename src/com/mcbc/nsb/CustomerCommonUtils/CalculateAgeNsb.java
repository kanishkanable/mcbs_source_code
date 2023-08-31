package com.mcbc.nsb.CustomerCommonUtils;

import java.time.LocalDate;
import java.time.Period;

public class CalculateAgeNsb {
    String DOB ;
    String todayT24 ;
   
    
    public CalculateAgeNsb(String DateOfBirth, String TodayDate) {    
            this.DOB = DateOfBirth;
            this.todayT24 = TodayDate;
    }
    
    public int getAgeInteger() { 
        LocalDate StartDate = LocalDate.of( Integer.parseInt( this.DOB.substring(0, 4)), Integer.parseInt(this.DOB.substring(4, 6)),Integer.parseInt( this.DOB.substring(6, 8)));
        LocalDate EndDate = LocalDate.of( Integer.parseInt( this.todayT24.substring(0, 4)), Integer.parseInt(this.todayT24.substring(4, 6)),Integer.parseInt( this.todayT24.substring(6, 8)));
        
       Period diff = Period.between(StartDate, EndDate);
       int Age = diff.getYears();
       return(Age);
    }   
}