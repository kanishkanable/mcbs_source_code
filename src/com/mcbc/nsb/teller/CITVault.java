package com.mcbc.nsb.teller;

import java.lang.reflect.Array;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.company.CompanyRecord;
import com.temenos.t24.api.records.teller.Account1Class;
import com.temenos.t24.api.records.teller.DrDenomClass;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.records.tellerparameter.TellerParameterRecord;
import com.temenos.t24.api.records.tellertransaction.TellerTransactionRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * TODO: Document me!
 *
 * @author girlow
 *
 */
public class CITVault extends RecordLifecycle {

    String paramCurrency;
    String paramCompany;
    String paramAccountNumber1;
    String paramAccountNumber2;
    
    @Override
    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        try
        {
            TellerRecord tellerRecord = new TellerRecord(currentRecord);
            Session SessionContext = new Session((T24Context)this);
            DataAccess da = new DataAccess((T24Context)this);
            String vaultAcctNo = "";
            String tellerTransaction = tellerRecord.getTransactionCode().toString();
            String txnCcy = tellerRecord.getCurrency1().toString();
            String vaultCategory = "";
            String vaultID = "";
            String subDivCode = "";
            
            TellerTransactionRecord tellerTxnRec = new TellerTransactionRecord(da.getRecord("TELLER.TRANSACTION", tellerTransaction));
            vaultCategory = tellerTxnRec.getCatDeptCode1().toString();
            
            TellerParameterRecord tellerParamRec = new TellerParameterRecord(da.getRecord("TELLER.PARAMETER", SessionContext.getCompanyId()));
            vaultID = tellerParamRec.getVaultId(0).getVaultId().getValue();
            
            CompanyRecord CompanyRec = new CompanyRecord(da.getRecord("COMPANY", SessionContext.getCompanyId()));
            subDivCode = CompanyRec.getSubDivisionCode().toString();
            
            vaultAcctNo = txnCcy + vaultCategory + vaultID + subDivCode;
            System.out.println("vaultAcctNo->"+vaultAcctNo);
            
            //tellerRecord.setNarrative2(tellerRecord.getCurrency1().toString() +"-"+ SessionContext.getCompanyId(), 0);

            getParameters(SessionContext.getCompanyId(), tellerRecord.getCurrency1().toString());

            if (paramAccountNumber1 != "" && vaultAcctNo != "")
            {
                ((Account1Class)tellerRecord.getAccount1().get(0)).setAccount1(vaultAcctNo);

                tellerRecord.setAccount2(paramAccountNumber1);

                currentRecord.set(tellerRecord.toStructure());
            }
            
        }catch (Exception tellerRecordException)
        {
            
        }
        
    }
    
    
    
    @SuppressWarnings("unused")
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        TellerRecord currentTellerRecord = new TellerRecord(currentRecord);
        DataAccess da = new DataAccess((T24Context)this);
        
        //TField accountId = ((Account1Class)currentTellerRecord.getAccount1().get(0)).getAccount1();
        //AccountRecord acctRec = new AccountRecord(da.getRecord("ACCOUNT", accountId.toString()));
        
        try
        {
            TField originalTxnRef = currentTellerRecord.getLocalRefField("L.TT.ORIGINATOR");
            if(!originalTxnRef.getValue().isEmpty())
            {
                TellerRecord origRec = new TellerRecord(da.getRecord("TELLER", originalTxnRef.toString()));
                if(origRec == null)
                {
                    originalTxnRef.setOverride("TT-CIT.TXN.MISMATCH.NSB");
                }
                else
                {
                    String currency1 = origRec.getCurrency1().toString();
                    TField currCurrency1 = currentTellerRecord.getCurrency1();
                    String currentCurrency1 = currCurrency1.toString();
                    if(currency1.intern() != currentCurrency1.intern())
                    {
                        //System.out.println("currency not same");
                        //System.out.println("orig->"+currency1);
                        //System.out.println("curr->"+currentTellerRecord.getCurrency1());
                        currCurrency1.setOverride("TT-CIT.CCY.MISMATCH.NSB");
                    }
                    
                    String amountLocal = ((Account1Class)origRec.getAccount1().get(0)).getAmountLocal1().toString();
                    TField currAmountLocal = ((Account1Class)currentTellerRecord.getAccount1().get(0)).getAmountLocal1();
                    String currentAmountLocal = currAmountLocal.toString();
                    if(amountLocal.intern() != currentAmountLocal.intern())
                    {
                        //System.out.println("amount local not same");
                        //System.out.println("orig->"+amountLocal);
                        //System.out.println("curr->"+((Account1Class)currentTellerRecord.getAccount1().get(0)).getAmountLocal1());
                        currAmountLocal.setOverride("TT-CIT.AMT.LOCAL.MISMATCH.NSB");
                    }
                    
                    String amountFCY = ((Account1Class)origRec.getAccount1().get(0)).getAmountFcy1().toString();
                    TField currAmountFCY = ((Account1Class)currentTellerRecord.getAccount1().get(0)).getAmountFcy1();
                    String currentAmountFCY = currAmountFCY.toString();
                    if(amountFCY.intern() != currentAmountFCY.intern())
                    {
                        //System.out.println("amount fcy not same");
                        //System.out.println("orig->"+amountFCY);
                        //System.out.println("curr->"+((Account1Class)currentTellerRecord.getAccount1().get(0)).getAmountFcy1());
                        currAmountFCY.setOverride("TT-CIT.AMT.FCY.MISMATCH.NSB");
                    }
                    
                    String account1 = origRec.getAccount1().toString();
                    List<Account1Class> currAccount1 = currentTellerRecord.getAccount1();
                    String currentAccount1 = currAccount1.toString();
                    if(account1.intern() != currentAccount1.intern())
                    {
                        //System.out.println("account 1 not same");
                        //System.out.println("orig->"+account1);
                        //System.out.println("curr->"+currentTellerRecord.getAccount1());
                        currAccount1.get(0).getAccount1().setOverride("TT-CIT.VAULT.MISMATCH.NSB");
                    }
                    
                    String account2 = origRec.getAccount2().toString();
                    TField currAccount2 = currentTellerRecord.getAccount2();
                    String currentAccount2 = currAccount2.toString();
                    if(account2.intern() != currentAccount2.intern())
                    {
                        //System.out.println("account 2 not same");
                        //System.out.println("orig->"+account2);
                        //System.out.println("curr->"+currentTellerRecord.getAccount2());
                        currAccount2.setOverride("TT-CIT.ACCT.MISMATCH.NSB");
                    }
                    
                    String narrative = ((Account1Class)origRec.getAccount1().get(0)).getNarrative1().toString();
                    List<TField> currNarrative = ((Account1Class)currentTellerRecord.getAccount1().get(0)).getNarrative1();
                    String currentNarrative = currNarrative.toString();
                    if(narrative.intern() != currentNarrative.intern())
                    {
                        //System.out.println("narratie not same");
                        //System.out.println("orig->"+narrative);
                        //System.out.println("curr->"+currentTellerRecord.getAccount1().get(0).getNarrative1());
                        currNarrative.get(0).setOverride("TT-CIT.NARR.MISMATCH.NSB");
                    }
                    
                    try
                    {
                        String branchCode = origRec.getLocalRefField("L.BRANCH.CODE").toString();
                        TField currBranchCode = currentTellerRecord.getLocalRefField("L.BRANCH.CODE");
                        String currentBranchCode = currBranchCode.toString();
                        if(branchCode.intern() != currentBranchCode.intern())
                        {
                            //System.out.println("branch code not same");
                            //System.out.println("orig->"+branchCode);
                            //System.out.println("curr->"+currentTellerRecord.getLocalRefField("L.BRANCH.CODE"));
                            currBranchCode.setOverride("TT-CIT.BRANCH.MISMATCH.NSB");
                        }
                    } catch(Exception localRefException){}
                    
                    
                    List<DrDenomClass> currDrDenomList = currentTellerRecord.getDrDenom();
                    List<DrDenomClass> origDrDenomList = origRec.getDrDenom();
                    ListIterator<DrDenomClass> currDrDenomListIterator = currDrDenomList.listIterator();
                                        
                    for (int i = 0; i < currDrDenomList.size(); i++) {
//                        System.out.println("orig denom 1st->"+origDrDenomList.get(0).getDrUnit().toString());
//                        if (Integer.parseInt(origDrDenomList.get(i).getDrUnit().toString()) > 0 && currDrDenomList.get(i).getDrDenom().toString().intern() != origDrDenomList.get(i).getDrDenom().toString().intern())
//                        {
//                          System.out.println("dr denom not same");
//                        }
                        
//                        if (currDrDenomList.get(i).getDrUnit().toString().intern() != origDrDenomList.get(i).getDrUnit().toString().intern())
//                        {
//                            System.out.println("dr denom unit not same");
//                        }
                        
                    }
                    
                }
            }
        } catch(Exception localRefException){}
        
        return currentTellerRecord.getValidationResponse();
    }


    public void getParameters(String inputCompany, String inputCurrency)
    {
        paramCurrency = "";
        paramCompany = "";
        paramAccountNumber1 = "";
        paramAccountNumber2 = "";
        
        String[] tempValues;

        DataAccess DataOjb = new DataAccess(this);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "CIT.ACCOUNTS" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        for (int i = 0; ParamConfig.get("TELLER").get("CIT.ACCOUNTS").size() > i; i++)
        {
            tempValues = ParamConfig.get("TELLER").get("CIT.ACCOUNTS").get(i).getValue().split("-");
            String currency = Array.get(tempValues, 0).toString();
            //String company = Array.get(tempValues, 1).toString();
            String acct1 = Array.get(tempValues, 1).toString();
            //String acct2 = Array.get(tempValues, 3).toString();
            //if(currency.equals(inputCurrency) && company.equals(inputCompany))
            if(currency.equals(inputCurrency))
            {
                paramAccountNumber1 = acct1;
                System.out.println("paramAccountNumber1->"+paramAccountNumber1);
            }
        }
    }
    
}
