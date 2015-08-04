package com.zhack.poskasir;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.model.ReportSales;
import com.zhack.poskasir.util.ItemProvider;
import com.zhack.poskasir.util.PrintJob;
import com.zhack.poskasir.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class ReportSalesActivity extends Activity {

    private int postReport = 0;
    private ArrayList<ReportSales> mReportData;
    private POSAdapter mPOSAdapter;
    private TextView mTotalPriceText, mReportPriceText, mPrintText;
    private ListView mReportList, mReportDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportsales);

        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mReportPriceText = (TextView) findViewById(R.id.subtotal_text);
        mPrintText = (TextView) findViewById(R.id.print_text);
        mReportList = (ListView) findViewById(R.id.report_list);
        mReportDetailList = (ListView) findViewById(R.id.report_detail_list);
        mReportData = getReportSalesListData();
        mReportList.setAdapter(new ReportAdapter());
        mPOSAdapter = new POSAdapter();
        mReportDetailList.setAdapter(mPOSAdapter);
        mReportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                calculateReportPrice(position);
                postReport = position;
                mPOSAdapter.notifyDataSetChanged();
            }
        });
        mPrintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReportData.size() > 0) {
                    new PrintJob(getApplicationContext(), mReportData.get(postReport).posData, mReportData.get(postReport).pay);
                }
            }
        });
        calculateTotalPrice();
        if (mReportData.size() > 0) {
            calculateReportPrice(0);
        }
    }

    private void calculateTotalPrice() {
        int totalPrice = 0;
        for (ReportSales rep : mReportData) {
            totalPrice += rep.price;
        }
        mTotalPriceText.setText(Utils.convertRp(totalPrice));
    }

    private void calculateReportPrice(int position) {
        int reportPrice = 0;
        for (POSData pos : mReportData.get(position).posData) {
            reportPrice += pos.price;
        }
        mReportPriceText.setText(Utils.convertRp(reportPrice));
    }

    private ArrayList<ReportSales> getReportSalesListData() {
        ArrayList<ReportSales> list = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ItemProvider.REPORTSALES_CONTENT_URI, ReportSales.QUERY_SHORT, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                int reportId = cursor.getColumnIndexOrThrow(ReportSales.ID);
                int reportPrice = cursor.getColumnIndexOrThrow(ReportSales.PRICE);
                int reportPay = cursor.getColumnIndexOrThrow(ReportSales.PAY);
                int reportDate = cursor.getColumnIndexOrThrow(ReportSales.DATE);
                int reportPOSData = cursor.getColumnIndexOrThrow(ReportSales.POS_DATA);

                list = new ArrayList<ReportSales>(cursor.getCount());
                while (cursor.moveToNext()) {
                    ReportSales report = new ReportSales();
                    report.id = cursor.getString(reportId);
                    report.price = cursor.getInt(reportPrice);
                    report.pay = cursor.getInt(reportPay);
                    report.date = cursor.getString(reportDate);

                    String json = cursor.getString(reportPOSData);
                    ArrayList<POSData> posData = new ArrayList<POSData>();
                    JSONArray jsonArr = new JSONArray(json);
                    for (int i=0; i < jsonArr.length(); i++) {
                        POSData pos = new POSData();
                        pos.image = jsonArr.getJSONObject(i).getString(POSData.POS_IMAGE);
                        pos.title = jsonArr.getJSONObject(i).getString(POSData.POS_TITLE);
                        pos.quantity = jsonArr.getJSONObject(i).getInt(POSData.POS_QUANTITY);
                        pos.price = jsonArr.getJSONObject(i).getInt(POSData.POS_PRICE);

                        posData.add(pos);
                    }
                    report.posData = posData;

                    list.add(report);
                }
                return list;
            } else {
                return list = new ArrayList<ReportSales>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private class ReportAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView id;
            public TextView price;
            public TextView date;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.view_report_list, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.id = (TextView) rowView.findViewById(R.id.id_text);
                viewHolder.price = (TextView) rowView.findViewById(R.id.price_text);
                viewHolder.date = (TextView) rowView.findViewById(R.id.date_text);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.id.setText(mReportData.get(position).id);
            holder.price.setText(Utils.convertRp(mReportData.get(position).price));
            holder.date.setText(convertDate(mReportData.get(position).date,"dd/MM/yyyy hh:mm:ss"));

            return rowView;
        }

        public String convertDate(String dateInMilliseconds,String dateFormat) {
            return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
        }

        @Override
        public int getCount() {
            return mReportData.size();
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
            holder.title.setText(mReportData.get(postReport).posData.get(position).title);
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                    + "/poskasir/img/" + mReportData.get(postReport).posData.get(position).image);
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(100, 75));
            holder.image.setImageBitmap(bitmap);
            holder.quantity.setText(String.valueOf(mReportData.get(postReport).posData.get(position).quantity));
            holder.price.setText(Utils.convertRp(mReportData.get(postReport).posData.get(position).price));

            return rowView;
        }

        @Override
        public int getCount() {
            if (mReportData.size() > 0) {
                return mReportData.get(postReport).posData.size();
            }
            return 0;
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
