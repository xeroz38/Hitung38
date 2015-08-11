package com.zhack.poskasir.model;

import java.util.ArrayList;

/**
 * Created by zunaidi.chandra on 11/08/2015.
 */
public class Invoice {

    public String id;
    public String restaurant;
    public String address;
    public String date;
    public int pay;
    public ArrayList<POSData> posData;

    public Invoice() {
        posData = new ArrayList<POSData>();
    }
}
