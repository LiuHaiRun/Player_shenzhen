package com.ghts.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.ghts.player.activity.PlayerActivity;
import com.ghts.player.application.CrashApplication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogObserverService extends Service implements Runnable {
    private String TAG = "LogObserverService";
    private boolean isObserverLog = false;
    private StringBuffer logContent = null;
    private Bundle mBundle = null;
    private Intent mIntent = null;
    public static String LOG_ACTION = "com.example.admin.logobserver.LOG_ACTION";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.v("检测日志","startCommand");
        //START_STICKY是service被kill掉后自动重写创建
//        flags =  START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
        mIntent = new Intent();
        mBundle = new Bundle();
        logContent = new StringBuffer();
        startLogObserver();
    }

    /**
     * 开启检测日志
     */
    public void startLogObserver() {
        Log.i(TAG,"startObserverLog");
        isObserverLog = true;
        Thread mTherad = new Thread(this);
        mTherad.start();
    }

    /**
     * 关闭检测日志
     */
    public void stopLogObserver() {
        isObserverLog = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLogObserver();
    }

    /**
     * 发送log内容
     * @param logContent
     */
    private void sendLogContent(String logContent){
        mBundle.putString("log",logContent);
        mIntent.putExtras(mBundle);
        mIntent.setAction(LOG_ACTION);
        sendBroadcast(mIntent);
    }


    @Override
    public void run() {
        Process pro = null;
        BufferedReader bufferedReader = null;
        try {
            String[] running=new String[]{ "logcat","|find","com.ghts.player" };
            //          pro = Runtime.getRuntime().exec("logcat");
            pro = Runtime.getRuntime().exec(running);
            //          Runtime.getRuntime().exec("logcat -c").waitFor();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    pro.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //筛选需要的字串
        String strFilter="feeding synchro with a new reference point trying to recover from clock gap";
       String error = "Fatal signal 11 (SIGSEGV) at";
        String line = null;
        try {
//            Log.e("走到这里没","走到这里没");
//            System.out.println(bufferedReader.readLine());
            while ((line =bufferedReader.readLine()) != null) {
//                Log.e("监测日志",line.toString());
                if (line.contains(strFilter)|| line.contains(error)) {
                    //读出每行log信息
                    Log.e("监测日志","直播异常");
                    System.out.println(line);
                    logContent.delete(0,logContent.length());
                    logContent.append(line);
                    ((CrashApplication) PlayerActivity.context.getApplicationContext()).removeAllActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}