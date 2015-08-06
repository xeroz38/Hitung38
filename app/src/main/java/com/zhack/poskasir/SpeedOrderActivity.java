package com.zhack.poskasir;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by zunaidi.chandra on 06/08/2015.
 */
public class SpeedOrderActivity extends Activity {

    private TextView mTotalPriceText;
    private Button mNum1Btn, mNum2Btn, mNum3Btn, mNum4Btn, mNum5Btn, mNum6Btn, mNum7Btn, mNum8Btn, mNum9Btn,
            mPlusBtn, mMinusBtn, mMultiBtn, mDivBtn, mDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedorder);

        mTotalPriceText = (TextView) findViewById(R.id.totalprice_text);
        mNum1Btn = (Button) findViewById(R.id.num1_btn);
        mNum2Btn = (Button) findViewById(R.id.num2_btn);
        mNum3Btn = (Button) findViewById(R.id.num3_btn);
        mNum4Btn = (Button) findViewById(R.id.num4_btn);
        mNum5Btn = (Button) findViewById(R.id.num5_btn);
        mNum6Btn = (Button) findViewById(R.id.num6_btn);
        mNum7Btn = (Button) findViewById(R.id.num7_btn);
        mNum8Btn = (Button) findViewById(R.id.num8_btn);
        mNum9Btn = (Button) findViewById(R.id.num9_btn);
        mPlusBtn = (Button) findViewById(R.id.plus_btn);
        mMinusBtn = (Button) findViewById(R.id.minus_btn);
        mMultiBtn = (Button) findViewById(R.id.multiply_btn);
        mDivBtn = (Button) findViewById(R.id.division_btn);
        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SpeedOrderActivity.this, PointOfSalesDetailActivity.class);
//                intent.putParcelableArrayListExtra(Constant.ITEM_LIST, mPOSData);
//                startActivity(intent);
            }
        });
    }
}