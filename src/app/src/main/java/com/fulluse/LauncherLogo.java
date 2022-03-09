package com.fulluse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class LauncherLogo extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private Intent i;
    private static final String LOG_TAG = "LAUNCHERLOGO";

    private boolean checkAppRunFirstSinceInstallation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean("firstTimeSinceInstallation", true);
    }

    private boolean checkAppRunFirstTimeToday() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH) + 1,
                day = c.get(Calendar.DAY_OF_MONTH);

        String dateToday = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year),
                dateInStorage = sharedPreferences.getString("dateToday", "");

        Log.d(LOG_TAG, "dateToday: " + dateToday);
        Log.d(LOG_TAG, "dateInStorage: " + dateInStorage);

        /*
            if !dateToday.equals(dateInStorage) returns true this means dateToday != dateInStorage, i.e. this is first run
            if !dateToday.equals(dateInStorage) returns false it means dateToday = dateInStorage, i.e. this isn't first run
        */
        return !dateToday.equals(dateInStorage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_logo);
        Log.d(LOG_TAG, "Launcher Logo Started");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        i = new Intent(this, MainActivity.class);
        Log.d(LOG_TAG, "checkAppRunFirstSinceInstallation: " + String.valueOf(checkAppRunFirstSinceInstallation()));
        if (!checkAppRunFirstSinceInstallation()) {
            Log.d(LOG_TAG, "checkAppRunFirstTimeToday: " + String.valueOf(checkAppRunFirstTimeToday()));
            boolean a = checkAppRunFirstTimeToday();
            if (a) {
                editor.putInt("dayNumber", sharedPreferences.getInt("dayNumber", 1)+1);
                editor.apply();
                i = new Intent(this, MorningReview.class);
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        },1500);
    }
}
