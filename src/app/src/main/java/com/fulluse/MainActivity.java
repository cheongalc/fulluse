package com.fulluse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class MainActivity extends AppCompatActivity implements RootFragmentInterface {

    DBHelper dbHelper;

    // listView for short term task & long term task
    private ExpandableHeightListView shortTermTaskListView;
    private ExpandableHeightListView longTermTaskListView;

    // listView for free time
    private ExpandableHeightListView eventsListView;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private static final int TASK_NOTIFICATION_CODE = 9999;
    private static final int EVENT_NOTIFICATION_CODE = 1111;

    private TabLayout tabLayout;

    private float dp;

    private RelativeLayout mainLayout;
    private RelativeLayout eventsLayout;
    private RelativeLayout dashboardLayout;

    private String globalCurrentEventTitle = "";

    private static final String LOG_TAG = "MAINACTIVITY";

    private ViewPager viewPager;

    private GestureDetectorCompat gestureDetectorCompat;
    private TextView shortTermTaskTitle, longTermTaskTitle, eventHeaderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dp = MainActivity.this.getResources().getDisplayMetrics().density;

        dbHelper = new DBHelper(this);
        dbHelper.openDB();

        gestureDetectorCompat = new GestureDetectorCompat(this, new CustomGestureListener());

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day_of_month = c.get(Calendar.DAY_OF_MONTH);

        currentYear = year;
        currentMonth = month;
        currentDay = day_of_month;

        checkFirstTimeRunToday();
        handleFirstTimeRun();
        if (checkNotificationEnabled()) {
            initAlarms();
        }
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_done_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_event_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tree_tab_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager) findViewById(R.id.pager);
        final MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.setElevation(4 * dp);
        }
        getSupportActionBar().setElevation(4 * dp);
        getSupportActionBar().setTitle("To Do List");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    getSupportActionBar().setTitle("To Do List");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.setElevation(4 * dp);
                        getSupportActionBar().setElevation(4 * dp);
                    }


                } else if (tab.getPosition() == 1) {
                    DateTime nowDate = new DateTime();
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM").withLocale(Locale.US);
                    String month = formatter.print(nowDate);
                    if (!globalCurrentEventTitle.equals("")) {
                        getSupportActionBar().setTitle(globalCurrentEventTitle);
                    } else getSupportActionBar().setTitle(month + "'s Events");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.setElevation(0);
                        getSupportActionBar().setElevation(0);
                    }
                } else {
                    getSupportActionBar().setTitle("Dashboard");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.setElevation(4 * dp);
                        getSupportActionBar().setElevation(4 * dp);
                    }
                }

                Log.d("UI", String.valueOf(shortTermTaskListView.getAdapter()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTutorial();
            }
        }, 2000);
    }



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

        /*
            if !dateToday.equals(dateInStorage) returns true this means dateToday != dateInStorage, i.e. this is first run
            if !dateToday.equals(dateInStorage) returns false it means dateToday = dateInStorage, i.e. this isn't first run
        */
        return !dateToday.equals(dateInStorage);
    }

    private void checkFirstTimeRunToday() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (checkAppRunFirstTimeToday()) {

            Log.d(LOG_TAG, "App run first time today.");

            int tasksCompletedYesterday = sharedPreferences.getInt("tasksCompletedToday", 0),
                tasksCurrentlyInList = sharedPreferences.getInt("tasksCurrentlyInList", 0);

            Log.d(LOG_TAG, "(CheckFirstTimeRunTdy) checkAppRunFirstSinceInstallation: " + String.valueOf(checkAppRunFirstSinceInstallation()));

            if (!checkAppRunFirstSinceInstallation()) {
                    //TODO: 5/26/2017 Add possibility for "if there's nothing in list"
                    //TODO: 5/26/2017 This can be a privilege of growth > 50 people

                Log.d(LOG_TAG, "App run first time today & not first since installation.");

                if (tasksCompletedYesterday == 0 && tasksCurrentlyInList == 0) {
                    // show the alert dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Dehydrating Plant!")
                            .setMessage("Oh no! You did not complete much yesterday! Complete more tasks to keep your plant alive!")
                            .show();
                    if (sharedPreferences.getInt("tree_water", 0) <= 15) {
                        // if water <= 15%, just set to 0, prevents negative water value
                        editor.putInt("tree_water", 0);
                    } else {
                        // subtract 15% water from tree
                        editor.putInt("tree_water", sharedPreferences.getInt("tree_water", 0) - 15);
                    }
                }
                editor.putInt("tasksCompletedToday", 0);
                editor.putInt("growthAllowance", 14);
            }

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR),
                month = c.get(Calendar.MONTH) + 1,
                day = c.get(Calendar.DAY_OF_MONTH);
            editor.putString("dateToday", Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year));
            editor.apply();
        }
    }

    private boolean checkNotificationEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d(LOG_TAG, "isNotificationEnabled: " + sharedPreferences.getBoolean("isNotificationEnabled", true));
        return sharedPreferences.getBoolean("isNotificationEnabled", true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private static final String APP_VERSION = "0.0.9A";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.replayMorningReview:
                startActivity(new Intent(MainActivity.this, MorningReview.class));
                overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.viewVersion:
                Toast.makeText(this, "The current app version is " + APP_VERSION, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainLayout != null) updateUI(0);
        if (eventsLayout != null) updateUI(1); Log.d(LOG_TAG, "just updated Events");
        if (dashboardLayout != null) updateUI(2);


        if (checkNotificationEnabled()) {
            initAlarms();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "Restarting MainActivity");
        if (checkAppRunFirstTimeToday()) {
            Intent i = new Intent(MainActivity.this, LauncherLogo.class);
            startActivity(i);
        }
    }

    @Override
    public void onMainFragmentCreated(RelativeLayout layout) {
        initEHLVs(layout);
            mainLayout = layout;
            updateUI(0);

    }

    @Override
    public void onEventFragmentCreated(RelativeLayout layout) {
        initEventEHLV(layout);
        initToggleCalendarButton(layout);
        initCCV(layout);
        if (eventsLayout == null) {
            eventsLayout = layout;
            updateUI(1);
        }
    }

    private void initCCV(final RelativeLayout layout) {
        CompactCalendarView ccv = (CompactCalendarView) layout.findViewById(R.id.ccv_pickDate);
        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        ccv.setLocale(timeZone, Locale.US);
        ccv.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                TextView currentDayTextView = (TextView) layout.findViewById(R.id.tv_eventsTitle);
                DateTime newDate = new DateTime(dateClicked);
                int day = newDate.getDayOfMonth();
                int month = newDate.getMonthOfYear();
                int year = newDate.getYear();
                currentDayTextView.setText(String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year) + " Events");

                currentDay = day;
                currentMonth = month;
                currentYear = year;

                SQLiteDatabase db = dbHelper.openDB();
                updateEvents(db, currentDay, currentMonth, currentYear);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                DateTime newMonthDate = new DateTime(firstDayOfNewMonth);
                DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM").withLocale(Locale.US);
                String month = formatter.print(newMonthDate);
                globalCurrentEventTitle = month + "'s Events";
                getSupportActionBar().setTitle(globalCurrentEventTitle);
            }
        });
    }

    private void initToggleCalendarButton(final RelativeLayout layout) {
        final ImageButton imgbtn = (ImageButton) layout.findViewById(R.id.imgbtn_toggleCalendarVisibility);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompactCalendarView ccv = (CompactCalendarView) layout.findViewById(R.id.ccv_pickDate);
                if (ccv.isAnimating()) return;
                if (Integer.parseInt(String.valueOf(imgbtn.getTag())) == 1) {
                    imgbtn.setTag("2");
                    ccv.hideCalendarWithAnimation();
                    imgbtn.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.setElevation(4 * dp);
                        getSupportActionBar().setElevation(4 * dp);
                    }
                } else {
                    imgbtn.setTag("1");
                    ccv.showCalendarWithAnimation();
                    imgbtn.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tabLayout.setElevation(0);
                        getSupportActionBar().setElevation(0);
                    }
                }
            }
        });
    }

    @Override
    public void onDashboardFragmentCreated(RelativeLayout layout) {
        dashboardLayout = layout;
        updateUI(2);
    }

    public void handleFirstTimeRun() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (checkAppRunFirstSinceInstallation()) {

            Log.d(LOG_TAG, "(HandleFTR) checkAppRunFirstSinceInstallation: " + String.valueOf(checkAppRunFirstSinceInstallation()));

            Intent i = new Intent(this, InputUserNick.class);
            startActivity(i);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeSinceInstallation", false);
            editor.putBoolean("tutorialShownYet", false);
            editor.putInt("tree_growth", 0);
            editor.putInt("tree_water", 100);
            editor.putBoolean("tree_reached_shoot", false);
            editor.putBoolean("tree_reached_sapling", false);
            editor.putBoolean("tree_reached_tree", false);
            editor.putBoolean("isNotificationEnabled", true);
            editor.putString("notificationTiming", "09:00");
            editor.apply();

            Log.d(LOG_TAG, "(HandleFTR) checkAppRunFirstSinceInstallation: " + String.valueOf(checkAppRunFirstSinceInstallation()));
        }
    }

    private static final String SCV_TODOLIST = "SCV_TODOLIST";

    private void checkTutorial() {
        if (!checkTutorialShownYet()) {
            showTutorial();
        }
    }

    private boolean checkTutorialShownYet() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean("tutorialShownYet", false);
    }


    private void showTutorial() {
        CardView shortTermCardView = (CardView) mainLayout.findViewById(R.id.shortTermCardView);
        ImageButton searchByTagButton = (ImageButton) mainLayout.findViewById(R.id.imgbtn_searchByTag);
        CardView longTermCardView = (CardView) mainLayout.findViewById(R.id.longTermCardView);

        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.toolbar))
                .setContentText("Welcome to FullUse. This is the To Do List. Your tasks are split into Short Term and Long Term ones.")
                .setDismissText("GOT IT")
                .setTargetTouchable(false)
                .setMaskColour(maskColour)
                .withRectangleShape(true)
                .build()
        );
        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(this)
                .setTarget(shortTermCardView)
                .setContentText("Short Term Tasks denote what you want to do within a few days. Double-click to add a Short Term Task. Try it!")
                .setMaskColour(maskColour)
                .withRectangleShape(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(final MaterialShowcaseView materialShowcaseView) {
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                materialShowcaseView.hide();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                    }
                })
                .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(searchByTagButton)
                        .setDismissText("GOT IT")
                        .setContentText("If you have many tasks, you can search them by tag by clicking on this button.")
                        .setMaskColour(maskColour)
                        .setTargetTouchable(false)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
            new MaterialShowcaseView.Builder(this)
                    .setTarget(longTermCardView)
                    .setDismissText("GOT IT")
                    .setContentText("Long Term Tasks are tasks which span over a long time like projects or revision. The adding process is the same as Short Term Tasks.")
                    .setMaskColour(maskColour)
                    .setTargetTouchable(false)
                    .withRectangleShape(true)
                    .setListener(new IShowcaseListener() {
                        @Override
                        public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                        }

                        @Override
                        public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                            viewPager.setCurrentItem(1);
                            showTutorialPt2();
                        }
                    })
                    .build()
        );
        sequence.start();


    }

    private void showTutorialPt2() {
        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        CompactCalendarView ccv = (CompactCalendarView) eventsLayout.findViewById(R.id.ccv_pickDate);
        ImageButton toggle = (ImageButton) eventsLayout.findViewById(R.id.imgbtn_toggleCalendarVisibility);
        TextView todayDate = (TextView) eventsLayout.findViewById(R.id.tv_eventsTitle);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setContentText("You're now at the Events tab. You can swipe left/right to switch between tabs.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(tabLayout)
                        .setContentText("Alternatively, you can tap each of these tabs.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(ccv)
                        .setContentText("This is the calendar. Swipe left/right here to switch months or tap each number to switch days.")
                        .setDismissText("GOT IT")
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(toggle)
                        .setContentText("Expand or collapse the Calendar by clicking on this button.")
                        .setDismissText("GOT IT")
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(todayDate)
                        .setContentText("Your events are displayed beneath this heading. Double-click on anywhere in this tab to add an Event. Try it!")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(final MaterialShowcaseView materialShowcaseView) {
                                viewPager.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        materialShowcaseView.hide();
                                    }
                                }, 5000);
                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                viewPager.setCurrentItem(2);
                                showTutorialPt3();
                            }
                        })
                        .build()
        );
        sequence.start();
    }

    private void showTutorialPt3() {
        int maskColour = ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDarkShowcase);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);

        AspectRatioImageView ariv = (AspectRatioImageView) dashboardLayout.findViewById(R.id.aiv_userTree);
        TextView tv_userName = (TextView) dashboardLayout.findViewById(R.id.tv_username);
        ProgressBar pb_growth = (ProgressBar) dashboardLayout.findViewById(R.id.pb_userTreeGrowthBar);
        ProgressBar pb_water = (ProgressBar) dashboardLayout.findViewById(R.id.pb_userTreeWaterBar);
        CardView cv_statistics = (CardView) dashboardLayout.findViewById(R.id.cv_dashboardStats);
        LinearLayout numTasks = (LinearLayout) dashboardLayout.findViewById(R.id.ll_numTasks),
                     numDone = (LinearLayout) dashboardLayout.findViewById(R.id.ll_numDone),
                     numTrees = (LinearLayout) dashboardLayout.findViewById(R.id.ll_numTrees);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setContentText("You're now at the Dashboard. This contains your Tree and some statistics.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(ariv)
                        .setContentText("This is your Tree; in the Seed stage.")
                        .setDismissOnTouch(true)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                final ScrollView sv = (ScrollView) dashboardLayout.findViewById(R.id.sv_dashboard);
                                sv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sv.fullScroll(View.FOCUS_DOWN);
                                    }
                                });
                            }
                        })
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setDelay(1000)
                        .setContentText("Designed to help you train discipline, patience, as well as motivate you to do your tasks, Your Tree has 4 stages: Seed, Shoot, Sapling and a Full Tree.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(pb_growth)
                        .setContentText("The Tree has a growth attribute which determines the stage. Complete tasks to gain growth.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(pb_water)
                        .setContentText("The Tree also has a water attribute. Complete tasks to keep it hydrated or it will die!")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(cv_statistics)
                        .setContentText("These are some statistics about your FullUse usage.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape(true)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(numTasks)
                        .setContentText("This represents how many tasks are in your To Do List currently.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(numDone)
                        .setContentText("This represents how many tasks you have completed so far.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(numTrees)
                        .setContentText("This represents how many trees you have grown; when your Tree's growth reaches 100% you grow a new Tree.")
                        .setDismissText("GOT IT")
                        .setTargetTouchable(false)
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setContentText("Lastly - FullUse gives you morning reviews when you first launch it every day. You can restart it or open the Settings by clicking on the 3 dots on the right.")
                        .setDismissText("GOT IT")
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(findViewById(R.id.toolbar))
                        .setContentText("Congrats on completing the tutorial! Now you can start making FullUse of your life!")
                        .setDismissText("GOT IT")
                        .setMaskColour(maskColour)
                        .withRectangleShape()
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                viewPager.setCurrentItem(0, true);
                                completeTutorial();
                            }
                        })
                        .build()
        );
        sequence.start();
    }

    private void completeTutorial() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putBoolean("tutorialShownYet", true);
        editor.apply();
    }

    //Methods for initializing user interface
    public void initAlarms() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] pieces = sharedPreferences.getString("notificationTiming", "09:00").split(":");
        int hour = Integer.parseInt(pieces[0]);
        int minute = Integer.parseInt(pieces[1]);

        Log.d(LOG_TAG, "Setting alarm for " + sharedPreferences.getString("notificationTiming", "09:00"));

        Calendar taskAlarmCalendar = Calendar.getInstance(),
                currentCalendar = Calendar.getInstance();
        taskAlarmCalendar.setTimeInMillis(System.currentTimeMillis());
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        taskAlarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        taskAlarmCalendar.set(Calendar.MINUTE, minute);
        if (taskAlarmCalendar.before(currentCalendar)) {
            taskAlarmCalendar.add(Calendar.DATE, 1);
            Log.d("TASKNOTIFSVC", "Need to wait until tomorrow!");
        } else Log.d("TASKNOTIFSVC", "Wait until today " + String.valueOf(taskAlarmCalendar));


        Intent taskAlarmIntent = new Intent(MainActivity.this, TaskAlertReceiver.class),
               eventAlarmIntent = new Intent(MainActivity.this, EventAlertReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, TASK_NOTIFICATION_CODE, taskAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pi2 = PendingIntent.getBroadcast(MainActivity.this, EVENT_NOTIFICATION_CODE, eventAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Android M, API level 23 : escape doze mode
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    taskAlarmCalendar.getTimeInMillis(),
                    pi);
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 5000,
                    pi2);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Android Kitkat, API level 19

            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    taskAlarmCalendar.getTimeInMillis(),
                    pi);
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 5000,
                    pi2);
        } else {
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    taskAlarmCalendar.getTimeInMillis(),
                    pi);
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 5000,
                    pi2);
        }
    }

    public void initEHLVs(RelativeLayout layout) {
        shortTermTaskListView = (ExpandableHeightListView) layout.findViewById(R.id.ehlv_shortTermTasksList);
        shortTermTaskListView.setExpanded(true);
        longTermTaskListView = (ExpandableHeightListView) layout.findViewById(R.id.ehlv_longTermTaskList);
        longTermTaskListView.setExpanded(true);

    }


    public void initEventEHLV(RelativeLayout layout) {
        eventsListView = (ExpandableHeightListView) layout.findViewById(R.id.ehlv_userEvents);
        eventsListView.setExpanded(true);
    }

    //Methods for updating user interface
    public void updateUI(int position) {
        SQLiteDatabase db = dbHelper.openDB();
        Log.d("DB", String.valueOf(db));
        Log.d("DATE", "Current Dateset: [" + String.valueOf(currentYear) + " , " + String.valueOf(currentMonth) + " , " + String.valueOf(currentDay) + "]");

        checkForTreeSplashScreen();

        switch (position) {
            case 0:
                updateSTTList(db);
                updateLTTList(db);
                updateFreeTime(db);
                break;
            case 1:
                updateEvents(db, currentDay, currentMonth, currentYear);
                Log.d("UPDATEUI", "Events being updated");
                break;
            case 2:
                updateTree();
                updateDashboard();
                break;
            case -1:
                break;
        }

    }

    private void checkForTreeSplashScreen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int treeGrowth = sharedPreferences.getInt("tree_growth", -1);
        int treeWater = sharedPreferences.getInt("tree_water", -1);

        boolean hasReachedShootYet = sharedPreferences.getBoolean("tree_reached_shoot", false);
        boolean hasReachedSaplingYet = sharedPreferences.getBoolean("tree_reached_sapling", false);
        boolean hasReachedTreeYet = sharedPreferences.getBoolean("tree_reached_tree", false);

        if (treeGrowth != -1 && treeWater != -1) {
            if (treeGrowth > 24 && !hasReachedShootYet) {
                new AlertDialog.Builder(this)
                        .setTitle("Congratulations!")
                        .setMessage("Your seed has germinated and is now a shoot!")
                        .setPositiveButton("CHECK IT OUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUI(2);
                                viewPager.setCurrentItem(1, true);
                                viewPager.setCurrentItem(2, true);
                            }
                        }).show();
                editor.putBoolean("tree_reached_shoot", true);
                editor.apply();
            } else if (treeGrowth > 49 && !hasReachedSaplingYet) {
                new AlertDialog.Builder(this)
                        .setTitle("Congratulations!")
                        .setMessage("Your shoot has grown into a sapling!")
                        .setPositiveButton("CHECK IT OUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUI(2);
                                viewPager.setCurrentItem(1, true);
                                viewPager.setCurrentItem(2, true);
                            }
                        }).show();
                editor.putBoolean("tree_reached_sapling", true);
                editor.apply();
            } else if (treeGrowth > 74 && !hasReachedTreeYet) {
                new AlertDialog.Builder(this)
                        .setTitle("Congratulations!")
                        .setMessage("Your sapling has grown into a fine tree!")
                        .setPositiveButton("CHECK IT OUT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUI(2);
                                viewPager.setCurrentItem(1, true);
                                viewPager.setCurrentItem(2, true);
                            }
                        }).show();
                editor.putBoolean("tree_reached_tree", true);
                editor.apply();
            }
        }
    }

    public void updateFreeTime(SQLiteDatabase db) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH)+1,
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String[] columns = {
                DBHelper.EVENT_START_YR,
                DBHelper.EVENT_START_MTH,
                DBHelper.EVENT_START_DAY,
                DBHelper.EVENT_END_YR,
                DBHelper.EVENT_END_MTH,
                DBHelper.EVENT_END_DAY,
                DBHelper.EVENT_START_TIME,
                DBHelper.EVENT_END_TIME
        };

        Cursor cursor = db.query(
                DBHelper.TABLE_EVENTS,
                columns,
                null,
                null,
                null,
                null,
                null);
        ArrayList<Interval> arrayDifferences = new ArrayList<>();
        while (cursor.moveToNext()) {
            int indexStartDay = cursor.getColumnIndex(DBHelper.EVENT_START_DAY),
                indexStartMth = cursor.getColumnIndex(DBHelper.EVENT_START_MTH),
                indexStartYr = cursor.getColumnIndex(DBHelper.EVENT_START_YR),
                indexStartTime = cursor.getColumnIndex(DBHelper.EVENT_START_TIME);
            int indexEndDay = cursor.getColumnIndex(DBHelper.EVENT_END_DAY),
                indexEndMth = cursor.getColumnIndex(DBHelper.EVENT_END_MTH),
                indexEndYr = cursor.getColumnIndex(DBHelper.EVENT_END_YR),
                indexEndTime = cursor.getColumnIndex(DBHelper.EVENT_END_TIME);

            DateTime startDate = new DateTime(
                    Integer.parseInt(cursor.getString(indexStartYr)),
                    Integer.parseInt(cursor.getString(indexStartMth)),
                    Integer.parseInt(cursor.getString(indexStartDay)),
                    Integer.parseInt(cursor.getString(indexStartTime).substring(0,2)),
                    Integer.parseInt(cursor.getString(indexStartTime).substring(2,4)));
            DateTime endDate = new DateTime(
                    Integer.parseInt(cursor.getString(indexEndYr)),
                    Integer.parseInt(cursor.getString(indexEndMth)),
                    Integer.parseInt(cursor.getString(indexEndDay)),
                    Integer.parseInt(cursor.getString(indexEndTime).substring(0,2)),
                    Integer.parseInt(cursor.getString(indexEndTime).substring(2,4)));
            DateTime nowDate = new DateTime(
                    year,
                    month,
                    dayOfMonth,
                    0,
                    0);

            if (startDate.withTimeAtStartOfDay().isEqual(endDate.withTimeAtStartOfDay())
                    && startDate.withTimeAtStartOfDay().isEqual(nowDate)) {
                Interval interval = new Interval(startDate, endDate);
                arrayDifferences.add(interval);
            }
        }
        //eliminate double counting of overlapped timings
        for (int i = 1; i < arrayDifferences.size(); i++){
            Interval currentInterval = arrayDifferences.get(i),
                    prevInterval = arrayDifferences.get(i-1),
                    overlapInterval = prevInterval.overlap(currentInterval);
            if (overlapInterval != null) {
                DateTime overlapEndInstant = overlapInterval.getEnd();
                DateTime currentEndInstant = currentInterval.getEnd();
                Interval newInterval = new Interval(overlapEndInstant, currentEndInstant);
                arrayDifferences.set(i, newInterval);
            }
        }


        double finalFreeTime;
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            finalFreeTime = processFreeTime(arrayDifferences, true);
        } else finalFreeTime = processFreeTime(arrayDifferences, false);

        TextView freeTimeTextView = (TextView) mainLayout.findViewById(R.id.tv_freeTime);
        freeTimeTextView.setText(String.valueOf(finalFreeTime) + " Hours of Free Time");
    }

    private double processFreeTime(ArrayList<Interval> arrayDifferences, boolean isWeekend) {
        long initialMillis;
        if (isWeekend) initialMillis = 43200000L; //12 hours
        else initialMillis = 25200000L; // 7 hours

        for (int i = 0; i < arrayDifferences.size(); i++) {
            Interval currInterval = arrayDifferences.get(i);
            initialMillis -= currInterval.toDuration().getMillis();
        }

        Log.d("FREETIME", "initialMillis: " + String.valueOf(doubleRound(Long.valueOf(initialMillis).doubleValue() / 3600000, 1)));
        return doubleRound(Long.valueOf(initialMillis).doubleValue() / 3600000, 1);
    }

    public void updateEvents(SQLiteDatabase db, int currentDay, int currentMonth, int currentYear) {
        DateTime now = new DateTime();

        CompactCalendarView ccv = (CompactCalendarView) eventsLayout.findViewById(R.id.ccv_pickDate);
        ccv.removeAllEvents();
        ArrayList<EventData> arrayList = new ArrayList<>();
        String[] columns = {
                DBHelper.EVENT_NAME,
                DBHelper.EVENT_START_DAY,
                DBHelper.EVENT_START_MTH,
                DBHelper.EVENT_START_YR,
                DBHelper.EVENT_END_DAY,
                DBHelper.EVENT_END_MTH,
                DBHelper.EVENT_END_YR,
                DBHelper.EVENT_START_TIME,
                DBHelper.EVENT_END_TIME
        };
        Cursor cursor = db.query(DBHelper.TABLE_EVENTS, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String eventName = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME));
            int eventStartDay = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_DAY)),
                    eventStartMth = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_MTH)),
                    eventStartYr = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_START_YR));

            int eventEndDay = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_DAY)),
                    eventEndMth = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_MTH)),
                    eventEndYr = cursor.getInt(cursor.getColumnIndex(DBHelper.EVENT_END_YR));

            String eventStartTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START_TIME)),
                    eventEndTime = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END_TIME));

            int rawStartHour = Integer.parseInt(eventStartTime.substring(0,2)),
                    rawStartMin = Integer.parseInt(eventStartTime.substring(2,4)),
                    rawEndHour = Integer.parseInt(eventEndTime.substring(0,2)),
                    rawEndMin = Integer.parseInt(eventEndTime.substring(2,4));


            DateTime startDate = new DateTime(eventStartYr, eventStartMth, eventStartDay, rawStartHour, rawStartMin);
            DateTime endDate = new DateTime(eventEndYr, eventEndMth, eventEndDay, rawEndHour, rawEndMin);
            DateTime currentDate = new DateTime(currentYear, currentMonth, currentDay, 0, 0);
            processCCVEvents(startDate, endDate);

            Log.d("EVENTS", "Start: " + eventName + " [" + String.valueOf(eventStartDay) + ", " + String.valueOf(eventStartMth) + ", " + String.valueOf(eventStartYr) + ", " + eventStartTime + "]");
            Log.d("EVENTS", "End: [" + String.valueOf(eventEndDay) + ", " + String.valueOf(eventEndMth) + ", " + String.valueOf(eventEndYr) + ", " + eventEndTime + "]");

            if (isBetweenInclusive(startDate.withTimeAtStartOfDay(), endDate, currentDate)) {
                String eventStartDate = String.valueOf(eventStartDay) + "/" + String.valueOf(eventStartMth) + "/" + String.valueOf(eventStartYr);
                String eventEndDate = String.valueOf(eventEndDay) + "/" + String.valueOf(eventEndMth) + "/" + String.valueOf(eventEndYr);
                if (currentDate.isBefore(now)) {
                    arrayList.add(new EventData(eventName, eventStartTime, eventEndTime, eventStartDate, eventEndDate, true));
                } else {
                    arrayList.add(new EventData(eventName, eventStartTime, eventEndTime, eventStartDate, eventEndDate, false));
                }

            }
        }
        cursor.close();

        Collections.sort(arrayList, new Comparator<EventData>() {
            @Override
            public int compare(EventData LHS, EventData RHS) {
                return LHS.getEventStartTiming().compareTo(RHS.getEventStartTiming());
            }
        });

        Log.d("EVENTS", "size2: " + String.valueOf(arrayList.size()));

        EventDataAdapter adapter = new EventDataAdapter(MainActivity.this, 0, arrayList);
        eventsListView.setAdapter(adapter);

        eventHeaderTextView = (TextView) eventsLayout.findViewById(R.id.tv_eventsTitle);
        eventHeaderTextView.setText(
                String.valueOf(currentDay)
                + "/"
                + String.valueOf(currentMonth)
                + "/"
                + String.valueOf(currentYear)
                + " Events");
    }

    private boolean isBetweenInclusive(DateTime startDate, DateTime endDate, DateTime targetDate) {
        return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);
    }

    private void processCCVEvents(DateTime startDate, DateTime endDate) {
        CompactCalendarView ccv = (CompactCalendarView) eventsLayout.findViewById(R.id.ccv_pickDate);
        Log.d("CCV", "Event startDate:" + String.valueOf(startDate));
        Log.d("CCV", "Event endDate: " + String.valueOf(endDate));
        startDate = startDate.withTimeAtStartOfDay();
        endDate = endDate.withTimeAtStartOfDay();
        if (startDate.isEqual(endDate)) {
            // i.e. event spanning only one day
            ccv.addEvent(new Event(ContextCompat.getColor(MainActivity.this, R.color.ccvEventColor), startDate.getMillis(), null));
        } else {
            ccv.addEvent(new Event(ContextCompat.getColor(MainActivity.this, R.color.ccvEventColor), startDate.getMillis(), null));
            while (!startDate.isEqual(endDate)) {
                startDate = startDate.plusDays(1);
                ccv.addEvent(new Event(ContextCompat.getColor(MainActivity.this, R.color.ccvEventColor), startDate.getMillis(), null));
            }
        }
    }

    public void updateSTTList(SQLiteDatabase db) {
        ArrayList<ShortTermTaskData> arrayList = new ArrayList<>();
        int STTsInList = 0;

        String[] STTcolumns = new String[] {
                DBHelper.SHORT_TERM_TASK_ID,
                DBHelper.SHORT_TERM_TASK_NAME,
                DBHelper.SHORT_TERM_TASK_PRIORITY,
                DBHelper.SHORT_TERM_TASK_DUEDAY,
                DBHelper.SHORT_TERM_TASK_DUEMTH,
                DBHelper.SHORT_TERM_TASK_DUEYR,
                DBHelper.SHORT_TERM_TASK_TAGSTR
        };

        Cursor cursor = db.query(DBHelper.TABLE_SHORT_TERM_TASK, STTcolumns, null, null, null, null, null);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasksCurrentlyInList", STTsInList);
        editor.commit();

        while (cursor.moveToNext()) {
            int indexName = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_NAME)
                    , indexPriority = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_PRIORITY)
                    , indexDueDay = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEDAY)
                    , indexDueMth = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEMTH)
                    , indexDueYr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEYR)
                    , indexTagStr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_TAGSTR);

            String displayedName = cursor.getString(indexName);

            Log.d("SQLITE", String.valueOf(cursor.getString(indexName)));

            String displayedPriority = "";
            switch (cursor.getInt(indexPriority)) {
                case 1:
                    displayedPriority = "!!!";
                    break;
                case 2:
                    displayedPriority = "!!";
                    break;
                case 3:
                    displayedPriority = "!";
                    break;
                default: break;
            }

            String displayedDueDate = "Due " + String.valueOf(cursor.getInt(indexDueDay)) + "/" + String.valueOf(cursor.getInt(indexDueMth)) + "/" + String.valueOf(cursor.getInt(indexDueYr));
            String encodedTagStr = cursor.getString(indexTagStr);

            DateTime taskDueDate = new DateTime(
                    cursor.getInt(indexDueYr),
                    cursor.getInt(indexDueMth),
                    cursor.getInt(indexDueDay),
                    0,
                    0);
            DateTime nowDate = new DateTime().withTimeAtStartOfDay();

            if (taskDueDate.isBefore(nowDate)) {
                arrayList.add(new ShortTermTaskData(displayedName, displayedPriority, displayedDueDate, encodedTagStr, false, true));
            } else arrayList.add(new ShortTermTaskData(displayedName, displayedPriority, displayedDueDate, encodedTagStr, false, false));


            //Update the number of tasks in list, to display in the dashboard
            STTsInList++;

            //Sort the list of short term tasks according to priority
            Collections.sort(arrayList, new Comparator<ShortTermTaskData>() {
                @Override
                public int compare(ShortTermTaskData LHS, ShortTermTaskData RHS) {
                    return RHS.getTaskPriority().compareTo(LHS.getTaskPriority());
                }
            });
        }

        editor.putInt("tasksCurrentlyInList", STTsInList);
        editor.commit();

        //Setting the top priority tasks bold.
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(0).getTaskPriority().equals(arrayList.get(i).getTaskPriority())) {
                arrayList.get(i).setIsBold();
            }
        }

        ArrayAdapter<ShortTermTaskData> adapter = new ShortTermTaskDataAdapter(this, 0, arrayList);
        shortTermTaskListView.setAdapter(adapter);


        shortTermTaskTitle = (TextView) mainLayout.findViewById(R.id.tv_shortTermTaskTitle);
        shortTermTaskTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(LOG_TAG, "shortTermTaskTitle has been clicked");
                Log.d(LOG_TAG, "shortTermTaskTitle touch results: " + String.valueOf( MainActivity.this.gestureDetectorCompat.onTouchEvent(motionEvent)));
                return MainActivity.this.gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });
    }
    public void updateLTTList(SQLiteDatabase db) {
        Cursor cursor = db.query(
                DBHelper.TABLE_LONG_TERM_TASK,
                new String[] {
                        DBHelper.LONG_TERM_TASK_ID,
                        DBHelper.LONG_TERM_TASK_NAME,
                        DBHelper.LONG_TERM_TASK_PRIORITY,
                        DBHelper.LONG_TERM_TASK_ENDDAY,
                        DBHelper.LONG_TERM_TASK_ENDMTH,
                        DBHelper.LONG_TERM_TASK_ENDYR
                },
                null,
                null,
                null,
                null,
                null);

        ArrayList<LongTermTaskData> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int indexName = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_NAME),
                    indexPriority = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_PRIORITY),
                    indexEndDay = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDDAY),
                    indexEndMth = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDMTH),
                    indexEndYr = cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDYR);


            String displayedPriority = "";
            switch (cursor.getInt(indexPriority)) {
                case 1:
                    displayedPriority = "!!!";
                    break;
                case 2:
                    displayedPriority = "!!";
                    break;
                case 3:
                    displayedPriority = "!";
                    break;
                default: break;
            }

            String displayedEndDate = "Ends "
                    + String.valueOf(cursor.getInt(indexEndDay)) + "/"
                    + String.valueOf(cursor.getInt(indexEndMth)) + "/"
                    + String.valueOf(cursor.getInt(indexEndYr));

            Log.d("LTT", "lttName: " + cursor.getString(indexName));

            arrayList.add(new LongTermTaskData(cursor.getString(indexName), displayedPriority, displayedEndDate));
        }

        Collections.sort(arrayList, new Comparator<LongTermTaskData>() {
            @Override
            public int compare(LongTermTaskData LHS, LongTermTaskData RHS) {
                return LHS.getTaskPriority().compareTo(RHS.getTaskPriority());
            }
        });

        ArrayAdapter<LongTermTaskData> longTermTaskAdapter = new LongTermTaskDataAdapter(this, 0, arrayList);
        longTermTaskListView.setAdapter(longTermTaskAdapter);

        longTermTaskTitle = (TextView) mainLayout.findViewById(R.id.tv_longTermTaskTitle);
        longTermTaskTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(LOG_TAG, "longTermTaskTitle has been clicked");
                Log.d(LOG_TAG, "longTermTaskTitle touch results: " + String.valueOf( MainActivity.this.gestureDetectorCompat.onTouchEvent(motionEvent)));
                return MainActivity.this.gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });
    }
    public void updateTree() {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        TextView userNameTextView = (TextView) dashboardLayout.findViewById(R.id.tv_username);
        String username = data.getString("username", null);
        userNameTextView.setText(username + "'s Tree");

        //Importing data for the "Water" of the tree.
        TextView treeWaterTextView = (TextView) dashboardLayout.findViewById(R.id.tv_userTreeWaterPercentage);
        ProgressBar waterProgressBar = (ProgressBar) dashboardLayout.findViewById(R.id.pb_userTreeWaterBar);
        int water = data.getInt("tree_water", 0);
        TextView treeGrowthTextView = (TextView) dashboardLayout.findViewById(R.id.tv_userTreeGrowthPercentage);
        final ProgressBar growthProgressBar = (ProgressBar) dashboardLayout.findViewById(R.id.pb_userTreeGrowthBar);
        final int growth = data.getInt("tree_growth", 0);
        if (water <= 100 && water >= 0) {
            String waterPercentage = String.valueOf(water);
            treeWaterTextView.setText(waterPercentage + "%");
            waterProgressBar.setProgress(water);
        } else if (water < 1) {
            treeWaterTextView.setText("0%");
            waterProgressBar.setProgress(0);
        }

        SharedPreferences.Editor editor = data.edit();
        if (water < 1) {
            editor.putInt("tree_water", 0);
            editor.commit();
            new AlertDialog.Builder(this)
                    .setTitle("Oh no!")
                    .setMessage("Your plant died... Time to restart. Take better care of it next time!")
                    .setPositiveButton("OK", null)
                    .show();
            resetTree(growthProgressBar, waterProgressBar);
        } else if (water > 100) {
            editor.putInt("tree_water", 100);
            editor.commit();
        }

        //Importing data for the "Growth" of tree.

        if (growth < 100 && growth >= 0) {
            String growthPercentage = String.valueOf(growth);
            treeGrowthTextView.setText(growthPercentage + "%");
            growthProgressBar.setProgress(0);
            growthProgressBar.setProgress(growth);
            growthProgressBar.post(new Runnable() {
                @Override
                public void run() {
                    growthProgressBar.setProgress(growth);
                }
            });
            Log.d(LOG_TAG, "growthBar Progress: " + String.valueOf(growthProgressBar.getProgress()));
        } else if (growth < 0) {
            treeGrowthTextView.setText("0%");
            growthProgressBar.setProgress(0);
        } else if (growth >= 100) {
            treeGrowthTextView.setText("100%");
            growthProgressBar.setProgress(100);
            resetTree(growthProgressBar, waterProgressBar);
        }

        AspectRatioImageView tree = (AspectRatioImageView) dashboardLayout.findViewById(R.id.aiv_userTree);

        if (growth < 25) {
            // growth 0 ~ 24: seed
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_seed_0);
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_seed_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            userNameTextView.setText(username + "'s Seed");
        } else if (growth < 50) {
            // growth 25 ~ 49: shoot
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_shoot_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_shoot_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_shoot_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_shoot_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_shoot_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            userNameTextView.setText(username + "'s Shoot");
        } else if (growth < 75) {
            // growth 50 ~ 74: sapling
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_sapling_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_sapling_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sapling_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sapling_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_sapling_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            userNameTextView.setText(username + "'s Sapling");
        } else if (growth <= 100){
            // growth 75 ~ 100: full tree
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_full_tree_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_full_tree_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_full_tree_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_full_tree_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.ic_full_tree_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            userNameTextView.setText(username + "'s Tree");
        }
    }
    public void updateDashboard() {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Importing data for the dashboard stats: Number of task in list currently
        TextView taskInListTextView = (TextView) dashboardLayout.findViewById(R.id.tv_tasksInList);
        TextView tasksInListTextView2 = (TextView) dashboardLayout.findViewById(R.id.tv_tasksInListText);
        String tasksCurrentlyInList = String.valueOf(data.getInt("tasksCurrentlyInList", -1));
        taskInListTextView.setText(tasksCurrentlyInList);
        // Correcting grammatical errors in number of tasks in list currently.
        if (data.getInt("tasksCurrentlyInList", -1) == 1) { tasksInListTextView2.setText("TASK"); }
        else { tasksInListTextView2.setText("TASKS"); }

        //Importing data for dashboard stats: Number of completed tasks in total
        TextView completedTasksTextView = (TextView) dashboardLayout.findViewById(R.id.tv_tasksCompleted);
        String completedTasks = String.valueOf(data.getInt("completedTasks", 0));
        completedTasksTextView.setText(completedTasks);

        //Importing data for dashboard stats: Number of trees grown.
        TextView numberOfTreesTextView = (TextView) dashboardLayout.findViewById(R.id.tv_treesCount);
        TextView numberOfTreesTextView2 = (TextView) dashboardLayout.findViewById(R.id.tv_treesCountText);
        String numOfTrees = String.valueOf(data.getInt("numOfTrees", 0));
        numberOfTreesTextView.setText(numOfTrees);
        if (data.getInt("numOfTrees", 0) == 1) {numberOfTreesTextView2.setText("TREE"); }
        else {numberOfTreesTextView2.setText("TREES"); }
    }

    //Methods for completing tasks
    public void completeShortTermTask(View view) {
        dbHelper.openDB();
        ListView listView = (ListView) view.getParent().getParent().getParent();
        ShortTermTaskDataAdapter adapter = (ShortTermTaskDataAdapter) listView.getAdapter();

        RelativeLayout relativeLayout = (RelativeLayout) view.getParent().getParent();
        TextView tv_taskName = (TextView) relativeLayout.findViewById(R.id.tv_taskName);
        String apparentName = String.valueOf(tv_taskName.getText());

        String realName = adapter.getSTTDWithTruncatedName(apparentName);
        dbHelper.deleteFromSTT(realName);
        dbHelper.closeDB();

        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = data.edit();
        editor.putInt("completedTasks", data.getInt("completedTasks", 0)+1);
        editor.commit();

        if (data.getInt("tree_water", 0) > 100) editor.putInt("tree_water", 100); editor.commit();

        if (data.getInt("tree_growth", 0) <= 99) {
            if (data.getInt("growthAllowance", 14) >= 2) {
                editor.putInt("tree_growth", data.getInt("tree_growth", 0)+2);
                editor.putInt("growthAllowance", data.getInt("growthAllowance", 14)-2);
            } else {
                int prevGrowthAllowance = data.getInt("growthAllowance", 0);
                editor.putInt("tree_growth", data.getInt("tree_growth", 0) + data.getInt("growthAllowance", 0));
                editor.putInt("growthAllowance", data.getInt("growthAllowance", 0) - prevGrowthAllowance);
            }
            editor.putInt("tree_water", data.getInt("tree_water", 0)+5);
            editor.putInt("tasksCompletedToday", data.getInt("tasksCompletedToday", 0)+1);
            editor.commit();
        }
        Log.d(LOG_TAG, "treeGrowth: " + String.valueOf(data.getInt("tree_growth", 0)));
        Log.d(LOG_TAG, "treeWater: " + String.valueOf(data.getInt("tree_water", 0)));
        Log.d("ADDSTT", String.valueOf(data.getInt("tasksCompletedToday", 0)));
        Log.d(LOG_TAG, "growthAllowance: " + String.valueOf(data.getInt("growthAllowance", -1)));

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI(0);
            }
        }, 1500);
    }
    public void completeLongTermTask(View view) {
        dbHelper.openDB();
        ListView parentListView = (ListView) view.getParent().getParent().getParent();
        LongTermTaskDataAdapter adapter = (LongTermTaskDataAdapter) parentListView.getAdapter();

        RelativeLayout parentRelativeLayout = (RelativeLayout) view.getParent().getParent();
        TextView taskNameTextView = (TextView) parentRelativeLayout.findViewById(R.id.tv_taskName);
        String apparentName = String.valueOf(taskNameTextView.getText());

        String realName = adapter.getLTTDWithTruncatedName(apparentName);
        dbHelper.deleteFromLTT(realName);
        dbHelper.closeDB();

        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = data.edit();

        if (data.getInt("tree_water", 0) > 100) editor.putInt("tree_water", 100); editor.commit();

        if (data.getInt("tree_growth", 0) <= 99) {
            if (data.getInt("growthAllowance", 14) >= 10) {
                editor.putInt("tree_growth", data.getInt("tree_growth", 0)+10);
                editor.putInt("growthAllowance", data.getInt("growthAllowance", 14)-10);
            } else {
                int prevGrowthAllowance = data.getInt("growthAllowance", 0);
                editor.putInt("tree_growth", data.getInt("tree_growth", 0) + data.getInt("growthAllowance", 0));
                editor.putInt("growthAllowance", data.getInt("growthAllowance", 0) - prevGrowthAllowance);
            }
            editor.putInt("tree_water", data.getInt("tree_water", 0)+20);
            editor.putInt("tasksCompletedToday", data.getInt("tasksCompletedToday", 0)+1);
            editor.putInt("completedTasks", data.getInt("completedTasks", 0)+1);
            editor.commit();
        }

        Log.d(LOG_TAG, "growthAllowance: " + String.valueOf(data.getInt("growthAllowance", -1)));

        updateUI(0);
    }
    public void deleteEvent(View view) {
        dbHelper.openDB();
        ListView parentListView = (ListView) view.getParent().getParent().getParent().getParent().getParent();
        EventDataAdapter adapter = (EventDataAdapter) parentListView.getAdapter();

        RelativeLayout parentRelativeLayout = (RelativeLayout) view.getParent().getParent().getParent().getParent();
        TextView taskTitle = (TextView) parentRelativeLayout.findViewById(R.id.tv_eventName);
        String apparentName = String.valueOf(taskTitle.getText());
        String realName = adapter.getEventNameWithTruncatedName(apparentName);
        dbHelper.deleteEvent(realName);
        dbHelper.closeDB();
        updateUI(1);
        updateUI(0);
    }

    //Methods for editing tasks
    public void editShortTermTask(View view) {
        String displayedName = String.valueOf(((TextView) view).getText());
        ListView parent = (ListView) view.getParent().getParent();
        ShortTermTaskDataAdapter adapter = (ShortTermTaskDataAdapter) parent.getAdapter();
        String realName = adapter.getSTTDWithTruncatedName(displayedName);

        Intent i = new Intent(MainActivity.this, EditShortTermTask.class);
        i.putExtra("taskName", realName);
        startActivity(i);
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }
    public void editLongTermTask(View view) {
        String displayedName = String.valueOf(((TextView) view).getText());
        ListView parent = (ListView) view.getParent().getParent();
        LongTermTaskDataAdapter adapter = (LongTermTaskDataAdapter) parent.getAdapter();
        String realName = adapter.getLTTDWithTruncatedName(displayedName);
        Log.d("EDITLTT", "displayedName: " + displayedName);
        Log.d("EDITLTT", "realName: " + realName);

        Intent i = new Intent(MainActivity.this, EditLongTermTask.class);
        i.putExtra("taskName", realName);
        startActivity(i);
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }
    public void editEvent(View view) {
        RelativeLayout parentRelativeLayout = (RelativeLayout) view.getParent().getParent();
        String displayedName = String.valueOf(((TextView) parentRelativeLayout.findViewById(R.id.tv_eventName)).getText());
        ListView parentListView = (ListView) parentRelativeLayout.getParent().getParent().getParent();
        EventDataAdapter adapter = (EventDataAdapter) parentListView.getAdapter();
        String realName = adapter.getEventNameWithTruncatedName(displayedName);

        Intent i = new Intent(MainActivity.this, EditEvent.class);
        i.putExtra("eventName", realName);
        startActivity(i);
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

    public void resetTree(ProgressBar growthBar, ProgressBar waterBar) {
        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage("You have grown a new tree! Time to move on to a new seed...")
                .show();
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (data.getInt("numOfTrees", -1) == -1) {
            // does not exist
            SharedPreferences.Editor editor = data.edit();
            editor.putInt("numOfTrees", 1);
            editor.putInt("tree_growth", 0);
            editor.putInt("tree_water", 100);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = data.edit();
            editor.putInt("numOfTrees", data.getInt("numOfTrees", 0)+1);
            editor.putInt("tree_growth", 0);
            editor.putInt("tree_water", 100);
            editor.putBoolean("tree_reached_shoot", false);
            editor.putBoolean("tree_reached_sapling", false);
            editor.putBoolean("tree_reached_tree", false);
            editor.commit();
        }
        growthBar.setProgress(0);
        waterBar.setProgress(100);
    }

    public static double doubleRound(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return (bd.doubleValue() > 0) ? bd.doubleValue() : 0.0;
    }

    public void startSearchActivity(View view) {
        Intent i = new Intent(MainActivity.this, SearchByTag.class);
        startActivity(i);
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onLongPress(e);
            if (isCoordsInsideView(e.getRawX(), e.getRawY(), shortTermTaskTitle)) {
                Intent i = new Intent(MainActivity.this, AddShortTermTask.class);
                startActivity(i);
            } else if (isCoordsInsideView(e.getRawX(), e.getRawY(), longTermTaskTitle)) {
                Intent i = new Intent(MainActivity.this, AddLongTermTask.class);
                startActivity(i);
            } else if (viewPager.getCurrentItem() == 1 && isCoordsInsideView(e.getRawX(), e.getRawY(), eventHeaderTextView)) {
                Intent i = new Intent(MainActivity.this, AddNormalEvent.class);
                startActivity(i);
            }
            return true;
        }


//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//
//            if (isCoordsInsideView(e.getRawX(), e.getRawY(), shortTermTaskTitle)) {
//                Intent i = new Intent(MainActivity.this, AddShortTermTask.class);
//                startActivity(i);
//            } else if (isCoordsInsideView(e.getRawX(), e.getRawY(), longTermTaskTitle)) {
//                Intent i = new Intent(MainActivity.this, AddLongTermTask.class);
//                startActivity(i);
//            } else if (viewPager.getCurrentItem() == 1) {
//                Intent i = new Intent(MainActivity.this, AddNormalEvent.class);
//                startActivity(i);
//            }
//
//            return true;
//        }
    }

    private boolean isCoordsInsideView(float rawX, float rawY, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0], viewY = location[1];
        Log.d(LOG_TAG, "view Location: " + Arrays.toString(location) + ", touch Location: [" + String.valueOf(rawX) + ", " + String.valueOf(rawY) + "]");
        return (rawX > viewX && rawX < (viewX + view.getWidth()))
                && (rawY > viewY && rawY < (viewY + view.getHeight()));
    }
}
