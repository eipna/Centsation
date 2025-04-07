package com.eipna.centsation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eipna.centsation.data.saving.Saving;
import com.eipna.centsation.data.saving.SavingRepository;
import com.eipna.centsation.util.AlarmUtil;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            rescheduleDeadlines(context);
        }
    }

    private void rescheduleDeadlines(Context context) {
        try (SavingRepository savingRepository = new SavingRepository(context)) {
            ArrayList<Saving> savings = new ArrayList<>(savingRepository.getSavings(Saving.NOT_ARCHIVE));
            for (Saving saving : savings) {
                if (saving.getDeadline() != AlarmUtil.NO_ALARM) {
                    AlarmUtil.set(context, saving);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}