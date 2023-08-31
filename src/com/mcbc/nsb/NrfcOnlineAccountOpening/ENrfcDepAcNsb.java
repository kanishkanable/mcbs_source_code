package com.mcbc.nsb.NrfcOnlineAccountOpening;

import java.util.List;

import com.temenos.t24.api.complex.eb.enquiryhook.EnquiryContext;
import com.temenos.t24.api.complex.eb.enquiryhook.FilterCriteria;
import com.temenos.t24.api.hook.system.Enquiry;
import com.temenos.t24.api.records.aaarrangement.AaArrangementRecord;
import com.temenos.t24.api.records.account.AccountRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author Devinda De Zoysa
 *
 */
public class ENrfcDepAcNsb extends Enquiry {

    DataAccess da = new DataAccess(this);
    String nrfcFlag = null;
    String arrStatus = null;
    String prodLine = null;
    String arrId = null;

    @Override
    public List<FilterCriteria> setFilterCriteria(List<FilterCriteria> filterCriteria, EnquiryContext enquiryContext) {

        try {
            FilterCriteria fc = new FilterCriteria();
            String s1 = filterCriteria.get(0)
                    .getValue(); /* will have the ACCOUNT NO */

            fc.setFieldname(filterCriteria.get(0).getFieldname());
            fc.setOperand("EQ");

            AccountRecord accRec = new AccountRecord(da.getRecord("ACCOUNT", s1));
            nrfcFlag = accRec.getLocalRefField("L.NRFC.FLG").getValue().toString();

            try {
                fc.setValue(s1);
                if (s1.equals("")) {
                    fc.setValue("-1");
                } else {
                    if (nrfcFlag.equals("YES")) {
                        arrId = accRec.getArrangementId().toString();
                        AaArrangementRecord aaArrRec = new AaArrangementRecord(da.getRecord("AA.ARRANGEMENT", arrId));
                        arrStatus = aaArrRec.getArrStatus().toString();
                        prodLine = aaArrRec.getProductLine().toString();

                        if (arrStatus.equals("AUTH") && prodLine.equals("DEPOSITS")) {
                        } else {
                            fc.setValue("-1");
                        }
                    } else {
                        fc.setValue("-1");
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }

            filterCriteria.set(0, fc);
        } catch (Exception e) {

        }
        return filterCriteria;

    }
}