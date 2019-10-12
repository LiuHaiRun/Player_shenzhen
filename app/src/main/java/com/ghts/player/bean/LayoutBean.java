package com.ghts.player.bean;

import android.text.format.Time;

/**
 * Created by lijingjing on 17-5-2.
 */
public class LayoutBean {


    //    <Layout3 File="peixun.XML" StartTime="20:00:01" StopDate="364" StopTime="00:00:00" />
    private String fileName;
    private Time StartTime;
    private String StopDate;
    private Time StopTime;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setStartTime(Time startTime) {
        StartTime = startTime;
    }

    public void setStopDate(String stopDate) {
        StopDate = stopDate;
    }

    public void setStopTime(Time stopTime) {
        StopTime = stopTime;
    }

    public String getFileName() { return fileName; }

    public Time getStartTime() {
        return StartTime;
    }

    public String getStopDate() {
        return StopDate;
    }

    public Time getStopTime() {
        return StopTime;
    }
}
