package com.zhack.poskasir.util;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by xeRoz on 8/9/2015.
 */
public class PushInvoiceService extends IntentService {

    public PushInvoiceService() {
        super("PushInvoiceService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpConnect con = new HttpConnect();

        String imei = intent.getStringExtra(Constant.IMEI);
        String invoiceId = intent.getStringExtra(Constant.TRAN_INVOICE);
        String price = intent.getStringExtra(Constant.TRAN_PRICE);
        String tax = intent.getStringExtra(Constant.TRAN_TAX);

        int strObj = 0;
        try {
            strObj = con.sendGet("http://www.lowyat.net/");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("PushInvoiceService", imei);
        Log.e("PushInvoiceService", invoiceId);
        Log.e("PushInvoiceService", price);
        Log.e("PushInvoiceService", "" + strObj);
    }
}
