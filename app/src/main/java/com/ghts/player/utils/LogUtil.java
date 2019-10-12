package com.ghts.player.utils;

import android.util.Log;

/**
 * Created by lijingjing on 17-6-12.
 */
public class LogUtil {

    private static final int BETA = 2;
    /**
     * 当前阶段标示
     */
    private static int currentStage = BETA;


    public static void i(String tag,String msg){
        if(currentStage==BETA){
            Log.e(tag, msg);
        }

    }

    public static void i(String tag,String msg,Throwable tr){
        if(currentStage==BETA){
            Log.i(tag, msg,tr);
        }

    }

    public static void e(String tag,String msg){
        if(currentStage==BETA){
            Log.e(tag, msg);
        }

    }
    public static void e(String tag,Exception e){
        if(currentStage==BETA){
            if(null!=e){
                Log.e(tag, e.getMessage());
            }

        }

    }
    public static void e(String tag,String msg,Throwable tr){
        if(currentStage==BETA){
            Log.e(tag, msg,tr);
        }

    }



}
