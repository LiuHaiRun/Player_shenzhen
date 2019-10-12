package com.ghts.player.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.ghts.player.R;
import com.ghts.player.application.CrashApplication;
import com.ghts.player.bean.GlobalBean;
import com.ghts.player.bean.UpgradeBean;
import com.ghts.player.manager.ViewManager;
import com.ghts.player.parse.XmlUpdate;
import com.ghts.player.service.LogObserverService;
import com.ghts.player.service.LogService;
import com.ghts.player.service.LongRunningService;
import com.ghts.player.service.PlayerService;
import com.ghts.player.service.RecvService;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.ShellUtils;
import com.ghts.player.utils.VeDate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android_serialport_api.SerialPortUtil;

/**
 * Created by lijingjing on 17-5-3.
 */
public class PlayerActivity extends BaseActivity {

    public Intent playServiceIntent, recvIntent, longRunService, logService,logObserverIntent;
    public static boolean isExit = false;
    public static Context context;
    public static PlayerActivity activity;
    private static final String TAG = "PlayerActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) {
            context = PlayerActivity.this;
            activity = PlayerActivity.this;
        }
        try {
            Const.globalBean = GlobalBean.getInstance();
            ViewManager.getInstance().init(this);
            Const.parseXml.getWeather();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //upgrade();
    }

    protected void onResume() {
        String classname = LogService.class.getName();
        if (!PubUtil.isServiceRunning(this, classname)) {
            logService = new Intent("com.ghyf.service.LOG");
            startService(logService);
        }
        classname = PlayerService.class.getName();
        LogUtil.e("player启动", PubUtil.isServiceRunning(this, classname) + "");
        if (!PubUtil.isServiceRunning(this, classname)) {
            playServiceIntent = new Intent(this, PlayerService.class);
            startService(playServiceIntent);
        }
        classname = RecvService.class.getName();
        if (!PubUtil.isServiceRunning(this, classname)) {
            recvIntent = new Intent(this, RecvService.class);
            startService(recvIntent);
        }

//        classname = LogObserverService.class.getName();
//        if (!PubUtil.isServiceRunning(this, classname)) {
//            logObserverIntent = new Intent(this, LogObserverService.class);
//            startService(logObserverIntent);
//        }
        //
        if (Const.globalBean.getIsTrain().equals("0")) {
            classname = LongRunningService.class.getName();
            if (!PubUtil.isServiceRunning(this, classname)) {
                longRunService = new Intent(this, LongRunningService.class);
                startService(longRunService);
            }

//            if (Const.globalBean.getPctrlType() == 1) {
//                if (!SerialPortUtil.flag) {
//                    SerialPortUtil.openSrialPort();
//                }
//                Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
//                myIntent.putExtra("cmd", ConstantValue.CMD_MT_SCREEN_ON);
//                context.sendBroadcast(myIntent);
//            }
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("--onStop--", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("--onRestart--", "onRestart");
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            LogUtil.i("--main--onDestroy", "onDestroy");
            isExit = true;
            sendBroadcast(new Intent("com.android.action_DISPLAY_BAR"));
            SerialPortUtil.closeSerialPort();
            ((CrashApplication) context.getApplicationContext()).removeAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("--main--", "onDestroy", e);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isShow == false) {
                sendBroadcast(new Intent("com.android.action_DISPLAY_BAR"));
                isShow = true;
            } else {
                sendBroadcast(new Intent("com.android.action.HIDE_BAR"));
                isShow = false;
            }
        }
        return super.onTouchEvent(event);
    }

    private void upgrade() {
        try {
            UpgradeBean upgradeBean = Const.parseXml.getUpgrade();
            if (upgradeBean != null) {
                if (upgradeBean.getInstall().equals("0")) {
                    //文件现在成功时
                    Date date = upgradeBean.getTime();
                    File file = upgradeBean.getFile();
                    //检测路径是否存在
                    if (!file.exists()) {
                        return;
                    } else {
                        install(file);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 静默安装
     *
     * @param ApkFile
     */
    private void install(File ApkFile) {
        if (ApkFile != null) {
            int sucess = ShellUtils.installBySlient(this, ApkFile.getPath());
            //            "/sata/upgrade/MPlay.apk"
            if (sucess == 1) {
                XmlUpdate xmlUpdate = new XmlUpdate();
                xmlUpdate.alertXml(Const.UPGRADE);
            }
        }
    }


    public void DesService() {
        if (playServiceIntent != null) {
            stopService(playServiceIntent);
        }
        if (recvIntent != null) {
            stopService(recvIntent);
        }
        if (logService != null) {
            stopService(logService);
        }
        if (Const.globalBean.getIsTrain().equals("0")) {
            if (longRunService != null) {
                stopService(longRunService);
            }
        }
    }

}
