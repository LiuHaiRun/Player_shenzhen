package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.utils.MTypefaceSpan;
import com.ghts.player.utils.TypeFaceFactory;


/**
 * Created by lijingjing on 17-9-5.
 */
public class MAtsStrain extends AutoFitTextView {
    private static final String TAG = "MAtsStrain";
    private int module_type; // 模块类型
    private int zOrder; // 模块的ZOrder
    private String module_name;    //控件名
    private boolean isShow;// 是否显示
    private Thread myThread;
    private MAtsStrain mAtsStrain;
    private String info;
    private String ids;
    private FontParam fontNum,fontTrans,fontTransEn,font;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public MAtsStrain(Context context,int size) {
        super(context,size);
    }
    public MAtsStrain(Context context,int size,FontParam fontParam,FontParam FontNum,
                      FontParam FontTrans,FontParam FontTransEn) {
        super(context,size);
        this.font = fontParam;
        this.fontNum = FontNum;
        this.fontTrans = FontTrans;
        this.fontTransEn = FontTransEn;
    }

    private Runnable myRunnable = new Runnable() {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mAtsStrain.setText(info);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        myThread = new Thread(myRunnable);
        myThread.setName("MAtsStrain");
        myThread.start();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        myThread.interrupt();
        super.onDetachedFromWindow();
    }
   public void initSpanFont(SpannableString spanStr, FontParam font, int start,
                      int end) {
        // 设置大小
        spanStr.setSpan(new AbsoluteSizeSpan(font.getSize()), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体
        Typeface typeface = TypeFaceFactory.createTypeface(font.getName());
        spanStr.setSpan(new MTypefaceSpan("ceshi", typeface), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置颜色
        RGBA rgba = font.getFaceColor();
        int color = Color.argb(rgba.getAlpha(), rgba.getRed(), rgba.getGreen(),
                rgba.getBlue());
        spanStr.setSpan(new ForegroundColorSpan(color), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public static String getTAG() {
        return TAG;
    }

    public int getModule_type() {
        return module_type;
    }

    public int getzOrder() {
        return zOrder;
    }

    public String getModule_name() {
        return module_name;
    }

    public boolean isShow() {
        return isShow;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public MAtsStrain getmAtsStrain() {
        return mAtsStrain;
    }

    public String getInfo() {
        return info;
    }

    public void setModule_type(int module_type) {
        this.module_type = module_type;
    }

    public void setzOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    public void setmAtsStrain(MAtsStrain mAtsStrain) {
        this.mAtsStrain = mAtsStrain;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public FontParam getFontNum() {
        return fontNum;
    }

    public void setFontNum(FontParam fontNum) {
        this.fontNum = fontNum;
    }

    public FontParam getFontTrans() {
        return fontTrans;
    }

    public void setFontTrans(FontParam fontTrans) {
        this.fontTrans = fontTrans;
    }

    public FontParam getFontTransEn() {
        return fontTransEn;
    }

    public void setFontTransEn(FontParam fontTransEn) {
        this.fontTransEn = fontTransEn;
    }

    public FontParam getFont() {
        return font;
    }

    public void setFont(FontParam font) {
        this.font = font;
    }
}
