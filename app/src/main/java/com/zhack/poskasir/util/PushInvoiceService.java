package com.zhack.poskasir.util;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

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

        try {
            JSONObject json = new JSONObject();
            json.put("imei", intent.getStringExtra(Constant.IMEI));
            json.put("tipe", 2);
            json.put("transaksi", intent.getStringExtra(Constant.TRAN_PRICE));
            json.put("pajak", intent.getStringExtra(Constant.TRAN_TAX));
            json.put("noStruk", intent.getStringExtra(Constant.TRAN_INVOICE));
            json.put("nopd", String.valueOf(intent.getLongExtra(Constant.NOPD, 0)));
            int responseCode = con.sendPost(Constant.URL_TRANS, json.toString());

            Log.e("PushInvoiceService", String.valueOf(responseCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
