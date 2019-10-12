package com.ghts.player.bean;

import java.io.File;

/**
 *本地视频列表
 */
public class LocalPlayItem {

    private File file;// 播放的视频文件
    private Frame start_frame;//开始时间
    private Frame in_point;	//开始位置
    private Frame play_length;	//播放时长
    private int volume;	//播放音量

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Frame getStart_frame() {
        return start_frame;
    }

    public void setStart_frame(Frame start_frame) {
        this.start_frame = start_frame;
    }

    public Frame getIn_point() {
        return in_point;
    }

    public void setIn_point(Frame in_point) {
        this.in_point = in_point;
    }

    public Frame getPlay_length() {
        return play_length;
    }

    public void setPlay_length(Frame play_length) {
        this.play_length = play_length;
    }


}
