package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.ItemProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class PointOfSalesDetailActivity extends Activity {

    private List<Item> mItemData;
    private List<POSData> mPOSData;
    private List<ItemGroup> mItemGroupData;
    private TextView mTotalPriceText;
    private Button mDoneBtn;
    private GridView mItemGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointofsales_detail);

        mItemGrid = (GridView) findViewById(R.id.item_grid);
        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mDoneBtn = (Button) findViewById(R.id.done_btn);

        if (getIntent() != null) {
            mPOSData = getIntent().getParcelableArrayListExtra(Constant.ITEM_LIST);
        }

        mPOSData = new ArrayList<POSData>();
        mItemData = new ArrayList<Item>();
        mItemGrid.setAdapter(new ItemAdapter());
        mItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkDataExist(mItemData.get(position))) {
                    for (int i = 0; i < mPOSData.size(); i++) {
                        if (mPOSData.get(i).title.equals(mItemData.get(position).title)) {
                            mPOSData.get(i).quantity++;
                        }
                    }
                } else {
                    POSData pos = new POSData();
                    pos.image = mItemData.get(position).image;
                    pos.title = mItemData.get(position).title;
                    pos.quantity++;
                    pos.price = Integer.parseInt(mItemData.get(position).price);
                    mPOSData.add(pos);
                }
                calculateTotalPrice();
            }
        });
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void calculateTotalPrice() {
        int totalPrice = 0;
        for (POSData pos : mPOSData) {
            totalPrice += pos.quantity * pos.price;
        }
        mTotalPriceText.setText("Rp " + totalPrice);
    }

    private boolean checkDataExist(Item item) {
        for (POSData pos : mPOSData) {
            if (pos.title.equals(item.title)) {
                return true;
            }
        }
        return false;
    }

    private class ItemAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView text;
            public ImageView image;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.view_item_grid, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.title_text);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.item_img);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(mItemData.get(position).title);
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                    + "/poskasir/img/" + mItemData.get(position).image);
            holder.image.setImageBitmap(bitmap);

            return rowView;
        }

        @Override
        public int getCount() {
            return mItemData.size();
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
