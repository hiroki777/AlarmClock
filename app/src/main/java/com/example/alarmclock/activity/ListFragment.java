package com.example.alarmclock.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alarmclock.R;
import com.example.alarmclock.constant.Const;
import com.example.alarmclock.listcomponent.ListAdapter;
import com.example.alarmclock.listcomponent.ListItem;
import com.example.alarmclock.util.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ListFragment extends Fragment {

    RecyclerView rv = null;
    private boolean isTablet = false;
    ListAdapter listAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = Objects.requireNonNull(getActivity());
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // アラームのデータを取得
        ArrayList<ListItem> data = this.loadAlarms(activity);

        // RecyclerViewに設定
        this.setRV(data, view, activity);

        // フローティングアクションボタンの設定
        this.setFAB(activity, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // タブレットかどうかの判定
        if(getActivity().findViewById(R.id.inputFrame) != null){
            isTablet = true;
            this.listAdapter.setTablet(isTablet);
        }
    }

    /*
    * フローティングアクションボタンの設定
     */
    private void setFAB(Context context, View view){

        FloatingActionButton fbt = view.findViewById(R.id.fbtn);
        final Context context1 = context;
        fbt.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(isTablet == true){
                            // タブレットの場合
                            // フラグメントを更新
                            Bundle bundle = new Bundle();
                            bundle.putInt(context1.getString(R.string.request_code), Const.NEW_REQ_CODE);

                            FragmentManager manager
                                    = Objects.requireNonNull(((AppCompatActivity) context1).getSupportFragmentManager());
                            InputFragment inputFragment = new InputFragment();
                            inputFragment.setArguments(bundle);
                            manager.beginTransaction().replace(R.id.inputFrame,inputFragment).commit();

                        }else{
                            // スマホの場合
                            Intent i = new Intent(context1, InputActivity.class);
                            i.putExtra(getString(R.string.request_code), Const.NEW_REQ_CODE);
                            ((Activity)context1).startActivityForResult(i,Const.NEW_REQ_CODE);
                        }
                    }
                });
    }

    /*
     * アラーム情報を取得
     */
    public ArrayList<ListItem> loadAlarms(Context context){
        DatabaseHelper helper = DatabaseHelper.getInstance(context);

        ArrayList<ListItem> data = new ArrayList<>();

        try(SQLiteDatabase db = helper.getReadableDatabase()) {

            String[] cols ={"alarmid","name","alarttime"};

            Cursor cs = db.query("alarms",cols,null,null,
                    null,null,"alarmid",null);
            boolean eol = cs.moveToFirst();
            while (eol){
                ListItem item = new ListItem();
                item.setAlarmID(cs.getInt(0));
                item.setAlarmName(cs.getString(1));
                item.setTime(cs.getString(2));
                data.add(item);
                eol = cs.moveToNext();
            }
        }
        return data;
    }

    private void setRV(ArrayList<ListItem> data , View view, Context context){
        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        listAdapter = new ListAdapter(data);
        rv.setAdapter(listAdapter);
    }

    public void updateRV(ArrayList<ListItem> data){
        listAdapter = new ListAdapter(data, isTablet);
        rv.setAdapter(listAdapter);
    }
}
