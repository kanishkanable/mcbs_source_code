package com.mcbc.nsb.CustomerNsb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.customer.CustomerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Date;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebblacklistcustnsb.EbBlacklistCustNsbRecord;
import com.temenos.t24.api.tables.ebnoblacklistcustnsb.EbNoblacklistCustNsbRecord;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BCustomerBlacklistNsb extends ServiceLifecycle {

    DataAccess da = new DataAccess(this);
    Date SystemDate = new Date(this);
    String TodayDate = SystemDate.getDates().getToday().getValue();
    // String AgeRecordId = "0103";
    List<String> Returnval = new ArrayList<String>();
    Session SessionContext = new Session(this);
    String Branch = SessionContext.getCompanyId();
    
    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub

        try {
            TStructure BlacklistCustRecord = da.getRecord("EB.BLACKLIST.CUST.NSB", TodayDate);
            EbBlacklistCustNsbRecord EbBlacklistCustNsbRec = new EbBlacklistCustNsbRecord(BlacklistCustRecord);
            for (TField i : EbBlacklistCustNsbRec.getCustomer()) {
                Returnval.add(i.getValue());
            }
        } catch (Exception e) {
            return Returnval;
        }
        return Returnval;
        // return super.getIds(serviceData, controlList);
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stubtransactionControl

        GetParamValueNsb Config = new GetParamValueNsb();
        Config.AddParam("CUSTOMER", new String[] { "CUSTOMER", "BLACKLIST.CUSTOMER" });
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(da);
        String BlacklistNo = ParamConfig.get("CUSTOMER").get("BLACKLIST.CUSTOMER").get(2).getValue();

        CustomerRecord CustomerRec = new CustomerRecord(da.getRecord("CUSTOMER", id));

        Date TodayDt = new Date(this);
        String TodayDtFormat = TodayDt.getDates().getToday().getValue();
        String CustMajId = id + "-" + TodayDtFormat + "-" + Branch;
        Collection<SynchronousTransactionData> ReturTransactionDataList = new ArrayList<SynchronousTransactionData>();

        EbNoblacklistCustNsbRecord NoBlackListRec = new EbNoblacklistCustNsbRecord();
        NoBlackListRec.setCustomer(id, 0);
        NoBlackListRec.setDateCustNobl(TodayDtFormat);
        // CustMajorRecord.
        records.add(NoBlackListRec.toStructure());

        SynchronousTransactionData td = new SynchronousTransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(CustMajId);
        td.setVersionId("EB.NOBLACKLIST.CUST.NSB,UPDATE.NSB");
        ReturTransactionDataList.add(td);

        
        CustomerRec.getLocalRefField("L.BLACK.LIST").setValue(BlacklistNo);
        records.add(CustomerRec.toStructure());

        SynchronousTransactionData td1 = new SynchronousTransactionData();
        td1.setFunction("INPUT");
        td1.setNumberOfAuthoriser("0");
        td1.setTransactionId(id);
        td1.setVersionId("CUSTOMER,NO.BLACKLIST.NSB");
        ReturTransactionDataList.add(td1);
        
        
        transactionData.addAll(ReturTransactionDataList);
    }
}
