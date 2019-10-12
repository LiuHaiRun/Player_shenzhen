package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lijingjing on 17-4-24.
 */
//public class AutoScrollTextView extends TextView implements View.OnClickListener {
//    public final static String TAG = AutoScrollTextView.class.getSimpleName();
//
//    private float textLength = 0f;//文本长度
//    private float viewWidth = 0f;
//    private float step = 0f;//文字的横坐标
//    private float y = 0f;//文字的纵坐标
//    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
//    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
//    public boolean isStarting = false;//是否开始滚动
//    private Paint paint = null;//绘图样式
//    private String text = "";//文本内容
//
//
//    public AutoScrollTextView(Context context) {
//        super(context);
//        initView();
//    }
//
//    public AutoScrollTextView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView();
//    }
//
//    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initView();
//    }
//
//
//    private void initView() {
//        setOnClickListener(this);
//    }
//
//
//    public void init(WindowManager windowManager, int width) {
//        paint = getPaint();
//        text = getText().toString();
//        textLength = paint.measureText(text);
//        viewWidth = getWidth();
//        if (viewWidth == 0) {
//            if (windowManager != null) {
//                Display display = windowManager.getDefaultDisplay();
//                viewWidth = display.getWidth() - 200;
//            }
//            Log.e("--viewWidth--1240-", viewWidth + "===");
//        }
//        step = textLength;
//        temp_view_plus_text_length = viewWidth + textLength;
//
//        temp_view_plus_two_text_length = viewWidth + textLength * 2;
//        y = getTextSize() + getPaddingTop();
//
//        Log.e("--plus_text-2668-", temp_view_plus_text_length + "===");
//        Log.e("--iew_two-text-4096-", temp_view_plus_two_text_length + "===");
//
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//        SavedState ss = new SavedState(superState);
//
//        ss.step = step;
//        ss.isStarting = isStarting;
//
//        return ss;
//
//    }
//
//    @Override
//    public void onRestoreInstanceState(Parcelable state) {
//        if (!(state instanceof SavedState)) {
//            super.onRestoreInstanceState(state);
//            return;
//        }
//        SavedState ss = (SavedState) state;
//        super.onRestoreInstanceState(ss.getSuperState());
//
//        step = ss.step;
//        isStarting = ss.isStarting;
//
//    }
//
//    public static class SavedState extends BaseSavedState {
//        public boolean isStarting = false;
//        public float step = 0.0f;
//
//        SavedState(Parcelable superState) {
//            super(superState);
//        }
//
//        @Override
//        public void writeToParcel(Parcel out, int flags) {
//            super.writeToParcel(out, flags);
//            out.writeBooleanArray(new boolean[]{isStarting});
//            out.writeFloat(step);
//        }
//
//
//        public static final Creator<SavedState> CREATOR
//                = new Creator<SavedState>() {
//
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//
//            @Override
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//        };
//
//        private SavedState(Parcel in) {
//            super(in);
//            boolean[] b = null;
//            in.readBooleanArray(b);
//            if (b != null && b.length > 0)
//                isStarting = b[0];
//            step = in.readFloat();
//        }
//    }
//
//
//    public void startScroll() {
//        isStarting = true;
//        invalidate();
//    }
//
//
//    public void stopScroll() {
//        isStarting = false;
//        invalidate();
//    }
//
//
//    @Override
//    public void onDraw(Canvas canvas) {
//
//        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
//        if (!isStarting) {
//            return;
//        }
//        step += 3;//0.5为文字滚动速度。
//        if (step > temp_view_plus_two_text_length)
//            step = textLength;
//        invalidate();
//
////        Log.e("---canvas--",temp_view_plus_text_length - step+"");
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (isStarting)
//            stopScroll();
//        else
//            startScroll();
//
//    }
//
//    @Override
//    public boolean isFocused() {
//        return true;
//    }
//}

/**
 * Created by lijingjing on 17-4-24.
 */
public class AutoScrollTextView extends TextView {
    public final static String TAG = AutoScrollTextView.class.getSimpleName();

    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;
    private float speed = 0f;//文字的横坐标
    private float x, y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容
    private Context context;
    private int color;
    private int width;
    private boolean isMeasure = false;

    public AutoScrollTextView(Context context) {
        super(context);
        this.context = context;
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void getS(ViewGroup viewGroup, Context context, String text, int width, int height, int size, int color, int left, int top, int right, int bottom, int id) {
        this.text = text;
        this.color = color;
    }

    public void getScrollText(int width, String text, int color, int speed, int x, int y) {
        this.text = text;
        this.color = color;
        this.width = width;
        this.x = x;
        this.y = y;
        this.speed = speed;

        init(width, text);

    }

    public void init(int width, String text) {
        paint = getPaint();
        paint.setColor(color);
        this.text = text;
        textLength = paint.measureText(text);
        viewWidth = getWidth();
        if (viewWidth == 0) {
            viewWidth = width;
        }
        step = textLength;
        temp_view_plus_text_length = viewWidth + textLength;

        temp_view_plus_two_text_length = viewWidth + textLength * 2;
        y = getTextSize() + getPaddingTop();

        startScroll();
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.step = step;
        ss.isStarting = isStarting;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        step = ss.step;
        isStarting = ss.isStarting;
    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if (b != null && b.length > 0)
                isStarting = b[0];
            step = in.readFloat();
        }
    }

    public void startScroll() {
        isStarting = true;
//        invalidate();
    }


    public void stopScroll() {
        isStarting = false;
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
        if (!isStarting) {
            return;
        }
        step += speed;//0.5为文字滚动速度。
        if (step > temp_view_plus_two_text_length)
            step = textLength;
        invalidate();
    }


    public void setText(String text) {
        this.text = text;
        init(width, text);
        invalidate();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}

