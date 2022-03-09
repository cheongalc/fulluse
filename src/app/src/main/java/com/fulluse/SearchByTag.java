package com.fulluse;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class SearchByTag extends AppCompatActivity {

    private static final String LOG_TAG = "SearchByTag";
    private ExpandableHeightListView tagResults;
    private DBHelper dbHelper;
    private TextView noResultsTextView;

    private String tagName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_tag);

        dbHelper = new DBHelper(this);
        tagResults = (ExpandableHeightListView) findViewById(R.id.ehlv_tagSearchResults);
        tagResults.setExpanded(true);
        noResultsTextView = (TextView) findViewById(R.id.tv_noResults);
        searchSTTs("");
        final EditText et_searchBox = (EditText) findViewById(R.id.et_searchTagBox);
        et_searchBox.setImeActionLabel("DONE", KeyEvent.KEYCODE_ENTER);
        et_searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tagName = String.valueOf(et_searchBox.getText());
                searchSTTs(tagName);
                Log.d(LOG_TAG, "searching using keyword " + tagName);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                et_searchBox.clearFocus();
                return false;
            }
        });
    }

    private void searchSTTs(String tagName) {
        ArrayList<ShortTermTaskData> arrayList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = {
                DBHelper.SHORT_TERM_TASK_ID,
                DBHelper.SHORT_TERM_TASK_NAME,
                DBHelper.SHORT_TERM_TASK_PRIORITY,
                DBHelper.SHORT_TERM_TASK_DUEDAY,
                DBHelper.SHORT_TERM_TASK_DUEMTH,
                DBHelper.SHORT_TERM_TASK_DUEYR,
                DBHelper.SHORT_TERM_TASK_TAGSTR
        };
        Cursor cursor = db.query(DBHelper.TABLE_SHORT_TERM_TASK, columns, null, null, null, null, null);
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

            if (tagStrContainsSearchQuery(tagName, encodedTagStr)) {
                if (taskDueDate.isBefore(nowDate)) {
                    arrayList.add(new ShortTermTaskData(displayedName, displayedPriority, displayedDueDate, encodedTagStr, false, true));
                } else arrayList.add(new ShortTermTaskData(displayedName, displayedPriority, displayedDueDate, encodedTagStr, false, false));
            }
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

        ArrayAdapter<ShortTermTaskData> adapter = new ShortTermTaskDataAdapter(this, 0, arrayList);
        tagResults.setAdapter(adapter);

        if (cursor.getCount() == 0) {
            noResultsTextView.setText("No Tasks");
            noResultsTextView.setVisibility(View.VISIBLE);
        } else if (arrayList.size() == 0) {
            noResultsTextView.setText("No Results");
            noResultsTextView.setVisibility(View.VISIBLE);
        } else noResultsTextView.setVisibility(View.GONE);

        cursor.close();
        dbHelper.closeDB();
    }

    private boolean tagStrContainsSearchQuery(String tagName, String encodedTagStr) {
        return Pattern.compile(Pattern.quote(tagName), Pattern.CASE_INSENSITIVE).matcher(encodedTagStr).find();
    }

    public void closeSearch(View v) {
        finish();
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

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
                searchSTTs(tagName);
            }
        }, 1500);
    }
    public void editShortTermTask(View v) {}
}
