package com.fulluse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

/**
 * Created by Al Cheong on 6/23/2017.
 */

public class TaskBootReceiver extends BroadcastReceiver {

    private static final int TASK_NOTIFICATION_CODE = 9999;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Restart all alarms
            Calendar calendarAlarm = Calendar.getInstance();
            Calendar calendarNow = Calendar.getInstance();
            calendarAlarm.setTimeInMillis(System.currentTimeMillis());
            calendarNow.setTimeInMillis(System.currentTimeMillis());
            calendarAlarm.set(Calendar.HOUR_OF_DAY, 9);
            calendarAlarm.set(Calendar.MINUTE, 0);
            if (calendarAlarm.before(calendarNow)) calendarAlarm.add(Calendar.DATE, 1);

            Intent alarmIntent = new Intent(context, TaskAlertReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, TASK_NOTIFICATION_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Android M, API level 23 : escape doze mode

                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendarAlarm.getTimeInMillis(),
                        pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                // Android Kitkat, API level 19

                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendarAlarm.getTimeInMillis(),
                        pi);
            } else alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendarAlarm.getTimeInMillis(),
                    pi);
        }
    }
}
