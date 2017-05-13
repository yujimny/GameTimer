package com.yujimny.jp.android.gametimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/** 各機能に画面遷移するだけ **/
public class MainActivity extends AppCompatActivity {
    EditText mEditTextTimerMain, mEditTextTimerAdditional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yujimny.jp.android.gametimer.R.layout.activity_main);

        mEditTextTimerMain = (EditText)findViewById(R.id.editText2);
        mEditTextTimerAdditional = (EditText)findViewById(R.id.editText3);
        Button btnOk = (Button)findViewById(R.id.button);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = data.edit();
                editor.putInt("TimerMain", Integer.valueOf(mEditTextTimerMain.getText().toString()));
                editor.putInt("TimerAdditional", Integer.valueOf(mEditTextTimerAdditional.getText().toString()));
                editor.apply();
                Toast.makeText(getApplicationContext(), R.string.toast_done_save_settings, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences data = getSharedPreferences("Data", Context.MODE_PRIVATE);
        int timer_main = data.getInt("TimerMain", getResources().getInteger(R.integer.default_main_timer_minutes));
        int timer_additional = data.getInt("TimerAdditional", getResources().getInteger(R.integer.default_additional_timer_minutes));
        mEditTextTimerMain.setText(String.valueOf(timer_main));
        mEditTextTimerAdditional.setText(String.valueOf(timer_additional));
    }
}