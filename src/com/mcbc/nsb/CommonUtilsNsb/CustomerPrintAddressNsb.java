package com.mcbc.nsb.CommonUtilsNsb;

import com.temenos.t24.api.records.customer.CustomerRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustomerPrintAddressNsb {

    private String PrintAddress = null;
    
    public String GetPrintAddressNsb() {
        return this.PrintAddress;
    }
    
    public void PrintAddressNsb(CustomerRecord CustomerRec) {
        
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
     
    }
}
