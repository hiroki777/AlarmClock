package com.example.alarmclock.listcomponent;

import java.util.Date;

public class ListItem {

    private int alarmID = -1;
    private String alarmName = null;
    private String time = null;

    public String getAlarmName() {
        return alarmName;
    }

    public String getTime() {
        return time;
    }

    public String getHour(){
        return getTime().substring(0,2);
    }

    public String getMinitsu(){
        return getTime().substring(3,5);
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAlarmID(int alarmID) {
        this.alarmID = alarmID;
    }

    public int getAlarmID() {
        return alarmID;
    }
}
