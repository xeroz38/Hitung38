package com.zhack.poskasir;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhack.poskasir.model.Invoice;
import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.model.ReportSales;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.ZhackProvider;
import com.zhack.poskasir.util.PrintJob;
import com.zhack.poskasir.util.PushInvoiceService;
import com.zhack.poskasir.util.Utils;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class SpeedOrderDetailActivity extends Activity {

    private long totalPrice;
    private ArrayList<POSData> mPOSData;
    private TextView mSubPriceText, mTaxPriceText, mTotalPriceText, mChangePriceText;
    private EditText mPayEdit;
    private Button mDoneBtn;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedorder_detail);

        mSubPriceText = (TextView) findViewById(R.id.subprice_text);
        mTaxPriceText = (TextView) findViewById(R.id.taxprice_text);
        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mChangePriceText = (TextView) findViewById(R.id.changeprice_text);
        mPayEdit = (EditText) findViewById(R.id.payprice_edit);
        mDoneBtn = (Button) findViewById(R.id.done_btn);

        if (getIntent() != null) {
            totalPrice = getIntent().getExtras().getLong(Constant.SPEEDORDER_PRICE);
        }
        sharedPref = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPayEdit.getText().toString().trim().length() > 0 && Integer.parseInt(mPayEdit.getText().toString()) >= (totalPrice + (totalPrice / 10))) {
                    String timeMillis = String.valueOf(System.currentTimeMillis());
                    int counter = sharedPref.getInt(Constant.COUNT, 0);
                    ++counter;
                    sharedPref.edit().putInt(Constant.COUNT, counter).apply();
                    String invoiceId = "S"
                            + String.valueOf(sharedPref.getLong(Constant.NOPD, 0)).substring(12) + "-"
                            + Utils.convertDate(timeMillis, "ddMMyy") + "-"
                            + ("000" + counter).substring(String.valueOf(counter).length());
                    // Insert to sqlite, S means Speed Order
                    insertReportSalesData(timeMillis);
                    // Push data through service
                    Intent intService = new Intent(getApplicationContext(), PushInvoiceService.class);
                    intService.putExtra(Constant.IMEI, sharedPref.getString(Constant.IMEI, ""));
                    intService.putExtra(Constant.NOPD, sharedPref.getLong(Constant.NOPD, 0));
                    intService.putExtra(Constant.DATE, timeMillis);
                    intService.putExtra(Constant.TRAN_INVOICE, invoiceId);
                    intService.putExtra(Constant.TRAN_PRICE, String.valueOf(totalPrice));
                    intService.putExtra(Constant.TRAN_TAX, String.valueOf(totalPrice / 10));
                    startService(intService);
                    // Print info
                    Invoice invoice = new Invoice();
                    invoice.id = invoiceId;
                    invoice.restaurant = sharedPref.getString(Constant.RESTAURANT, "");
                    invoice.address = sharedPref.getString(Constant.ADDRESS, "");
                    invoice.date = Utils.convertDate(timeMillis, "dd/MM/yyyy hh:mm");
                    invoice.pay = Integer.parseInt(mPayEdit.getText().toString());
                    invoice.posData = mPOSData;
                    new PrintJob(getApplicationContext(), invoice);
                    // Clear all intent to MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Pembayaran tidak cukup", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPayEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mChangePriceText.setText(Utils.convertRp((int) (Integer.parseInt(s.toString()) - (totalPrice + (totalPrice / 10)))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSubPriceText.setText(Utils.convertRp((int) totalPrice));
        mTaxPriceText.setText(Utils.convertRp((int) (totalPrice / 10)));
        mTotalPriceText.setText(Utils.convertRp((int) (totalPrice + (totalPrice / 10))));
    }

    private void insertReportSalesData(String timeMillis) {
        // Speed Order info
        mPOSData = new ArrayList<POSData>();
        POSData pos = new POSData();
        pos.image = "";
        pos.title = getString(R.string.speed_order);
        pos.price = (int) totalPrice;
        pos.quantity = 1;
        mPOSData.add(pos);

        // Store to JSONArray
        JSONArray jsonArr = new JSONArray();
        for (int i=0; i < mPOSData.size(); i++) {
            jsonArr.put(mPOSData.get(i).getJSONObject());
        }

        // Content SQLite
        ContentValues values = new ContentValues();
        values.put(ReportSales.PRICE, totalPrice);
        values.put(ReportSales.PAY, Integer.parseInt(mPayEdit.getText().toString()));
        values.put(ReportSales.DATE, timeMillis);
        values.put(ReportSales.POS_DATA, jsonArr.toString());
        getContentResolver().insert(ZhackProvider.REPORTSALES_CONTENT_URI, values);
    }
}
