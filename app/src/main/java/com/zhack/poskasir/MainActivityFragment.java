package com.zhack.poskasir;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zhack.poskasir.model.Item;
import com.zhack.poskasir.model.ItemGroup;
import com.zhack.poskasir.util.ItemProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A placeholder fragment containing a main view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private Button mMasterBtn, mPosBtn, mReportBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mMasterBtn = (Button) view.findViewById(R.id.master_btn);
        mPosBtn = (Button) view.findViewById(R.id.pos_btn);
        mReportBtn = (Button) view.findViewById(R.id.report_btn);
        mMasterBtn.setOnClickListener(this);
        mPosBtn.setOnClickListener(this);
        mReportBtn.setOnClickListener(this);

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
