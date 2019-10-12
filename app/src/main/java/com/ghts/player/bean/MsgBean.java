package com.ghts.player.bean;

import java.util.List;

/**
 * Created by lijingjing on 17-4-19.
 */
public class MsgBean {
//    <Layout UpdateTime="2017-04-13 10:04:32" ResWidth="1920" ResHeight="1080" BkType="2" BkFile="">
    private String updateTime;
    private int width,height;
    private int bkType;
    private String bkFIle;
    private int msgCount,emMSgCount;
    private List<MsgModel> msgModels;
    private List<MsgModel> emMsgModels;

    public List<MsgModel> getMsgModels() {
        return msgModels;
    }

    public void setMsgModels(List<MsgModel> msgModels) {
        this.msgModels = msgModels;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBkFIle() {
        return bkFIle;
    }

    public void setBkFIle(String bkFIle) {
        this.bkFIle = bkFIle;
    }

    public int getBkType() {
        return bkType;
    }

    public void setBkType(int bkType) {
        this.bkType = bkType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getEmMSgCount() {
        return emMSgCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setEmMSgCount(int emMSgCount) {
        this.emMSgCount = emMSgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<MsgModel> getEmMsgModels() {
        return emMsgModels;
    }

    public void setEmMsgModels(List<MsgModel> emMsgModels) {
        this.emMsgModels = emMsgModels;
    }

    @Override
    public String toString() {
        return "MsgBean{" +
                "updateTime='" + updateTime + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", bkType=" + bkType +
                ", bkFIle='" + bkFIle + '\'' +
                ", msgCount=" + msgCount +
                ", emMSgCount=" + emMSgCount +
                ", msgModels=" + msgModels +
                '}';
    }
}
