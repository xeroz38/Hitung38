package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by zunaidi.chandra on 02/08/2015.
 */
public class ReportActivity extends Activity implements View.OnClickListener {

    private Button mSalesBtn, mSalesItemBtn, mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mSalesBtn = (Button) findViewById(R.id.sales_btn);
        mSalesItemBtn = (Button) findViewById(R.id.sales_item_btn);
        mSalesBtn.setOnClickListener(this);
        mSalesItemBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sales_btn: {
                Intent intent = new Intent(this, ReportSalesActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.sales_item_btn: {
                break;
            }
            default:
                break;
        }
    }
}
