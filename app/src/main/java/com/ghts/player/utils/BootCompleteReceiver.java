package com.ghts.player.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ghts.player.activity.PlayerActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by lijingjing on 17-8-4.
 * 开机启动
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    private SharedPreferences sp;
    private Context mContext;
    int count;
    private String filepath = "/sata/"+"boot.txt";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "程序主activity开启");
        mContext = context;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                Thread.sleep(40000); //延迟开启app等待存储设备挂载成功。
                writeReboot();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent2 = new Intent(context, PlayerActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }

    }

    public void writeReboot() {
        try {
            sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
            count = sp.getInt("countInt", 0);
            count++;
            Log.i(TAG, "count = " + count);
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            //下面把数据写入创建的文件，首先新建文件名为参数创建FileWriter对象
            FileWriter resultFile = new FileWriter(file, true);
            //把该对象包装进PrinterWriter对象
            PrintWriter myNewFile = new PrintWriter(resultFile);
            //再通过PrinterWriter对象的println()方法把字符串数据写入新建文件
            boolean isExit = UserInfoSingleton.getBoolean("isExit", false);
            resultFile.close();   //关闭文件写入流

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("countInt", count);
            editor.commit();
//            Toast.makeText(mContext, "第" + count + "次开机", Toast.LENGTH_SHORT).show();
            UserInfoSingleton.putBooleanAndCommit("isExit", false);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}