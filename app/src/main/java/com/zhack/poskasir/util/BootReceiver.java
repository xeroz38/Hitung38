package com.zhack.poskasir.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhack.poskasir.MainActivity;

/**
 * Created by zunaidi.chandra on 04/08/2015.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }
}
