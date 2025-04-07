package com.eipna.centsation.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.eipna.centsation.R;
import com.eipna.centsation.ui.activities.MainActivity;

public class NotificationUtil {

    public static String CHANNEL_DEADLINE_ID = "channel_deadline";
    public static String CHANNEL_DEADLINE_NAME = "Deadlines";
    public static int CHANNEL_DEADLINE_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel deadlineChannel = new NotificationChannel(CHANNEL_DEADLINE_ID, CHANNEL_DEADLINE_NAME, CHANNEL_DEADLINE_IMPORTANCE);
            notificationManager.createNotificationChannel(deadlineChannel);
        }
    }

    public static void create(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String savingName = intent.getStringExtra("saving_name");
        int savingRequestCode = (int) intent.getLongExtra("saving_deadline", -1);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, savingRequestCode, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DEADLINE_ID)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Saving Deadline")
                .setContentText(String.format("The deadline for your savings, %s, has been reached.", savingName))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(savingRequestCode, builder.build());
    }
}