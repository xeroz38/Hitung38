package com.zhack.poskasir.model;

import java.util.ArrayList;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class ReportSales {

    public static final String ID       = "reportsales_id";
    public static final String INVOICE  = "reportsales_invoice";
    public static final String PRICE    = "reportsales_price";
    public static final String PAY      = "reportsales_pay";
    public static final String DATE     = "reportsales_date";
    public static final String STATUS   = "reportsales_status";
    public static final String POS_DATA = "reportsales_pos_data";

    public static final String[] QUERY_SHORT = {
            ID,
            INVOICE,
            PRICE,
            PAY,
            DATE,
            STATUS,
            POS_DATA
    };

    public String id;
    public String invoice;
    public int price;
    public int pay;
    public String date;
    public String status;
    public ArrayList<POSData> posData;

    public ReportSales() {
        posData = new ArrayList<POSData>();
    }
}
