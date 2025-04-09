package com.eipna.centsation.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.receiver.DeadlineReceiver;

import java.util.Calendar;

public class AlarmUtil {

    public static int NO_ALARM = 0;

    public static void set(Context context, Saving saving) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DeadlineReceiver.class);
        intent.putExtra("saving_name", saving.getName());
        intent.putExtra("saving_deadline", saving.getDeadline());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(saving.getDeadline());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) saving.getDeadline(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancel(Context context, Saving saving) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, DeadlineReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) saving.getDeadline(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}