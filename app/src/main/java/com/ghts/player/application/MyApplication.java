package com.ghts.player.application;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.ghts.player.BuildConfig;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.UserInfoSingleton;

import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by lijingjing on 17-6-13.
 */
public class MyApplication extends CrashApplication {

    private static MyApplication instance;
    private static final String TAG = "MyApplication";
    public static SharedPreferences.Editor sysEdit;
    public static SharedPreferences sysShare;
    private static final String SYSSHARED = "sysShared";

    public void onCreate() {
        super.onCreate();
        try {
            pManager = getPackageManager();
            manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            instance = this;
            sysShare = getSharedPreferences(SYSSHARED, 0);
            sysEdit = sysShare.edit();
            // 设置崩溃后自动重启 APP
            UncaughtExceptionHandlerImpl.getInstance().init(this, BuildConfig.DEBUG, true, 0, com.ghts.player.activity.PlayerActivity.class);
             //设置时区
            AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setTimeZone("Asia/Shanghai");
            UserInfoSingleton.setIslive(ConstantValue.PLAY_LOCAL);
            UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_NO+"");
            UserInfoSingleton.setIsScreenOff(ConstantValue.IsScreenOff_NO+"");
            UserInfoSingleton.setIsPowerOff(ConstantValue.IsPowerOff_NO+"");
         } catch (Exception e) {
            LogUtil.e(TAG, "出现异常", e);
        }
    }

    public static Context getAppContext() {
        return instance;
    }

    public static Resources getAppResources() {
        if (instance == null) return null;
        return instance.getResources();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
//        AppManager.getAppManager().AppExit(MyApplication.getAppContext());
        //杀死该应用进程
        LogUtil.e("---killProcess--", "killProcess");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private ActivityManager manager;
    private List<ActivityManager.RunningAppProcessInfo> runningProcesses;
    private String packName;
    private PackageManager pManager;

    private void killOthers(Context context) {
        runningProcesses = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningProcess : runningProcesses) {
            try {
                packName = runningProcess.processName;
                ApplicationInfo applicationInfo = pManager.getPackageInfo(packName, 0).applicationInfo;
                LogUtil.e("---packName-----", packName);
//                if (packName.equals("com.ghts.player")) {
                forceStopPackage("com.ghts.player", context);
//                    System.out.println(packName + "JJJJJJ");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 强制停止应用程序
     *
     * @param pkgName
     */
    private void forceStopPackage(String pkgName, Context context) throws Exception {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method forceStopPackage = am.getClass().getDeclaredMethod("com.ghts.player", String.class);
        forceStopPackage.setAccessible(true);
        forceStopPackage.invoke(am, pkgName);
    }

    /**
     * 判断某个应用程序是 不是三方的应用程序
     * @param info
     * @return
     */
    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

}
