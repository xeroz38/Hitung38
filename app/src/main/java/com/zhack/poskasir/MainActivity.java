package com.zhack.poskasir;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.model.Transaction;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.HttpConnect;
import com.zhack.poskasir.util.PushInvoiceService;
import com.zhack.poskasir.util.Utils;
import com.zhack.poskasir.util.ZhackProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLeftLayout;
    private Button mAddDummyBtn, mHeartBeatBtn, mSchedulerBtn, mIpBtn, mLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set IP from sharedPref
        Constant.MAIN_URL = "http://" + getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE).getString(Constant.IP, "");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mAddDummyBtn = (Button) findViewById(R.id.add_btn);
        mHeartBeatBtn = (Button) findViewById(R.id.heartbeat_btn);
        mSchedulerBtn = (Button) findViewById(R.id.scheduler_btn);
        mIpBtn = (Button) findViewById(R.id.ipaddress_btn);
        mLogBtn = (Button) findViewById(R.id.log_btn);
        mHeartBeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HeartBeatTask(MainActivity.this).execute(getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE).getString(Constant.IMEI, ""));
            }
        });
        mAddDummyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyData();
            }
        });
        mSchedulerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
                ArrayList<Transaction> transData = getTransactionListData();
                for (Transaction trans : transData) {
                    Intent intService = new Intent(getApplicationContext(), PushInvoiceService.class);
                    intService.putExtra(Constant.IMEI, sharedPref.getString(Constant.IMEI, ""));
                    intService.putExtra(Constant.NOPD, sharedPref.getLong(Constant.NOPD, 0));
                    intService.putExtra(Constant.DATE, trans.date);
                    intService.putExtra(Constant.TRAN_INVOICE, trans.invoice);
                    intService.putExtra(Constant.TRAN_PRICE, trans.price);
                    intService.putExtra(Constant.TRAN_TAX, trans.tax);
                    startService(intService);
                    Log.i("MainActivity", "Start service scheduler " + trans.invoice + " " + trans.date);
                }
                Log.i("MainActivity", "Array Transaction size : " + transData.size());
            }
        });
        mIpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterIPDialog();
            }
        });
        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogDialog();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
    }

    private ArrayList<Transaction> getTransactionListData() {
        ArrayList<Transaction> list = new ArrayList<Transaction>();
        Cursor cursor = getContentResolver().query(ZhackProvider.TRANSACTION_CONTENT_URI, Transaction.QUERY_SHORT, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int invoiceCol = cursor.getColumnIndexOrThrow(Transaction.TRANS_INVOICE);
            int dateCol = cursor.getColumnIndexOrThrow(Transaction.TRANS_DATE);
            int priceCol = cursor.getColumnIndexOrThrow(Transaction.TRANS_PRICE);
            int taxCol = cursor.getColumnIndexOrThrow(Transaction.TRANS_TAX);

            while (cursor.moveToNext()) {
                Transaction transaction = new Transaction();
                transaction.invoice = cursor.getString(invoiceCol);
                transaction.date = cursor.getString(dateCol);
                transaction.price = cursor.getString(priceCol);
                transaction.tax = cursor.getString(taxCol);

                list.add(transaction);
            }
            return list;
        } else {
            return list = new ArrayList<Transaction>();
        }
    }

    private void showLogDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_loglist_dialog);
        dialog.setTitle(R.string.log);

        final TextView logContent = (TextView) dialog.findViewById(R.id.content_text);
        final Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
        logContent.setText(Utils.readFromFile());
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegisterIPDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_registerip_dialog);
        dialog.setTitle(R.string.address);

        final EditText ip = (EditText) dialog.findViewById(R.id.ip_edit);
        final Button saveBtn = (Button) dialog.findViewById(R.id.ok_btn);

        ip.setText(getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE).getString(Constant.IP, ""));
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip.getText().toString().trim().length() > 0) {
                    // Save sharepref ip address
                    SharedPreferences sp = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
                    sp.edit().putString(Constant.IP, ip.getText().toString()).apply();
                    Constant.MAIN_URL = "http://" + ip.getText().toString();

                    dialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Harus isi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private class HeartBeatTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;

        public HeartBeatTask(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // params[0] = imei
            HttpConnect con = new HttpConnect();
            try {
                JSONObject json = new JSONObject();
                json.put("imei", params[0]);
                json.put("tipe", 1);

                return con.sendPost(Constant.MAIN_URL + Constant.URL_BEAT, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Utils.writeToFile(Utils.convertDate(String.valueOf(System.currentTimeMillis()), "dd-MM-yyyy hh:mm:ss") +
                        " " + "Alert" +
                        " " + "SUCCESS");
                Toast.makeText(getApplicationContext(), "Sukses", Toast.LENGTH_SHORT).show();
            } else {
                Utils.writeToFile(Utils.convertDate(String.valueOf(System.currentTimeMillis()), "dd-MM-yyyy hh:mm:ss") +
                        " " + "Alert" +
                        " " + "FAIL");
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void addDummyData() {
        mDrawerLayout.closeDrawer(mLeftLayout);

        ContentValues valuesI = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Ayam Goreng");
        valuesI.put(Item.ITEM_IMAGE, "img_1");
        valuesI.put(Item.ITEM_CATEGORY, "Indonesian");
        valuesI.put(Item.ITEM_PRICE, "10000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI1 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Fu Yung Hai");
        valuesI.put(Item.ITEM_IMAGE, "img_2");
        valuesI.put(Item.ITEM_CATEGORY, "Chinese");
        valuesI.put(Item.ITEM_PRICE, "20000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI2 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Bim Bim Bap");
        valuesI.put(Item.ITEM_IMAGE, "img_3");
        valuesI.put(Item.ITEM_CATEGORY, "Korean");
        valuesI.put(Item.ITEM_PRICE, "30000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI3 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Sushi");
        valuesI.put(Item.ITEM_IMAGE, "img_4");
        valuesI.put(Item.ITEM_CATEGORY, "Japanese");
        valuesI.put(Item.ITEM_PRICE, "10000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI4 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Roti Prata");
        valuesI.put(Item.ITEM_IMAGE, "img_5");
        valuesI.put(Item.ITEM_CATEGORY, "Indian");
        valuesI.put(Item.ITEM_PRICE, "20000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI5 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Bistik Sapi");
        valuesI.put(Item.ITEM_IMAGE, "img_6");
        valuesI.put(Item.ITEM_CATEGORY, "Western");
        valuesI.put(Item.ITEM_PRICE, "30000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI6 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Es Kopyor");
        valuesI.put(Item.ITEM_IMAGE, "img_7");
        valuesI.put(Item.ITEM_CATEGORY, "Minuman");
        valuesI.put(Item.ITEM_PRICE, "10000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues valuesI7 = new ContentValues();
        valuesI.put(Item.ITEM_TITLE, "Jus Melon");
        valuesI.put(Item.ITEM_IMAGE, "img_8");
        valuesI.put(Item.ITEM_CATEGORY, "Minuman");
        valuesI.put(Item.ITEM_PRICE, "10000");
        getContentResolver().insert(ZhackProvider.ITEM_CONTENT_URI, valuesI);

        ContentValues values = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Indonesian");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values1 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Chinese");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values2 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Japanese");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values3 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Korean");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values4 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Indian");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values5 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Western");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        ContentValues values6 = new ContentValues();
        values.put(ItemGroup.ITEMGROUP_TITLE, "Minuman");
        getContentResolver().insert(ZhackProvider.ITEMGROUP_CONTENT_URI, values);

        int[] img = new int[] {R.drawable.img_1, R.drawable.img_2, R.drawable.img_3,
                R.drawable.img_4, R.drawable.img_5, R.drawable.img_6,
                R.drawable.img_7, R.drawable.img_8};
        for (int i = 1; i <= 8; i++) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), img[i - 1]);
            File file = new File(Environment.getExternalStorageDirectory() + "/poskasir/img", "img_" + i);
            OutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
