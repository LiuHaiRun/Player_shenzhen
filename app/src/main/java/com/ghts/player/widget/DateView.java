package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ghts.player.activity.PlayerActivity;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.DateParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowLang;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;


/**
 * Created by lijingjing on 17-6-14.
 */
public class DateView extends BaseModuleInfo {

    private Context mContext = null;
    private BGParam backgroud;
    private AlignMode align;// 对齐方式
    private int left_margin;// 左边距
    private int top_margin;// 上边距
    private boolean show_date;// 是否显示
    private ShowLang date_Lang;// 显示日期的语言
    private DateParam date_param; // 日期显示格式。
    private FontParam date_font;// 日期字体
    private boolean show_week;// 是否显示星期
    private ShowLang week_lang;// 显示星期的语言
    private FontParam week_font;// 显示星期的字体。
    private MyDate mDate, mDate2;
    private int SpaceNum;//空格数
    private POS wPos, dPos;
    private int weekPos; // 显示位置

    @Override
    public View getView(Context context) {
        mContext = context;
        if (weekPos == 2 && show_week) {
             FrameLayout view = new FrameLayout(PlayerActivity.context);
            mDate = new MyDate(context);
            mDate.setIncludeFontPadding(false);
            //设置控件基本属性
            mDate.setModule_type(module_type);
            mDate.setSpaceNum(SpaceNum);
            mDate.setWeekPos(weekPos);
            mDate.setzOrder(zOrder);
            mDate.setModule_name(module_name);
            // 控件位置及大小设置。
            initPosition(mDate, dPos);
            // 背景设置
            initBackground(mDate, backgroud);

            mDate.setGravity(Gravity.CENTER);
            mDate.setShow_date(show_date);
            mDate.setDate_Lang(date_Lang);
            mDate.setDate_param(date_param);
            mDate.setDate_font(date_font);

            mDate.setShow_week(false);
            mDate.setWeek_lang(week_lang);
            mDate.setWeek_font(week_font);
            mDate.setmDate(mDate);
            view.addView(mDate);

            // -----------------创建两个-----------------------------------
            mDate2 = new MyDate(context);
            mDate2.setIncludeFontPadding(false);
            //设置控件基本属性
            mDate2.setModule_type(module_type);
            mDate2.setSpaceNum(SpaceNum);
            mDate2.setWeekPos(weekPos);
            mDate2.setzOrder(zOrder);
            mDate2.setModule_name(module_name);
            // 控件位置及大小设置。
            initPosition(mDate2, wPos);
            // 背景设置
            initBackground(mDate2, backgroud);

            mDate2.setGravity(Gravity.CENTER);
            mDate2.setShow_date(false);
            mDate2.setDate_Lang(date_Lang);
            mDate2.setDate_param(date_param);
            mDate2.setDate_font(date_font);

            mDate2.setShow_week(show_week);
            mDate2.setWeek_lang(week_lang);
            mDate2.setWeek_font(week_font);
            mDate2.setmDate(mDate2);
            view.addView(mDate2);
            return view;
        } else {
             //同行在前
            mDate = new MyDate(context);
            mDate.setIncludeFontPadding(false);
            //设置控件基本属性
            mDate.setModule_type(module_type);
            mDate.setSpaceNum(SpaceNum);
            mDate.setWeekPos(weekPos);
            mDate.setzOrder(zOrder);
            mDate.setModule_name(module_name);
            // 控件位置及大小设置。
            initPosition(mDate, pos);
            // 背景设置
            initBackground(mDate, backgroud);

            mDate.setGravity(Gravity.CENTER);
            mDate.setShow_date(show_date);
            mDate.setDate_Lang(date_Lang);
            mDate.setDate_param(date_param);
            mDate.setDate_font(date_font);

            mDate.setShow_week(show_week);
            mDate.setWeek_lang(week_lang);
            mDate.setWeek_font(week_font);
            mDate.setmDate(mDate);
            return mDate;
        }
    }

    void initBackground(View view, BGParam bg) {
        switch (bg.getType()) {
            case NOTSHOW: // 背景不显示
                break;
            case GRADIENTCOLOR: // 背景类型为渐变色
                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
                    //水平渐变 参考：url:http://blog.csdn.net/a_large_swan/article/details/7107126
                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
                            bg.getGradientcolor1().getBlue());
                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
                            bg.getGradientcolor2().getBlue());
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
                    view.setBackgroundDrawable(gradientDrawable);
                } else {
                    // 垂直渐变
                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
                            bg.getGradientcolor1().getBlue());
                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
                            bg.getGradientcolor2().getBlue());
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
                    view.setBackgroundDrawable(gradientDrawable);
                }
                break;
            case PICTURE: // 背景类型为图片
                Bitmap bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
                Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                // 设置图片背景
                view.setBackgroundDrawable(drawable);
                break;
            case PURECOLOR: // 背景类型为纯色
                RGBA purecolor = bg.getPurecolor();
                view.setBackgroundColor(Color.argb(purecolor.getAlpha(),
                        purecolor.getRed(), purecolor.getGreen(),
                        purecolor.getBlue()));
                break;
            default: // 其他
                break;
        }
    }

    /**
     * 初始化控件的位置
     */
    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public void setVisibility(boolean isShow) {
        if (isShow) {
            mDate.setVisibility(View.VISIBLE);
        } else {
            mDate.setVisibility(View.GONE);
        }
    }

    public BGParam getBackgroud() {
        return backgroud;
    }

    public void setBackgroud(BGParam backgroud) {
        this.backgroud = backgroud;
    }

    public AlignMode getAlign() {
        return align;
    }

    public void setAlign(AlignMode align) {
        this.align = align;
    }

    public int getLeft_margin() {
        return left_margin;
    }

    public void setLeft_margin(int left_margin) {
        this.left_margin = left_margin;
    }

    public int getTop_margin() {
        return top_margin;
    }

    public void setTop_margin(int top_margin) {
        this.top_margin = top_margin;
    }

    public boolean isShow_date() {
        return show_date;
    }

    public void setShow_date(boolean show_date) {
        this.show_date = show_date;
    }

    public ShowLang getDate_Lang() {
        return date_Lang;
    }

    public void setDate_Lang(ShowLang date_Lang) {
        this.date_Lang = date_Lang;
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
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.GradientDrawable;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import com.ghts.player.activity.PlayerActivity;
//import com.ghts.player.enumType.AlignMode;
//import com.ghts.player.enumType.BGParam;
//import com.ghts.player.enumType.DateParam;
//import com.ghts.player.enumType.FontParam;
//import com.ghts.player.enumType.GradientColorType;
//import com.ghts.player.enumType.POS;
//import com.ghts.player.enumType.RGBA;
//import com.ghts.player.enumType.ShowLang;
//import com.ghts.player.utils.Const;
//
//
///**
// * Created by lijingjing on 17-6-14.
// */
//public class DateView extends BaseModuleInfo {
//
//    private Context mContext = null;
//    private BGParam backgroud;
//    private AlignMode align;// 对齐方式
//    private int left_margin;// 左边距
//    private int top_margin;// 上边距
//    private boolean show_date;// 是否显示
//    private ShowLang date_Lang;// 显示日期的语言
//    private DateParam date_param; // 日期显示格式。
//    private FontParam date_font;// 日期字体
//    private boolean show_week;// 是否显示星期
//    private ShowLang week_lang;// 显示星期的语言
//    private FontParam week_font;// 显示星期的字体。
//    private MyDate mDate,mDate2;
//    private int SpaceNum;//空格数
//    private POS wPos,dPos;
//    private int weekPos; // 显示位置
//
//    @Override
//    public View getView(Context context) {
//        mContext = context;
//        if(weekPos == 0 || weekPos == 1){
//            //同行在前
//            mDate = new MyDate(context);
//            //设置控件基本属性
//            mDate.setModule_type(module_type);
//            mDate.setSpaceNum(SpaceNum);
//            mDate.setWeekPos(weekPos);
//            mDate.setzOrder(zOrder);
//            mDate.setModule_name(module_name);
//            // 控件位置及大小设置。
//            initPosition(mDate, pos);
//            // 背景设置
//            initBackground(mDate, backgroud);
//
//            mDate.setGravity(Gravity.CENTER);
//            mDate.setShow_date(show_date);
//            mDate.setDate_Lang(date_Lang);
//            mDate.setDate_param(date_param);
//            mDate.setDate_font(date_font);
//
//            mDate.setShow_week(show_week);
//            mDate.setWeek_lang(week_lang);
//            mDate.setWeek_font(week_font);
//            mDate.setmDate(mDate);
//            return mDate;
//        }else if(weekPos == 2){
//            FrameLayout view = new FrameLayout(PlayerActivity.context);
//            mDate = new MyDate(context);
//            //设置控件基本属性
//            mDate.setModule_type(module_type);
//            mDate.setSpaceNum(SpaceNum);
//            mDate.setWeekPos(weekPos);
//            mDate.setzOrder(zOrder);
//            mDate.setModule_name(module_name);
//            // 控件位置及大小设置。
//            initPosition(mDate, dPos);
//            // 背景设置
//            initBackground(mDate, backgroud);
//
//            mDate.setGravity(Gravity.CENTER);
//            mDate.setShow_date(show_date);
//            mDate.setDate_Lang(date_Lang);
//            mDate.setDate_param(date_param);
//            mDate.setDate_font(date_font);
//
//            mDate.setShow_week(false);
//            mDate.setWeek_lang(week_lang);
//            mDate.setWeek_font(week_font);
//            mDate.setmDate(mDate);
//            view.addView(mDate);
//
//            // -----------------创建两个-----------------------------------
//            mDate2 = new MyDate(context);
//            //设置控件基本属性
//            mDate2.setModule_type(module_type);
//            mDate2.setSpaceNum(SpaceNum);
//            mDate2.setWeekPos(weekPos);
//            mDate2.setzOrder(zOrder);
//            mDate2.setModule_name(module_name);
//            // 控件位置及大小设置。
//            initPosition(mDate2, wPos);
//            // 背景设置
//            initBackground(mDate2, backgroud);
//
//            mDate2.setGravity(Gravity.CENTER);
//            mDate2.setShow_date(false);
//            mDate2.setDate_Lang(date_Lang);
//            mDate2.setDate_param(date_param);
//            mDate2.setDate_font(date_font);
//
//            mDate2.setShow_week(show_week);
//            mDate2.setWeek_lang(week_lang);
//            mDate2.setWeek_font(week_font);
//            mDate2.setmDate(mDate2);
//            view.addView(mDate2);
//            return view;
//        }
//        return null;
//    }
//
//    public String getDateStr(){
//        if(weekPos == 0 || weekPos == 1) {
//            return mDate.getText().toString();
//        }else if(weekPos == 2){
//            return mDate.getText().toString()+" "+mDate2.getText().toString();
//        }
//        return "";
//    }
//
//    void initBackground(View view, BGParam bg) {
//        switch (bg.getType()) {
//            case NOTSHOW: // 背景不显示
//                break;
//            case GRADIENTCOLOR: // 背景类型为渐变色
//                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
//                    //水平渐变 参考：url:http://blog.csdn.net/a_large_swan/article/details/7107126
//                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
//                            bg.getGradientcolor1().getBlue());
//                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
//                            bg.getGradientcolor2().getBlue());
//                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
//                    view.setBackgroundDrawable(gradientDrawable);
//                } else {
//                    // 垂直渐变
//                    int startColor = Color.argb(bg.getGradientcolor1().getAlpha(), bg.getGradientcolor1().getRed(), bg.getGradientcolor1().getGreen(),
//                            bg.getGradientcolor1().getBlue());
//                    int endColor = Color.argb(bg.getGradientcolor2().getAlpha(), bg.getGradientcolor2().getRed(), bg.getGradientcolor2().getGreen(),
//                            bg.getGradientcolor2().getBlue());
//                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{startColor, endColor});
//                    view.setBackgroundDrawable(gradientDrawable);
//                }
//                break;
//            case PICTURE: // 背景类型为图片
//                Bitmap bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
//                Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
//                // 设置图片背景
//                view.setBackgroundDrawable(drawable);
//                break;
//            case PURECOLOR: // 背景类型为纯色
//                RGBA purecolor = bg.getPurecolor();
//                view.setBackgroundColor(Color.argb(purecolor.getAlpha(),
//                        purecolor.getRed(), purecolor.getGreen(),
//                        purecolor.getBlue()));
//                break;
//            default: // 其他
//                break;
//        }
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
//
//
//    public void setVisibility(boolean isShow) {
//        if (isShow) {
//            mDate.setVisibility(View.VISIBLE);
//        } else {
//            mDate.setVisibility(View.GONE);
//        }
//    }
//
//    public BGParam getBackgroud() {
//        return backgroud;
//    }
//
//    public void setBackgroud(BGParam backgroud) {
//        this.backgroud = backgroud;
//    }
//
//    public AlignMode getAlign() {
//        return align;
//    }
//
//    public void setAlign(AlignMode align) {
//        this.align = align;
//    }
//
//    public int getLeft_margin() {
//        return left_margin;
//    }
//
//    public void setLeft_margin(int left_margin) {
//        this.left_margin = left_margin;
//    }
//
//    public int getTop_margin() {
//        return top_margin;
//    }
//
//    public void setTop_margin(int top_margin) {
//        this.top_margin = top_margin;
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
//        return date_Lang;
//    }
//
//    public void setDate_Lang(ShowLang date_Lang) {
//        this.date_Lang = date_Lang;
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
