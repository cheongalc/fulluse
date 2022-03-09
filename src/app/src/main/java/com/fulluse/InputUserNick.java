package com.fulluse;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputUserNick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_nick);

        final EditText editText = (EditText) findViewById(R.id.userNickEditText);
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String nickname = String.valueOf(editText.getText());
                Log.d("IUN", nickname);

                if (nickname != "" && nickname != null) {
                    SharedPreferences username = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = username.edit();
                    editor.putString("username", nickname);
                    editor.commit();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(InputUserNick.this)
                            .setTitle("Error!")
                            .setMessage("Please set a username.")
                            .create();
                    alertDialog.show();
                }
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                return false;
            }
        });


    }

}
