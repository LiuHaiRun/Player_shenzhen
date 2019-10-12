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

import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowType;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.TypeFaceFactory;

/**
 * Created by lijingjing on 17-7-21.
 * 全屏显示
 */
public class ExigentInfo_Full extends BaseModuleInfo {
    private static final String TAG = "ExigentInfo_full";

    private BGParam backgroud;// 背景信息

    private Context mContext = null;

    private AlignMode align;// 对齐方式

    private int left_margin;

    private int top_margin;

    private FontParam font;// 字体信息

    private ShowType show_type;// 显示方式

    // <Scroll_Speed>25</Scroll_Speed>
    private int scroll_speed;// 滚动速度

    // <Min_Size >50</Min_Size>
    private int min_size;// 最小字号。
    private String text;
    private MExigent_Full mExigent;

    @Override
    public MExigent_Full getView(Context context) {
        mContext = context;
        mExigent = new MExigent_Full(context);
        //设置控件基本属性
        mExigent.setModule_type(module_type);
        mExigent.setzOrder(zOrder);
        mExigent.setModule_name(module_name);
        // 位置
        pos.setTop(0);
        pos.setLeft(0);
        pos.setWidth(Const.screenW);
        pos.setHeight(Const.screenH);

        initPosition(mExigent, pos);
        //背景
        initBackground(mExigent, backgroud);
        // 设置文本及其字体
        //对齐方式实现九宫格中左上，左下，右上，右下对齐方式。
        //        mExigent.setPadding(10, 10, 10, 10);
        showType();
        mExigent.setGravity(Gravity.CENTER|Gravity.CENTER);
        return mExigent;
    }

    void showType() {
        //        String content = "本站发生紧急事件,请各位乘客不要慌张,听从工作人员指引,从B出口顺序离站,谢谢!";
        switch (show_type) {
            case LEFTSCROLL://左滚
                showText(mExigent, font, text);
                break;
            case UPSCROLL: //上滚
                showText(mExigent, font, text);
                break;
            case NOSCROLL: //静止 如果是静止，则直接显示
                showText(mExigent, font, text);
                break;
        }
    }

    /**
     * 设置当前View显示/隐藏
     *
     * @param isShow
     */
    public void setVisibility(boolean isShow) {
        if (isShow) {
            mExigent.setVisibility(View.VISIBLE);
        } else {
            mExigent.setVisibility(View.GONE);
        }
    }

    /**
     * 设置TextView的文本和字体
     *
     * @param tv   textview控件
     * @param font 文本的字体
     * @param text 文本
     */
    void showText(TextView tv, FontParam font, String text) {
        tv.setText(text);
        int size = (int) (font.getSize() * Const.ScaleY);
        tv.setTextSize(size);
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        tv.setTypeface(typeface);
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        tv.setTextColor(color);

        tv.setGravity(Gravity.LEFT|Gravity.CENTER);

    }

    public void setTextSize(float size) {
        mExigent.setTextSize(size);
    }

    /**
     * 初始化控件的背景
     *
     * @param view 被设置的控件
     * @param bg   控件的背景
     */
    void initBackground(View view, BGParam bg) {
        switch (bg.getType()) {
            case NOTSHOW: // 背景不显示
                break;
            case GRADIENTCOLOR: // 背景类型为渐变色
                if (bg.getColorType() == GradientColorType.HORIZONTALGRADIENT) {
                    //水平渐变
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
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeFile(bg.getBkfile().getPath());
                Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
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
     * 接收发送的紧急模板的文件指令
     */
    public void receiveControlCmd(String text){
         showText(mExigent, font, text);
    }

    /**
     * 初始化控件的位置
     *
     * @param view 被设置的控件
     * @param pos  控件的位置
     */
    void initPosition(View view, POS pos) {
        //        int loc[] = PubUtil.getLocation(pos.getWidth(),pos.getHeight(),pos.getLeft(),pos.getTop());
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
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

    public ShowType getShow_type() {
        return show_type;
    }

    public void setShow_type(ShowType show_type) {
        this.show_type = show_type;
    }

    public int getScroll_speed() {
        return scroll_speed;
    }

    public void setScroll_speed(int scroll_speed) {
        this.scroll_speed = scroll_speed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMin_size() {
        return min_size;
    }

    public void setMin_size(int min_size) {
        this.min_size = min_size;
    }

}
