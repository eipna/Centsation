package com.eipna.centsation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eipna.centsation.util.NotificationUtil;

public class DeadlineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.create(context, intent);
    }
}