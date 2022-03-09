package com.fulluse;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.regex.Pattern;

public class EditEvent extends AppCompatActivity {

    private DBHelper dbHelper;

    //Save these to update the task in db
    private String originalName;
    private int originalID;

    //integers for updating date & time
    private int startYear,
            startMonth,
            startDay,
            startHour,  //24 hour
            startMinute;
    private int endYear,
            endMonth,
            endDay,
            endHour,  //24 hour
            endMinute;

    private static final String ERROR_EVENT_NAME_BLANK = "ERROR_EVENT_NAME_BLANK";
    private static final String ERROR_EVENT_ZERO_MINUTES = "ERROR_EVENT_ZERO_MINUTES";
    private static final String ERROR_EVENT_END_EARLIER  = "ERROR_EVENT_END_EARLIER";

    //Listeners for DatePickerDialogs
    private DatePickerDialog.OnDateSetListener changeStartDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear+1;
            startDay = dayOfMonth;
            TextView startDateTextView = (TextView) findViewById(R.id.tv_eventStartDate);
            startDateTextView.setText(String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
        }
    }, changeEndDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            endYear = year;
            endMonth = monthOfYear+1;
            endDay = dayOfMonth;
            TextView endDateTextView = (TextView) findViewById(R.id.tv_eventEndDate);
            endDateTextView.setText(String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
        }
    };

    //Listeners for TimePickerDialogs
    private TimePickerDialog.OnTimeSetListener changeStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
            String displayedStartTime;
            if (hourOfDay < 10) displayedStartTime = "0" + String.valueOf(hourOfDay);
            else displayedStartTime = String.valueOf(hourOfDay);
            if (minute < 10) displayedStartTime += ":0" + String.valueOf(minute);
            else displayedStartTime += ":" + String.valueOf(minute);
            TextView startTimeTextView = (TextView) findViewById(R.id.tv_eventStartTime);
            startTimeTextView.setText(displayedStartTime);
        }
    }, changeEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            endHour = hourOfDay;
            endMinute = minute;
            String displayedEndTime;
            if (hourOfDay < 10) displayedEndTime = "0" + String.valueOf(hourOfDay);
            else displayedEndTime = String.valueOf(hourOfDay);
            if (minute < 10) displayedEndTime += ":0" + String.valueOf(minute);
            else displayedEndTime += ":" + String.valueOf(minute);
            TextView endTimeTextView = (TextView) findViewById(R.id.tv_eventEndTime);
            endTimeTextView.setText(displayedEndTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        dbHelper = new DBHelper(this);
        String eventName = getIntent().getStringExtra("eventName");
        originalName = eventName;

        EditText eventNameET = (EditText) findViewById(R.id.et_editEvent);
        eventNameET.setText(eventName);

        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = {
                DBHelper.EVENT_ID,
                DBHelper.EVENT_START_YR,
                DBHelper.EVENT_START_MTH,
                DBHelper.EVENT_START_DAY,
                DBHelper.EVENT_START_TIME,
                DBHelper.EVENT_END_YR,
                DBHelper.EVENT_END_MTH,
                DBHelper.EVENT_END_DAY,
                DBHelper.EVENT_END_TIME
        };
        String whereClause = DBHelper.EVENT_NAME + " =?";
        String[] whereArgs = {originalName};

        Cursor cursor = db.query(
                DBHelper.TABLE_EVENTS,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        cursor.move(1);

        originalID = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_ID));
        startYear = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_YR));
        startMonth = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_MTH));
        startDay = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_DAY));
        endYear = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_YR));
        endMonth = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_MTH));
        endDay = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_DAY));
        String startTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START_TIME)),
               endTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END_TIME));
        startHour = Integer.parseInt(startTime.substring(0,2));
        startMinute = Integer.parseInt(startTime.substring(2,4));
        endHour = Integer.parseInt(endTime.substring(0,2));
        endMinute = Integer.parseInt(endTime.substring(2,4));

        TextView startDateTextView = (TextView) findViewById(R.id.tv_eventStartDate);
        startDateTextView.setText(String.valueOf(startMonth) + "/" + String.valueOf(startDay) + "/" + String.valueOf(startYear));

        TextView endDateTextView = (TextView) findViewById(R.id.tv_eventEndDate);
        endDateTextView.setText(String.valueOf(endMonth) + "/" + String.valueOf(endDay) + "/" + String.valueOf(endYear));

        String displayedStartTime;
        if (startHour < 10) displayedStartTime = "0" + String.valueOf(startHour);
        else displayedStartTime = String.valueOf(startHour);
        if (startMinute < 10) displayedStartTime += ":0" + String.valueOf(startMinute);
        else displayedStartTime += ":" + String.valueOf(startMinute);
        TextView startTimeTextView = (TextView) findViewById(R.id.tv_eventStartTime);
        startTimeTextView.setText(displayedStartTime);

        String displayedEndTime;
        if (endHour < 10) displayedEndTime = "0" + String.valueOf(endHour);
        else displayedEndTime = String.valueOf(endHour);
        if (endMinute < 10) displayedEndTime += ":0" + String.valueOf(endMinute);
        else displayedEndTime += ":" + String.valueOf(endMinute);
        TextView endTimeTextView = (TextView) findViewById(R.id.tv_eventEndTime);
        endTimeTextView.setText(displayedEndTime);
    }



    public void showChangeStartDateDialog(View view) {
        new DatePickerDialog(EditEvent.this, changeStartDateListener, startYear, startMonth-1, startDay).show();
    }

    public void showChangeStartTimeDialog(View view) {
        new TimePickerDialog(EditEvent.this, changeStartTimeListener, startHour, startMinute, true).show();
    }

    public void showChangeEndDateDialog(View view) {
        new DatePickerDialog(EditEvent.this, changeEndDateListener, endYear, endMonth-1, endDay).show();
    }

    public void showChangeEndTimeDialog(View view) {
        new TimePickerDialog(EditEvent.this, changeEndTimeListener, endHour, endMinute, true).show();
    }

    public void finishEditing(View view) {
        String newName = String.valueOf(((EditText) findViewById(R.id.et_editEvent)).getText());

        String displayedStartTime;
        if (startHour < 10) displayedStartTime = "0" + String.valueOf(startHour);
        else displayedStartTime = String.valueOf(startHour);
        if (startMinute < 10) displayedStartTime += "0" + String.valueOf(startMinute);
        else displayedStartTime += String.valueOf(startMinute);

        String displayedEndTime;
        if (endHour < 10) displayedEndTime = "0" + String.valueOf(endHour);
        else displayedEndTime = String.valueOf(endHour);
        if (endMinute < 10) displayedEndTime += "0" + String.valueOf(endMinute);
        else displayedEndTime += String.valueOf(endMinute);

        Log.d("EDITEVENT", "startTime: " + displayedStartTime + ", endTime: " + displayedEndTime);

        DateTime startDate = new DateTime(startYear, startMonth-1, startDay, startHour, startMinute);
        DateTime endDate = new DateTime(endYear, endMonth-1, endDay, endHour, endMinute);

        if (isEventNameBlank(newName)) {showAlertDialog(ERROR_EVENT_NAME_BLANK); return;}
        if (isEventZeroMinutes(startDate, endDate)) {showAlertDialog(ERROR_EVENT_ZERO_MINUTES); return;}
        if (isEventEndEarlier(startDate, endDate)) {showAlertDialog(ERROR_EVENT_END_EARLIER); return;}

        dbHelper.openDB();
        dbHelper.editEvent(originalID, newName,
                startYear, startMonth, startDay, displayedStartTime,
                endYear, endMonth, endDay, displayedEndTime);

        showToast();
        finish();
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

    private boolean isEventNameBlank (String eventName) {
        return eventName.equals("");
    }
    private boolean isEventZeroMinutes(DateTime startDate, DateTime endDate) {
        return startDate.isEqual(endDate);
    }
    private boolean isEventEndEarlier(DateTime startDate, DateTime endDate) {
        return endDate.isBefore(startDate);
    }
    private boolean isEventContainingTest(String eventName) {
        String[] testStrings = {
                "test",
                "exam",
                "quiz",
                "assessment",
                "eoy",
                "ct",
                "fa",
                "graded"
        };

        for (int i = 0; i < testStrings.length; i++) {
            if (Pattern.compile(Pattern.quote(testStrings[i]), Pattern.CASE_INSENSITIVE).matcher(eventName).find())
                return true;
        }
        return false;
    }

    private void showAlertDialog(String tag) {
        switch (tag) {
            case ERROR_EVENT_NAME_BLANK:
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Your event name is blank")
                        .show();
                break;
            case ERROR_EVENT_ZERO_MINUTES:
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Your event start time and end time is the same. It cannot be 0 minutes.")
                        .show();
                break;
            case ERROR_EVENT_END_EARLIER:
                new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Your event end is earlier than your event start.")
                        .show();
                break;
            default: break;
        }
    }

    private void showToast() {
        String toast = "Event " + originalName + " updated.";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
