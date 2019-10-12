package com.ghts.player.bean;

import com.ghts.player.data.TrainInfoData;
import com.ghts.player.data.TrainInfoItem;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.POS;

import java.io.Serializable;

public class TrainInfoBean implements Serializable {


    private TrainInfoItem Terminate,FirstTrain,LastTrain;

    private TrainInfoData Up,Down;

    private POS pos;
    private BGParam bgParam;


    public TrainInfoBean(){
        super();
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

    public TrainInfoItem getTerminate() {
        return Terminate;
    }

    public void setTerminate(TrainInfoItem terminate) {
        Terminate = terminate;
    }

    public TrainInfoItem getFirstTrain() {
        return FirstTrain;
    }

    public void setFirstTrain(TrainInfoItem firstTrain) {
        FirstTrain = firstTrain;
    }

    public TrainInfoItem getLastTrain() {
        return LastTrain;
    }

    public void setLastTrain(TrainInfoItem lastTrain) {
        LastTrain = lastTrain;
    }

    public TrainInfoData getUp() {
        return Up;
    }

    public void setUp(TrainInfoData up) {
        Up = up;
    }

    public TrainInfoData getDown() {
        return Down;
    }

    public void setDown(TrainInfoData down) {
        Down = down;
    }

    @Override
    public String toString() {
        return "TrainInfoBean{" +
                "Terminate=" + Terminate +
                ", FirstTrain=" + FirstTrain +
                ", LastTrain=" + LastTrain +
                ", Up=" + Up +
                ", Down=" + Down +
                ", pos=" + pos +
                ", bgParam=" + bgParam +
                '}';
    }
}
