package com.eipna.centsation.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.util.AlarmUtil;
import com.eipna.centsation.util.NotificationUtil;

import java.util.ArrayList;

public class DeadlineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(intent.getAction())) {
            rescheduleDeadlines(context);
        } else {
            NotificationUtil.create(context, intent);
        }
    }

    private void rescheduleDeadlines(Context context) {
        try (SavingRepository savingRepository = new SavingRepository(context)) {
            ArrayList<Saving> savings = new ArrayList<>(savingRepository.getAllSavings());
            for (Saving saving : savings) {
                if (saving.getDeadline() != AlarmUtil.NO_ALARM) {
                    AlarmUtil.set(context, saving);
                }
            }
        } catch (Exception e) {
            Log.e("Reschedule Alarm", "Error while rescheduling some deadlines", e);
        }
    }
}