package com.ghts.player.bean;

import java.util.List;

/**
 * Created by lijingjing on 17-5-31.
 * 视频模块
 */
public class PlayerBean {

    private String name,x,y,w,h,fullScreen,channel,outputMode,type,liveName,file,left,right;
    private List<LocalPlayItem> localPlayItems;

    public List<LocalPlayItem> getLocalPlayItems() {
        return localPlayItems;
    }
    public void setLocalPlayItems(List<LocalPlayItem> localPlayItems) {
        this.localPlayItems = localPlayItems;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setW(String w) {
        this.w = w;
    }

    public void setH(String h) {
        this.h = h;
    }

    public void setFullScreen(String fullScreen) {
        this.fullScreen = fullScreen;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setOutputMode(String outputMode) {
        this.outputMode = outputMode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getName() {

        return name;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getW() {
        return w;
    }

    public String getH() {
        return h;
    }

    public String getFullScreen() {
        return fullScreen;
    }

    public String getChannel() {
        return channel;
    }

    public String getOutputMode() {
        return outputMode;
    }

    public String getType() {
        return type;
    }

    public String getLiveName() {
        return liveName;
    }

    public String getFile() {
        return file;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "PlayerBean{" +
                "name='" + name + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", w='" + w + '\'' +
                ", h='" + h + '\'' +
                ", fullScreen='" + fullScreen + '\'' +
                ", channel='" + channel + '\'' +
                ", outputMode='" + outputMode + '\'' +
                ", type='" + type + '\'' +
                ", liveName='" + liveName + '\'' +
                ", file='" + file + '\'' +
                ", left='" + left + '\'' +
                ", right='" + right + '\'' +
                ", localPlayItems=" + localPlayItems +
                '}';
    }
}
