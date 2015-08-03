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
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.util.ItemProvider;


/**
 * A placeholder fragment containing a main view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private Button mMasterBtn, mPosBtn, mReportBtn, mAddBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mMasterBtn = (Button) view.findViewById(R.id.master_btn);
        mPosBtn = (Button) view.findViewById(R.id.pos_btn);
        mReportBtn = (Button) view.findViewById(R.id.report_btn);
        mAddBtn = (Button) view.findViewById(R.id.add_btn);
        mMasterBtn.setOnClickListener(this);
        mPosBtn.setOnClickListener(this);
        mReportBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);

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
            case R.id.add_btn: {
                ContentValues valuesI = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Ayam Goreng");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Indonesian");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI1 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Fu Yung Hai");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Chinese");
                valuesI.put(Item.ITEM_PRICE, "20000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI2 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Bim Bim Bap");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Korean");
                valuesI.put(Item.ITEM_PRICE, "30000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI3 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Sushi");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Japanese");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI4 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Roti Prata");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Indian");
                valuesI.put(Item.ITEM_PRICE, "20000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI5 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Bistik Sapi");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Western");
                valuesI.put(Item.ITEM_PRICE, "30000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI6 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Es Kopyor");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Minuman");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues valuesI7 = new ContentValues();
                valuesI.put(Item.ITEM_TITLE, "Jus Melon");
                valuesI.put(Item.ITEM_IMAGE, "IMG_1");
                valuesI.put(Item.ITEM_CATEGORY, "Minuman");
                valuesI.put(Item.ITEM_PRICE, "10000");
                getActivity().getContentResolver().insert(ItemProvider.ITEM_CONTENT_URI, valuesI);

                ContentValues values = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Indonesian");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values1 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Chinese");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values2 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Japanese");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values3 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Korean");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values4 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Indian");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values5 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Western");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                ContentValues values6 = new ContentValues();
                values.put(ItemGroup.ITEMGROUP_TITLE, "Minuman");
                getActivity().getContentResolver().insert(ItemProvider.ITEMGROUP_CONTENT_URI, values);

                break;
            }
            case R.id.pos_btn: {
                Intent intent = new Intent(getActivity(), PointOfSalesActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.report_btn: {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}
