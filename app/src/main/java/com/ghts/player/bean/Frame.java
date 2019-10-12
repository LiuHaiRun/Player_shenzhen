package com.ghts.player.bean;

/**
 * 表示视频帧数据
 */
public class Frame {

    private int hour;	//时
    private int minute;	//分
    private int sec;	//秒
    private int frame;	//帧

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    /**
     * 将帧转化为毫秒的单位，便于设置视频开始入点。
     *
     * @param frameRate  帧率
     *
     * @return 帧对应的毫秒值
     */
    public int toMillisecond(int frameRate) {

        return hour * 60 * 60 * 1000 + minute * 60 * 1000 + sec * 1000 + 1000
                * frame / frameRate;
    }


}
