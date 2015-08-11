package com.zhack.poskasir;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.HttpConnect;
import com.zhack.poskasir.util.Utils;

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
                        new RegisterTask(RegistrationActivity.this).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        new LocationTask().execute();
    }

    private class RegisterTask extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog dialog;

        public RegisterTask(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            HttpConnect con = new HttpConnect();
            try {
                int responseCodeVerify = con.sendGet(Constant.URL_NOPD_INFO + mNoPDText.getText().toString());
                if (responseCodeVerify == HttpURLConnection.HTTP_OK) {
                    int responseCodeReg = con.sendPost(Constant.URL_REG_IMEI, "imei=" + telephonyManager.getDeviceId() +
                            "&latitude=" + "0" +
                            "&longitude=" + "0" +
                            "&nopd=" + mNoPDText.getText().toString());

                    if (responseCodeReg == HttpURLConnection.HTTP_OK) {
                        return responseCodeReg;
                    }
                }
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
        sp.edit().putLong(Constant.NO_PD, Long.parseLong(mNoPDText.getText().toString())).apply();
        sp.edit().putString(Constant.RESTAURANT, mRestaurantText.getText().toString()).apply();
        sp.edit().putString(Constant.ADDRESS, mAddressText.getText().toString()).apply();
    }
}
