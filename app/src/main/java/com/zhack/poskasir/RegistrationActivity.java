package com.zhack.poskasir;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.HttpConnect;
import com.zhack.poskasir.util.Utils;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by zunaidi.chandra on 06/08/2015.
 */
public class RegistrationActivity extends Activity {

    private double latitude, longitude;
    private TextView mIMEIText, mNoPDText, mRestaurantText, mAddressText;
    private Button mRegisterBtn;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mIMEIText = (TextView) findViewById(R.id.imei_text);
        mNoPDText = (TextView) findViewById(R.id.nopd_text);
        mRestaurantText = (TextView) findViewById(R.id.restaurant_text);
        mAddressText = (TextView) findViewById(R.id.address_text);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mIMEIText.setText(telephonyManager.getDeviceId());

        mNoPDText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 16) {
                    if (Utils.isConnected(getApplicationContext())) {
                        new VerifyNoPDTask(RegistrationActivity.this).execute(mNoPDText.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNoPDText.getText().toString().trim().length() != 16) {
                    Toast.makeText(getApplicationContext(), "No PD harus 16 digit", Toast.LENGTH_SHORT).show();
                } else if (mNoPDText.getText().toString().trim().length() < 1 ||
                        mRestaurantText.getText().toString().trim().length() < 1 ||
                        mAddressText.getText().toString().trim().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Harus diisi", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isConnected(getApplicationContext())) {
                        new RegisterTask(RegistrationActivity.this).execute(mNoPDText.getText().toString(), telephonyManager.getDeviceId());
                    } else {
                        Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        showRegisterIPDialog();
        new LocationTask().execute();
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

    private class VerifyNoPDTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;

        public VerifyNoPDTask(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // params[0] = nopd
            HttpConnect con = new HttpConnect();
            try {
                return con.sendGet(Constant.MAIN_URL + Constant.URL_NOPD_INFO + params[0]);
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
                mRegisterBtn.setEnabled(true);
                mNoPDText.setEnabled(false);
            } else {
                Toast.makeText(getApplicationContext(), "NoPD Salah", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RegisterTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;

        public RegisterTask(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // params[0] = nopd, params[1] = imei
            HttpConnect con = new HttpConnect();
            try {
                JSONObject json = new JSONObject();
                json.put("imei", params[1]);
                json.put("latitude", String.valueOf(latitude));
                json.put("longitude", String.valueOf(longitude));
                json.put("nopd", params[0]);

                return con.sendPost(Constant.MAIN_URL + Constant.URL_REG_IMEI, json.toString());
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
                requestRegistrationStatus();
            } else {
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LocationTask extends AsyncTask<Void, Void, Void> {

        private String bestProvider;
        private List<Address> user = null;
        private Geocoder geocoder;

        @Override
        protected Void doInBackground(Void... params) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            bestProvider = lm.getBestProvider(criteria, false);
            Location location = lm.getLastKnownLocation(bestProvider);

            if (location != null) {
                geocoder = new Geocoder(getApplicationContext());
                try {
                    user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    latitude=(double)user.get(0).getLatitude();
                    longitude=(double)user.get(0).getLongitude();

                    Log.e("RegistrationActivity", "Coordinate = " + String.valueOf(latitude + " / " + longitude));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private void requestRegistrationStatus() {
        // Start new activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        // Save info
        SharedPreferences sp = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
        sp.edit().putString(Constant.IMEI, telephonyManager.getDeviceId()).apply();
        sp.edit().putLong(Constant.NOPD, Long.parseLong(mNoPDText.getText().toString())).apply();
        sp.edit().putString(Constant.RESTAURANT, mRestaurantText.getText().toString()).apply();
        sp.edit().putString(Constant.ADDRESS, mAddressText.getText().toString()).apply();
    }
}
