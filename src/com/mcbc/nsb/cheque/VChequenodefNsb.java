package com.mcbc.nsb.cheque;

import java.util.ArrayList;
import java.util.List;

import com.temenos.api.TField;
import com.temenos.api.TStructure;
import com.temenos.t24.api.complex.eb.templatehook.TransactionContext;
import com.temenos.t24.api.hook.system.RecordLifecycle;
import com.temenos.t24.api.records.stockregister.StockRegisterRecord;
import com.temenos.t24.api.records.stockregister.SeriesIdClass;
import com.temenos.t24.api.records.teller.TellerRecord;
import com.temenos.t24.api.system.DataAccess;
import com.temenos.t24.api.system.Session;

public class VChequenodefNsb extends RecordLifecycle {
    DataAccess da = new DataAccess(this);
    Session sess = new Session(this);
    TellerRecord tellRec = null;
    StockRegisterRecord stockRec = null;
    String compId = "";
    String chq = "DRAFT";
    String stockId = "";
    List<SeriesIdClass> seriesIdS = new ArrayList<>();
    SeriesIdClass seriesId = null;
    String seriesIdVal = "";
    String chqVal = "";
    String[] splitVal;
    String chqFrst = "";
    List<TField> chqList = new ArrayList<>();

    public void defaultFieldValues(String application, String currentRecordId, TStructure currentRecord,
            TStructure unauthorisedRecord, TStructure liveRecord, TransactionContext transactionContext) {
        tellRec = new TellerRecord(currentRecord);
        compId = sess.getCompanyId();
        stockId = chq + "." + compId;
        try {
            stockRec = new StockRegisterRecord(da.getRecord("STOCK.REGISTER", stockId));
        } catch (Exception e) {
        }
        if (stockRec != null) {
            seriesIdS = stockRec.getSeriesId();
            for (SeriesIdClass seriesId : seriesIdS) {
                seriesIdVal = seriesId.getSeriesId().getValue();
                chqList = seriesId.getSeriesNo();
                for (TField chqSplitted : chqList) {
                    chqVal = chqSplitted.getValue();
                    splitVal = chqVal.split("[-]");
                    chqFrst = splitVal[0];
                }
            }
        }
        tellRec.setStockRegister(stockId);
        tellRec.setSeriesId(seriesIdVal);
        tellRec.setStockNumber(chqFrst);
        currentRecord.set(tellRec.toStructure());
    }
}
