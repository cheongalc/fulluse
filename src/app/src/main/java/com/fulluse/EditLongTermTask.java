package com.fulluse;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;

public class EditLongTermTask extends AppCompatActivity {

    private DBHelper dbHelper;
    private String originalName;
    private int originalID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_long_term_task);

        dbHelper = new DBHelper(this);

        String taskName = getIntent().getStringExtra("taskName");

        originalName = taskName;

        EditText taskNameEditText = (EditText) findViewById(R.id.et_editLTT);
        taskNameEditText.setText(taskName);

        SQLiteDatabase db = dbHelper.openDB();
        String[] columns = {
                DBHelper.LONG_TERM_TASK_ID,
                DBHelper.LONG_TERM_TASK_PRIORITY,
                DBHelper.LONG_TERM_TASK_ENDDAY,
                DBHelper.LONG_TERM_TASK_ENDMTH,
                DBHelper.LONG_TERM_TASK_ENDYR
        };
        String whereClause = DBHelper.LONG_TERM_TASK_NAME + " =?";
        String[] whereArgs = {originalName};

        Log.d("LTT", originalName);

        Cursor cursor = db.query(
                DBHelper.TABLE_LONG_TERM_TASK,
                columns,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        cursor.move(1);

        originalID = cursor.getInt(cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ID));
        int taskPriority = cursor.getInt(cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_PRIORITY)),
                endDay = cursor.getInt(cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDDAY)),
                endMth = cursor.getInt(cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDMTH)),
                endYr = cursor.getInt(cursor.getColumnIndex(DBHelper.LONG_TERM_TASK_ENDYR));

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

        WheelDatePicker wdp = (WheelDatePicker) findViewById(R.id.wdp_editLTT);
        wdp.setSelectedDay(endDay);
        wdp.setSelectedMonth(endMth);
        wdp.setSelectedYear(endYr);
        wdp.setSelectedItemTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    public void finishEditing(View view) {
        String newName = String.valueOf(((EditText) findViewById(R.id.et_editLTT)).getText());
        if (newName.equals("")) newName = originalName;

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_editLTT);
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

        WheelDatePicker wdp = (WheelDatePicker) findViewById(R.id.wdp_editLTT);

        dbHelper.openDB();
        dbHelper.editLTT(originalID, newName, newPriority, wdp.getCurrentDay(), wdp.getCurrentMonth(), wdp.getCurrentYear());
        dbHelper.closeDB();

        showToast();

        finish();
        overridePendingTransition(R.anim.flyupfrombottom, R.anim.flydownfrombottom);
    }

    public void showToast() {
        String toast = "Long term task " + originalName + " updated.";
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
