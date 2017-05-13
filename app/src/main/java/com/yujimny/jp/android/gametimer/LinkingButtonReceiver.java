package com.yujimny.jp.android.gametimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class LinkingButtonReceiver extends BroadcastReceiver {

    private static final boolean DBG = false;
    private static final String TAG = LinkingButtonReceiver.class.getSimpleName();

    private static final String EXTRA_BUTTON_ID = "com.nttdocomo.android.smartdeviceagent.extra.NOTIFICATION_DEVICE_BUTTON_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        int buttonId = intent.getIntExtra(EXTRA_BUTTON_ID, -1);
        TimerService.startActionButtonPress(context, buttonId);
        if(DBG) Log.d(TAG, "received pairing notification : buttonId[" + buttonId + "]");
    }
}
