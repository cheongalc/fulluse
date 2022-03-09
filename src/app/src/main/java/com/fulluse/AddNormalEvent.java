package com.fulluse;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;


import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AddNormalEvent extends AppCompatActivity implements NormalEventFragmentInterface {

    private Calendar calendar = Calendar.getInstance();

    private int fromPos, toPos;
    private List<String> timeList;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private static final int NUM_PAGES = 4;

    private DBHelper dbHelper;

    private RelativeLayout introLayout, titleLayout, startDateLayout, endDateLayout;

    private String globalEventName;
    private static final String LOG_TAG = "ADD_NORMAL_EVENT";
    private static final String ERROR_EVENT_NAME_BLANK = "ERROR_EVENT_NAME_BLANK";
    private static final String ERROR_EVENT_ZERO_MINUTES = "ERROR_EVENT_ZERO_MINUTES";
    private static final String ERROR_EVENT_END_EARLIER  = "ERROR_EVENT_END_EARLIER";

    private DateTime startDate, endDate;
    private int startYear = calendar.get(Calendar.YEAR),
                startMonth = calendar.get(Calendar.MONTH)+1,
                startDay = calendar.get(Calendar.DAY_OF_MONTH),
                startHour = calendar.get(Calendar.HOUR_OF_DAY),  //24 hour
                startMinute = calendar.get(Calendar.MINUTE);
    private int endYear = calendar.get(Calendar.YEAR),
                endMonth = calendar.get(Calendar.MONTH)+1,
                endDay = calendar.get(Calendar.DAY_OF_MONTH),
                endHour = calendar.get(Calendar.HOUR_OF_DAY),  //24 hour
                endMinute = calendar.get(Calendar.MINUTE);
    private final DatePickerDialog.OnDateSetListener onStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            startYear = year;
            startMonth = monthOfYear+1;
            startDay = dayOfMonth;
            TextView eventStartDateTextView = (TextView) startDateLayout.findViewById(R.id.tv_eventStartDateDisplay);
            eventStartDateTextView.setText(String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
        }
    };
    private final TimePickerDialog.OnTimeSetListener onStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
            String displayedStartTime;
            if (hourOfDay < 10) displayedStartTime = "0" + String.valueOf(hourOfDay);
            else displayedStartTime = String.valueOf(hourOfDay);
            if (minute < 10) displayedStartTime += ":0" + String.valueOf(minute);
            else displayedStartTime += ":" + String.valueOf(minute);
            TextView startTimeTextView = (TextView) startDateLayout.findViewById(R.id.tv_eventStartTimeDisplay);
            startTimeTextView.setText(displayedStartTime);
        }
    };
    private final DatePickerDialog.OnDateSetListener onEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            endYear = year;
            endMonth = monthOfYear+1;
            endDay = dayOfMonth;
            TextView endDateTextView = (TextView) endDateLayout.findViewById(R.id.tv_eventEndDateDisplay);
            endDateTextView.setText(String.valueOf(monthOfYear+1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year));
        }
    };
    private final TimePickerDialog.OnTimeSetListener onEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            endHour = hourOfDay;
            endMinute = minute;
            String displayedEndTime;
            if (hourOfDay < 10) displayedEndTime = "0" + String.valueOf(hourOfDay);
            else displayedEndTime = String.valueOf(hourOfDay);
            if (minute < 10) displayedEndTime += ":0" + String.valueOf(minute);
            else displayedEndTime += ":" + String.valueOf(minute);
            TextView endTimeTextView = (TextView) endDateLayout.findViewById(R.id.tv_eventEndTimeDisplay);
            endTimeTextView.setText(displayedEndTime);
        }
    };


    int maskColour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_normal_event);

        maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);

        timeList = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.vp_addEvent);
        pagerAdapter = new NormalEventPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rl_addNormalEvent);
                String tag = String.valueOf(position+1);

                for (int i = 1; i <= NUM_PAGES; i++) {
                    ImageButton imageButton = (ImageButton) rootLayout.findViewWithTag(String.valueOf(i));
                    imageButton.setImageResource(R.drawable.ic_task_imgbtn_deselected);
                }

                ImageButton selectedBtn = (ImageButton) rootLayout.findViewWithTag(tag);
                selectedBtn.setImageResource(R.drawable.ic_task_imgbtn_selected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageButton preSelectedBtn = (ImageButton) findViewById(R.id.imgbtn_eventFragment1);
        preSelectedBtn.setImageResource(R.drawable.ic_task_imgbtn_selected);

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
               checkTutorial();
            }
        }, 1000);
    }

    private boolean checkTutorialShownYet() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean("tutorialShownYet", false);
    }

    private void checkTutorial() {
        if (!checkTutorialShownYet()) {
            showTutorial();
        }
    }

    private void showTutorial() {
        TextView eventDesc = (TextView) introLayout.findViewById(R.id.tv_eventDesc);
        new MaterialShowcaseView.Builder(AddNormalEvent.this)
                .setTarget(eventDesc)
                .setDismissText("GOT IT")
                .setContentText("The swiping functions all remain the same.")
                .setDelay(1000)
                .setMaskColour(maskColour)
                .withRectangleShape()
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
                .show();
    }

    private void showTutorialPt2() {
        TextView eventNameEditText = (TextView) titleLayout.findViewById(R.id.et_eventTitle);
        new MaterialShowcaseView.Builder(AddNormalEvent.this)
                .setTarget(eventNameEditText)
                .setDismissText("GOT IT")
                .setContentText("Key in your event name here.")
                .setDelay(1000)
                .setMaskColour(maskColour)
                .withRectangleShape(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        viewPager.setCurrentItem(2);
                        showTutorialPt3();
                    }
                })
                .show();
    }

    private void showTutorialPt3() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        TextView eventStartDateDesc = (TextView) startDateLayout.findViewById(R.id.tv_eventStartDateDesc);
        LinearLayout eventStartDate = (LinearLayout) startDateLayout.findViewById(R.id.ll_eventStartDate);
        LinearLayout eventStartTime = (LinearLayout) startDateLayout.findViewById(R.id.ll_eventStartTime);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(eventStartDateDesc)
                        .setDismissText("GOT IT")
                        .setContentText("Information regarding the Event Start Date and Time.")
                        .withRectangleShape(true)
                        .setMaskColour(maskColour)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(eventStartDate)
                        .setDismissText("GOT IT")
                        .setContentText("You can change this Start Date to make your event span over multiple days.")
                        .withRectangleShape()
                        .setMaskColour(maskColour)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(eventStartTime)
                        .setDismissText("GOT IT")
                        .setContentText("You can also change the Start Time.")
                        .withRectangleShape()
                        .setMaskColour(maskColour)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                viewPager.setCurrentItem(3);;
                                showTutorialPt4();
                            }
                        })
                        .build()
        );
        sequence.start();
    }

    private void showTutorialPt4() {
        LinearLayout eventEndDate = (LinearLayout) endDateLayout.findViewById(R.id.ll_eventEndDate);

        new MaterialShowcaseView.Builder(AddNormalEvent.this)
                .setTarget(eventEndDate)
                .setDismissText("GOT IT")
                .setContentText("Same rules apply. Make sure the event isn't 0 min, or the end is not before the start.")
                .setDelay(1000)
                .setMaskColour(maskColour)
                .withRectangleShape(true)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        closeActivity(null);
                    }
                })
                .show();
    }

    public void closeActivity(View view) {
        finish();
    }

    public void addEvent(View view) {
        EditText eventNameEditText = (EditText) titleLayout.findViewById(R.id.et_eventTitle);
        final String eventName = String.valueOf(eventNameEditText.getText());
        globalEventName = eventName;
        // if the event Name is blank, show alert & break out of function
        if (isEventNameBlank(eventName)) {
            showAlertDialog(ERROR_EVENT_NAME_BLANK);
            return;
        }


        DateTime startDate = new DateTime(startYear, startMonth, startDay, startHour, startMinute);
        DateTime endDate = new DateTime(endYear, endMonth, endDay, endHour, endMinute);
        String displayedStartTime, displayedEndTime;
        if (startHour < 10) displayedStartTime = "0" + String.valueOf(startHour);
        else displayedStartTime = String.valueOf(startHour);
        if (startMinute < 10) displayedStartTime += "0" + String.valueOf(startMinute);
        else displayedStartTime += String.valueOf(startMinute);
        if (endHour < 10) displayedEndTime = "0" + String.valueOf(endHour);
        else displayedEndTime = String.valueOf(endHour);
        if (endMinute < 10) displayedEndTime += "0" + String.valueOf(endMinute);
        else displayedEndTime += String.valueOf(endMinute);
        final String finalDisplayedStartTime = displayedStartTime;
        final String finalDisplayedEndTime = displayedEndTime;

        if (isEventZeroMinutes(startDate, endDate)) {
            showAlertDialog(ERROR_EVENT_ZERO_MINUTES);
            return;
        }

        if (isEventEndEarlier(startDate, endDate)) {
            showAlertDialog(ERROR_EVENT_END_EARLIER);
            return;
        }

        if (isEventContainingTest(eventName)) {
            new AlertDialog.Builder(this)
                    .setTitle("Info")
                    .setMessage("Would you like to add a long term task: Revise for " + eventName)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String taskName = "Revise for " + eventName;
                            int taskPriority = 1;
                            DBHelper dbHelper = new DBHelper(AddNormalEvent.this);
                            dbHelper.openDB();
                            dbHelper.insertLongTermTask(taskName, taskPriority, startDay, startMonth, startYear);
                            dbHelper.insertEvent(
                                    eventName,
                                    startDay,
                                    startMonth,
                                    startYear,
                                    endDay,
                                    endMonth,
                                    endYear,
                                    finalDisplayedStartTime,
                                    finalDisplayedEndTime);
                            dbHelper.closeDB();
                            finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBHelper dbHelper = new DBHelper(AddNormalEvent.this);
                            dbHelper.openDB();
                            dbHelper.insertEvent(
                                    eventName,
                                    startDay,
                                    startMonth,
                                    startYear,
                                    endDay,
                                    endMonth,
                                    endYear,
                                    finalDisplayedStartTime,
                                    finalDisplayedEndTime);
                            dbHelper.closeDB();
                            finish();
                        }
                    })
                    .show();
        } else {
            DBHelper dbHelper = new DBHelper(AddNormalEvent.this);
            dbHelper.openDB();
            dbHelper.insertEvent(
                    eventName,
                    startDay,
                    startMonth,
                    startYear,
                    endDay,
                    endMonth,
                    endYear,
                    displayedStartTime,
                    displayedEndTime);
            dbHelper.closeDB();
            finish();
        }

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
    private boolean isEventNameBlank (String eventName) {
        return eventName.equals("");
    }
    private boolean isEventZeroMinutes(DateTime startDate, DateTime endDate) {
        return startDate.isEqual(endDate);
    }
    private boolean isEventEndEarlier(DateTime startDate, DateTime endDate) {
        return endDate.isBefore(startDate);
    }

    public void selectFragment(View view) {
        int tag = Integer.parseInt(String.valueOf(view.getTag()))-1;
        viewPager.setCurrentItem(tag, true);
    }

    @Override
    public void onIntroFragmentCreated(RelativeLayout layout) {
        introLayout = layout;
    }

    @Override
    public void onTitleFragmentCreated(RelativeLayout layout) {
        titleLayout = layout;
    }
    @Override
    public void onStartDateFragmentCreated(RelativeLayout layout) {
        startDateLayout = layout;
        TextView startDateTextView = (TextView) layout.findViewById(R.id.tv_eventStartDateDisplay);
        startDateTextView.setText(String.valueOf(startMonth) + "/" + String.valueOf(startDay) + "/" + String.valueOf(startYear));
        TextView startTimeTextView = (TextView) layout.findViewById(R.id.tv_eventStartTimeDisplay);
        String displayedStartTime;
        if (startHour < 10) displayedStartTime = "0" + String.valueOf(startHour);
        else displayedStartTime = String.valueOf(startHour);
        if (startMinute < 10) displayedStartTime += ":0" + String.valueOf(startMinute);
        else displayedStartTime += ":" + String.valueOf(startMinute);
        startTimeTextView.setText(displayedStartTime);
    }
    @Override
    public void onEndDateFragmentCreated(RelativeLayout layout) {
        endDateLayout = layout;
        TextView endDateTextView = (TextView) layout.findViewById(R.id.tv_eventEndDateDisplay);
        endDateTextView.setText(String.valueOf(endMonth) + "/" + String.valueOf(endDay) + "/" + String.valueOf(endYear));
        TextView endTimeTextView = (TextView) layout.findViewById(R.id.tv_eventEndTimeDisplay);
        String displayedEndTime;
        if (endHour < 10) displayedEndTime = "0" + String.valueOf(endHour);
        else displayedEndTime = String.valueOf(endHour);
        if (endMinute < 10) displayedEndTime += ":0" + String.valueOf(endMinute);
        else displayedEndTime += ":" + String.valueOf(endMinute);
        endTimeTextView.setText(displayedEndTime);
    }

    public void showChangeStartDateDialog(View view) {
        new DatePickerDialog(AddNormalEvent.this, onStartDateSetListener,
                             startYear, startMonth-1, startDay).show();
    }

    public void showChangeStartTimeDialog(View view) {
        new TimePickerDialog(AddNormalEvent.this, onStartTimeSetListener,
                             startHour, startMinute, true).show();
    }

    public void showChangeEndDateDialog(View view) {
        new DatePickerDialog(AddNormalEvent.this, onEndDateSetListener,
                             endYear, endMonth-1, endDay).show();
    }

    public void showChangeEndTimeDialog(View view) {
        new TimePickerDialog(AddNormalEvent.this, onEndTimeSetListener,
                             endHour, endMinute, true).show();
    }


    private class NormalEventPagerAdapter extends FragmentStatePagerAdapter {

        private int numberOfPages;

        public NormalEventPagerAdapter(FragmentManager fm, int numberOfPages) {
            super(fm);
            this.numberOfPages = numberOfPages;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NormalEventIntroFragment();
                case 1:
                    return new NormalEventTitleFragment();
                case 2:
                    return new NormalEventStartDateFragment();
                case 3:
                    return new NormalEventEndDateFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return this.numberOfPages;
        }
    }
    public static class NormalEventIntroFragment extends Fragment {
        private RelativeLayout rootLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_event_intro, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                NormalEventFragmentInterface nefi = (NormalEventFragmentInterface) getActivity();
                nefi.onIntroFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement NormalEventFragmentInterface");
            }
        }
    }
    public static class NormalEventTitleFragment extends Fragment {
        private RelativeLayout rootLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_event_name, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                NormalEventFragmentInterface nefi = (NormalEventFragmentInterface) getActivity();
                nefi.onTitleFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement NormalEventFragmentInterface");
            }
        }
    }
    public static class NormalEventStartDateFragment extends Fragment {
        private RelativeLayout rootLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_event_start_date, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                NormalEventFragmentInterface nefi = (NormalEventFragmentInterface) getActivity();
                nefi.onStartDateFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement NormalEventFragmentInterface");
            }
        }
    }
    public static class NormalEventEndDateFragment extends Fragment {
        private RelativeLayout rootLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_event_end_date, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                NormalEventFragmentInterface nefi = (NormalEventFragmentInterface) getActivity();
                nefi.onEndDateFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement NormalEventFragmentInterface");
            }
        }
    }
}
