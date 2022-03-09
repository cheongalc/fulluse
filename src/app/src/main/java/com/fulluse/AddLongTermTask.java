package com.fulluse;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;

public class AddLongTermTask extends AppCompatActivity implements TaskFragmentInterface {

    private DBHelper dbHelper;
    private static final int NUM_PAGES = 4;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private RelativeLayout titleLayout, priorityLayout, endDateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_long_term_task);

        dbHelper = new DBHelper(this);

        viewPager = (ViewPager) findViewById(R.id.vp_addLTT);
        pagerAdapter = new LongTermTaskPagerAdapter(getSupportFragmentManager(), NUM_PAGES);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rl_addLTT);
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
        ImageButton currentBtn = (ImageButton) findViewById(R.id.imgbtn_lttFragment1);
        currentBtn.setImageResource(R.drawable.ic_task_imgbtn_selected);
    }

    public void addLongTermTask(View view) {
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
        if (endDateLayout == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please specify a task end date.")
                    .show();
            return;
        }
        WheelDatePicker wheelDatePicker = (WheelDatePicker) endDateLayout.findViewById(R.id.wdp_lttEndDate);

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

                dbHelper = new DBHelper(this);
                dbHelper.openDB();
                dbHelper.insertLongTermTask(taskName, taskPriority, wheelDatePicker.getCurrentDay(), wheelDatePicker.getCurrentMonth(), wheelDatePicker.getCurrentYear());
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
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please add a task name.")
                    .show();
        }

    }

    public void showToast(String taskName) {
        String toast = "Long term task " + taskName + " added.";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void closeActivity(View view) {
        finish();
    }

    public void selectFragment(View view) {
        int tag = Integer.parseInt(String.valueOf(view.getTag()))-1;
        viewPager.setCurrentItem(tag, true);
    }

    @Override
    public void onTitleFragmentCreated(RelativeLayout layout) {
        titleLayout = layout;
        EditText et_addTag = (EditText) layout.findViewById(R.id.et_addTag);
        et_addTag.setVisibility(View.GONE);
    }

    @Override
    public void onPriorityFragmentCreated(RelativeLayout layout) {
        priorityLayout = layout;
    }

    @Override
    public void onDueDateFragmentCreated(RelativeLayout layout) {
        //ignore
    }

    @Override
    public void onEndDateFragmentCreated(RelativeLayout layout) {
        endDateLayout = layout;
        WheelDatePicker wdp = (WheelDatePicker) findViewById(R.id.wdp_lttEndDate);
        wdp.setVisibleItemCount(5);
        wdp.setCyclic(true);
        wdp.setSelectedItemTextColor(ContextCompat.getColor(this, R.color.wdpWhite));
        wdp.setItemTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private class LongTermTaskPagerAdapter extends FragmentStatePagerAdapter {

        int numberOfFragments;

        public LongTermTaskPagerAdapter(FragmentManager fm, int numberOfFragments) {
            super(fm);
            this.numberOfFragments = numberOfFragments;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LongTermTaskIntroFragment();
                case 1:
                    return new TaskTitleFragment();
                case 2:
                    return new TaskPriorityFragment();
                case 3:
                    return new LongTermTaskEndDateFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return this.numberOfFragments;
        }
    }

    public static class LongTermTaskIntroFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_ltt_intro, container, false);
            return rootView;
        }
    }

    public static class LongTermTaskEndDateFragment extends Fragment {

        private RelativeLayout rootLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_ltt_enddate, container, false);
            rootLayout = (RelativeLayout) rootView;
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            try {
                TaskFragmentInterface tfi = (TaskFragmentInterface) getActivity();
                tfi.onEndDateFragmentCreated(rootLayout);
            } catch (ClassCastException e) {
                Log.e("ERROR", String.valueOf(getActivity()) + " must implement TaskFragmentInterface");
            }
        }
    }
}
