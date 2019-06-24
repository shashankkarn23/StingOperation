package com.shashank.seconpart.svrecorder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class MyReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number= intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if(number.equals("**11##")){
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        else if(number.equals("**22##")){
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
    }
}

