package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.Utils;
import com.zhack.poskasir.util.ZhackProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class PointOfSalesActivity extends Activity {

    public static final int RESULT_POS_ADD_ITEM = 101;

    private boolean isItemGroup = false;
    private boolean isTablet;
    private List<Item> mItemData;
    private ArrayList<POSData> mPOSData;
    private List<ItemGroup> mItemGroupData;
    private ItemAdapter mItemAdapter;
    private POSAdapter mPOSAdapter;
    private LinearLayout mItemGroupLayout;
    private TextView mCurrentTitleText, mTotalPriceText;
    private Button mItemBtn, mItemGroupBtn, mAddItemBtn, mDoneBtn;
    private GridView mItemGrid;
    private ListView mPOSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_pointofsales);

        mItemGroupLayout = (LinearLayout) findViewById(R.id.itemgroup_layout);
        mItemGrid = (GridView) findViewById(R.id.item_grid);
        mPOSList = (ListView) findViewById(R.id.pos_list);
        mCurrentTitleText = (TextView) findViewById(R.id.title_current_text);
        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mItemBtn = (Button) findViewById(R.id.item_btn);
        mItemGroupBtn = (Button) findViewById(R.id.itemgroup_btn);
        mAddItemBtn = (Button) findViewById(R.id.add_item_btn);
        mDoneBtn = (Button) findViewById(R.id.done_btn);
        mItemData = getItemListData();
        mItemGroupData = getItemGroupListData();
        mPOSData = new ArrayList<POSData>();
        mPOSAdapter = new POSAdapter();
        mItemAdapter = new ItemAdapter();
        mItemGrid.setAdapter(mItemAdapter);
        mPOSList.setAdapter(mPOSAdapter);
        mItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isItemGroup) {
                    isItemGroup = false;
                    mCurrentTitleText.setText(mItemGroupData.get(position).title);
                    mItemData = sortItemByGroupData(mItemGroupData.get(position).title);
                    mItemAdapter.notifyDataSetChanged();
                } else {
                    if (isItemExist(mItemData.get(position).title)) {
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
        mItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isItemGroup = false;
                mCurrentTitleText.setText(R.string.item);
                mItemGroupBtn.setTypeface(null, Typeface.NORMAL);
                mItemBtn.setTypeface(null, Typeface.BOLD);
                mItemData = getItemListData();
                mItemAdapter.notifyDataSetChanged();
            }
        });
        mItemGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isItemGroup = true;
                mCurrentTitleText.setText(R.string.item_group);
                mItemBtn.setTypeface(null, Typeface.NORMAL);
                mItemGroupBtn.setTypeface(null, Typeface.BOLD);
                mItemAdapter.notifyDataSetChanged();
            }
        });
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PointOfSalesActivity.this, PointOfSalesDetailActivity.class);
                intent.putParcelableArrayListExtra(Constant.ITEM_LIST, mPOSData);
                startActivity(intent);
            }
        });
        if (!isTablet) {
            mAddItemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PointOfSalesActivity.this, PointOfSalesItemActivity.class);
                    startActivityForResult(intent, RESULT_POS_ADD_ITEM);
                }
            });
            mItemGroupLayout.setVisibility(View.GONE);
            mAddItemBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_POS_ADD_ITEM) {
            if (resultCode == RESULT_OK) {
                POSData pos = data.getExtras().getParcelable(Constant.ITEM_LIST);
                if (isItemExist(pos.title)) {
                    for (int i = 0; i < mPOSData.size(); i++) {
                        if (mPOSData.get(i).title.equals(pos.title)) {
                            mPOSData.get(i).quantity++;
                        }
                    }
                } else {
                    mPOSData.add(pos);
                }
                calculateTotalPrice();
                mPOSAdapter.notifyDataSetChanged();
            }
        }
    }

    private void calculateTotalPrice() {
        int totalPrice = 0;
        for (POSData pos : mPOSData) {
            totalPrice += pos.quantity * pos.price;
        }
        mTotalPriceText.setText(Utils.convertRp(totalPrice));
    }

    private boolean isItemExist(String title) {
        for (POSData pos : mPOSData) {
            if (pos.title.equals(title)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<Item> sortItemByGroupData(String group) {
        ArrayList<Item> sortedItem = new ArrayList<Item>();
        for (Item item : mItemData) {
            if (item.category.equals(group)) {
                sortedItem.add(item);
            }
        }
        return sortedItem;
    }

    private ArrayList<Item> getItemListData() {
        ArrayList<Item> list = null;
        Cursor cursor = null;
        try	{
            cursor = getContentResolver().query(ZhackProvider.ITEM_CONTENT_URI, Item.QUERY_SHORT, null, null, null);
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

    private ArrayList<ItemGroup> getItemGroupListData() {
        ArrayList<ItemGroup> list = null;
        Cursor cursor = null;
        try	{
            cursor = getContentResolver().query(ZhackProvider.ITEMGROUP_CONTENT_URI, ItemGroup.QUERY_SHORT, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                int itemTitle = cursor.getColumnIndexOrThrow(ItemGroup.ITEMGROUP_TITLE);

                list = new ArrayList<ItemGroup>(cursor.getCount());
                while (cursor.moveToNext()) {
                    ItemGroup itemGroup = new ItemGroup();
                    itemGroup.title = cursor.getString(itemTitle);

                    list.add(itemGroup);
                }
                return list;
            } else {
                return list = new ArrayList<ItemGroup>();
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
            if (isItemGroup) {
                holder.text.setText(mItemGroupData.get(position).title);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                holder.image.setBackgroundColor(color);
                holder.image.setImageBitmap(null);
            } else {
                holder.text.setText(mItemData.get(position).title);
                Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir().getAbsolutePath() + File.separator + mItemData.get(position).image);
                holder.image.setImageBitmap(bitmap);
            }

            return rowView;
        }

        @Override
        public int getCount() {
            if (isItemGroup) {
                return mItemGroupData.size();
            }
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
            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir().getAbsolutePath() + File.separator + mPOSData.get(position).image);
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
