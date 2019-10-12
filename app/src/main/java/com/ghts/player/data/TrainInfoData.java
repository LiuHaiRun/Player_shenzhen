package com.ghts.player.data;

public class TrainInfoData {
    private String id;
    private TrainInfoItem Terminate,FirstTrain,LastTrain;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "TrainInfoData{" +
                "id='" + id + '\'' +
                ", Terminate=" + Terminate +
                ", FirstTrain=" + FirstTrain +
                ", LastTrain=" + LastTrain +
                '}';
    }
}
