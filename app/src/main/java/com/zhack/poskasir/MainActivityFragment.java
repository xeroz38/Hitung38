package com.zhack.poskasir;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.util.ItemProvider;


/**
 * A placeholder fragment containing a main view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener{

    private Button mMasterBtn, mPosBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mMasterBtn = (Button) view.findViewById(R.id.master_btn);
        mPosBtn = (Button) view.findViewById(R.id.pos_btn);
        mMasterBtn.setOnClickListener(this);
        mPosBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.master_btn: {
                Intent intent = new Intent(getActivity(), MasterActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.pos_btn: {
                for (int i = 0; i < 30; i++) {
                    ContentValues values = new ContentValues();
                    values.put(Item.ITEM_TITLE, "Ketoprak");
                    values.put(Item.ITEM_IMAGE, "IMG_1438250462193");
                    values.put(Item.ITEM_CATEGORY, "Chinese");
                    values.put(Item.ITEM_PRICE, "90000");

                    getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, values);
                }
                break;
            }
            default: break;
        }
    }
}
