package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.TypeFaceFactory;

import java.io.File;

/**
 * Created by lijingjing on 17-8-1.
 */
public class TimeView extends BaseModuleInfo {
    private BGParam backgroud;
    private Context mContext = null;
    private AlignMode align;// 对齐方式
    private int left_margin;// 左边距
    private int top_margin;// 上边距
    private FontParam font;
    private boolean show_second;// 是否显示秒数。
    private File analogclock;// 模拟时钟文件
    private MClock mClock;
    private int show_interval;
    MTime mTime = null;

    @Override
    public View getView(Context context) {
        mContext = context;
        //        if (time_type == ClockType.DIGITALCLOCK) { // 数字时钟
        mTime = new MTime(mContext);
        mTime.setIncludeFontPadding(false);
        //设置控件基本属性
        mTime.setModule_type(module_type);
        mTime.setzOrder(zOrder);
        mTime.setModule_name(module_name);
        mTime.setmTime(mTime); // 将mTime的引用传递
        mTime.setShow_second(show_second);
        // 控件位置
        initPosition(mTime, pos);
        // 控件背景
        initBackground(mTime, backgroud);
        // 设置font
        initFont(mTime, font);
        //设置对齐方式
        setGravity();
        // 设置左、上的padding（用left、top margin变量设置）
        mTime.setPadding(left_margin, top_margin, 0, 0);
        return mTime;

        //        } else { // 模拟时钟
        //            mClock = new MClock(context);
        //            //设置控件基本属性
        //            mClock.setModule_type(module_type);
        //            mClock.setzOrder(zOrder);
        //            mClock.setFile_version(file_version);
        //            mClock.setModule_name(module_name);
        //            mClock.setModule_uid(module_uid);
        //            mClock.setModule_gid(module_gid);
        //            // 控件位置
        //            initPosition(mClock, pos);
        //            // 控件背景
        //            initBackground(mClock, backgroud);
        //            // 设置左、上的padding（用left、top margin变量设置）
        //            mClock.setPadding(left_margin, top_margin, 0, 0);
        //            return mClock;
        //        }
    }

    void setGravity() {
        switch (align) {
            case MIDDLELEFT:
                mTime.setGravity(Gravity.LEFT | Gravity.CENTER);
                break;
            case CENTER:
                mTime.setGravity(Gravity.CENTER);
                break;
            case MIDDLERIGHT:
                mTime.setGravity(Gravity.RIGHT | Gravity.CENTER);
                break;
            default:
                break;
        }
    }

    void initFont(TextView tv, FontParam font) {
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

    public void setVisibility(boolean isShow) {
        if (isShow) {
            mTime.setVisibility(View.VISIBLE);
        } else {
            mTime.setVisibility(View.GONE);
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
                // 本地图片文件路径生成drawable
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


    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        int bottom = Const.screenH - pos.getTop() - pos.getHeight();
        LogUtil.e("测试1",view.getBaseline()+"--"+bottom);
         view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
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

    public FontParam getFont() {
        return font;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }

    public boolean isShow_second() {
        return show_second;
    }

    public void setShow_second(boolean show_second) {
        this.show_second = show_second;
    }

    public File getAnalogclock() {
        return analogclock;
    }

    public void setAnalogclock(File analogclock) {
        this.analogclock = analogclock;
    }

}
