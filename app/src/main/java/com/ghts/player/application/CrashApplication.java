package com.ghts.player.application;

import android.app.Activity;
import android.app.Application;

import com.ghts.player.activity.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * CrashApplication
 * 在使用者中，需要继承自这个 Application
 *
 * */

public class CrashApplication extends Application {
    private List<Activity> mActivityList;


    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<Activity>();
    }

    /**
     * 添加单个Activity
     */
    public void addActivity(Activity activity) {
        // 为了避免重复添加，需要判断当前集合是否满足不存在该Activity
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity); // 把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity(Activity activity) {
        // 判断当前集合是否存在该Activity
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity); // 从集合中移除
            if (activity != null){
                activity.finish(); // 销毁当前Activity
            }
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeAllActivity() {
        // 通过循环，把集合中的所有Activity销毁
        for (Activity activity : mActivityList) {
            if (activity != null){
                activity.finish();
            }
        }
        PlayerActivity.activity.DesService();
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
