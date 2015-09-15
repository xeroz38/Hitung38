package com.zhack.poskasir;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.Invoice;
import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.model.ReportSales;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.PrintJob;
import com.zhack.poskasir.util.Utils;
import com.zhack.poskasir.util.ZhackProvider;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class ReportSalesDetailActivity extends Activity {

    private String reportId, invoiceId, date;
    private int pay;
    private ArrayList<POSData> mPOSData;
    private POSAdapter mPOSAdapter;
    private TextView mReportPriceText, mPrintText, mVoidText;
    private ListView mReportDetailList;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_reportsales_detail);

        mReportPriceText = (TextView) findViewById(R.id.subtotal_text);
        mPrintText = (TextView) findViewById(R.id.print_text);
        mVoidText = (TextView) findViewById(R.id.void_text);
        mReportDetailList = (ListView) findViewById(R.id.report_detail_list);

        if (getIntent() != null) {
            reportId = getIntent().getExtras().getString(ReportSales.ID);
            invoiceId = getIntent().getExtras().getString(ReportSales.INVOICE);
            pay = getIntent().getExtras().getInt(ReportSales.PAY);
            date = getIntent().getExtras().getString(ReportSales.DATE);
            mPOSData = getIntent().getParcelableArrayListExtra(ReportSales.POS_DATA);
        }

        sharedPref = getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
        mPOSAdapter = new POSAdapter();
        mReportDetailList.setAdapter(mPOSAdapter);
        mPrintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invoice invoice = new Invoice();
                invoice.id = invoiceId;
                invoice.restaurant = sharedPref.getString(Constant.RESTAURANT, "");
                invoice.address = sharedPref.getString(Constant.ADDRESS, "");
                invoice.date = date;
                invoice.pay = pay;
                invoice.posData = mPOSData;
                new PrintJob(getApplicationContext(), invoice);
            }
        });
        mVoidText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(ReportSales.STATUS, "Batal");
                getContentResolver().update(ZhackProvider.REPORTSALES_CONTENT_URI,
                        values, ReportSales.ID + "=?", new String[]{reportId});
                finish();
            }
        });
        calculateReportPrice();
    }

    private void calculateReportPrice() {
        int reportPrice = 0;
        for (POSData pos : mPOSData) {
            reportPrice += pos.price;
        }
        mReportPriceText.setText(Utils.convertRp(reportPrice));
    }

    private class POSAdapter extends BaseAdapter {

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
            if (!mPOSData.get(position).image.equals("")) {
                Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir().getAbsolutePath() + File.separator + mPOSData.get(position).image);
                holder.image.setLayoutParams(new LinearLayout.LayoutParams(100, 75));
                holder.image.setImageBitmap(bitmap);
            } else {
                holder.image.setLayoutParams(new LinearLayout.LayoutParams(0, 75));
            }
            holder.title.setText(mPOSData.get(position).title);
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
