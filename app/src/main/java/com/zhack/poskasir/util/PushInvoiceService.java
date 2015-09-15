package com.zhack.poskasir.util;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.zhack.poskasir.model.Transaction;

import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;

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
            int responseCode = con.sendPost(Constant.MAIN_URL + Constant.URL_TRANS, json.toString());

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Utils.writeToFile(getCacheDir().getAbsolutePath() + File.separator + "Log", Utils.convertDate(intent.getStringExtra(Constant.DATE), "dd-MM-yyyy hh:mm:ss") +
                        " " + "Transaction" +
                        " " + intent.getStringExtra(Constant.TRAN_INVOICE) +
                        " " + "SUCCESS");

                if (isInvoiceExist(intent.getStringExtra(Constant.TRAN_INVOICE))) {
                    getContentResolver().delete(ZhackProvider.TRANSACTION_CONTENT_URI,
                            Transaction.TRANS_INVOICE + "=?", new String[]{intent.getStringExtra(Constant.TRAN_INVOICE)});
                }
            } else {
                Utils.writeToFile(getCacheDir().getAbsolutePath() + File.separator + "Log", Utils.convertDate(intent.getStringExtra(Constant.DATE), "dd-MM-yyyy hh:mm:ss") +
                        " " + "Transaction" +
                        " " + intent.getStringExtra(Constant.TRAN_INVOICE) +
                        " " + "FAIL");

                if (!isInvoiceExist(intent.getStringExtra(Constant.TRAN_INVOICE))) {
                    ContentValues values = new ContentValues();
                    values.put(Transaction.TRANS_INVOICE, intent.getStringExtra(Constant.TRAN_INVOICE));
                    values.put(Transaction.TRANS_DATE, intent.getStringExtra(Constant.DATE));
                    values.put(Transaction.TRANS_PRICE, intent.getStringExtra(Constant.TRAN_PRICE));
                    values.put(Transaction.TRANS_TAX, intent.getStringExtra(Constant.TRAN_TAX));
                    getContentResolver().insert(ZhackProvider.TRANSACTION_CONTENT_URI, values);
                }
            }
            Log.i("PushInvoiceService", "Url:" + Constant.MAIN_URL + Constant.URL_TRANS +
                    " Response:" + responseCode + "\nJSON: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isInvoiceExist(String invoiceId) {
        Cursor cursor = getContentResolver().query(ZhackProvider.TRANSACTION_CONTENT_URI,
                Transaction.QUERY_SHORT,
                Transaction.TRANS_INVOICE + "=?", new String[]{invoiceId}, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }
}
