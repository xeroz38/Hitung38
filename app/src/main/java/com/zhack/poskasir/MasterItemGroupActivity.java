package com.zhack.poskasir;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.util.ItemProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class MasterItemgroupActivity extends Activity {

    private List<ItemGroup> mItemGroupData;
    private ListView mItemGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_itemgroup);

        mItemGroupList = (ListView) findViewById(R.id.itemgroup_list);
        View footerView = getLayoutInflater().inflate(R.layout.view_itemgroup_list, null);
        TextView addItemGroup = (TextView) footerView.findViewById(R.id.title_text);
        addItemGroup.setText(R.string.add_itemgroup);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("");
            }
        });
        mItemGroupData = getItemGroupListData();
        mItemGroupList.addFooterView(footerView);
        mItemGroupList.setAdapter(new ItemGroupAdapter());
        mItemGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(mItemGroupData.get(position).title);
            }
        });
    }

    private ArrayList<ItemGroup> getItemGroupListData() {
        ArrayList<ItemGroup> list = null;
        Cursor cursor = null;
        try	{
            cursor = getContentResolver().query(ItemProvider.ITEMGROUP_CONTENT_URI, ItemGroup.QUERY_SHORT, null, null, null);
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

    private void showDialog(String content) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.view_itemgroup_dialog);
        dialog.setTitle(R.string.edit_itemgroup);

        TextView text = (TextView) dialog.findViewById(R.id.content_edit);
        text.setText(content);

        Button cancelBtn = (Button) dialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button saveBtn = (Button) dialog.findViewById(R.id.ok_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class ItemGroupAdapter extends BaseAdapter {

        public class ViewHolder {
            public TextView text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.view_itemgroup_list, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.title_text);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText(mItemGroupData.get(position).title);

            return rowView;
        }

        @Override
        public int getCount() {
            return mItemGroupData.size();
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
