package com.ghts.player.utils;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * 根据自定义字体类创建对应TypefaceSpan类型字体的类。
 */
public class MTypefaceSpan extends TypefaceSpan {
    private final Typeface newType;

    /**
     * 创建对应TypefaceSpan类型的字体，给SpannableString设置字体时用到
     * @param family	参数任意指定，无意义
     * @param type	字体类型
     */
    public MTypefaceSpan(String family, Typeface type) {
        super(family);
        newType = type;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        applyCustomTypeFace(ds, newType);

    }

    @Override
    public void updateMeasureState(TextPaint paint) {

        applyCustomTypeFace(paint, newType);

    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {

        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }
        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(tf);

    }
}
