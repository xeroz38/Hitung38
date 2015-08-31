package com.zhack.poskasir;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
public class PointOfSalesDetailActivity extends Activity {

    private int totalPrice;
    private ArrayList<POSData> mPOSData;
    private TextView mSubPriceText, mTaxPriceText, mTotalPriceText, mChangePriceText;
    private EditText mPayEdit;
    private Button mDoneBtn;
    private ListView mItemGrid;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointofsales_detail);

        mItemGrid = (ListView) findViewById(R.id.pos_list);
        mSubPriceText = (TextView) findViewById(R.id.subprice_text);
        mTaxPriceText = (TextView) findViewById(R.id.taxprice_text);
        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mChangePriceText = (TextView) findViewById(R.id.changeprice_text);
        mPayEdit = (EditText) findViewById(R.id.payprice_edit);
        mDoneBtn = (Button) findViewById(R.id.done_btn);

        if (getIntent() != null) {
            mPOSData = getIntent().getParcelableArrayListExtra(Constant.ITEM_LIST);
        }
        sharedPref = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
        mItemGrid.setAdapter(new ItemAdapter());
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPayEdit.getText().toString().trim().length() > 0 && Integer.parseInt(mPayEdit.getText().toString()) >= (totalPrice + (totalPrice / 10))) {
                    String timeMillis = String.valueOf(System.currentTimeMillis());
                    // Insert SQLlite
                    insertReportSalesData(timeMillis);
                    // Push data through service
                    String invoiceId = "JK-" + String.valueOf(sharedPref.getLong(Constant.NOPD, 0)).substring(12) + "-" + timeMillis.substring(9);
                    Intent intService = new Intent(getApplicationContext(), PushInvoiceService.class);
                    intService.putExtra(Constant.IMEI, sharedPref.getString(Constant.IMEI, ""));
                    intService.putExtra(Constant.NOPD, sharedPref.getLong(Constant.NOPD, 0));
                    intService.putExtra(Constant.DATE, timeMillis);
                    intService.putExtra(Constant.TRAN_INVOICE, invoiceId);
                    intService.putExtra(Constant.TRAN_PRICE, String.valueOf(totalPrice));
                    intService.putExtra(Constant.TRAN_TAX, String.valueOf(totalPrice / 10));
                    startService(intService);
                    // Print invoice
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

        calculateTotalPrice();
        mPayEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    mChangePriceText.setText(Utils.convertRp((Integer.parseInt(s.toString()) - (totalPrice + (totalPrice / 10)))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void calculateTotalPrice() {
        totalPrice = 0;
        for (POSData pos : mPOSData) {
            totalPrice += pos.quantity * pos.price;
        }
        mSubPriceText.setText(Utils.convertRp(totalPrice));
        mTaxPriceText.setText(Utils.convertRp(totalPrice / 10));
        mTotalPriceText.setText(Utils.convertRp(totalPrice + (totalPrice / 10)));
    }


    private void insertReportSalesData(String timeMillis) {
        int totalPrice = 0;
        for (POSData pos : mPOSData) {
            totalPrice += pos.quantity * pos.price;
        }

        JSONArray jsonArr = new JSONArray();
        for (int i=0; i < mPOSData.size(); i++) {
            jsonArr.put(mPOSData.get(i).getJSONObject());
        }

        ContentValues values = new ContentValues();
        values.put(ReportSales.PRICE, totalPrice);
        values.put(ReportSales.PAY, Integer.parseInt(mPayEdit.getText().toString()));
        values.put(ReportSales.DATE, timeMillis);
        values.put(ReportSales.POS_DATA, jsonArr.toString());

        getContentResolver().insert(ZhackProvider.REPORTSALES_CONTENT_URI, values);
    }

    private class ItemAdapter extends BaseAdapter {

        public class ViewHolder {
            public ImageView image;
            public TextView title;
            public TextView quantity;
            public TextView price;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.view_pos_list, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) rowView.findViewById(R.id.item_img);
                viewHolder.title = (TextView) rowView.findViewById(R.id.title_text);
                viewHolder.quantity = (TextView) rowView.findViewById(R.id.quantity_text);
                viewHolder.price = (TextView) rowView.findViewById(R.id.price_text);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.title.setText(mPOSData.get(position).title);
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                    + "/poskasir/img/" + mPOSData.get(position).image);
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(100, 75));
            holder.image.setImageBitmap(bitmap);
            holder.quantity.setText(String.valueOf(mPOSData.get(position).quantity));
            holder.price.setText(Utils.convertRp(mPOSData.get(position).price));

            return rowView;
        }

        @Override
        public int getCount() {
            return mPOSData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
