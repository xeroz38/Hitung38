package com.zhack.poskasir.util;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.hdx.lib.printer.SerialPrinter;
import com.hdx.lib.serial.SerialParam;
import com.hdx.lib.serial.SerialPortOperaion;
import com.hdx.lib.serial.SerialPortOperaion.SerialReadData;
import com.pda3505.printer.PrinterClassSerialPort;
import com.zhack.poskasir.R;
import com.zhack.poskasir.model.Invoice;
import com.zkc.helper.printer.PrinterClass;

import hdx.HdxUtil;

/**
 * Created by xeRoz on 8/1/2015.
 */
public class PrintJob {

    private Context mContext;
    private Invoice mInvoice;
    private SerialPrinter mSerialPrinter = SerialPrinter.GetSerialPrinter();;
    private PrinterClassSerialPort printerClass;
    private WakeLock mLock;
    private TelephonyManager telephonyManager;

    public PrintJob(Context context, Invoice invoice) {
        mContext = context;
        mInvoice = invoice;

        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        initPrinter();
    }

    private void initPrinter() {
        switch (Build.MODEL) {
            case "ww808_emmc" : {
                printerClass = new PrinterClassSerialPort(new SerialMiniHandler());
                printerClass.open(mContext);
                printMiniChar();

                break;
            }
            case "12": {
                try {
                    HdxUtil.SwitchSerialFunction(HdxUtil.SERIAL_FUNCTION_PRINTER);
                    mSerialPrinter.OpenPrinter(new SerialParam(115200, "/dev/ttyS1", 0), new SerialDataHandler());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                mLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "PrintJob");
                // Print Array List
                new WriteThread().start();

                break;
            }
            default: break;
        }
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
            mSerialPrinter.printString("MESIN       : " + telephonyManager.getDeviceId());
            mSerialPrinter.sendLineFeed();
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

    private void printMiniChar() {
        printerClass.printText("PEMPROV DKI JAKARTA\n");
        printerClass.printText(mInvoice.restaurant + "\n");
        printerClass.printText(mInvoice.address + "\n");
        printerClass.printText("================================");
        printerClass.printText("MESIN       : " + telephonyManager.getDeviceId() + "\n");
        printerClass.printText("STRUK       : " + mInvoice.id + "\n");
        printerClass.printText("TANGGAL     : " + mInvoice.date + "\n");
        printerClass.printText("================================");

        int totalPrice = 0;
        for (int i=0; i<mInvoice.posData.size(); i++) {
            totalPrice += mInvoice.posData.get(i).quantity * mInvoice.posData.get(i).price;

            printerClass.printText("" + (i + 1) + ". " + mInvoice.posData.get(i).title + "\n");
            printerClass.printText("   " + mInvoice.posData.get(i).quantity + " X " + mInvoice.posData.get(i).price +
                    " =          " + mInvoice.posData.get(i).quantity * mInvoice.posData.get(i).price + "\n");
        }
        printerClass.printText("================================");
        printerClass.printText("Sub Total   :           " + totalPrice + "\n");
        printerClass.printText("Service     :           " + "0" + "\n");
        printerClass.printText("Pajak       :    10%    " + totalPrice / 10 + "\n");
        printerClass.printText("            --------------------");
        printerClass.printText("Grand Total :           " + (totalPrice + totalPrice / 10) + "\n");
        printerClass.printText("Bayar       :           " + mInvoice.pay + "\n");
        printerClass.printText("Kembalian   :           " + (mInvoice.pay - (totalPrice + totalPrice / 10)) + "\n");
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

    private class SerialMiniHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterClass.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf[0] == 0x13) {
                        Toast.makeText(mContext, mContext.getString(R.string.buff_fulled), Toast.LENGTH_SHORT).show();
                    } else if (readBuf[0] == 0x11) {
                        Toast.makeText(mContext, mContext.getString(R.string.buff_fulled), Toast.LENGTH_SHORT).show();
                    } else if (readBuf[0] == 0x08) {
                    } else if (readBuf[0] == 0x01) {
                    } else if (readBuf[0] == 0x04) {
                    } else if (readBuf[0] == 0x02) {
                    } else {
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        if (readMessage.contains("800")) {
                            Toast.makeText(mContext, "80mm", Toast.LENGTH_SHORT).show();
                        } else if (readMessage.contains("580")) {
                            Toast.makeText(mContext, "58mm", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case PrinterClass.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case PrinterClass.STATE_CONNECTED://
                            break;
                        case PrinterClass.STATE_CONNECTING://
                            Toast.makeText(mContext, "STATE_CONNECTING", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.STATE_LISTEN:
                        case PrinterClass.STATE_NONE:
                            break;
                        case PrinterClass.SUCCESS_CONNECT:
                            Toast.makeText(mContext, "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.FAILED_CONNECT:
                            Toast.makeText(mContext, "FAILED_CONNECT", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterClass.LOSE_CONNECT:
                            Toast.makeText(mContext, "LOSE_CONNECT", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PrinterClass.MESSAGE_WRITE:
                    break;
            }
        }
    };
}
