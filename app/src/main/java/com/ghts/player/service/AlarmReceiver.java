package com.ghts.player.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.ShellUtils;

import static android.content.Intent.ACTION_TIME_TICK;

/**
 * Created by lijingjing on 17-9-26.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        if (ACTION_TIME_TICK.equals(action)) {
            LogUtil.e("#####日期发生变化#####", "AlarmReceiver");
            Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
            mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_WEEK);
            context.sendBroadcast(mIntent);
        }else if("PLAYER_REBOOT".equals(action)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.e("#####执行重启#####", "AlarmReceiver");
                   ShellUtils.reboot();
                }
            }).start();
        }
    }

}
