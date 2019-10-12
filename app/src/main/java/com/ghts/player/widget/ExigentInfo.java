package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;

import com.ghts.player.data.CmdDate;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-6-26.
 * 局部的紧急消息设置(横滚)
 */
public class ExigentInfo extends BaseModuleInfo {
    private static final String TAG = "ExigentInfo";

    private MarqueeTextView2 mScroll;
    private Context mContext = null;
    private POS pos;
    private BGParam bgParam;
    private FontParam font;
    private AlignMode align;// 对齐方式
    private int left_margin;// 左边距
    private int top_margin;// 上边距
    private int scroll_speed;
    private ArrayList<String> text_list = null;// 待显示的文本列表
    //是否收到广播 默认false
    private boolean isReceive = false;
    private String mText;
    private int refresh_time = 1; // 滚动刷新的时间间隔
    private int scroll_pixel = 1; // 滚动刷新的间距
    private int oneStepMaxSpeed = 10; // 80 当scroll_speed小于该值时每次刷新滚动1个像素，小于该值N倍时滚动N个像素。

    @Override
    public View getView(Context context) {
        mContext = context;
        mScroll = new MarqueeTextView2(context);
        mScroll.setFocusableInTouchMode(false);
        //设置控件基本属性
        mScroll.setModule_type(module_type);
        mScroll.setzOrder(zOrder);
        mScroll.setModule_name(module_name);
        // 控件位置及大小设置。
        initPosition(mScroll, pos);
        // 背景设置
        initBackground(mScroll, bgParam);
        // 设置字体
        mScroll.setFont(font);
        // 设置文本列表
        mScroll.setText_list(text_list);

        // 设置左、上的padding（用left、top margin变量设置）
        mScroll.setPadding(left_margin, 0, 0, top_margin);

        if (scroll_speed <= 0 || scroll_speed >= 2000) { // 当速度为零或太大时，选默认速度
            scroll_pixel = 2;
            refresh_time = 20;
        } else { // 根据scroll_speed和oneStepMaxSpeed算出合适的步长scroll_pixel，再决定滚动刷新的时间。
            scroll_pixel = Math.round((float) scroll_speed / oneStepMaxSpeed);
            refresh_time = 0;

        }
        mScroll.setScroll_pixel(scroll_pixel);
        mScroll.setRefresh_time(refresh_time);
        mScroll.setCurrentScrollX(pos.getWidth());// 设置滚动文本的初始位置。
        return mScroll;

    }

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
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

    public void setVisibility(boolean flag){
        if(flag){
            mScroll.setVisibility(View.VISIBLE);
        }else{
            mScroll.setVisibility(View.GONE);
        }
    }
    public Context getmContext() {
        return mContext;
    }


    public POS getPos() {
        return pos;
    }

    public BGParam getBgParam() {
        return bgParam;
    }

    public FontParam getFont() {
        return font;
    }

    public AlignMode getAlign() {
        return align;
    }

    public int getLeft_margin() {
        return left_margin;
    }

    public int getTop_margin() {
        return top_margin;
    }

    public int getScroll_speed() {
        return scroll_speed;
    }

    public ArrayList<String> getText_list() {
        return text_list;
    }

    public boolean isReceive() {
        return isReceive;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }


    public void setPos(POS pos) {
        this.pos = pos;
    }

    public void setBgParam(BGParam bgParam) {
        this.bgParam = bgParam;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }

    public void setAlign(AlignMode align) {
        this.align = align;
    }

    public void setLeft_margin(int left_margin) {
        this.left_margin = left_margin;
    }

    public void setTop_margin(int top_margin) {
        this.top_margin = top_margin;
    }

    public void setScroll_speed(int scroll_speed) {
        this.scroll_speed = scroll_speed;
    }

    public void setText_list(ArrayList<String> text_list) {
        this.text_list = text_list;
    }

    public void setReceive(boolean receive) {
        isReceive = receive;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    /**
     * 接收发送的紧急模板的文件指令
     */
    public void receiveControlCmd(String str) {
        text_list = new ArrayList<String>();
        text_list.add(str);
        LogUtil.e(TAG, "接收:" + str);
        setText_list(text_list);
        mScroll.setText_list(text_list);
    }


    public void receiveControlCmd(CmdDate data) {
        String str = data.getTast_set().getSzEmergencyInfo();
        //        text_list.clear();
        text_list = new ArrayList<String>();
        text_list.add(str);
        LogUtil.e(TAG, "接收:" + str);
        setText_list(text_list);
        mScroll.setText_list(text_list);
    }
}
