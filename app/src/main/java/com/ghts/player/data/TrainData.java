package com.ghts.player.data;

import com.ghts.player.enumType.FontParam;

/**
 * Created by lijingjing on 17-10-19.
 */
public class TrainData {

   private TrainItem timeData,nextData,dstData;
    private String id;
   private FontParam fontNum,fontTrans,fontTransEn;

    public TrainItem getTimeData() {
        return timeData;
    }

    public void setTimeData(TrainItem timeData) {
        this.timeData = timeData;
    }

    public TrainItem getNextData() {
        return nextData;
    }

    public void setNextData(TrainItem nextData) {
        this.nextData = nextData;
    }

    public TrainItem getDstData() {
        return dstData;
    }

    public void setDstData(TrainItem dstData) {
        this.dstData = dstData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFontNum(FontParam fontNum) {
        this.fontNum = fontNum;
    }

    public void setFontTrans(FontParam fontTrans) {
        this.fontTrans = fontTrans;
    }

    public void setFontTransEn(FontParam fontTransEn) {
        this.fontTransEn = fontTransEn;
    }

    public FontParam getFontNum() {
        return fontNum;
    }

    public FontParam getFontTrans() {
        return fontTrans;
    }

    public FontParam getFontTransEn() {
        return fontTransEn;
    }

    @Override
    public String toString() {
        return "TrainData{" +
                "timeData=" + timeData +
                ", nextData=" + nextData +
                ", dstData=" + dstData +
                ", id='" + id + '\'' +
                '}';
    }
}
