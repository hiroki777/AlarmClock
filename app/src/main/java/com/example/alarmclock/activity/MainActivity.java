package com.example.alarmclock.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.alarmclock.R;
import com.example.alarmclock.listcomponent.ListItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static public int NEW_REQ_CODE = 1;
    final static public int EDIT_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // リクエストコードと結果コードをチェック
        if(requestCode == RESULT_CANCELED){
            // 何もしない
        }else if((requestCode == NEW_REQ_CODE || requestCode == EDIT_REQ_CODE) && resultCode == RESULT_OK){
            ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
            ArrayList<ListItem> dataAlarms = fragment.loadAlarms(this);
            fragment.updateRV(dataAlarms);
        }
    }
}
