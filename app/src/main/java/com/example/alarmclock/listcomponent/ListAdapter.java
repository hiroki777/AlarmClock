package com.example.alarmclock.listcomponent;


import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;
import com.example.alarmclock.activity.InputFragment;
import com.example.alarmclock.activity.InputActivity;
import com.example.alarmclock.constant.Const;

import java.util.ArrayList;
import java.util.Objects;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private ArrayList<ListItem> data;
    private boolean isTablet = false;

    public ListAdapter(ArrayList<ListItem> data){

        this.data = data;
    }

    public ListAdapter(ArrayList<ListItem> data, boolean isTablet){

        this.data = data;
        this.isTablet = isTablet;
    }

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);


        final ListViewHolder holder = new ListViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = parent.getContext();
                int position = holder.getAdapterPosition();

                if(isTablet){
                    // タブレットの場合
                    // InputFragmentに渡す値を設定
                    Bundle bundle = new Bundle();
                    bundle.putInt(context.getString(R.string.request_code), Const.EDIT_REQ_CODE);
                    bundle.putInt(context.getString(R.string.alarm_id),data.get(position).getAlarmID());

                    // フラグメントを更新
                    FragmentManager manager = Objects.requireNonNull(((AppCompatActivity) context).getSupportFragmentManager());
                    InputFragment inputFragment = new InputFragment();
                    inputFragment.setArguments(bundle);
                    manager.beginTransaction().replace(R.id.inputFrame,inputFragment).commit();

                }else {
                    // スマホの場合
                    Intent i = new Intent(context, InputActivity.class);
                    i.putExtra(context.getString(R.string.request_code), Const.EDIT_REQ_CODE);
                    i.putExtra(context.getString(R.string.alarm_id),data.get(position).getAlarmID());
                    ((Activity) context).startActivityForResult(i, Const.EDIT_REQ_CODE);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.alarmName.setText(this.data.get(position).getAlarmName());
        holder.time.setText(this.data.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }
}
