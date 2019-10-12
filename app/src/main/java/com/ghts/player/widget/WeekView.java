package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowFontType;
import com.ghts.player.utils.TypeFaceFactory;

import java.util.HashMap;

/**
 * @author ljj
 * @des 2018-10-18
 */
public class WeekView extends BaseModuleInfo{
    private Context context;
    private BGParam backgroud;
    private FontParam font,fontEn; // 字体
    private MWeek mWeek;
    private ShowFontType mShowFontType; //显示模式
    private int interval; //轮训间隔 单位：秒
    private HashMap<Integer, String> weekStrC = new HashMap<Integer, String>();
    private HashMap<Integer, String> weekStrEn = new HashMap<Integer, String>();

    public View getView(Context context) {
        this.context = context;
        mWeek = new MWeek(context);
        mWeek.setIncludeFontPadding(false);
        mWeek.setWeek_font(font);
        mWeek.setWeek_font_EN(fontEn);
        mWeek.setShowFontType(mShowFontType);
        mWeek.setInterval(interval);
        mWeek.setWeekC(weekStrC);
        mWeek.setWeekEn(weekStrEn);
        mWeek.setWeek(mWeek);
        mWeek.setGravity(Gravity.CENTER);

        initBackground(mWeek, backgroud);
        initPosition(mWeek, pos);
        initFont(mWeek, font);
        return mWeek;
    }


    void initFont(TextView tv, FontParam font) {
        tv.setTextSize(font.getSize());
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);
    }

    void initBackground(View view, BGParam bg) {
        switch (bg.getType()) {
            case NOTSHOW: // 背景不显示
                break;
            case GRADIENTCOLOR: // 背景类型为渐变色
                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
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
                // 本地图片文件路径生成drawable
                Bitmap bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
                Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
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

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public ShowFontType getShowFontType() {
        return mShowFontType;
    }

    public int getInterval() {
        return interval;
    }

    public HashMap<Integer, String> getWeekStrC() {
        return weekStrC;
    }

    public HashMap<Integer, String> getWeekStrEn() {
        return weekStrEn;
    }

    public void setShowFontType(ShowFontType showFontType) {
        mShowFontType = showFontType;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setWeekStrC(HashMap<Integer, String> weekStrC) {
        this.weekStrC = weekStrC;
    }

    public void setWeekStrEn(HashMap<Integer, String> weekStrEn) {
        this.weekStrEn = weekStrEn;
    }

    public BGParam getBackgroud() {
        return backgroud;
    }

    public FontParam getFont() {
        return font;
    }

    public FontParam getFontEn() {
        return fontEn;
    }

    public void setBackgroud(BGParam backgroud) {
        this.backgroud = backgroud;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }

    public void setFontEn(FontParam fontEn) {
        this.fontEn = fontEn;
    }
}
