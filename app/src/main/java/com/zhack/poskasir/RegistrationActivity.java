package com.zhack.poskasir;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by zunaidi.chandra on 06/08/2015.
 */
public class RegistrationActivity extends Activity {

    private TextView mIMEIText, mNoPDText, mRestaurantText, mAddressText;
    private Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mIMEIText = (TextView) findViewById(R.id.imei_text);
        mNoPDText = (TextView) findViewById(R.id.nopd_text);
        mRestaurantText = (TextView) findViewById(R.id.restaurant_text);
        mAddressText = (TextView) findViewById(R.id.address_text);
        mRegisterBtn = (Button) findViewById(R.id.register_btn);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
    }

    private class RegisterTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog dialog;

        public RegisterTask(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpConnect con = new HttpConnect();
            try {
                String strObj = con.sendGet("http://www.lowyat.net/");
                Log.e("##zun", strObj);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            super.onPostExecute(isValid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (isValid) {
                requestRegistrationStatus();
            }
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
        sp.edit().putLong(Constant.NO_PD, Long.parseLong(mNoPDText.getText().toString())).apply();
        sp.edit().putString(Constant.RESTAURANT, mRestaurantText.getText().toString()).apply();
        sp.edit().putString(Constant.ADDRESS, mAddressText.getText().toString()).apply();
    }
}
