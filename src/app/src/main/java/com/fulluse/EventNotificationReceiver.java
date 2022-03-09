package com.fulluse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Al Cheong on 7/26/2017.
 */

public class EventNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("eventName");
        String eventTiming = intent.getStringExtra("eventTiming");
        int requestCode = intent.getIntExtra("requestCode", 0);
        pushNotification(context, name, requestCode, eventTiming);
    }

    private void pushNotification(Context context, String name, int requestCode, String eventTiming) {
        PendingIntent notifIntent = PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_notif)
                .setContentTitle("Upcoming Event: " + name)
                .setTicker("You have an upcoming event!")
                .setContentText(eventTiming);
        builder.setContentIntent(notifIntent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(requestCode, builder.build());
    }
}
