package com.ghts.player.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ghts.player.application.MyApplication;
import com.ghts.player.manager.AppManager;
import com.ghts.player.parse.ParseXml;
import com.ghts.player.utils.Const;

/**
 * 基类
 */
public class BaseActivity extends Activity {
    public boolean isShow;// 导航栏是否隐藏
    public ParseXml parseXml;
    private boolean allowFullScreen = true; // 是否允许全屏
    private boolean allowDestroy = true;   // 是否允许销毁
    private View view;
    public static Activity activity;
//    MyOrientoinListener myOrientoinListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = false;
        allowFullScreen = true;
        activity = this;

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        hideNavigationBar();
        parseXml = ParseXml.getParseXml(this);
        Const.parseXml = parseXml;
        //加到堆栈的作用:为了方便统一退出所有Activity,目前该app只有两个Activity
        AppManager.getAppManager().addActivity(this);
        MyApplication.getInstance().addActivity(this);

        //设置屏幕大小
        setBig(this);
//        myOrientoinListener = new MyOrientoinListener(this);
//        boolean autoRotateOn = (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
//        //检查系统是否开启自动旋转
//        if (autoRotateOn) {
//            myOrientoinListener.enable();
//        }
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
    protected void onDestroy() {
        super.onDestroy();
        //销毁时取消监听
//        myOrientoinListener.disable();
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    public void setBig(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        Const.screenH = height;
        Const.screenW = width;
        String info = "手机型号: " + Build.MODEL + ",\nSDK版本:"
                + Build.VERSION.SDK + ",\n系统版本:"
                + Build.VERSION.RELEASE + "\n屏幕宽度（像素）: " + width + "\n屏幕高度（像素）: " + height + "\n屏幕密度:  " + density + "\n屏幕密度DPI: " + densityDpi;
        Log.e("system Enviriment", info);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideNavigationBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        Window _window = getWindow();
        WindowManager.LayoutParams params = _window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        _window.setAttributes(params);

    }

    @Override
    /**
     * 监听鼠标的点击，进行导航栏的显示和隐藏。
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShow == false) {
                //由安卓系统接收,升级成4.4后可以参考VLC怎么全屏的
                sendBroadcast(new Intent("com.android.action_DISPLAY_BAR"));
                isShow = true;
            } else {
                sendBroadcast(new Intent("com.android.action.HIDE_BAR"));
                isShow = false;
            }
        }
        return super.onTouchEvent(event);
    }


    public boolean isAllowFullScreen() {
        return allowFullScreen;
    }

    //设置是否可以全屏
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.allowFullScreen = allowFullScreen;
    }

    public void setAllowDestroy(boolean allowDestroy) {
        this.allowDestroy = allowDestroy;
    }

    public void setAllowDestroy(boolean allowDestroy, View view) {
        this.allowDestroy = allowDestroy;
        this.view = view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
            view.onKeyDown(keyCode, event);
            if (!allowDestroy) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

class MyOrientoinListener extends OrientationEventListener {
    private String TAG = "旋转";
    private Context context;

    public MyOrientoinListener(Context context) {
        super(context);
        this.context = context;
    }

    public MyOrientoinListener(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        Log.d(TAG, "orention" + orientation);
        int screenOrientation = context.getResources().getConfiguration().orientation;
        if (((orientation >= 0) && (orientation < 45)) || (orientation > 315)) {//设置竖屏
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && orientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                Log.d(TAG, "设置竖屏");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
             }
        } else if (orientation > 225 && orientation < 315) { //设置横屏
            Log.d(TAG, "设置横屏");
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
             }
        } else if (orientation > 45 && orientation < 135) {// 设置反向横屏
            Log.d(TAG, "反向横屏");
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
             }
        } else if (orientation > 135 && orientation < 225) {
            Log.d(TAG, "反向竖屏");
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
             }
        }
    }
}
}
