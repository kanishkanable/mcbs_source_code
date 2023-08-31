package com.mcbc.nsb.CustomerNsb;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.helptextmenu.HelptextMenuRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BHelptextMenuNsb extends ServiceLifecycle {

    DataAccess da = new DataAccess(this);
    
    @Override
    public String getTableName(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        
        return "F.HELPTEXT.MENU";
    }


    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub
        
        System.out.println("ID1  :  " + id);
        HelptextMenuRecord HtMRecord = new HelptextMenuRecord(da.getRecord("HELPTEXT.MENU", id));
        String recordId = id + ".NSB";
        System.out.println("ID5  :  " + recordId);
        records.add(HtMRecord.toStructure());
        
        SynchronousTransactionData td = new SynchronousTransactionData();
        td.setFunction("INPUT");
        td.setNumberOfAuthoriser("0");
        td.setSourceId("GENERIC.OFS.PROCESS");
        td.setTransactionId(recordId);
        td.setVersionId("HELPTEXT.MENU,CHANGE");
        System.out.println("ID6  :  " + td);
        transactionData.add(td);
        System.out.println("ID7  :  " + td);
    }
}
