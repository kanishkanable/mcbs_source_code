package com.mcbc.nsb.CustomerCommonUtils;

import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.LocalRefGroup;
import com.temenos.api.LocalRefList;
import com.temenos.api.TField;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class CustGenderFullShortNameNsb {

    private String ShortName;
    private String Gender;
    private List<TField> TitleGenderMale;
    private List<TField> TitleGenderFemale;
    private String Title;
    private String GivenName;
    private String FamilyName;
    private LocalRefGroup grp;
    private String Mnemonic;
    
    public LocalRefGroup GetFullNamegrp() {
        return this.grp;
    }

    public String GetShortName() {
        return this.ShortName;
    }

    public String GetGender() {
        return this.Gender;
    }

    public String GetMnemonic() {
        return this.Mnemonic;
    }

    public void UpdateGenderFullAndShortName(CustomerRecord customerRec, DataAccess DataObj, String currentRecordId) {

        this.Title = customerRec.getTitle().getValue();
//        this.GivenName = CustomerRec.getGivenNames().getValue();
        LocalRefList otherNameList = customerRec.getLocalRefGroups("L.OTHER.NAME");
        int count = 0;
        for (LocalRefGroup otherNameMv : otherNameList){
            String otherNameSv = null;
            otherNameSv = otherNameMv.getLocalRefField("L.OTHER.NAME").getValue();
            if (count == 0) {
                this.GivenName = otherNameSv;
            } else {
                this.GivenName = GivenName + " " + otherNameSv;
            }
            count +=1;
        }
        this.FamilyName = customerRec.getFamilyName().getValue();

        setParameterValues(DataObj);
        setGenderFromTitle(customerRec);
        setFullnameFromGivenFamilyName(customerRec);
        setShortnameFromTitleGivenFamilyName(customerRec);
        setMnemonicValue(customerRec, currentRecordId);
    }

    private void setParameterValues(DataAccess DataObj) {
        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "TITLE.GENDER.MALE", "TITLE.GENDER.FEMALE" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataObj);
        this.TitleGenderMale = ParamConfig.get("CUSTOMER").get("TITLE.GENDER.MALE");
        this.TitleGenderFemale = ParamConfig.get("CUSTOMER").get("TITLE.GENDER.FEMALE");
    }

    // DEFAULT MNEMONIC VALUE
    private void setMnemonicValue(CustomerRecord CustomerRec, String currentRecordId) {
        this.Mnemonic = "C-" + currentRecordId;
    }

    // DEFAULT GENDER BASED ON TITLE
    private void setGenderFromTitle(CustomerRecord CustomerRec) {
        boolean GenderCheck = false;
        String Title = CustomerRec.getTitle().getValue();
        for (int titlecnt = 1; titlecnt < TitleGenderMale.size(); titlecnt++) {
            if (TitleGenderMale.get(titlecnt).getValue().equals(Title)) {
                this.Gender = TitleGenderMale.get(0).getValue();
                GenderCheck = true;
                break;
            }
        }
        if (!GenderCheck) {
            for (int titlecnt = 1; titlecnt < TitleGenderFemale.size(); titlecnt++) {
                if (TitleGenderFemale.get(titlecnt).getValue().equals(Title)) {
                    this.Gender = TitleGenderFemale.get(0).getValue();
                    break;
                }
            }
        }
    }

    private void setFullnameFromGivenFamilyName(CustomerRecord CustomerRec) {
        this.grp = CustomerRec.createLocalRefGroup("L.FULL.NAME");
        grp.getLocalRefField("L.FULL.NAME")
                .setValue(String.valueOf(GivenName.toUpperCase()) + ' ' + FamilyName.toUpperCase());
    }

    private void setShortnameFromTitleGivenFamilyName(CustomerRecord CustomerRec) {
        String[] GivenNameSplit = GivenName.toString().split(" ");
        String ShortNameValue = Character.toString(GivenNameSplit[0].charAt(0));
        for (int count = 1; count < GivenNameSplit.length; count++) {
            ShortNameValue = String.valueOf(ShortNameValue) + " " + GivenNameSplit[count].charAt(0);
        }
        this.ShortName = String.valueOf(Title.toString().toUpperCase()) + ' ' + ShortNameValue.toUpperCase() + ' '
                + FamilyName.toString().toUpperCase();
    }
}