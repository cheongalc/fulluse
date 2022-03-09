package com.fulluse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Handler;



import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;


import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MorningReview extends AppCompatActivity implements MorningReviewFragmentInterface {

    private VerticalViewPager verticalViewPager;
    private PagerAdapter pagerAdapter;

    private RelativeLayout introLayout, sttLayout, lttLayout, eventsLayout, treeAnalysisLayout;


    private static final int NUM_PAGES = 6;
    private static final String LOG_TAG = "MORNINGREVIEW";

    private SharedPreferences sharedPreferences;

    private DBHelper dbHelper;

    private Handler handler = null;

    Calendar c = Calendar.getInstance();
    int currentYear = c.get(Calendar.YEAR),
        currentMonth = c.get(Calendar.MONTH) + 1,
        currentDay = c.get(Calendar.DAY_OF_MONTH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morning_review);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dbHelper = new DBHelper(this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        verticalViewPager = (VerticalViewPager) findViewById(R.id.vvp_morningReview);
        pagerAdapter = new MorningReviewPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        verticalViewPager.setAdapter(pagerAdapter);

        handler = new Handler();
        int smoothScrollDelay = 3500;
        for (int i = 1; i < NUM_PAGES; i++) {
            final int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    verticalViewPager.setCurrentItem(finalI, true);
                }
            }, i*smoothScrollDelay);
        }




//        final Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        verticalViewPager.setCurrentItem(verticalViewPager.getCurrentItem()+1, true);
//                    }
//                });
//                switch (verticalViewPager.getCurrentItem()) {
//                    case 2:
//                        staggerSTTAnimations();
//                        break;
//                    case NUM_PAGES-1:
//                        timer.cancel();
//                        timer.purge();
//                        break;
//                }
//            }
//        }, 5000, 5000);


//        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 1:
//                        break;
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

//    private void staggerSTTAnimations() {
//        if (STTanimPlayedAlready) return;
//        final ExpandableHeightListView morningReviewEHLV = (ExpandableHeightListView) sttLayout.findViewById(R.id.ehlv_morningReviewSTTs);
//        final ArrayList<ShortTermTaskData> tempArrayList = new ArrayList<>();
//        ShortTermTaskDataAdapter adapter = new ShortTermTaskDataAdapter(MorningReview.this, 0, tempArrayList);
//        morningReviewEHLV.setAdapter(adapter);
//        for (int i = 0; i < STTarrayList.size(); i++) {
//            ShortTermTaskDataAdapter newAdapter = (ShortTermTaskDataAdapter) morningReviewEHLV.getAdapter();
//            tempArrayList.add(STTarrayList.get(i));
//            newAdapter.setTaskData(tempArrayList);
//
//        }
//    }


    private void initailizeSTTContent() {
        DBHelper dbHelper = new DBHelper(MorningReview.this);
        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = {
                DBHelper.SHORT_TERM_TASK_NAME,
                DBHelper.SHORT_TERM_TASK_PRIORITY,
                DBHelper.SHORT_TERM_TASK_DUEDAY,
                DBHelper.SHORT_TERM_TASK_DUEMTH,
                DBHelper.SHORT_TERM_TASK_DUEYR,
                DBHelper.SHORT_TERM_TASK_TAGSTR
        };
        Cursor cursor = db.query(
                DBHelper.TABLE_SHORT_TERM_TASK,
                columns,
                null,
                null,
                null,
                null,
                null);
        ArrayList<ShortTermTaskData> arrayList = new ArrayList<>();
        int tasksInList = 0;
        while (cursor.moveToNext()) {
            int indexName = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_NAME),
                    indexPriority = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_PRIORITY),
                    indexDueDay = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEDAY),
                    indexDueMth = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEMTH),
                    indexDueYr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEYR),
                    indexTagStr = cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_TAGSTR);

            String taskName = cursor.getString(indexName);
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
            String displayedDueDate = "Due "
                    + String.valueOf(cursor.getInt(indexDueMth))
                    + "/"
                    + String.valueOf(cursor.getInt(indexDueDay))
                    + "/"
                    + String.valueOf(cursor.getInt(indexDueYr));
            DateTime taskDueDate = new DateTime(
                    cursor.getInt(indexDueYr),
                    cursor.getInt(indexDueMth),
                    cursor.getInt(indexDueDay),
                    0,
                    0);
            DateTime nowDate = new DateTime().withTimeAtStartOfDay();
            String encodedTagStr = cursor.getString(indexTagStr);
            if (taskDueDate.isBefore(nowDate)) {
                arrayList.add(new ShortTermTaskData(taskName, displayedPriority, displayedDueDate, encodedTagStr, false, true));
            } else arrayList.add(new ShortTermTaskData(taskName, displayedPriority, displayedDueDate, encodedTagStr, false, false));
            tasksInList++;
        }
        Collections.sort(arrayList, new Comparator<ShortTermTaskData>() {
            @Override
            public int compare(ShortTermTaskData LHS, ShortTermTaskData RHS) {
                return RHS.getTaskPriority().compareTo(LHS.getTaskPriority());
            }
        });
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(0).getTaskPriority().equals(arrayList.get(i).getTaskPriority())) {
                arrayList.get(i).setIsBold();
            }
        }
        ArrayList<ShortTermTaskData> finalArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            finalArrayList.add(arrayList.get(i));
            if (i > 2) break;
        }
        ExpandableHeightListView morningReviewSTTEHLV = (ExpandableHeightListView) sttLayout.findViewById(R.id.ehlv_morningReviewSTTs);
        ShortTermTaskDataAdapter adapter = new ShortTermTaskDataAdapter(MorningReview.this, 0, finalArrayList);
        morningReviewSTTEHLV.setExpanded(true);
        morningReviewSTTEHLV.setAdapter(adapter);

        TextView numSTTsTextView = (TextView) sttLayout.findViewById(R.id.tv_morningReviewSTTsTitle);
        if (tasksInList == 0) {
            numSTTsTextView.setText("You have 0 short term tasks. Plan some!");
        } else if (tasksInList == 1) {
            numSTTsTextView.setText("You have 1 short term task.");
        } else numSTTsTextView.setText("You have " + String.valueOf(tasksInList) + " short term tasks.");
    }

    private void initializeIntro() {
        TextView dayNumber = (TextView) introLayout.findViewById(R.id.tv_dayNum);
        int dayNumberToDisplay = sharedPreferences.getInt("dayNumber", 1);
        dayNumber.setText("DAY #" + String.valueOf(dayNumberToDisplay));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


        TextView detailedDate = (TextView) introLayout.findViewById(R.id.tv_detailedDate);
        String detailedDateToDisplay = "";
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                detailedDateToDisplay += "Monday, ";
                break;
            case Calendar.TUESDAY:
                detailedDateToDisplay += "Tuesday, ";
                break;
            case Calendar.WEDNESDAY:
                detailedDateToDisplay += "Wednesday, ";
                break;
            case Calendar.THURSDAY:
                detailedDateToDisplay += "Thursday, ";
                break;
            case Calendar.FRIDAY:
                detailedDateToDisplay += "Friday, ";
                break;
            case Calendar.SATURDAY:
                detailedDateToDisplay += "Saturday, ";
                break;
            case Calendar.SUNDAY:
                detailedDateToDisplay += "Sunday, ";
                break;
            default:
                break;
        }
        detailedDateToDisplay += String.valueOf(day) + " ";
        switch (month) {
            case Calendar.JANUARY:
                detailedDateToDisplay += "January ";
                break;
            case Calendar.FEBRUARY:
                detailedDateToDisplay += "February ";
                break;
            case Calendar.MARCH:
                detailedDateToDisplay += "March ";
                break;
            case Calendar.APRIL:
                detailedDateToDisplay += "April ";
                break;
            case Calendar.MAY:
                detailedDateToDisplay += "May ";
                break;
            case Calendar.JUNE:
                detailedDateToDisplay += "June ";
                break;
            case Calendar.JULY:
                detailedDateToDisplay += "July ";
                break;
            case Calendar.AUGUST:
                detailedDateToDisplay += "August ";
                break;
            case Calendar.SEPTEMBER:
                detailedDateToDisplay += "September ";
                break;
            case Calendar.OCTOBER:
                detailedDateToDisplay += "October ";
                break;
            case Calendar.NOVEMBER:
                detailedDateToDisplay += "November ";
                break;
            case Calendar.DECEMBER:
                detailedDateToDisplay += "December ";
                break;
            default:
                break;
        }
        detailedDateToDisplay += String.valueOf(year);
        detailedDate.setText(detailedDateToDisplay);
    }

    public void finishReview(View view) {
        Intent i = new Intent(MorningReview.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onIntroFragmentCreated(RelativeLayout layout) {
        Log.d("MORNINGREVIEW", "Intro Fragment Created");
        Log.d("MORNINGREVIEW", String.valueOf(layout));
        introLayout = layout;
        initializeIntro();
    }

    @Override
    public void onSTTFragmentCreated(RelativeLayout layout) {
        sttLayout = layout;
        initailizeSTTContent();
    }

    @Override
    public void onLTTFragmentCreated(RelativeLayout layout) {
        lttLayout = layout;
        initializeLTTcontent();
    }

    @Override
    public void onEventsFragmentCreated(RelativeLayout layout) {
        eventsLayout = layout;
        initializeEvents();
    }

    private boolean isBetweenInclusive(DateTime startDate, DateTime endDate, DateTime targetDate) {
        return !targetDate.isBefore(startDate) && !targetDate.isAfter(endDate);
    }

    private void initializeEvents() {
        DateTime now = new DateTime();
        ArrayList<EventData> arrayList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(MorningReview.this);
        SQLiteDatabase db = dbHelper.openDB();
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
            if (isBetweenInclusive(startDate.withTimeAtStartOfDay(), endDate, currentDate)) {
                String eventStartDate = String.valueOf(eventStartDay) + "/" + String.valueOf(eventStartMth) + "/" + String.valueOf(eventStartYr);
                String eventEndDate = String.valueOf(eventEndDay) + "/" + String.valueOf(eventEndMth) + "/" + String.valueOf(eventEndYr);
                arrayList.add(new EventData(eventName, eventStartTime, eventEndTime, eventStartDate, eventEndDate, true));
            }
        }
        TextView tv_eventCount = (TextView) eventsLayout.findViewById(R.id.tv_morningReviewEventsTitle);
        Log.d(LOG_TAG, "number of events: " + String.valueOf(arrayList.size()));
        if (arrayList.size() == 0) {
            tv_eventCount.setText("You have 0 events today. Plan some!");
        } else if (arrayList.size() == 1){
            tv_eventCount.setText("You have 1 event today.");
        } else {
            tv_eventCount.setText("You have " + String.valueOf(arrayList.size()) + " events today.");
        }

        cursor.close();
        Collections.sort(arrayList, new Comparator<EventData>() {
            @Override
            public int compare(EventData LHS, EventData RHS) {
                return LHS.getEventStartTiming().compareTo(RHS.getEventStartTiming());
            }
        });
        ImmutableEventDataAdapter adapter = new ImmutableEventDataAdapter(MorningReview.this, 0, arrayList);
        ExpandableHeightListView ehlv_events = (ExpandableHeightListView) eventsLayout.findViewById(R.id.ehlv_morningReviewEvents);
        ehlv_events.setAdapter(adapter);
        ehlv_events.setExpanded(true);
    }

    @Override
    public void onTreeAnalysisFragmentCreated(RelativeLayout layout) {
        treeAnalysisLayout = layout;
        initializeTreeContent();
    }

    private void initializeTreeContent() {
        TextView treeGrowthTextView = (TextView) treeAnalysisLayout.findViewById(R.id.tv_userTreeGrowthPercentage);
        ProgressBar treeGrowthProgressBar = (ProgressBar) treeAnalysisLayout.findViewById(R.id.pb_userTreeGrowthBar);
        int growth = sharedPreferences.getInt("tree_growth", 0);
        treeGrowthTextView.setText(String.valueOf(growth) + "%");
        treeGrowthProgressBar.setProgress(growth);

        TextView treeWaterTextView = (TextView) treeAnalysisLayout.findViewById(R.id.tv_userTreeWaterPercentage);
        ProgressBar treeWaterProgressBar = (ProgressBar) treeAnalysisLayout.findViewById(R.id.pb_userTreeWaterBar);
        int water = sharedPreferences.getInt("tree_water", 0);
        if (water > 100) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("tree_water", 100);
            editor.commit();
        }
        water = 100;
        treeWaterTextView.setText(String.valueOf(water) + "%");
        treeWaterProgressBar.setProgress(water);


        AspectRatioImageView tree = (AspectRatioImageView) treeAnalysisLayout.findViewById(R.id.ariv_userTree);
        TextView analysisTitle = (TextView) treeAnalysisLayout.findViewById(R.id.tv_morningReviewTreeAnalysisTitle);

        if (growth < 25) {
            // growth 0 ~ 24: seed
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_seed_0);
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_seed_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            analysisTitle.setText("Plant Analysis: Seed Stage");
        } else if (growth < 50) {
            // growth 25 ~ 49: shoot
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_shoot_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_shoot_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_shoot_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_shoot_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_shoot_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            analysisTitle.setText("Plant Analysis: Shoot Stage");
        } else if (growth < 75) {
            // growth 50 ~ 74: sapling
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_sapling_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_sapling_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_sapling_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_sapling_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_sapling_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            analysisTitle.setText("Plant Analysis: Sapling Stage");
        } else if (growth <= 100){
            // growth 75 ~ 100: full tree
            if (water < 10) {
                tree.setImageResource(R.drawable.ic_full_tree_0);
            } else if (water <= 25) {
                tree.setImageResource(R.drawable.ic_full_tree_25);
            } else if (water <= 50) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_full_tree_anim_50);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else if (water <= 75) {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_full_tree_anim_75);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            } else {
                AnimatedVectorDrawableCompat drawable = AnimatedVectorDrawableCompat.create(MorningReview.this, R.drawable.ic_full_tree_anim_100);
                tree.setImageDrawable(drawable);
                if (drawable != null) drawable.start();
            }

            analysisTitle.setText("Plant Analysis: Full Tree");
        }
    }

    private void initializeLTTcontent() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.openDB();
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
        int tasksInList = 0;
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
                    + String.valueOf(cursor.getInt(indexEndMth)) + "/"
                    + String.valueOf(cursor.getInt(indexEndDay)) + "/"
                    + String.valueOf(cursor.getInt(indexEndYr));

            arrayList.add(new LongTermTaskData(cursor.getString(indexName), displayedPriority, displayedEndDate));
            tasksInList++;
        }
        cursor.close();
        Collections.sort(arrayList, new Comparator<LongTermTaskData>() {
            @Override
            public int compare(LongTermTaskData LHS, LongTermTaskData RHS) {
                return RHS.getTaskPriority().compareTo(LHS.getTaskPriority());
            }
        });
        ArrayList<LongTermTaskData> finalArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            finalArrayList.add(arrayList.get(i));
            if (i > 2) break;
        }

        ExpandableHeightListView morningReviewLTTsEHLV = (ExpandableHeightListView) lttLayout.findViewById(R.id.ehlv_morningReviewLTTs);
        ArrayAdapter<LongTermTaskData> longTermTaskAdapter = new LongTermTaskDataAdapter(this, 0, finalArrayList);
        morningReviewLTTsEHLV.setAdapter(longTermTaskAdapter);
        morningReviewLTTsEHLV.setExpanded(true);

        TextView morningReviewLTTsTextView = (TextView) lttLayout.findViewById(R.id.tv_morningReviewLTTsTitle);
        if (tasksInList == 0) {
            morningReviewLTTsTextView.setText("You have 0 long term tasks. Plan ahead!");
        } else if (tasksInList == 1) {
            morningReviewLTTsTextView.setText("You have 1 long term task.");
        } else {
            morningReviewLTTsTextView.setText("You have " + String.valueOf(tasksInList) + " long term tasks.");
        }
    }

    public void completeShortTermTask(View view) {
        ListView listView = (ListView) view.getParent().getParent().getParent();
        ShortTermTaskDataAdapter adapter = (ShortTermTaskDataAdapter) listView.getAdapter();

        RelativeLayout relativeLayout = (RelativeLayout) view.getParent().getParent()   ;
        TextView tv_taskName = (TextView) relativeLayout.findViewById(R.id.tv_taskName);
        String apparentName = String.valueOf(tv_taskName.getText());

        String realName = adapter.getSTTDWithTruncatedName(apparentName);


        dbHelper.openDB();
        dbHelper.deleteFromSTT(realName);
        dbHelper.closeDB();

        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = data.edit();
        editor.putInt("completedTasks", data.getInt("completedTasks", 0)+1);
        editor.commit();


        if (data.getInt("tree_growth", 0) <= 99 && data.getInt("tree_water", 0) <= 100) {
            editor.putInt("tree_growth", data.getInt("tree_growth", 0)+2);
            editor.putInt("tree_water", data.getInt("tree_water", 0)+5);
            editor.putInt("tasksCompletedToday", data.getInt("tasksCompletedToday", 0)+1);
            editor.commit();
        }
        Log.d("ADDSTT", String.valueOf(data.getInt("tasksCompletedToday", 0)));

        initailizeSTTContent();
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
        dbHelper.close();

        initializeLTTcontent();
    }

    public void editShortTermTask(View view) {}
    public void editLongTermTask(View view) {}

    private class MorningReviewPagerAdapter extends FragmentStatePagerAdapter {

        int numberOfFragments;

        public MorningReviewPagerAdapter(FragmentManager fm, int numberOfFragments) {
            super(fm);
            this.numberOfFragments = numberOfFragments;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MorningReviewIntroFragment();
                case 1:
                    return new MorningReviewSTTFragment();
                case 2:
                    return new MorningReviewLTTFragment();
                case 3:
                    return new MorningReviewEventsFragment();
                case 4:
                    return new MorningReviewTreeAnalysisFragment();
                case 5:
                    return new MorningReviewClosureFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return this.numberOfFragments;
        }
    }
    public static class MorningReviewIntroFragment extends Fragment {

        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_intro, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                MorningReviewFragmentInterface mrfi = (MorningReviewFragmentInterface) getActivity();
                mrfi.onIntroFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement MorningReviewFragmentInterface");
            }

        }
    }
    public static class MorningReviewSTTFragment extends Fragment {
        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_stts, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                MorningReviewFragmentInterface mrfi = (MorningReviewFragmentInterface) getActivity();
                mrfi.onSTTFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement MorningReviewFragmentInterface");
            }

        }
    }
    public static class MorningReviewLTTFragment extends Fragment {
        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_ltts, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                MorningReviewFragmentInterface mrfi = (MorningReviewFragmentInterface) getActivity();
                mrfi.onLTTFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement MorningReviewFragmentInterface");
            }

        }
    }
    public static class MorningReviewEventsFragment extends Fragment {
        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_events, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                MorningReviewFragmentInterface mrfi = (MorningReviewFragmentInterface) getActivity();
                mrfi.onEventsFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement MorningReviewFragmentInterface");
            }
        }
    }
    public static class MorningReviewTreeAnalysisFragment extends Fragment {
        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_treeanalysis, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                MorningReviewFragmentInterface mrfi = (MorningReviewFragmentInterface) getActivity();
                mrfi.onTreeAnalysisFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement MorningReviewFragmentInterface");
            }

        }
    }
    public static class MorningReviewClosureFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_morningreview_closing, container, false);
            return rootView;
        }
    }
}
