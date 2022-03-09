package com.fulluse;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;

import java.util.ArrayList;
import java.util.Arrays;

public class EditShortTermTask extends AppCompatActivity {

    private static final String LOG_TAG = "EditShortTermTask";
    private DBHelper dbHelper;
    private String originalName;
    private int originalID;
    private HorizontalScrollView hsv;
    private LinearLayout ll_tags;

    private ArrayList<String> tagsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_short_term_task);

        dbHelper = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        String taskName = extras.getString("taskName");

        originalName = taskName;

        EditText taskNameEditText = (EditText) findViewById(R.id.et_editSTT);
        taskNameEditText.setText(taskName);

        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = {
                DBHelper.SHORT_TERM_TASK_ID,
                DBHelper.SHORT_TERM_TASK_PRIORITY,
                DBHelper.SHORT_TERM_TASK_DUEDAY,
                DBHelper.SHORT_TERM_TASK_DUEMTH,
                DBHelper.SHORT_TERM_TASK_DUEYR,
                DBHelper.SHORT_TERM_TASK_TAGSTR
        };
        String whereClause = DBHelper.SHORT_TERM_TASK_NAME + " =?";
        String[] whereArgs = {taskName};

        Cursor cursor = db.query(
                DBHelper.TABLE_SHORT_TERM_TASK,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        cursor.move(1);

        originalID = cursor.getInt(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_ID));
        int taskPriority = cursor.getInt(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_PRIORITY)),
                dueDay = cursor.getInt(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEDAY)),
                dueMth = cursor.getInt(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEMTH)),
                dueYr = cursor.getInt(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_DUEYR));
        String encodedTagStr = cursor.getString(cursor.getColumnIndex(DBHelper.SHORT_TERM_TASK_TAGSTR));

        Log.d("EDITSTT", String.valueOf(taskPriority));

        switch (taskPriority) {
            case 3:
                RadioButton lowBtn = (RadioButton) findViewById(R.id.rb_priorityLow);
                lowBtn.setChecked(true);
                break;
            case 2:
                RadioButton medBtn = (RadioButton) findViewById(R.id.rb_priorityMed);
                medBtn.setChecked(true);
                break;
            case 1:
                RadioButton highBtn = (RadioButton) findViewById(R.id.rb_priorityHigh);
                highBtn.setChecked(true);
                break;
            default:
                break;
        }

        WheelDatePicker wdp = (WheelDatePicker) findViewById(R.id.wdp_editSTT);
        wdp.setSelectedDay(dueDay);
        wdp.setSelectedMonth(dueMth);
        wdp.setSelectedYear(dueYr);
        wdp.setSelectedItemTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        ll_tags = (LinearLayout) findViewById(R.id.ll_tags);
        loadTags(encodedTagStr);
        hsv = (HorizontalScrollView) findViewById(R.id.hsv_tags);
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);

        final EditText et_addTag = (EditText) findViewById(R.id.et_addTag);
        et_addTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String tagName = String.valueOf(et_addTag.getText());
                if (!tagName.equals("")) {
                    addTag(tagName);
                    et_addTag.setText("");
                    et_addTag.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_addTag, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }, 10);
                }                return false;
            }
        });
    }

    private void addTag(String tagName) {
        LayoutInflater inflater = (LayoutInflater) EditShortTermTask.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View tag = inflater.inflate(R.layout.item_tag, null);
        TextView tv_tagName = (TextView) tag.findViewById(R.id.tv_tagName);
        tv_tagName.setText(tagName);
        if (tag.getParent() != null) {
            ((ViewGroup) tag.getParent()).removeView(tag);
        }
        tagsArrayList.add(tagName);
        ll_tags.addView(tag);
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100L);
    }

    private void loadTags(String encodedTagStr) {
        String[] fragments = encodedTagStr.split(":");
        LayoutInflater inflater = (LayoutInflater) EditShortTermTask.this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i].equals("")) continue;
            View v = inflater.inflate(R.layout.item_tag, null);
            TextView tv_tagName = (TextView) v.findViewById(R.id.tv_tagName);
            tv_tagName.setText(fragments[i]);
            tagsArrayList.add(fragments[i]);
            if (v.getParent() != null) ((ViewGroup) v.getParent()).removeView(v);
            ll_tags.addView(v);
        }
    }

    public void finishEditing(View view) {
        String newName = String.valueOf(((EditText) findViewById(R.id.et_editSTT)).getText());
        if (newName.equals("")) newName = originalName;
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_editSTT);
        int selectedID = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedID);

        String priorityType = String.valueOf(radioButton.getText());
        int newPriority;
        switch (priorityType) {
            case "Low":
                newPriority = 3;
                break;
            case "Medium":
                newPriority = 2;
                break;
            case "High":
                newPriority = 1;
                break;
            default:
                newPriority = 3;
                break;
        }

        WheelDatePicker wdp = (WheelDatePicker) findViewById(R.id.wdp_editSTT);
        String encodedTagStr = encodeTags();

        dbHelper.openDB();
        dbHelper.editSTT(originalID, newName, newPriority, wdp.getCurrentDay(), wdp.getCurrentMonth(), wdp.getCurrentYear(), encodedTagStr);
        dbHelper.closeDB();

        showToast();

        finish();
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

    private String encodeTags() {
        String encodedString = "";
        for (int i = 0; i < tagsArrayList.size(); i++) {
            String currentTag = tagsArrayList.get(i);
            encodedString += currentTag + ":";
        }
        Log.d(LOG_TAG, "encoded tagstr: " + encodedString);
        return encodedString;
    }

    public void showToast() {
        String toast = "Short term task " + originalName + " updated.";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public void deleteTag(View view) {
        RelativeLayout parentTagContainer = (RelativeLayout) view.getParent().getParent();
        TextView tv_tagName = (TextView) parentTagContainer.findViewById(R.id.tv_tagName);
        String tagName = String.valueOf(tv_tagName.getText());
        Log.d(LOG_TAG, "tagName to be deleted: " + tagName);
        tagsArrayList.remove(tagName);
        ll_tags.removeView(parentTagContainer);
    }

}
