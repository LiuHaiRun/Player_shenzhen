package com.ghts.player.bean;

import android.text.format.Time;

/**
 * Created by lijingjing on 17-9-21.
 */
public class TaskBean {

    private String enable,time_limit,content;
    private Time time;

    public String getEnable() {
        return enable;
    }

    public Time getTime() {
        return time;
    }

    public String getTime_limit() {
        return time_limit;
    }

    public String getContent() {
        return content;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setTime_limit(String time_limit) {
        this.time_limit = time_limit;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "enable='" + enable + '\'' +
                ", time='" + time + '\'' +
                ", time_limit='" + time_limit + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
