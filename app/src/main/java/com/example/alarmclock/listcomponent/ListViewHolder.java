package com.example.alarmclock.listcomponent;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;

import org.w3c.dom.Text;

public class ListViewHolder extends RecyclerView.ViewHolder {
    TextView alarmName;
    TextView time;

    ListViewHolder(View itemView){
        super(itemView);
        this.alarmName = itemView.findViewById(R.id.alarmName);
        this.time = itemView.findViewById(R.id.time);
    }
}

