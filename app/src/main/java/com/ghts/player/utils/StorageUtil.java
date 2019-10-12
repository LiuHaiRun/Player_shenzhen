package com.ghts.player.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by lijingjing on 17-8-9.
 * 获取手机内存，CPU，硬盘空间
 */
public class StorageUtil {

    private static final String TAG = "StorageUtil";
    private static final int ERROR = -1;

    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }




    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }


    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize / 1024 / 1024;
        } else {
            return ERROR;
        }
    }

    /**
     * 数据硬盘总空间<br>
     *
     * @return
     */
    public static long getDataTotalSize() {
        StatFs stat = new StatFs("/sata/");
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount() * blockSize / 1024 / 1024;
        //		     long availableBlocks =stat.getFreeBlocks()*blockSize/1024/1024;

        //        LogUtil.e("--数据硬盘总空间--",totalBlocks+"--");
        return totalBlocks;
    }

    /**
     * 数据硬盘剩余空间<br>
     *
     * @return
     */
    public static long getDataFreeSize() {
        StatFs stat = new StatFs("/sata");
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getFreeBlocks() * blockSize / 1024 / 1024;
        //        LogUtil.e("--数据硬盘剩余空间--",availableBlocks+"--");
        return availableBlocks;
    }

    /**
     * 数据硬盘<br>
     *
     * @return
     */
    public static int getDataUse() {
        int number = 0;
        StatFs stat = new StatFs("/sata");
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount() * blockSize / 1024 / 1024;
        long availableBlocks = stat.getFreeBlocks() * blockSize / 1024 / 1024;
        //		     LogUtil.i(TAG, "installApp  totalBlocks "+totalBlocks+",blockSize "+blockSize+" availableBlocks "+availableBlocks);
        number = (int) (100 * (totalBlocks - availableBlocks) / totalBlocks);

        return number;
    }

    /**
     * 系统硬盘<br>
     *
     * @return
     */
    public static int getHardUse() {
        int number = 0;
        String path = Environment.getExternalStorageDirectory().getPath();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount() * blockSize / 1024 / 1024;

        long availableBlocks = stat.getFreeBlocks() * blockSize / 1024 / 1024;
        number = (int) (100 * (totalBlocks - availableBlocks) / totalBlocks);

        return number;
    }

    /**
     * @param context
     * @return
     */
    public static long getRamUse1(Context context) {
        long userMem = 0;
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获得MemoryInfo对象
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            //获得系统可用内存，保存在MemoryInfo对象上
            mActivityManager.getMemoryInfo(memoryInfo);
            long totalMen = memoryInfo.totalMem;
            //可用内存
            long memSize = memoryInfo.availMem >> 10;
            boolean lowMemory = memoryInfo.lowMemory;
            //
            long threshold = memoryInfo.threshold;
            userMem = threshold / 1024 / 1024;
        } catch (Exception e) {
            LogUtil.e(TAG, "getCpuUse error", e);
        }
        return userMem;
    }

    public static long getTotalMem(Context context) {
        long totalMem = 0;
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            //获得MemoryInfo对象
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            //获得系统可用内存，保存在MemoryInfo对象上
            mActivityManager.getMemoryInfo(memoryInfo);
            totalMem = memoryInfo.totalMem / 1024 / 1024;
        } catch (Exception e) {
            LogUtil.e(TAG, "getCpuUse error", e);
        }
        return totalMem;
    }

    public static int readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            //	    		LogUtil.i(TAG, "installApp  ram"+load);
            long idle1 = Long.parseLong(toks[5]);
            //	    		 LogUtil.i(TAG, "installApp  idle1"+idle1);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
                    + Long.parseLong(toks[4]) + Long.parseLong(toks[6])
                    + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(1000);

            } catch (Exception e) {
            }
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
                    + Long.parseLong(toks[4]) + Long.parseLong(toks[6])
                    + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取CPU温度
     */
    public static String getCpuTemperature() {
        String s = "";
        String cmd = "";
        cmd = "/system/bin/cat " + "/sys/devices/virtual/thermal/thermal_zone0/temp";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader ISR = new InputStreamReader(p.getInputStream());
            BufferedReader BFR = new BufferedReader(ISR);
            String line = null;
            while ((line = BFR.readLine()) != null) {
                s += line;
            }
            if (ISR != null) {
                ISR.close();
            }
            if (BFR != null) {
                BFR.close();
            }
            try {
                if (p != null) {
                    p.exitValue();
                }
            } catch (IllegalThreadStateException e) {
                p.destroy();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    //当前系统的可用内存
    public static String getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        long mem = mi.availMem/1024/1024;
        return mem+"";// 将获取的内存大小规格化
    }

    public static String getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";
        // 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            // 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();
            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        long mem = initial_memory/1024;
        return mem+"";
    }
}

