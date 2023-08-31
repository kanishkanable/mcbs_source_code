package com.mcbc.nsb.CustomerCommonUtils;

import com.temenos.api.TStructure;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebdistrictnsb.EbDistrictNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class UpdateProvinceFromDistrict {

    private String Province;
    private String District;
    
    public String getDistrict(){
        return this.District;
    }
    
    public String getProvince(){
        return this.Province;
    }
    
    public void AddProvinceFromDistrict(CustomerRecord CustomerRec, DataAccess DataObj) {

        try {
            this.District = CustomerRec.getLocalRefField("L.DISTRICT").getValue();
            TStructure DistrictNsb = DataObj.getRecord("EB.DISTRICT.NSB", District);
            try {
                EbDistrictNsbRecord ProvinceNsb = new EbDistrictNsbRecord(DistrictNsb);
                this.Province = ProvinceNsb.getProvince().toString();
            } catch (Exception e) {
                CustomerRec.getLocalRefField("L.DISTRICT").setError("EB-DISTRICT.INC.NSB");
            }
        } catch (Exception e) {
            CustomerRec.getLocalRefField("L.DISTRICT").setError("EB-DISTRICT.NSB");
        }
    }
}
