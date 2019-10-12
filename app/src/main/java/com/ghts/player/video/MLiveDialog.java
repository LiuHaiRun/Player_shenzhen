package com.ghts.player.video;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.ghts.player.enumType.POS;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.UserInfoSingleton;

/**
 * Created by lijingjing on 17-9-21.
 */
public class MLiveDialog extends Activity {
    public static MLiveDialog instance;
    private ViewGroup viewGroup;
    private VideoSurfaceInfo surfaceInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.CENTER);
        UserInfoSingleton.setIslive(ConstantValue.PLAY_LIVE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        surfaceInfo = VideoSurfaceInfo.getInstance();
        POS pos = surfaceInfo.getModule_Pos();
        WindowManager.LayoutParams layoutParams0 = getWindow().getAttributes();
        int screenWidth = Const.screenW;
        int screenHeight = Const.screenH;

        layoutParams0.x = 0 - screenWidth + pos.getLeft();
        layoutParams0.y = 0 - screenHeight + pos.getTop();
        layoutParams0.width = pos.getWidth();
        layoutParams0.height = pos.getHeight();

        instance = this;
        viewGroup = new FrameLayout(getApplicationContext()); // 创建控件容器，显示布局
        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight());
        viewGroup.setLayoutParams(layoutParams1); // 设置容器的宽高
        viewGroup.addView(surfaceInfo.getView(this));
        setContentView(viewGroup);
        LogUtil.e("####启动直播activity","启动");
        surfaceInfo.startPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            LogUtil.e("--surfaceInfo--","--onstop-");
            surfaceInfo.onstop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            LogUtil.e("--surfaceInfo--","--onDestroy-");
            surfaceInfo.onDestroy();
            instance = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
