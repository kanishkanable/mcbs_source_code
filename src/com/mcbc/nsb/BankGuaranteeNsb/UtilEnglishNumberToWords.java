package com.mcbc.nsb.BankGuaranteeNsb;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.mcbc.nsb.CommonUtilsNsb.GetParamValueNsb;
import com.temenos.api.TField;
import com.temenos.api.exceptions.T24CoreException;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.tables.ebcommonparamnsb.EbCommonParamNsbRecord;
import com.temenos.t24.api.tables.ebcommonparamnsb.ParamNameClass;

public class UtilEnglishNumberToWords {

    private Map<String, Map<String, List<TField>>> paramConfig;
    private List<ParamNameClass> ecpCurrencyParamNameList;
    private String ecpUnits = null;
    private String ecpDecimals = null;
    
    private static final String[] tensNames = { "", " ten", " twenty", " thirty", " forty", " fifty", " sixty",
            " seventy", " eighty", " ninety" };

    private static final String[] numNames = { "", " one", " two", " three", " four", " five", " six", " seven",
            " eight", " nine", " ten", " eleven", " twelve", " thirteen", " fourteen", " fifteen", " sixteen",
            " seventeen", " eighteen", " nineteen" };

    private static String convertLessThanOneThousand(int number) {
        String soFar;

        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;

            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0)
            return soFar;
        return numNames[number] + " hundred" + soFar;
    }

    public static String convert(long number) {
        // 0 to 999 999 999 999
        if (number == 0) {
            return "zero";
        }

        String snumber = Long.toString(number);

        // pad with "0"
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);

        // XXXnnnnnnnnn
        int billions = Integer.parseInt(snumber.substring(0, 3));
        // nnnXXXnnnnnn
        int millions = Integer.parseInt(snumber.substring(3, 6));
        // nnnnnnXXXnnn
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        // nnnnnnnnnXXX
        int thousands = Integer.parseInt(snumber.substring(9, 12));

        String tradBillions;
        switch (billions) {
        case 0:
            tradBillions = "";
            break;
        case 1:
            tradBillions = convertLessThanOneThousand(billions) + " billion ";
            break;
        default:
            tradBillions = convertLessThanOneThousand(billions) + " billion ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
        case 0:
            tradMillions = "";
            break;
        case 1:
            tradMillions = convertLessThanOneThousand(millions) + " million ";
            break;
        default:
            tradMillions = convertLessThanOneThousand(millions) + " million ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
        case 0:
            tradHundredThousands = "";
            break;
        case 1:
            tradHundredThousands = "one thousand ";
            break;
        default:
            tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " thousand ";
        }
        result = result + tradHundredThousands;

        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;

        // remove extra spaces!
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }

    /**
     * testing
     * 
     * @param args
     */

    public String getWord(String numberArgument, DataAccess dataObj, String currency) {

        double number = Float.parseFloat(numberArgument.replace(",", ""));
        int quotient = (int) Math.floor(number);
        int remainder = (int) Math.round((number - quotient) * 100);
        String word = UtilEnglishNumberToWords.convert(quotient);
        getParamValues(currency, dataObj);
        
        word = word + " " + ecpUnits;
        if (remainder > 0) {
            word = word + " " + UtilEnglishNumberToWords.convert(remainder) + " " + ecpDecimals;
        }

        return word;
        /*
         *** zero one sixteen one hundred one hundred eighteen two hundred two
         * hundred nineteen eight hundred eight hundred one one thousand three
         * hundred sixteen one million two millions three millions two hundred
         *** seven hundred thousand nine millions nine millions one thousand one
         * hundred twenty three millions four hundred fifty six thousand seven
         * hundred eighty nine two billion one hundred forty seven millions four
         * hundred eighty three thousand six hundred forty seven three billion
         * ten
         **/
    }
    
    private void getParamValues(String currency, DataAccess dataObj){
        
        EbCommonParamNsbRecord ecpRecord = new EbCommonParamNsbRecord(dataObj.getRecord("EB.COMMON.PARAM.NSB", "AMOUNT.IN.WORDS"));
        ecpCurrencyParamNameList = ecpRecord.getParamName();
        
        if (ecpCurrencyParamNameList.toString().contains(currency)){
            GetParamValueNsb config = new GetParamValueNsb();
            config.AddParam("AMOUNT.IN.WORDS", new String[] {currency});
            paramConfig = config.GetParamValue(dataObj);
            ecpUnits = paramConfig.get("AMOUNT.IN.WORDS").get(currency).get(0).getValue();
            ecpDecimals = paramConfig.get("AMOUNT.IN.WORDS").get(currency).get(1).getValue();
        } else {
            throw new T24CoreException("","Given Currency needs to be added in EB.COMMON.PARAM.NSB > AMOUNT.IN.WORDS ");
        }
        
    }
}
