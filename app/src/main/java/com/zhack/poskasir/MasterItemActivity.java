package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.TextView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.util.Constant;
import com.zhack.poskasir.util.ZhackProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class MasterItemActivity extends Activity {

    private List<Item> mItemData;
    private ItemAdapter mAdapter;
    private Button mAddItemBtn;
    private GridView mItemGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_master_item);

        mAddItemBtn = (Button) findViewById(R.id.add_item_btn);
        mItemGrid = (GridView) findViewById(R.id.item_grid);
        mItemData = getItemListData();
        mAdapter = new ItemAdapter();
        mItemGrid.setAdapter(mAdapter);
        mItemGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MasterItemActivity.this, MasterItemDetailActivity.class);
                intent.putExtra(Constant.ITEM_EDIT, true);
                intent.putExtra(Constant.ITEM_NAME, mItemData.get(position).title);
                intent.putExtra(Constant.ITEM_IMAGE, mItemData.get(position).image);
                intent.putExtra(Constant.ITEM_CATEGORY, mItemData.get(position).category);
                intent.putExtra(Constant.ITEM_PRICE, mItemData.get(position).price);
                startActivity(intent);
            }
        });
        mAddItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasterItemActivity.this, MasterItemDetailActivity.class);
                intent.putExtra(Constant.ITEM_EDIT, false);
                intent.putExtra(Constant.ITEM_NAME, "");
                intent.putExtra(Constant.ITEM_IMAGE, "");
                intent.putExtra(Constant.ITEM_CATEGORY, "");
                intent.putExtra(Constant.ITEM_PRICE, "");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItemData = getItemListData();
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<Item> getItemListData() {
        ArrayList<Item> list = new ArrayList<Item>();
        Cursor cursor = getContentResolver().query(ZhackProvider.ITEM_CONTENT_URI, Item.QUERY_SHORT, null, null, null);
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
            return list;
        }
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
