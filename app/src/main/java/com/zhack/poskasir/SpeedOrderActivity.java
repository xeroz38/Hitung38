package com.zhack.poskasir;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zhack.poskasir.util.Constant;

/**
 * Created by zunaidi.chandra on 06/08/2015.
 */
public class SpeedOrderActivity extends Activity implements View.OnClickListener {

    private final int ADDITION      = 100;
    private final int SUBTRACTION   = 101;
    private final int MULTIPLY      = 102;
    private final int DIVISION      = 103;
    private final int EQUAL         = 104;

    private boolean isSecond = false;
    private EditText mInputEdit, mInput2Edit;
    private TextView mOperatorText;
    private Button mNum0Btn, mNum1Btn, mNum2Btn,
            mNum3Btn, mNum4Btn, mNum5Btn, mNum6Btn,
            mNum7Btn, mNum8Btn, mNum9Btn,
            mPlusBtn, mMinusBtn, mMultiBtn, mDivBtn, mEqualBtn, mDoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_speedorder);

        mInputEdit = (EditText) findViewById(R.id.input_edit);
        mInput2Edit = (EditText) findViewById(R.id.input2_edit);
        mOperatorText = (TextView) findViewById(R.id.operator_text);
        mNum0Btn = (Button) findViewById(R.id.num0_btn);
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
        mEqualBtn = (Button) findViewById(R.id.equal_btn);
        mDoneBtn = (Button) findViewById(R.id.done_btn);

        mNum0Btn.setOnClickListener(this);
        mNum1Btn.setOnClickListener(this);
        mNum2Btn.setOnClickListener(this);
        mNum3Btn.setOnClickListener(this);
        mNum4Btn.setOnClickListener(this);
        mNum5Btn.setOnClickListener(this);
        mNum6Btn.setOnClickListener(this);
        mNum7Btn.setOnClickListener(this);
        mNum8Btn.setOnClickListener(this);
        mNum9Btn.setOnClickListener(this);

        mPlusBtn.setOnClickListener(this);
        mMinusBtn.setOnClickListener(this);
        mMultiBtn.setOnClickListener(this);
        mDivBtn.setOnClickListener(this);
        mEqualBtn.setOnClickListener(this);
        mDoneBtn.setOnClickListener(this);

        mInputEdit.setKeyListener(DigitsKeyListener.getInstance(true, true));
        mInput2Edit.setKeyListener(DigitsKeyListener.getInstance(true, true));
    }

    private void calculateInput(int operator) {
        mOperatorText.setVisibility(View.VISIBLE);
        mInput2Edit.setVisibility(View.VISIBLE);
        if (mInputEdit.getText().toString().trim().length() < 1) {
            mInputEdit.setText("0");
        }

        switch (operator) {
            case ADDITION : {
                mOperatorText.setText("+");
                isSecond = true;
                break;
            }
            case SUBTRACTION : {
                mOperatorText.setText("-");
                isSecond = true;
                break;
            }
            case MULTIPLY : {
                mOperatorText.setText("*");
                isSecond = true;
                break;
            }
            case DIVISION : {
                mOperatorText.setText("/");
                isSecond = true;
                break;
            }
            case EQUAL : {
                String calc = null;
                if (mOperatorText.getText().toString().equals("+")) {
                    calc = String.valueOf(Long.parseLong(mInputEdit.getText().toString()) +
                            Long.parseLong(mInput2Edit.getText().toString()));
                } else if (mOperatorText.getText().toString().equals("-")) {
                    calc = String.valueOf(Long.parseLong(mInputEdit.getText().toString()) -
                            Long.parseLong(mInput2Edit.getText().toString()));
                } else if (mOperatorText.getText().toString().equals("*")) {
                    calc = String.valueOf(Long.parseLong(mInputEdit.getText().toString()) *
                            Long.parseLong(mInput2Edit.getText().toString()));
                } else if (mOperatorText.getText().toString().equals("/")) {
                    calc = String.valueOf(Long.parseLong(mInputEdit.getText().toString()) /
                            Long.parseLong(mInput2Edit.getText().toString()));
                }
                mInputEdit.setText(String.valueOf(calc));
                isSecond = false;
                mOperatorText.setVisibility(View.GONE);
                mInput2Edit.setVisibility(View.GONE);
                mInput2Edit.setText("");
                break;
            }
            default: break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.num0_btn : {
                if (isSecond) {
                    mInput2Edit.append("0");
                } else {
                    mInputEdit.append("0");
                }
                break;
            }
            case R.id.num1_btn : {
                if (isSecond) {
                    mInput2Edit.append("1");
                } else {
                    mInputEdit.append("1");
                }
                break;
            }
            case R.id.num2_btn : {
                if (isSecond) {
                    mInput2Edit.append("2");
                } else {
                    mInputEdit.append("2");
                }
                break;
            }
            case R.id.num3_btn : {
                if (isSecond) {
                    mInput2Edit.append("3");
                } else {
                    mInputEdit.append("3");
                }
                break;
            }
            case R.id.num4_btn : {
                if (isSecond) {
                    mInput2Edit.append("4");
                } else {
                    mInputEdit.append("4");
                }
                break;
            }
            case R.id.num5_btn : {
                if (isSecond) {
                    mInput2Edit.append("5");
                } else {
                    mInputEdit.append("5");
                }
                break;
            }
            case R.id.num6_btn : {
                if (isSecond) {
                    mInput2Edit.append("6");
                } else {
                    mInputEdit.append("6");
                }
                break;
            }
            case R.id.num7_btn : {
                if (isSecond) {
                    mInput2Edit.append("7");
                } else {
                    mInputEdit.append("7");
                }
                break;
            }
            case R.id.num8_btn : {
                if (isSecond) {
                    mInput2Edit.append("8");
                } else {
                    mInputEdit.append("8");
                }
                break;
            }
            case R.id.num9_btn : {
                if (isSecond) {
                    mInput2Edit.append("9");
                } else {
                    mInputEdit.append("9");
                }
                break;
            }
            case R.id.plus_btn : {
                calculateInput(ADDITION);
                break;
            }
            case R.id.minus_btn : {
                calculateInput(SUBTRACTION);
                break;
            }
            case R.id.multiply_btn : {
                calculateInput(MULTIPLY);
                break;
            }
            case R.id.division_btn : {
                calculateInput(DIVISION);
                break;
            }
            case R.id.equal_btn : {
                calculateInput(EQUAL);
                break;
            }
            case R.id.done_btn : {
                if (mInputEdit.getText().toString().trim().length() < 1) {
                    mInputEdit.setText("0");
                }
                Intent intent = new Intent(SpeedOrderActivity.this, SpeedOrderDetailActivity.class);
                intent.putExtra(Constant.SPEEDORDER_PRICE, Long.parseLong(mInputEdit.getText().toString()));
                startActivity(intent);
                break;
            }
            default: break;
        }
    }
}