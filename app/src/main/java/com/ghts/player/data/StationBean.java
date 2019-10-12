package com.ghts.player.data;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-9-8.
 */
public class StationBean implements Serializable {

    //    <T1 Time="1" Dst="公主坟" EnTime="1" EnDst="Gongzhufen" />

    private String time, dst, enTime, enDst;

    public String getTime() {
        return time;
    }

    public String getDst() {
        return dst;
    }

    public String getEnTime() {
        return enTime;
    }

    public String getEnDst() {
        return enDst;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public void setEnTime(String enTime) {
        this.enTime = enTime;
    }

    public void setEnDst(String enDst) {
        this.enDst = enDst;
    }

    @Override
    public String toString() {
        return "{" +
                "倒计时='" + time + '\'' +
                ",终点站='" + dst + '\'' +
                ",倒计时英文='" + enTime + '\'' +
                ",终点站英文='" + enDst + '\'' +
                '}';
    }
}
