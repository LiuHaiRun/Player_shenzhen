package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.TypeFaceFactory;

/**
 * Title文本类
 */
public class TitleView extends BaseModuleInfo {

    private BGParam backgroud;
    private AlignMode align;// 对齐方式
    private int left_margin;// 左边距
    private int top_margin;// 上边距
    private int show_interval;
    private FontParam font; // 字体
    private TextView mText, bottomText;
    private Context context;
    private String showStr, bottomStr;
    FrameLayout view = null;

    public View getView(Context context) {
        this.context = context;

        mText = new TextView(context);
        mText.setIncludeFontPadding(false);
        initBackground(mText, backgroud);
        initFont(mText, font);
        initPosition(mText, pos);
        setGravity(mText);
        mText.setText(showStr);
        if (!TextUtils.isEmpty(bottomStr)) {
            mText.setText(showStr + "\n" + bottomStr);
        }
        mText.setPadding(left_margin, top_margin, 0, 0);
        return mText;
    }


    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
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

    void setGravity(TextView mText) {
        switch (align) {
            case MIDDLELEFT:
                mText.setGravity(Gravity.LEFT | Gravity.CENTER);
                break;
            case CENTER:
                mText.setGravity(Gravity.CENTER);
                break;
            case MIDDLERIGHT:
                mText.setGravity(Gravity.RIGHT | Gravity.CENTER);
                break;
            default:
                break;
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


    public void setVisibility(boolean isShow) {
        if (isShow) {
            mText.setVisibility(View.VISIBLE);
        } else {
            mText.setVisibility(View.GONE);
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

    public int getShow_interval() {
        return show_interval;
    }

    public void setShow_interval(int show_interval) {
        this.show_interval = show_interval;
    }

    public FontParam getFont() {
        return font;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }

    public String getShowStr() {
        return showStr;
    }

    public void setShowStr(String showStr) {
        this.showStr = showStr;
    }

    public String getBottomStr() {
        return bottomStr;
    }

    public void setBottomStr(String bottomStr) {
        this.bottomStr = bottomStr;
    }
}