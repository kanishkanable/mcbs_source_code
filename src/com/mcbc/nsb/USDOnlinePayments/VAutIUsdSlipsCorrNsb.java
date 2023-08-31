package com.mcbc.nsb.USDOnlinePayments;

import java.util.List;

import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.api.exceptions.T24IOException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.paymentorder.PaymentOrderRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebiusdslipsnsb.EbIUsdSlipsNsbRecord;
import com.temenos.t24.api.tables.ebiusdslipsnsb.EbIUsdSlipsNsbTable;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VAutIUsdSlipsCorrNsb extends RecordLifecycle {

    Session session = new Session(this);
    DataAccess dataObj = new DataAccess(this);

    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord,
            List<TransactionData> transactionData, List<TStructure> currentRecords,
            TransactionContext transactionContext) {
        // TODO Auto-generated method stub
        
        PaymentOrderRecord PoRec = new PaymentOrderRecord(currentRecord);
        
        String inputter = null;
        
        //EB.I.USD.SLIPS.NSB
        EbIUsdSlipsNsbRecord eIUsdSlipsRec = new EbIUsdSlipsNsbRecord();
        
/*                -   POST.REF.CORR = Current PO ID
                -   STATUS = SUCCESS
                -   RETURN.CODE = To be made blank
                -   ERR.REASON = To be made blank
*/
                
        EbIUsdSlipsNsbTable eIUsdSlipsTable = new EbIUsdSlipsNsbTable(this);
        try {
            eIUsdSlipsTable.write(currentRecordId, eIUsdSlipsRec);
        } catch (T24IOException e) {
        }

    }
    
}
