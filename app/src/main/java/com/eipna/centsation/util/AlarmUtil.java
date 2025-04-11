package com.eipna.centsation.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.receiver.DeadlineReceiver;

public class AlarmUtil {

    public static int NO_ALARM = 0;

    public static void set(Context context, Saving saving) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DeadlineReceiver.class);
        intent.putExtra("saving_id", saving.getID());
        intent.putExtra("saving_name", saving.getName());
        intent.putExtra("saving_deadline", saving.getDeadline());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, saving.getID().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, saving.getDeadline(), pendingIntent);
    }

    public static void cancel(Context context, Saving saving) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DeadlineReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, saving.getID().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}