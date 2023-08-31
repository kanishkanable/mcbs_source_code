package com.mcbc.nsb.helptext;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.servicehook.ServiceData;
import com.temenos.t24.api.complex.eb.servicehook.SynchronousTransactionData;
import com.temenos.t24.api.complex.eb.servicehook.TransactionControl;
import com.temenos.t24.api.hook.system.ServiceLifecycle;
import com.temenos.t24.api.records.filecontrol.FileControlRecord;
import com.temenos.t24.api.records.helptextmainmenu.HelptextMainmenuRecord;
import com.temenos.t24.api.records.helptextmainmenu.IdOfHelpMenuClass;
import com.temenos.t24.api.records.helptextmenu.ApplicationClass;
import com.temenos.t24.api.records.helptextmenu.HelptextMenuRecord;
import com.temenos.t24.api.system.DataAccess;

/**
 * TODO: Document me!
 *
 * @author kalpap
 *
 */
public class BExtractHtmNsb extends ServiceLifecycle {

    List<String> BuildOutput = new ArrayList<String>();
    List<String> Returnval = new ArrayList<String>();
    DataAccess da = new DataAccess(this);

    @Override
    public List<String> getIds(ServiceData serviceData, List<String> controlList) {
        // TODO Auto-generated method stub
        List<String> recIds = da.selectRecords("", "HELPTEXT.MAINMENU", "", "");
        
        return recIds;
    }

    @Override
    public void updateRecord(String id, ServiceData serviceData, String controlItem,
            TransactionControl transactionControl, List<SynchronousTransactionData> transactionData,
            List<TStructure> records) {
        // TODO Auto-generated method stub

        System.out.println("******** FIRST STEP ********");
        HelptextMainmenuRecord HtMmRecord = new HelptextMainmenuRecord(da.getRecord("HELPTEXT.MAINMENU", id));
        List<IdOfHelpMenuClass> MenuIdToBeProcessed_temp = HtMmRecord.getIdOfHelpMenu();

        Session session = new Session(this);
        
        List<String> Output_tmp = new ArrayList<String>();
        for (IdOfHelpMenuClass h : MenuIdToBeProcessed_temp) {
            if (h.getIdOfHelpMenu().getValue().contains("*")) {
                continue;
            }
            // System.out.println("h.getIdOfHelpMenu().getValue() : " +
            // h.getIdOfHelpMenu().getValue());
            Returnval.add(FormatString(
                    id + "#" + h.getIdOfHelpMenu().getValue() + "#" + h.getDescript().get(0).getValue() + "#MENU#"));
            Returnval.addAll(MakeOutputData(id, h.getIdOfHelpMenu().getValue(), Output_tmp));
        }

        System.out.println("******** FINAL STEP ********");

        // BuildOutput = WriteHelpTextMenu(Returnval);

        ArrayList<String> newList = new ArrayList<String>();

        // Traverse through the first list
        for (String element : Returnval) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        //String outputLocation = "/t24appl/nsbdev/t24/bnk/UD/im.images/extract.txt";
        String outputLocation = "/nsbt24/nsbdev/bnk/CLIENT_BUILD/UD/im.images";

        FileWriter myWriter;
        try {
            myWriter = new FileWriter(outputLocation);
            myWriter.write(Returnval.toString());
            myWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
        }
    }

    @Override
    public void processSingleThreaded(ServiceData serviceData) {
        // TODO Auto-generated method stub
        try {
//            List<String> recIds = da.selectRecords("", "HELPTEXT.MAINMENU", "", "");

//            ListIterator<String> recIdsIterator = recIds.listIterator();

//            while (recIdsIterator.hasNext()) {
//                String id = recIdsIterator.next();
                String id = "3";
                HelptextMainmenuRecord HtMmRecord = new HelptextMainmenuRecord(da.getRecord("HELPTEXT.MAINMENU", id));
                List<IdOfHelpMenuClass> MenuIdToBeProcessed_temp = HtMmRecord.getIdOfHelpMenu();

                List<String> Output_tmp = new ArrayList<String>();
                for (IdOfHelpMenuClass h : MenuIdToBeProcessed_temp) {
                    if (h.getIdOfHelpMenu().getValue().contains("*")) {
                        continue;
                    }
                    System.out.println("h.getIdOfHelpMenu().getValue() : " + h.getIdOfHelpMenu().getValue());
                    Returnval.add(FormatString(id + "#" + h.getIdOfHelpMenu().getValue() + "#"
                            + h.getDescript().get(0).getValue() + "#MENU#"));
//                    System.out.println("Returnval 117 : " + Returnval);
                    Returnval.addAll(MakeOutputData(id, h.getIdOfHelpMenu().getValue(), Output_tmp));
                }
                
                System.out.println("Returnval 119 : " + Returnval);
//            }
            System.out.println("******** FINAL STEP ********");

            // BuildOutput = WriteHelpTextMenu(Returnval);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // BuildOutput = WriteHelpTextMenu(Returnval);
        // Create a new ArrayList
        ArrayList<String> newList = new ArrayList<String>();

        // Traverse through the first list
        for (String element : Returnval) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list

        //String outputLocation = "/t24appl/nsbdev/t24/bnk/UD/im.images/extract.txt";
        String outputLocation = "/nsbt24/interfaces/im.images/menuExtract.txt";

        FileWriter myWriter;
        try {
            myWriter = new FileWriter(outputLocation);
            myWriter.write(newList.toString());
            myWriter.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Uncomment and replace with appropriate logger
            // LOGGER.error(e, e);
        }
    }

    private List<String> MakeOutputData(String Parent, String MenuId, List<String> Output_tmp) {
        try {
            HelptextMenuRecord HtmRecord = new HelptextMenuRecord(da.getRecord("HELPTEXT.MENU", MenuId));
            for (ApplicationClass a : HtmRecord.getApplication()) {
                try {
                    new HelptextMenuRecord(
                            da.getRecord("HELPTEXT.MENU", a.getApplication().getValue()));
                    if (!a.getApplication().getValue().contains("*")) {
                        Output_tmp.add(FormatString(
                                Parent + "#" + MenuId + "#" + a.getDescript().get(0).getValue() + "#" + "MENU"));
                        // System.out.println("@@@@@@ Calling Recursive Function
                        // for "+ a.getDescript().get(0).getValue());
                        Output_tmp = MakeOutputData(MenuId, a.getApplication().getValue(), Output_tmp);

                    } else {
                        // System.out.println("**** Contains * "+
                        // a.getDescript().get(0).getValue());
                    }
                } catch (Exception e) {
                    // System.out.println("$$$$$ Returning for "+
                    // a.getDescript().get(0).getValue());
                    String Applid1 = FormatString(Parent + "#" + MenuId + "#" + a.getDescript().get(0).getValue() + "#"
                            + a.getApplication().getValue());
                    Output_tmp.add(Applid1);
                    continue;
                }
            }

        } catch (Exception e) {
            // System.out.println("Exception Occured");
            Output_tmp.add("###");
        }
        return Output_tmp;

    }

    private String FormatString(String MenuIdValue) {
        if (MenuIdValue.equals("###")) {
            return ("");
        }

        String Parent = MenuIdValue.split("#")[0];
        String OpMenu = MenuIdValue.split("#")[1];
        String OpDescription = MenuIdValue.split("#")[2];
        String OpApplication = MenuIdValue.split("#")[3];
        String Output = "";

        if (OpApplication.equalsIgnoreCase("menu")) {
            Output = OpMenu + "#" + OpDescription + "#MENU#";
        } else if ((OpApplication.substring(0, 4).equals("ENQ ")) || (OpApplication.substring(0, 5).equals("QUERY"))) {

            Output = OpMenu + "#" + OpDescription + "#ENQ#" + OpApplication.split(" ")[1];
            // System.out.println("Output 1 : " + Output);
        }

        else if (OpApplication.substring(0, 4).equals("COS ")) {
            Output = OpMenu + "#" + OpDescription + "#COS#" + OpApplication.split(" ")[1];
            // System.out.println("Output 2 : " + Output);
        }

        else if (OpApplication.substring(0, 4).equals("TAB ")) {
            Output = OpMenu + "#" + OpDescription + "#TAB#" + OpApplication.split(" ")[1];
            // System.out.println("Output 3 : " + Output);
        }

        else if (OpApplication.substring(0, 3).equals("PW ")) {
            Output = OpMenu + "#" + OpDescription + "#PW#" + OpApplication.split(" ")[1];
            // System.out.println("Output 3 : " + Output);
        }
        
        else if (OpApplication.contains(",")) {
            Output = OpMenu + "#" + OpDescription + "#VER#" + OpApplication.split(" ")[0];
            // System.out.println("Output 4 : " + Output);
        } else {
            try {
                String FileControlId = OpApplication.split(" ")[1];
                new FileControlRecord(da.getRecord("FILE.CONTROL", FileControlId));
                Output = OpMenu + "#" + OpDescription + "#APPL#" + OpApplication.split(" ")[0];
            } catch (Exception e) {
            }
        }
        Output = Parent + "#" + Output;

        return Output;
    }
}
