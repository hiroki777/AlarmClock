package com.example.alarmclock.activity;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.alarmclock.R;
import com.example.alarmclock.constant.Const;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.receiver.AlarmReceiver;
import com.example.alarmclock.util.DatabaseHelper;
import com.example.alarmclock.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends Fragment {

    private AlarmManager alarmMgr = null;
    private PendingIntent alarmIntent = null;
    private TimePicker timePicker = null;
    private DatabaseHelper helper = null;
    private EditText editAlarmName = null;
    private int reqCode = -1;
    private int alarmID = -1;
    Intent retnIntent = null;
    int currentApiVersion = Build.VERSION.SDK_INT;
    private static int MENU_DELETE_ID = 2;
    private boolean isTablet = false;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        Activity activity = Objects.requireNonNull(getActivity());
        if(activity.findViewById(R.id.inputFrame) != null){
            isTablet = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        // タイムピッカーを取得
        timePicker = view.findViewById(R.id.time_picker);

        // アラーム名を取得
        editAlarmName = view.findViewById(R.id.editAlarmText);

        Activity activity = Objects.requireNonNull(getActivity());

        // ヘルパーの準備
        helper = DatabaseHelper.getInstance(activity);

        // 渡された情報を取得
        this.getReceiveValue(activity);

        // 編集情報を取得
        if(reqCode == Const.EDIT_REQ_CODE){
            // 編集モード
            // 編集前のデータを取得
            this.setEditData();
        }

        // 新規作成の場合、削除ボタンを非表示
        if(reqCode == Const.NEW_REQ_CODE){
            Button deleteBtn = view.findViewById(R.id.delete_btn);
            deleteBtn.setVisibility(view.GONE);
        }

        // メニューの設定
        if(isTablet == false){
            this.setMenu(view, activity);
        }

        // ボタン押下時の処理
        this.setBtnCliekEvent(activity, view);

        return  view;
    }

    /*
    * メニューの設定
     */
    private void setMenu(View view, Activity activity){

        // メニュークリック時の処理設定
        this.setMenuClickEvent(view, activity);

    }

    /*
    * 編集データの設定
     */
    private void setEditData(){

        ListItem item = Util.getAlarmsByID(alarmID, helper);
        editAlarmName.setText(item.getAlarmName());

        if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            timePicker.setHour(Integer.parseInt(item.getHour()));
            timePicker.setMinute(Integer.parseInt(item.getMinitsu()));
        } else {
            timePicker.setCurrentHour(Integer.parseInt(item.getHour()));
            timePicker.setCurrentMinute(Integer.parseInt(item.getMinitsu()));
        }
    }

    /**
     * クリックイベントの設定
     */
    private void setMenuClickEvent(View view, final Activity activity){

        Toolbar toolbar = this.setToolBar(reqCode, view);

        // キャンセルボタン押下時
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(activity);
            }
        });

        // 保存ボタン押下時
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_save) {

                    // 保存処理
                    saveAlarm(activity);

                }else if(id == MENU_DELETE_ID){

                    // アラーム削除処理
                    deleteAlarm(activity);
                }

                return true;
            }
        });
    }

    /**
     * ツールバーの設定
     */
    private Toolbar setToolBar(int reqCode, View view){
        Toolbar toolbar = toolbar = (Toolbar) view.findViewById(R.id.toolbarInput);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        // 保存、削除ボタンの設定
        toolbar.inflateMenu(R.menu.edit_menu);

        // 編集情報を取得
        if(reqCode == Const.EDIT_REQ_CODE){
            // 編集モード
            // 削除ボタンを追加する
            Menu menu = toolbar.getMenu();
            menu.add(0,MENU_DELETE_ID,2,R.string.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return toolbar;
    }

    private void getReceiveValue(final Activity activity){

        // 入力画面に渡された情報を取得
        if(isTablet == true){
            // タブレットの場合
            Bundle bundle = Objects.requireNonNull(getArguments());
            reqCode = bundle.getInt(getString(R.string.request_code),-1);
            alarmID = bundle.getInt(getString(R.string.alarm_id),-1);
        }else{
            // スマホの場合
            Intent intent = activity.getIntent();
            reqCode = intent.getIntExtra(getString(R.string.request_code),-1);
            alarmID = intent.getIntExtra(getString(R.string.alarm_id),-1);
        }
    }

    private void saveAlarm(final Activity activity){

        final int alarmIDForMenu = alarmID;

        // アラーム設定処理
        // 設定時刻を取得
        int hour;
        int minute;
        if (currentApiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            hour = timePicker.getHour();
            minute = timePicker.getMinute();

        } else {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // データ登録 or 更新
        // TODO DB登録後にエラーが発生した場合の考慮が必要
        int requestCode = -1;

        // アラーム名の設定
        String alarmName = editAlarmName.getText().toString();
        if(alarmName.equals("")){
            alarmName = "無題";
        }

        // 時刻登録の準備
        String alarmTime = String.format("%02d", hour) + ":"
                + String.format("%02d", minute);

        if(reqCode == Const.EDIT_REQ_CODE){
            // 編集
            // データ更新処理
            requestCode = alarmIDForMenu;
            try(SQLiteDatabase db = helper.getWritableDatabase()){
                ContentValues cv = new ContentValues();
                cv.put("name",alarmName);
                cv.put("alarttime", alarmTime);
                String[] params = {String.valueOf(requestCode)};
                db.update("alarms",cv,"alarmid = ?",params);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            // 新規
            // データ登録
            try(SQLiteDatabase db = helper.getWritableDatabase()){
                ContentValues cv = new ContentValues();
                cv.put("name",alarmName);
                cv.put("alarttime", alarmTime);
                requestCode = (int)db.insert("alarms",null,cv);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 参考 https://qiita.com/hiroaki-dev/items/e3149e0be5bfa52d6a51
        // アラームの設定
        ListItem listItem = new ListItem();
        listItem.setAlarmID(requestCode);
        listItem.setAlarmName(alarmName);
        listItem.setTime(alarmTime);
        Util.setAlarm(activity, listItem);

        Toast.makeText(activity,R.string.alarm_save_msg,Toast.LENGTH_SHORT).show();

        if(isTablet == true){
            // タブレットの場合
            this.finishFragment(activity);
        }else{
            // スマホの場合
            retnIntent = new Intent();
            activity.setResult(activity.RESULT_OK, retnIntent);
            activity.finish();
        }

    }

    private void deleteAlarm(final Activity activity){

        alarmMgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        Intent sendIntent = new Intent(activity, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(activity, alarmID, sendIntent, 0);
        alarmMgr.cancel(alarmIntent);

        // データ削除処理
        try(SQLiteDatabase db = helper.getWritableDatabase()){
            String[] params = {String.valueOf(alarmID)};
            db.delete("alarms","alarmid = ?",params);
        }catch (Exception e){
            e.printStackTrace();
        }

        Toast.makeText(activity,R.string.alarm_delete_msg,Toast.LENGTH_SHORT).show();

        if(isTablet == true){
            // タブレットの場合
            this.finishFragment(activity);
        }else{
            retnIntent = new Intent();
            activity.setResult(activity.RESULT_OK, retnIntent);
            activity.finish();
        }
    }

    private void cancel(final Activity activity){
        if(isTablet){
            getFragmentManager().beginTransaction().remove(this).commit();
        }else{
            Intent i = new Intent();
            activity.setResult(activity.RESULT_CANCELED, i);
            activity.finish();
        }
    }

    private void setBtnCliekEvent(final Activity activity,View view){
        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm(activity);
            }
        });

        Button deleteBtn = view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlarm(activity);
            }
        });

        Button cancelBtn = view.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(activity);
            }
        });
    }

    private void finishFragment(Activity activity){
        ListFragment fragment
                = (ListFragment) ((AppCompatActivity)activity).getSupportFragmentManager().findFragmentById(R.id.listFragment);
        ArrayList<ListItem> dataAlarms = fragment.loadAlarms(activity);
        fragment.updateRV(dataAlarms);
        getFragmentManager().beginTransaction().remove(this).commit();
    }

}
