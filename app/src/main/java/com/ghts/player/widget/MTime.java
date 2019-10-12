package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.widget.TextView;

import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;

/**
 * Created by lijingjing on 17-8-1.
 */
public class MTime extends TextView {

    private int module_type; // 模块类型
    private int zOrder; // 模块的ZOrder
    private String module_name;    //控件名
    private MTime mTime;
    private boolean show_second;
    public boolean runFlag = true;
    private String timeStr; // 当前时间的字符串
    private Paint mPaint;

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 显示当前时间的字符串
            mTime.setText(timeStr);
//            mTime.setmText(timeStr);
        }
    };

    public MTime(Context context) {
        super(context);
    }

    // 显示当前时间的线程
    private Runnable myRunnable1 = new Runnable() {
        private long currentTimeMillis;
        private Time time = new Time();
        private String hourStr = "";
        private String minStr = "";
        private String secStr = "";

        @Override
        public void run() {
            // 获取当前时间的时，分，秒值。
            currentTimeMillis = System.currentTimeMillis();
            time.set(currentTimeMillis);
            hourStr = String.valueOf(time.hour);
            int min = time.minute;
            if (min >= 10) {
                minStr = String.valueOf(min);
            } else {
                minStr = "0" + String.valueOf(min);
            }
            int sec = time.second;
            if (sec >= 10) {
                secStr = String.valueOf(sec);
            } else {
                secStr = "0" + String.valueOf(sec);
            }
            if (show_second) { // 显示秒
                timeStr = hourStr + ":" + minStr + ":" + secStr;
            } else { // 不显示秒
                timeStr = hourStr + ":" + minStr;
            }
            // 显示当前时间的字符串
            mTime.setText(timeStr);
//            mTime.setmText(timeStr);
            Const.moduleMap.put("Time", timeStr);

            if (runFlag) {
                handler.postDelayed(this, 500);
            }
        }
    };
    /**
     * 当控件显示时自动执行的方法
     */
    @Override
    protected void onAttachedToWindow() {
        LogUtil.e("----MTime----", "--显示--");
        runFlag = true;
        handler.postDelayed(myRunnable1, 500);
        super.onAttachedToWindow();
    }

    /**
     * 当控件从界面退出时自动执行的方法
     */
    @Override
    protected void onDetachedFromWindow() {
        runFlag = false;
        super.onDetachedFromWindow();
    }

    public MTime getmTime() {
        return mTime;
    }

    public void setmTime(MTime mTime) {
        this.mTime = mTime;
    }

    public boolean isShow_second() {
        return show_second;
    }

    public void setShow_second(boolean show_second) {
        this.show_second = show_second;
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


}