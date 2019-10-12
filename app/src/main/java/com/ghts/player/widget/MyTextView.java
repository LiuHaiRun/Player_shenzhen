package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * @author ljj
 * @updateDes $2019-0320
 * 重写TextView,设置垂直居中显示
 */
public class MyTextView extends TextView {
    private final static String TAG = "MyTextView";

    //文字
    private String mText;

    //文字位置
    private int  gravity;

    //绘制的范围
    private Rect mBound;
    private Paint mPaint;

    public MyTextView(Context context) {
        this(context, null);
        init();
    }
    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
         super(context, attrs, defStyleAttr);
         init();
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
    }

    /**
     * 初始化数据
     */
    private void init() {
        //初始化数据
        mText = "1970:00:00";

        //初始化Paint数据
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //获取绘制的宽高
        mBound = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
    }


    @Override
    protected void onDraw(Canvas canvas) {
         //计算基线
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        int x = getPaddingLeft();
        // x: 开始的位置  y：基线
        canvas.drawText(getText().toString(), x, baseLine, mPaint);
        invalidate();
    }

}

