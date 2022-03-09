package com.fulluse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by Al Cheong on 5/28/2017.
 */

public class TaskAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFSVCTHREAD", "Request Received.");



        context.startService(new Intent(context, TaskNotificationService.class));

        Intent alarmIntent = new Intent(context, TaskAlertReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 9999, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M, API level 23 : escape doze mode

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                    pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Android Kitkat, API level 19

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                    pi);
        } else alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                pi);
    }
}
