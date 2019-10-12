package com.ghts.player.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author ljj
 * @des 2019-1-16
 */
public class AlarmBean implements Serializable{

    private String upDate;
    private int num;
    private ArrayList<WeatherBean> zones;

    public AlarmBean() {

    }
    public AlarmBean(String upDate, int num, ArrayList<WeatherBean> zones) {
        this.upDate = upDate;
        this.num = num;
        this.zones = zones;
    }
    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
    public ArrayList<WeatherBean> getZones() {
        return zones;
    }
    public void setZones(ArrayList<WeatherBean> zones) {
        this.zones = zones;
    }
    @Override
    public String toString() {
        return "AlarmBean{" +
                "upDate=" + upDate +
                ", num=" + num +
                ", zones=" + zones +
                '}';
    }
}
