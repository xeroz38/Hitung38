package com.zhack.poskasir;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhack.poskasir.model.ItemGroup;

import java.util.List;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class MasterItemGroupActivity extends Activity {

    private List<ItemGroup> itemGroupData;
    private ListView mItemGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_itemgroup);

        mItemGroupList = (ListView) findViewById(R.id.itemgroup_list);
        View footerView = getLayoutInflater().inflate(R.layout.itemgroup_list_view, null);
        TextView addItemGroup = (TextView) footerView.findViewById(R.id.title_text);
        addItemGroup.setText(R.string.add_itemgroup);
        mItemGroupList.addFooterView(footerView);
        mItemGroupList.setAdapter(new ItemGroupAdapter());
        mItemGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // pop
            }
        });
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
                rowView = inflater.inflate(R.layout.itemgroup_list_view, null);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.title_text);
                rowView.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.text.setText("Food");

            return rowView;
        }

        @Override
        public int getCount() {
            return 10;
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
