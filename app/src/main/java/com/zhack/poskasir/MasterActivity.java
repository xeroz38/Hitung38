package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class MasterActivity extends Activity implements View.OnClickListener {

    private Button mItemBtn, mItemGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        mItemBtn = (Button) findViewById(R.id.item_btn);
        mItemBtn.setOnClickListener(this);
        mItemGroupBtn = (Button) findViewById(R.id.item_group_btn);
        mItemGroupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_btn: {
                Intent intent = new Intent(this, MasterItemActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.item_group_btn: {
                Intent intent = new Intent(this, MasterItemGroupActivity.class);
                startActivity(intent);
                break;
            }
            default: break;
        }
    }
}
