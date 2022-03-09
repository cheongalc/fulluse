package com.fulluse;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Calendar;

/**
 * Created by Al Cheong on 6/6/2017.
 */

public class TaskNotificationService extends IntentService {

    private static final String LOG_TAG = "TASKNOTIFSVC";
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private NotificationManager notificationManager;

    private static final int REQUESTFLAG_STT = 1234;
    private static final int REQUESTFLAG_LTT = 2345;

    public TaskNotificationService() {
        super("TaskNotificationServiceThread");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        dbHelper = new DBHelper(this);

        Log.d("NOTIFSVCTHREAD", "Service Started.");

        Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        DateTime currentCalendar = new DateTime(year, month, day, 0, 0);
        goThroughSTTs(currentCalendar);
        goThroughLTTs(currentCalendar);
        goThroughTreeStats();

        Log.d("NOTIFSVCTHREAD", "Service Ended.");

    }

    private void goThroughTreeStats() {
        int water = sharedPreferences.getInt("tree_water", 100);
        if (water < 40) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_notif)
                    .setContentTitle("Dehydrating Tree!")
                    .setTicker("Oh no!")
                    .setContentText("Your tree is dehydrating! Quick save it!");
            builder.setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0));
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
            builder.setAutoCancel(true);

            notificationManager.notify(1, builder.build());

        }
    }

    private void goThroughLTTs(DateTime currentCalendar) {
        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = new String[] {
                DBHelper.LONG_TERM_TASK_ID,
                DBHelper.LONG_TERM_TASK_NAME,
                DBHelper.LONG_TERM_TASK_PRIORITY,
                DBHelper.LONG_TERM_TASK_ENDDAY,
                DBHelper.LONG_TERM_TASK_ENDMTH,
                DBHelper.LONG_TERM_TASK_ENDYR
        };
        Cursor cursor = db.query(DBHelper.TABLE_LONG_TERM_TASK, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int indexID = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ID)
                    , indexName = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_NAME)
                    , indexPriority = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_PRIORITY)
                    , indexEndDay = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDDAY)
                    , indexEndMth = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDMTH)
                    , indexEndYr = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDYR);

            int _id = cursor.getInt(indexID)
                    , taskPriority = cursor.getInt(indexPriority)
                    , taskEndDay = cursor.getInt(indexEndDay)
                    , taskEndMth = cursor.getInt(indexEndMth)
                    , taskEndYr = cursor.getInt(indexEndYr);
            String taskName = cursor.getString(indexName);

            Log.d(LOG_TAG, "Looking at LTT [name: " + taskName + ", priority: " + String.valueOf(taskPriority) + "]");

            DateTime taskCalendar = new DateTime(taskEndYr, taskEndMth, taskEndDay, 0, 0),
                     twoWeeksBefore = taskCalendar.minusWeeks(2),
                     oneWeekBefore = taskCalendar.minusWeeks(1);
            Log.d(LOG_TAG, "Today's date: " + String.valueOf(currentCalendar));
            Log.d(LOG_TAG, "LTT name(" + taskName + ") originalDate: " + String.valueOf(taskCalendar));
            Log.d(LOG_TAG, "LTT name(" + taskName + ") twoWeeksBefore: " + String.valueOf(twoWeeksBefore));
            Log.d(LOG_TAG, "LTT name(" + taskName + ") oneWeekBefore: " + String.valueOf(oneWeekBefore));

            boolean isTwoWeeksBefore = currentCalendar.withTimeAtStartOfDay().isEqual(twoWeeksBefore.withTimeAtStartOfDay()),
                    isOneWeekBefore = currentCalendar.withTimeAtStartOfDay().isEqual(oneWeekBefore.withTimeAtStartOfDay());

            Log.d(LOG_TAG, "is LTT name(" + taskName + ") twoWeeksLater: " + String.valueOf(isTwoWeeksBefore));
            Log.d(LOG_TAG, "is LTT name(" + taskName + ") oneWeekLater: " + String.valueOf(isOneWeekBefore));

            if (isTwoWeeksBefore) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_notif)
                        .setContentTitle(taskName)
                        .setTicker("You have a long term task!")
                        .setContentText("Your long term task is ending in 2 weeks time. Remember to work on it!");
                builder.setContentIntent(PendingIntent.getActivity(this, REQUESTFLAG_LTT, new Intent(this, MainActivity.class), 0));
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                builder.setAutoCancel(true);

                notificationManager.notify(_id, builder.build());

                if (taskPriority > 1) taskPriority--;
                dbHelper.editLTT(_id, taskName, taskPriority, -1, -1, -1);
            } else if (isOneWeekBefore) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_notif)
                        .setContentTitle(taskName)
                        .setTicker("You have a long term task!")
                        .setContentText("Your long term task is ending in a week's time. Remember to work on it!");
                builder.setContentIntent(PendingIntent.getActivity(this, REQUESTFLAG_LTT, new Intent(this, MainActivity.class), 0));
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                builder.setAutoCancel(true);

                notificationManager.notify(_id, builder.build());

                if (taskPriority > 1) taskPriority--;
                dbHelper.editLTT(_id, taskName, taskPriority, -1, -1, -1);
            }
        }
        dbHelper.closeDB();
    }

    private void goThroughSTTs(DateTime currentCalendar) {

        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = new String[] {
                DBHelper.SHORT_TERM_TASK_ID,
                DBHelper.SHORT_TERM_TASK_NAME,
                DBHelper.SHORT_TERM_TASK_PRIORITY,
                DBHelper.SHORT_TERM_TASK_DUEDAY,
                DBHelper.SHORT_TERM_TASK_DUEMTH,
                DBHelper.SHORT_TERM_TASK_DUEYR,
                DBHelper.SHORT_TERM_TASK_TAGSTR
        };
        Cursor cursor = db.query(DBHelper.TABLE_SHORT_TERM_TASK, columns, null, null, null, null, null);

        Log.d(LOG_TAG, "Cursor size: " + String.valueOf(cursor.getCount()));

        while (cursor.moveToNext()) {
            int indexID = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_ID)
                    , indexName = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_NAME)
                    , indexPriority = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_PRIORITY)
                    , indexDueDay = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEDAY)
                    , indexDueMth = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEMTH)
                    , indexDueYr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEYR)
                    , indexTagStr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_TAGSTR);

            int _id = cursor.getInt(indexID);
            String taskName = cursor.getString(indexName);
            int taskPriority = cursor.getInt(indexPriority);
            Log.d("NOTIFSVC", String.valueOf(taskPriority));
            int taskDueDay = cursor.getInt(indexDueDay);
            int taskDueMth = cursor.getInt(indexDueMth);
            int taskDueYear = cursor.getInt(indexDueYr);
            String tagStr = cursor.getString(indexTagStr);

            Log.d(LOG_TAG, "Current STT tag: " + tagStr);

            DateTime taskCalendar = new DateTime(taskDueYear, taskDueMth, taskDueDay, 0, 0);
            DateTime taskDate = taskCalendar;
            DateTime twoDaysBefore = taskCalendar.minusDays(2);
            DateTime oneDayBefore = taskCalendar.minusDays(1);

            boolean isOnDayItself = currentCalendar.withTimeAtStartOfDay().isEqual(taskDate.withTimeAtStartOfDay());
            boolean isTwoDaysBefore = currentCalendar.withTimeAtStartOfDay().isEqual(twoDaysBefore.withTimeAtStartOfDay());
            boolean isOneDayBefore = currentCalendar.withTimeAtStartOfDay().isEqual(oneDayBefore.withTimeAtStartOfDay());

            if (isTwoDaysBefore) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_notif)
                        .setContentTitle(taskName)
                        .setTicker("You have a task!")
                        .setContentText("Your task is due in 2 days time. Remember to work on it!");
                builder.setContentIntent(PendingIntent.getActivity(this, REQUESTFLAG_STT, new Intent(this, MainActivity.class), 0));
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                builder.setAutoCancel(true);

                notificationManager.notify(_id, builder.build());

                if (taskPriority > 1) taskPriority--;
                dbHelper.editSTT(_id, taskName, taskPriority, -1, -1, -1, "`");
            } else if (isOneDayBefore) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_notif)
                        .setContentTitle(taskName)
                        .setTicker("You have a task!")
                        .setContentText("Your task is due in 1 day. Remember to work on it!");
                builder.setContentIntent(PendingIntent.getActivity(this, REQUESTFLAG_STT, new Intent(this, MainActivity.class), 0));
                builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
                builder.setAutoCancel(true);

                notificationManager.notify(_id, builder.build());

                if (taskPriority > 1) taskPriority--;
                dbHelper.editSTT(_id, taskName, taskPriority, -1, -1, -1, "`");
            } else if (isOnDayItself) {
                dbHelper.deleteFromLTT(taskName);
            }
        }
        dbHelper.closeDB();
    }
}
