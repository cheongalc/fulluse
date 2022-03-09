package com.fulluse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by Al Cheong on 7/27/2017.
 */

public class EventAlertBootReceiver extends BroadcastReceiver {

    private static final int EVENT_NOTIFICATION_CODE = 1111;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Restart all alarms


            Intent alarmIntent = new Intent(context, EventAlertReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, EVENT_NOTIFICATION_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Android M, API level 23 : escape doze mode

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 5000,
                        pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                // Android Kitkat, API level 19

                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 5000,
                        pi);
            } else alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 5000,
                    pi);
        }
    }
}
