package com.zhack.poskasir.model;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class Transaction {

    public static final String TRANS_ID         = "trans_id";
    public static final String TRANS_INVOICE    = "trans_invoice";
    public static final String TRANS_DATE       = "trans_date";
    public static final String TRANS_PRICE      = "trans_price";
    public static final String TRANS_TAX        = "trans_tax";

    public static final String[] QUERY_SHORT = {
            TRANS_ID,
            TRANS_INVOICE,
            TRANS_DATE,
            TRANS_PRICE,
            TRANS_TAX
    };
    public String invoice;
    public String date;
    public String price;
    public String tax;
}
