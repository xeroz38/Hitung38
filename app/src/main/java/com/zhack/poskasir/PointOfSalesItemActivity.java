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
import android.widget.TextView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.model.POSData;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.ZhackProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class PointOfSalesItemActivity extends Activity {

    private boolean isItemGroup = false;
    private List<Item> mItemData;
    private List<ItemGroup> mItemGroupData;
    private ItemAdapter mItemAdapter;
    private TextView mCurrentTitleText;
    private Button mItemBtn, mItemGroupBtn;
    private GridView mItemGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pointofsales_item);

        mItemGrid = (GridView) findViewById(R.id.item_grid);
        mCurrentTitleText = (TextView) findViewById(R.id.title_current_text);
        mItemBtn = (Button) findViewById(R.id.item_btn);
        mItemGroupBtn = (Button) findViewById(R.id.itemgroup_btn);
        mItemData = getItemListData();
        mItemGroupData = getItemGroupListData();
        mItemAdapter = new ItemAdapter();
        mItemGrid.setAdapter(mItemAdapter);
        mItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isItemGroup) {
                    isItemGroup = false;
                    mCurrentTitleText.setText(mItemGroupData.get(position).title);
                    mItemData = sortItemByGroupData(mItemGroupData.get(position).title);
                    mItemAdapter.notifyDataSetChanged();
                } else {
                    POSData pos = new POSData();
                    pos.image = mItemData.get(position).image;
                    pos.title = mItemData.get(position).title;
                    pos.quantity++;
                    pos.price = Integer.parseInt(mItemData.get(position).price);

                    Intent intent = new Intent();
                    intent.putExtra(Constant.ITEM_LIST, pos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
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
}
