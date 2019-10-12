package com.ghts.player.data;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-9-10.
 */
public class StrainBean implements Serializable{

//    <Time Name=" " EnName=" "/>
//    <NextStation Name="西单" EnName="Xidan" Title="下一站" EnTitle="Next St." />
//    <DstStation Name="公主坟" EnName="Gongzhufen" Title="终点站" EnTitle="Dst St." />

    private String timeName,timeEnName;
    private String nextStationName,nextStationEnName,nextStationTitle,nextStationEnTitle;
    private String dstStationName,dstStationEnName,dstStationTitle,dstStationEnTitle;

    public String getTimeName() {
        return timeName;
    }

    public String getTimeEnName() {
        return timeEnName;
    }

    public String getNextStationName() {
        return nextStationName;
    }

    public String getNextStationEnName() {
        return nextStationEnName;
    }

    public String getDstStationName() {
        return dstStationName;
    }

    public String getDstStationEnName() {
        return dstStationEnName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    public void setTineEnName(String timeEnName) {
        this.timeEnName = timeEnName;
    }

    public void setNextStationName(String nextStationName) {
        this.nextStationName = nextStationName;
    }

    public void setNextStationEnName(String nextStationEnName) {
        this.nextStationEnName = nextStationEnName;
    }

    public void setDstStationName(String dstStationName) {
        this.dstStationName = dstStationName;
    }

    public void setDstStationEnName(String dstStationEnName) {
        this.dstStationEnName = dstStationEnName;
    }

    public String getNextStationTitle() {
        return nextStationTitle;
    }

    public String getNextStationEnTitle() {
        return nextStationEnTitle;
    }

    public String getDstStationTitle() {
        return dstStationTitle;
    }

    public String getDstStationEnTitle() {
        return dstStationEnTitle;
    }

    public void setTimeEnName(String timeEnName) {
        this.timeEnName = timeEnName;
    }

    public void setNextStationTitle(String nextStationTitle) {
        this.nextStationTitle = nextStationTitle;
    }

    public void setNextStationEnTitle(String nextStationEnTitle) {
        this.nextStationEnTitle = nextStationEnTitle;
    }

    public void setDstStationTitle(String dstStationTitle) {
        this.dstStationTitle = dstStationTitle;
    }

    public void setDstStationEnTitle(String dstStationEnTitle) {
        this.dstStationEnTitle = dstStationEnTitle;
    }

    @Override
    public String toString() {
        return "{" +
                "时间='" + timeName + '\'' +
//                ", 英文时间='" + timeEnName + '\'' +
                ", 下一站='" + nextStationName + '\'' +
//                ", 下一站英文='" + nextStationEnName + '\'' +
                ", 下一站标题='" + nextStationTitle + '\'' +
//                ", 下一站标题英文='" + nextStationEnTitle + '\'' +
                ", 终点站='" + dstStationName + '\'' +
//                ", 终点站英文='" + dstStationEnName + '\'' +
                ", 终点站标题='" + dstStationTitle + '\'' +
//                ", 终点站标题英文='" + dstStationEnTitle + '\'' +
                '}';
    }
}
