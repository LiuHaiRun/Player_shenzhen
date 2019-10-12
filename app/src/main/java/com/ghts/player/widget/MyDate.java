package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghts.player.enumType.DateParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.MDShow;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowLang;
import com.ghts.player.enumType.YearShow;
import com.ghts.player.inter.WeakHandler;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.MTypefaceSpan;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.TypeFaceFactory;

import java.util.Date;

/**
 * Created by lijingjing on 17-8-2.
 */
public class MyDate extends TextView {

    private static final String TAG = "MDate";

    private int module_type; // 模块类型

    private int zOrder; // 模块的ZOrder

    private String file_version;    //文件版本

    private String module_name;    //控件名

    private int module_uid;//控件标识

    private int module_gid;//控件组标识

    // <Show_Date>1</Show_Date>
    private boolean show_date;// 是否显示

    // <Date_Lang>1</Date_Lang>
    private ShowLang Date_Lang;// 显示日期的语言

    private DateParam date_param; // 日期显示格式。

    private FontParam date_font;// 日期字体

    private boolean show_week;// 是否显示星期
    // <Week_Lang>1</Week_Lang>
    private ShowLang week_lang;// 显示星期的语言

    private FontParam week_font;// 显示星期的字体。

    private static int SpaceNum; //空格数 &#160;

    private static int weekPos; // 显示前后独立位置

    private static POS wPos, dPos;

    private MyDate mDate;

    private Thread myThread;
    private Time time;


    private final Handler handler = new DateHandler(this);

    private static class DateHandler extends WeakHandler<MyDate> {
        private SpannableString spanStr = null;
        MyDate activity = null;
        public DateHandler(MyDate owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            activity = getOwner();
            if (activity == null)
                return;
            if (activity.show_date && activity.show_week) { // 日期、星期都显示
                switch (weekPos) {
                    case 0:
                        spanStr = new SpannableString(activity.weekStr + PubUtil.getSpaceCount(SpaceNum) + activity.dateStr);
                        // 日期字符串的大小，字体，颜色
                        activity.initSpanFont(spanStr, activity.date_font, 0, activity.weekStr.length());
                        // 星期字符串的大小，字体，颜色
                        activity.initSpanFont(spanStr, activity.date_font, activity.weekStr.length(),
                                activity.dateStr.length() + activity.weekStr.length() + SpaceNum);
                        break;
                    case 1:
                        spanStr = new SpannableString(activity.dateStr + PubUtil.getSpaceCount(SpaceNum) + activity.weekStr);
                        // 日期字符串的大小，字体，颜色
                        activity.initSpanFont(spanStr, activity.date_font, 0, activity.dateStr.length());
                        // 星期字符串的大小，字体，颜色
                        activity.initSpanFont(spanStr, activity.date_font, activity.dateStr.length(),
                                activity.dateStr.length() + activity.weekStr.length() + SpaceNum);
                        break;
                    default:
                        break;
                }
                activity.mDate.setText(spanStr);
                Const.moduleMap.put("Date", spanStr.toString());
            } else if (!activity.show_date && activity.show_week) { // 只显示星期
                String weeks = activity.weekStr;
                activity.setTextFont(activity.mDate, activity.week_font, weeks);
                Const.moduleMap.put("Date", weeks);
            } else if (activity.show_date && !activity.show_week) { // 只显示日期
                String dates = activity.dateStr;
                activity.setTextFont(activity.mDate, activity.date_font, dates);
                Const.moduleMap.put("Date", dates);
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
                weekStr = getWeekStr(time);
                dateStr = getDateStr(time);
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogUtil.i(TAG, "Thread.sleep...\r\n" + e);
                }
            }
        }
    };

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

    /**
     * 设置扩展文本的字体，如大小，字体，颜色……
     *
     * @param start 扩展文本的起始位置
     * @param end   扩展文本的结束位置
     */
    void initSpanFont(SpannableString spanStr, FontParam font, int start,
                      int end) {
        // 设置大小
        spanStr.setSpan(new AbsoluteSizeSpan(font.getSize()), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        spanStr.setSpan(new MTypefaceSpan("sdifojao", typeface), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置颜色
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        spanStr.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    /**
     * 初始化控件的位置
     */
    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    /**
     * 得到星期的显示字符串。
     *
     * @param time 当前时间值对象
     * @return
     */
    String getWeekStr(Time time) {
        if (week_lang == ShowLang.CHINESE) { // 星期为中文显示
            switch (time.weekDay) {
                case 0:
                    weekStr = "星期日";
                    break;
                case 1:
                    weekStr = "星期一";
                    break;
                case 2:
                    weekStr = "星期二";
                    break;
                case 3:
                    weekStr = "星期三";
                    break;
                case 4:
                    weekStr = "星期四";
                    break;
                case 5:
                    weekStr = "星期五";
                    break;
                case 6:
                    weekStr = "星期六";
                    break;
                default:
                    break;
            }
        } else if (week_lang == ShowLang.ENGLISH) { // 星期为英文显示
            switch (time.weekDay) {
                case 0:
                    weekStr = "SUNDAY";
                    break;
                case 1:
                    weekStr = "MONDAY";
                    break;
                case 2:
                    weekStr = "TUESDAY";
                    break;
                case 3:
                    weekStr = "WEDNESDAY";
                    break;
                case 4:
                    weekStr = "THURSDAY";
                    break;
                case 5:
                    weekStr = "FRIDAY";
                    break;
                case 6:
                    weekStr = "SATURDAY";
                    break;
                default:
                    weekStr = "SUNDAY";
                    break;
            }
        }
        return weekStr;
    }

    private String int2ChineseNum(int num) {
        String chinese = "";
        switch (num) {
            case 0:
                chinese = "〇";
                break;
            case 1:
                chinese = "一";
                break;
            case 2:
                chinese = "二";
                break;
            case 3:
                chinese = "三";
                break;
            case 4:
                chinese = "四";
                break;
            case 5:
                chinese = "五";
                break;
            case 6:
                chinese = "六";
                break;
            case 7:
                chinese = "七";
                break;
            case 8:
                chinese = "八";
                break;
            case 9:
                chinese = "九";
                break;
            default:
                LogUtil.e(TAG, "int2ChineseNum default");
                break;
        }
        return chinese;
    }

    private String int2ChineseString(int num) {
        StringBuffer temBuffer = new StringBuffer();
        if (num / 1000 != 0) {//表明入参是年
            temBuffer.append(int2ChineseNum(num / 1000));
            num %= 1000;
            temBuffer.append(int2ChineseNum(num / 100));
            num %= 100;
            temBuffer.append(int2ChineseNum(num / 10));
            num %= 10;
            temBuffer.append(int2ChineseNum(num));
        } else {
            if (num / 10 != 0) {
                temBuffer.append(int2ChineseNum(num / 10));
                num %= 10;
            }
            temBuffer.append(int2ChineseNum(num));
        }
        return temBuffer.toString();
    }

    String weekStr = "";
    String dateStr = ""; // 日期字符串格式
    String yearStr = ""; // 年字符串格式
    String monthStr = ""; // 月字符串格式
    String dayStr = ""; // 日字符串格式
    String YearTip = "";
    String monthTip = "";
    String DayTip = "";

    /**
     * 得到日期的显示字符串。
     */
    String getDateStr(Time time) {
        switch (Date_Lang) {
            case CHINESE:
                yearStr = int2ChineseString(time.year);
                monthStr = int2ChineseString((time.month + 1));
                dayStr = int2ChineseString(time.monthDay);
                YearTip = date_param.getYear_tip();
                monthTip = date_param.getMonth_tip();
                DayTip = date_param.getDay_tip();
                break;
            case ENGLISH:
                String[] temp = (new Date()).toString().split(" ");
                yearStr = temp[6];
                monthStr = temp[1];
                dayStr = temp[2];
                YearTip = " ";
                monthTip = ". ";
                DayTip = " ";
                break;
            case DIGITAL:
                yearStr = String.valueOf(time.year);
                monthStr = String.valueOf((time.month + 1));
                dayStr = String.valueOf(time.monthDay);
                YearTip = date_param.getYear_tip();
                monthTip = date_param.getMonth_tip();
                DayTip = date_param.getDay_tip();
                break;
            default:
                break;
        }

        if (date_param.getYear_show() == YearShow.NOTSHOW) { // 不显示年
            YearTip = ""; // 不显示年，所以不显示年的tip
            yearStr = "";
            //			monthStr = String.valueOf((time.month + 1));
            //			dayStr = String.valueOf(time.monthDay);
            if (date_param.getMD_Show() == MDShow.NOZERO) { // 月日不加零
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }

            } else { // 月日前加零
                if (time.month < 9) { // 当月数小于10时前面加0。
                    monthStr = "0" + String.valueOf((time.month + 1));
                }
                if (time.monthDay < 10) { // 当月数小于10时前面加0。
                    dayStr = "0" + String.valueOf(time.monthDay);
                }
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }
            }

        } else if (date_param.getYear_show() == YearShow.SHOWTWO) { // 年显示两位
            if (date_param.getMD_Show() == MDShow.NOZERO) { // 月日不加零
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }

            } else { // 月日前加零
                if (time.month < 9) { // 当月数小于10时前面加0。
                    monthStr = "0" + String.valueOf((time.month + 1));
                }
                if (time.monthDay < 10) { // 当月数小于10时前面加0。
                    dayStr = "0" + String.valueOf(time.monthDay);
                }
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }
            }
        } else if (date_param.getYear_show() == YearShow.SHOWFOUR) { // 年显示四位
            if (date_param.getMD_Show() == MDShow.NOZERO || Date_Lang == ShowLang.CHINESE || Date_Lang == ShowLang.ENGLISH) { // 月日不加零
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }

            } else { // 月日前加零
                if (time.month < 9) { // 当月数小于10时前面加0。
                    monthStr = "0" + String.valueOf((time.month + 1));
                }
                if (time.monthDay < 10) { // 当月数小于10时前面加0。
                    dayStr = "0" + String.valueOf(time.monthDay);
                }
                switch (date_param.getShow_seq()) {
                    case YMD: // 年月日
                        // 最终得到的日期字符串格式。
                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
                                + DayTip;
                        break;
                    case MDY: // 月日年
                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
                                + YearTip;
                        break;
                    case DMY: // 日月年
                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
                                + YearTip;
                        break;
                }
            }
        }
        return dateStr;
    }

    public boolean isShow_date() {
        return show_date;
    }

    public void setShow_date(boolean show_date) {
        this.show_date = show_date;
    }

    public ShowLang getDate_Lang() {
        return Date_Lang;
    }

    public void setDate_Lang(ShowLang date_Lang) {
        Date_Lang = date_Lang;
    }

    public DateParam getDate_param() {
        return date_param;
    }

    public void setDate_param(DateParam date_param) {
        this.date_param = date_param;
    }

    public FontParam getDate_font() {
        return date_font;
    }

    public void setDate_font(FontParam date_font) {
        this.date_font = date_font;
    }

    public boolean isShow_week() {
        return show_week;
    }

    public void setShow_week(boolean show_week) {
        this.show_week = show_week;
    }

    public ShowLang getWeek_lang() {
        return week_lang;
    }

    public void setWeek_lang(ShowLang week_lang) {
        this.week_lang = week_lang;
    }

    public FontParam getWeek_font() {
        return week_font;
    }

    public void setWeek_font(FontParam week_font) {
        this.week_font = week_font;
    }

    public MyDate getmDate() {
        return mDate;
    }

    public void setmDate(MyDate mDate) {
        this.mDate = mDate;
    }

    public MyDate(Context context) {
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

    public String getFile_version() {
        return file_version;
    }

    public void setFile_version(String file_version) {
        this.file_version = file_version;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public int getModule_uid() {
        return module_uid;
    }

    public void setModule_uid(int module_uid) {
        this.module_uid = module_uid;
    }

    public int getModule_gid() {
        return module_gid;
    }

    public void setModule_gid(int module_gid) {
        this.module_gid = module_gid;
    }

    public int getSpaceNum() {
        return SpaceNum;
    }

    public void setSpaceNum(int spaceNum) {
        SpaceNum = spaceNum;
    }

    public int getWeekPos() {
        return weekPos;
    }

    public void setWeekPos(int weekPos) {
        this.weekPos = weekPos;
    }

    public POS getwPos() {
        return wPos;
    }

    public void setwPos(POS wPos) {
        this.wPos = wPos;
    }

    public POS getdPos() {
        return dPos;
    }

    public void setdPos(POS dPos) {
        this.dPos = dPos;
    }
}
//package com.ghts.player.widget;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Handler;
//import android.os.Message;
//import android.text.SpannableString;
//import android.text.Spanned;
//import android.text.format.Time;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.ForegroundColorSpan;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.ghts.player.enumType.DateParam;
//import com.ghts.player.enumType.FontParam;
//import com.ghts.player.enumType.MDShow;
//import com.ghts.player.enumType.POS;
//import com.ghts.player.enumType.RGBA;
//import com.ghts.player.enumType.ShowLang;
//import com.ghts.player.enumType.YearShow;
//import com.ghts.player.inter.WeakHandler;
//import com.ghts.player.utils.Const;
//import com.ghts.player.utils.LogUtil;
//import com.ghts.player.utils.MTypefaceSpan;
//import com.ghts.player.utils.PubUtil;
//import com.ghts.player.utils.TypeFaceFactory;
//
//import java.util.Date;
//
///**
// * Created by lijingjing on 17-8-2.
// */
//public class MyDate extends TextView {
//    private static final String TAG = "MDate";
//
//    private int module_type; // 模块类型
//
//    private int zOrder; // 模块的ZOrder
//
//    private String file_version;    //文件版本
//
//    private String module_name;    //控件名
//
//    private int module_uid;//控件标识
//
//    private int module_gid;//控件组标识
//
//    // <Show_Date>1</Show_Date>
//    private boolean show_date;// 是否显示
//
//    // <Date_Lang>1</Date_Lang>
//    private ShowLang Date_Lang;// 显示日期的语言
//
//    private DateParam date_param; // 日期显示格式。
//
//    private FontParam date_font;// 日期字体
//
//    private boolean show_week;// 是否显示星期
//    // <Week_Lang>1</Week_Lang>
//    private ShowLang week_lang;// 显示星期的语言
//
//    private FontParam week_font;// 显示星期的字体。
//
//    private static int SpaceNum; //空格数 &#160;
//
//    private static int weekPos; // 显示前后独立位置
//
//    private static POS wPos,dPos;
//
//    private MyDate mDate;
//
//    private Thread myThread;
//    private Time time;
//
//    private final Handler handler = new DateHandler(this);
//    private static class DateHandler extends WeakHandler<MyDate> {
//        private SpannableString spanStr = null;
//        MyDate activity = null;
//        public DateHandler(MyDate owner) {
//            super(owner);
//        }
//        @Override
//        public void handleMessage(Message msg) {
//            activity = getOwner();
//            if (activity == null)
//                return;
//            if (activity.show_date && activity.show_week) { // 日期、星期都显示
//                switch (weekPos) {
//                    case 0:
//                        spanStr = new SpannableString(activity.weekStr + PubUtil.getSpaceCount(SpaceNum) + activity.dateStr);
//                        // 日期字符串的大小，字体，颜色
//                        activity.initSpanFont(spanStr, activity.week_font, 0, activity.weekStr.length());
//                        // 星期字符串的大小，字体，颜色
//                        activity.initSpanFont(spanStr, activity.date_font, activity.weekStr.length(),
//                                activity.dateStr.length() + activity.weekStr.length()+SpaceNum);
//                        break;
//                    case 1:
//                        spanStr = new SpannableString(activity.dateStr + PubUtil.getSpaceCount(SpaceNum) + activity.weekStr);
//                        // 日期字符串的大小，字体，颜色
//                        activity.initSpanFont(spanStr, activity.date_font, 0, activity.dateStr.length());
//                        // 星期字符串的大小，字体，颜色
//                        activity.initSpanFont(spanStr, activity.week_font, activity.dateStr.length(),
//                                activity.dateStr.length() + activity.weekStr.length()+SpaceNum);
//                        break;
//                    default:
//                        break;
//                }
//                activity.mDate.setText(spanStr);
//                Const.moduleMap.put("Date", spanStr.toString());
//            } else if (!activity.show_date && activity.show_week) { // 只显示星期
//                activity.setTextFont(activity.mDate, activity.week_font, activity.weekStr);
//                Const.moduleMap.put("Date", activity.weekStr);
//            } else if (activity.show_date && !activity.show_week) { // 只显示日期
//                activity.setTextFont(activity.mDate, activity.date_font, activity.dateStr);
//                Const.moduleMap.put("Date", activity.dateStr);
//            }
//            super.handleMessage(msg);
//        }
//    }
//
//    /**
//     * 得到当前星期日期的线程
//     */
//    private Runnable myRunnable = new Runnable() {
//        @Override
//        public void run() {
//            while (!myThread.isInterrupted()) {
//                time.set(System.currentTimeMillis());
//                time = new Time();
//
//                weekStr = getWeekStr(time);
//                weekStr = PubUtil.getSpaceCount(getSpaceNum()) + weekStr;
//                dateStr = getDateStr(time);
//
//                handler.sendEmptyMessage(0);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    LogUtil.i(TAG, "Thread.sleep...\r\n" + e);
//                }
//            }
//        }
//    };
//
//    /**
//     * 当控件显示时自动执行
//     */
//    @Override
//    protected void onAttachedToWindow() {
//        myThread = new Thread(myRunnable);
//        myThread.setName("MDateThread");
//        myThread.start();
//        super.onAttachedToWindow();
//    }
//
//
//    /**
//     * 当控件从界面退出时自动执行
//     */
//    @Override
//    protected void onDetachedFromWindow() {
//        myThread.interrupt();
//        super.onDetachedFromWindow();
//    }
//
//    void setTextFont(TextView tv, FontParam font, String text) {
//        tv.setText(text);
//        // 大小
//        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,font.getSize());
//        // 字体
//        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
//        tv.setTypeface(typeface);
//        // 颜色
//        RGBA rgba = font.getFaceColor();
//        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
//                rgba.getBlue());
//        tv.setTextColor(color);
//    }
//
//    /**
//     * 设置扩展文本的字体，如大小，字体，颜色……
//     *
//     * @param start 扩展文本的起始位置
//     * @param end   扩展文本的结束位置
//     */
//    void initSpanFont(SpannableString spanStr, FontParam font, int start,
//                      int end) {
//        // 设置大小
//        spanStr.setSpan(new AbsoluteSizeSpan(font.getSize()), start, end,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // 设置字体
//        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
//        spanStr.setSpan(new MTypefaceSpan("sdifojao", typeface), start, end,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        // 设置颜色
//        RGBA rgba = font.getFaceColor();
//        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
//                rgba.getBlue());
//        spanStr.setSpan(new ForegroundColorSpan(color), start, end,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//    }
//
//    /**
//     * 初始化控件的位置
//     */
//    void initPosition(View view, POS pos) {
//        view.setX(pos.getLeft());
//        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
//        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
//    }
//    /**
//     * 得到星期的显示字符串。
//     *
//     * @param time 当前时间值对象
//     * @return
//     */
//    String getWeekStr(Time time) {
//        if (week_lang == ShowLang.CHINESE) { // 星期为中文显示
//            switch (time.weekDay) {
//                case 0:
//                    weekStr = "星期日";
//                    break;
//                case 1:
//                    weekStr = "星期一";
//                    break;
//                case 2:
//                    weekStr = "星期二";
//                    break;
//                case 3:
//                    weekStr = "星期三";
//                    break;
//                case 4:
//                    weekStr = "星期四";
//                    break;
//                case 5:
//                    weekStr = "星期五";
//                    break;
//                case 6:
//                    weekStr = "星期六";
//                    break;
//                default:
//                    break;
//            }
//        } else if (week_lang == ShowLang.ENGLISH) { // 星期为英文显示
//            switch (time.weekDay) {
//                case 0:
//                    weekStr = "SUNDAY";
//                    break;
//                case 1:
//                    weekStr = "MONDAY";
//                    break;
//                case 2:
//                    weekStr = "TUESDAY";
//                    break;
//                case 3:
//                    weekStr = "WEDNESDAY";
//                    break;
//                case 4:
//                    weekStr = "THURSDAY";
//                    break;
//                case 5:
//                    weekStr = "FRIDAY";
//                    break;
//                case 6:
//                    weekStr = "SATURDAY";
//                    break;
//                default:
//                    weekStr = "SUNDAY";
//                    break;
//            }
//        }
//        return weekStr;
//    }
//
//    private String int2ChineseNum(int num) {
//        String chinese = "";
//        switch (num) {
//            case 0:
//                chinese = "〇";
//                break;
//            case 1:
//                chinese = "一";
//                break;
//            case 2:
//                chinese = "二";
//                break;
//            case 3:
//                chinese = "三";
//                break;
//            case 4:
//                chinese = "四";
//                break;
//            case 5:
//                chinese = "五";
//                break;
//            case 6:
//                chinese = "六";
//                break;
//            case 7:
//                chinese = "七";
//                break;
//            case 8:
//                chinese = "八";
//                break;
//            case 9:
//                chinese = "九";
//                break;
//            default:
//                LogUtil.e(TAG, "int2ChineseNum default");
//                break;
//        }
//        return chinese;
//    }
//
//    private String int2ChineseString(int num) {
//        StringBuffer temBuffer = new StringBuffer();
//        if (num / 1000 != 0) {//表明入参是年
//            temBuffer.append(int2ChineseNum(num / 1000));
//            num %= 1000;
//            temBuffer.append(int2ChineseNum(num / 100));
//            num %= 100;
//            temBuffer.append(int2ChineseNum(num / 10));
//            num %= 10;
//            temBuffer.append(int2ChineseNum(num));
//        } else {
//            if (num / 10 != 0) {
//                temBuffer.append(int2ChineseNum(num / 10));
//                num %= 10;
//            }
//            temBuffer.append(int2ChineseNum(num));
//        }
//        return temBuffer.toString();
//    }
//
//    String weekStr = "";
//    String dateStr = ""; // 日期字符串格式
//    String yearStr = ""; // 年字符串格式
//    String monthStr = ""; // 月字符串格式
//    String dayStr = ""; // 日字符串格式
//    String YearTip = "";
//    String monthTip = "";
//    String DayTip = "";
//    /**
//     * 得到日期的显示字符串。
//     */
//    String getDateStr(Time time) {
//        switch (Date_Lang) {
//            case CHINESE:
//                yearStr = int2ChineseString(time.year);
//                monthStr = int2ChineseString((time.month + 1));
//                dayStr = int2ChineseString(time.monthDay);
//                YearTip = date_param.getYear_tip();
//                monthTip = date_param.getMonth_tip();
//                DayTip = date_param.getDay_tip();
//                break;
//            case ENGLISH:
//                String[] temp = (new Date()).toString().split(" ");
//                yearStr = temp[6];
//                monthStr = temp[1];
//                dayStr = temp[2];
//                YearTip = " ";
//                monthTip = ". ";
//                DayTip = " ";
//                break;
//            case DIGITAL:
//                yearStr = String.valueOf(time.year);
//                monthStr = String.valueOf((time.month + 1));
//                dayStr = String.valueOf(time.monthDay);
//                YearTip = date_param.getYear_tip();
//                monthTip = date_param.getMonth_tip();
//                DayTip = date_param.getDay_tip();
//                break;
//            default:
//                break;
//        }
//
//        if (date_param.getYear_show() == YearShow.NOTSHOW) { // 不显示年
//            YearTip = ""; // 不显示年，所以不显示年的tip
//            yearStr = "";
//            //			monthStr = String.valueOf((time.month + 1));
//            //			dayStr = String.valueOf(time.monthDay);
//            if (date_param.getMD_Show() == MDShow.NOZERO) { // 月日不加零
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//
//            } else { // 月日前加零
//                if (time.month < 9) { // 当月数小于10时前面加0。
//                    monthStr = "0" + String.valueOf((time.month + 1));
//                }
//                if (time.monthDay < 10) { // 当月数小于10时前面加0。
//                    dayStr = "0" + String.valueOf(time.monthDay);
//                }
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//            }
//
//        } else if (date_param.getYear_show() == YearShow.SHOWTWO) { // 年显示两位
//            if (date_param.getMD_Show() == MDShow.NOZERO) { // 月日不加零
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//
//            } else { // 月日前加零
//                if (time.month < 9) { // 当月数小于10时前面加0。
//                    monthStr = "0" + String.valueOf((time.month + 1));
//                }
//                if (time.monthDay < 10) { // 当月数小于10时前面加0。
//                    dayStr = "0" + String.valueOf(time.monthDay);
//                }
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//            }
//        } else if (date_param.getYear_show() == YearShow.SHOWFOUR) { // 年显示四位
//            if (date_param.getMD_Show() == MDShow.NOZERO || Date_Lang == ShowLang.CHINESE || Date_Lang == ShowLang.ENGLISH) { // 月日不加零
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//
//            } else { // 月日前加零
//                if (time.month < 9) { // 当月数小于10时前面加0。
//                    monthStr = "0" + String.valueOf((time.month + 1));
//                }
//                if (time.monthDay < 10) { // 当月数小于10时前面加0。
//                    dayStr = "0" + String.valueOf(time.monthDay);
//                }
//                switch (date_param.getShow_seq()) {
//                    case YMD: // 年月日
//                        // 最终得到的日期字符串格式。
//                        dateStr = yearStr + YearTip + monthStr + monthTip + dayStr
//                                + DayTip;
//                        break;
//                    case MDY: // 月日年
//                        dateStr = monthStr + monthTip + dayStr + DayTip + yearStr
//                                + YearTip;
//                        break;
//                    case DMY: // 日月年
//                        dateStr = dayStr + DayTip + monthStr + monthTip + yearStr
//                                + YearTip;
//                        break;
//                }
//            }
//        }
//        return dateStr;
//    }
//
//    public boolean isShow_date() {
//        return show_date;
//    }
//
//    public void setShow_date(boolean show_date) {
//        this.show_date = show_date;
//    }
//
//    public ShowLang getDate_Lang() {
//        return Date_Lang;
//    }
//
//    public void setDate_Lang(ShowLang date_Lang) {
//        Date_Lang = date_Lang;
//    }
//
//    public DateParam getDate_param() {
//        return date_param;
//    }
//
//    public void setDate_param(DateParam date_param) {
//        this.date_param = date_param;
//    }
//
//    public FontParam getDate_font() {
//        return date_font;
//    }
//
//    public void setDate_font(FontParam date_font) {
//        this.date_font = date_font;
//    }
//
//    public boolean isShow_week() {
//        return show_week;
//    }
//
//    public void setShow_week(boolean show_week) {
//        this.show_week = show_week;
//    }
//
//    public ShowLang getWeek_lang() {
//        return week_lang;
//    }
//
//    public void setWeek_lang(ShowLang week_lang) {
//        this.week_lang = week_lang;
//    }
//
//    public FontParam getWeek_font() {
//        return week_font;
//    }
//
//    public void setWeek_font(FontParam week_font) {
//        this.week_font = week_font;
//    }
//
//    public MyDate getmDate() {
//        return mDate;
//    }
//
//    public void setmDate(MyDate mDate) {
//        this.mDate = mDate;
//    }
//
//    public MyDate(Context context) {
//        super(context);
//    }
//
//    public int getModule_type() {
//        return module_type;
//    }
//
//    public void setModule_type(int module_type) {
//        this.module_type = module_type;
//    }
//
//    public int getzOrder() {
//        return zOrder;
//    }
//
//    public void setzOrder(int zOrder) {
//        this.zOrder = zOrder;
//    }
//
//    public String getFile_version() {
//        return file_version;
//    }
//
//    public void setFile_version(String file_version) {
//        this.file_version = file_version;
//    }
//
//    public String getModule_name() {
//        return module_name;
//    }
//
//    public void setModule_name(String module_name) {
//        this.module_name = module_name;
//    }
//
//    public int getModule_uid() {
//        return module_uid;
//    }
//
//    public void setModule_uid(int module_uid) {
//        this.module_uid = module_uid;
//    }
//
//    public int getModule_gid() {
//        return module_gid;
//    }
//
//    public void setModule_gid(int module_gid) {
//        this.module_gid = module_gid;
//    }
//
//    public int getSpaceNum() {
//        return SpaceNum;
//    }
//
//    public void setSpaceNum(int spaceNum) {
//        SpaceNum = spaceNum;
//    }
//
//    public int getWeekPos() {
//        return weekPos;
//    }
//
//    public void setWeekPos(int weekPos) {
//        this.weekPos = weekPos;
//    }
//
//    public POS getwPos() {
//        return wPos;
//    }
//
//    public void setwPos(POS wPos) {
//        this.wPos = wPos;
//    }
//
//    public POS getdPos() {
//        return dPos;
//    }
//
//    public void setdPos(POS dPos) {
//        this.dPos = dPos;
//    }
//}