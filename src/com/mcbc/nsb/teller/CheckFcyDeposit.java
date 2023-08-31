package com.mcbc.nsb.teller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TDate;
import com.temenos.api.TField;
import com.temenos.api.TNumber;
import com.temenos.api.TStructure;
import com.temenos.api.TValidationResponse;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.rates.Currency;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.tafj.api.client.impl.T24Context;

/**
 * This Java program will be used to validate the FCY deposits capture.
 *
 * @author girlow
 *
 */
public class CheckFcyDeposit extends RecordLifecycle {

    public double fcyDepositThreshold;
    public int daysSinceLanding;
    
    @Override
    public TValidationResponse validateRecord(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

        TellerRecord tellerRecord = new TellerRecord(currentRecord);
        TField depositAmount1 = tellerRecord.getAmountFcy2();
        System.out.println("depositAmount-> " + depositAmount1);
        try
        {
            TField arrivalDate = tellerRecord.getLocalRefField("L.ARRIVAL.DT");
            TField declarationFormNo = tellerRecord.getLocalRefField("L.DECLAR.FRM");
            TField depositAmount = tellerRecord.getAmountFcy2();
            System.out.println("depositAmount-> " + depositAmount);
            long calculatedDays = 0;
            
            getParameters();
            
            TField fromCcy = tellerRecord.getCurrency1();
            String toCcy = "USD";
            System.out.println("Currency 1->" + tellerRecord.getCurrency1().getValue());
            double convertedAmt = getExchRateAmount(toCcy, fromCcy.toString(), "1", fromCcy.toString(), Double.parseDouble(depositAmount.toString()));
            System.out.println("Exchange 1000 GBP to EUR GBP-> " + getExchRateAmount("EUR", "GBP", "1", "GBP", 1000)); //GBP TO EUR
            System.out.println("Exchange 1000 GBP to EUR EUR-> " + getExchRateAmount("EUR", "GBP", "1", "EUR", 1000)); //EUR TO GBP
            
            if (!arrivalDate.toString().isEmpty())
            {
                LocalDate arrivalDateFmt = LocalDate.parse(arrivalDate.toString(), formatter);
                LocalDate currentDateFmt = LocalDate.now();
                calculatedDays = ChronoUnit.DAYS.between(arrivalDateFmt, currentDateFmt);
                System.out.println("arrivalDateFmt-> " + arrivalDateFmt);
                System.out.println("currentDateFmt-> " + currentDateFmt);
                System.out.println("calculatedDays-> " + calculatedDays);
                if (calculatedDays <= daysSinceLanding)
                {
                    if (Double.parseDouble(depositAmount.toString()) > fcyDepositThreshold && declarationFormNo.toString().trim().isEmpty())
                    {
                        declarationFormNo.setError("EB-DEC.FORM.MAND.NSB");
                    }
                }
                else
                {
                    arrivalDate.setError("EB-MAX.LAND.DAYS.NSB");
                }
            }
            
            if (convertedAmt > fcyDepositThreshold)
            {
                depositAmount.setOverride("TT-FCY.DEP.THRESHOLD.NSB");
            }
            
        } catch(Exception tellerRecordException)
        {
            
        }
        
        return tellerRecord.getValidationResponse();
    }

    public void getParameters()
    {
        T24Context EcpNsb = new T24Context("EB.COMMON.PARAM.NSB");
        DataAccess DataOjb = new DataAccess(EcpNsb);
        GetParamValueNsb Config = new GetParamValueNsb();
        
        Config.AddParam("TELLER", new String[] { "DEPOSIT.THRESHOLD.FCY", "MAX.DAYS.LANDING" });
        
        Map<String, Map<String, List<TField>>> ParamConfig = Config.GetParamValue(DataOjb);
        
        fcyDepositThreshold = Double.parseDouble(ParamConfig.get("TELLER").get("DEPOSIT.THRESHOLD.FCY").get(0).getValue());
        daysSinceLanding = Integer.parseInt(ParamConfig.get("TELLER").get("MAX.DAYS.LANDING").get(0).getValue());
        System.out.println("fcyDepositThreshold-> " + fcyDepositThreshold);
        System.out.println("daysSinceLanding-> " + daysSinceLanding);
        
    }

    public double getExchRateAmount(String buyCurrency, String sellCurrency, String ccyMarket, String outputCcy, double amount)
    {
        TNumber sellAmount = new TNumber();
        TDate historyDate = null;
        sellAmount.set(amount);
        
        TNumber amtObj;
        Currency exchRate = new Currency(this);
        amtObj = exchRate.calculateRate(buyCurrency, sellCurrency, ccyMarket, outputCcy, historyDate);
        //System.out.println("Amount exchange->" + abcd.getDealAmount().getValue());
        //System.out.println("Amount exchange object->" + abcd.getClass().getFields().toString());
        
        return amount*amtObj.doubleValue();
    }
}
