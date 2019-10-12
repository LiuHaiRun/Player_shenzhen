package com.ghts.player.video;

import android.content.Context;

/**
 *
 */
public class VlcVideo extends MLocalVideo {

    private int module_type; // 模块类型

    private int zOrder; // 模块的ZOrder

    private String module_name;    //控件名

    private int currentPosition;

    public VlcVideo(Context context) {
        super(context);
        currentPosition = 0;
    }

    /* (non-Javadoc)视频尺寸和view大小一致
      */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    public int getModule_type() {
        return module_type;
    }


    public void setModule_type(int module_type) {
        this.module_type = module_type;
    }

    public int getzOrder() {
        return zOrder;
    }

    public void setzOrder(int zOrder) {
        this.zOrder = zOrder;
    }


    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;

    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}

