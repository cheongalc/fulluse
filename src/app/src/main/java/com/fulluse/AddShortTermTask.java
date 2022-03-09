package com.fulluse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AddShortTermTask extends AppCompatActivity implements TaskFragmentInterface{

    private static final String LOG_TAG = "ADDSHORTTERMTASK";
    private DBHelper dbHelper;
    private static final int NUM_PAGES = 4;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private RelativeLayout titleLayout, priorityLayout, dueDateLayout;

    private String taskNameTemp;

    private LinearLayout ll_tags;
    private HorizontalScrollView hsv;

    private int numOfTags = 0;
    private static final int TAG_LIMIT = 4;

    private ArrayList<View> tagArrayList = new ArrayList<>();

    private static final String[][] subjects = {
            new String[] {
                   "MATH", "MA", "MATHEMATICS", "ARITHMETIC", "METH"
            },

            new String[] {
                    "ENGLISH", "ENG", "EN", "ELL", "ENGLAND", "ENGERISH"
            },

            new String[] {
                    "CHINESE", "CL", "CHYNA", "CHINA", "CINA"
            },

            new String[] {
                    "SCIENCE", "LSS", "SC", "SCIENTIST", "CHEM", "CHEMISTRY", "BIO", "BIOLOGY", "PHY", "PHYSICS"
            },

            new String[] {
                    "HUMANITIES", "HISTORY", "HI", "GEOGRAPHY", "GEOG", "GI"
            },

            new String[] {
                    "INFOCOMM", "HTML", "C++", "PYTHON", "SHEE", "SCRATCH", "ROBOTICS"
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_short_term_task);

        dbHelper = new DBHelper(this);

        viewPager = (ViewPager) findViewById(R.id.vp_addSTT);
        pagerAdapter = new ShortTermTaskPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rl_addSTT);
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

        ImageButton preSelectedBtn = (ImageButton) findViewById(R.id.imgbtn_sttFragment1);
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

        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);

        LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.ll_addSTT);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.imgbtn_cancelSTT);
        ImageButton addButton = (ImageButton) findViewById(R.id.imgbtn_addSTT);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(AddShortTermTask.this);
        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                .setTarget(buttonsLayout)
                .setDismissText("GOT IT")
                .setContentText("This is the interface for adding Short Term Tasks. Swipe left/right to switch pages, or use these circular buttons.")
                .setTargetTouchable(false)
                .withRectangleShape(true)
                .setMaskColour(maskColour)
                .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                .setTarget(cancelButton)
                .setDismissText("GOT IT")
                .setContentText("Click this button to go back if you don't want to add your task.")
                .setTargetTouchable(false)
                .withRectangleShape()
                .setMaskColour(maskColour)
                .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                .setTarget(addButton)
                .setDismissText("GOT IT")
                .setContentText("Click this button to finish adding your Short Term Task.")
                .setTargetTouchable(false)
                .withRectangleShape()
                .setMaskColour(maskColour)
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
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(AddShortTermTask.this);
        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        EditText taskNameEditText = (EditText) titleLayout.findViewById(R.id.et_taskTitle);
        EditText addTagEditText = (EditText) titleLayout.findViewById(R.id.et_addTag);

        sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(taskNameEditText)
                        .setDismissText("GOT IT")
                        .setContentText("Key in your task name here.")
                        .withRectangleShape(true)
                        .setMaskColour(maskColour)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(addTagEditText)
                        .setDismissText("GOT IT")
                        .setContentText("You can tag your Short Term Tasks to distinguish between subjects, etc.")
                        .withRectangleShape()
                        .setMaskColour(maskColour)
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
                        .build()
        );
        sequence.start();
    }

    private void showTutorialPt3() {
        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        RadioGroup rg = (RadioGroup) priorityLayout.findViewById(R.id.rg_taskPriority);
        RadioButton rbLow = (RadioButton) rg.findViewById(R.id.rb_priorityLow);
        RadioButton rbHigh = (RadioButton) rg.findViewById(R.id.rb_priorityHigh);

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(rg)
                        .setDismissText("GOT IT")
                        .setContentText("The Task Priority denotes how important a task is. Your To Do List is sorted according to Priority.")
                        .withRectangleShape(true)
                        .setMaskColour(maskColour)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(rbLow)
                        .setDismissText("GOT IT")
                        .setContentText("In the To Do List, ! denotes Low Priority and !! denotes Medium Priority.")
                        .withRectangleShape()
                        .setMaskColour(maskColour)
                        .build()
        );
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(rbHigh)
                        .setDismissText("GOT IT")
                        .setContentText("!!! denotes High Priority and these tasks are highlighted in bold.")
                        .withRectangleShape()
                        .setMaskColour(maskColour)
                        .setListener(new IShowcaseListener() {
                            @Override
                            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                            }

                            @Override
                            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                                viewPager.setCurrentItem(3);
                                showTutorialPt4();
                            }
                        })
                        .build()
        );
        sequence.start();
    }

    private void showTutorialPt4() {
        int maskColour = ContextCompat.getColor(this, R.color.colorPrimaryDarkShowcase);
        WheelDatePicker wdp = (WheelDatePicker) dueDateLayout.findViewById(R.id.wdp_sttEndDate);
        new MaterialShowcaseView.Builder(this)
                .setTarget(wdp)
                .setDismissText("GOT IT")
                .setContentText("You can set your task's Due Date here. You will be notified 2 days and 1 day before it at a time you can set (default 9AM).")
                .withRectangleShape(true)
                .setDelay(1000)
                .setMaskColour(maskColour)
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


    public void addShortTermTask(View view) {

        EditText taskNameEditText = (EditText) titleLayout.findViewById(R.id.et_taskTitle);
        if (taskNameEditText == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please add a task name.")
                    .show();
            return;
        }
        String taskName = String.valueOf(taskNameEditText.getText());

        if  (priorityLayout == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please add a task priority.")
                    .show();
            return;
        }
        RadioGroup radioGroup = (RadioGroup) priorityLayout.findViewById(R.id.rg_taskPriority);
        int selectedID = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) priorityLayout.findViewById(selectedID);

        Log.d("ADDSTT", String.valueOf(priorityLayout));
        if (dueDateLayout == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please specify a task due date.")
                    .show();
            return;
        }
        WheelDatePicker wheelDatePicker = (WheelDatePicker) dueDateLayout.findViewById(R.id.wdp_sttEndDate);
        if (!taskName.equals("")) {
            if (radioButton != null){

                String priorityType = String.valueOf(radioButton.getText());
                int taskPriority;
                switch (priorityType) {
                    case "Low":
                        taskPriority = 3;
                        break;
                    case "Medium":
                        taskPriority = 2;
                        break;
                    case "High":
                        taskPriority = 1;
                        break;
                    default:
                        taskPriority = 3;
                        break;
                }

                String encodedTagString = encodeTags();

                dbHelper = new DBHelper(this);
                dbHelper.openDB();
                dbHelper.insertShortTermTask(taskName, taskPriority, wheelDatePicker.getCurrentDay(), wheelDatePicker.getCurrentMonth(), wheelDatePicker.getCurrentYear(), encodedTagString);
                dbHelper.closeDB();

                showToast(taskName);

                finish();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Please add a task priority.")
                        .show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please add a task name.")
                    .show();
        }
    }

    private String encodeTags() {
        String encodedString = "";
        for (int i = 0; i < tagArrayList.size(); i++) {
            View currentTag = tagArrayList.get(i);
            TextView tv_tagName = (TextView) currentTag.findViewById(R.id.tv_tagName);
            String tagName = String.valueOf(tv_tagName.getText());
            encodedString += tagName + ":";
        }
        return encodedString;
    }

    @Override
    public void onTitleFragmentCreated(RelativeLayout layout) {
        titleLayout = layout;
        ll_tags = (LinearLayout) layout.findViewById(R.id.ll_tags);
        hsv = (HorizontalScrollView) layout.findViewById(R.id.hsv_tags);

        final EditText et_taskName = (EditText) layout.findViewById(R.id.et_taskTitle);

        loadTags();

        et_taskName.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        et_taskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String taskName = String.valueOf(et_taskName.getText());
                taskNameTemp = taskName;
                if (taskName != "" && taskName != null) {
                    //press enter -- creating a new taskname, makes sense to clear previous tags + custom tags
                    ll_tags.removeAllViews();
                    tagArrayList.clear();

                    tagArrayList = searchKeyWords(taskName);
                    loadTags();

                    hsv.postDelayed(new Runnable() {
                        public void run() {
                            hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                        }
                    }, 100L);
                }
                return false;
            }
        });

        final EditText et_addTag = (EditText) layout.findViewById(R.id.et_addTag);
        et_addTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String tagName = String.valueOf(et_addTag.getText());
                if (!tagName.equals("") && numOfTags < TAG_LIMIT) {
                    addTag(tagName);
                    numOfTags++;
                    et_addTag.setText("");
                    et_addTag.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_addTag, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }, 10);
                }
                return false;
            }
        });
    }

    private ArrayList<View> searchKeyWords(String taskName) {

        String[] fragments = taskName.split("\\s+");
        Log.d(LOG_TAG, "beginning of fragments: [");
        for (int i = 0; i < fragments.length; i++) {
            Log.d(LOG_TAG, fragments[i] + ", ");
        }
        Log.d(LOG_TAG, "] end of fragments");

        ArrayList<View> arrayList = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) AddShortTermTask.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        for (String currentStr : fragments) {
            boolean matchFound = false;
            for (String[] subject : subjects) {
                if (matchFound) break;
                for (String subjectNameUC : subject) {
                    if (subjectNameUC.toLowerCase().equals(currentStr.toLowerCase())) {
                        Log.d(LOG_TAG, "match found between " + currentStr + " and " + subjectNameUC);
                        View view = inflater.inflate(R.layout.item_tag, null);
                        String subjectName = subject[0];
                        TextView tv_tagName = (TextView) view.findViewById(R.id.tv_tagName);
                        tv_tagName.setText(subjectName);
                        arrayList.add(view);
                        matchFound = true;
                        numOfTags++;
                        break;
                    }
                }
            }
        }
        Log.d(LOG_TAG, " @searchKeyWords(): pushing out array list of size " + String.valueOf(arrayList.size()));
        return arrayList;
    }

    public void addTag (String tagName) {
        LayoutInflater inflater = (LayoutInflater) AddShortTermTask.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View tag = inflater.inflate(R.layout.item_tag, null);
        TextView tv_tagName = (TextView) tag.findViewById(R.id.tv_tagName);
        tv_tagName.setText(tagName);
        if (tag.getParent() != null) {
            ((ViewGroup) tag.getParent()).removeView(tag);
        }
        tagArrayList.add(tag);
        ll_tags.addView(tag);
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    public void loadTags() {
        for (int i = 0; i < tagArrayList.size(); i++) {
            View currentTag = tagArrayList.get(i);
            if (currentTag.getParent() != null) ((ViewGroup) currentTag.getParent()).removeView(currentTag);
            ll_tags.addView(currentTag);
        }
    }

    public void deleteTag(View v) {
        RelativeLayout parentTag = (RelativeLayout) v.getParent().getParent();
        ll_tags.removeView(parentTag);
        tagArrayList.remove(parentTag);
        numOfTags--;
    }

    @Override
    public void onPriorityFragmentCreated(RelativeLayout layout) {
        priorityLayout = layout;
    }

    @Override
    public void onDueDateFragmentCreated(RelativeLayout layout) {
        dueDateLayout = layout;
        WheelDatePicker wdp = (WheelDatePicker) layout.findViewById(R.id.wdp_sttEndDate);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;


        Log.d(LOG_TAG, "screen height: " + String.valueOf(height));

        if (height <= 1280) {
            wdp.setVisibleItemCount(4);
        } else wdp.setVisibleItemCount(5);
        wdp.setCyclic(true);
        wdp.setSelectedItemTextColor(ContextCompat.getColor(this, R.color.wdpWhite));
        wdp.setItemTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onEndDateFragmentCreated(RelativeLayout layout) {

    }

    public void showToast(String taskName) {
        String toast = "Short term task " + taskName + " added.";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void selectFragment(View view) {
        int tag = Integer.parseInt(String.valueOf(view.getTag()))-1;
        viewPager.setCurrentItem(tag, true);
    }

    private class ShortTermTaskPagerAdapter extends FragmentStatePagerAdapter {

        int numberOfFragments;

        public ShortTermTaskPagerAdapter(FragmentManager fm, int numberOfFragments) {
            super(fm);
            this.numberOfFragments = numberOfFragments;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ShortTermTaskIntroFragment();
                case 1:
                    return new TaskTitleFragment();
                case 2:
                    return new TaskPriorityFragment();
                case 3:
                    return new ShortTermTaskDueDateFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return this.numberOfFragments;
        }
    }

    public static class ShortTermTaskIntroFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stt_intro, container, false);
            return rootView;
        }

    }

    public static class ShortTermTaskDueDateFragment extends Fragment {

        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stt_duedate, container, false);

            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                TaskFragmentInterface tfi = (TaskFragmentInterface) getActivity();
                tfi.onDueDateFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement TaskFragmentInterface");
            }
        }
    }
}
