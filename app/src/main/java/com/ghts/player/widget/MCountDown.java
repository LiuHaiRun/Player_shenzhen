package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowLang;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.MTypefaceSpan;
import com.ghts.player.utils.TypeFaceFactory;

/**
 * Created by lijingjing on 17-8-9.
 */
public class MCountDown extends TextView {
    private static final String TAG="MCountDown";
    private int module_type; // 模块类型

    private int zOrder; // 模块的ZOrder

    private String file_version;	//文件版本

    private String module_name;	//控件名

    private int module_uid;//控件标识

    private int module_gid;//控件组标识

    private boolean runFlag = true;
    private Thread myThread;
    private SpannableString spanStr;
    private String str_tip;
    private String str_min;
    private String str_sec;
    private ShowLang show_lang;
    private long time_countdown;
    private FontParam time_font;
    private MCountDown mCountDown;
    private boolean show_second;
    private FontParam tip_font;
    private int show_interval;// 中英文切换时间
//    private ArrayList<ConvertItem> convertItem_list;// 即将到达转义项集合
    private FontParam convert_font;// 转义字体
    private Time countDownMillis = new Time();


//    private Handler handler = new Handler() {
//        private int interval = 0;
//        private ConvertItem convertItem;
//        private ConvertItem last_convertItem; // 上一个转义项
//
//        @Override
//        public void handleMessage(Message msg) {
//            // 改变中英文显示
//            if (interval >= show_interval) {
//                if (show_lang == ShowLang.ENGLISH) {
//                    show_lang = ShowLang.CHINESE;
//                } else {
//                    show_lang = ShowLang.ENGLISH;
//                }
//                interval = 0;
//            }
//            interval++;
//
//            // 判断是否显示转义内容
//            ListIterator<ConvertItem> iterator = convertItem_list
//                    .listIterator();
//            if (iterator.hasNext()) {
//                convertItem = iterator.next();
//                last_convertItem = convertItem; // 记录上一个转义项
//                if (countDownMillis.toMillis(false) < convertItem.getTime() * 1000) { // 当倒计时时间小于第一个转义时间项
//                    // 设置显示的文本及其字体
//                    while (iterator.hasNext()) {
//                        convertItem = iterator.next();
//                        if (countDownMillis.toMillis(false) < convertItem
//                                .getTime() * 1000) { // 当倒计时时间小于第一个转义时间项时停止
//                            last_convertItem = convertItem; // 记录上一个转义项
//                        } else {
//                            break;
//                        }
//                    }
//                    String content = last_convertItem.getContent();
//                    setTextFont(mCountDown, convert_font, content);
//                } else { // 不转义，正常显示倒计时时间
//                    if (show_second) { // 显示秒数
//                        showSec(mCountDown);
//                    } else { // 不显示秒数
//                        showMin(mCountDown);
//                    }
//                }
//            }
//            super.handleMessage(msg);
//        }
//    };

    /**
     * 设置TextView的文本和字体
     * @param tv	文本控件
     * @param font	字体
     * @param text	文本
     */
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

    public MCountDown(Context context) {
        super(context);

    }
    //倒计时的线程
    private Runnable myRunnable = new Runnable() {
        public void run() {
            while (runFlag) {

                if(time_countdown>0){
                    // 倒计时时间减一秒。
                    time_countdown -= 1000;
                    if (time_countdown <= 0) { // 倒计时为零后重新计时
//                        time_countdown = ConstantValue.COUNTDOWNTIME;


                    }
                    // 将倒计时的毫秒值转成Time对象值
                    countDownMillis.set(time_countdown);
//                    handler.sendEmptyMessage(0);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogUtil.i(TAG, "InterruptedException...\r\n" + e);
                }
            }
        }
    };

    /**
     * 只显示分钟数的倒计时
     * @param mCountDown 倒计时控件
     */
    void showMin(MCountDown mCountDown) {
        // 倒计时的分钟数
        int minute = (Integer) countDownMillis.minute;

        if (show_lang == ShowLang.CHINESE) { // 显示中文
            str_tip = "下次列车:";
            str_min = "分";
        } else if (show_lang == ShowLang.ENGLISH) { // 显示英文
            str_tip = "NextTrain:";
            str_min = "min";
        }
        spanStr = new SpannableString(str_tip + "\n" + minute + str_min);

        int minStrStart = str_tip.length() + 1
                + String.valueOf(minute).length(); // 分钟字符串的起始位置

        // 设置提示文字字体
        initSpanFont(spanStr, tip_font, 0, str_tip.length());

        // 换行
        spanStr.setSpan(new AlignmentSpan() {
                            @Override
                            public Layout.Alignment getAlignment() {
                                return Layout.Alignment.ALIGN_OPPOSITE;
                            }
                        }, str_tip.length() + 1, minStrStart + str_min.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置分钟数文本字体
        initSpanFont(spanStr, time_font, str_tip.length() + 1, minStrStart);

        // 设置分钟字符串字体
        initSpanFont(spanStr, tip_font, minStrStart,
                minStrStart + str_min.length());

        mCountDown.setText(spanStr);
    }

    /**
     * 倒计时需要显示秒数
     * @param mCountDown 倒计时控件
     */
    void showSec(MCountDown mCountDown) {
        // 倒计时的分钟数
        int minute = (Integer) countDownMillis.minute;
        // 倒计时的秒数
        int second = (Integer) countDownMillis.second;

        if (show_lang == ShowLang.CHINESE) { // 显示中文
            str_tip = "下次列车:";
            str_min = "分";
            str_sec = "秒";
        } else if (show_lang == ShowLang.ENGLISH) { // 显示英文
            str_tip = "NextTrain:";
            str_min = "min";
            str_sec = "sec";
        }
        spanStr = new SpannableString(str_tip + "\n" + minute + str_min
                + second + str_sec);

        int minStrStart = str_tip.length() + 1
                + String.valueOf(minute).length(); // 分钟字符串的起始位置
        int secStrStart = minStrStart + str_min.length()
                + String.valueOf(second).length(); // 秒钟字符串的起始位置

        // 设置提示文字字体
        initSpanFont(spanStr, tip_font, 0, str_tip.length());

        // 换行
        spanStr.setSpan(new AlignmentSpan() {
                            @Override
                            public Layout.Alignment getAlignment() {
                                return Layout.Alignment.ALIGN_OPPOSITE;
                            }
                        }, str_tip.length() + 1, secStrStart + str_sec.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置分钟数文本字体
        initSpanFont(spanStr, time_font, str_tip.length() + 1, minStrStart);

        // 设置分钟字符串字体
        initSpanFont(spanStr, tip_font, minStrStart,
                minStrStart + str_min.length());

        // 设置秒钟数字体
        initSpanFont(spanStr, time_font, minStrStart + str_min.length(),
                secStrStart);

        // 设置秒钟字符串字体
        initSpanFont(spanStr, tip_font, secStrStart,
                secStrStart + str_sec.length());

        mCountDown.setText(spanStr);
    }

    /**
     * 设置文本的字体，如大小，字体，颜色……
     * @param spanStr 可扩展的String
     * @param font	字体
     * @param start	待设置字符串的起始位置
     * @param end	待设置字符串的结束位置
     */
    void initSpanFont(SpannableString spanStr, FontParam font, int start,
                      int end) {
        spanStr.setSpan(new AbsoluteSizeSpan(font.getSize()), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        // MTypefaceSpan("sdifojao", typeface)构造方法的第一个参数无意义，属于瞎写。
        spanStr.setSpan(new MTypefaceSpan("sdifojao", typeface), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        spanStr.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // TODO [暂不考虑]字体的其它设置,比如阴影,倾角之类,暂时不考虑
    }
    public void startThread(){
        myThread = new Thread(myRunnable);
        runFlag = true;
        myThread.setName("MCountDownThread");
        myThread.start();
    }

    public void stopThread(){
        runFlag = false;
        if(myThread!=null){
            myThread.interrupt();
        }

    }

    @Override
    /**
     * 当控件显示时自动执行的方法
     */
    protected void onAttachedToWindow() {
        LogUtil.i(TAG, "onAttachedToWindow...........");
        startThread();
        super.onAttachedToWindow();
    }

    @Override
    /**
     * 当控件从界面退出时自动执行的方法
     */
    protected void onDetachedFromWindow() {
        LogUtil.i(TAG, "onDetachedFromWindow...........");
        stopThread();
        super.onDetachedFromWindow();
    }

    public ShowLang getShow_lang() {
        return show_lang;
    }

    public void setShow_lang(ShowLang show_lang) {
        this.show_lang = show_lang;
    }

    public long getTime_countdown() {
        return time_countdown;
    }

    public void setTime_countdown(long time_countdown) {
        this.time_countdown = time_countdown;
    }

    public MCountDown getmCountDown() {
        return mCountDown;
    }

    public void setmCountDown(MCountDown mCountDown) {
        this.mCountDown = mCountDown;
    }

    public boolean isShow_second() {
        return show_second;
    }

    public void setShow_second(boolean show_second) {
        this.show_second = show_second;
    }

    public FontParam getTime_font() {
        return time_font;
    }

    public void setTime_font(FontParam time_font) {
        this.time_font = time_font;
    }

    public FontParam getTip_font() {
        return tip_font;
    }

    public void setTip_font(FontParam tip_font) {
        this.tip_font = tip_font;
    }

//    public ArrayList<ConvertItem> getConvertItem_list() {
//        return convertItem_list;
//    }
//
//    public void setConvertItem_list(ArrayList<ConvertItem> convertItem_list) {
//        this.convertItem_list = convertItem_list;
//    }

    public FontParam getConvert_font() {
        return convert_font;
    }

    public void setConvert_font(FontParam convert_font) {
        this.convert_font = convert_font;
    }

    public int getShow_interval() {
        return show_interval;
    }

    public void setShow_interval(int show_interval) {
        this.show_interval = show_interval;
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


}