package com.ghts.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class ZTextSwitcher extends TextSwitcher implements TextSwitcher.ViewFactory {
    private Context mContext;


    public ZTextSwitcher(Context context) {
        this(context, null);
    }
    public ZTextSwitcher(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.mContext= context;
        init();
    }
    private void init() {
        // 设置转换动画，这里引用系统自带动画
        Animation in = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
        this.setInAnimation(in);
        this.setOutAnimation(out);
        // 设置ViewSwitcher.ViewFactory
        this.setFactory(this);
    }

    @Override
    public View makeView() {
        TextView textView =new TextView(mContext);
        textView.setTextSize(24);
        return textView;
    }
}
