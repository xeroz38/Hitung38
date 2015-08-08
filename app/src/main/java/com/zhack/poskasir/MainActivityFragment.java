package com.zhack.poskasir;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zhack.poskasir.util.Constant;


/**
 * A placeholder fragment containing a main view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private TextView mNoPDText, mRestaurantText;
    private Button mMasterBtn, mPosBtn, mSpeedOrderBtn, mReportBtn;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(Constant.ZHACK_SP, Context.MODE_PRIVATE);
        if (!sharedPref.contains(Constant.NO_PD)) {
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mNoPDText = (TextView) view.findViewById(R.id.nopd_text);
        mRestaurantText = (TextView) view.findViewById(R.id.restaurant_text);
        mMasterBtn = (Button) view.findViewById(R.id.master_btn);
        mPosBtn = (Button) view.findViewById(R.id.pos_btn);
        mSpeedOrderBtn = (Button) view.findViewById(R.id.speedorder_btn);
        mReportBtn = (Button) view.findViewById(R.id.report_btn);
        mMasterBtn.setOnClickListener(this);
        mPosBtn.setOnClickListener(this);
        mSpeedOrderBtn.setOnClickListener(this);
        mReportBtn.setOnClickListener(this);

        mNoPDText.setText("No.PD       : " + sharedPref.getLong(Constant.NO_PD, 0));
        mRestaurantText.setText("Restoran : " + sharedPref.getString(Constant.RESTAURANT, ""));

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
            case R.id.speedorder_btn: {
                Intent intent = new Intent(getActivity(), SpeedOrderActivity.class);
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
