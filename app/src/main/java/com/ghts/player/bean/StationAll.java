package com.ghts.player.bean;

import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.POS;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lijingjing on 17-9-11.
 */
public class StationAll implements Serializable{

    private String name;
    private POS pos;
    private BGParam bgParam;
    private ArrayList<StationViewBean> stationView;


    public String getName() {
        return name;
    }

    public POS getPos() {
        return pos;
    }

    public BGParam getBgParam() {
        return bgParam;
    }

    public ArrayList<StationViewBean> getStationView() {
        return stationView;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPos(POS pos) {
        this.pos = pos;
    }

    public void setBgParam(BGParam bgParam) {
        this.bgParam = bgParam;
    }

    public void setStationView(ArrayList<StationViewBean> stationView) {
        this.stationView = stationView;
    }
}
