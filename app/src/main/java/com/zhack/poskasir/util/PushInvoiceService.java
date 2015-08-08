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
        String dataString = intent.getDataString();
        Log.e("##zun", dataString);
    }
}
