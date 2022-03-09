package com.fulluse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by Al Cheong on 7/26/2017.
 */

public class EventAlertReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "EVENTALERTRECEIVER";
    private static final int EVENT_NOTIFICATION_CODE = 1111;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "Received request, starting EventNotificationCheckingService.");

        context.startService(new Intent(context, EventNotificationCheckingService.class));
        Intent alarmIntent = new Intent(context, EventAlertReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, EVENT_NOTIFICATION_CODE, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M, API level 23 : escape doze mode

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pi);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Android Kitkat, API level 19

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pi);
        } else alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                pi);
    }
}
