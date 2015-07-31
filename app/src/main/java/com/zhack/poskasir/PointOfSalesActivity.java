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
import com.zhack.poskasir.util.PrintJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class PointOfSalesActivity extends Activity {

    private List<Item> mItemData;
    private ArrayList<POSData> mPOSData;
    private List<ItemGroup> mItemGroupData;
    private POSAdapter mPOSAdapter;
    private TextView mTotalPriceText;
    private Button mDoneBtn;
    private GridView mItemGrid;
    private ListView mPOSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointofsales);

        mItemGrid = (GridView) findViewById(R.id.item_grid);
        mPOSList = (ListView) findViewById(R.id.pos_list);
        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mDoneBtn = (Button) findViewById(R.id.done_btn);
        mItemData = getItemListData();
        mPOSData = new ArrayList<POSData>();
        mPOSAdapter = new POSAdapter();
        mItemGrid.setAdapter(new ItemAdapter());
        mPOSList.setAdapter(mPOSAdapter);
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
                mPOSAdapter.notifyDataSetChanged();
            }
        });
        mPOSList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPOSData.get(position).quantity > 1) {
                    mPOSData.get(position).quantity--;
                } else {
                    mPOSData.remove(position);
                }
                calculateTotalPrice();
                mPOSAdapter.notifyDataSetChanged();
            }
        });
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PointOfSalesActivity.this, PointOfSalesDetailActivity.class);
//                intent.putParcelableArrayListExtra(Constant.ITEM_LIST, mPOSData);
//                startActivity(intent);
                new PrintJob(getApplicationContext(), mPOSData);
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

    private ArrayList<Item> getItemListData() {
        ArrayList<Item> list = null;
        Cursor cursor = null;
        try	{
            cursor = getContentResolver().query(ItemProvider.ITEM_CONTENT_URI, Item.QUERY_SHORT, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                int itemTitle = cursor.getColumnIndexOrThrow(Item.ITEM_TITLE);
                int itemImage = cursor.getColumnIndexOrThrow(Item.ITEM_IMAGE);
                int itemCategory = cursor.getColumnIndexOrThrow(Item.ITEM_CATEGORY);
                int itemPrice = cursor.getColumnIndexOrThrow(Item.ITEM_PRICE);

                list = new ArrayList<Item>(cursor.getCount());
                while (cursor.moveToNext()) {
                    Item item = new Item();
                    item.title = cursor.getString(itemTitle);
                    item.image = cursor.getString(itemImage);
                    item.category = cursor.getString(itemCategory);
                    item.price = cursor.getString(itemPrice);

                    list.add(item);
                }
                return list;
            } else {
                return list = new ArrayList<Item>();
            }
        } finally { }
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
            holder.title.setText(mPOSData.get(position).title);
            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                    + "/poskasir/img/" + mPOSData.get(position).image);
            holder.image.setImageBitmap(bitmap);
            holder.quantity.setText(String.valueOf(mPOSData.get(position).quantity));
            holder.price.setText(String.valueOf(mPOSData.get(position).price));

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
