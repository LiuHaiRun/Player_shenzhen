package com.ghts.player.data;

import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.POS;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-10-19.
 */
public class SystemBean implements Serializable {

    private String id;
    private String name;
    private POS pos;
    private BGParam bgParam;
    /**
     * 车站ATS
     */
    private TrainData train0, train1, train2, train3;
    private TrainItem curStation;
    /**
     * 车载ATS
     */
    private TrainItem strainNextStation, strainDstStation;
    /**
     * 车载ATS新增模块数据
     */
    private TrainItem strainStartStation,strainCurStation,StrianTrainStatus;

    public TrainItem getStrainDstStation() {
        return strainDstStation;
    }

    public void setStrainDstStation(TrainItem strainDstStation) {
        this.strainDstStation = strainDstStation;
    }

    public TrainItem getStrainNextStation() {
        return strainNextStation;
    }

    public void setStrainNextStation(TrainItem strainNextStation) {
        this.strainNextStation = strainNextStation;
    }

    public TrainItem getStrainStartStation() {
        return strainStartStation;
    }

    public TrainItem getStrainCurStation() {
        return strainCurStation;
    }

    public TrainItem getStrianTrainStatus() {
        return StrianTrainStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStrainStartStation(TrainItem strainStartStation) {
        this.strainStartStation = strainStartStation;
    }

    public void setStrainCurStation(TrainItem strainCurStation) {
        this.strainCurStation = strainCurStation;
    }

    public void setStrianTrainStatus(TrainItem strianTrainStatus) {
        StrianTrainStatus = strianTrainStatus;
    }

    public TrainItem getCurStation() {
        return curStation;
    }

    public void setCurStation(TrainItem curStation) {
        this.curStation = curStation;
    }

    public TrainData getTrain0() {
        return train0;
    }

    public void setTrain0(TrainData train0) {
        this.train0 = train0;
    }

    public TrainData getTrain1() {
        return train1;
    }

    public void setTrain1(TrainData train1) {
        this.train1 = train1;
    }

    public TrainData getTrain2() {
        return train2;
    }

    public void setTrain2(TrainData train2) {
        this.train2 = train2;
    }

    public TrainData getTrain3() {
        return train3;
    }

    public void setTrain3(TrainData train3) {
        this.train3 = train3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public POS getPos() {
        return pos;
    }

    public void setPos(POS pos) {
        this.pos = pos;
    }

    public BGParam getBgParam() {
        return bgParam;
    }

    public void setBgParam(BGParam bgParam) {
        this.bgParam = bgParam;
    }

    @Override
    public String toString() {
        return "SystemBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pos=" + pos +
                ", bgParam=" + bgParam +
                ", curStation=" + curStation +
                ", train0=" + train0 +
                ", train1=" + train1 +
                ", train2=" + train2 +
                ", train3=" + train3 +
                '}';
    }
}
