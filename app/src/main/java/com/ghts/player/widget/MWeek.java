package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.widget.TextView;

import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowFontType;
import com.ghts.player.inter.WeakHandler;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.TypeFaceFactory;

import java.util.HashMap;

public class MWeek extends TextView {
    private static final String TAG = "MWeek";
    private int module_type; // 模块类型
    private int zOrder; // 模块的ZOrder
    private String file_version;    //文件版本
    private String module_name;    //控件名
    private ShowFontType mShowFontType; //显示模式
    private FontParam week_font;// 显示星期的字体
    private FontParam week_font_EN;// 显示星期的英文字体
    private int interval; //轮训间隔 单位：秒
    private HashMap<Integer, String> weekC;
    private HashMap<Integer, String> weekEn;
    private Time time;
    private String weekStr, weekEnStr;
    private MWeek mWeek;
    private Thread myThread;
    private boolean isChStr = false;
    private final Handler handler = new WeekHandler(this);

    private class WeekHandler extends WeakHandler<MWeek> {
        MWeek activity = null;

        public WeekHandler(MWeek owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            activity = getOwner();
            if (activity == null)
                return;
            if (mShowFontType == ShowFontType.CHINESE) { // 星期为中文显示
                activity.setTextFont(activity.mWeek, activity.week_font, activity.weekStr);
                Const.moduleMap.put("Week", activity.weekStr);
            } else if (mShowFontType == ShowFontType.ENGLISH) {
                activity.setTextFont(activity.mWeek, activity.week_font_EN, activity.weekEnStr);
                Const.moduleMap.put("Week", activity.weekEnStr);
            } else if (mShowFontType == ShowFontType.DIGITAL) {
                if (interval > 0) {
                    if (isChStr) {
                        activity.setTextFont(activity.mWeek, activity.week_font, activity.weekStr);
                        isChStr = false;
                        Const.moduleMap.put("Week", activity.weekStr);
                    } else {
                        activity.setTextFont(activity.mWeek, activity.week_font_EN, activity.weekEnStr);
                        isChStr = true;
                        Const.moduleMap.put("Week", activity.weekEnStr);
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 得到当前星期日期的线程
     */
    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            while (!myThread.isInterrupted()) {
                time = new Time();
                time.set(System.currentTimeMillis());
                getWeekStr(time);
                handler.sendEmptyMessage(0);
                if (mShowFontType == ShowFontType.DIGITAL && interval > 0) {
                    try {
                        Thread.sleep(1000 * interval);
                    } catch (InterruptedException e) {
                        LogUtil.i(TAG, "Thread.sleep...\r\n" + e);
                    }
                } else {
                    try {
                        Thread.sleep(1000*60*10);
                    } catch (InterruptedException e) {
                        LogUtil.i(TAG, "Thread.sleep...\r\n" + e);
                    }
                }
            }
        }
    };

    private void getWeekStr(Time time) {
        if (mShowFontType == ShowFontType.CHINESE) { // 星期为中文显示
            switch (time.weekDay) {
                case 0:
                    weekStr = weekC.get(0);
                    break;
                case 1:
                    weekStr = weekC.get(1);
                    break;
                case 2:
                    weekStr = weekC.get(2);
                    break;
                case 3:
                    weekStr = weekC.get(3);
                    break;
                case 4:
                    weekStr = weekC.get(4);
                    break;
                case 5:
                    weekStr = weekC.get(5);
                    break;
                case 6:
                    weekStr = weekC.get(6);
                    break;
                default:
                    break;
            }
        } else if (mShowFontType == ShowFontType.ENGLISH) { // 星期为英文显示
            switch (time.weekDay) {
                case 0:
                    weekEnStr = weekEn.get(0);
                    break;
                case 1:
                    weekEnStr = weekEn.get(1);
                    break;
                case 2:
                    weekEnStr = weekEn.get(2);
                    break;
                case 3:
                    weekEnStr = weekEn.get(3);
                    break;
                case 4:
                    weekEnStr = weekEn.get(4);
                    break;
                case 5:
                    weekEnStr = weekEn.get(5);
                    break;
                case 6:
                    weekEnStr = weekEn.get(6);
                    break;
                default:
                    break;
            }
        } else if (mShowFontType == ShowFontType.DIGITAL) {
            switch (time.weekDay) {
                case 0:
                    weekStr = weekC.get(0);
                    weekEnStr = weekEn.get(0);
                    break;
                case 1:
                    weekStr = weekC.get(1);
                    weekEnStr = weekEn.get(1);
                    break;
                case 2:
                    weekStr = weekC.get(2);
                    weekEnStr = weekEn.get(2);
                    break;
                case 3:
                    weekStr = weekC.get(3);
                    weekEnStr = weekEn.get(3);
                    break;
                case 4:
                    weekStr = weekC.get(4);
                    weekEnStr = weekEn.get(4);
                    break;
                case 5:
                    weekStr = weekC.get(5);
                    weekEnStr = weekEn.get(5);
                    break;
                case 6:
                    weekStr = weekC.get(6);
                    weekEnStr = weekEn.get(6);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 当控件显示时自动执行
     */
    @Override
    protected void onAttachedToWindow() {
        myThread = new Thread(myRunnable);
        myThread.setName("MDateThread");
        myThread.start();
        super.onAttachedToWindow();
    }


    /**
     * 当控件从界面退出时自动执行
     */
    @Override
    protected void onDetachedFromWindow() {
        myThread.interrupt();
        super.onDetachedFromWindow();
    }

    void setTextFont(TextView tv, FontParam font, String text) {
        tv.setText(text);
        // 大小
        tv.setTextSize(font.getSize());
        // 字体
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        // 颜色
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);
    }

    public void setShowFontType(ShowFontType showFontType) {
        mShowFontType = showFontType;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setWeekC(HashMap<Integer, String> weekC) {
        this.weekC = weekC;
    }

    public void setWeekEn(HashMap<Integer, String> weekEn) {
        this.weekEn = weekEn;
    }

    public void setWeek_font(FontParam week_font) {
        this.week_font = week_font;
    }


    public MWeek(Context context) {
        super(context);
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

    public MWeek getWeek() {
        return mWeek;
    }

    public void setWeek(MWeek week) {
        mWeek = week;
    }

    public void setWeek_font_EN(FontParam week_font_EN) {
        this.week_font_EN = week_font_EN;
    }

}
