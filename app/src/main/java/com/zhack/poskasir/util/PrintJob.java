package com.zhack.poskasir.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

import com.hdx.lib.printer.SerialPrinter;
import com.hdx.lib.serial.SerialParam;
import com.hdx.lib.serial.SerialPortOperaion;
import com.hdx.lib.serial.SerialPortOperaion.SerialReadData;
import com.zhack.poskasir.R;
import com.zhack.poskasir.model.POSData;

import java.util.ArrayList;

import hdx.HdxUtil;

/**
 * Created by xeRoz on 8/1/2015.
 */
public class PrintJob {

    private static final String TAG = "PrintJob";

    private Context mContext;
    private SerialPrinter mSerialPrinter = SerialPrinter.GetSerialPrinter();
    private WakeLock mLock;

    public PrintJob(Context context, ArrayList<POSData> posData) {
        HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
        try {
            mSerialPrinter.OpenPrinter(new SerialParam(115200, "/dev/ttyS1", 0), new SerialDataHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);

        // Print Array List
        new WriteThread(posData).start();
    }

    private class WriteThread extends Thread {
        ArrayList<POSData> posData;

        public WriteThread(ArrayList<POSData> posData) {
            this.posData = posData;
        }

        public void run() {
            super.run();
            mLock.acquire();
            try {
                HdxUtil.SetPrinterPower(1);
                sleep(200);
                sendCharacterDemo(posData);
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.release();
                HdxUtil.SetPrinterPower(0);
            }
        }
    }

    private  void sendCharacterDemo(ArrayList<POSData> posData) {
        Log.e(TAG, "Start Print");
        try {
            mSerialPrinter.enlargeFontSize(3,3);
            mSerialPrinter.printString("Makanan ");
            mSerialPrinter.setHighlight(true);
            mSerialPrinter.printString("RESTAURANT");
            mSerialPrinter.setHighlight(false);
            mSerialPrinter.enlargeFontSize(1, 1);
            mSerialPrinter.printString(posData.get(0).title);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString(posData.get(0).image);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("" + posData.get(0).price);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("" + posData.get(0).quantity);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("" + posData.get(0).price);
            mSerialPrinter.setLineSpace(30);
            mSerialPrinter.enlargeFontSize(2, 2);
            mSerialPrinter.printString("Total: Rp 90.000,-");

            mSerialPrinter.walkPaper(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Resources r = getResources();
//        InputStream is = r.openRawResource(R.drawable.test);
//        BitmapDrawable  bmpDraw = new BitmapDrawable(is);
//        Bitmap bmp = bmpDraw.getBitmap();
//        mSerialPrinter.PrintBmp(100,bmp);

        Log.e(TAG, "Print End");
    }

    private class SerialDataHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SerialPortOperaion.SERIAL_RECEIVED_DATA_MSG:
                    SerialReadData data = (SerialReadData) msg.obj;
                    StringBuilder sb = new StringBuilder();
                    for (int x=0;x<data.size;x++) {
                        sb.append(String.format("%02x", data.data[x]));
                    }
                    Log.d(TAG, "data =" + sb);
                    if ((data.data[0]&1) == 1) {
                        Toast.makeText(mContext, mContext.getString(R.string.no_paper), Toast.LENGTH_SHORT).show();
                    }
                    if ((data.data[0]&2) == 2) {
                        Toast.makeText(mContext, mContext.getString(R.string.buff_fulled), Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }
}
