package com.ghts.player.bean;

/**
 * Created by lijingjing on 17-4-25.
 * 实体类屏幕的宽高ScreenBean,保存视频宽高:
 */
public class ScreenBean {
    private int sWidth;
    private int sHeight;

    public ScreenBean(int sWidth, int sHeight) {
        super();
        this.sWidth = sWidth;
        this.sHeight = sHeight;
    }
    public int getsWidth() {
        return sWidth;
    }
    public void setsWidth(int sWidth) {
        this.sWidth = sWidth;
    }
    public int getsHeight() {
        return sHeight;
    }
    public void setsHeight(int sHeight) {
        this.sHeight = sHeight;
    }
}
