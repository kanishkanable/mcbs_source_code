package com.mcbc.nsb.teller;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.complex.eb.servicehook.TransactionData;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.ebtickler.EbTicklerRecord;
import com.temenos.t24.api.records.ebtickler.SenderIdClass;
import com.temenos.t24.api.records.ebtickler.ToUserIdClass;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.t24.api.tables.ebttalertparamnsb.EbTtAlertParamNsbRecord;
import com.temenos.t24.api.tables.ebttalertparamnsb.ParamPurposeClass;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class TillLimitAlert extends RecordLifecycle {
    
    Session SessionContext = new Session(this);
    DataAccess da = new DataAccess(this);
    String companyCode = SessionContext.getCompanyId();
    
    EbTicklerRecord TicklerRecord = new EbTicklerRecord();
    SenderIdClass senderData = new SenderIdClass();
    ToUserIdClass receiverData = new ToUserIdClass();
    
    List<TField> overrideList = new ArrayList<TField>();
    List<ParamPurposeClass> purposeList = new ArrayList<ParamPurposeClass>();
    List<TField> userList = new ArrayList<TField>();
    
    @Override
    public void postUpdateRequest(String application, String currentRecordId, TStructure currentRecord, List<TransactionData> transactionData, List<TStructure> currentRecords, TransactionContext transactionContext){

            TellerRecord tellerRecord = new TellerRecord(currentRecord);
            overrideList = tellerRecord.getOverride();
            
            for (int i = 0; i < overrideList.size(); i++) {
                System.out.println(overrideList.get(i).getValue());
                if(overrideList.get(i).getValue().contains("TT-TILL.CATG.EXCEED.THE.LIMIT"))
                {
                    System.out.println("Override Found");
                    break;
                }
            }
            
            String tellerId = tellerRecord.getTellerId1().toString();
            String alertMsg = "Teller ID "+tellerId+" has exceeded till limit.";
                    
            EbTtAlertParamNsbRecord ttAlertRec = new EbTtAlertParamNsbRecord(da.getRecord("EB.TT.ALERT.PARAM.NSB", companyCode));
            purposeList = ttAlertRec.getParamPurpose();
            
            for (int y = 0; y < purposeList.size(); y++) {
                System.out.println(purposeList.get(y).getParamPurpose());
                if("TILL.LIMIT".equals(purposeList.get(y).getParamPurpose().getValue()))
                {
                    System.out.println("Param Found");
                    userList = purposeList.get(y).getUserId();
                    System.out.println("userList-> "+userList.toString());
                    break;
                }
            }
            
            for (int z = 0; z < userList.size(); z++) {
                senderData.setTicklerTxt(alertMsg, 0);
                senderData.setToUserId(receiverData, 0);
                receiverData.setToUserId(userList.get(z).getValue());
                TicklerRecord.setApplication("TELLER");
                TicklerRecord.setSenderId(senderData, 0);
                
                System.out.println("ticklerRecord->" + TicklerRecord.toString());
                
                currentRecords.add(TicklerRecord.toStructure());
                
                TransactionData td = new TransactionData();
                td.setFunction("INPUT");
                td.setNumberOfAuthoriser("0");
                td.setUserName("INPUTT");
                td.setSourceId("GENERIC.OFS.PROCESS");
                td.setVersionId("EB.TICKLER,INPUT.NSB");
                System.out.println("ticklerRecord-> 92 : " + td);
                transactionData.add(td);
                System.out.println("ticklerRecord-> 94 : " + transactionData);
            }        
    }

}
