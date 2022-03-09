package com.fulluse;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.util.Calendar;


/**
 * Created by Al Cheong on 7/26/2017.
 */

public class EventNotificationCheckingService extends IntentService {

    private DBHelper dbHelper;
    private static final String LOG_TAG = "EVENTNOTIFCHECKSVC";

    public EventNotificationCheckingService() {
        super("EventNotificationCheckingThread");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        Log.d(LOG_TAG, "EventNotificationCheckingService started.");

        dbHelper = new DBHelper(this);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH),
                oneIndexedMonth = month + 1,
                day = c.get(Calendar.DAY_OF_MONTH);

        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = new String[] {
                DBHelper.EVENT_ID,
                DBHelper.EVENT_NAME,
                DBHelper.EVENT_START_TIME,
                DBHelper.EVENT_END_TIME
        };
        String whereClause = DBHelper.EVENT_START_DAY + " = ? AND " + DBHelper.EVENT_START_MTH + " = ? AND " + DBHelper.EVENT_START_YR + " = ?";
        String whereArgs[] = new String[]{
                String.valueOf(day),
                String.valueOf(oneIndexedMonth),
                String.valueOf(year)
        };
        Cursor cursor = db.query(DBHelper.TABLE_EVENTS, columns, whereClause, whereArgs, null, null, null);
        while (cursor.moveToNext()) {
            int indexID = cursor.getColumnIndex(DBHelper.EVENT_ID)
                    , indexName = cursor.getColumnIndex(DBHelper.EVENT_NAME)
                    , indexStartTime = cursor.getColumnIndex(DBHelper.EVENT_START_TIME)
                    , indexEndTime = cursor.getColumnIndex(DBHelper.EVENT_END_TIME);

            int _id = cursor.getInt(indexID) - Integer.MAX_VALUE;
            String eventName = cursor.getString(indexName);
            String eventStartTime = cursor.getString(indexStartTime);
            String eventEndTime = cursor.getString(indexEndTime);
            int eventStartHour = Integer.parseInt(eventStartTime.substring(0,2));
            int eventStartMinute = Integer.parseInt(eventStartTime.substring(2,4));

            Log.d(LOG_TAG, "Looking at event: [name: " + eventName + ", startTime: " + eventStartTime  + ", endTime: " + eventEndTime + "]");

            DateTime eventDate = new DateTime(year, oneIndexedMonth, day, eventStartHour, eventStartMinute);
            DateTime oneHourBefore = eventDate.minusHours(1),
                     halfHourBefore = eventDate.minusMinutes(30),
                     fifteenMinBefore = eventDate.minusMinutes(15),
                     fiveMinBefore = eventDate.minusMinutes(5);
            DateTime currentDate = new DateTime();
            if (oneHourBefore.isBefore(currentDate)) {
                Log.d(LOG_TAG, "Time Now has passed 1 hour before event (" + eventName + ").");
                // If the time now has already passed 1 hour before the event
                // then check if time now passed 30 min before event
                if (halfHourBefore.isBefore(currentDate)) {
                    // If the time now already passed 1 hour before event & 30 min before event
                    Log.d(LOG_TAG, "Time Now has passed 30 min before event (" + eventName + ").");
                    if (fifteenMinBefore.isBefore(currentDate)) {
                        // If the time now already passed 1 hour, 30 min and 15 min before event
                        Log.d(LOG_TAG, "Time Now has passed 15 min before event (" + eventName + ").");
                        if (fiveMinBefore.isBefore(currentDate)) {
                            Log.d(LOG_TAG, "Time Now has passed 5 min before event (" + eventName + ").");
                            continue;
                        } else {
                            eventDate = fiveMinBefore;
                        }
                    } else {
                        Log.d(LOG_TAG, "Time Now has passed 30 min but not 15 min before event (" + eventName + ").");
                        eventDate = fifteenMinBefore;
                    }
                } else {
                    Log.d(LOG_TAG, "Time Now has passed 1 hour but not 30 min before event (" + eventName + ").");
                    eventDate = halfHourBefore;
                }
            } else {
                Log.d(LOG_TAG, "Time Now has not passed 1 hour before event (" + eventName + ").");
                eventDate = oneHourBefore;
            }

            Interval intervalToEvent = new Interval(currentDate, eventDate);
            Duration durationOfInterval = intervalToEvent.toDuration();
            Long millisOfDuration = durationOfInterval.getMillis();

            Log.d(LOG_TAG, "Setting alarm id " + String.valueOf(_id) + " for event " + eventName + " due to start in " + String.valueOf(millisOfDuration) + " ms");

            Intent intent = new Intent(this, EventNotificationReceiver.class);
            intent.putExtra("eventName", eventName);
            intent.putExtra("requestCode", _id);
            intent.putExtra("eventTiming", eventStartTime + " - " + eventEndTime);
            PendingIntent pi = PendingIntent.getBroadcast(this, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Android M, API level 23 : escape doze mode
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + millisOfDuration,
                        pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                // Android Kitkat, API level 19

                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + millisOfDuration,
                        pi);
            } else alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + millisOfDuration,
                    pi);
        }
        Log.d(LOG_TAG, "EventNotificationCheckingService ended.");
    }
}
