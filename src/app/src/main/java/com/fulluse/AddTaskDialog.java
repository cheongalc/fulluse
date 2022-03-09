package com.fulluse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AddTaskDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_dialog);
    }

    public void startAddShortTermTaskActivity(View view) {
        Intent i = new Intent(this, AddShortTermTask.class);
        startActivity(i);
        finish();
    }


    public void closeActivity(View view) {
        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void startAddLongTermTaskActivity(View view) {
        Intent i = new Intent(this, AddLongTermTask.class);
        startActivity(i);
        finish();
    }
}
