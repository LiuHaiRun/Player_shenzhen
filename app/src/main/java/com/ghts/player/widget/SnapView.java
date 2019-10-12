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
import android.widget.TextSwitcher;
import android.widget.TextView;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;

/**
 * @author ljj
 * @updateDes $2018-11-18
 * 文本切换类
 */
public class SnapView extends BaseModuleInfo {

    private Context context;
    private AlignMode align;// 对齐方式
    private int left_margin;// 左边距
    private int top_margin;// 上边距
    private int show_interval;
    private FontParam font; // 字体
    private ZTextSwitcher mText;
    private int type, SnapTime, Direction, UpdateInterval, SpaceNum, LineSpace, LeftMargin, RightMargin,
            TopMargin, BottomMargin;
    //    <Content File="aaa - 副本.txt" />
    //    <Effect Type="0" SnapTime="0" Direction="-1" UpdateInterval="5" SpaceNum="4" LineSpace="10"
    // LeftMargin="10" RightMargin="10" TopMargin="10" BottomMargin="10" />

    public View getView(Context context) {
        this.context = context;
        mText = new ZTextSwitcher(context);
//         initBackground(mText, backgroud);
        initFont(mText, font);
        initPosition(mText, pos);
//      setGravity(mText);

        return mText;
    }


    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }


    void initFont(TextSwitcher tv, FontParam font) {
//        tv.setTextSize(font.getSize());
//        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
//        tv.setTypeface(typeface);
//        RGBA rgba = font.getFaceColor();
//        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
//                rgba.getBlue());
//        tv.setTextColor(color);
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSnapTime() {
        return SnapTime;
    }

    public void setSnapTime(int snapTime) {
        SnapTime = snapTime;
    }

    public int getDirection() {
        return Direction;
    }

    public void setDirection(int direction) {
        Direction = direction;
    }

    public int getUpdateInterval() {
        return UpdateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        UpdateInterval = updateInterval;
    }

    public int getSpaceNum() {
        return SpaceNum;
    }

    public void setSpaceNum(int spaceNum) {
        SpaceNum = spaceNum;
    }

    public int getLineSpace() {
        return LineSpace;
    }

    public void setLineSpace(int lineSpace) {
        LineSpace = lineSpace;
    }

    public int getLeftMargin() {
        return LeftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        LeftMargin = leftMargin;
    }

    public int getRightMargin() {
        return RightMargin;
    }

    public void setRightMargin(int rightMargin) {
        RightMargin = rightMargin;
    }

    public int getTopMargin() {
        return TopMargin;
    }

    public void setTopMargin(int topMargin) {
        TopMargin = topMargin;
    }

    public int getBottomMargin() {
        return BottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        BottomMargin = bottomMargin;
    }
}