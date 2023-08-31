package com.mcbc.nsb.CommonUtilsNsb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.json.JSONObject;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class VConsumeWebservicesNsb {

    HttpURLConnection connection;
    JSONObject json;

    public String ConsumeSwaggerToT24Nsb(String swaggerUrl) throws Exception {
        // TODO Auto-generated method stub

        // This line makes the request
        
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        URL url;
        String result;
        System.out.println("ConsumeSwaggerToT24Nsb  30  : swaggerUrl  :  " + swaggerUrl);
        try {
            url = new URL(swaggerUrl);
            System.out.println("ConsumeSwaggerToT24Nsb  33  : url  :  " + url);
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("ConsumeSwaggerToT24Nsb  35  : url  :  " + connection.toString());
        } catch (Exception e) {
//            throw new T24CoreException(String.valueOf(connection.getResponseCode()) , connection.getResponseMessage().toString() );
            System.out.println("ConsumeSwaggerToT24Nsb  38  : url  :  " );
            return String.valueOf(connection.getResponseCode()) +"*"+ connection.getResponseMessage().toString();
        }
        System.out.println("ConsumeSwaggerToT24Nsb  41  : url  :  " );
        
        InputStreamReader responseStremeReader = null;
                
        try{
            System.out.println("ConsumeSwaggerToT24Nsb  44  : url  :  " );
            responseStremeReader = new InputStreamReader(connection.getInputStream());
            System.out.println("ConsumeSwaggerToT24Nsb  46  : url  :  " );
            result = new BufferedReader(responseStremeReader).lines().collect(Collectors.joining(""));
            System.out.println("ConsumeSwaggerToT24Nsb  48  : url  :  " );
        } catch (Exception e1){
            System.out.println("ConsumeSwaggerToT24Nsb  50  : url  :  " + String.valueOf(connection.getResponseCode()) +"*"+ connection.getResponseMessage().toString());
            System.out.println("ConsumeSwaggerToT24Nsb  44  : url  :  " );
            responseStremeReader = new InputStreamReader(connection.getErrorStream());
            System.out.println("ConsumeSwaggerToT24Nsb  46  : url  :  " );
            result = new BufferedReader(responseStremeReader).lines().collect(Collectors.joining(""));
            System.out.println("ConsumeSwaggerToT24Nsb  48  : url  :  " );
//           throw new T24CoreException("", "Error in Connection");
//           return String.valueOf(connection.getResponseCode()) +"*"+ connection.getResponseMessage().toString();
        }
        System.out.println("ConsumeSwaggerToT24Nsb  54  : result  :  " + result);
        
        System.out.println("ConsumeSwaggerToT24Nsb  60  : result  :  " + result);
        
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        
        
        return result;
    }
}