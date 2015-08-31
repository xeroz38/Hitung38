package com.zhack.poskasir;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.HttpConnect;
import com.zhack.poskasir.util.ItemProvider;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;


public class MainActivity extends FragmentActivity {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mLeftLayout;
    private Button mAddDummyBtn, mHeartBeatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set IP from sharedPref
        Constant.MAIN_URL = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE).getString(Constant.IP, "");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_drawer);
        mAddDummyBtn = (Button) findViewById(R.id.add_btn);
        mHeartBeatBtn = (Button) findViewById(R.id.heartbeat_btn);
        mHeartBeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HeartBeatTask(MainActivity.this).execute(getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE).getString(Constant.IMEI, ""));
            }
        });
        mAddDummyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mLeftLayout);

                ContentValues valuesI = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Ayam Goreng");
                valuesI.put(Item.ITEM_IMAGE, "img_1");
                valuesI.put(Item.ITEM_CATEGORY, "Indonesian");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI1 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Fu Yung Hai");
                valuesI.put(Item.ITEM_IMAGE, "img_2");
                valuesI.put(Item.ITEM_CATEGORY, "Chinese");
                valuesI.put(Item.ITEM_PRICE, "20000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI2 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Bim Bim Bap");
                valuesI.put(Item.ITEM_IMAGE, "img_3");
                valuesI.put(Item.ITEM_CATEGORY, "Korean");
                valuesI.put(Item.ITEM_PRICE, "30000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI3 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Sushi");
                valuesI.put(Item.ITEM_IMAGE, "img_4");
                valuesI.put(Item.ITEM_CATEGORY, "Japanese");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI4 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Roti Prata");
                valuesI.put(Item.ITEM_IMAGE, "img_5");
                valuesI.put(Item.ITEM_CATEGORY, "Indian");
                valuesI.put(Item.ITEM_PRICE, "20000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI5 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Bistik Sapi");
                valuesI.put(Item.ITEM_IMAGE, "img_6");
                valuesI.put(Item.ITEM_CATEGORY, "Western");
                valuesI.put(Item.ITEM_PRICE, "30000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI6 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Es Kopyor");
                valuesI.put(Item.ITEM_IMAGE, "img_7");
                valuesI.put(Item.ITEM_CATEGORY, "Minuman");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI7 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Jus Melon");
                valuesI.put(Item.ITEM_IMAGE, "img_8");
                valuesI.put(Item.ITEM_CATEGORY, "Minuman");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues values = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Indonesian");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values1 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Chinese");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values2 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Japanese");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values3 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Korean");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values4 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Indian");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values5 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Western");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values6 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Minuman");
                getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

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
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new MainActivityFragment())
                .commit();
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
                int responseCode = con.sendPost(Constant.MAIN_URL + Constant.URL_BEAT, json.toString());

                return responseCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(getApplicationContext(), "Sukses", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
