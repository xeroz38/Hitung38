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
import com.zhack.poskasir.model.Invoice;

import hdx.HdxUtil;

/**
 * Created by xeRoz on 8/1/2015.
 */
public class PrintJob {

    private Context mContext;
    private Invoice mInvoice;
    private SerialPrinter mSerialPrinter = SerialPrinter.GetSerialPrinter();;
    private WakeLock mLock;

    public PrintJob(Context context, Invoice invoice) {
        mContext = context;
        mInvoice = invoice;
        try {
            HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
            mSerialPrinter.OpenPrinter(new SerialParam(115200, "/dev/ttyS1", 0), new SerialDataHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "PrintJob");
        // Print Array List
        new WriteThread().start();
    }

    private class WriteThread extends Thread {

        public void run() {
            super.run();
            mLock.acquire();
            try {
                HdxUtil.SetPrinterPower(1);
                sleep(200);
                printStringChar();
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.release();
                HdxUtil.SetPrinterPower(0);
            }
        }
    }

    private  void printStringChar() {
        Log.e("PrintJob", "Start Print");
        try {
            mSerialPrinter.printString("PEMPROV DKI JAKARTA");
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString(mInvoice.restaurant);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString(mInvoice.address);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("================================");
            mSerialPrinter.printString("STRUK       : " + mInvoice.id);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("TANGGAL     : " + mInvoice.date);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("================================");

            int totalPrice = 0;
            for (int i=0; i<mInvoice.posData.size(); i++) {
                totalPrice += mInvoice.posData.get(i).quantity * mInvoice.posData.get(i).price;

                mSerialPrinter.printString("" + (i+1) + ". " + mInvoice.posData.get(i).title);
                mSerialPrinter.sendLineFeed();
                mSerialPrinter.printString("   " + mInvoice.posData.get(i).quantity + " X " + mInvoice.posData.get(i).price +
                        " =          " + mInvoice.posData.get(i).quantity * mInvoice.posData.get(i).price);
                mSerialPrinter.sendLineFeed();
            }
            mSerialPrinter.printString("================================");
            mSerialPrinter.printString("Sub Total   :           " + totalPrice);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("Service     :           " + "0");
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("Pajak       :    10%    " + totalPrice / 10);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("            --------------------");
            mSerialPrinter.printString("Grand Total :           " + (totalPrice + totalPrice / 10));
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("Bayar       :           " + mInvoice.pay);
            mSerialPrinter.sendLineFeed();
            mSerialPrinter.printString("Kembalian   :           " + (mInvoice.pay - (totalPrice + totalPrice / 10)));

            mSerialPrinter.walkPaper(120);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("PrintJob", "Print End");
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
                    Log.d("PrintJob", "data =" + sb);
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
