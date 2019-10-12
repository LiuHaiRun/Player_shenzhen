package com.ghts.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ghts.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijingjing on 17-5-5.
 * 垂直滚动条
 */
public class VerScrollText extends FrameLayout {
    private Context context;
    public VerticalScrollTv verticalScrollTv;

    public VerScrollText(Context context) {
        super(context);
        this.context = context;

    }

    public VerScrollText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public VerScrollText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }
    public View init(ViewGroup viewGroup,  String text,int size, int color,int speed,int location[], int id) {
        View view = LayoutInflater.from(context).inflate(R.layout.verstext, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(location[0], location[1]);
        view.setLayoutParams(params);//将设置好的布局参数应用到控件中
        verticalScrollTv = (VerticalScrollTv) view.findViewById(R.id.vertext);
        verticalScrollTv.setTextColor(color);
        verticalScrollTv.setTextSize(size);
        verticalScrollTv.setId(id);
        //一行显示的字数
        int count = (location[0] )/ (int)verticalScrollTv.getTextSize();
        int j = (int) text.length() / count;
        List<String> data = new ArrayList<String>();
        for (int i = 0; i <= j; i++) {
            String s;
            if (i == 0) {
                s = text.substring(0, count);
            } else if (i == j) {
                s = text.substring(i * count, text.length());
            } else {
                s = text.substring(i * count, (i + 1) * count);
            }
            data.add(s);
        }
        if (data != null && data.size() > 0) {
            StringBuilder sBuilder = new StringBuilder();
            for (String txtBean : data) {
                String content = txtBean;
                sBuilder.append(content).append("k#");
            }
            sBuilder.deleteCharAt(sBuilder.lastIndexOf("#"));
            sBuilder.deleteCharAt(sBuilder.lastIndexOf("k"));
            verticalScrollTv.setScrollText(sBuilder.toString().trim(),speed);
        }
        view.setX(location[2]);
        view.setY(location[3]);
        viewGroup.addView(view);
        return view;
    }
}