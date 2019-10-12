package com.ghts.player.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by lijingjing on 17-8-3.
 */
public class CommonUtil {

   private final static  String TAG = "CommonUtil";

    public static boolean isProcessRun(String whichProcess)
    {
        Process process = null;
        String str="";
        try {
            process = Runtime.getRuntime().exec("busybox ps -o comm");
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            LogUtil.e(TAG, e);
        }
        InputStream localInputStream = process.getInputStream();
        DataInputStream localDataInputStream = new DataInputStream(localInputStream);
        try {
            while((str = localDataInputStream.readLine())!=null){
                if (str.equals(whichProcess)) {
                    return true;
                }
            }
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        }
        process.exitValue();
        return false;
    }

    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceinfos = am.getRunningServices(120);
        for (ActivityManager.RunningServiceInfo info : serviceinfos) {
//            System.out.println(info.service.getClassName() + "--->" + className);
            if (className.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    /** *//**
     * 把字节数组转换成16进制字符串
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray,int length) {
        StringBuffer sb = new StringBuffer(length);
        String sTemp;
        for (int i = 0; i < length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase(Locale.US));
        }
        return sb.toString();
    }

    /**
     * 重启软件
     * @param context
     */
    public static void reSoftWare(Context context){
        try{
            Intent myIntent = new Intent(ConstantValue.RESOFTWARE_RECEIVER);
            myIntent.putExtra("path", ConstantValue.COMMAND_INSTALL_APP);
            myIntent.setAction(ConstantValue.RESOFTWARE_RECEIVER);
            context.sendBroadcast(myIntent);
        }catch(Exception e){
            LogUtil.e(TAG, "reSoftWare error",e);
        }

    }
}
