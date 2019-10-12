package com.ghts.player.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ghts.player.enumType.POS;
import com.ghts.player.manager.ViewManager;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.UserInfoSingleton;

/**
 * Created by lijingjing on 17-8-29.
 * 全屏显示紧急消息
 */
public class ExigentActivity extends BaseActivity {

    public static ExigentActivity instance = null;
    private static final String TAG = "ExigentActivity";
    private ViewGroup viewGroup;
    TextView mExigentInfo_full;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_YES + "");
        POS pos = new POS();
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        pos.setWidth(width);
        pos.setHeight(height);

        WindowManager.LayoutParams layoutParams0 = getWindow().getAttributes();
        layoutParams0.x = 0 - width + pos.getLeft();
        layoutParams0.y = 0 - height + pos.getTop();
        layoutParams0.width = pos.getWidth();
        layoutParams0.height = pos.getHeight();

        viewGroup = new FrameLayout(getApplicationContext()); // 创建控件容器，显示布局
        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight());
        viewGroup.setLayoutParams(layoutParams1); // 设置容器的宽高
        Bundle bundle = this.getIntent().getExtras();
        String exigent = bundle.getString("exigent");
        if (ViewManager.getExigentInfo_full() != null) {
            mExigentInfo_full = ViewManager.getExigentInfo_full().getView(this);
            mExigentInfo_full.setText(exigent);
            viewGroup.addView(mExigentInfo_full);
        }
        setContentView(viewGroup);
    }

  public void receiveMsg(String txt){
      if(mExigentInfo_full != null && !TextUtils.isEmpty(txt)){
          mExigentInfo_full.setText(txt);
      }
  }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }


}

